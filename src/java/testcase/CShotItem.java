/*
 * CShotItem.java
 *
 * Created on July 16, 2008, 6:51 PM
 */

package testcase;

import manapp.*;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.*;

/** shot item */
public class CShotItem
{
   public String shotid;
   public Date shotdate;
   public String vaccinecd;
   public String mfrcd;
   protected SimpleDateFormat mdyfmt;
   
   public String reftype;
   public String periodtype;
   public int periodamt;
   public int periodoff;
   
   /** Creates a new instance of CShotItem */
   public CShotItem()
   {
      shotid = "";
      shotdate = new Date();
      vaccinecd = CConsts.TagNoValue;
      mfrcd = CConsts.TagNoValue;
      mdyfmt = new SimpleDateFormat(CConsts.DateFmtStr);
      
      reftype = CConsts.TagNoValue;
      periodtype = CConsts.TagNoValue;
      periodamt = 0;
      periodoff = 0;
   }
   
   public Date calcShotDate(Date abirth, Date alast)
   {
System.err.println("Birth: " + mdyfmt.format(abirth));      
System.err.println("Last: " + mdyfmt.format(alast));      
System.err.println("Reference: " + reftype);      
      
     
      Calendar refdt = new GregorianCalendar();
      if (reftype.equals(CConsts.RefTypeAge)) refdt.setTime(abirth);
      else refdt.setTime(alast);

      if (periodtype.equals(CConsts.PeriodYears)) refdt.add(Calendar.YEAR, periodamt);
      if (periodtype.equals(CConsts.PeriodMonths)) refdt.add(Calendar.MONTH, periodamt);
      if (periodtype.equals(CConsts.PeriodWeeks)) refdt.add(Calendar.DATE, periodamt*7);
      if (periodtype.equals(CConsts.PeriodDays)) refdt.add(Calendar.DATE, periodamt);
      refdt.add(Calendar.DATE, periodoff);
      shotdate.setTime(refdt.getTimeInMillis());
      return(refdt.getTime());
   }
   
   public String calcMdyShotDate(Date abirth, Date alast)
   {
      Date shtdt = calcShotDate(abirth, alast);
      return(mdyfmt.format(shtdt));
   }
   
   public String getMdyShotDate()
   {
      return(mdyfmt.format(shotdate));
   }
   
   public void setPeriodAmt(String aval)
   {
      if (aval == null || aval.length() < 1) 
      {
         periodamt = 0;
         return;
      }
      try
      {
         periodamt = Integer.parseInt(aval);
      }
      catch (Exception ex)
      {
         periodamt = 0;
      }
   }
   
   public void setPeriodOff(String aval)
   {
      if (aval == null || aval.length() < 1) 
      {
         periodoff = 0;
         return;
      }
      try
      {
         periodoff = Integer.parseInt(aval);
      }
      catch (Exception ex)
      {
         periodoff = 0;
      }
   }
   

   
   
   
   
   public String getYmdStr()
   {
      SimpleDateFormat ymdfmt = new SimpleDateFormat(CConsts.DateFmtYmd);
      return(ymdfmt.format(shotdate));
   }
   public String getShotDateStr()
   {
      if (shotdate.getTime() == 0) return("");
      return(mdyfmt.format(shotdate));
   }
   public void setShotDate(String aval) throws Exception
   {
      shotdate = mdyfmt.parse(aval);
   }
   
   public String getShotAgeDays(Date abirth)
   {
      Calendar birth = new GregorianCalendar();
      birth.setTime(abirth);

      Calendar shot = new GregorianCalendar();
      shot.setTime(shotdate);

      int days = 0;
      while (birth.compareTo(shot) <= 0)
      {
         days++;
         birth.add(Calendar.DATE, 1);
      }

      if (days <= 1) return("0 Days");
      days--;
      
      String dstr = Integer.toString(days) + " Days";
      return(dstr);
   }
   
   public String getShotAgeWeeks(Date abirth)
   {
      Calendar birth = new GregorianCalendar();
      birth.setTime(abirth);

      Calendar shot = new GregorianCalendar();
      shot.setTime(shotdate);

      int weeks = 0;
      
      while (birth.compareTo(shot) <= 0)
      {
         weeks++;
         birth.add(Calendar.DATE, 7);
      }
      
      if (weeks <= 1) return("");
      weeks--;
      birth.add(Calendar.DATE, -7);

      String wstr = "; " + Integer.toString(weeks) + " Weeks";
      
      int days = 0;
      while (birth.compareTo(shot) <= 0)
      {
         days++;
         birth.add(Calendar.DATE, 1);
      }
              
      if (days <= 1) return(wstr + " 0 Days");

      days--;
      return(wstr + " " + Integer.toString(days) + " Days");
   }
   
   public String getShotAgeMonths(Date abirth)
   {
      Calendar birth = new GregorianCalendar();
      birth.setTime(abirth);
      
      Calendar shot = new GregorianCalendar();
      shot.setTime(shotdate);
      int months = 0;
      
      while (birth.compareTo(shot) <= 0)
      {
         months++;
         birth.add(Calendar.MONTH, 1);
      }
      
      if (months <= 1) return("");
      months--;
      birth.add(Calendar.MONTH, -1);

      String mstr = "; " + Integer.toString(months) + " Months";
      
      int days = 0;
      while (birth.compareTo(shot) <= 0)
      {
         days++;
         birth.add(Calendar.DATE, 1);
      }
              
      if (days <= 1) return(mstr + " 0 Days");
      days--;
      return(mstr + " " + Integer.toString(days) + " Days");
   }
   
   public String getShotAge(Date abirth)
   {
      String daystr = getShotAgeDays(abirth);
      String wkstr = getShotAgeWeeks(abirth);
      String mnstr = getShotAgeMonths(abirth);
      
      String retstr = daystr + wkstr + mnstr;
      return(retstr);
   }
   
   public String buildShotStr(int achild, int ashot)
   {
      String shotstr = "line~" + Integer.toString(ashot) + "^" +
                   "shot_id~" + Integer.toString(ashot) + "^" +
                   "child_id~" + Integer.toString(achild) + "^" +
                   "shot_dt~" + getYmdStr() + "^" + 
                   "vaccine_cd~" + vaccinecd + "^" + 
                   "mfr_cd~" + mfrcd + "^";
      return(shotstr);
   }
   
   public String makeRefOptions()
   {
      String retstr = "<option value='"+CConsts.TagNoValue+"'>" + CConsts.TagNoLabel + "</option>\n";
      retstr = retstr + "<option value='"+CConsts.RefTypeAge+"'";
      if (reftype.equals(CConsts.RefTypeAge)) retstr = retstr + " SELECTED";
      retstr = retstr + ">" + CConsts.RefTypeAge + "</option>\n";
      retstr = retstr + "<option value='"+CConsts.RefTypeInt+"'";
      if (reftype.equals(CConsts.RefTypeInt)) retstr = retstr + " SELECTED";
      retstr = retstr + ">" + CConsts.RefTypeInt + "</option>\n";
      return(retstr);      
   }
   
   public String makePeriodOptions()
   {
      String retstr = "<option value='"+CConsts.TagNoValue+"'>" + CConsts.TagNoLabel + "</option>\n";

      retstr = retstr + "<option value='"+CConsts.PeriodDays+"'";
      if (periodtype.equals(CConsts.PeriodDays)) retstr = retstr + " SELECTED";
      retstr = retstr + ">" + CConsts.PeriodDays + "</option>\n";

      retstr = retstr + "<option value='"+CConsts.PeriodWeeks+"'";
      if (periodtype.equals(CConsts.PeriodWeeks)) retstr = retstr + " SELECTED";
      retstr = retstr + ">" + CConsts.PeriodWeeks + "</option>\n";

      retstr = retstr + "<option value='"+CConsts.PeriodMonths+"'";
      if (periodtype.equals(CConsts.PeriodMonths)) retstr = retstr + " SELECTED";
      retstr = retstr + ">" + CConsts.PeriodMonths + "</option>\n";

      retstr = retstr + "<option value='"+CConsts.PeriodYears+"'";
      if (periodtype.equals(CConsts.PeriodYears)) retstr = retstr + " SELECTED";
      retstr = retstr + ">" + CConsts.PeriodYears + "</option>\n";
      
      return(retstr);      
   }
}
