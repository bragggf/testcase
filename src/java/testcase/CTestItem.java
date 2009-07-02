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

/** test case item
 */
public class CTestItem
{
   public String testgroupid;
   public String testid;
   public String testtitle;
   public String testdesc;
   public String testnote;
   public String lastname;
   public String firstname;
   public Date birthdate;
   public String gendercd;
   public String createby;
   public Date moddate;
   public Date basedate;
   public String testresult;
   public String resultnotes;
   public CShotList shotlist;
   public CNonadList nonadmlist;
   public CTextList ereslist;
   public CDoseList edoselist;
   public CEvalList evallist;
   public CDosevList dosevlist;
   
   protected SimpleDateFormat dtfmt;

   
   /** Creates a new instance of CTestItem */
   public CTestItem()
   {
      testgroupid = "";
      testid = "";
      testtitle = "";
      testdesc = "";
      testnote = "";
      lastname = "";
      firstname = "";
      birthdate = new Date(0);
      birthdate = new Date(birthdate.getTime()-2*365*24*60*60*1000);
      gendercd = "";
      createby = "";
      moddate = new Date();
      basedate = new Date();
      testresult = CConsts.StatusNone;
      resultnotes = "";
      
      shotlist = new CShotList();
      nonadmlist = new CNonadList();
      ereslist = new CTextList();
      edoselist = new CDoseList();
      evallist = new CEvalList();
      dosevlist = new CDosevList();
      
      dtfmt = new SimpleDateFormat(CConsts.DateFmtStr);
   }

   public String makeKey()
   {
      String retstr = testgroupid + "|" + testid;
      return(retstr);
   }
   
   public String getYmdStr(Date adate)
   {
      SimpleDateFormat ymdfmt = new SimpleDateFormat(CConsts.DateFmtYmd);
      return(ymdfmt.format(adate));
   }
   public String getBirthDateStr()
   {
      if (birthdate.getTime() == 0) return("");
      return(dtfmt.format(birthdate));
   }
   public void setBirthDate(String aval) throws Exception
   {
      birthdate = dtfmt.parse(aval);
   }

   public String getModDateStr()
   {
      if (moddate.getTime() == 0) return("");
      return(dtfmt.format(moddate));
   }
   public void setModDate()
   {
      moddate = new Date();
   }
   
   public String getBaseDateStr()
   {
      if (basedate.getTime() == 0) return("");
      return(dtfmt.format(basedate));
   }
   public void setBaseDate(String aval) throws Exception
   {
      basedate = dtfmt.parse(aval);
   }
   
   public void updateBaseDate(String aoldstr, String anewstr) throws Exception
   {
      Date olddate = dtfmt.parse(aoldstr);
      Date newdate = dtfmt.parse(anewstr);
      long datdif = newdate.getTime() - olddate.getTime();
      birthdate.setTime(birthdate.getTime() + datdif);
      
      shotlist.modifyDates(birthdate);
      nonadmlist.modifyDates(datdif);
      edoselist.modifyDates(datdif);
   }
   
   
   public void dbReadItem(Connection aconn, String agroup, String atest)
   {
      try
      {
         testgroupid = agroup;
         testid = atest;
         String qstr = "Select TestTitle,TestDesc,TestNotes," +
               "LastName,FirstName,BirthDate,GenderCd,CreateBy,ModDate,BaseDate,TestResult,ResultNotes" +
               " From TestCaseTbl" +
               " Where TestGroupId='" + agroup + "' and TestId='" + atest + "'";
         Statement qstmt = aconn.createStatement();
         ResultSet rset = qstmt.executeQuery(qstr);

         if (rset.next())
         {
            testtitle = rset.getString(1);
            testdesc = rset.getString(2);
            testnote = rset.getString(3);
            lastname = rset.getString(4);
            firstname = rset.getString(5);
            birthdate = rset.getDate(6);
            gendercd = rset.getString(7);
            createby = rset.getString(8);
            moddate = rset.getDate(9);
            basedate = rset.getDate(10);
            testresult = rset.getString(11);
            resultnotes = rset.getString(12);
         }
         rset.close();
         qstmt.close();
         
      }
      catch (Exception ex)
      {
         CLogError.logError(CConsts.ErrMsgFile, false, "CTestItem.dbReadItem cannot read item. ", ex);
      }
   }
      
   public void dbDeleteItem(Connection aconn)
   {
      try
      {
         String qstr = "Delete From TestCaseTbl Where TestGroupId='" + testgroupid + "' and TestId='" + testid + "'";
         Statement qstmt = aconn.createStatement();
         qstmt.executeUpdate(qstr);
         qstmt.close();
      }
      catch (Exception ex)
      {
         CLogError.logError(CConsts.ErrMsgFile, false, "CTestItem.dbDeleteItem cannot delete item. ", ex);
      }
   }
   
   public void dbWriteItem(Connection aconn)
   {
      try
      {
         dbDeleteItem(aconn);
         String qstr = "Insert into TestCaseTbl (TestGroupId,TestId,TestTitle,TestDesc,TestNotes," +
               "LastName,FirstName,BirthDate,GenderCd,CreateBy,ModDate,BaseDate,TestResult,ResultNotes) Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
         PreparedStatement stmt = aconn.prepareStatement(qstr);

         setModDate();
         
         stmt.setString(1, testgroupid);
         stmt.setString(2, testid);
         stmt.setString(3, testtitle);
         stmt.setString(4, testdesc);
         stmt.setString(5, testnote);
         stmt.setString(6, lastname);
         stmt.setString(7, firstname);
         stmt.setDate(8, new java.sql.Date(birthdate.getTime()));
         stmt.setString(9, gendercd);
         stmt.setString(10, createby);
         stmt.setDate(11, new java.sql.Date(moddate.getTime()));
         stmt.setDate(12, new java.sql.Date(basedate.getTime()));
         stmt.setString(13, testresult);
         stmt.setString(14, resultnotes);
         stmt.executeUpdate();
         
         stmt.close();
      }
      catch (Exception ex)
      {
         CLogError.logError(CConsts.ErrMsgFile, false, "CTestItem.dbWriteItem cannot write item. ", ex);
      }
   }
   
   public void dbReadDetail(Connection aconn)
   {
      shotlist.dbReadList(aconn, testgroupid, testid);
      nonadmlist.dbReadList(aconn, testgroupid, testid);
      ereslist.dbReadList(aconn, testgroupid, testid);
      edoselist.dbReadList(aconn, testgroupid, testid);
      evallist.dbReadList(aconn, testgroupid, testid);
      dosevlist.dbReadList(aconn, testgroupid, testid);
   }
   
   public void dbWriteDetail(Connection aconn)
   {
      shotlist.dbWriteList(aconn, testgroupid, testid);
      nonadmlist.dbWriteList(aconn, testgroupid, testid);
      ereslist.dbWriteList(aconn, testgroupid, testid);
      edoselist.dbWriteList(aconn, testgroupid, testid);
      evallist.dbWriteList(aconn, testgroupid, testid);
      dosevlist.dbWriteList(aconn, testgroupid, testid);
   }
   
   public String getStatusStyle(String astatus)
   {
      if (CConsts.StatusPass.equals(astatus)) return(CConsts.StylePass);
      if (CConsts.StatusFail.equals(astatus)) return(CConsts.StyleFail);
      return(CConsts.StyleNone);
   }
   
   public void setTestStatus()
   {
      testresult = edoselist.getTestStatus();
   }

   public static String showStatusHead()
   {
      String retstr = "<tr>\n";
      retstr = retstr + "<th class='result'>Test Case</th>\n";
      retstr = retstr + "<th class='result'>Status</th>\n";
      retstr = retstr + "<th class='result'>View Details</th>\n";
      retstr = retstr + "<th class='result'>Edit Case</th>\n";
      retstr = retstr + "<th class='result'>Run Case</th>\n";
      retstr = retstr + "</tr>\n";
      
      return(retstr);
   }

   public String showStatus()
   {
      String retstr = "<tr>\n";
      retstr = retstr + "<td class='result'>" + testtitle + "</td>\n";
      String stylestr = getStatusStyle(testresult);
      
      retstr = retstr + "<td class='" + stylestr + "'>" + testresult + "</td>\n";

      String btnid = "Detail" + testid;
      retstr = retstr + "<td class='result'><a OnMouseDown='javascript:SwapBtn(\"" + btnid + "\",\"DetailsDn\")' " + 
                               "OnMouseUp='javascript:SwapBtn(\"" + btnid + "\",\"DetailsUp\")' " +
                               "HREF='javascript:DoSubmit(\"StatusForm\",\"" + btnid + "\")'>" +
                             "<img class='btnimg' id='" + btnid + "' name='" + btnid + "' " +
                                  "alt='Detail button' src='images/DetailsUp.gif'></a></td>\n";
      btnid = "Edit" + testid;
      retstr = retstr + "<td class='result'><a OnMouseDown='javascript:SwapBtn(\"" + btnid + "\",\"EditDn\")' " + 
                               "OnMouseUp='javascript:SwapBtn(\"" + btnid + "\",\"EditUp\")' " +
                               "HREF='javascript:DoSubmit(\"StatusForm\",\"" + btnid + "\")'>" +
                             "<img class='btnimg' id='" + btnid + "' name='" + btnid + "' " +
                                  "alt='Edit button' src='images/EditUp.gif'></a></td>\n";
      
      btnid = "Execute" + testid;
      retstr = retstr + "<td class='result'><a OnMouseDown='javascript:SwapBtn(\"" + btnid + "\",\"ExecuteDn\")' " + 
                               "OnMouseUp='javascript:SwapBtn(\"" + btnid + "\",\"ExecuteUp\")' " +
                               "HREF='javascript:DoSubmit(\"StatusForm\",\"" + btnid + "\")'>" +
                             "<img class='btnimg' id='" + btnid + "' name='" + btnid + "' " +
                                  "alt='Execute button' src='images/ExecuteUp.gif'></a></td>\n";
      retstr = retstr + "</tr>\n";
      return(retstr);
   }

   public String showEdit(Connection aconn)
   {
      CCodeDesc genders = new CCodeDesc(aconn, "GenderTbl","GenderCd","GenderNm","GenderSrt");
      String retstr = "<dt class='details'>Test Id</dt>\n";
      retstr = retstr + "<dd class='details'>" + testid + "</dd>\n";

      retstr = retstr + "<dt class='details'><label for='TestTitle'>Test Name</label></dt>\n";
      retstr = retstr + "<dd class='details'><input type='text' name='TestTitle' id='TestTitle'" +
            " maxlength=" + Integer.toString(CConsts.MaxLenTitle) + " value='" + testtitle + "'></dd>\n";
      retstr = retstr + "<dt class='details'><label for='CreateBy'>Author</label></dt>\n";
      retstr = retstr + "<dd class='details'><input type='text' name='CreateBy' id='CreateBy'" +
            " maxlength=" + Integer.toString(CConsts.MaxLenName) + " value='" + createby + "'></dd>\n";
      retstr = retstr + "<dt class='details'>Modified Date</dt><dd class='details'>" + getModDateStr() + "</dd>\n";
      retstr = retstr + "<dt class='details'><label for='TestDesc'>Description</label></dt>\n";
      retstr = retstr + "<dd class='details'><textarea name='TestDesc' id='TestDesc' cols=80 rows=3" +
            " onkeyup='javascript:EnforceMaxLen(this," + Integer.toString(CConsts.MaxLenNote) + ")'" +
            ">" + testdesc + "</textarea></dd>\n";
      retstr = retstr + "<dt class='details'><label for='TestNote'>Notes</label></dt>\n";
      retstr = retstr + "<dd class='details'><textarea name='TestNote' id='TestNote' cols=80 rows=3" +
            " onkeyup='javascript:EnforceMaxLen(this," + Integer.toString(CConsts.MaxLenNote) + ")'" +
            ">" + testnote + "</textarea></dd>\n";
      retstr = retstr + "<dt class='details'><label for='BaseDate'>Base Date</label></dt>\n";
      retstr = retstr + "<dd class='details'><input type='text' name='BaseDate' id='BaseDate'" +
            " maxlength=" + Integer.toString(CConsts.MaxLenDate) + " value='" + getBaseDateStr() + "'>" +
            "<input type='hidden' name='HideDate' id='HideDate' value='" + getBaseDateStr() + "'>" +
            " <a OnMouseDown='javascript:SwapBtn(\"Scale\",\"ScaleDn\")' " + 
                 "OnMouseUp='javascript:SwapBtn(\"Scale\",\"ScaleUp\")' " +
                 "HREF='javascript:DoSubmit(\"EditForm\",\"Scale\")'>" +
                 "<img class='btnimg' id='Scale' name='Scale' " +
                 "alt='Edit button' src='images/ScaleUp.gif'></a>" +
            "</dd>\n";
      retstr = retstr + "<dt class='details'>Child Information</dt>\n";
      retstr = retstr + "<dd class='details'>\n";
      retstr = retstr + "<table class='factors' summary='child information'>\n";
      retstr = retstr + "<tr>\n";
      retstr = retstr + "<th class='factors' scope='col'><label for='FirstName'>First Name</label></th>\n";
      retstr = retstr + "<th class='factors' scope='col'><label for='LastName'>Last Name</label></th>\n";
      retstr = retstr + "<th class='factors' scope='col'><label for='BirthDate'>Date of Birth</label></th>\n";
      retstr = retstr + "<th class='factors' scope='col'><label for='Gender'>Gender</label></th>\n";
      retstr = retstr + "</tr>\n";
      retstr = retstr + "<tr>\n";
      retstr = retstr + "<td class='edits'><input type='text' name='FirstName' id='FirstName'" +
            " maxlength=" + Integer.toString(CConsts.MaxLenName) + " value='" + firstname + "'></td>\n";
      retstr = retstr + "<td class='edits'><input type='text' name='LastName' id='LastName'" +
            " maxlength=" + Integer.toString(CConsts.MaxLenName) + " value='" + lastname + "'></td>\n";
      retstr = retstr + "<td class='edits'><input type='text' name='BirthDate' id='BirthDate'" +
            " maxlength=" + Integer.toString(CConsts.MaxLenDate) + " value='" + getBirthDateStr() + "'></td>\n";
      retstr = retstr + "<td class='edits'><select name='Gender' id='Gender' size=1>\n" +
                        genders.makeOptions(gendercd) + "</select></td>\n";
      retstr = retstr + "</tr>\n";

      retstr = retstr + "</table></dd>\n";

      retstr = retstr + shotlist.showEdit(aconn);
      retstr = retstr + nonadmlist.showEdit(aconn);
      retstr = retstr + ereslist.showEdit(aconn);
      retstr = retstr + edoselist.showEdit(aconn);
      retstr = retstr + evallist.showEdit(aconn);
      retstr = retstr + dosevlist.showEdit(aconn);
      
      retstr = retstr + "<dt class='details'><label for='ResNotes'>Result Notes</label></dt>\n";
      retstr = retstr + "<dd class='details'><textarea name='ResNotes' id='ResNotes' cols=80 rows=3" +
            " onkeyup='javascript:EnforceMaxLen(this," + Integer.toString(CConsts.MaxLenNote) + ")'" +
            ">" + resultnotes + "</textarea></dd>\n";
      
      return(retstr);
   }

   public void updateItem(HttpServletRequest arequest) throws Exception
   {
      String group = arequest.getParameter("TestGroup");
      if (group != null) testgroupid = group;
        
      testtitle = CParser.truncStr(arequest.getParameter("TestTitle"), CConsts.MaxLenTitle);
      createby = CParser.truncStr(arequest.getParameter("CreateBy"), CConsts.MaxLenName);
      testdesc = CParser.truncStr(arequest.getParameter("TestDesc"), CConsts.MaxLenNote);
      testnote = CParser.truncStr(arequest.getParameter("TestNote"), CConsts.MaxLenNote);
      String datstr = CParser.truncStr(arequest.getParameter("BaseDate"), CConsts.MaxLenDate);
      setBaseDate(datstr);
      firstname = CParser.truncStr(arequest.getParameter("FirstName"), CConsts.MaxLenName);
      lastname = CParser.truncStr(arequest.getParameter("LastName"), CConsts.MaxLenName);
      datstr = CParser.truncStr(arequest.getParameter("BirthDate"), CConsts.MaxLenDate);
      setBirthDate(datstr);
      gendercd = arequest.getParameter("Gender");
      resultnotes = CParser.truncStr(arequest.getParameter("ResNotes"), CConsts.MaxLenNote);
      
      shotlist.updateItem(arequest, this.birthdate);
      nonadmlist.updateItem(arequest);
      ereslist.updateItem(arequest);
      edoselist.updateItem(arequest);
   }

   public String showDisplay(Connection aconn)
   {
      CCodeDesc genders = new CCodeDesc(aconn, "GenderTbl","GenderCd","GenderNm","GenderSrt");
      
      String retstr = "<dt class='details'>Test Id</dt><dd class='details'>" + testid + "</dd>\n";
      retstr = retstr + "<dt class='details'>Test Name</dt><dd class='details'>" + testtitle + "</dd>\n";
      retstr = retstr + "<dt class='details'>Author</dt><dd class='details'>" + createby + "</dd>\n";
      retstr = retstr + "<dt class='details'>Modified Date</dt><dd class='details'>" + getModDateStr() + "</dd>\n";

      String tdesc = testdesc;
      if (testdesc.length() < 1) tdesc = "&nbsp;";
      retstr = retstr + "<dt class='details'>Description</dt><dd class='details'>" + tdesc + "</dd>\n";

      String tnote = testnote;
      if (testnote.length() < 1) tnote = "&nbsp;";
      retstr = retstr + "<dt class='details'>Notes</dt><dd class='details'>" + tnote + "</dd>\n";

      retstr = retstr + "<dt class='details'>Base Date</dt><dd class='details'>" + getBaseDateStr() + "</dd>\n";
      retstr = retstr + "<dt class='details'>Child Information</dt>\n";
      retstr = retstr + "<dd class='details'>\n";
      retstr = retstr + "<table class='factors' summary='child information'>\n";
      retstr = retstr + "<tr>\n";
      retstr = retstr + "<th class='factors' scope='col'>First Name</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Last Name</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Date of Birth</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Gender</th>\n";
      retstr = retstr + "</tr>\n";
      retstr = retstr + "<tr>\n";
      retstr = retstr + "<td class='factors'>" + firstname + "</td>\n";
      retstr = retstr + "<td class='factors'>" + lastname + "</td>\n";
      retstr = retstr + "<td class='factors'>" + getBirthDateStr() + "</td>\n";
      retstr = retstr + "<td class='factors'>" + genders.getDescByCode(gendercd)  + "</td></tr>\n";
      retstr = retstr + "</table></dd>\n";

      retstr = retstr + shotlist.showDisplay(aconn, this.birthdate);
      retstr = retstr + nonadmlist.showDisplay(aconn);
      retstr = retstr + ereslist.showDisplay(aconn);
      retstr = retstr + edoselist.showDisplay(aconn);
      retstr = retstr + evallist.showDisplay(aconn);
      retstr = retstr + dosevlist.showDisplay(aconn);

      String rnote = resultnotes;
      if (resultnotes.length() < 1) rnote = "&nbsp;";
      retstr = retstr + "<dt class='details'>Result Notes</dt><dd class='details'>" + rnote + "</dd>\n";
      return(retstr);
   }
}