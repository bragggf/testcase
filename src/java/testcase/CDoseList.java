/*
 * CDoseList.java
 *
 * Created on July 17, 2008, 4:10 PM
 */

package testcase;

import manapp.*;
import java.sql.*;
import javax.servlet.http.*;

/** list of expected next dose items */
public class CDoseList extends CStringList
{
   
   /** Creates a new instance of CDoseList */
   public CDoseList()
   {
      super(true);
   }
   
   public void dbReadList(Connection aconn, String agroup, String atest)
   {
      try
      {
         String qstr = "Select DoseId,SeriesCd,ResultCd,NextDoseNum,AccelDate,RecomDate,OverdueDate,TestResult" +
               " From TExpectDoseTbl" + 
               " Where TestGroupId='" + agroup + "' and TestId='" + atest + "'";
         Statement qstmt = aconn.createStatement();
         ResultSet rset = qstmt.executeQuery(qstr);

         while (rset.next())
         {
            CDoseItem myitem = new CDoseItem();
            myitem.doseid = rset.getString(1);
            myitem.seriescd = rset.getString(2);
            myitem.resultcd = rset.getString(3);
            myitem.doseord = rset.getInt(4);
            myitem.acceldate = rset.getDate(5);
            myitem.recomdate = rset.getDate(6);
            myitem.overduedate = rset.getDate(7);
            myitem.testresult = rset.getString(8);

            this.addItem(myitem.doseid, myitem);
         }
         rset.close();
         qstmt.close();
         
      }
      catch (Exception ex)
      {
         CLogError.logError(CAppConsts.ErrorFile, false, "CDoseList.dbReadList cannot read list. ", ex);
      }
   }
      
   public void dbDeleteList(Connection aconn, String agroup, String atest)
   {
      try
      {
         String qstr = "Delete From TExpectDoseTbl Where TestGroupId='" + agroup + "' and TestId='" + atest + "'";
         Statement qstmt = aconn.createStatement();
         qstmt.executeUpdate(qstr);
         qstmt.close();
      }
      catch (Exception ex)
      {
         CLogError.logError(CAppConsts.ErrorFile, false, "CDoseList.dbDeleteList cannot delete list. ", ex);
      }
   }
   
   public void dbWriteList(Connection aconn, String agroup, String atest)
   {
      try
      {
         dbDeleteList(aconn, agroup, atest);
         String qstr = "Insert into TExpectDoseTbl (TestGroupId,TestId,DoseId,SeriesCd,ResultCd," +
               "NextDoseNum,AccelDate,RecomDate,OverdueDate,TestResult)" +
               " Values (?,?,?,?,?,?,?,?,?,?)";
         PreparedStatement stmt = aconn.prepareStatement(qstr);

         for (int idx = 0; idx < this.getCount(); idx++)
         {
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
            stmt.executeUpdate();
         }
         
         stmt.close();
      }
      catch (Exception ex)
      {
         CLogError.logError(CAppConsts.ErrorFile, false, "CDoseList.dbWriteList cannot write list. ", ex);
      }
   }

   public void modifyDates(long adiff)
   {
      for (int idx = 0; idx < this.getCount(); idx++)
      {
         CDoseItem myitem = (CDoseItem) this.getItem(idx);
         myitem.acceldate.setTime(myitem.acceldate.getTime() + adiff);
         myitem.recomdate.setTime(myitem.recomdate.getTime() + adiff);
         myitem.overduedate.setTime(myitem.overduedate.getTime() + adiff);
      }
   }

   public String showEdit(Connection aconn)
   {
      CCodeDesc series = new CCodeDesc(aconn, "SeriesTbl","SeriesCd","SeriesNm","SeriesCd");
      CCodeDesc results = new CCodeDesc(aconn, "EvalResultTbl","ResultCd","ResultNm","ResultCd");
      String retstr = "<dt class='details'>Expected Next Dose</dt>\n";
      retstr = retstr + "<dd class='details'>\n";
      retstr = retstr + "<table class='factors' summary='next dose'>\n";
      retstr = retstr + "<tr>\n";
      retstr = retstr + "<th class='factors' scope='col'>Series</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Result</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Dose Number</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Accelerated Date</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Recommended Date</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Overdue Date</th></tr>\n";
      
      int icnt = 0;
      for (int idx = 0; idx < this.getCount(); idx++)
      {
         icnt++;
         CDoseItem myitem = (CDoseItem) this.getItem(idx);
         String serid = "Series" + myitem.doseid;
         String resid = "Imstat" + myitem.doseid;
         String dosid = "Dose" + myitem.doseid;
         String accid = "Accel" + myitem.doseid;
         String recid = "Recom" + myitem.doseid;
         String ovrid = "Over" + myitem.doseid;
      
         retstr = retstr + "<tr>\n";
         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+serid+"'>Series " + Integer.toString(icnt) + "</label>" +
               "<select name='"+serid+"' id='"+serid+"' size=1>\n";
         retstr = retstr + "<option value='"+CAppConsts.TagNoValue+"'>" + CAppConsts.TagNoLabel + "</option>\n";
         retstr = retstr + series.makeOptions(myitem.seriescd);
         retstr = retstr + "</select></td>\n";
         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+resid+"'>Result " + Integer.toString(icnt) + "</label>" +
               "<select name='"+resid+"' id='"+resid+"' size=1>\n";
         retstr = retstr + "<option value='"+CAppConsts.TagNoValue+"'>" + CAppConsts.TagNoLabel + "</option>\n";
         retstr = retstr + results.makeOptions(myitem.resultcd);
         retstr = retstr + "</select></td>\n";
         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+dosid+"'>Dose " + Integer.toString(icnt) + "</label>" +
               "<input type='text' name='"+dosid+"' id='"+dosid+"' size=4" +
               " maxlength=" + Integer.toString(CAppConsts.MaxLenDoseNum) + " value='" + Integer.toString(myitem.doseord) + "'></td>\n";
         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+accid+"'>Accelerated date " + Integer.toString(icnt) + "</label>" +
               "<input type='text' name='"+accid+"' id='"+accid+"' size=8" +
               " maxlength=" + Integer.toString(CAppConsts.MaxLenDate) + " value='" + myitem.getAccelDateStr() + "'></td>\n";
         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+recid+"'>Recommended date " + Integer.toString(icnt) + "</label>" +
               "<input type='text' name='"+recid+"' id='"+recid+"' size=8" +
               " maxlength=" + Integer.toString(CAppConsts.MaxLenDate) + " value='" + myitem.getRecomDateStr() + "'></td>\n";
         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+ovrid+"'>Overdue date " + Integer.toString(icnt) + "</label>" +
               "<input type='text' name='"+ovrid+"' id='"+ovrid+"' size=8" +
               " maxlength=" + Integer.toString(CAppConsts.MaxLenDate) + " value='" + myitem.getOverdueDateStr() + "'></td>\n";
         retstr = retstr + "</tr>\n";
      }      
      
      int nslot = Math.max(CAppConsts.NewSlotAntEval, CAppConsts.NumSlotAntEval - getCount());
      for (int idx = 0; idx < nslot; idx++)
      {
         icnt++;
         String myid = "New" + Integer.toString(idx);
         String serid = "Series" + myid;
         String resid = "Imstat" + myid;
         String dosid = "Dose" + myid;
         String accid = "Accel" + myid;
         String recid = "Recom" + myid;
         String ovrid = "Over" + myid;

         retstr = retstr + "<tr>\n";
         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+serid+"'>Series " + Integer.toString(icnt) + "</label>" +
               "<select name='"+serid+"' id='"+serid+"' size=1>\n";
         retstr = retstr + "<option value='"+CAppConsts.TagNoValue+"'>" + CAppConsts.TagNoLabel + "</option>\n";
         retstr = retstr + series.makeOptions(CAppConsts.TagNoValue);
         retstr = retstr + "</select></td>\n";
         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+resid+"'>Result " + Integer.toString(icnt) + "</label>" +
               "<select name='"+resid+"' id='"+resid+"' size=1>\n";
         retstr = retstr + "<option value='"+CAppConsts.TagNoValue+"'>" + CAppConsts.TagNoLabel + "</option>\n";
         retstr = retstr + results.makeOptions(CAppConsts.TagNoValue);
         retstr = retstr + "</select></td>\n";
         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+dosid+"'>Dose " + Integer.toString(icnt) + "</label>" +
               "<input type='text' name='"+dosid+"' id='"+dosid+"' size=4" +
               " maxlength=" + Integer.toString(CAppConsts.MaxLenDoseNum) + " value=''></td>\n";
         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+accid+"'>Accelerated date " + Integer.toString(icnt) + "</label>" +
               "<input type='text' name='"+accid+"' id='"+accid+"' size=8" +
               " maxlength=" + Integer.toString(CAppConsts.MaxLenDate) + " value=''></td>\n";
         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+recid+"'>Recommended date " + Integer.toString(icnt) + "</label>" +
               "<input type='text' name='"+recid+"' id='"+recid+"' size=8" +
               " maxlength=" + Integer.toString(CAppConsts.MaxLenDate) + " value=''></td>\n";
         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+ovrid+"'>Overdue date " + Integer.toString(icnt) + "</label>" +
               "<input type='text' name='"+ovrid+"' id='"+ovrid+"' size=8" +
               " maxlength=" + Integer.toString(CAppConsts.MaxLenDate) + " value=''></td>\n";
         retstr = retstr + "</tr>\n";
      }         

      retstr = retstr + "</table></dd>\n";
      return(retstr);
   }
   
   public void updateItem(HttpServletRequest arequest) throws Exception
   {
      try
      {
         for (int idx = this.getCount()-1; idx >= 0; idx--)
         {
            CDoseItem myitem = (CDoseItem) this.getItem(idx);
            String serid = "Series" + myitem.doseid;
            String resid = "Imstat" + myitem.doseid;
            String dosid = "Dose" + myitem.doseid;
            String accid = "Accel" + myitem.doseid;
            String recid = "Recom" + myitem.doseid;
            String ovrid = "Over" + myitem.doseid;
            
            String serstr = arequest.getParameter(serid);
            if (serstr == null || serstr.equals(CAppConsts.TagNoValue))
            {
               this.delItem(idx);
               continue;
            }
            String resstr = arequest.getParameter(resid);
            if (resstr == null || resstr.equals(CAppConsts.TagNoValue))
            {
               this.delItem(idx);
               continue;
            }
            String dosstr = CParser.truncStr(arequest.getParameter(dosid), CAppConsts.MaxLenDoseNum);
            String accstr = CParser.truncStr(arequest.getParameter(accid), CAppConsts.MaxLenDate);
            String recstr = CParser.truncStr(arequest.getParameter(recid), CAppConsts.MaxLenDate);
            String ovrstr = CParser.truncStr(arequest.getParameter(ovrid), CAppConsts.MaxLenDate);

            myitem.seriescd = serstr;
            myitem.resultcd = resstr;
            if (dosstr.length() < 1) myitem.doseord = 0;
            else myitem.doseord = Integer.parseInt(dosstr);
            if (accstr.length() < 1) myitem.setAccelDate(0);
            else myitem.setAccelDate(accstr);
            if (recstr.length() < 1) myitem.setRecomDate(0);
            else myitem.setRecomDate(recstr);
            if (ovrstr.length() < 1) myitem.setOverdueDate(0);
            else myitem.setOverdueDate(ovrstr);
         }
         
         int nslot = Math.max(CAppConsts.NewSlotAntEval, CAppConsts.NumSlotAntEval - getCount());
         for (int idx = 0; idx < nslot; idx++)
         {
            CDoseItem myitem = new CDoseItem();
            String myid = "New" + Integer.toString(idx);
            String serid = "Series" + myid;
            String resid = "Imstat" + myid;
            String dosid = "Dose" + myid;
            String accid = "Accel" + myid;
            String recid = "Recom" + myid;
            String ovrid = "Over" + myid;
            
            String serstr = arequest.getParameter(serid);
            if (serstr == null || serstr.equals(CAppConsts.TagNoValue)) continue;
            String resstr = arequest.getParameter(resid);
            if (resstr == null || resstr.equals(CAppConsts.TagNoValue)) continue;
            String dosstr = CParser.truncStr(arequest.getParameter(dosid), CAppConsts.MaxLenDoseNum);
            String accstr = CParser.truncStr(arequest.getParameter(accid), CAppConsts.MaxLenDate);
            String recstr = CParser.truncStr(arequest.getParameter(recid), CAppConsts.MaxLenDate);
            String ovrstr = CParser.truncStr(arequest.getParameter(ovrid), CAppConsts.MaxLenDate);
            
            myitem.doseid = this.makeNewId("dos", 6);
            myitem.seriescd = serstr;
            myitem.resultcd = resstr;
            if (dosstr.length() < 1) myitem.doseord = 0;
            else myitem.doseord = Integer.parseInt(dosstr);
            if (accstr.length() < 1) myitem.setAccelDate(0);
            else myitem.setAccelDate(accstr);
            if (recstr.length() < 1) myitem.setRecomDate(0);
            else myitem.setRecomDate(recstr);
            if (ovrstr.length() < 1) myitem.setOverdueDate(0);
            else myitem.setOverdueDate(ovrstr);
            this.addItem(myitem.doseid, myitem);
         }
      }
      catch (Exception ex)
      {
         CLogError.logError(CAppConsts.ErrorFile, false, "CDoseList.updateItem ", ex);
         throw(ex);
      }
   }

   public String showDisplay(Connection aconn)
   {
      CCodeDesc series = new CCodeDesc(aconn, "SeriesTbl","SeriesCd","SeriesNm","SeriesCd");
      CCodeDesc results = new CCodeDesc(aconn, "EvalResultTbl","ResultCd","ResultNm","ResultCd");

      String retstr = "<dt class='details'>Expected Next Dose</dt>\n";
      retstr = retstr + "<dd class='details'>\n";
      retstr = retstr + "<table class='factors' summary='next dose'>\n";
      retstr = retstr + "<tr>\n";
      retstr = retstr + "<th class='factors' scope='col'>Series</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Result</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Dose Number</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Accelerated Date</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Recommended Date</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Overdue Date</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Status</th>\n";
      retstr = retstr + "</tr>\n";
      
      for (int idx = 0; idx < this.getCount(); idx++)
      {
         CDoseItem myitem = (CDoseItem) this.getItem(idx);
         retstr = retstr + "<tr>\n";
         retstr = retstr + "<td class='factors'>" + series.getDescByCode(myitem.seriescd) + "</td>\n";
         retstr = retstr + "<td class='factors'>" + results.getDescByCode(myitem.resultcd) + "</td>\n";
         retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.doseord) + "</td>\n";
         retstr = retstr + "<td class='factors'>" + myitem.getAccelDateStr() + "</td>\n";
         retstr = retstr + "<td class='factors'>" + myitem.getRecomDateStr() + "</td>\n";
         retstr = retstr + "<td class='factors'>" + myitem.getOverdueDateStr() + "</td>\n";
         retstr = retstr + "<td class='factors'>" + myitem.testresult + "</td>\n";
         retstr = retstr + "</tr>\n";
      }      

      retstr = retstr + "</table></dd>\n";
      return(retstr);
   }
   
   public boolean isSeriesDose(String aseries)
   {
      for (int idx = 0; idx < this.getCount(); idx++)
      {
         CDoseItem myitem = (CDoseItem) this.getItem(idx);
         if (myitem.seriescd.equals(aseries)) return(true);
      }
      return(false);
   }
   
   public void setTestResult(CEvalItem aitem)
   {
      for (int idx = 0; idx < this.getCount(); idx++)
      {
         CDoseItem myitem = (CDoseItem) this.getItem(idx);
         if (myitem.seriescd.equals(aitem.seriescd)) 
         {
            myitem.setTestResult(aitem);
            return;
         }
      }
   }
   
   public void initTestResults()
   {
      for (int idx = 0; idx < this.getCount(); idx++)
      {
         CDoseItem myitem = (CDoseItem) this.getItem(idx);
         myitem.testresult = CAppConsts.StatusNone;
      }
   }
   
   public String getTestStatus()
   {
      String mystat = CAppConsts.StatusNone;
      for (int idx = 0; idx < this.getCount(); idx++)
      {
         CDoseItem myitem = (CDoseItem) this.getItem(idx);
         if (CAppConsts.StatusFail.equals(myitem.testresult))
            return(CAppConsts.StatusFail);
         else if (CAppConsts.StatusNone.equals(myitem.testresult))
            return(CAppConsts.StatusNone);
         mystat = myitem.testresult;
      }
      return(mystat);
   }
}
