package testcase;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

import manapp.CAppConsts;

public class CParseXml extends DefaultHandler
{
   protected String testgroupid;
   protected CTestList testcaselist;
   protected CTestItem testcaseitem;
   protected CShotItem shotitem;
   protected CNonadItem nonaditem;
   protected CTextItem expresitem;
   protected CDoseItem expdoseitem;
   protected String curstr;
   protected SimpleDateFormat dtfmt;

   public CParseXml()
   {
      super();
      curstr = "";
      testcaselist = new CTestList();
      testcaseitem = new CTestItem();
      shotitem = new CShotItem();
      nonaditem = new CNonadItem();
      expresitem = new CTextItem();
      expdoseitem = new CDoseItem();
      dtfmt = new SimpleDateFormat(CAppConsts.DateFmtStr);
   }

   public void setTestGroup(String agrpid)
   {
      testgroupid = agrpid;
   }

   public CTestList getTestList()
   {
      return(testcaselist);
   }

   public Date parseDate(String adstr)
   {
      Date mydate = new Date(0);
      try
      {
         mydate = dtfmt.parse(adstr);
      }
      catch (Exception ex)
      {}
      return(mydate);
   }

   public void characters(char[] ch, int start, int length) throws SAXException
   {
      curstr = new String(ch, start, length);
   }

   public void endElement(String uri, String localName, String atagname) throws SAXException
   {
      if (atagname.equals("TestCaseItem"))
      {
         String mykey = testcaselist.makeNewId(testgroupid, "test", 8);
         testcaseitem.testgroupid = testgroupid;
         testcaseitem.testid = CParser.getRemnant(mykey, "|");
//System.err.println("endElement: TestCaseItem " + mykey);
         testcaselist.addItem(mykey, testcaseitem);
         testcaseitem = new CTestItem();
         testcaseitem.testgroupid = testgroupid;
      }

      else if (atagname.equals("ShotItem"))
      {
//System.err.println("endElement: ShotItem");
         shotitem.shotid = testcaseitem.shotlist.makeNewId("sht", 3);
         testcaseitem.shotlist.addItem(shotitem.shotid, shotitem);
         shotitem = new CShotItem();
      }

      else if (atagname.equals("NonAdminItem"))
      {
//System.err.println("endElement: NonAdminItem");
         nonaditem.nonadmid = testcaseitem.nonadmlist.makeNewId("nad", 6);
         testcaseitem.nonadmlist.addItem(nonaditem.nonadmid, nonaditem);
         nonaditem = new CNonadItem();
      }

      else if (atagname.equals("ExpectResultItem"))
      {
//System.err.println("endElement: ExpectResultItem");
         expresitem.expectid = testcaseitem.ereslist.makeNewId("ert", 6);
         testcaseitem.ereslist.addItem(expresitem.expectid, expresitem);
         expresitem = new CTextItem();
      }

      else if (atagname.equals("ExpectDoseItem"))
      {
//System.err.println("endElement: ExpectDoseItem");
         expdoseitem.doseid = testcaseitem.edoselist.makeNewId("dos", 6);
         testcaseitem.edoselist.addItem(expdoseitem.doseid, expdoseitem);
         expdoseitem = new CDoseItem();
      }

      else if (atagname.equals("TestTitle")) testcaseitem.testtitle = curstr;
      else if (atagname.equals("TestDesc")) testcaseitem.testdesc = curstr;
      else if (atagname.equals("TestNotes")) testcaseitem.testnote = curstr;
      else if (atagname.equals("CreateBy")) testcaseitem.createby = curstr;
      else if (atagname.equals("BaseDate")) testcaseitem.basedate = parseDate(curstr);
      else if (atagname.equals("LastName")) testcaseitem.lastname = curstr;
      else if (atagname.equals("FirstName")) testcaseitem.firstname = curstr;
      else if (atagname.equals("BirthDate")) testcaseitem.birthdate = parseDate(curstr);
      else if (atagname.equals("GenderCd")) testcaseitem.gendercd = curstr;

      else if (atagname.equals("ShotDate")) shotitem.shotdate = parseDate(curstr);
      else if (atagname.equals("VaccineCd")) shotitem.vaccinecd = curstr;
      else if (atagname.equals("MfrCd")) shotitem.mfrcd = curstr;
//      else if (atagname.equals("RefType")) shotitem.reftype = curstr;
//      else if (atagname.equals("PeriodType")) shotitem.periodtype = curstr;
//      else if (atagname.equals("PeriodAmt")) shotitem.periodamt = Integer.parseInt(curstr);
//      else if (atagname.equals("PeriodOff")) shotitem.periodoff = Integer.parseInt(curstr);

      else if (atagname.equals("NonadmDate")) nonaditem.nonadmdate = parseDate(curstr);
      else if (atagname.equals("AntSeriesCd")) nonaditem.seriescd = curstr;
      else if (atagname.equals("ReasonCd")) nonaditem.reasoncd = curstr;

      else if (atagname.equals("ExpectTxt")) expresitem.expecttxt = curstr;
      else if (atagname.equals("ResultTxt")) expresitem.resulttxt = curstr;

      else if (atagname.equals("SeriesCd")) expdoseitem.seriescd = curstr;
      else if (atagname.equals("ResultCd")) expdoseitem.resultcd = curstr;
      else if (atagname.equals("NextDoseNum")) expdoseitem.doseord = Integer.parseInt(curstr);
      else if (atagname.equals("AccelDate")) expdoseitem.acceldate = parseDate(curstr);
      else if (atagname.equals("RecomDate")) expdoseitem.recomdate = parseDate(curstr);
      else if (atagname.equals("OverdueDate")) expdoseitem.overduedate = parseDate(curstr);
      curstr = "";
   }
}
