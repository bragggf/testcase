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
   
   /** Creates a new instance of CDoseItem */
   public CDoseItem()
   {
      doseid = "";
      seriescd = CConsts.TagNoValue;
      resultcd = CConsts.TagNoValue;
      doseord = 0;
      acceldate = new Date(0);
      recomdate = new Date(0);
      overduedate = new Date(0);
      testresult = CConsts.StatusNone;
      dtfmt = new SimpleDateFormat(CConsts.DateFmtStr);
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
      this.testresult = CConsts.StatusNone;

      if (aitem.resultcd.equals(this.resultcd) && this.resultcd.equals(CConsts.ResComplete))
      {
         this.testresult = CConsts.StatusPass;
         return;
      }

      if (aitem.resultcd.equals(this.resultcd) && this.resultcd.equals(CConsts.ResImmune))
      {
         this.testresult = CConsts.StatusPass;
         return;
      }

      if (!aitem.resultcd.equals(this.resultcd))
      {
         this.testresult = CConsts.StatusFail;
         return;
      }
      
      if (aitem.doseord != this.doseord)
      {
         this.testresult = CConsts.StatusFail;
         return;
      }
      
      if (aitem.acceldate.getTime() != this.acceldate.getTime())
      {
         this.testresult = CConsts.StatusFail;
         return;
      }
      
      if (aitem.recomdate.getTime() != this.recomdate.getTime())
      {
         this.testresult = CConsts.StatusFail;
         return;
      }
      
      if (aitem.overduedate.getTime() != this.overduedate.getTime())
      {
         this.testresult = CConsts.StatusFail;
         return;
      }
      
      this.testresult = CConsts.StatusPass;
   }
}
