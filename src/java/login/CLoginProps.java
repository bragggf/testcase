/*
 * CLoginProps.java
 * 
 * Created on Jan 22, 2009, 5:14:28 PM
 * 
 * By lwaisanen
 */

package login;

import java.util.Properties;
import java.io.*;

public class CLoginProps 
{
   final static public String PropFile = "login.properties";

   final static public String RoleNone = "none";
   final static public String RoleUser = "user";
   final static public String RoleAdmin = "admin";
   
   final static public int FailLockOpen = 0;
   final static public int FailLockTemp = 1;
   final static public int FailLockPerm = 2;
   final static public String PassHashMd5 = "MD5";
   final static public String PassHashSHA1 = "SHA-1";
   final static public String PassHashSHA256 = "SHA-256";

   public String LoginPageAboveFile = "LoginPageAbove.txt";
   public String LoginPageBelowFile = "LoginPageBelow.txt";
   public String ErrorLogFile = "LoginErrors.txt";
   public String UsageLogFile = "applog.log";
   public int PwLifeDays = 90;
   public int MinPassDiff = 2;
   public int MinPassLower = 2;
   public int MinPassUpper = 2;
   public int MinPassDigit = 2;
   public int MinPassSpecial = 2;
   public int MinPassLeng = MinPassLower + MinPassUpper + MinPassDigit + MinPassSpecial;
   public int MaxPassLeng = 32;
   public int MaxUserLeng = 32;
   public int MaxLoginTries = 3;
   public int MaxLoginFails = 6;
   public long FailLockPeriod =  60 * 60 * 1000;
   public String HashMethod = CLoginProps.PassHashMd5;
   
   public CLoginProps()
   {
      try
      {
         InputStream finp = this.getClass().getResourceAsStream(CLoginProps.PropFile);
         Properties props = new Properties(); 
         props.load(finp);
         LoginPageAboveFile = props.getProperty("LoginPageAboveFile");
         LoginPageBelowFile = props.getProperty("LoginPageBelowFile");
         ErrorLogFile = props.getProperty("ErrorLogFile");
         UsageLogFile = props.getProperty("UsageLogFile");
         PwLifeDays = Integer.parseInt(props.getProperty("PwLifeDays"));
         MinPassDiff = Integer.parseInt(props.getProperty("MinPassDiff"));
         MinPassLower = Integer.parseInt(props.getProperty("MinPassLower"));
         MinPassUpper = Integer.parseInt(props.getProperty("MinPassUpper"));
         MinPassDigit = Integer.parseInt(props.getProperty("MinPassDigit"));
         MinPassSpecial = Integer.parseInt(props.getProperty("MinPassSpecial"));
         MinPassLeng = java.lang.Math.max((MinPassLower + MinPassUpper + MinPassDigit + MinPassSpecial), 
                                           Integer.parseInt(props.getProperty("MinPassLength")));
         MaxPassLeng = Integer.parseInt(props.getProperty("MaxPassLeng"));
         MaxUserLeng = Integer.parseInt(props.getProperty("MaxUserLeng"));
         MaxLoginTries = Integer.parseInt(props.getProperty("MaxLoginTries"));
         MaxLoginFails = Integer.parseInt(props.getProperty("MaxLoginFails"));
         FailLockPeriod =  Integer.parseInt(props.getProperty("FailLockPeriod")) * 60 * 1000;
         HashMethod = props.getProperty("HashMethod");
      }
      catch (Exception ex)
      {
         System.err.println("Error fetching properties: " + ex.getMessage());
      }
   }
}
