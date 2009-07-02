/*
 * CConsts.java
 *
 * Created on April 17, 2008, 12:13 PM
 */

package manapp;

public class CConsts
{
   final static public String AppPropFile = "app.properties";
   final static public String NotSelected = "Z";
   final static public String ErrMsgFile = "/apps/testcase/logs/TestcaseErr.log";
   final static public String WebAppTitle = "Test Cases";
   final static public String WebAppAbbr = "Test Cases";
   final static public String WebAppVersion = "0.3.0";
   final static public String WebAppLogo = "images/AltarumLogo.png";
   final static public String WebAppLogoAlt = "Altarum Institute";
   final static public String WebAppLogoTitle = "Systems Research for Better Health";
   final static public String JspLinkCentral = "/testcase/DoTestCase";
   final static public String LinkCentral = "/DoTestCase";
   final static public String LinkLoginPage = "LoginPage";
   final static public String LinkPassChange = "SetpwPage";
   final static public String LinkLoginSuccess = "StatusPage";
   final static public String LinkLoginFailure = "LoginFailPage";
   
   final static public String RoleNone = "none";
   final static public String RoleUser = "user";
   final static public String RoleAdmin = "admin";
   
   final static public String LoginFailure = "Failure";
   final static public String LoginSuccess = "Success";
   
   final static public long MilsecDay =  24 * 60 * 60 * 1000;
   final static public long PwLifeDays = 365;      //60;
   final static public int MinPassDiff = 2;
   final static public int MinPassLower = 2;
   final static public int MinPassUpper = 2;
   final static public int MinPassDigit = 2;
   final static public int MinPassSpecial = 2;
   final static public int MinPassLeng = MinPassLower + MinPassUpper + MinPassDigit + MinPassSpecial;

   final static public int MaxPassLeng = 20;
   final static public int MaxUserLeng = 32;
   
   final static public int MaxLoginTries = 3;
   final static public int MaxLoginFails = 6;
         
   final static public int FailLockOpen = 0;
   final static public int FailLockTemp = 1;
   final static public int FailLockPerm = 2;
   final static public long FailLockPeriod =  60 * 60 * 1000;
         
   final static public String PwChangeRequire = "Required";
   final static public String PwChangeOptional = "Optional";
   
   final static public String TagNewValue = "[Specify]";
   final static public String TagTextArea = "textarea";
   final static public String TagInputText = "input type='text'";
   final static public String TagNoValue = "z";
   final static public String TagNoLabel = "[Specify]";
   final static public String DateFmtStr = "MM/dd/yyyy";
   final static public String DateFmtYmd = "yyyyMMdd";
         
   final static public String StatusPass = "Pass";
   final static public String StatusFail = "Fail";
   final static public String StatusSent = "Sent";
   final static public String StatusNone = "Not Run";
   
   final static public String StylePass = "testpass";
   final static public String StyleFail = "testfail";
   final static public String StyleNone = "testnone";
   
   final static public String ResComplete = "1";
   final static public String ResImmune = "5";

   final static public int MaxLenExpectTxt = 100;
   final static public int MaxLenTitle = 30;
   final static public int MaxLenNote = 512;
   final static public int MaxLenDate = 10;
   final static public int MaxLenDoseNum = 2;
   final static public int MaxLenName = 20;
   
   final static public int NumSlotAntEval = 5;
   final static public int NewSlotAntEval = 2;
   final static public int NumSlotShotHist = 6;
   final static public int NewSlotShotHist = 2;
   final static public int NumSlotNonAdmin = 3;
   final static public int NewSlotNonAdmin = 1;

   final static public String RefTypeAge = "Age";
   final static public String RefTypeInt = "Interval";
   final static public String PeriodDays = "Days";
   final static public String PeriodWeeks = "Weeks";
   final static public String PeriodMonths = "Months";
   final static public String PeriodYears = "Years";
   
}
