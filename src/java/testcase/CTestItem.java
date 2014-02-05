/*
 * CTestItem.java
 *
 * Created on July 15, 2008, 3:38 PM
 */
package testcase;

import manapp.*;

import java.util.Date;
import java.text.*;
import java.sql.*;
import javax.servlet.http.*;

/**
 * test case item
 */
public class CTestItem {

    public String testgroupid;
    public String testid;
    public String testtitle;
    public String testdesc;
    public String testnote;
    public String testreqid;
    public String casesource;
    public String lastname;
    public String firstname;
    public int ageyears;
    public int agemonths;
    public int ageweeks;
    public int agedays;
    public Date birthdate;
    public String agemethod;
    public String gendercd;
    public String createby;
    public Date createdate;
    public Date moddate;
    public Date basedate;
    public String testresult;
    public String resultnotes;
    public String fc1;
    public String fc1result;
    public Date fc1rundt;
    public String fc1resnotes;
    public String fc2;
    public String fc2result;
    public Date fc2rundt;
    public String fc2resnotes;
    public CShotList shotlist;
    public CNonadList nonadmlist;
    public CTextList ereslist;
    public CDoseList edoselist;
    public CEvalList evallist;
    public CDosevList dosevlist;
    protected SimpleDateFormat dtfmt;

    /**
     * Creates a new instance of CTestItem
     */
    public CTestItem() {
        testgroupid = "";
        testid = "";
        testtitle = "";
        testdesc = "";
        testnote = "";
        testreqid = "";
        casesource = "";
        lastname = "";
        firstname = "";
        ageyears = 0;
        agemonths = 0;
        ageweeks = 0;
        agedays = 0;
        agemethod = "1";
        birthdate = new Date(0);
        birthdate = new Date(birthdate.getTime() - 2 * 365 * 24 * 60 * 60 * 1000);
        gendercd = "";
        createby = "";
        createdate = new Date();
        moddate = new Date();
        basedate = new Date();
        testresult = CAppConsts.StatusNone;
        resultnotes = "";
        fc1 = "";
        fc1result = CAppConsts.StatusNone;
        fc1rundt = null;
        fc1resnotes = "";
        fc2 = "";
        fc2result = CAppConsts.StatusNone;
        fc2rundt = null;
        fc2resnotes = "";

        shotlist = new CShotList();
        nonadmlist = new CNonadList();
        ereslist = new CTextList();
        edoselist = new CDoseList();
        evallist = new CEvalList();
        dosevlist = new CDosevList();

        dtfmt = new SimpleDateFormat(CAppConsts.DateFmtStr);
    }

    public void copyItem(CTestItem aitem) {
        testgroupid = aitem.testgroupid;
        testid = aitem.testid;
        testtitle = aitem.testtitle;
        testdesc = aitem.testdesc;
        testnote = aitem.testnote;
        testreqid = aitem.testreqid;
        casesource = aitem.casesource;
        lastname = aitem.lastname;
        firstname = aitem.firstname;
        ageyears = aitem.ageyears;
        agemonths = aitem.agemonths;
        ageweeks = aitem.ageweeks;
        agedays = aitem.agedays;
        agemethod = aitem.agemethod;
        birthdate.setTime(aitem.birthdate.getTime());
        gendercd = aitem.gendercd;
        createby = aitem.createby;
        createdate.setTime(aitem.createdate.getTime());
        moddate.setTime(aitem.moddate.getTime());
        basedate.setTime(aitem.basedate.getTime());
        testresult = aitem.testresult;
        resultnotes = aitem.resultnotes;
    }

    public String makeKey() {
        String retstr = testgroupid + "|" + testid;
        return (retstr);
    }

    public String getYmdStr(Date adate) {
        SimpleDateFormat ymdfmt = new SimpleDateFormat(CAppConsts.DateFmtYmd);
        return (ymdfmt.format(adate));
    }

    public String getBirthDateStr() {
        if (birthdate == null || birthdate.getTime() == 0) {
            return ("");
        }
        return (dtfmt.format(birthdate));
    }

    public void setBirthDate(String aval) throws Exception {
        birthdate = dtfmt.parse(aval);
    }

    public String getFC1RunDtStr() {
        if (fc1rundt == null) {
            return ("");
        }
        return (dtfmt.format(fc1rundt));
    }

    public void setFC1RunDt(String aval) throws Exception {
        fc1rundt = dtfmt.parse(aval);
    }

    public String getFC2RunDtStr() {
        if (fc2rundt == null) {
            return ("");
        }
        return (dtfmt.format(fc2rundt));
    }

    public void setFC2RunDt(String aval) throws Exception {
        fc2rundt = dtfmt.parse(aval);
    }

    public String getFC1ResultStr() {
        if (fc1result == null) {
            return ("");
        }
        return (fc1result + " (" + getFC1RunDtStr() + ")");
    }

    public String getFC2ResultStr() {
        if (fc2result == null) {
            return ("");
        }
        return (fc2result + " (" + getFC2RunDtStr() + ")");
    }

    public String getCreateDateStr() {
        if (createdate == null) {
            return (getModDateStr());
        }
        return (dtfmt.format(createdate));
    }

    public void setCreateDate(String aval) throws Exception {
        if (aval.length() != 0) {
            createdate = dtfmt.parse(aval);
        } else {
            createdate = moddate;
        }
    }

    public String getModDateStr() {
        if (moddate.getTime() == 0) {
            return ("");
        }
        return (dtfmt.format(moddate));
    }

    public void setModDate() {
        moddate = new Date();
    }

    public String getBaseDateStr() {
        if (basedate.getTime() == 0) {
            return ("");
        }
        return (dtfmt.format(basedate));
    }

    public void setBaseDate(String aval) throws Exception {
        basedate = dtfmt.parse(aval);
    }

    /*   public void updateBaseDate(String aoldstr, String anewstr) throws Exception
     {
     Date olddate = dtfmt.parse(aoldstr);
     Date newdate = dtfmt.parse(anewstr);
     long datdif = newdate.getTime() - olddate.getTime();
     birthdate.setTime(birthdate.getTime() + datdif);

     shotlist.modifyDates(birthdate);
     nonadmlist.modifyDates(datdif);
     edoselist.modifyDates(datdif);
     }
     */
    public void dbReadItem(Connection aconn, String agroup, String atest) {
        try {
            testgroupid = agroup;
            testid = atest;
            String qstr = "Select TestTitle,TestDesc,TestNotes,BugReqID,CaseSource,"
                    + "LastName,FirstName,AgeYrs,AgeMos,AgeWks,AgeDays,BirthDate,AgeEntryMethod,GenderCd,CreateBy,CreationDate,ModDate,BaseDate,TestResult,ResultNotes"
                    + " From TestCaseTbl"
                    + " Where TestGroupId='" + agroup + "' and TestId='" + atest + "'";
            Statement qstmt = aconn.createStatement();
            ResultSet rset = qstmt.executeQuery(qstr);

            if (rset.next()) {
                testtitle = rset.getString(1);
                testdesc = rset.getString(2);
                testnote = rset.getString(3);
                testreqid = rset.getString(4)==null?"":rset.getString(4); 
                casesource = rset.getString(5)==null?"":rset.getString(5);
                lastname = rset.getString(6);
                firstname = rset.getString(7);
                ageyears = rset.getInt(8);
                agemonths = rset.getInt(9);
                ageweeks = rset.getInt(10);
                agedays = rset.getInt(11);
                birthdate = rset.getDate(12);
                agemethod = rset.getString(13)==null?"":rset.getString(13);
                gendercd = rset.getString(14);
                createby = rset.getString(15);
                createdate = rset.getDate(16);
                moddate = rset.getDate(17);
                basedate = rset.getDate(18);
                testresult = rset.getString(19);
                resultnotes = rset.getString(20);
            }
            rset.close();
            qstmt.close();

        } catch (Exception ex) {
            CLogError.logError(CAppConsts.ErrorFile, false, "CTestItem.dbReadItem cannot read item. ", ex);
        }
    }

    public void dbDeleteItem(Connection aconn) {
        try {
            String qstr = "Delete From TestCaseTbl Where TestGroupId='" + testgroupid + "' and TestId='" + testid + "'";
            Statement qstmt = aconn.createStatement();
            qstmt.executeUpdate(qstr);
            qstmt.close();
        } catch (Exception ex) {
            CLogError.logError(CAppConsts.ErrorFile, false, "CTestItem.dbDeleteItem cannot delete item. ", ex);
        }
    }

    public void dbWriteItem(Connection aconn) {
     String qstr="";
     String dbg="";
     try {  if (createdate==null) createdate=new Date();
            dbDeleteItem(aconn);
            qstr = "Insert into TestCaseTbl (TestGroupId,TestId,TestTitle,TestDesc,TestNotes,BugReqID,CaseSource,"
                    + "LastName,FirstName,AgeYrs,AgeMos,AgeWks,AgeDays,BirthDate,AgeEntryMethod,GenderCd,CreateBy,CreationDate,ModDate,BaseDate,TestResult,ResultNotes) Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement stmt = aconn.prepareStatement(qstr);

            setModDate();
dbg=" (" +  testgroupid + "," + testid + "," + testtitle + "," + testdesc + "," + testnote + "," +testreqid + "," 
        + casesource + "," + lastname + "," + firstname + "," + ageyears+ "," +agemonths+ "," +ageweeks+ "," +agedays+ "," +birthdate+ "," +agemethod+ ","
        + gendercd + "," +createby+ "," +createdate+ "," +moddate+ "," +basedate+ "," +testresult + "," +resultnotes+ "," +")";
            stmt.setString(1, testgroupid);
            stmt.setString(2, testid);
            stmt.setString(3, testtitle);
            stmt.setString(4, testdesc);
            stmt.setString(5, testnote);
            stmt.setString(6, testreqid);
            stmt.setString(7, casesource);
            stmt.setString(8, lastname);
            stmt.setString(9, firstname);
            stmt.setInt(10, ageyears);
            stmt.setInt(11, agemonths);
            stmt.setInt(12, ageweeks);
            stmt.setInt(13, agedays);
            stmt.setDate(14, new java.sql.Date(birthdate.getTime()));
            stmt.setString(15, agemethod);
            stmt.setString(16, gendercd);
            stmt.setString(17, createby);
            stmt.setDate(18, new java.sql.Date(createdate.getTime()));
            stmt.setDate(19, new java.sql.Date(moddate.getTime()));
            stmt.setDate(20, new java.sql.Date(basedate.getTime()));
            stmt.setString(21, testresult);
            stmt.setString(22, resultnotes);
            
            stmt.executeUpdate();

            stmt.close();
        } catch (Exception ex) {
            System.out.println(dbg);
            CLogError.logError(CAppConsts.ErrorFile, false, qstr + dbg + "[] CTestItem.dbWriteItem cannot write item. ", ex);
        }
    }

    public void dbDeleteResult1(Connection aconn) {
        try {
            String qstr = "Delete From TestResultTbl Where TestGroupId='" + testgroupid + "' and TestId='" + testid + "' and ForecasterId='" + fc1 + "'";
            Statement qstmt = aconn.createStatement();
            qstmt.executeUpdate(qstr);
            qstmt.close();
        } catch (Exception ex) {
            CLogError.logError(CAppConsts.ErrorFile, false, "CTestItem.dbDeleteResult1 cannot delete item. ", ex);
        }
    }

    public void dbWriteResult1(Connection aconn) {
        try {
            dbDeleteResult1(aconn);
            String qstr = "Insert into TestResultTbl (TestGroupId,TestId,ForecasterId,LastRun,"
                    + "TestResult,ResultNotes) Values (?,?,?,?,?,?)";
            PreparedStatement stmt = aconn.prepareStatement(qstr);

            setModDate();

            stmt.setString(1, testgroupid);
            stmt.setString(2, testid);
            stmt.setString(3, fc1);
            stmt.setDate(4, (fc1rundt == null) ? null : new java.sql.Date(fc1rundt.getTime()));
            stmt.setString(5, fc1result);
            stmt.setString(6, fc1resnotes);
            stmt.executeUpdate();

            stmt.close();
        } catch (Exception ex) {
            CLogError.logError(CAppConsts.ErrorFile, false, "CTestItem.dbWriteResult1 cannot write item. ", ex);
        }
    }

    public void dbDeleteResult2(Connection aconn) {
        try {
            String qstr = "Delete From TestResultTbl Where TestGroupId='" + testgroupid + "' and TestId='" + testid + "' and ForecasterId='" + fc2 + "'";
            Statement qstmt = aconn.createStatement();
            qstmt.executeUpdate(qstr);
            qstmt.close();
        } catch (Exception ex) {
            CLogError.logError(CAppConsts.ErrorFile, false, "CTestItem.dbDeleteResult2 cannot delete item. ", ex);
        }
    }

    public void dbWriteResult2(Connection aconn) {
        try {
            dbDeleteResult2(aconn);
            String qstr = "Insert into TestResultTbl (TestGroupId,TestId,ForecasterId,LastRun,"
                    + "TestResult,ResultNotes) Values (?,?,?,?,?,?)";
            PreparedStatement stmt = aconn.prepareStatement(qstr);

            setModDate();

            stmt.setString(1, testgroupid);
            stmt.setString(2, testid);
            stmt.setString(3, fc2);
            stmt.setDate(4, (fc2rundt == null) ? null : new java.sql.Date(fc2rundt.getTime()));
            stmt.setString(5, fc2result);
            stmt.setString(6, fc2resnotes);
            stmt.executeUpdate();

            stmt.close();
        } catch (Exception ex) {
            CLogError.logError(CAppConsts.ErrorFile, false, "CTestItem.dbWriteResult2 cannot write item. ", ex);
        }
    }

    public void dbReadDetail(Connection aconn) {
        shotlist.dbReadList(aconn, testgroupid, testid);
        nonadmlist.dbReadList(aconn, testgroupid, testid);
        ereslist.dbReadList(aconn, testgroupid, testid);
        edoselist.dbReadList(aconn, testgroupid, testid);
        evallist.dbReadList(aconn, testgroupid, testid);
        dosevlist.dbReadList(aconn, testgroupid, testid);
    }

    public void dbWriteDetail(Connection aconn) {
        shotlist.dbWriteList(aconn, testgroupid, testid);
        nonadmlist.dbWriteList(aconn, testgroupid, testid);
        ereslist.dbWriteList(aconn, testgroupid, testid);
        edoselist.dbWriteList(aconn, testgroupid, testid);
        evallist.dbWriteList(aconn, testgroupid, testid);
        dosevlist.dbWriteList(aconn, testgroupid, testid);
    }

    public String getStatusStyle(String astatus) {
        if (CAppConsts.StatusPass.equals(astatus)) {
            return (CAppConsts.StylePass);
        }
        if (CAppConsts.StatusFail.equals(astatus)) {
            return (CAppConsts.StyleFail);
        }
        return (CAppConsts.StyleNone);
    }

    public void setTestStatus() {
        testresult = edoselist.getTestStatus();

    }

    public void setLastRunFC1() {
        fc1result = edoselist.getTestStatus();
        fc1rundt = new Date();
    }

    public void setLastRunFC2() {
        fc2result = CAppConsts.StatusDone;
        fc2rundt = new Date();
    }

    public static String showStatusHead(String forecasterA, String forecasterB) {
        String retstr = "<tr>\n";
        retstr = retstr + "<th class='result'>Test Case</th>\n";
        retstr = retstr + "<th colspan='3' class='result'>" + forecasterA + "</th>\n";
        retstr = retstr + "<th colspan='3' class='result'>" + forecasterB + "</th>\n";
        retstr = retstr + "</tr>\n";

        return (retstr);
    }

    public String showStatus() {
        String retstr = "<tr>\n";
        String btnid = "Edit" + testid;
        retstr = retstr + "<td class='result'><a HREF='javascript:DoSubmit(\"StatusForm\",\"" + btnid + "\")'>" + testtitle + "</a></td>\n";
        String stylestr1 = getStatusStyle(fc1result);

        retstr = retstr + "<td class='" + stylestr1 + "'>" + getFC1ResultStr() + "</td>\n";
        btnid = "FC1Detail" + testid;
        retstr = retstr + "<td class='result'><a HREF='javascript:DoSubmit(\"StatusForm\",\"" + btnid + "\")'>View</a></td>\n";
        btnid = "FC1Execute" + testid;
        retstr = retstr + "<td class='result'><button onclick='DoSubmit(\"StatusForm\",\"" + btnid + "\")'>Run Case</button></td>\n";
//second forecaster
        if (!fc2.equals(CAppConsts.TagNoValue)) {
            String stylestr2 = getStatusStyle(fc2result);
            retstr = retstr + "<td class='" + stylestr2 + "'>" + getFC2ResultStr() + "</td>\n";
            btnid = "FC2Detail" + testid;
            retstr = retstr + "<td class='result'><a HREF='javascript:DoSubmit(\"StatusForm\",\"" + btnid + "\")'>View</a></td>\n";
            btnid = "FC2Execute" + testid;
            retstr = retstr + "<td class='result'><button onclick='DoSubmit(\"StatusForm\",\"" + btnid + "\")'>Run Case</button></td>\n";
        } else {
            retstr = retstr + "<td class='result'></td><td class='result'></td><td class='result'></td>\n";
        }
        retstr = retstr + "</tr>\n";
        return (retstr);
    }

    public String showEdit(Connection aconn) {
        CCodeDesc genders = new CCodeDesc(aconn, "GenderTbl", "GenderCd", "GenderNm", "GenderSrt");
        CCodeDesc testgroups = new CCodeDesc(aconn, "TestGroupTbl", "TestGroupId", "TestGroupNm", "TestGroupSrt");
        CCodeDesc agecalcmethods = new CCodeDesc(aconn, "AgeTypeTbl", "AgeTypeCd", "AgeTypeTxt", "AgeTypeCd");

        String retstr = " <table class=\"result\" > <table  width=\"100%\">";
        retstr = retstr + "<tr> <td colspan=\"7\"> <table class=\"result\">";
        retstr = retstr + "<td class=\"title\" >Test Group:</td> <td class=\"titleinfo\">" + testgroups.getDescByCode(testgroupid) + "</td>";
        retstr = retstr + "<td class=\"title\" >Test Case ID:</td> <td class=\"titleinfo\">" + testid + "</td>";
        retstr = retstr + "<td class=\"titlelight\" >Created on:</td> <td class=\"titleinfo\">" + getCreateDateStr() + "</td>";
        retstr = retstr + "<td class=\"titlelight\" >Last Update:</td> <td class=\"titleinfo\">" + getModDateStr() + "</td>";
        retstr = retstr + "</table></td></tr>";

        retstr = retstr + "<tr>";
        retstr = retstr + "<td class=\"fldlabel\">Case Name:</td><td class=\"field\"><input type=\"text\" name=\"TestTitle\" id=\"TestTitle\" value=\"" + testtitle + "\"> </td>";
        retstr = retstr + "<td colspan=\"2\" class=\"field\"></td>";
        retstr = retstr + "<td class=\"fldlabel\">ReqID/BugID:</td><td class=\"field\"><input type=\"text\" name=\"TestReqID\" id=\"TestReqID\" value=\"" + testreqid + "\"> </td>";
        retstr = retstr + "</tr>";
        retstr = retstr + "<tr>";
        retstr = retstr + "<td class=\"fldlabel\">Case Date:</td><td class=\"field\"> <input type=\"text\" name=\"BaseDate\" onchange=\"javascript:setCalcDob()\" id=\"BaseDate\" value=\"" + getBaseDateStr() + "\"> </td>";
        retstr = retstr + "<td colspan=\"2\" class=\"fldlabelleft\">Goals/Notes</td>";
        retstr = retstr + "<td class=\"fldlabel\">Author:</td><td class=\"field\"><input type=\"text\" name=\"CreateBy\" id=\"CreateBy\" value=\"" + createby + "\" </td>";
        retstr = retstr + "<td class=\"field\"> </td>";
        retstr = retstr + "</tr>";
        retstr = retstr + "<tr>";
        retstr = retstr + "<td class=\"fldlabel\">Description:</td>";
        retstr = retstr + "<td rowspan=\"3\" class=\"field\"><textarea name=\"TestDesc\" id=\"TestDesc\" maxlength=\"80\" cols=\"30\" rows=\"3\" value=\"\">" + testdesc + "</textarea></td>";
        retstr = retstr + "<td colspan=\"2\" rowspan=\"3\" class=\"field\"><textarea name=\"TestNote\" id=\"TestNote\"  maxlength=\"80\" cols=\"30\" rows=\"3\" value=\"\">" + testnote + "</textarea> </td>";
        retstr = retstr + "<td class=\"fldlabel\">Source:</td><td rowspan=\"2\" class=\"field\"><textarea name=\"CaseSource\" id=\"CaseSource\" maxlength=\"80\" cols=\"30\" rows=\"2\" value=\"\">" + casesource + "</textarea> </td>";
        retstr = retstr + "</tr>";
        retstr = retstr + "<tr>";
        retstr = retstr + "<td class=\"field\"> </td>";
        retstr = retstr + "<td class=\"field\"> </td>";
        retstr = retstr + "</tr>";
        retstr = retstr + "<tr>";
        retstr = retstr + "</tr>";
        retstr = retstr + "<tr> <td class=\"fldlabel\">Entry Method:</td>";

        retstr = retstr + "<td class=\"field\"> <select name=\"AgeMethod\" id=\"AgeMethod\" onchange=\"javascript:setAgeCalcMethod(this.value);\">\n";
//      retstr = retstr + " <option value='"+CAppConsts.TagNoValue+"'></option>\n";
        retstr = retstr + agecalcmethods.makeOptions(agemethod);
        retstr = retstr + "</select></td>\n";

        retstr = retstr + "<td colspan=\"4\"></td> </tr>";
        retstr = retstr + "<tr><td class=\"fldlabel\">Current Age For Assessment:</td>";

        retstr = retstr + "<td colspan=\"3\">";
        retstr = retstr + "<table class='factors' >\n";
        retstr = retstr + "<tr>\n";
        retstr = retstr + "<th class='factors' scope='col'><label for='AgeYrs'>Yrs</label></th>\n";
        retstr = retstr + "<th class='factors' scope='col'><label for='AgeMos'>Mos</label></th>\n";
        retstr = retstr + "<th class='factors' scope='col'><label for='AgeWks'>Wks</label></th>\n";
        retstr = retstr + "<th class='factors' scope='col'><label for='AgeDays'>Days</label></th>\n";
        retstr = retstr + "<th class='factors' scope='col'><label for='BirthDate'>DOB</label></th>\n";
        retstr = retstr + "</tr>\n";
        retstr = retstr + "<tr>\n";
        retstr = retstr + "<td class='edits'><input type='text' onkeypress=\"return numbersonly(event)\" onchange=\"javascript:setCalcDob()\" name='AgeYrs' id='AgeYrs'"
                + " size=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value='" + ageyears + "'></td>\n";
        retstr = retstr + "<td class='edits'><input type='text' onkeypress=\"return numbersonly(event)\" onchange=\"javascript:setCalcDob()\" name='AgeMos' id='AgeMos'"
                + " size=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value='" + agemonths + "'></td>\n";
        retstr = retstr + "<td class='edits'><input type='text' onkeypress=\"return numbersonly(event)\" onchange=\"javascript:setCalcDob()\" name='AgeWks' id='AgeWks'"
                + " size=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value='" + ageweeks + "'></td>\n";
        retstr = retstr + "<td class='edits'><input type='text' onkeypress=\"return numbersonly(event)\" onchange=\"javascript:setCalcDob()\"  name='AgeDays' id='AgeDays'"
                + " size=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value='" + agedays + "'></td>\n";
        retstr = retstr + "<td class=\"edits\"><input type='text' onkeypress=\"return datecharsonly(event)\"   onchange=\"javascript:setCalcAge('BirthDate','AgeYrs','AgeMos','AgeWks','AgeDays')\"  name='BirthDate' id='BirthDate'"
                + " size=" + Integer.toString(CAppConsts.MaxLenDate) + " maxlength=" + Integer.toString(CAppConsts.MaxLenDate) + " value='" + getBirthDateStr() + "'></td>\n";
        retstr = retstr + "</tr>\n";

        retstr = retstr + "</table>";
        retstr = retstr + "<td colspan=\"3\">";
        retstr = retstr + "<table class='factors' >\n";
        retstr = retstr + "<tr>\n";
        retstr = retstr + "<th class='factors' scope='col'><label for='First'>First Name</label></th>\n";
        retstr = retstr + "<th class='factors' scope='col'><label for='Last'>Last Name</label></th></tr>\n";
        retstr = retstr + "</tr>\n";
        retstr = retstr + "<tr>\n";
        retstr = retstr + "<td class='edits'><input type=\"text\" name=\"FirstName\" id=\"FirstName\" value=\"" + firstname + "\"> </td>";
        retstr = retstr + "<td class='edits'><input type=\"text\" name=\"LastName\" id=\"LastName\" value=\"" + lastname + "\"> </td>";

        retstr = retstr + "</tr>\n";
        retstr = retstr + "</table>";

        retstr = retstr + "<tr><td class=\"fldlabel\">Gender:</td>";
        retstr = retstr + "<td class=\"field\"><select name='Gender' id='Gender' size=1>\n"
                + genders.makeOptions(gendercd) + "</select></td>\n";
//        retstr = retstr + "<td class=\"fldlabel\">First Name:</td><td class=\"field\"><input type=\"text\" name=\"FirstName\" id=\"FirstName\" value=\"" + firstname + "\"> </td>";
//        retstr = retstr + "<td class=\"fldlabel\">Last Name:</td><td class=\"field\"><input type=\"text\" name=\"LastName\" id=\"LastName\" value=\"" + lastname + "\"> </td>";
        retstr = retstr + "<td colspan='5'></td></tr>\n";

        retstr = retstr + "</table></table><br>";

        /*    String retstr = "<dt class='details'>Test Case ID</dt>\n";
         retstr = retstr + "<dd class='details'>" + testid + "</dd>\n";

         retstr = retstr + "<dt class='details'><label for='TestTitle'>Test Case Name</label></dt>\n";
         retstr = retstr + "<dd class='details'><input type='text' name='TestTitle' id='TestTitle'" +
         " maxlength=" + Integer.toString(CAppConsts.MaxLenTitle) + " value='" + testtitle + "'></dd>\n";
         retstr = retstr + "<dt class='details'><label for='CreateBy'>Author</label></dt>\n";
         retstr = retstr + "<dd class='details'><input type='text' name='CreateBy' id='CreateBy'" +
         " maxlength=" + Integer.toString(CAppConsts.MaxLenName) + " value='" + createby + "'></dd>\n";
         retstr = retstr + "<dt class='details'>Modified Date</dt><dd class='details'>" + getModDateStr() + "</dd>\n";
         retstr = retstr + "<dt class='details'><label for='TestDesc'>Case Description</label></dt>\n";
         retstr = retstr + "<dd class='details'><textarea name='TestDesc' id='TestDesc' cols=80 rows=3" +
         " onkeyup='javascript:EnforceMaxLen(this," + Integer.toString(CAppConsts.MaxLenNote) + ")'" +
         ">" + testdesc + "</textarea></dd>\n";
         retstr = retstr + "<dt class='details'><label for='TestNote'>Goals/Notes</label></dt>\n";
         retstr = retstr + "<dd class='details'><textarea name='TestNote' id='TestNote' cols=80 rows=3" +
         " onkeyup='javascript:EnforceMaxLen(this," + Integer.toString(CAppConsts.MaxLenNote) + ")'" +
         ">" + testnote + "</textarea></dd>\n";
         retstr = retstr + "<dt class='details'><label for='BaseDate'>Case Date</label></dt>\n";
         retstr = retstr + "<dd class='details'><input type='text' name='BaseDate' id='BaseDate'" +
         " maxlength=" + Integer.toString(CAppConsts.MaxLenDate) + " value='" + getBaseDateStr() + "'>" +
         "<input type='hidden' name='HideDate' id='HideDate' value='" + getBaseDateStr() + "'>" +
         " <a OnMouseDown='javascript:SwapBtn(\"Scale\",\"ScaleDn\")' " +
         "OnMouseUp='javascript:SwapBtn(\"Scale\",\"ScaleUp\")' " +
         "HREF='javascript:DoSubmit(\"EditForm\",\"Scale\")'>" +
         "<img class='btnimg' id='Scale' name='Scale' " +
         "alt='Edit button' src='images/ScaleUp.gif'></a>" +
         "</dd>\n";
         */

        retstr = retstr + shotlist.showEdit(aconn);
        retstr = retstr + nonadmlist.showEdit(aconn);
        retstr = retstr + edoselist.showEdit(aconn);
        retstr = retstr + evallist.showEdit(aconn);
        retstr = retstr + dosevlist.showEdit(aconn);
        retstr = retstr + ereslist.showEdit(aconn);

        retstr = retstr + "<dt class='details'><label for='ResNotes'>Result Notes</label></dt>\n";
        retstr = retstr + "<dd class='details'><textarea name='ResNotes' id='ResNotes' cols=80 rows=3"
                + " onkeyup='javascript:EnforceMaxLen(this," + Integer.toString(CAppConsts.MaxLenNote) + ")'"
                + ">" + resultnotes + "</textarea></dd>\n";

        return (retstr);
    }

    public void updateItem(HttpServletRequest arequest) throws Exception {
        String group = arequest.getParameter("TestGroup");
        if (group != null) {
            testgroupid = group;
        }

        testtitle = CParser.truncStr(arequest.getParameter("TestTitle"), CAppConsts.MaxLenTitle);
        testdesc = CParser.truncStr(arequest.getParameter("TestDesc"), CAppConsts.MaxLenNote);
        testnote = CParser.truncStr(arequest.getParameter("TestNote"), CAppConsts.MaxLenNote);
        testreqid = CParser.truncStr(arequest.getParameter("TestReqID"), CAppConsts.MaxLenTitle);
        casesource = CParser.truncStr(arequest.getParameter("CaseSource"), CAppConsts.MaxLenNote);
        lastname = CParser.truncStr(arequest.getParameter("LastName"), CAppConsts.MaxLenName);
        firstname = CParser.truncStr(arequest.getParameter("FirstName"), CAppConsts.MaxLenName);
        String datstr = CParser.truncStr(arequest.getParameter("BaseDate"), CAppConsts.MaxLenDate);
        setBaseDate(datstr);
        gendercd = arequest.getParameter("Gender");
        datstr = CParser.truncStr(arequest.getParameter("BirthDate"), CAppConsts.MaxLenDate);
        setBirthDate(datstr);
        ageyears = getIntVal(arequest.getParameter("AgeYrs"));
        agemonths = getIntVal(arequest.getParameter("AgeMos"));
        ageweeks = getIntVal(arequest.getParameter("AgeWks"));
        agedays = getIntVal(arequest.getParameter("AgeDays"));
        agemethod = arequest.getParameter("AgeMethod");

        moddate = new Date(); //saving now   
        createby = CParser.truncStr(arequest.getParameter("CreateBy"), CAppConsts.MaxLenName);
        datstr = CParser.truncStr(arequest.getParameter("CreateDate"), CAppConsts.MaxLenDate);
        setCreateDate(datstr);
        resultnotes = CParser.truncStr(arequest.getParameter("ResNotes"), CAppConsts.MaxLenNote);

        shotlist.updateItem(arequest, this.birthdate);
        nonadmlist.updateItem(arequest);
        ereslist.updateItem(arequest);
        edoselist.updateItem(arequest);
    }

    public String showDisplay(Connection aconn, String viewfcres) {
        CCodeDesc genders = new CCodeDesc(aconn, "GenderTbl", "GenderCd", "GenderNm", "GenderSrt");
        CCodeDesc testgroups = new CCodeDesc(aconn, "TestGroupTbl", "TestGroupId", "TestGroupNm", "TestGroupSrt");
       // CCodeDesc agecalcmethods = new CCodeDesc(aconn, "AgeTypeTbl", "AgeTypeCd", "AgeTypeTxt", "AgeTypeCd");

        String retstr = " <table class=\"result\" > <table  width=\"100%\">";
        retstr = retstr + "<tr> <td colspan=\"7\"> <table class=\"result\">";
        retstr = retstr + "<td class=\"title\" >Test Group:</td> <td class=\"titleinfo\">" + testgroups.getDescByCode(testgroupid) + "</td>";
        retstr = retstr + "<td class=\"titlelight\" >Test Case ID:</td> <td class=\"titleinfo\">" + testid + "</td>";
        retstr = retstr + "<td class=\"titlelight\" >Created on:</td> <td class=\"titleinfo\">" + getCreateDateStr() + "</td>";
        retstr = retstr + "<td class=\"titlelight\" >Last Update:</td> <td class=\"titleinfo\">" + getModDateStr() + "</td>";
        retstr = retstr + "</table></td></tr>";

        retstr = retstr + "<tr>";
        retstr = retstr + "<td class=\"fldlabel\">Case Name:</td><td class=\"field\">"+ testtitle + " </td>";
        retstr = retstr + "<td colspan=\"2\" class=\"field\"></td>";
        retstr = retstr + "<td class=\"fldlabel\">ReqID/BugID:</td><td class=\"field\">"+ testreqid + " </td>";
        retstr = retstr + "</tr>";
        retstr = retstr + "<tr>";
        retstr = retstr + "<td class=\"fldlabel\">Case Date:</td><td class=\"field\">" + getBaseDateStr() + "</td>";
        retstr = retstr + "<td colspan=\"2\" class=\"fldlabelleft\">Goals/Notes</td>";
        retstr = retstr + "<td class=\"fldlabel\">Author:</td><td class=\"field\">" + createby + "</td>";
        retstr = retstr + "<td class=\"field\"> </td>";
        retstr = retstr + "</tr>";
        retstr = retstr + "<tr>";
        retstr = retstr + "<td class=\"fldlabel\">Description:</td>";
        retstr = retstr + "<td rowspan=\"3\" class=\"field\"><textarea readonly='yes' maxlength=\"80\" cols=\"30\" rows=\"3\" value=\"\">" + testdesc + "</textarea></td>";
        retstr = retstr + "<td colspan=\"2\" rowspan=\"3\" class=\"field\"><textarea readonly='yes' maxlength=\"80\" cols=\"30\" rows=\"3\" value=\"\">" + testnote + "</textarea> </td>";
        retstr = retstr + "<td class=\"fldlabel\">Source:</td><td rowspan=\"2\" class=\"field\"><textarea readonly='yes' maxlength=\"80\" cols=\"30\" rows=\"2\" value=\"\">" + casesource + "</textarea> </td>";
        retstr = retstr + "</tr>";
        retstr = retstr + "<tr>";
        retstr = retstr + "<td class=\"field\"> </td>";
        retstr = retstr + "<td class=\"field\"> </td>";
        retstr = retstr + "</tr>";
        retstr = retstr + "<tr>";
        retstr = retstr + "</tr>";
//        retstr = retstr + "<tr> <td class=\"fldlabel\">Entry Method:</td>";
//        retstr = retstr + "<td class=\"field\">" + agecalcmethods.getDescByCode(agemethod) + "</td>\n";
//        retstr = retstr + "<td colspan=\"4\"></td> </tr>";
        retstr = retstr + "<tr><td class=\"fldlabel\">Current Age For Assessment:</td>";

        retstr = retstr + "<td colspan=\"3\">";
        retstr = retstr + "<table class='factors' >\n";
        retstr = retstr + "<tr>\n";
        retstr = retstr + "<th class='factors' scope='col'><label for='AgeYrs'>Yrs</label></th>\n";
        retstr = retstr + "<th class='factors' scope='col'><label for='AgeMos'>Mos</label></th>\n";
        retstr = retstr + "<th class='factors' scope='col'><label for='AgeWks'>Wks</label></th>\n";
        retstr = retstr + "<th class='factors' scope='col'><label for='AgeDays'>Days</label></th>\n";
        retstr = retstr + "<th class='factors' scope='col'><label for='BirthDate'>DOB</label></th>\n";
        retstr = retstr + "</tr>\n";
        retstr = retstr + "<tr>\n";
        retstr = retstr + "<td class='edits'>" + ageyears + "</td>\n";
        retstr = retstr + "<td class='edits'>" + agemonths + "</td>\n";
        retstr = retstr + "<td class='edits'>" + ageweeks + "</td>\n";
        retstr = retstr + "<td class='edits'>" + agedays + "</td>\n";
        retstr = retstr + "<td class='edits'>" + getBirthDateStr() + "</td>\n";
        retstr = retstr + "</tr>\n";
        retstr = retstr + "</table>";
        retstr = retstr + "</tr>\n";
        retstr = retstr + "<tr><td class=\"fldlabel\">Gender:</td>";
        retstr = retstr + "<td class=\"field\">" + genders.getDescByCode(gendercd) + "</td>\n";
        retstr = retstr + "<td class=\"fldlabel\">First Name:</td><td class=\"field\">" + firstname + "</td>";
        retstr = retstr + "<td class=\"fldlabel\">Last Name:</td><td class=\"field\">" + lastname + "</td>";
        retstr = retstr + "<td></td></tr>\n";
        retstr = retstr + "</table></table>";

        
        retstr = retstr + shotlist.showDisplay(aconn, this.birthdate);
        retstr = retstr + nonadmlist.showDisplay(aconn);
        retstr = retstr + edoselist.showDisplay(aconn);
        if (viewfcres.equals(fc1)) {  //available for mcir
            retstr = retstr + evallist.showDisplay(aconn);
            retstr = retstr + dosevlist.showDisplay(aconn);
            retstr = retstr + ereslist.showDisplay(aconn);
        } else {         //used to show results from forecaster services
            retstr = retstr + " <table class=\"result\" >";
            retstr = retstr + "<tr>";
            retstr = retstr + "<td colspan='8' class=\"restitleleft\" >&nbsp;&nbsp;Results</td> </tr> </table>";
            if (fc2resnotes != null && fc2resnotes.length() > 0) {
                //retstr = retstr + "<p style=\"word-wrap: break-word;\">" + fc2resnotes + "</p>\n";
                retstr= retstr + "<tr><td class=\"field\"><textarea readonly='yes'  cols=\"40\" rows=\"45\" style=\"width:95%;wrap:hard;\"  value=\"\">" + fc2resnotes + "</textarea> </td>";
                retstr = retstr + "</tr>";
            } else {
                retstr = retstr + "<p style=\"word-wrap: break-word;\"> [none] </p>\n";
            }
            //   retstr.append( "<table class=\"result\" >") ;
            //   retstr.append( "<tr>") ;
            //   retstr.append("<td class='factors' style='word-wrap:break-word'>"  + fc2resnotes  + "</td>\n");
            //   retstr.append( "</tr></table>");
        }


        String rnote = resultnotes;
        if (resultnotes.length() < 1) {
            rnote = "&nbsp;";
        }
        retstr = retstr + "<dt class='details'>Case Result Notes</dt><dd class='details'>" + rnote + "</dd>\n";
        return (retstr);
    }

    public String exportItem() {
        StringBuilder retstr = new StringBuilder(4096);
        retstr.append("<TestCaseItem>\n");

        retstr.append("<TestGroup>" + testgroupid + "</TestGroup>\n");
        retstr.append("<TestTitle>" + testtitle + "</TestTitle>\n");
        retstr.append("<TestDesc>" + testdesc + "</TestDesc>\n");
        retstr.append("<TestNotes>" + testnote + "</TestNotes>\n");
        retstr.append("<CreateBy>" + createby + "</CreateBy>\n");
        retstr.append("<BaseDate>" + dtfmt.format(basedate) + "</BaseDate>\n");
        retstr.append("<LastName>" + lastname + "</LastName>\n");
        retstr.append("<FirstName>" + firstname + "</FirstName>\n");
        retstr.append("<BirthDate>" + dtfmt.format(birthdate) + "</BirthDate>\n");
        retstr.append("<GenderCd>" + gendercd + "</GenderCd>\n");

        retstr.append(shotlist.exportList());
        retstr.append(nonadmlist.exportList());
        retstr.append(ereslist.exportList());
        retstr.append(edoselist.exportList());

        retstr.append("</TestCaseItem>\n");
        return (retstr.toString());
    }

    private int getIntVal(String sval) {
        if (sval == null || sval.length() < 1) {
            return 0;
        } else {
            return (Integer.parseInt(sval));
        }
    }
}