/*
 * CDosevList.java
 *
 * Created on July 24, 2008, 3:27 PM
 */
package testcase;

import manapp.*;
import java.sql.*;

/**
 * list of dose evaluation items
 */
public class CDosevList extends CStringList {

    /**
     * Creates a new instance of CDosevList
     */
    public CDosevList() {
        super();
    }

    public void dbReadList(Connection aconn, String agroup, String atest) {
        try {
            String qstr = "Select ShotId,SeriesCd,DoseNum,ValidFlag,InvalidCd"
                    + " From TDoseEvalTbl"
                    + " Where TestGroupId='" + agroup + "' and TestId='" + atest + "'";
            Statement qstmt = aconn.createStatement();
            ResultSet rset = qstmt.executeQuery(qstr);

            while (rset.next()) {
                CDosevItem myitem = new CDosevItem();
                myitem.shotid = rset.getString(1);
                myitem.seriescd = rset.getString(2);
                myitem.dosenum = rset.getInt(3);
                myitem.validflag = rset.getString(4);
                myitem.invalidcd = rset.getString(5);

                this.addItem(myitem.shotid, myitem);
            }
            rset.close();
            qstmt.close();

        } catch (Exception ex) {
            CLogError.logError(CAppConsts.ErrorFile, false, "CDosevList.dbReadList cannot read list. ", ex);
        }
    }

    public void dbDeleteList(Connection aconn, String agroup, String atest) {
        try {
            String qstr = "Delete From TDoseEvalTbl Where TestGroupId='" + agroup + "' and TestId='" + atest + "'";
            Statement qstmt = aconn.createStatement();
            qstmt.executeUpdate(qstr);
            qstmt.close();
        } catch (Exception ex) {
            CLogError.logError(CAppConsts.ErrorFile, false, "CDosevList.dbDeleteList cannot delete list. ", ex);
        }
    }

    public void dbWriteList(Connection aconn, String agroup, String atest) {
        try {
            dbDeleteList(aconn, agroup, atest);
            String qstr = "Insert into TDoseEvalTbl (TestGroupId,TestId,ShotId,SeriesCd,DoseNum,ValidFlag,InvalidCd)"
                    + " Values (?,?,?,?,?,?,?)";
            PreparedStatement stmt = aconn.prepareStatement(qstr);

            for (int idx = 0; idx < this.getCount(); idx++) {
                CDosevItem myitem = (CDosevItem) this.getItem(idx);

                stmt.setString(1, agroup);
                stmt.setString(2, atest);
                stmt.setString(3, myitem.shotid);
                stmt.setString(4, myitem.seriescd);
                stmt.setInt(5, myitem.dosenum);
                stmt.setString(6, myitem.validflag);
                stmt.setString(7, myitem.invalidcd);
                stmt.executeUpdate();
            }

            stmt.close();
        } catch (Exception ex) {
            CLogError.logError(CAppConsts.ErrorFile, false, "CDosevList.dbWriteList cannot write list. ", ex);
        }
    }

    public String showEdit(Connection aconn) {
        if (this.getCount() == 0) {
            return ("");
        }
        CCodeDesc series = new CCodeDesc(aconn, "SeriesTbl", "SeriesCd", "SeriesNm", "SeriesCd");
        CCodeDesc reasons = new CCodeDesc(aconn, "InvalidReasTbl", "InvalidCd", "InvalidNm", "InvalidCd");

        String retstr = " <table class=\"midtitle\" >";
        retstr = retstr + "<tr>";
        retstr = retstr + "<td class=\"midtitleleft\" >&nbsp;&nbsp;Dose Evaluation</td> </tr> </table>";
        retstr = retstr + "<dd class='details'>\n";

        retstr = retstr + "<table class='factors' summary='dose evaluation'>\n";
        retstr = retstr + "<tr>\n";
        retstr = retstr + "<th class='factors' scope='col'>Series</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Dose Number</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Valid</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Invalid Reason</th>\n";
        retstr = retstr + "</tr>\n";

        for (int idx = 0; idx < this.getCount(); idx++) {
            CDosevItem myitem = (CDosevItem) this.getItem(idx);
            retstr = retstr + "<tr>\n";
            retstr = retstr + "<td class='factors'>" + series.getDescByCode(myitem.seriescd) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.dosenum) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + myitem.validflag + "</td>\n";
            retstr = retstr + "<td class='factors'>" + (myitem.invalidcd.equals("") ? "" : reasons.getDescByCode(myitem.invalidcd)) + "</td>\n";
            retstr = retstr + "</tr>\n";
        }

        retstr = retstr + "</table></dd><br>\n";
        return (retstr);
    }

    public String showDisplay(Connection aconn) {
        CCodeDesc series = new CCodeDesc(aconn, "SeriesTbl", "SeriesCd", "SeriesNm", "SeriesCd");
        CCodeDesc reasons = new CCodeDesc(aconn, "InvalidReasTbl", "InvalidCd", "InvalidNm", "InvalidCd");

        String retstr = " <table class=\"midtitle\" >";
        retstr = retstr + "<tr>";
        retstr = retstr + "<td class=\"midtitleleft\" >&nbsp;&nbsp;Dose Evaluation</td> </tr> </table> ";
        retstr = retstr + "<dd class='details'>\n";

        retstr = retstr + "<table class='factors' summary='dose evaluation'>\n";
        retstr = retstr + "<tr>\n";
        retstr = retstr + "<th class='factors' scope='col'>Series</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Dose Number</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Valid</th>\n";
        retstr = retstr + "<th class='factors' scope='col'>Invalid Reason</th>\n";
        retstr = retstr + "</tr>\n";

        for (int idx = 0; idx < this.getCount(); idx++) {
            CDosevItem myitem = (CDosevItem) this.getItem(idx);
            retstr = retstr + "<tr>\n";
            retstr = retstr + "<td class='factors'>" + series.getDescByCode(myitem.seriescd) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.dosenum) + "</td>\n";
            retstr = retstr + "<td class='factors'>" + myitem.validflag + "</td>\n";
            retstr = retstr + "<td class='factors'>" + (myitem.invalidcd.equals("") ? "" : reasons.getDescByCode(myitem.invalidcd)) + "</td>\n";
            retstr = retstr + "</tr>\n";
        }

        retstr = retstr + "</table></dd><br>\n";
        return (retstr);
    }
}
