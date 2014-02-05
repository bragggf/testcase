/*
 * CDoseItem.java
 *
 * Created on July 17, 2008, 4:02 PM
 */

package testcase;

import manapp.*;
import java.util.Date;
import java.text.*;

/** expected or resulting next dose item */
public class CDoseItem
{
   public String doseid;
   public String seriescd;
   public String resultcd;
   public int doseord;
   public Date acceldate;
   public Date recomdate;
   public Date overduedate;
   public String testresult;
   protected SimpleDateFormat dtfmt;
   public int accageyears;
   public int accagemonths;
   public int accageweeks;
   public int accagedays;
   public int recageyears;
   public int recagemonths;
   public int recageweeks;
   public int recagedays;
   public int ovrageyears;
   public int ovragemonths;
   public int ovrageweeks;
   public int ovragedays;

   /** Creates a new instance of CDoseItem */
   public CDoseItem()
   {
      doseid = "";
      seriescd = CAppConsts.TagNoValue;
      resultcd = CAppConsts.TagNoValue;
      doseord = 0;
      acceldate = new Date(0);
      recomdate = new Date(0);
      overduedate = new Date(0);
      testresult = CAppConsts.StatusNone;
      dtfmt = new SimpleDateFormat(CAppConsts.DateFmtStr);
      accageyears = 0;
      accagemonths = 0;
      accageweeks = 0;
      accagedays = 0;
      recageyears = 0;
      recagemonths = 0;
      recageweeks = 0;
      recagedays = 0;
      ovrageyears = 0;
      ovragemonths = 0;
      ovrageweeks = 0;
      ovragedays = 0;
}

   public String getAccelDateStr()
   {
      if (acceldate.getTime() <= 0) return("&nbsp;");
      return(dtfmt.format(acceldate));
   }
   public void setAccelDate(String aval) throws Exception
   {
      if (aval == null || aval.length() < 4) acceldate = new Date(0);
      else acceldate = dtfmt.parse(aval);
   }
   public void setAccelDate(long aval) throws Exception
   {
      acceldate.setTime(aval);
   }

   public String getRecomDateStr()
   {
      if (recomdate.getTime() <= 0) return("&nbsp;");
      return(dtfmt.format(recomdate));
   }
   public void setRecomDate(String aval) throws Exception
   {
      if (aval == null || aval.length() < 4) recomdate = new Date(0);
      else recomdate = dtfmt.parse(aval);
   }
   public void setRecomDate(long aval) throws Exception
   {
      recomdate.setTime(aval);
   }

   public String getOverdueDateStr()
   {
      if (overduedate.getTime() <= 0) return("&nbsp;");
      return(dtfmt.format(overduedate));
   }
   public void setOverdueDate(String aval) throws Exception
   {
      if (aval == null || aval.length() < 4) overduedate = new Date(0);
      else overduedate = dtfmt.parse(aval);
   }
   public void setOverdueDate(long aval) throws Exception
   {
      overduedate.setTime(aval);
   }

   public void setTestResult(CEvalItem aitem)
   {
      this.testresult = CAppConsts.StatusNone;

      if (aitem.resultcd.equals(this.resultcd) && this.resultcd.equals(CAppConsts.ResComplete))
      {
         this.testresult = CAppConsts.StatusPass;
         return;
      }

      if (aitem.resultcd.equals(this.resultcd) && this.resultcd.equals(CAppConsts.ResImmune))
      {
         this.testresult = CAppConsts.StatusPass;
         return;
      }

      if (!aitem.resultcd.equals(this.resultcd))
      {
         this.testresult = CAppConsts.StatusFail;
         return;
      }

      if (aitem.doseord != this.doseord)
      {
         this.testresult = CAppConsts.StatusFail;
         return;
      }

      if (aitem.acceldate.getTime() != this.acceldate.getTime())
      {
         this.testresult = CAppConsts.StatusFail;
         return;
      }

      if (aitem.recomdate.getTime() != this.recomdate.getTime())
      {
         this.testresult = CAppConsts.StatusFail;
         return;
      }

      if (aitem.overduedate.getTime() != this.overduedate.getTime())
      {
         this.testresult = CAppConsts.StatusFail;
         return;
      }

      this.testresult = CAppConsts.StatusPass;
   }

   public String exportItem()
   {
      StringBuilder retstr = new StringBuilder(128);
      retstr.append("<ExpectDoseItem>\n");
      retstr.append("<SeriesCd>" + seriescd + "</SeriesCd>\n");
      retstr.append("<ResultCd>" + resultcd + "</ResultCd>\n");
      retstr.append("<NextDoseNum>" + Integer.toString(doseord) + "</NextDoseNum>\n");
      retstr.append("<AccelDate>" + dtfmt.format(acceldate) + "</AccelDate>\n");
      retstr.append("<RecomDate>" + dtfmt.format(recomdate) + "</RecomDate>\n");
      retstr.append("<OverdueDate>" + dtfmt.format(overduedate) + "</OverdueDate>\n");
      retstr.append("</ExpectDoseItem>\n");
      return(retstr.toString());
   }
}