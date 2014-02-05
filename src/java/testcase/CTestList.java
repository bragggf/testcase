/*
 * CTestList.java
 *
 * Created on July 15, 2008, 5:34 PM
 */
package testcase;

import manapp.*;

import java.sql.*;

/**
 * list of test cases
 */
public class CTestList extends CStringList {

    /**
     * Creates a new instance of CTestList
     */
    public CTestList() {
        super(true);
    }

    public void dbReadList(Connection aconn, String agroup, String fc1, String fc2) {
        try {
            /*original:         String qstr = "Select TestTitle,TestDesc,TestNotes,BugReqID,CaseSource," +
             "LastName,FirstName,AgeYrs,AgeMos,AgeWks,AgeDays,BirthDate,AgeEntryMethod,GenderCd,CreateBy,CreationDate,ModDate,BaseDate,TestResult,ResultNotes,TestId" +
             " From TestCaseTbl" +
             " Where TestGroupId='" + agroup + "'";
             */
            /*    String qstr = "select tc.TestTitle,tc.TestDesc,tc.TestNotes,tc.BugReqID,tc.CaseSource,tc.LastName,tc.FirstName,tc.AgeYrs,tc.AgeMos,tc.AgeWks,tc.AgeDays,tc.BirthDate," +
             "tc.AgeEntryMethod,tc.GenderCd,tc.CreateBy,tc.CreationDate,tc.ModDate,tc.BaseDate,tc.TestResult,tc.ResultNotes,tc.TestId," +         
             "tr1.testresult, tr1.lastrun,tr2.testresult,tr2.lastrun "  +
             " from TestCaseTbl tc join TestResultTbl tr1 join TestResultTbl tr2 " +
             " on tc.testgroupid=tr1.testgroupid and tc.testid=tr1.testid " +
             " and tc.testgroupid=tr2.testgroupid and tc.testid=tr2.testid " +
             " where tr1.forecasterid='" + fc1 + "'" +
             " and tr2.forecasterid='" + fc2 + "'" +
             " and tc.TestGroupId='" + agroup + "'";
             */
            String qstr = "select tc.TestTitle,tc.TestDesc,tc.TestNotes,tc.BugReqID,tc.CaseSource,tc.LastName,tc.FirstName,"
                    + "tc.AgeYrs,tc.AgeMos,tc.AgeWks,tc.AgeDays,tc.BirthDate,"
                    + "tc.AgeEntryMethod,tc.GenderCd,tc.CreateBy,tc.CreationDate,tc.ModDate,tc.BaseDate,tc.TestResult,tc.ResultNotes,tc.TestId,    "
                    + "tr1.testresult as tr1r,tr1.lastrun as tr1d,tr1.ResultNotes as tr1rn,tr2.testresult as tr2r,tr2.lastrun as tr2d,tr2.ResultNotes as tr2rn "
                    + "from testcasetbl tc "
                    + "left outer join testresulttbl tr1 on tc.testgroupid=tr1.testgroupid and tc.testid=tr1.testid and tr1.forecasterid='" + fc1 + "'"
                    + " left outer join testresulttbl tr2 on tc.testgroupid=tr2.testgroupid and tc.testid=tr2.testid and tr2.forecasterid='" + fc2 + "'"
                    + " where tc.TestGroupId='" + agroup + "'";


            Statement qstmt = aconn.createStatement();
            ResultSet rset = qstmt.executeQuery(qstr);

            while (rset.next()) {
                CTestItem myitem = new CTestItem();
                myitem.testgroupid = agroup;
                myitem.fc1 = fc1;
                myitem.fc2 = fc2;
                myitem.testtitle = rset.getString(1);
                myitem.testdesc = rset.getString(2);
                myitem.testnote = rset.getString(3);
                myitem.testreqid = rset.getString(4)==null?"":rset.getString(4);
                myitem.casesource = rset.getString(5)==null?"":rset.getString(5);
                myitem.lastname = rset.getString(6);
                myitem.firstname = rset.getString(7);
                myitem.ageyears = rset.getInt(8);
                myitem.agemonths = rset.getInt(9);
                myitem.ageweeks = rset.getInt(10);
                myitem.agedays = rset.getInt(11);
                myitem.birthdate = rset.getDate(12);
                myitem.agemethod = rset.getString(13)==null?"1":rset.getString(13);
                myitem.gendercd = rset.getString(14);
                myitem.createby = rset.getString(15);
                myitem.createdate = rset.getDate(16);
                myitem.moddate = rset.getDate(17);
                myitem.basedate = rset.getDate(18);
                myitem.testresult = rset.getString(19);
                myitem.resultnotes = rset.getString(20);
                myitem.testid = rset.getString(21);
                myitem.fc1result = rset.getString(22);
                myitem.fc1rundt = rset.getDate(23);
                myitem.fc1resnotes = rset.getString(24);
                myitem.fc2result = rset.getString(25);
                myitem.fc2rundt = rset.getDate(26);
                myitem.fc2resnotes = rset.getString(27);
              
                if (myitem.agemethod.length() == 0) {
                    myitem.agemethod = "1";
                }

                this.addItem(myitem.makeKey(), myitem);
            }
            rset.close();
            qstmt.close();
        } catch (Exception ex) {
            CLogError.logError(CAppConsts.ErrorFile, false, "CTestList.dbReadList cannot read list. ", ex);
        }
    }

    public String showStatus(String myforecaster1, String myforecaster2) {
        String retstr = "<table class='result' summary='test case status'>\n";
        retstr = retstr + CTestItem.showStatusHead(myforecaster1, myforecaster2);
        for (int itst = 0; itst < this.getCount(); itst++) {
            CTestItem myitem = (CTestItem) this.getItem(itst);
            retstr = retstr + myitem.showStatus();
        }
        retstr = retstr + "</table>\n";
        return (retstr);
    }

    public int getStatusCnt(String astat) {
        int statcnt = 0;
        for (int itst = 0; itst < this.getCount(); itst++) {
            CTestItem myitem = (CTestItem) this.getItem(itst);
            if (myitem.testresult.equals(astat)) {
                statcnt++;
            }
        }
        return (statcnt);
    }

    public String exportList(Connection aconn) {
        StringBuilder retstr = new StringBuilder(32678);
        retstr.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        retstr.append("<TestCaseList xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"TestCaseList.xsd\">\n");
        for (int idx = 0; idx < getCount(); idx++) {
            CTestItem myitem = (CTestItem) this.getItem(idx);
            CTestItem testitem = new CTestItem();
            testitem.copyItem(myitem);
            testitem.dbReadDetail(aconn);
            retstr.append(testitem.exportItem());
        }
        retstr.append("</TestCaseList>\n");
        return (retstr.toString());
    }
}
