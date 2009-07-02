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
         CLogError.logError(CConsts.ErrMsgFile, false, "CEvalList.dbReadList cannot read list. ", ex);
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
         CLogError.logError(CConsts.ErrMsgFile, false, "CEvalList.dbDeleteList cannot delete list. ", ex);
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
         CLogError.logError(CConsts.ErrMsgFile, false, "CEvalList.dbWriteList cannot write list. ", ex);
      }
   }

   public String showDisplay(Connection aconn)
   {
      CCodeDesc series = new CCodeDesc(aconn, "SeriesTbl","SeriesCd","SeriesNm","SeriesCd");
      CCodeDesc results = new CCodeDesc(aconn, "EvalResultTbl","ResultCd","ResultNm","ResultCd");
      
      String retstr = "<dt class='details'>Evaluated Next Dose</dt>\n";
      retstr = retstr + "<dd class='details'>\n";
      retstr = retstr + "<table class='factors' summary='next dose'>\n";
      retstr = retstr + "<tr>\n";
      retstr = retstr + "<th class='factors' scope='col'>Series</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Result</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Dose Number</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Accelerated Date</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Recommended Date</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Overdue Date</th></tr>\n";
      
      for (int idx = 0; idx < this.getCount(); idx++)
      {
         CEvalItem myitem = (CEvalItem) this.getItem(idx);
         retstr = retstr + "<tr>\n";
         retstr = retstr + "<td class='factors'>" + series.getDescByCode(myitem.seriescd) + "</td>\n";
         retstr = retstr + "<td class='factors'>" + results.getDescByCode(myitem.resultcd) + "</td>\n";
         retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.doseord) + "</td>\n";
         retstr = retstr + "<td class='factors'>" + myitem.getAccelDateStr() + "</td>\n";
         retstr = retstr + "<td class='factors'>" + myitem.getRecomDateStr() + "</td>\n";
         retstr = retstr + "<td class='factors'>" + myitem.getOverdueDateStr() + "</td>\n";
         retstr = retstr + "</tr>\n";
      }      

      retstr = retstr + "</table></dd>\n";
      return(retstr);
   }

   public String showEdit(Connection aconn)
   {
      if (this.getCount() == 0) return("");
      CCodeDesc series = new CCodeDesc(aconn, "SeriesTbl","SeriesCd","SeriesNm","SeriesCd");
      CCodeDesc results = new CCodeDesc(aconn, "EvalResultTbl","ResultCd","ResultNm","ResultCd");
      
      String retstr = "<dt class='details'>Evaluated Next Dose</dt>\n";
      retstr = retstr + "<dd class='details'>\n";

      retstr = retstr + "<table class='factors' summary='next dose'>\n";
      retstr = retstr + "<tr>\n";
      retstr = retstr + "<th class='factors' scope='col'>Series</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Result</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Dose Number</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Accelerated Date</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Recommended Date</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Overdue Date</th>\n";
      retstr = retstr + "</tr>\n";
      
      for (int idx = 0; idx < this.getCount(); idx++)
      {
         CEvalItem myitem = (CEvalItem) this.getItem(idx);
         retstr = retstr + "<tr>\n";
         retstr = retstr + "<td class='factors'>" + series.getDescByCode(myitem.seriescd) + "</td>\n";
         retstr = retstr + "<td class='factors'>" + results.getDescByCode(myitem.resultcd) + "</td>\n";
         retstr = retstr + "<td class='factors'>" + Integer.toString(myitem.doseord) + "</td>\n";
         retstr = retstr + "<td class='factors'>" + myitem.getAccelDateStr() + "</td>\n";
         retstr = retstr + "<td class='factors'>" + myitem.getRecomDateStr() + "</td>\n";
         retstr = retstr + "<td class='factors'>" + myitem.getOverdueDateStr() + "</td>\n";
         retstr = retstr + "</tr>\n";
      }      

      retstr = retstr + "</table></dd>\n";
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
