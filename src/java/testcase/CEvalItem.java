/*
 * CEvalItem.java
 *
 * Created on July 22, 2008, 12:45 PM
 */

package testcase;

import manapp.*;
import java.util.Date;
import java.text.*;

/** series evaluation result item */
public class CEvalItem
{
   public String evalid;
   public String seriescd;
   public String resultcd;
   public int doseord;
   public Date acceldate;
   public Date recomdate;
   public Date overduedate;
   protected SimpleDateFormat dtfmt;
   protected SimpleDateFormat ymdfmt;
   
   /** Creates a new instance of CEvalItem */
   public CEvalItem()
   {
      evalid = "";
      seriescd = CConsts.TagNoValue;
      resultcd = "";
      doseord = 0;
      acceldate = new Date(0);
      recomdate = new Date(0);
      overduedate = new Date(0);
      dtfmt = new SimpleDateFormat(CConsts.DateFmtStr);
      ymdfmt = new SimpleDateFormat(CConsts.DateFmtYmd);
   }
   
   public void copyItem(CEvalItem aitem)
   {
      evalid = aitem.evalid;
      seriescd = aitem.seriescd;
      resultcd = aitem.resultcd;
      doseord = aitem.doseord;
      acceldate.setTime(aitem.acceldate.getTime());
      recomdate.setTime(aitem.recomdate.getTime());
      overduedate.setTime(aitem.overduedate.getTime());
      
      if (resultcd.equals(CConsts.ResComplete) || resultcd.equals(CConsts.ResImmune))
      {
         doseord = 0;
         acceldate.setTime(0);
         recomdate.setTime(0);
         overduedate.setTime(0);
      }
   }
   
   public String getAccelDateStr()
   {
      if (acceldate.getTime() <= 0) return("");
      return(dtfmt.format(acceldate));
   }
   public void setAccelDate(String aval) throws Exception
   {
      acceldate = ymdfmt.parse(aval);
   }
   
   public String getRecomDateStr()
   {
      if (recomdate.getTime() <= 0) return("");
      return(dtfmt.format(recomdate));
   }
   public void setRecomDate(String aval) throws Exception
   {
      recomdate = ymdfmt.parse(aval);
   }
   
   public String getOverdueDateStr()
   {
      if (overduedate.getTime() <= 0) return("");
      return(dtfmt.format(overduedate));
   }
   public void setOverdueDate(String aval) throws Exception
   {
      overduedate = ymdfmt.parse(aval);
   }
}
