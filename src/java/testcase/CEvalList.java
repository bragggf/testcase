/*
 * CEvalList.java
 *
 * Created on July 22, 2008, 12:45 PM
 */

package testcase;

import manapp.*;
import java.sql.*;

/** list of evaluation results */
public class CEvalList extends CStringList
{
   
   /** Creates a new instance of CEvalList */
   public CEvalList()
   {
      super(true);
   }
   
   public void dbReadList(Connection aconn, String agroup, String atest)
   {
      try
      {
         String qstr = "Select EvalId,SeriesCd,ResultCd,NextDoseNum,AccelDate,RecomDate,OverdueDate" +
               " From TSeriesEvalTbl" + 
               " Where TestGroupId='" + agroup + "' and TestId='" + atest + "'";
         Statement qstmt = aconn.createStatement();
         ResultSet rset = qstmt.executeQuery(qstr);

         while (rset.next())
         {
            CEvalItem myitem = new CEvalItem();
            myitem.evalid = rset.getString(1);
            myitem.seriescd = rset.getString(2);
            myitem.resultcd = rset.getString(3);
            myitem.doseord = rset.getInt(4);
            myitem.acceldate = rset.getDate(5);
            myitem.recomdate = rset.getDate(6);
            myitem.overduedate = rset.getDate(7);
            
            this.addItem(myitem.evalid, myitem);
         }
         rset.close();
         qstmt.close();
         
      }
      catch (Exception ex)
      {
         CLogError.logError(CAppConsts.ErrorFile, false, "CEvalList.dbReadList cannot read list. ", ex);
      }
   }
      
   public void dbDeleteList(Connection aconn, String agroup, String atest)
   {
      try
      {
         String qstr = "Delete From TSeriesEvalTbl Where TestGroupId='" + agroup + "' and TestId='" + atest + "'";
         Statement qstmt = aconn.createStatement();
         qstmt.executeUpdate(qstr);
         qstmt.close();
      }
      catch (Exception ex)
      {
         CLogError.logError(CAppConsts.ErrorFile, false, "CEvalList.dbDeleteList cannot delete list. ", ex);
      }
   }
   
   public void dbWriteList(Connection aconn, String agroup, String atest)
   {
      try
      {
         dbDeleteList(aconn, agroup, atest);
         String qstr = "Insert into TSeriesEvalTbl (TestGroupId,TestId,EvalId,SeriesCd,ResultCd,NextDoseNum,AccelDate,RecomDate,OverdueDate)" +
               " Values (?,?,?,?,?,?,?,?,?)";
         PreparedStatement stmt = aconn.prepareStatement(qstr);

         for (int idx = 0; idx < this.getCount(); idx++)
         {
            CEvalItem myitem = (CEvalItem) this.getItem(idx);
         
            stmt.setString(1, agroup);
            stmt.setString(2, atest);
            stmt.setString(3, myitem.evalid);
            stmt.setString(4, myitem.seriescd);
            stmt.setString(5, myitem.resultcd);
            stmt.setInt(6, myitem.doseord);
            stmt.setDate(7, new java.sql.Date(myitem.acceldate.getTime()));
            stmt.setDate(8, new java.sql.Date(myitem.recomdate.getTime()));
            stmt.setDate(9, new java.sql.Date(myitem.overduedate.getTime()));
            stmt.executeUpdate();
         }
         
         stmt.close();
      }
      catch (Exception ex)
      {
         CLogError.logError(CAppConsts.ErrorFile, false, "CEvalList.dbWriteList cannot write list. ", ex);
      }
   }

   public String showDisplay(Connection aconn)
   {
      CCodeDesc series = new CCodeDesc(aconn, "SeriesTbl","SeriesCd","SeriesNm","SeriesCd");
      CCodeDesc results = new CCodeDesc(aconn, "EvalResultTbl","ResultCd","ResultNm","ResultCd");
      
      
        String retstr = " <table class=\"result\" >";
        retstr = retstr + "<tr>";
        retstr = retstr + "<td class=\"restitleleft\" >&nbsp;&nbsp;Evaluation Results</td> </tr> </table>";
        
        retstr = retstr + "<table class='factors' summary='next dose evaluation'>\n";
        retstr = retstr + "<col style='width:20%'><col style='width:15%'><col style='width:5%'>" ;
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
         
     
      for (int idx = 0; idx < this.getCount(); idx++)
      {
         CEvalItem myitem = (CEvalItem) this.getItem(idx);
         retstr = retstr + "<tr>\n";
         retstr = retstr + "<td class='result'>" + series.getDescByCode(myitem.seriescd) + "</td>\n";
         retstr = retstr + "<td class='result'>" + results.getDescByCode(myitem.resultcd) + "</td>\n";
         retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.doseord) + "</td>\n";
           
         retstr = retstr + "<td class='factors' name='EAyrs"+ idx +"' id='EAyrs"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='EAmos"+ idx +"' id='EAmos"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='EAwks"+ idx +"' id='EAwks"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='EAdys"+ idx +"' id='EAdys"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='EAdate"+ idx +"' id='EAdate"+ idx +"'>" + myitem.getAccelDateStr() + "</td>\n";
         retstr = retstr + "<td class='factors' name='ERyrs"+ idx +"' id='ERyrs"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='ERmos"+ idx +"' id='ERmos"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='ERwks"+ idx +"' id='ERwks"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='ERdys"+ idx +"' id='ERdys"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='ERdate"+ idx +"' id='ERdate"+ idx +"'>" + myitem.getRecomDateStr() + "</td>\n";
         retstr = retstr + "<td class='factors' name='EOyrs"+ idx +"' id='EOyrs"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='EOmos"+ idx +"' id='EOmos"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='EOwks"+ idx +"' id='EOwks"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='EOdys"+ idx +"' id='EOdys"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='EOdate"+ idx +"' id='EOdate"+ idx +"'>" + myitem.getOverdueDateStr() + "</td>\n";
         retstr = retstr + "</tr>\n";
      }      

      retstr = retstr + "</table><br>\n";
      return(retstr);
   }

   public String showEdit(Connection aconn)
   {
      if (this.getCount() == 0) return("");
      CCodeDesc series = new CCodeDesc(aconn, "SeriesTbl","SeriesCd","SeriesNm","SeriesCd");
      CCodeDesc results = new CCodeDesc(aconn, "EvalResultTbl","ResultCd","ResultNm","ResultCd");
      
      
        String retstr = " <table class=\"result\" >";
        retstr = retstr + "<tr>";
        retstr = retstr + "<td class=\"restitleleft\" >&nbsp;&nbsp;Evaluation Results</td> </tr></table> ";

        retstr = retstr + "<table class='factors' summary='next dose evaluation'>\n";
        retstr = retstr + "<col style='width:20%'><col style='width:15%'><col style='width:5%'>" ;
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
         
      for (int idx = 0; idx < this.getCount(); idx++)
      {
         CEvalItem myitem = (CEvalItem) this.getItem(idx);
         retstr = retstr + "<tr>\n";
         retstr = retstr + "<td class='result'>" + series.getDescByCode(myitem.seriescd) + "</td>\n";
         retstr = retstr + "<td class='result'>" + results.getDescByCode(myitem.resultcd) + "</td>\n";
         retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.doseord) + "</td>\n";
           
         retstr = retstr + "<td class='factors' name='EAyrs"+ idx +"' id='EAyrs"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='EAmos"+ idx +"' id='EAmos"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='EAwks"+ idx +"' id='EAwks"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='EAdys"+ idx +"' id='EAdys"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='EAdate"+ idx +"' id='EAdate"+ idx +"'>" + myitem.getAccelDateStr() + "</td>\n";
         retstr = retstr + "<td class='factors' name='ERyrs"+ idx +"' id='ERyrs"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='ERmos"+ idx +"' id='ERmos"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='ERwks"+ idx +"' id='ERwks"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='ERdys"+ idx +"' id='ERdys"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='ERdate"+ idx +"' id='ERdate"+ idx +"'>" + myitem.getRecomDateStr() + "</td>\n";
         retstr = retstr + "<td class='factors' name='EOyrs"+ idx +"' id='EOyrs"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='EOmos"+ idx +"' id='EOmos"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='EOwks"+ idx +"' id='EOwks"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='EOdys"+ idx +"' id='EOdys"+ idx +"'></td>\n";
         retstr = retstr + "<td class='factors' name='EOdate"+ idx +"' id='EOdate"+ idx +"'>" + myitem.getOverdueDateStr() + "</td>\n";
         retstr = retstr + "</tr>\n";
      }      

      retstr = retstr + "</table><br>\n";
      return(retstr);
   }
   
   public void makeItem(CEvalItem aitem)
   {
      CEvalItem myitem = new CEvalItem();
      myitem.copyItem(aitem);
      myitem.evalid = this.makeNewId("res", 6);
      this.addItem(myitem.evalid, myitem);
   }
}
