/*
 * CNonadItem.java
 *
 * Created on July 17, 2008, 6:13 PM
 */

package testcase;

import manapp.*;

import java.util.Date;
import java.text.*;
import java.sql.*;

/** non-administered vaccine item */
public class CNonadItem
{
   public String nonadmid;
   public Date nonadmdate;
   public String seriescd;
   public String reasoncd;
   protected SimpleDateFormat dtfmt;
   
   public int nageyears;
   public int nagemonths;
   public int nageweeks;
   public int nagedays;

   /** Creates a new instance of CNonadItem */
   public CNonadItem()
   {
      nonadmid = "";
      nonadmdate = new Date();
      seriescd = CAppConsts.TagNoValue;
      reasoncd = CAppConsts.TagNoValue;
      dtfmt = new SimpleDateFormat(CAppConsts.DateFmtStr);
      nageyears=0;
      nagemonths=0;
      nageweeks=0;
      nagedays=0;
   }

   public String getYmdStr(Date adate)
   {
      SimpleDateFormat ymdfmt = new SimpleDateFormat(CAppConsts.DateFmtYmd);
      return(ymdfmt.format(adate));
   }
   public String getNonadmDateStr()
   {
      if (nonadmdate.getTime() == 0) return("");
      return(dtfmt.format(nonadmdate));
   }
   public void setNonadmDate(String aval) throws Exception
   {
      nonadmdate = dtfmt.parse(aval);
   }

   public String buildWaiverStr(Connection aconn, int achild, int ashot)
   {
      CMapCode seriesmap = new CMapCode(aconn, "SeriesTbl", "SeriesCd", "SeriesId", CMapCode.TypeInteger);
      String retstr = "line~" + Integer.toString(ashot) + "^" +
                   "child_waiver_id~" + Integer.toString(ashot) + "^" +
                   "child_id~" + Integer.toString(achild) + "^" +
                   "series_id~" + seriesmap.mapCode(seriescd) + "^" +
                   "waiver_type_id~1^" +
                   "waiver_date~" + getYmdStr(nonadmdate) + "^" +
                   "date_created~" + getYmdStr(new Date()) + "^" +
                   "created_by~IMM_ASSESS_L^";
      return(retstr);
   }

   public String buildTiterStr(Connection aconn, int achild, int ashot)
   {
      CMapCode antigenmap = new CMapCode(aconn, "SeriesTbl", "SeriesCd", "AntigenId", CMapCode.TypeInteger);
      String retstr = "line~" + Integer.toString(ashot) + "^" +
                   "child_titer_id~" + Integer.toString(ashot) + "^" +
                   "child_id~" + Integer.toString(achild) + "^" +
                   "antigen_id~" + antigenmap.mapCode(seriescd) + "^" +
                   "titer_date~" + getYmdStr(nonadmdate) + "^" +
                   "date_created~" + getYmdStr(new Date()) + "^" +
                   "created_by~IMM_ASSESS_L^";
      return(retstr);
   }

   public String exportItem()
   {
      StringBuilder retstr = new StringBuilder(128);
      retstr.append("<NonAdminItem>\n");
      retstr.append("<NonadmDate>" + dtfmt.format(nonadmdate) + "</NonadmDate>\n");
      retstr.append("<AntSeriesCd>" + seriescd + "</AntSeriesCd>\n");
      retstr.append("<ReasonCd>" + reasoncd + "</ReasonCd>\n");
      retstr.append("</NonAdminItem>\n");
      return(retstr.toString());
   }
}
