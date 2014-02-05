/*
 * CShotList.java
 *
 * Created on July 16, 2008, 6:51 PM
 */
package testcase;

import manapp.*;

import java.util.Date;
import java.sql.*;
import javax.servlet.http.*;

/**
 * list of shots
 */
public class CShotList extends CStringList {

    /**
     * Creates a new instance of CShotList
     */
    public CShotList() {
        super(true);
    }

    public void dbReadList(Connection aconn, String agroup, String atest) {
        try {
            String qstr = "Select ShotId,ShotDate,VaccineCd,MfrCd,"
                    + "VacAgeYrs,VacAgeMos,VacAgeWks,VacAgeDays,NoteCode"
                    + " From TShotHistTbl"
                    + " Where TestGroupId='" + agroup + "' and TestId='" + atest + "'"
                    + " Order by ShotId";
            Statement qstmt = aconn.createStatement();
            ResultSet rset = qstmt.executeQuery(qstr);

            while (rset.next()) {
                CShotItem myitem = new CShotItem();
                myitem.shotid = rset.getString(1);
                myitem.shotdate = rset.getDate(2);
                myitem.vaccinecd = rset.getString(3);
                myitem.mfrcd = rset.getString(4);
                myitem.vageyears = rset.getInt(5);
                myitem.vagemonths = rset.getInt(6);
                myitem.vageweeks = rset.getInt(7);
                myitem.vagedays = rset.getInt(8);
                myitem.vacnote = rset.getString(9);
                if (myitem.vacnote == null) {
                    myitem.vacnote = CAppConsts.TagNoValue;
                }

                /*          myitem.reftype = rset.getString(5);
                 if (rset.wasNull()) myitem.reftype = CAppConsts.RefTypeAge;
                 myitem.periodtype = rset.getString(6);
                 if (rset.wasNull()) myitem.periodtype = CAppConsts.PeriodDays;
                 myitem.periodamt = rset.getInt(7);
                 if (rset.wasNull()) myitem.periodamt = 0;
                 myitem.periodoff = rset.getInt(8);
                 if (rset.wasNull()) myitem.periodoff = 0;
                 */
                this.addItem(myitem.shotid, myitem);
            }
            rset.close();
            qstmt.close();

        } catch (Exception ex) {
            CLogError.logError(CAppConsts.ErrorFile, false, "CShotList.dbReadList cannot read list. ", ex);
        }
    }

    public void dbDeleteList(Connection aconn, String agroup, String atest) {
        try {
            String qstr = "Delete From TShotHistTbl Where TestGroupId='" + agroup + "' and TestId='" + atest + "'";
            Statement qstmt = aconn.createStatement();
            qstmt.executeUpdate(qstr);
            qstmt.close();
        } catch (Exception ex) {
            CLogError.logError(CAppConsts.ErrorFile, false, "CShotList.dbDeleteList cannot delete list. ", ex);
        }
    }

    public void dbWriteList(Connection aconn, String agroup, String atest) {
        try {
            dbDeleteList(aconn, agroup, atest);
            String qstr = "Insert into TShotHistTbl ("
                    + "TestGroupId,TestId,ShotId,ShotDate,VaccineCd,MfrCd,"
                    + "VacAgeYrs,VacAgeMos,VacAgeWks,VacAgeDays,NoteCode )"
                    + " Values (?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement stmt = aconn.prepareStatement(qstr);

            for (int idx = 0; idx < this.getCount(); idx++) {
                CShotItem myitem = (CShotItem) this.getItem(idx);

                stmt.setString(1, agroup);
                stmt.setString(2, atest);
                stmt.setString(3, myitem.shotid);
                stmt.setDate(4, new java.sql.Date(myitem.shotdate.getTime()));
                stmt.setString(5, myitem.vaccinecd);
                stmt.setString(6, myitem.mfrcd);

                stmt.setInt(7, myitem.vageyears);
                stmt.setInt(8, myitem.vagemonths);
                stmt.setInt(9, myitem.vageweeks);
                stmt.setInt(10, myitem.vagedays);
                stmt.setString(11, myitem.vacnote);

                stmt.executeUpdate();
            }

            stmt.close();
        } catch (Exception ex) {
            CLogError.logError(CAppConsts.ErrorFile, false, "CShotList.dbWriteList cannot write list. ", ex);
        }
    }

    public void modifyDates(Date abirthdt) {
        Date lastdt = new Date(abirthdt.getTime());
        for (int idx = 0; idx < this.getCount(); idx++) {
            CShotItem myitem = (CShotItem) this.getItem(idx);
//         lastdt = myitem.calcShotDate(abirthdt, lastdt);
        }
    }

    public String showEdit(Connection aconn) {
        CCodeDesc vaccines = new CCodeDesc(aconn, "VaccineTbl", "VaccineCd", "VaccineNm", "VaccineCd");
        //display only the short name (code value) for MFR since this field may not be relevant to assessment and can default to UNK
        CCodeDesc mfrs = new CCodeDesc(aconn, "MfrTbl", "MfrCd", "MfrCd", "MfrCd", "VaccineMfrTbl", "VaccineCd");
        CCodeDesc vacnotes = new CCodeDesc(aconn, "NoteTbl", "NoteCd", "NoteTxt", "NoteCd");
        String retstr = " <table class=\"midtitle\" >";
        retstr = retstr + "<tr>";
        retstr = retstr + "<td colspan='8' class=\"midtitleleft\" >&nbsp;&nbsp;Vaccinations</td> </tr> </table>";


        retstr = retstr + "<dd class='details'>"
                + "<input type=hidden name='mfrdatadrop' id='mfrdatadrop' value='" + mfrs.makeDataDrop() + "'>\n";
        retstr = retstr + "<table class='factors' summary='vaccinations'>";
        retstr = retstr + "<tr>\n";
        retstr = retstr + "<th class='factors' scope='col'><label>Vaccine</label></th>";
        retstr = retstr + "<th class='factors' scope='col'><label>Mfr</label></th>";
        retstr = retstr + "<th class='factors' scope='col'><label>Yrs</label></th>";
        retstr = retstr + "<th class='factors' scope='col'><label>Mos</label></th>";
        retstr = retstr + "<th class='factors' scope='col'><label>Wks</label></th>";
        retstr = retstr + "<th class='factors' scope='col'><label>Days</label></th>";
        retstr = retstr + "<th class='factors' scope='col'><label>Date</label></th>";
        retstr = retstr + "<th class='factors' scope='col'><label>Note</label></th>";
        retstr = retstr + "<th class='hidden' scope='col'></th>";
        retstr = retstr + "</tr>";
        int ivac = 0;
        for (int idx = 0; idx < this.getCount(); idx++) {
            ivac++;
            CShotItem myitem = (CShotItem) this.getItem(idx);
            String vacid = "Vaccine" + myitem.shotid;
            String mfrid = "Mfr" + myitem.shotid;
            String vyrsid = "Vyrs" + myitem.shotid;
            String vmosid = "Vmos" + myitem.shotid;
            String vwksid = "Vwks" + myitem.shotid;
            String vdysid = "Vdys" + myitem.shotid;
            String vdtid = "Vdate" + myitem.shotid;
            String vnoteid = "Vnote" + myitem.shotid;

            retstr = retstr + "<tr>\n";
//         retstr = retstr + "<td class='edits'>" +
//                 "<label class='hidden' for='"+refid+"'>Reference for vaccination " + Integer.toString(ivac) + "</label>" +
//                 "<select name='"+refid+"' id='"+refid+"' size=1>\n";
//         retstr = retstr + myitem.makeRefOptions();
//         retstr = retstr + "</select></td>\n";
//         retstr = retstr + "<td class='edits'>" +
//                 "<label class='hidden' for='"+perid+"'>Period Type for vaccination " + Integer.toString(ivac) + "</label>" +
//                 "<select name='"+perid+"' id='"+perid+"' size=1>\n";
//         retstr = retstr + myitem.makePeriodOptions();
//         retstr = retstr + "</select></td>\n";
//         retstr = retstr + "<td class='edits'>" +
//               "<label class='hidden' for='"+amtid+"'>Period Amount for vaccination " + Integer.toString(ivac) + "</label>" +
//               "<input type='text' name='"+amtid+"' id='"+amtid+"' size=4" +
//               " maxlength=5 value='" + Integer.toString(myitem.periodamt) + "'></td>\n";
//         retstr = retstr + "<td class='edits'>" +
//               "<label class='hidden' for='"+offid+"'>Additional Days for vaccination " + Integer.toString(ivac) + "</label>" +
//               "<input type='text' name='"+offid+"' id='"+offid+"' size=4" +
//               " maxlength=5 value='" + Integer.toString(myitem.periodoff) + "'></td>\n";

            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + vacid + "'>Vaccine for vaccination " + Integer.toString(ivac) + "</label>"
                    + "<select name='" + vacid + "' id='" + vacid + "' size=1 onchange='javascript:ChangeVaccine(\"" + vacid + "\",\"" + mfrid + "\")'>\n";
            retstr = retstr + "<option value='" + CAppConsts.TagNoValue + "'>" + CAppConsts.TagNoLabel + "</option>\n";
            retstr = retstr + vaccines.makeOptions(myitem.vaccinecd);
            retstr = retstr + "</select></td>\n";

            retstr = retstr + "<td class='edits'>";
            retstr = retstr + "<label class='hidden' for='" + mfrid + "'>Manufacturer for vaccination " + Integer.toString(ivac) + "</label>"
                    + "<select name='" + mfrid + "' id='" + mfrid + "' size=1>\n";
            retstr = retstr + "<option value='" + CAppConsts.TagNoValue + "'></option>\n";
            retstr = retstr + mfrs.makeOptions(myitem.vaccinecd, myitem.mfrcd);
            retstr = retstr + "</select></td>\n";

            retstr = retstr + "<td class='edits'>"
                    + "<input type='text' name='" + vyrsid + "' id='" + vyrsid + "' size=4 onchange=\"javascript:setCalcVacDate(this.name)\""
                    + " maxlength=5 value='" + Integer.toString(myitem.vageyears) + "'></td>\n";

            retstr = retstr + "<td class='edits'>"
                    + "<input type='text' name='" + vmosid + "' id='" + vmosid + "' size=4 onchange=\"javascript:setCalcVacDate(this.name)\""
                    + " maxlength=5 value='" + Integer.toString(myitem.vagemonths) + "'></td>\n";

            retstr = retstr + "<td class='edits'>"
                    + "<input type='text' name='" + vwksid + "' id='" + vwksid + "' size=4 onchange=\"javascript:setCalcVacDate(this.name)\""
                    + " maxlength=5 value='" + Integer.toString(myitem.vageweeks) + "'></td>\n";

            retstr = retstr + "<td class='edits'>"
                    + "<input type='text' name='" + vdysid + "' id='" + vdysid + "' size=4 onchange=\"javascript:setCalcVacDate(this.name)\""
                    + " maxlength=5 value='" + Integer.toString(myitem.vagedays) + "'></td>\n";

            retstr = retstr + "<td class='factors'><input type='text' name='" + vdtid + "' id='" + vdtid + "' size=10 onchange=\"javascript:setCalcVacAge(this.name)\""
                    + " maxlength=10 value='" + myitem.getShotDateStr() + "'></td>\n";

            retstr = retstr + "<td class='edits'>";
            retstr = retstr + "<label class='hidden' for='" + vnoteid + "'>Notes for vaccination " + Integer.toString(ivac) + "</label>"
                    + "<select name='" + vnoteid + "' id='" + vnoteid + "' size=1>\n";
            retstr = retstr + "<option value='" + CAppConsts.TagNoValue + "'></option>\n";
            retstr = retstr + vacnotes.makeOptions(myitem.vacnote);
            retstr = retstr + "</select></td>\n";

            retstr = retstr + "</tr>\n";
        }

        //     CShotItem myitem = new CShotItem();
        //     int nslot = Math.max(CAppConsts.NewSlotShotHist, CAppConsts.NumSlotShotHist - getCount());
        //if no data entered show 4 rows otherwise just show 1 extra row for new data entry
        int nslot = (this.getCount() == 0) ? 4 : 1;

        for (int idx = 0; idx < nslot; idx++) {
            ivac++;
            String myid = "New" + Integer.toString(idx);

            String vacid = "Vaccine" + myid;
            String mfrid = "Mfr" + myid;
            String vyrsid = "Vyrs" + myid;
            String vmosid = "Vmos" + myid;
            String vwksid = "Vwks" + myid;
            String vdysid = "Vdys" + myid;
            String vdtid = "Vdate" + myid;
            String vnoteid = "Vnote" + myid;

            retstr = retstr + "<tr>\n";
//         retstr = retstr + "<td class='edits'>" +
//                 "<label class='hidden' for='"+refid+"'>Reference for vaccination" + Integer.toString(ivac) + "</label>" +
//                 "<select name='"+refid+"' id='"+refid+"' size=1>\n";
//         retstr = retstr + myitem.makeRefOptions();
//         retstr = retstr + "</select></td>\n";
//         retstr = retstr + "<td class='edits'>" +
//                 "<label class='hidden' for='"+perid+"'>Period Type for vaccination " + Integer.toString(ivac) + "</label>" +
//                 "<select name='"+perid+"' id='"+perid+"' size=1>\n";
//         retstr = retstr + myitem.makePeriodOptions();
//         retstr = retstr + "</select></td>\n";
//         retstr = retstr + "<td class='edits'>" +
//               "<label class='hidden' for='"+amtid+"'>Period Amount for vaccination " + Integer.toString(ivac) + "</label>" +
//               "<input type='text' name='"+amtid+"' id='"+amtid+"' size=4" +
//               " maxlength=5 value=''></td>\n";
//         retstr = retstr + "<td class='edits'>" +
//               "<label class='hidden' for='"+offid+"'>Additional Days for vaccination " + Integer.toString(ivac) + "</label>" +
//               "<input type='text' name='"+offid+"' id='"+offid+"' size=4" +
//               " maxlength=5 value=''></td>\n";

            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + vacid + "'>Vaccine for vaccination " + Integer.toString(ivac) + "</label>"
                    + "<select name='" + vacid + "' id='" + vacid + "' size=1 onchange='javascript:ChangeVaccine(\"" + vacid + "\",\"" + mfrid + "\")'>\n";
            retstr = retstr + "<option value='" + CAppConsts.TagNoValue + "'>" + CAppConsts.TagNoLabel + "</option>\n";
            retstr = retstr + vaccines.makeOptions(CAppConsts.TagNoValue);
            retstr = retstr + "</select></td>\n";

            retstr = retstr + "<td class='edits'>";
            retstr = retstr + "<label class='hidden' for='" + mfrid + "'>Manufacturer for vaccination " + Integer.toString(ivac) + "</label>"
                    + "<select name='" + mfrid + "' id='" + mfrid + "' size=1>\n";
            retstr = retstr + "<option value='" + CAppConsts.TagNoValue + "'></option>\n";
            retstr = retstr + mfrs.makeOptions(CAppConsts.TagNoValue, CAppConsts.TagNoValue);
            retstr = retstr + "</select></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<input type='text' name='" + vyrsid + "' id='" + vyrsid + "' size=4 onchange=\"javascript:setCalcVacDate(this.name)\""
                    + " maxlength=4 value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<input type='text' name='" + vmosid + "' id='" + vmosid + "' size=4 onchange=\"javascript:setCalcVacDate(this.name)\""
                    + " maxlength=4 value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<input type='text' name='" + vwksid + "' id='" + vwksid + "' size=4 onchange=\"javascript:setCalcVacDate(this.name)\""
                    + " maxlength=4 value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<input type='text' name='" + vdysid + "' id='" + vdysid + "' size=4 onchange=\"javascript:setCalcVacDate(this.name)\""
                    + " maxlength=4 value=''></td>\n";
            retstr = retstr + "<td class='edits'>"
                    + "<label class='hidden' for='" + vdtid + "'>Date at vaccination " + Integer.toString(ivac) + "</label>"
                    + "<input type='text' name='" + vdtid + "' id='" + vdtid + "' size=10 onchange=\"javascript:setCalcVacAge(this.name)\""
                    + " maxlength=10 value=''></td>\n";

            retstr = retstr + "<td class='edits'>";
            retstr = retstr + "<label class='hidden' for='" + vnoteid + "'>Notes for vaccination " + Integer.toString(ivac) + "</label>"
                    + "<select name='" + vnoteid + "' id='" + vnoteid + "' size=1>\n";
            retstr = retstr + "<option value='" + CAppConsts.TagNoValue + "'></option>\n";
            retstr = retstr + vacnotes.makeOptions(CAppConsts.TagNoValue);
            retstr = retstr + "</select></td>\n";
            retstr = retstr + "</tr>\n";

        }
        retstr = retstr + "</table>\n";
        //     retstr = retstr + "<a OnMouseDown='javascript:SwapBtn(\"Calc\",\"CalcDn\")' " +
        //                "OnMouseUp='javascript:SwapBtn(\"Calc\",\"CalcUp\")' " +
        //                "HREF='javascript:DoSubmit(\"EditForm\",\"Calc\")'>" +
        //                "<img class='btnimg' id='Calc' name='Calc' " +
        //                "alt='Calculate button' src='images/CalcUp.gif'></a>\n";
        retstr = retstr + "</dd>\n";
        retstr = retstr + "<br>\n";
        return (retstr);
    }

    public void updateItem(HttpServletRequest arequest, Date abirthdt) throws Exception {
        CStringList tmplist = new CStringList(true);
        //     Date lastdt = new Date(abirthdt.getTime());
        //     System.out.println(arequest);
        for (int idx = 0; idx < this.getCount(); idx++) {
            CShotItem myitem = (CShotItem) this.getItem(idx);
            //        String refid = "Refer" + myitem.shotid;
            //        String perid = "Period" + myitem.shotid;
            //        String amtid = "Amount" + myitem.shotid;
            //        String offid = "Offset" + myitem.shotid;
            String vacid = "Vaccine" + myitem.shotid;
            String mfrid = "Mfr" + myitem.shotid;
            String vyrsid = "Vyrs" + myitem.shotid;
            String vmosid = "Vmos" + myitem.shotid;
            String vwksid = "Vwks" + myitem.shotid;
            String vdysid = "Vdys" + myitem.shotid;
            String shtdid = "Vdate" + myitem.shotid;
            String vnoteid = "Vnote" + myitem.shotid;

            String vacstr = arequest.getParameter(vacid);
//         String amtstr = arequest.getParameter(amtid);

            if (vacstr.equals(CAppConsts.TagNoValue)) // || amtstr == null || amtstr.length() == 0)
            {
                continue;
            }

            myitem.vaccinecd = vacstr;
            myitem.mfrcd = arequest.getParameter(mfrid);
            myitem.vageyears = getIntVal(arequest.getParameter(vyrsid));
            myitem.vagemonths = getIntVal(arequest.getParameter(vmosid));
            myitem.vageweeks = getIntVal(arequest.getParameter(vwksid));
            myitem.vagedays = getIntVal(arequest.getParameter(vdysid));
            myitem.vacnote = arequest.getParameter(vnoteid);
            myitem.shotdate = myitem.mdyfmt.parse(arequest.getParameter(shtdid));
//         myitem.reftype = arequest.getParameter(refid);
//         myitem.periodtype = arequest.getParameter(perid);
//         String offstr = arequest.getParameter(offid);
//         myitem.setPeriodAmt(amtstr);
//         myitem.setPeriodOff(offstr);

//         lastdt = myitem.calcShotDate(abirthdt, lastdt);
            String nextid = tmplist.makeNewId("sht", 6);
            tmplist.addItem(nextid, myitem);
        }

        //int nslot = Math.max(CAppConsts.NewSlotShotHist, CAppConsts.NumSlotShotHist - getCount());
        int nslot = (this.getCount() == 0) ? 4 : 1;
        for (int idx = 0; idx < nslot; idx++) {
            CShotItem myitem = new CShotItem();
            String myid = "New" + Integer.toString(idx);
//         String refid = "Refer" + myid;
//         String perid = "Period" + myid;
//         String amtid = "Amount" + myid;
//         String offid = "Offset" + myid;
            String vacid = "Vaccine" + myid;
            String mfrid = "Mfr" + myid;
            String vyrsid = "Vyrs" + myid;
            String vmosid = "Vmos" + myid;
            String vwksid = "Vwks" + myid;
            String vdysid = "Vdys" + myid;
            String vnoteid = "Vnote" + myid;
            String shtdid = "Vdate" + myid;

            String vacstr = arequest.getParameter(vacid);
            //        String amtstr = arequest.getParameter(amtid);

            if (vacstr.equals(CAppConsts.TagNoValue)) //||             amtstr == null || amtstr.length() == 0)
            {
                continue;
            }

            myitem.vaccinecd = vacstr;
            myitem.mfrcd = arequest.getParameter(mfrid);
            myitem.vageyears = getIntVal(arequest.getParameter(vyrsid));
            myitem.vagemonths = getIntVal(arequest.getParameter(vmosid));
            myitem.vageweeks = getIntVal(arequest.getParameter(vwksid));
            myitem.vagedays = getIntVal(arequest.getParameter(vdysid));
            myitem.vacnote = arequest.getParameter(vnoteid);
//         myitem.reftype = arequest.getParameter(refid);
//         myitem.periodtype = arequest.getParameter(perid);
//         String offstr = arequest.getParameter(offid);
//         myitem.setPeriodAmt(amtstr);
//         myitem.setPeriodOff(offstr);
//         lastdt = myitem.calcShotDate(abirthdt, lastdt);

            myitem.shotdate = myitem.mdyfmt.parse(arequest.getParameter(shtdid));

            String nextid = tmplist.makeNewId("sht", 6);
            tmplist.addItem(nextid, myitem);
        }

        this.clear();
        for (int idx = 0; idx < tmplist.getCount(); idx++) {
            String myid = tmplist.getString(idx);
            CShotItem myitem = (CShotItem) tmplist.getItem(idx);
            myitem.shotid = myid;
            this.addItem(myid, myitem);
        }
    }

    public String showDisplay(Connection aconn, Date abirth) {
        CCodeDesc vaccines = new CCodeDesc(aconn, "VaccineTbl", "VaccineCd", "VaccineNm", "VaccineCd");
        //display only the short name (code value) for MFR since this field may not be relevant to assessment and can default to UNK
        CCodeDesc mfrs = new CCodeDesc(aconn, "MfrTbl", "MfrCd", "MfrCd", "MfrCd", "VaccineMfrTbl", "VaccineCd");
        CCodeDesc vacnotes = new CCodeDesc(aconn, "NoteTbl", "NoteCd", "NoteTxt", "NoteCd");

        String retstr = " <table class=\"midtitle\" >";
        retstr = retstr + "<tr>";
        retstr = retstr + "<td colspan='8' class=\"midtitleleft\" >&nbsp;&nbsp;Vaccinations</td> </tr> </table>";
        retstr = retstr + "<dd class='details'>"
                + "<input type=hidden name='mfrdatadrop' id='mfrdatadrop' value='" + mfrs.makeDataDrop() + "'>\n";
        retstr = retstr + "<table class='factors' summary='vaccinations'>";
        retstr = retstr + "<tr>\n";
        retstr = retstr + "<th class='factors' scope='col'><label>Vaccine</label></th>";
        retstr = retstr + "<th class='factors' scope='col'><label>Mfr</label></th>";
        retstr = retstr + "<th class='factors' scope='col'><label>Yrs</label></th>";
        retstr = retstr + "<th class='factors' scope='col'><label>Mos</label></th>";
        retstr = retstr + "<th class='factors' scope='col'><label>Wks</label></th>";
        retstr = retstr + "<th class='factors' scope='col'><label>Days</label></th>";
        retstr = retstr + "<th class='factors' scope='col'><label>Date</label></th>";
        retstr = retstr + "<th class='factors' scope='col'><label>Note</label></th>";
        retstr = retstr + "<th class='hidden' scope='col'></th>";
        retstr = retstr + "</tr>";


        for (int idx = 0; idx < this.getCount(); idx++) {
            CShotItem myitem = (CShotItem) this.getItem(idx);
            retstr = retstr + "<tr>\n";
            retstr = retstr + "<td class='factors'>" + vaccines.getDescByCode(myitem.vaccinecd) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + mfrs.getDescByCode(myitem.vaccinecd, myitem.mfrcd) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.vageyears) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.vagemonths) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.vageweeks) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.vagedays) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + myitem.getShotDateStr() + "</td>\n";
            retstr = retstr + "<td class='factors'>" + (CAppConsts.TagNoValue.equals(myitem.vacnote) ? "" : vacnotes.getDescByCode(myitem.vacnote)) + "</td>\n";
            retstr = retstr + "</tr>\n";
        }
        retstr = retstr + "</table></dd><br>\n";
        return (retstr);


    }

    public String buildShotStr(int achild) {
        String shotstr = "";
        int shotnum = 1;
        for (int idx = 0; idx < getCount(); idx++) {
            CShotItem shot = (CShotItem) this.getItem(idx);
            shotstr = shotstr + shot.buildShotStr(achild, shotnum);
            shotnum++;
        }
        return (shotstr);
    }

    public String exportList() {
        if (this.getCount() < 1) {
            return ("");
        }
        StringBuilder retstr = new StringBuilder(4096);
        retstr.append("<ShotList>\n");
        for (int idx = 0; idx < getCount(); idx++) {
            CShotItem shot = (CShotItem) this.getItem(idx);
            retstr.append(shot.exportItem());
        }
        retstr.append("</ShotList>\n");
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
