/*
 * CTestList.java
 *
 * Created on July 15, 2008, 5:34 PM
 */

package testcase;

import manapp.*;

import java.sql.*;

/** list of test cases */
public class CTestList extends CStringList
{
   
   /** Creates a new instance of CTestList */
   public CTestList()
   {
      super(true);
   }
   
   public void dbReadList(Connection aconn, String agroup)
   {
      try
      {
         String qstr = "Select TestId,TestTitle,TestDesc,TestNotes," +
               "LastName,FirstName,BirthDate,GenderCd,CreateBy,ModDate,BaseDate,TestResult,ResultNotes" +
               " From TestCaseTbl" +
               " Where TestGroupId='" + agroup + "'";
         Statement qstmt = aconn.createStatement();
         ResultSet rset = qstmt.executeQuery(qstr);

         while (rset.next())
         {
            CTestItem myitem = new CTestItem();
            myitem.testgroupid = agroup;
            myitem.testid = rset.getString(1);
            myitem.testtitle = rset.getString(2);
            myitem.testdesc = rset.getString(3);
            myitem.testnote = rset.getString(4);
            myitem.lastname = rset.getString(5);
            myitem.firstname = rset.getString(6);
            myitem.birthdate = rset.getDate(7);
            myitem.gendercd = rset.getString(8);
            myitem.createby = rset.getString(9);
            myitem.moddate = rset.getDate(10);
            myitem.basedate = rset.getDate(11);
            myitem.testresult = rset.getString(12);
            myitem.resultnotes = rset.getString(13);
            
            this.addItem(myitem.makeKey(), myitem);
         }
         rset.close();
         qstmt.close();
      }
      catch (Exception ex)
      {
         CLogError.logError(CAppConsts.ErrorFile, false, "CTestList.dbReadList cannot read list. ", ex);
      }
   }
   
   public String showStatus()
   {
      String retstr = "<table class='result' summary='test case status'>\n";
      retstr = retstr + CTestItem.showStatusHead();
      for (int itst = 0; itst < this.getCount(); itst++)
      {
         CTestItem myitem = (CTestItem) this.getItem(itst);
         retstr = retstr + myitem.showStatus();
      }
      retstr = retstr + "</table>\n";
      return(retstr);
   }
   
   public int getStatusCnt(String astat)
   {
      int statcnt = 0;
      for (int itst = 0; itst < this.getCount(); itst++)
      {
         CTestItem myitem = (CTestItem) this.getItem(itst);
         if (myitem.testresult.equals(astat)) statcnt++;
      }
      return(statcnt);
   }
}
