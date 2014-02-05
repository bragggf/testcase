/*
 * CDoseList.java
 *
 * Created on July 17, 2008, 4:10 PM
 */
package testcase;

import manapp.*;
import java.sql.*;
import javax.servlet.http.*;

/**
 * list of expected next dose items
 */
public class CDoseList extends CStringList {

    /**
     * Creates a new instance of CDoseList
     */
    public CDoseList() {
        super(true);
    }

    public void dbReadList(Connection aconn, String agroup, String atest) {
        try {
            String qstr = "Select DoseId,SeriesCd,ResultCd,NextDoseNum,AccelDate,RecomDate,OverdueDate,TestResult,"
                    + "AccAgeYrs,AccAgeMos,AccAgeWks,AccAgeDays,RecAgeYrs,RecAgeMos,RecAgeWks,RecAgeDays,OvrAgeYrs,OvrAgeMos,OvrAgeWks,OvrAgeDays"
                    + " From TExpectDoseTbl"
                    + " Where TestGroupId='" + agroup + "' and TestId='" + atest + "'";
            Statement qstmt = aconn.createStatement();
            ResultSet rset = qstmt.executeQuery(qstr);

            while (rset.next()) {
                CDoseItem myitem = new CDoseItem();
                myitem.doseid = rset.getString(1);
                myitem.seriescd = rset.getString(2);
                myitem.resultcd = rset.getString(3);
                myitem.doseord = rset.getInt(4);
                myitem.acceldate = rset.getDate(5);
                myitem.recomdate = rset.getDate(6);
                myitem.overduedate = rset.getDate(7);
                myitem.testresult = rset.getString(8);
                myitem.accageyears = rset.getInt(9);
                myitem.accagemonths = rset.getInt(10);
                myitem.accageweeks = rset.getInt(11);
                myitem.accagedays = rset.getInt(12);
                myitem.recageyears = rset.getInt(13);
                myitem.recagemonths = rset.getInt(14);
                myitem.recageweeks = rset.getInt(15);
                myitem.recagedays = rset.getInt(16);
                myitem.ovrageyears = rset.getInt(17);
                myitem.ovragemonths = rset.getInt(18);
                myitem.ovrageweeks = rset.getInt(19);
                myitem.ovragedays = rset.getInt(20);

                this.addItem(myitem.doseid, myitem);
            }
            rset.close();
            qstmt.close();

        } catch (Exception ex) {
            CLogError.logError(CAppConsts.ErrorFile, false, "CDoseList.dbReadList cannot read list. ", ex);
        }
    }

    public void dbDeleteList(Connection aconn, String agroup, String atest) {
        try {
            String qstr = "Delete From TExpectDoseTbl Where TestGroupId='" + agroup + "' and TestId='" + atest + "'";
            Statement qstmt = aconn.createStatement();
            qstmt.executeUpdate(qstr);
            qstmt.close();
        } catch (Exception ex) {
            CLogError.logError(CAppConsts.ErrorFile, false, "CDoseList.dbDeleteList cannot delete list. ", ex);
        }
    }

    public void dbWriteList(Connection aconn, String agroup, String atest) {
        try {
            dbDeleteList(aconn, agroup, atest);
            String qstr = "Insert into TExpectDoseTbl (TestGroupId,TestId,DoseId,SeriesCd,ResultCd,"
                    + "NextDoseNum,AccelDate,RecomDate,OverdueDate,TestResult,"
                    + "AccAgeYrs,AccAgeMos,AccAgeWks,AccAgeDays,RecAgeYrs,RecAgeMos,RecAgeWks,RecAgeDays,OvrAgeYrs,OvrAgeMos,OvrAgeWks,OvrAgeDays)"
                    + " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement stmt = aconn.prepareStatement(qstr);

            for (int idx = 0; idx < this.getCount(); idx++) {
                CDoseItem myitem = (CDoseItem) this.getItem(idx);

                stmt.setString(1, agroup);
                stmt.setString(2, atest);
                stmt.setString(3, myitem.doseid);
                stmt.setString(4, myitem.seriescd);
                stmt.setString(5, myitem.resultcd);
                stmt.setInt(6, myitem.doseord);
                stmt.setDate(7, new java.sql.Date(myitem.acceldate.getTime()));
                stmt.setDate(8, new java.sql.Date(myitem.recomdate.getTime()));
                stmt.setDate(9, new java.sql.Date(myitem.overduedate.getTime()));
                stmt.setString(10, myitem.testresult);
                stmt.setInt(11, myitem.accageyears);
                stmt.setInt(12, myitem.accagemonths);
                stmt.setInt(13, myitem.accageweeks);
                stmt.setInt(14, myitem.accagedays);
                stmt.setInt(15, myitem.recageyears);
                stmt.setInt(16, myitem.recagemonths);
                stmt.setInt(17, myitem.recageweeks);
                stmt.setInt(18, myitem.recagedays);
                stmt.setInt(19, myitem.ovrageyears);
                stmt.setInt(20, myitem.ovragemonths);
                stmt.setInt(21, myitem.ovrageweeks);
                stmt.setInt(22, myitem.ovragedays);

                stmt.executeUpdate();
            }

            stmt.close();
        } catch (Exception ex) {
            CLogError.logError(CAppConsts.ErrorFile, false, "CDoseList.dbWriteList cannot write list. ", ex);
        }
    }

    public void modifyDates(long adiff) {
        for (int idx = 0; idx < this.getCount(); idx++) {
            CDoseItem myitem = (CDoseItem) this.getItem(idx);
            myitem.acceldate.setTime(myitem.acceldate.getTime() + adiff);
            myitem.recomdate.setTime(myitem.recomdate.getTime() + adiff);
            myitem.overduedate.setTime(myitem.overduedate.getTime() + adiff);
        }
    }

    public String showEdit(Connection aconn) {
        CCodeDesc series = new CCodeDesc(aconn, "SeriesTbl", "SeriesCd", "SeriesNm", "SeriesCd");
        CCodeDesc results = new CCodeDesc(aconn, "EvalResultTbl", "ResultCd", "ResultNm", "ResultCd");
        String retstr = " <table class=\"midtitle\" >";
        retstr = retstr + "<tr>";
        retstr = retstr + "<td class=\"midtitleleft\" >&nbsp;&nbsp;Expected Next Dose</td> </tr> ";
        retstr = retstr + "<dd class='details'>\n";
        retstr = retstr + "<table class='factors' summary='next dose'>\n";
        retstr = retstr + "<col style='width:20%'><col style='width:15%'><col style='width:5%'>";
        retstr = retstr + "<col style='width:3%'><col style='width:3%'><col style='width:3%'><col style='width:3%'><col style='width:8%'>";
        retstr = retstr + "<col style='width:3%'><col style='width:3%'><col style='width:3%'><col style='width:3%'><col style='width:8%'>";
        retstr = retstr + "<col style='width:3%'><col style='width:3%'><col style='width:3%'><col style='width:3%'><col style='width:8%'>";

        retstr = retstr + "<tr>\n";
        retstr = retstr + " <td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td class='subtitle' style='border-bottom-color: #0066FF;' colspan='5'>Accelerated Schedule</td><td class='subtitle' style='border-bottom-color: #00CC00;' colspan='5'>Recommended Schedule</td><td class='subtitle'  style='border-bottom-color: #FF6600;' colspan='5'>Overdue Schedule</td></tr><tr>";
        retstr = retstr + "<th class='factors' scope='col'>Series</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Status</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Dose</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Yrs</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Mos</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Wks</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Dys</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Acc Date</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Yrs</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Mos</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Wks</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Dys</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Rec Date</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Yrs</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Mos</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Wks</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Dys</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Ovr Date</th></tr>\n";

        int icnt = 0;
        for (int idx = 0; idx < this.getCount(); idx++) {
            icnt++;
            CDoseItem myitem = (CDoseItem) this.getItem(idx);
            String serid = "Series" + myitem.doseid;
            String resid = "Imstat" + myitem.doseid;
            String dosid = "Dose" + myitem.doseid;
            String accid = "Adate" + myitem.doseid;
            String ayrsid = "Ayrs" + myitem.doseid;
            String amosid = "Amos" + myitem.doseid;
            String awksid = "Awks" + myitem.doseid;
            String adysid = "Adys" + myitem.doseid;
            String recid = "Rdate" + myitem.doseid;
            String ryrsid = "Ryrs" + myitem.doseid;
            String rmosid = "Rmos" + myitem.doseid;
            String rwksid = "Rwks" + myitem.doseid;
            String rdysid = "Rdys" + myitem.doseid;
            String ovrid = "Odate" + myitem.doseid;
            String oyrsid = "Oyrs" + myitem.doseid;
            String omosid = "Omos" + myitem.doseid;
            String owksid = "Owks" + myitem.doseid;
            String odysid = "Odys" + myitem.doseid;

            retstr = retstr + "<tr>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + serid + "'>Series " + Integer.toString(icnt) + "</label>"
                    + "<select name='" + serid + "' id='" + serid + "' size=1 style=\"width:100%;\" >\n";
            retstr = retstr + "<option value='" + CAppConsts.TagNoValue + "'>" + CAppConsts.TagNoLabel + "</option>\n";
            retstr = retstr + series.makeOptions(myitem.seriescd);
            retstr = retstr + "</select></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + resid + "'>Result " + Integer.toString(icnt) + "</label>"
                    + "<select name='" + resid + "' id='" + resid + "' size=1 style=\"width:100%;\" >\n";
            retstr = retstr + "<option value='" + CAppConsts.TagNoValue + "'>" + CAppConsts.TagNoLabel + "</option>\n";
            retstr = retstr + results.makeOptions(myitem.resultcd);
            retstr = retstr + "</select></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + dosid + "'>Dose " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + dosid + "' id='" + dosid + "' style=\"width:90%;\" "
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenDoseNum) + " value='" + Integer.toString(myitem.doseord) + "'></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + ayrsid + "'>Age years " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + ayrsid + "' id='" + ayrsid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value='" + Integer.toString(myitem.accageyears) + "'></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + amosid + "'>Age months " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + amosid + "' id='" + amosid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value='" + Integer.toString(myitem.accagemonths) + "'></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + awksid + "'>Age weeks " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + awksid + "' id='" + awksid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value='" + Integer.toString(myitem.accageweeks) + "'></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + adysid + "'>Age days " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + adysid + "' id='" + adysid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value='" + Integer.toString(myitem.accagedays) + "'></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + accid + "'>Accelerated date " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + accid + "' id='" + accid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueAge(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenDate) + " value='" + myitem.getAccelDateStr() + "'></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + ryrsid + "'>Age years " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + ryrsid + "' id='" + ryrsid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value='" + Integer.toString(myitem.recageyears) + "'></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + rmosid + "'>Age months " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + rmosid + "' id='" + rmosid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value='" + Integer.toString(myitem.recagemonths) + "'></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + rwksid + "'>Age weeks " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + rwksid + "' id='" + rwksid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value='" + Integer.toString(myitem.recageweeks) + "'></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + rdysid + "'>Age days " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + rdysid + "' id='" + rdysid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value='" + Integer.toString(myitem.recagedays) + "'></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + recid + "'>Recommended date " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + recid + "' id='" + recid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueAge(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenDate) + " value='" + myitem.getRecomDateStr() + "'></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + oyrsid + "'>Age years " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + oyrsid + "' id='" + oyrsid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value='" + Integer.toString(myitem.ovrageyears) + "'></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + omosid + "'>Age months " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + omosid + "' id='" + omosid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value='" + Integer.toString(myitem.ovragemonths) + "'></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + owksid + "'>Age weeks " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + owksid + "' id='" + owksid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value='" + Integer.toString(myitem.ovrageweeks) + "'></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + odysid + "'>Age days " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + odysid + "' id='" + odysid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value='" + Integer.toString(myitem.ovragedays) + "'></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + ovrid + "'>Overdue date " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + ovrid + "' id='" + ovrid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueAge(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenDate) + " value='" + myitem.getOverdueDateStr() + "'></td>\n";
            retstr = retstr + "</tr>\n";
        }

        //int nslot = Math.max(CAppConsts.NewSlotAntEval, CAppConsts.NumSlotAntEval - getCount());
        int nslot = (this.getCount() == 0) ? 4 : 1;

        for (int idx = 0; idx < nslot; idx++) {
            icnt++;
            String myid = "New" + Integer.toString(idx);
            String serid = "Series" + myid;
            String resid = "Imstat" + myid;
            String dosid = "Dose" + myid;
            String accid = "Adate" + myid;
            String ayrsid = "Ayrs" + myid;
            String amosid = "Amos" + myid;
            String awksid = "Awks" + myid;
            String adysid = "Adys" + myid;
            String recid = "Rdate" + myid;
            String ryrsid = "Ryrs" + myid;
            String rmosid = "Rmos" + myid;
            String rwksid = "Rwks" + myid;
            String rdysid = "Rdys" + myid;
            String ovrid = "Odate" + myid;
            String oyrsid = "Oyrs" + myid;
            String omosid = "Omos" + myid;
            String owksid = "Owks" + myid;
            String odysid = "Odys" + myid;

            retstr = retstr + "<tr>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + serid + "'>Series " + Integer.toString(icnt) + "</label>"
                    + "<select name='" + serid + "' id='" + serid + "' size=1 style=\"width:100%;\" >\n";
            retstr = retstr + "<option value='" + CAppConsts.TagNoValue + "'>" + CAppConsts.TagNoLabel + "</option>\n";
            retstr = retstr + series.makeOptions(CAppConsts.TagNoValue);
            retstr = retstr + "</select></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + resid + "'>Result " + Integer.toString(icnt) + "</label>"
                    + "<select name='" + resid + "' id='" + resid + "' size=1 style=\"width:100%;\" >\n";
            retstr = retstr + "<option value='" + CAppConsts.TagNoValue + "'>" + CAppConsts.TagNoLabel + "</option>\n";
            retstr = retstr + results.makeOptions(CAppConsts.TagNoValue);
            retstr = retstr + "</select></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + dosid + "'>Dose " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + dosid + "' id='" + dosid + "' style=\"width:90%;\" "
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenDoseNum) + " value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + ayrsid + "'>Age years " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + ayrsid + "' id='" + ayrsid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + amosid + "'>Age months " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + amosid + "' id='" + amosid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + awksid + "'>Age weeks " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + awksid + "' id='" + awksid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + adysid + "'>Age days " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + adysid + "' id='" + adysid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + accid + "'>Accelerated date " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + accid + "' id='" + accid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueAge(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenDate) + " value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + ryrsid + "'>Age years " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + ryrsid + "' id='" + ryrsid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + rmosid + "'>Age months " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + rmosid + "' id='" + rmosid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + rwksid + "'>Age weeks " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + rwksid + "' id='" + rwksid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + rdysid + "'>Age days " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + rdysid + "' id='" + rdysid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + recid + "'>Recommended date " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + recid + "' id='" + recid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueAge(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenDate) + " value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + oyrsid + "'>Age years " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + oyrsid + "' id='" + oyrsid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + omosid + "'>Age months " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + omosid + "' id='" + omosid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + owksid + "'>Age weeks " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + owksid + "' id='" + owksid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + odysid + "'>Age days " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + odysid + "' id='" + odysid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueDate(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenAgeNum) + " value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + ovrid + "'>Overdue date " + Integer.toString(icnt) + "</label>"
                    + "<input type='text' name='" + ovrid + "' id='" + ovrid + "' style=\"width:90%;\"  onchange=\"javascript:setCalcDueAge(this.name)\""
                    + " maxlength=" + Integer.toString(CAppConsts.MaxLenDate) + " value=''></td>\n";
            retstr = retstr + "</tr>\n";
        }

        retstr = retstr + "</table></dd><br>\n";
        return (retstr);
    }

    public void updateItem(HttpServletRequest arequest) throws Exception {
        try {
            for (int idx = this.getCount() - 1; idx >= 0; idx--) {
                CDoseItem myitem = (CDoseItem) this.getItem(idx);
                String serid = "Series" + myitem.doseid;
                String resid = "Imstat" + myitem.doseid;
                String dosid = "Dose" + myitem.doseid;
                String accid = "Adate" + myitem.doseid;
                String ayrsid = "Ayrs" + myitem.doseid;
                String amosid = "Amos" + myitem.doseid;
                String awksid = "Awks" + myitem.doseid;
                String adysid = "Adys" + myitem.doseid;
                String recid = "Rdate" + myitem.doseid;
                String ryrsid = "Ryrs" + myitem.doseid;
                String rmosid = "Rmos" + myitem.doseid;
                String rwksid = "Rwks" + myitem.doseid;
                String rdysid = "Rdys" + myitem.doseid;
                String ovrid = "Odate" + myitem.doseid;
                String oyrsid = "Oyrs" + myitem.doseid;
                String omosid = "Omos" + myitem.doseid;
                String owksid = "Owks" + myitem.doseid;
                String odysid = "Odys" + myitem.doseid;


                String serstr = arequest.getParameter(serid);
                if (serstr == null || serstr.equals(CAppConsts.TagNoValue)) {
                    this.delItem(idx);
                    continue;
                }
                String resstr = arequest.getParameter(resid);
                if (resstr == null || resstr.equals(CAppConsts.TagNoValue)) {
                    this.delItem(idx);
                    continue;
                }
                String dosstr = CParser.truncStr(arequest.getParameter(dosid), CAppConsts.MaxLenDoseNum);
                String accstr = CParser.truncStr(arequest.getParameter(accid), CAppConsts.MaxLenDate);
                String recstr = CParser.truncStr(arequest.getParameter(recid), CAppConsts.MaxLenDate);
                String ovrstr = CParser.truncStr(arequest.getParameter(ovrid), CAppConsts.MaxLenDate);

                myitem.seriescd = serstr;
                myitem.resultcd = resstr;
                if (dosstr.length() < 1) {
                    myitem.doseord = 0;
                } else {
                    myitem.doseord = Integer.parseInt(dosstr);
                }
                if (accstr.length() < 1) {
                    myitem.setAccelDate(0);
                } else {
                    myitem.setAccelDate(accstr);
                }
                if (recstr.length() < 1) {
                    myitem.setRecomDate(0);
                } else {
                    myitem.setRecomDate(recstr);
                }
                if (ovrstr.length() < 1) {
                    myitem.setOverdueDate(0);
                } else {
                    myitem.setOverdueDate(ovrstr);
                }

                myitem.accageyears = getIntVal(arequest.getParameter(ayrsid));
                myitem.accagemonths = getIntVal(arequest.getParameter(amosid));
                myitem.accageweeks = getIntVal(arequest.getParameter(awksid));
                myitem.accagedays = getIntVal(arequest.getParameter(adysid));
                myitem.recageyears = getIntVal(arequest.getParameter(ryrsid));
                myitem.recagemonths = getIntVal(arequest.getParameter(rmosid));
                myitem.recageweeks = getIntVal(arequest.getParameter(rwksid));
                myitem.recagedays = getIntVal(arequest.getParameter(rdysid));
                myitem.ovrageyears = getIntVal(arequest.getParameter(oyrsid));
                myitem.ovragemonths = getIntVal(arequest.getParameter(omosid));
                myitem.ovrageweeks = getIntVal(arequest.getParameter(owksid));
                myitem.ovragedays = getIntVal(arequest.getParameter(odysid));

            }

            int nslot = Math.max(CAppConsts.NewSlotAntEval, CAppConsts.NumSlotAntEval - getCount());
            for (int idx = 0; idx < nslot; idx++) {
                CDoseItem myitem = new CDoseItem();
                String myid = "New" + Integer.toString(idx);
                String serid = "Series" + myid;
                String resid = "Imstat" + myid;
                String dosid = "Dose" + myid;
                String accid = "Adate" + myid;
                String ayrsid = "Ayrs" + myid;
                String amosid = "Amos" + myid;
                String awksid = "Awks" + myid;
                String adysid = "Adys" + myid;
                String recid = "Rdate" + myid;
                String ryrsid = "Ryrs" + myid;
                String rmosid = "Rmos" + myid;
                String rwksid = "Rwks" + myid;
                String rdysid = "Rdys" + myid;
                String ovrid = "Odate" + myid;
                String oyrsid = "Oyrs" + myid;
                String omosid = "Omos" + myid;
                String owksid = "Owks" + myid;
                String odysid = "Odys" + myid;


                String serstr = arequest.getParameter(serid);
                if (serstr == null || serstr.equals(CAppConsts.TagNoValue)) {
                    continue;
                }
                String resstr = arequest.getParameter(resid);
                if (resstr == null || resstr.equals(CAppConsts.TagNoValue)) {
                    continue;
                }
                String dosstr = CParser.truncStr(arequest.getParameter(dosid), CAppConsts.MaxLenDoseNum);
                String accstr = CParser.truncStr(arequest.getParameter(accid), CAppConsts.MaxLenDate);
                String recstr = CParser.truncStr(arequest.getParameter(recid), CAppConsts.MaxLenDate);
                String ovrstr = CParser.truncStr(arequest.getParameter(ovrid), CAppConsts.MaxLenDate);

                myitem.doseid = this.makeNewId("dos", 6);
                myitem.seriescd = serstr;
                myitem.resultcd = resstr;
                if (dosstr.length() < 1) {
                    myitem.doseord = 0;
                } else {
                    myitem.doseord = Integer.parseInt(dosstr);
                }
                if (accstr.length() < 1) {
                    myitem.setAccelDate(0);
                } else {
                    myitem.setAccelDate(accstr);
                }
                if (recstr.length() < 1) {
                    myitem.setRecomDate(0);
                } else {
                    myitem.setRecomDate(recstr);
                }
                if (ovrstr.length() < 1) {
                    myitem.setOverdueDate(0);
                } else {
                    myitem.setOverdueDate(ovrstr);
                }
                myitem.accageyears = getIntVal(arequest.getParameter(ayrsid));
                myitem.accagemonths = getIntVal(arequest.getParameter(amosid));
                myitem.accageweeks = getIntVal(arequest.getParameter(awksid));
                myitem.accagedays = getIntVal(arequest.getParameter(adysid));
                myitem.recageyears = getIntVal(arequest.getParameter(ryrsid));
                myitem.recagemonths = getIntVal(arequest.getParameter(rmosid));
                myitem.recageweeks = getIntVal(arequest.getParameter(rwksid));
                myitem.recagedays = getIntVal(arequest.getParameter(rdysid));
                myitem.ovrageyears = getIntVal(arequest.getParameter(oyrsid));
                myitem.ovragemonths = getIntVal(arequest.getParameter(omosid));
                myitem.ovrageweeks = getIntVal(arequest.getParameter(owksid));
                myitem.ovragedays = getIntVal(arequest.getParameter(odysid));

                this.addItem(myitem.doseid, myitem);
            }
        } catch (Exception ex) {
            CLogError.logError(CAppConsts.ErrorFile, false, "CDoseList.updateItem ", ex);
            throw (ex);
        }
    }

    public String showDisplay(Connection aconn) {

        CCodeDesc series = new CCodeDesc(aconn, "SeriesTbl", "SeriesCd", "SeriesNm", "SeriesCd");
        CCodeDesc results = new CCodeDesc(aconn, "EvalResultTbl", "ResultCd", "ResultNm", "ResultCd");
        String retstr = " <table class=\"midtitle\" >";
        retstr = retstr + "<tr>";

        retstr = retstr + "<td class=\"midtitleleft\" >&nbsp;&nbsp;Expected Next Dose</td> </tr> ";
        retstr = retstr + "<dd class='details'>\n";
        retstr = retstr + "<table class='factors' summary='next dose'>\n";
        retstr = retstr + "<col style='width:20%'><col style='width:15%'><col style='width:5%'>";
        retstr = retstr + "<col style='width:3%'><col style='width:3%'><col style='width:3%'><col style='width:3%'><col style='width:8%'>";
        retstr = retstr + "<col style='width:3%'><col style='width:3%'><col style='width:3%'><col style='width:3%'><col style='width:8%'>";
        retstr = retstr + "<col style='width:3%'><col style='width:3%'><col style='width:3%'><col style='width:3%'><col style='width:8%'>";

        retstr = retstr + "<tr>\n";
        retstr = retstr + " <td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td class='subtitle' style='border-bottom-color: #0066FF;' colspan='5'>Accelerated Schedule</td><td class='subtitle' style='border-bottom-color: #00CC00;' colspan='5'>Recommended Schedule</td><td class='subtitle'  style='border-bottom-color: #FF6600;' colspan='5'>Overdue Schedule</td></tr><tr>";
        retstr = retstr + "<th class='factors' scope='col'>Series</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Status</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Dose</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Yrs</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Mos</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Wks</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Dys</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Acc Date</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Yrs</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Mos</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Wks</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Dys</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Rec Date</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Yrs</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Mos</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Wks</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Dys</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Ovr Date</th></tr>\n";

        for (int idx = 0; idx < this.getCount(); idx++) {
            CDoseItem myitem = (CDoseItem) this.getItem(idx);
            retstr = retstr + "<tr>\n";
            retstr = retstr + "<td class='factors'>" + series.getDescByCode(myitem.seriescd) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + results.getDescByCode(myitem.resultcd) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.doseord) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.accageyears) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.accagemonths) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.accageweeks) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.accagedays) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + myitem.getAccelDateStr() + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.recageyears) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.recagemonths) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.recageweeks) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.recagedays) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + myitem.getRecomDateStr() + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.ovrageyears) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.ovragemonths) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.ovrageweeks) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.ovragedays) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + myitem.getOverdueDateStr() + "</td>\n";
            retstr = retstr + "</tr>\n";
        }

        retstr = retstr + "</table></dd><br>\n";


        return (retstr);
    }

    public boolean isSeriesDose(String aseries) {
        for (int idx = 0; idx < this.getCount(); idx++) {
            CDoseItem myitem = (CDoseItem) this.getItem(idx);
            if (myitem.seriescd.equals(aseries)) {
                return (true);
            }
        }
        return (false);
    }

    public void setTestResult(CEvalItem aitem) {
        for (int idx = 0; idx < this.getCount(); idx++) {
            CDoseItem myitem = (CDoseItem) this.getItem(idx);
            if (myitem.seriescd.equals(aitem.seriescd)) {
                myitem.setTestResult(aitem);
                return;
            }
        }
    }

    public void initTestResults() {
        for (int idx = 0; idx < this.getCount(); idx++) {
            CDoseItem myitem = (CDoseItem) this.getItem(idx);
            myitem.testresult = CAppConsts.StatusNone;
        }
    }

    public String getTestStatus() {
        String mystat = CAppConsts.StatusNone;
        for (int idx = 0; idx < this.getCount(); idx++) {
            CDoseItem myitem = (CDoseItem) this.getItem(idx);
            if (CAppConsts.StatusFail.equals(myitem.testresult)) {
                return (CAppConsts.StatusFail);
            } else if (CAppConsts.StatusNone.equals(myitem.testresult)) {
                return (CAppConsts.StatusNone);
            }
            mystat = myitem.testresult;
        }
        return (mystat);
    }

    public String exportList() {
        if (this.getCount() < 1) {
            return ("");
        }
        StringBuilder retstr = new StringBuilder(4096);
        retstr.append("<ExpectDoseList>\n");
        for (int idx = 0; idx < getCount(); idx++) {
            CDoseItem myitem = (CDoseItem) this.getItem(idx);
            retstr.append(myitem.exportItem());
        }
        retstr.append("</ExpectDoseList>\n");
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
