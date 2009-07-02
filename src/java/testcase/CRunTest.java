/*
 * CRunTest.java
 *
 * Created on July 21, 2008, 11:10 AM
 */

package testcase;

import manapp.*;
import java.sql.*;
import java.text.*;

/** run test cases in a thread */
public class CRunTest extends Thread
{
   private String testgrpid;
   private String testid;
   private CAppProps props;
   
   /** Creates a new instance of CRunTest */
   public CRunTest(CAppProps aprops, String agroup, String atest)
   {
      super();
      props = aprops;
      testgrpid = agroup;
      testid = atest;
   }
   
   public void run()
   {
      CDbConnect locdbconn = new CDbConnect(props.DbConfigFile, props.ErrorLogFile, props.ErrMsgEcho); 
      Connection locconn = locdbconn.getConnection();
      CDbConnect remdbconn = new CDbConnect(props.RemDbConfigFile, props.ErrorLogFile, props.ErrMsgEcho);
      Connection remconn = remdbconn.getConnection();

      CTestList testlist = new CTestList();
      if (testid.equals(CConsts.TagNoValue))
      {
         testlist.dbReadList(locconn, testgrpid);
      }
      else
      {
         CTestItem testitem = new CTestItem();
         testitem.dbReadItem(locconn, testgrpid, testid);
         testlist.addItem(testitem.makeKey(), testitem);
      }
      
      try
      {
         CallableStatement cstmt = remconn.prepareCall("{call forecast_request.p_request_forecast(" +
               "pi_patient_id => ?," +
               "pi_birth_dt => ?," +
               "pi_gender_cd => ?," +
               "pi_protocol_id => ?," +
               "pi_as_of_dt => ?," +
               "pi_save_fl => ?," +
               "pi_shot_str => ?," +
               "pi_titer_str => ?," +
               "pi_waiver_str => ?," +
               "po_antigen_eval_str => ?," +
               "po_series_eval_str => ?," +
               "po_series_dose_str => ?," +
               "po_evaluation_str => ?," +
               "po_status_mg => ?," +
               "po_status_cd => ?" +
               ")}");

         int childid = 1;
         for (int idx = 0; idx < testlist.getCount(); idx++)
         {
            CTestItem testcase = (CTestItem) testlist.getItem(idx);
            testcase.dbReadDetail(locconn);
            runTestProc(locconn, testcase, childid, cstmt);
            childid++;
         }
         
         cstmt.close();
         remdbconn.shutDown();
         locdbconn.shutDown();
      }
      catch (Exception ex)
      {
         CLogError.logError(props.ErrorLogFile, props.ErrMsgEcho, "CRunTest.run error: ", ex);
      }
   }
   
   public void runTestProc(Connection aconn, CTestItem atestcase, int atnum, CallableStatement acstmt)
   {
      try
      {
         String shtstr = atestcase.shotlist.buildShotStr(atnum);
         String waivstr = atestcase.nonadmlist.buildWaiverStr(aconn, atnum);
         String titerstr = atestcase.nonadmlist.buildTiterStr(aconn, atnum);
/*
System.err.println("pi_patient_id => " + Integer.toString(atnum));
System.err.println("pi_birth_dt => " + atestcase.getYmdStr(atestcase.birthdate));
System.err.println("pi_gender_cd => " + atestcase.gendercd);
System.err.println("pi_protocol_id => " + "1");
System.err.println("pi_as_of_dt => " + atestcase.getYmdStr(atestcase.basedate));
System.err.println("pi_save_fl => " + props.SaveRemoteInfo);
System.err.println("pi_shot_str => " + shtstr);
System.err.println("pi_titer_str => " + titerstr);
System.err.println("pi_waiver_str => " + waivstr);
*/         
         dbSetDecimalParam(acstmt, 1, Integer.toString(atnum));
         dbSetVarCharParam(acstmt, 2, atestcase.getYmdStr(atestcase.birthdate));
         dbSetVarCharParam(acstmt, 3, atestcase.gendercd);
         dbSetDecimalParam(acstmt, 4, "1");
         dbSetVarCharParam(acstmt, 5, atestcase.getYmdStr(atestcase.basedate));
         dbSetVarCharParam(acstmt, 6, props.SaveRemoteInfo);
         dbSetVarCharParam(acstmt, 7, shtstr);
         dbSetVarCharParam(acstmt, 8, titerstr);
         dbSetVarCharParam(acstmt, 9, waivstr);
        
         acstmt.registerOutParameter(10, java.sql.Types.VARCHAR);
         acstmt.registerOutParameter(11, java.sql.Types.VARCHAR);
         acstmt.registerOutParameter(12, java.sql.Types.VARCHAR);
         acstmt.registerOutParameter(13, java.sql.Types.VARCHAR);
         acstmt.registerOutParameter(14, java.sql.Types.VARCHAR);
         acstmt.registerOutParameter(15, java.sql.Types.DECIMAL);
         
         acstmt.executeQuery();
         
         String antigen_eval = acstmt.getString(10);
         String series_dose = acstmt.getString(12);
//System.err.println("antigen_eval: " + antigen_eval);
//System.err.println("series_dose: " + series_dose);
         
         saveSeriesResult(aconn, atestcase, antigen_eval);
         saveDoseResult(aconn, atestcase, series_dose);
      }
      catch (Exception ex)
      {
         CLogError.logError(props.ErrorLogFile, props.ErrMsgEcho, 
               "runTestProc(" + atnum + ") error: ", ex);
      }
   }

   protected void saveSeriesResult(Connection aconn, CTestItem atestcase, String astr) throws Exception
   {
      if (astr == null || astr.length() == 0) return;
      SimpleDateFormat ymdfmt = new SimpleDateFormat(CConsts.DateFmtYmd);
      CMapCode seriesmap = new CMapCode(aconn, "SeriesTbl", "SeriesCd", "SeriesId", CMapCode.TypeInteger);
      CEvalList evallist = new CEvalList();
      CEvalItem evalitem = null;
      String rembuf = astr;
      
      while (rembuf.length() > 0)
      {
         String pairbuf = CParser.getToken(rembuf, "^");
         rembuf = CParser.getRemnant(rembuf, "^");
         
         if (pairbuf.length() == 0) continue;
         
         String tagstr = CParser.getToken(pairbuf,"~");
         String valstr = CParser.getRemnant(pairbuf,"~");
         
         if (tagstr.equals("line"))
         {
            if (evalitem != null) evallist.addItem(evalitem.evalid, evalitem);
            evalitem = new CEvalItem();
            evalitem.evalid = evallist.makeNewId("res", 6);
         }
         
         else if (tagstr.equals("child_id")) 
            continue;
         
         else if (tagstr.equals("series_id")) 
            evalitem.seriescd = seriesmap.unmapCode(valstr);

         else if (tagstr.equals("protocol_id")) 
            continue;
         
         else if (tagstr.equals("eval_result_id")) 
            evalitem.resultcd = valstr;
         
         else if (tagstr.equals("ser_eval_dt"))
            continue;
         
         else if (tagstr.equals("ser_erec_shot_dt"))
         {
            if (valstr.length() == 0) evalitem.acceldate.setTime(0);
            else evalitem.acceldate = ymdfmt.parse(valstr);
         }
         
         else if (tagstr.equals("ser_rrec_shot_dt"))
         {
            if (valstr.length() == 0) evalitem.recomdate.setTime(0);
            else evalitem.recomdate = ymdfmt.parse(valstr);
         }
         
         else if (tagstr.equals("ser_over_shot_dt"))
         {
            if (valstr.length() == 0) evalitem.overduedate.setTime(0);
            else evalitem.overduedate = ymdfmt.parse(valstr);
         }
         
         else if (tagstr.equals("ser_next_shot_ord"))
         {
            if (valstr.length() == 0) evalitem.doseord = 0;
            else evalitem.doseord = Integer.parseInt(valstr) + 1;
         }
         
         else if (tagstr.equals("eval_next_dt"))
            continue;
         
         else if (tagstr.equals("vacc_sched_id"))
            continue;
         
         else if (tagstr.equals("dose_schedule_id"))
            continue;
         
         else if (tagstr.equals("ser_eval_result_cd"))
            continue;
         
         else if (tagstr.equals("recall_date"))
            continue;
      }
      
      if (evalitem != null) evallist.addItem(evalitem.evalid, evalitem);

      atestcase.edoselist.initTestResults();
      atestcase.evallist.clear();
      for (int idx = 0; idx < evallist.getCount(); idx++)
      {
         CEvalItem myitem = (CEvalItem) evallist.getItem(idx);
         if (atestcase.edoselist.isSeriesDose(myitem.seriescd))
         {
            atestcase.evallist.makeItem(myitem);
            atestcase.edoselist.setTestResult(myitem);
         }
      }

      atestcase.setTestStatus();
      atestcase.dbWriteItem(aconn);
      atestcase.evallist.dbWriteList(aconn, atestcase.testgroupid, atestcase.testid);
      atestcase.edoselist.dbWriteList(aconn, atestcase.testgroupid, atestcase.testid);
   }

   protected void saveDoseResult(Connection aconn, CTestItem atestcase, String astr) throws Exception
   {
      if (astr == null || astr.length() == 0) return;
      CMapCode seriesmap = new CMapCode(aconn, "SeriesTbl", "SeriesCd", "SeriesId", CMapCode.TypeInteger);
      CDosevItem evitem = null;
      String rembuf = astr;
      atestcase.dosevlist.clear();
      
      while (rembuf.length() > 0)
      {
         String pairbuf = CParser.getToken(rembuf, "^");
         rembuf = CParser.getRemnant(rembuf, "^");
         
         if (pairbuf.length() == 0) continue;
         
         String tagstr = CParser.getToken(pairbuf,"~");
         String valstr = CParser.getRemnant(pairbuf,"~");
         
         if (tagstr.equals("line"))
         {
            if (evitem != null) atestcase.dosevlist.addItem(evitem.shotid, evitem);
            evitem = new CDosevItem();
            evitem.shotid = atestcase.dosevlist.makeNewId("sht", 6);
         }
         
         else if (tagstr.equals("child_id")) 
            continue;

         else if (tagstr.equals("protocol_id")) 
            continue;
         
         else if (tagstr.equals("dose_id")) 
            evitem.dosenum = Integer.parseInt(valstr);
         
         else if (tagstr.equals("series_id")) 
            evitem.seriescd = seriesmap.unmapCode(valstr);
         
         else if (tagstr.equals("dose_schedule_id")) 
            continue;
         
         else if (tagstr.equals("invalid_reason_id"))
            evitem.invalidcd = valstr;
         
         else if (tagstr.equals("valid_fl"))
            evitem.validflag = valstr;
      }
      if (evitem != null) atestcase.dosevlist.addItem(evitem.shotid, evitem);
      atestcase.dosevlist.dbWriteList(aconn, atestcase.testgroupid, atestcase.testid);
   }
   
   protected void dbSetDecimalParam(PreparedStatement astmt, int anum, String astr) throws Exception
   {
      if (astr.equals(""))
      {
         astmt.setNull(anum, java.sql.Types.DECIMAL);
      }
      else
      {
         java.math.BigDecimal tmpval = new java.math.BigDecimal(astr);
         astmt.setBigDecimal(anum, tmpval); 
      }
   }
   
   protected void dbSetVarCharParam(PreparedStatement astmt, int anum, String astr) throws Exception
   {
      if (astr.equals(""))
      {
         astmt.setNull(anum, java.sql.Types.VARCHAR);
      }
      else
      {
         astmt.setString(anum, astr);
      }
   }
   
   protected void dbSetCharParam(PreparedStatement astmt, int anum, String astr) throws Exception
   {
      if (astr.equals(""))
      {
         astmt.setNull(anum, java.sql.Types.CHAR);
      }
      else
      {
         astmt.setString(anum, astr);
      }
   }
}
