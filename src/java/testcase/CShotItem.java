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

//   public String reftype;
//   public String periodtype;
//   public int periodamt;
//   public int periodoff;

   //new
   public String vacnote;
   public int vageyears;
   public int vagemonths;
   public int vageweeks;
   public int vagedays;

   /** Creates a new instance of CShotItem */
   public CShotItem()
   {
      shotid = "";
      shotdate = new Date();
      vaccinecd = CAppConsts.TagNoValue;
      mfrcd = CAppConsts.TagNoValue;
      mdyfmt = new SimpleDateFormat(CAppConsts.DateFmtStr);

 //     reftype = CAppConsts.TagNoValue;
 //     periodtype = CAppConsts.TagNoValue;
 //     periodamt = 0;
 //     periodoff = 0;
      
      vacnote="";
      vageyears=0;
      vagemonths=0;
      vageweeks=0;
      vagedays=0;
   }

   /*public Date calcShotDate(Date abirth, Date alast)
   {
//System.err.println("Birth: " + mdyfmt.format(abirth));
//System.err.println("Last: " + mdyfmt.format(alast));
//System.err.println("Reference: " + reftype);
      Calendar refdt = new GregorianCalendar();
      if (reftype.equals(CAppConsts.RefTypeAge)) refdt.setTime(abirth);
      else refdt.setTime(alast);

      if (periodtype.equals(CAppConsts.PeriodYears)) refdt.add(Calendar.YEAR, periodamt);
      if (periodtype.equals(CAppConsts.PeriodMonths)) refdt.add(Calendar.MONTH, periodamt);
      if (periodtype.equals(CAppConsts.PeriodWeeks)) refdt.add(Calendar.DATE, periodamt*7);
      if (periodtype.equals(CAppConsts.PeriodDays)) refdt.add(Calendar.DATE, periodamt);
      refdt.add(Calendar.DATE, periodoff);
      shotdate.setTime(refdt.getTimeInMillis());
      return(refdt.getTime());
   }

   public String calcMdyShotDate(Date abirth, Date alast)
   {
      Date shtdt = calcShotDate(abirth, alast);
      return(mdyfmt.format(shtdt));
   }
*/
   public String getMdyShotDate()
   {
      return(mdyfmt.format(shotdate));
   }

/*   public void setPeriodAmt(String aval)
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
*/
   public String getYmdStr()
   {
      SimpleDateFormat ymdfmt = new SimpleDateFormat(CAppConsts.DateFmtYmd);
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
/*
   public String makeRefOptions()
   {
      String retstr = "<option value='"+CAppConsts.TagNoValue+"'>" + CAppConsts.TagNoLabel + "</option>\n";
      retstr = retstr + "<option value='"+CAppConsts.RefTypeAge+"'";
      if (reftype.equals(CAppConsts.RefTypeAge)) retstr = retstr + " SELECTED";
      retstr = retstr + ">" + CAppConsts.RefTypeAge + "</option>\n";
      retstr = retstr + "<option value='"+CAppConsts.RefTypeInt+"'";
      if (reftype.equals(CAppConsts.RefTypeInt)) retstr = retstr + " SELECTED";
      retstr = retstr + ">" + CAppConsts.RefTypeInt + "</option>\n";
      return(retstr);
   }

   public String makePeriodOptions()
   {
      String retstr = "<option value='"+CAppConsts.TagNoValue+"'>" + CAppConsts.TagNoLabel + "</option>\n";

      retstr = retstr + "<option value='"+CAppConsts.PeriodDays+"'";
      if (periodtype.equals(CAppConsts.PeriodDays)) retstr = retstr + " SELECTED";
      retstr = retstr + ">" + CAppConsts.PeriodDays + "</option>\n";

      retstr = retstr + "<option value='"+CAppConsts.PeriodWeeks+"'";
      if (periodtype.equals(CAppConsts.PeriodWeeks)) retstr = retstr + " SELECTED";
      retstr = retstr + ">" + CAppConsts.PeriodWeeks + "</option>\n";

      retstr = retstr + "<option value='"+CAppConsts.PeriodMonths+"'";
      if (periodtype.equals(CAppConsts.PeriodMonths)) retstr = retstr + " SELECTED";
      retstr = retstr + ">" + CAppConsts.PeriodMonths + "</option>\n";

      retstr = retstr + "<option value='"+CAppConsts.PeriodYears+"'";
      if (periodtype.equals(CAppConsts.PeriodYears)) retstr = retstr + " SELECTED";
      retstr = retstr + ">" + CAppConsts.PeriodYears + "</option>\n";

      return(retstr);
   }
*/
   public String exportItem()
   {
      StringBuilder retstr = new StringBuilder(128);
      retstr.append("<ShotItem>\n");
      retstr.append("<ShotDate>" + mdyfmt.format(shotdate) + "</ShotDate>\n");
      retstr.append("<VaccineCd>" + vaccinecd + "</VaccineCd>\n");
      retstr.append("<MfrCd>" + mfrcd + "</MfrCd>\n");
//      retstr.append("<RefType>" + reftype + "</RefType>\n");
//      retstr.append("<PeriodType>" + periodtype + "</PeriodType>\n");
//      retstr.append("<PeriodAmt>" + Integer.toString(periodamt) + "</PeriodAmt>\n");
//      retstr.append("<PeriodOff>" + Integer.toString(periodoff) + "</PeriodOff>\n");
      retstr.append("</ShotItem>\n");
      return(retstr.toString());
   }

   
}
