/*
 * CValidUser.java
 *
 * Created on Jan 22, 2009, 5:08:21 PM
 *
 * By lwaisanen
 */

package login;

import java.sql.*;
import java.util.Date;
import manapp.CAppConsts;

public class CValidUser
{
   final static public long MilsecDay =  24 * 60 * 60 * 1000;
   final static public int FailLockOpen = 0;
   final static public int FailLockTemp = 1;
   final static public int FailLockPerm = 2;
   final static public String PwChangeRequire = "Required";
   final static public String PwChangeOptional = "Optional";

   protected dbconn.CDbProps dbprops;
   protected CLoginProps loginprops;

   public String userid;
   public String role;
   private String passhash;
   private Date pwchangedt;
   private Date lastfailure;
   private Date lastsuccess;
   private int numfailures;
   private int numsuccess;

   public String failreason;
   public String nameprefix;
   public String firstname;
   public String lastname;

   public CValidUser()
   {
      dbprops = new dbconn.CDbProps();
      loginprops = new CLoginProps();
      userid = "";
      role = CLoginProps.RoleNone;
      passhash = "";
      pwchangedt = new Date();
      pwchangedt.setTime(pwchangedt.getTime() - (loginprops.PwLifeDays + 1) * CValidUser.MilsecDay);
      lastfailure = new Date(0L);
      lastsuccess = new Date(0L);
      numfailures = 0;
      numsuccess = 0;
      failreason = "";
      nameprefix = "";
      firstname = "";
      lastname = "";
   }

   public boolean isValidUser(Connection aconn, String auser, String apasswd)
   {
      try
      {
         if (aconn == null)
         {
            failreason = "Database is not available.";
            return(false);
         }

         userid = auser;
         String mypasshash = "";
         if (CLoginProps.PassHashSHA1.equals(loginprops.HashMethod))
            mypasshash = crypto.CSha1Hash.toHash(apasswd);
         else if (CLoginProps.PassHashSHA256.equals(loginprops.HashMethod))
            mypasshash = crypto.CSha256Hash.toHash(apasswd);
         else
            mypasshash = crypto.CMd5Hash.toHash(apasswd);

         String qstr = "Select PassHash,AppRole,PwChangeTm,LastFailure,LastSuccess,NumFailures,NumSuccess" +
                       " From UserTbl Where UserId=?";
         PreparedStatement pstmt = aconn.prepareStatement(qstr);
         pstmt.setString(1, auser);
         ResultSet rset = pstmt.executeQuery();

         if (rset.next())
         {
            passhash = rset.getString(1);
            role = rset.getString(2);
            java.sql.Timestamp tstamp = rset.getTimestamp(3);
            if (rset.wasNull())
            {
               Date exptm = new Date();
               exptm.setTime(exptm.getTime() - (loginprops.PwLifeDays + 1) * CValidUser.MilsecDay);
               tstamp = new java.sql.Timestamp(exptm.getTime());
            }
            pwchangedt = new java.util.Date(tstamp.getTime());

            tstamp = rset.getTimestamp(4);
            if (rset.wasNull()) tstamp = new java.sql.Timestamp(0L);
            lastfailure = new java.util.Date(tstamp.getTime());

            tstamp = rset.getTimestamp(5);
            if (rset.wasNull()) tstamp = new java.sql.Timestamp(0L);
            lastsuccess = new java.util.Date(tstamp.getTime());

            numfailures = rset.getInt(6);
            if (rset.wasNull()) numfailures = 0;

            numsuccess = rset.getInt(7);
            if (rset.wasNull()) numsuccess = 0;

            rset.close();
            pstmt.close();
         }
         else
         {
            rset.close();
            pstmt.close();
            failreason = "Invalid userid/password combination.";
            return(false);
         }

         int faillock = getFailLocked();
         if (faillock == CLoginProps.FailLockPerm)
         {
            failreason = "Account is locked.";
            return(false);
         }
         else if (faillock == CLoginProps.FailLockTemp)
         {
            failreason = "Account is temporarily locked.";
            return(false);
         }

         if (!passhash.equals(mypasshash))
         {
            dbFailure(aconn);
            failreason = "Invalid userid/password combination.";
            return(false);
         }
         if (!isUserRole(role))
         {
            failreason = "User not authorized.";
            return(false);
         }

         // at this point, we have a valid user.
         dbSuccess(aconn);

         // check if password has expired
         Date today = new Date();
         Date pwdate = new Date(pwchangedt.getTime() + CValidUser.MilsecDay * loginprops.PwLifeDays);
         if (today.getTime() > pwdate.getTime())
         {
            failreason = "Password has expired.";
         }
         return(true);
      }
      catch (Exception ex)
      {
         failreason = "Exception " + ex.getMessage();
         return(false);
      }
   }

   public boolean isUserRole(String arole)
   {
      return(arole.equals(CLoginProps.RoleAdmin) ||
             arole.equals(CLoginProps.RoleUser));
   }

   public void dbUpdateItem(Connection aconn)
   {
      try
      {
         String qstr = "Update UserTbl set PassHash=?,PwChangeTm=? where UserId=?";
         PreparedStatement stmt = aconn.prepareStatement(qstr);
         stmt.setString(1, passhash);
         java.sql.Timestamp tstmp = new java.sql.Timestamp(pwchangedt.getTime());
         stmt.setTimestamp(2, tstmp);
         stmt.setString(3, userid);
         stmt.executeUpdate();
         stmt.close();
      }
      catch (Exception ex)
      {
         dbconn.CDbError.logError(dbprops.ErrorLogFile, false, "dbUpdateItem error: ", ex);
      }
   }

   public void dbSuccess(Connection aconn)
   {
      try
      {
         Date lastsucc = new Date();
         numfailures = 0;
         numsuccess++;
         PreparedStatement stmt = aconn.prepareStatement(
                "Update UserTbl set LastSuccess=?,NumFailures=?,NumSuccess=? Where UserId=?");
         java.sql.Timestamp tstmp = new java.sql.Timestamp(lastsucc.getTime());
         stmt.setTimestamp(1, tstmp);
         stmt.setInt(2, numfailures);
         stmt.setInt(3, numsuccess);
         stmt.setString(4, userid);
         stmt.executeUpdate();
         stmt.close();
      }
      catch (Exception ex)
      {
         dbconn.CDbError.logError(dbprops.ErrorLogFile, false, "dbSuccess error: ", ex);
      }
   }

   public void dbFailure(Connection aconn)
   {
      try
      {
         numfailures++;
         lastfailure = new Date();

         PreparedStatement stmt = aconn.prepareStatement(
                "Update UserTbl set LastFailure=?,NumFailures=? Where UserId=?");
         java.sql.Timestamp tstmp = new java.sql.Timestamp(lastfailure.getTime());
         stmt.setTimestamp(1, tstmp);
         stmt.setInt(2, numfailures);
         stmt.setString(3, userid);
         stmt.executeUpdate();
         stmt.close();
      }
      catch (Exception ex)
      {
         dbconn.CDbError.logError(dbprops.ErrorLogFile, false, "dbFailure error: ", ex);
      }
   }

   public int getFailLocked()
   {
      if (numfailures < loginprops.MaxLoginTries) return(CLoginProps.FailLockOpen);
      if (numfailures >= loginprops.MaxLoginFails) return(CLoginProps.FailLockPerm);
      Date nowdt = new Date();
      if ((nowdt.getTime() - lastfailure.getTime()) < loginprops.FailLockPeriod) return(CLoginProps.FailLockTemp);
      return(CLoginProps.FailLockOpen);
   }
}
