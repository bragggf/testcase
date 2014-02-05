package login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CPassWd
{
   final static private String ExpLower = "[a-z]";
   final static private String ExpUpper = "[A-Z]";
   final static private String ExpDigit = "[0-9]";
   final static private String ExpSpecial = "[[\\x21-\\x2f\\x3a-\\x40\\x5b-\\x60\\x7b-\\x7e]]";
   
   public static String savePassWord(Connection aconn, String auser, String aoldpw, String anewpw, String aconfpw)
   {
      CLoginProps lgprops = new CLoginProps();

      String oldpass = aoldpw.substring(0, Math.min(aoldpw.length(), lgprops.MaxPassLeng));
      String newpass = anewpw.substring(0, Math.min(anewpw.length(), lgprops.MaxPassLeng));
      String confpass = aconfpw.substring(0, Math.min(aconfpw.length(), lgprops.MaxPassLeng));
      
      // was old password correct
      String curhash = dbGetPwHash(aconn, auser);
      String oldhash = "";
      if (CLoginProps.PassHashSHA1.equals(lgprops.HashMethod))
         oldhash = crypto.CSha1Hash.toHash(oldpass);
      else if (CLoginProps.PassHashSHA256.equals(lgprops.HashMethod))
         oldhash = crypto.CSha256Hash.toHash(oldpass);
      else
         oldhash = crypto.CMd5Hash.toHash(oldpass);
   
      if (!curhash.equals(oldhash))
         return("Your current password is incorrect.");
   
      // was new password confirmed
      if (!newpass.equals(confpass))
         return("You have not confirmed your new password.");
   
      // was the password changed
      if (newpass.equals(oldpass))
         return("You cannot reuse your current password.");
   
      if (newpass.length() < lgprops.MinPassLeng)
         return("Your new password must be at least " + Integer.toString(lgprops.MinPassLeng) + " characters.");
   
      int nummat = countMatches(ExpLower, newpass);
      int numchars = nummat;
      if (nummat < lgprops.MinPassLower)
         return("Your new password must use at least " + Integer.toString(lgprops.MinPassLower) + " lowercase characters.");

      nummat = countMatches(ExpUpper, newpass);
      numchars = numchars + nummat;
      if (nummat < lgprops.MinPassUpper)
         return("Your new password must use at least " + Integer.toString(lgprops.MinPassUpper) + " uppercase characters.");

      nummat = countMatches(ExpDigit, newpass);
      numchars = numchars + nummat;
      if (nummat < lgprops.MinPassDigit)
         return("Your new password must use at least " + Integer.toString(lgprops.MinPassDigit) + " numeric characters.");

      nummat = countMatches(ExpSpecial, newpass);
      numchars = numchars + nummat;
      if (nummat < lgprops.MinPassSpecial)
         return("Your new password must use at least " + Integer.toString(lgprops.MinPassSpecial) + " special characters.");
   
      if (numchars != newpass.length())
         return("Your new password contains disallowed characters.");
   
      if (countDiff(newpass, oldpass) < lgprops.MinPassDiff)
         return("Your new password must contain at least " + Integer.toString(lgprops.MinPassDiff) + " characters that were not used in you old password.");
   
      // new password is acceptable so update it        
      String passhash = "";
      if (CLoginProps.PassHashSHA1.equals(lgprops.HashMethod))
         passhash = crypto.CSha1Hash.toHash(newpass);
      else if (CLoginProps.PassHashSHA256.equals(lgprops.HashMethod))
         passhash = crypto.CSha256Hash.toHash(newpass);
      else
         passhash = crypto.CMd5Hash.toHash(newpass);
   
      String retstr = dbUpdateItem(aconn, auser, passhash);
      return(retstr);
   }
   
   protected static String dbGetPwHash(Connection aconn, String auser)
   {
      String curhash = "ERROR";
      try
      {
         String qstr = "Select PassHash From UserTbl Where UserId=?";
         PreparedStatement pstmt = aconn.prepareStatement(qstr);
         pstmt.setString(1, auser);
         ResultSet rset = pstmt.executeQuery();
         if (rset.next())
         {
            curhash = rset.getString(1);
         }
         rset.close();
         pstmt.close();
      }
      catch (Exception ex)
      {
         dbconn.CDbError.logError(manapp.CAppConsts.ErrorFile, false, "CPassWd.dbGetPwHash error: ", ex);
      }
      return(curhash);
   }

   protected static String dbUpdateItem(Connection aconn, String auserid, String apasshash)
   {
      try
      {
         java.util.Date pwdate = new java.util.Date();
         String qstr = "Update UserTbl set PassHash=?,PwChangeTm=? where UserId=?";
         PreparedStatement stmt = aconn.prepareStatement(qstr);
         stmt.setString(1, apasshash);
         java.sql.Timestamp tstmp = new java.sql.Timestamp(pwdate.getTime());
         stmt.setTimestamp(2, tstmp);
         stmt.setString(3, auserid);
         stmt.executeUpdate();
         stmt.close();
         return("");
      }
      catch (Exception ex)
      {
         dbconn.CDbError.logError(manapp.CAppConsts.ErrorFile, false, "CPassWd.dbUpdateItem error: ", ex);
         return("Failure saving password");
      }
   }

   protected static int countMatches(String aexp, String aseq)
   {
      Pattern p = Pattern.compile(aexp);
      Matcher m = p.matcher(aseq);
      int nmat = 0;
      while (m.find()) 
         nmat++;
      return(nmat);
   }

   protected static int countDiff(String anew, String aold)
   {
      int mat = 0;
      for (int idx = 0; idx < anew.length(); idx++)
      {
         char achr = anew.charAt(idx);
         for (int jdx = 0; jdx < aold.length(); jdx++)
         {
            char bchr = aold.charAt(jdx);
            if (achr == bchr) 
            {
               mat++;
               break;
            }
         }
      }
      return(anew.length() - mat);
   }
}
