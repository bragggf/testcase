/*
 * CUserItem.java
 *
 * Created on June 2, 2005, 12:25 PM
 */

package login;

import java.sql.*;
import java.util.Date;

/** User information.
 */
public class CUserItem
{
   private String userid;
   private String role;
   private String passhash;
   private Date pwchangedt;
   private Date lastfailure;
   private Date lastsuccess;
   private int numfailures;
   private int numsuccess;
   
   
   /** Creates a new instance of CUserItem */
   public CUserItem()
   {
      userid = "";
      role = manapp.CConsts.RoleNone;
      passhash = "";
      pwchangedt = new Date(0L);
      lastfailure = new Date(0L);
      lastsuccess = new Date(0L);
      numfailures = 0;
      numsuccess = 0;
   }
   
   /** Get user id. 
       @return user id */
   public String getUserId()
   {
      return(userid);
   }
   /** Set user id. 
       @param astr user id */
   public void setUserId(String astr)
   {
      userid = astr;
   }
   
   public String getRole()
   {
      return(role);
   }
   public void setRole(String astr)
   {
      role = astr;
   }
   
   public String getPassHash()
   {
      return(passhash);
   }
   public void setPassHash(String astr)
   {
      passhash = astr;
   }
   
   public Date getPwDate()
   {
      return(pwchangedt);
   }
   
   public void setPwDate(Date adate)
   {
      pwchangedt = adate;
   }

    /** gets last login failure */
   public Date getLastFailure() 
   {
      return lastfailure;
   }
    /** sets last login failure */
   public void setLastFailure(Date value) 
   {
      lastfailure = value;
   }

    /** gets last login success */
   public Date getLastSuccess() 
   {
      return lastsuccess;
   }
    /** sets last login success */
   public void setLastSuccess(Date value) 
   {
      lastsuccess = value;
   }

    /** gets number of login failures since the last login success */
   public int getNumFailures() 
   {
      return numfailures;
   }
    /** sets last login success */
   public void setNumFailures(int value) 
   {
      numfailures = value;
   }

    /** gets number of successful logins */
   public int getNumSuccess() 
   {
      return numsuccess;
   }
    /** sets last login success */
   public void setNumSuccess(int value) 
   {
      numsuccess = value;
   }
   
   public boolean isRoleUser()
   {
      return(role.equals(manapp.CConsts.RoleAdmin) || role.equals(manapp.CConsts.RoleUser));
   }
   
   public boolean isRoleAdmin()
   {
      return(role.equals(manapp.CConsts.RoleAdmin));
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
         manapp.CLogError.logError(manapp.CConsts.ErrMsgFile, false, "dbUpdateItem error: ", ex);
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
         manapp.CLogError.logError(manapp.CConsts.ErrMsgFile, false, "dbSuccess error: ", ex);
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
         manapp.CLogError.logError(manapp.CConsts.ErrMsgFile, false, "dbFailure error: ", ex);
      }
   }
   
   public int getFailLocked(Connection aconn)
   {
      if (numfailures < manapp.CConsts.MaxLoginTries) return(manapp.CConsts.FailLockOpen);
      if (numfailures >= manapp.CConsts.MaxLoginFails) return(manapp.CConsts.FailLockPerm);
      Date nowdt = new Date();
      if ((nowdt.getTime() - lastfailure.getTime()) < manapp.CConsts.FailLockPeriod) return(manapp.CConsts.FailLockTemp);
      return(manapp.CConsts.FailLockOpen);
   }
}
