/*
 * CTextList.java
 *
 * Created on July 18, 2008, 4:56 PM
 */

package testcase;

import manapp.*;

import java.sql.*;
import javax.servlet.http.*;

/** list of expected result text items */
public class CTextList extends CStringList
{
   
   /** Creates a new instance of CTextList */
   public CTextList()
   {
      super(true);
   }
   
   public void dbReadList(Connection aconn, String agroup, String atest)
   {
      try
      {
         String qstr = "Select ExpResId,ExpectTxt,ResultTxt" +
               " From TExpectResTbl" +
               " Where TestGroupId='" + agroup + "' and TestId='" + atest + "'";
         Statement qstmt = aconn.createStatement();
         ResultSet rset = qstmt.executeQuery(qstr);

         while (rset.next())
         {
            CTextItem myitem = new CTextItem();
            myitem.expectid = rset.getString(1);
            myitem.expecttxt = rset.getString(2);
            myitem.resulttxt = rset.getString(3);
            this.addItem(myitem.expectid, myitem);
         }
         rset.close();
         qstmt.close();
         
      }
      catch (Exception ex)
      {
         CLogError.logError(CAppConsts.ErrorFile, false, "CTextList.dbReadList cannot read list. ", ex);
      }
   }
      
   public void dbDeleteList(Connection aconn, String agroup, String atest)
   {
      try
      {
         String qstr = "Delete From TExpectResTbl Where TestGroupId='" + agroup + "' and TestId='" + atest + "'";
         Statement qstmt = aconn.createStatement();
         qstmt.executeUpdate(qstr);
         qstmt.close();
      }
      catch (Exception ex)
      {
         CLogError.logError(CAppConsts.ErrorFile, false, "CTextList.dbDeleteList cannot delete list. ", ex);
      }
   }
   
   public void dbWriteList(Connection aconn, String agroup, String atest)
   {
      try
      {
         dbDeleteList(aconn, agroup, atest);
         String qstr = "Insert into TExpectResTbl (TestGroupId,TestId,ExpResId,ExpectTxt,ResultTxt)" +
               " Values (?,?,?,?,?)";
         PreparedStatement stmt = aconn.prepareStatement(qstr);

         for (int idx = 0; idx < this.getCount(); idx++)
         {
            CTextItem myitem = (CTextItem) this.getItem(idx);
         
            stmt.setString(1, agroup);
            stmt.setString(2, atest);
            stmt.setString(3, myitem.expectid);
            stmt.setString(4, myitem.expecttxt);
            stmt.setString(5, myitem.resulttxt);
            stmt.executeUpdate();
         }
         
         stmt.close();
      }
      catch (Exception ex)
      {
         CLogError.logError(CAppConsts.ErrorFile, false, "CTextList.dbWriteList cannot write list. ", ex);
      }
   }

   public String showEdit(Connection aconn)
   {
      String retstr = "<dt class='details'>Overall Rule Assessment</dt>\n";
      retstr = retstr + "<dd class='details'>\n";
      retstr = retstr + "<table class='factors' summary='Overall Rule Assessment'>\n";
      retstr = retstr + "<tr>\n";
      retstr = retstr + "<th class='factors' scope='col'>Expected Result</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Result Notes</th>\n";
      retstr = retstr + "</tr>\n";
      int icnt = 0;
      for (int idx = 0; idx < this.getCount(); idx++)
      {
         icnt++;
         CTextItem myitem = (CTextItem) this.getItem(idx);
         String txtid = "ExpText" + myitem.expectid;
         String resid = "ResText" + myitem.expectid;
         retstr = retstr + "<tr>\n";
         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+txtid+"'>Expected Result " + Integer.toString(icnt) + "</label>" +
               "<input type='text' name='"+txtid+"' id='"+txtid+"' size=45" +
               " maxlength=" + Integer.toString(CAppConsts.MaxLenExpectTxt) + " value='" + myitem.expecttxt + "'></td>\n";
         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+resid+"'>Result Note " + Integer.toString(icnt) + "</label>" +
               "<input type='text' name='"+resid+"' id='"+resid+"' size=45" +
               " maxlength=" + Integer.toString(CAppConsts.MaxLenExpectTxt) + " value='" + myitem.resulttxt + "'></td>\n";
         retstr = retstr + "</tr>\n";
      }      
      
      int nslot = Math.max(1, 4 - getCount());
      for (int idx = 0; idx < nslot; idx++)
      {
         icnt++;
         String myid = "New" + Integer.toString(idx);
         String txtid = "ExpText" + myid;
         String resid = "ResText" + myid;
         
         retstr = retstr + "<tr>\n";
         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+txtid+"'>Expected Result " + Integer.toString(icnt) + "</label>" +
               "<input type='text' name='"+txtid+"' id='"+txtid+"' size=45" +
               " maxlength=" + Integer.toString(CAppConsts.MaxLenExpectTxt) + " value=''></td>\n";
         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+resid+"'>Result Note " + Integer.toString(icnt) + "</label>" +
               "<input type='text' name='"+resid+"' id='"+resid+"' size=45" +
               " maxlength=" + Integer.toString(CAppConsts.MaxLenExpectTxt) + " value=''></td>\n";
         retstr = retstr + "</tr>\n";
      }
      retstr = retstr + "</table></dd>\n";
      return(retstr);
   }
   
   public void updateItem(HttpServletRequest arequest) throws Exception
   {
      for (int idx = this.getCount()-1; idx >= 0; idx--)
      {
         CTextItem myitem = (CTextItem) this.getItem(idx);
         String txtid = "ExpText" + myitem.expectid;
         String resid = "ResText" + myitem.expectid;
         
         String expstr = CParser.truncStr(arequest.getParameter(txtid), CAppConsts.MaxLenExpectTxt);
         if (expstr == null || expstr.length() == 0) 
         {
            this.delItem(idx);
            continue;
         }
         String resstr = CParser.truncStr(arequest.getParameter(resid), CAppConsts.MaxLenExpectTxt);
         
         myitem.expecttxt = expstr;
         myitem.resulttxt = resstr;
      }
      
      int nslot = Math.max(1, 3 - getCount());
      for (int idx = 0; idx < nslot; idx++)
      {
         CTextItem myitem = new CTextItem();
         String myid = "New" + Integer.toString(idx);
         String txtid = "ExpText" + myid;
         String resid = "ResText" + myid;

         String expstr = CParser.truncStr(arequest.getParameter(txtid), CAppConsts.MaxLenExpectTxt);
         if (expstr == null || expstr.length() == 0) continue;
         String resstr = CParser.truncStr(arequest.getParameter(resid), CAppConsts.MaxLenExpectTxt);
         
         myitem.expectid = this.makeNewId("ert", 6);
         myitem.expecttxt = expstr;
         myitem.resulttxt = resstr;
         this.addItem(myitem.expectid, myitem);
      }
   }

   public String showDisplay(Connection aconn)
   {
      String retstr = "<dt class='details'>Overall Rule Assessment</dt>\n";
      retstr = retstr + "<dd class='details'>\n";
      retstr = retstr + "<table class='factors' summary='Overall Rule Assessment'>\n";
      retstr = retstr + "<tr>\n";
      retstr = retstr + "<th class='factors' scope='col'>Expected Result</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Result Notes</th>\n";
      retstr = retstr + "</tr>\n";
      for (int idx = 0; idx < this.getCount(); idx++)
      {
         CTextItem myitem = (CTextItem) this.getItem(idx);
         retstr = retstr + "<tr>\n";
         retstr = retstr + "<td class='factors'>" + myitem.expecttxt + "</td>\n";
         retstr = retstr + "<td class='factors'>" + myitem.resulttxt + "</td>\n";
         retstr = retstr + "</tr>\n";
      }      
      retstr = retstr + "</table></dd>\n";
      return(retstr);
   }
}
