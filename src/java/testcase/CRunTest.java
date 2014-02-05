/*
 * CRunTest.java
 *
 * Created on July 21, 2008, 11:10 AM
 */
package testcase;

import manapp.*;
import java.sql.*;
import java.text.*;
import java.sql.Connection;
import dbconn.CDbConnMan;

//to use the foreast service jar
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.tch.fc.*;
import org.tch.fc.model.Event;
import org.tch.fc.model.EventType;
import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.ForecastItem;
import org.tch.fc.model.Service;
import org.tch.fc.model.Software;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;

/**
 * run test cases in a thread
 */
public class CRunTest extends Thread {

    private String testgrpid;
    private String testid;
    private CAppProps props;
    private CDbConnMan locconnman;
    private CDbConnMan remconnman;
    private String fc1; //forecaster 1
    private String fc1nm;
    private String fc1url;
    private String fc2; //forecaster 2
    private String fc2nm;
    private String fc2url;
    private int fcrunoption;
    static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    /**
     * Creates a new instance of CRunTest
     */
    public CRunTest(CAppProps aprops, CDbConnMan alocman, CDbConnMan aremman, String agroup, String atest, String afc1, String afc2, int runoption) {
        super();
        locconnman = alocman;
        remconnman = aremman;
        props = aprops;
        testgrpid = agroup;
        testid = atest;
        fc1 = afc1;
        fc2 = afc2;
        fcrunoption = runoption;

    }

    public void run() {
        if (fcrunoption == CAppConsts.RunFC1 || fcrunoption == CAppConsts.RunBothFC) {
            if (fc1.equals(CAppConsts.DefaultForecaster)) {//first forecaster defaults to mcir assessment
                runmcir();  //direct to mcir assessment
            } else {
                fc1nm = CForecasters.getFCname(fc1);
                fc1url = CForecasters.getFCurl(fc1);

                runForecastService(fc1nm, fc1url);
            }
        }
        if (fcrunoption == CAppConsts.RunFC2 || fcrunoption == CAppConsts.RunBothFC) {

            fc2nm = CForecasters.getFCname(fc2);
            fc2url = CForecasters.getFCurl(fc2);
            runForecastService(fc2nm, fc2url);

        }

    }

    public void runmcir() {
        Connection locconn = locconnman.getConnection();
        Connection remconn = remconnman.getConnection();

        CTestList testlist = new CTestList();
        if (testid.equals(CAppConsts.TagNoValue)) {
            testlist.dbReadList(locconn, testgrpid, fc1, fc2);
        } else {
            CTestItem testitem = new CTestItem();
            testitem.dbReadItem(locconn, testgrpid, testid);  //need to read item with its results
            testitem.fc1 = fc1;
            testitem.fc2 = fc2;

            testlist.addItem(testitem.makeKey(), testitem);
        }

        try {
            CallableStatement cstmt = remconn.prepareCall("{call forecast_request.p_request_forecast("
                    + "pi_patient_id => ?,"
                    + "pi_birth_dt => ?,"
                    + "pi_gender_cd => ?,"
                    + "pi_protocol_id => ?,"
                    + "pi_protocol_version_id=> ?,"
                    + "pi_series_cd => ?,"
                    + "pi_as_of_dt => ?,"
                    + "pi_save_fl => ?,"
                    + "pi_shot_str => ?,"
                    + "pi_titer_str => ?,"
                    + "pi_waiver_str => ?,"
                    + "po_series_eval_str => ?,"
                    + "po_series_dose_str => ?,"
                    + "po_evaluation_str => ?,"
                    + "po_status_mg => ?,"
                    + "po_status_cd => ?"
                    + ")}");

            int childid = 1;
            for (int idx = 0; idx < testlist.getCount(); idx++) {
                CTestItem testcase = (CTestItem) testlist.getItem(idx);
                testcase.dbReadDetail(locconn);
                runTestProc(locconn, testcase, childid, cstmt);
                //testcase.fc1=fc1;
                //testcase.fc2=fc2;
                childid++;
            }

            cstmt.close();
        } catch (Exception ex) {
            CLogError.logError(props.ErrorLogFile, props.ErrMsgEcho, "CRunTest.run error: ", ex);
        }
        locconnman.returnConnection(locconn);
        remconnman.returnConnection(remconn);
    }

    private void runTestProc(Connection aconn, CTestItem atestcase, int atnum, CallableStatement acstmt) {
        try {
            String shtstr = atestcase.shotlist.buildShotStr(atnum);
            String waivstr = atestcase.nonadmlist.buildWaiverStr(aconn, atnum);
            String titerstr = atestcase.nonadmlist.buildTiterStr(aconn, atnum);
            String protocol_version = "";
            String series_eval = "";
            String series_dose = "";
            String evaluation = "";
            String status_mg = "";
            String status_cd = "";
            String series_cd = ""; //default until option is implemented in this tool
            String assessdate = atestcase.getYmdStr(atestcase.basedate); //could be case specific or same for a group of cases

  /*          System.err.println("pi_patient_id => " + Integer.toString(atnum));
            System.err.println("pi_birth_dt => " + atestcase.getYmdStr(atestcase.birthdate));
            System.err.println("pi_gender_cd => " + atestcase.gendercd);
            System.err.println("pi_protocol_id => " + "1");
            System.err.println("pi_as_of_dt => " + assessdate);
            System.err.println("pi_save_fl => " + props.SaveRemoteInfo);
            System.err.println("pi_shot_str => " + shtstr);
            System.err.println("pi_titer_str => " + titerstr);
            System.err.println("pi_waiver_str => " + waivstr);
*/
            dbSetDecimalParam(acstmt, 1, Integer.toString(atnum));
            dbSetVarCharParam(acstmt, 2, atestcase.getYmdStr(atestcase.birthdate));
            dbSetVarCharParam(acstmt, 3, atestcase.gendercd);
            dbSetDecimalParam(acstmt, 4, "1"); //protocol id
            dbSetDecimalParam(acstmt, 5, protocol_version);   //leave null  
            dbSetVarCharParam(acstmt, 6, series_cd); //to evaluate specific series
            dbSetVarCharParam(acstmt, 7, assessdate);
            dbSetVarCharParam(acstmt, 8, props.SaveRemoteInfo);
            dbSetVarCharParam(acstmt, 9, shtstr);
            dbSetVarCharParam(acstmt, 10, titerstr);
            dbSetVarCharParam(acstmt, 11, waivstr);


            acstmt.registerOutParameter(12, java.sql.Types.VARCHAR);
            acstmt.registerOutParameter(13, java.sql.Types.VARCHAR);
            acstmt.registerOutParameter(14, java.sql.Types.VARCHAR);
            acstmt.registerOutParameter(15, java.sql.Types.VARCHAR);
            acstmt.registerOutParameter(16, java.sql.Types.DECIMAL);

            acstmt.executeQuery();

//System.err.println("antigen_eval: " + antigen_eval);
//System.err.println("series_dose: " + series_dose);
            series_eval = acstmt.getString(12);
            series_dose = acstmt.getString(13);
            evaluation = acstmt.getString(14);
            status_mg = acstmt.getString(15);
            status_cd = acstmt.getString(16);

            System.out.println("Result " + status_cd + " : " + status_mg);
            System.out.println("Evaluation: " + atnum);
            System.out.println(evaluation);
            System.out.println("Series Evaluation: " + atnum);
            System.out.println(series_eval);
            System.out.println("Dose Evaluation: " + atnum);
            System.out.println(series_dose);

            saveSeriesResult(aconn, atestcase, series_eval);
            saveDoseResult(aconn, atestcase, series_dose);
        } catch (Exception ex) {
            CLogError.logError(props.ErrorLogFile, props.ErrMsgEcho,
                    "runTestProc(" + atnum + ") error: ", ex);
        }
    }

    protected void saveSeriesResult(Connection aconn, CTestItem atestcase, String astr) throws Exception {
        if (astr == null || astr.length() == 0) {
            return;
        }
        SimpleDateFormat ymdfmt = new SimpleDateFormat(CAppConsts.DateFmtYmd);
        CMapCode seriesmap = new CMapCode(aconn, "SeriesTbl", "SeriesCd", "SeriesId", CMapCode.TypeInteger);
        CEvalList evallist = new CEvalList();
        CEvalItem evalitem = null;
        String rembuf = astr;

        while (rembuf.length() > 0) {
            String pairbuf = CParser.getToken(rembuf, "^");
            rembuf = CParser.getRemnant(rembuf, "^");

            if (pairbuf.length() == 0) {
                continue;
            }

            String tagstr = CParser.getToken(pairbuf, "~");
            String valstr = CParser.getRemnant(pairbuf, "~");

            if (tagstr.equals("line")) {
                if (evalitem != null) {
                    evallist.addItem(evalitem.evalid, evalitem);
                }
                evalitem = new CEvalItem();
                evalitem.evalid = evallist.makeNewId("res", 6);
            } else if (tagstr.equals("child_id")) {
                continue;
            } else if (tagstr.equals("series_id")) {
                evalitem.seriescd = seriesmap.unmapCode(valstr);
            } else if (tagstr.equals("protocol_id")) {
                continue;
            } else if (tagstr.equals("eval_result_id")) {
                evalitem.resultcd = valstr;
            } else if (tagstr.equals("ser_eval_dt")) {
                continue;
            } else if (tagstr.equals("ser_erec_shot_dt")) {
                if (valstr.length() == 0) {
                    evalitem.acceldate.setTime(0);
                } else {
                    evalitem.acceldate = ymdfmt.parse(valstr);
                }
            } else if (tagstr.equals("ser_rrec_shot_dt")) {
                if (valstr.length() == 0) {
                    evalitem.recomdate.setTime(0);
                } else {
                    evalitem.recomdate = ymdfmt.parse(valstr);
                }
            } else if (tagstr.equals("ser_over_shot_dt")) {
                if (valstr.length() == 0) {
                    evalitem.overduedate.setTime(0);
                } else {
                    evalitem.overduedate = ymdfmt.parse(valstr);
                }
            } else if (tagstr.equals("ser_next_shot_ord")) {
                if (valstr.length() == 0) {
                    evalitem.doseord = 0;
                } else {
                    evalitem.doseord = Integer.parseInt(valstr) + 1;
                }
            } else if (tagstr.equals("eval_next_dt")) {
                continue;
            } else if (tagstr.equals("vacc_sched_id")) {
                continue;
            } else if (tagstr.equals("dose_schedule_id")) {
                continue;
            } else if (tagstr.equals("ser_eval_result_cd")) {
                continue;
            } else if (tagstr.equals("recall_date")) {
                continue;
            }
        }

        if (evalitem != null) {
            evallist.addItem(evalitem.evalid, evalitem);
        }

        atestcase.edoselist.initTestResults();
        atestcase.evallist.clear();
        for (int idx = 0; idx < evallist.getCount(); idx++) {
            CEvalItem myitem = (CEvalItem) evallist.getItem(idx);
            if (atestcase.edoselist.isSeriesDose(myitem.seriescd)) {
                atestcase.evallist.makeItem(myitem);
                atestcase.edoselist.setTestResult(myitem);
            }
        }

        //atestcase.setTestStatus();
 //cef       atestcase.dbWriteItem(aconn);
        if (fcrunoption == CAppConsts.RunFC1 || fcrunoption == CAppConsts.RunBothFC) {
            atestcase.setLastRunFC1();
            atestcase.dbWriteResult1(aconn);
        }
        if (fcrunoption == CAppConsts.RunFC2 || fcrunoption == CAppConsts.RunBothFC) {
            atestcase.setLastRunFC2();
            atestcase.dbWriteResult2(aconn);
        }
        atestcase.evallist.dbWriteList(aconn, atestcase.testgroupid, atestcase.testid);
        atestcase.edoselist.dbWriteList(aconn, atestcase.testgroupid, atestcase.testid);
    }

    protected void saveDoseResult(Connection aconn, CTestItem atestcase, String astr) throws Exception {
        if (astr == null || astr.length() == 0) {
            return;
        }
        CMapCode seriesmap = new CMapCode(aconn, "SeriesTbl", "SeriesCd", "SeriesId", CMapCode.TypeInteger);
        CDosevItem evitem = null;
        String rembuf = astr;
        atestcase.dosevlist.clear();

        while (rembuf.length() > 0) {
            String pairbuf = CParser.getToken(rembuf, "^");
            rembuf = CParser.getRemnant(rembuf, "^");

            if (pairbuf.length() == 0) {
                continue;
            }

            String tagstr = CParser.getToken(pairbuf, "~");
            String valstr = CParser.getRemnant(pairbuf, "~");

            if (tagstr.equals("line")) {
                if (evitem != null) {
                    atestcase.dosevlist.addItem(evitem.shotid, evitem);
                }
                evitem = new CDosevItem();
                evitem.shotid = atestcase.dosevlist.makeNewId("sht", 6);
            } else if (tagstr.equals("child_id")) {
                continue;
            } else if (tagstr.equals("protocol_id")) {
                continue;
            } else if (tagstr.equals("dose_id")) {
                evitem.dosenum = Integer.parseInt(valstr);
            } else if (tagstr.equals("series_id")) {
                evitem.seriescd = seriesmap.unmapCode(valstr);
            } else if (tagstr.equals("dose_schedule_id")) {
                continue;
            } else if (tagstr.equals("invalid_reason_id")) {
                evitem.invalidcd = valstr;
            } else if (tagstr.equals("valid_fl")) {
                evitem.validflag = valstr;
            }
        }
        if (evitem != null) {
            atestcase.dosevlist.addItem(evitem.shotid, evitem);
        }
        atestcase.dosevlist.dbWriteList(aconn, atestcase.testgroupid, atestcase.testid);
    }

    protected void dbSetDecimalParam(PreparedStatement astmt, int anum, String astr) throws Exception {
        if (astr.equals("")) {
            astmt.setNull(anum, java.sql.Types.DECIMAL);
        } else {
            java.math.BigDecimal tmpval = new java.math.BigDecimal(astr);
            astmt.setBigDecimal(anum, tmpval);
        }
    }

    protected void dbSetVarCharParam(PreparedStatement astmt, int anum, String astr) throws Exception {
        if (astr.equals("")) {
            astmt.setNull(anum, java.sql.Types.VARCHAR);
        } else {
            astmt.setString(anum, astr);
        }
    }

    protected void dbSetCharParam(PreparedStatement astmt, int anum, String astr) throws Exception {
        if (astr.equals("")) {
            astmt.setNull(anum, java.sql.Types.CHAR);
        } else {
            astmt.setString(anum, astr);
        }
    }

    public void runForecastService(String fcserv, String fcUrl) {
        //    String serviceUrl = "TEST";
        //    Service service = Service.MCIR;
        //    String serviceUrl = "http://tchforecasttester.org/fv/forecast";

        Connection locconn = locconnman.getConnection();
        System.out.println("IN RunForecastService");

        CTestList testlist = new CTestList();
        if (testid.equals(CAppConsts.TagNoValue)) {
            testlist.dbReadList(locconn, testgrpid, fc1, fc2);
        } else {
            CTestItem otestitem = new CTestItem();
            otestitem.dbReadItem(locconn, testgrpid, testid);  //need to read item with its results
            otestitem.fc1 = fc1;
            otestitem.fc2 = fc2;

            testlist.addItem(otestitem.makeKey(), otestitem);
        }

        Service service = Service.getService(fcserv);
        //prepare software object
        Software software = new Software();

        software.setServiceUrl(fcUrl);
        software.setService(service);
        System.out.println("IN RunForecastService part2");

        try {
            ConnectorInterface connector = ConnectFactory.createConnecter(software, ForecastItem.getForecastItemList());
            int childid = 1;



            for (int idx = 0; idx < testlist.getCount(); idx++) {
                CTestItem atestitem = (CTestItem) testlist.getItem(idx);
                atestitem.dbReadDetail(locconn);

                //prepare testcase object (to use existing functionality of the forecast connector jar ) 
                TestCase testCase = new TestCase();
                System.out.println("IN RunForecastService part3");


                //testCase.setEvalDate(sdf.parse("08/08/2013"));
                testCase.setEvalDate(atestitem.basedate);
                //testCase.setPatientSex("F");
                testCase.setPatientSex(atestitem.gendercd);
                //testCase.setTestCaseId(123);
                testCase.setTestCaseId(childid);//just a temp id
                //testCase.setPatientDob(sdf.parse("04/01/2010"));
                testCase.setPatientDob(atestitem.birthdate);
                List<TestEvent> testEventList = new ArrayList<TestEvent>();

                //shot history
                CShotList shotlst = atestitem.shotlist;
                for (int j = 0; j < shotlst.getCount(); j++) {
                    CShotItem shot = (CShotItem) shotlst.getItem(j);
                    TestEvent vac = new TestEvent();
                    //           vac1.setEventDate(sdf.parse("06/12/2013"));
                    vac.setEventDate(shot.shotdate);
//            vac1.setEvent(Event.getEvent(10));  //polio
                    int vacid = Integer.parseInt(shot.vaccinecd);
                    vac.setEvent(Event.getEvent(vacid));
                    System.out.println("vac= " + Event.getEvent(vacid).getLabel() + " " + Event.getEvent(vacid).getVaccineCvx());
                    Event.getEvent(vacid).setVaccineMvx(shot.mfrcd);
                    testEventList.add(vac);
                }
                /*        TestEvent vac2 = new TestEvent();
                 vac2.setEventDate(sdf.parse("06/12/2013"));
                 vac2.setEvent(Event.getEvent(46));  //hib
                 System.out.println("vac2= " + Event.getEvent(46).getLabel() + " " + Event.getEvent(46).getVaccineCvx());

                 Event.getEvent(46).setVaccineMvx("CON");
                 testEventList.add(vac2);
                 TestEvent vac3 = new TestEvent();
                 vac3.setEventDate(sdf.parse("06/12/2013"));
                 vac3.setEvent(Event.getEvent(42));  //hep b
                 System.out.println("vac3= " + Event.getEvent(42).getLabel() + " " + Event.getEvent(42).getVaccineCvx());
                 Event.getEvent(42).setVaccineMvx("MSD");
                 testEventList.add(vac3);
                 //end cef
                 */
                testCase.setTestEventList(testEventList);
                //create connector


                System.out.println("QUERY FOR FORECASTER");
                //query forecast


                List<ForecastActual> forecastActualList = connector.queryForForecast(testCase);


                childid++;
                //put results somewhere
                if (fcserv.equals(fc1nm)) {
                    atestitem.fc1resnotes = forecastActualList.get(0).getLogText().toString();
                }
                if (fcserv.equals(fc2nm)) {
                    atestitem.fc2resnotes = forecastActualList.get(0).getLogText().toString();
                }

                atestitem.dbWriteItem(locconn);
                if (fcrunoption == CAppConsts.RunFC1 || fcrunoption == CAppConsts.RunBothFC) {
                    atestitem.setLastRunFC1();
                    atestitem.dbWriteResult1(locconn);
                }
                if (fcrunoption == CAppConsts.RunFC2 || fcrunoption == CAppConsts.RunBothFC) {
                    atestitem.setLastRunFC2();
                    atestitem.dbWriteResult2(locconn);
                }
                //atestitem.evallist.dbWriteList(aconn, atestitem.testgroupid, atestitem.testid);
                //atestitem.edoselist.dbWriteList(aconn, atestitem.testgroupid, atestitem.testid);


                // debug:
                System.out.println();
                if (forecastActualList.size() > 0) {
                    System.out.print(forecastActualList.get(0).getLogText().toString());
                }
                /*           System.out.println();
                 System.out.println("DEMO RESULTS");

                 System.out.println("+----------------+------+------------+------------+------------+------------+-----+");
                 System.out.print(left("Forecast Item", 15));
                 System.out.print(left("Dose", 5));
                 System.out.print(left("Valid", 11));
                 System.out.print(left("Due", 11));
                 System.out.print(left("Overdue", 11));
                 System.out.print(left("Finished", 11));
                 System.out.print(left("CVX", 4));
                 System.out.println("|");
                 System.out.println("+----------------+------+------------+------------+------------+------------+-----+");
                 for (ForecastActual forecastActual : forecastActualList) {
                 System.out.print(left(forecastActual.getForecastItem().getLabel(), 15));
                 System.out.print(left(forecastActual.getDoseNumber(), 5));
                 System.out.print(left(forecastActual.getValidDate(), 11));
                 System.out.print(left(forecastActual.getDueDate(), 11));
                 System.out.print(left(forecastActual.getOverdueDate(), 11));
                 System.out.print(left(forecastActual.getFinishedDate(), 11));
                 System.out.print(left(forecastActual.getVaccineCvx(), 4));
                 System.out.println("|");
                 }
                 System.out.println("+----------------+------+------------+------------+------------+------------+-----+");
                 */
            }//loop throught testcases
        } catch (Exception ex) {
            CLogError.logError(props.ErrorLogFile, props.ErrMsgEcho, "runForecastService error: ", ex);
        }
        locconnman.returnConnection(locconn);
        


    }

    private static String left(Date date, int length) {
        if (date == null) {
            return left("", length);
        }
        return left(sdf.format(date), length);
    }

    private static String left(String text, int length) {
        String result = text + "                                              ";
        result = result.substring(0, length);
        return "| " + result;
    }
}
