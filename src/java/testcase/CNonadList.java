/*
 * CNonadList.java
 *
 * Created on July 17, 2008, 6:18 PM
 */

package testcase;

import manapp.*;

import java.sql.*;
import javax.servlet.http.*;

/** list of non-administered vaccine items */
public class CNonadList extends CStringList
{

   /** Creates a new instance of CNonadList */
   public CNonadList()
   {
      super(true);
   }

   public void dbReadList(Connection aconn, String agroup, String atest)
   {
      try
      {
         String qstr = "Select NonAdmId,NonAdmDate,AntSeriesCd,ReasonCd,VacAgeYrs,VacAgeMos,VacAgeWks,VacAgeDays" +
               " From TNonAdminTbl" +
               " Where TestGroupId='" + agroup + "' and TestId='" + atest + "'";
         Statement qstmt = aconn.createStatement();
         ResultSet rset = qstmt.executeQuery(qstr);

         while (rset.next())
         {
            CNonadItem myitem = new CNonadItem();
            myitem.nonadmid = rset.getString(1);
            myitem.nonadmdate = rset.getDate(2);
            myitem.seriescd = rset.getString(3);
            myitem.reasoncd = rset.getString(4);
            myitem.nageyears = rset.getInt(5);
            myitem.nagemonths = rset.getInt(6);
            myitem.nageweeks = rset.getInt(7);
            myitem.nagedays = rset.getInt(8);
            this.addItem(myitem.nonadmid, myitem);
         }
         rset.close();
         qstmt.close();

      }
      catch (Exception ex)
      {
         CLogError.logError(CAppConsts.ErrorFile, false, "CNonadList.dbReadList cannot read list. ", ex);
      }
   }

   public void dbDeleteList(Connection aconn, String agroup, String atest)
   {
      try
      {
         String qstr = "Delete From TNonAdminTbl Where TestGroupId='" + agroup + "' and TestId='" + atest + "'";
         Statement qstmt = aconn.createStatement();
         qstmt.executeUpdate(qstr);
         qstmt.close();
      }
      catch (Exception ex)
      {
         CLogError.logError(CAppConsts.ErrorFile, false, "CNonadList.dbDeleteList cannot delete list. ", ex);
      }
   }

   public void dbWriteList(Connection aconn, String agroup, String atest)
   {
      try
      {
         dbDeleteList(aconn, agroup, atest);
         String qstr = "Insert into TNonAdminTbl (TestGroupId,TestId,NonAdmId,NonAdmDate,AntSeriesCd,ReasonCd,VacAgeYrs,VacAgeMos,VacAgeWks,VacAgeDays)" +
               " Values (?,?,?,?,?,?,?,?,?,?)";
         PreparedStatement stmt = aconn.prepareStatement(qstr);

         for (int idx = 0; idx < this.getCount(); idx++)
         {
            CNonadItem myitem = (CNonadItem) this.getItem(idx);

            stmt.setString(1, agroup);
            stmt.setString(2, atest);
            stmt.setString(3, myitem.nonadmid);
            stmt.setDate(4, new java.sql.Date(myitem.nonadmdate.getTime()));
            stmt.setString(5, myitem.seriescd);
            stmt.setString(6, myitem.reasoncd);
            stmt.setInt(7, myitem.nageyears);
            stmt.setInt(8, myitem.nagemonths);
            stmt.setInt(9, myitem.nageweeks);
            stmt.setInt(10, myitem.nagedays);
            stmt.executeUpdate();
         }

         stmt.close();
      }
      catch (Exception ex)
      {
         CLogError.logError(CAppConsts.ErrorFile, false, "CNonadList.dbWriteList cannot write list. ", ex);
      }
   }

   public void modifyDates(long adiff)
   {
      for (int idx = 0; idx < this.getCount(); idx++)
      {
         CNonadItem myitem = (CNonadItem) this.getItem(idx);
         myitem.nonadmdate.setTime(myitem.nonadmdate.getTime() + adiff);
      }
   }

   public String showEdit(Connection aconn)
   {
      CCodeDesc series = new CCodeDesc(aconn, "SeriesTbl","SeriesCd","SeriesNm","SeriesCd");
      CCodeDesc reasons = new CCodeDesc(aconn, "NonAdmReasTbl","ReasonCd","ReasonNm","ReasonSrt");

      String retstr = " <table class=\"midtitle\" >" ;
      retstr = retstr + "<tr>" ;
      retstr = retstr + "<td class=\"midtitleleft\" >&nbsp;&nbsp;Non-Administrations</td> </tr> </table>";

      retstr = retstr + "<dd class='details'>\n";
      retstr = retstr + "<table class='factors' summary='non-administrations'>\n";
      retstr = retstr + "<tr>\n";
      retstr = retstr + "<th class='factors' scope='col'>Reason</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Series</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Yrs</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Mos</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Wks</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Days</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Date</th>\n";
      retstr = retstr + "</tr>\n";
      int ivac = 0;
      for (int idx = 0; idx < this.getCount(); idx++)
      {
         ivac++;
         CNonadItem myitem = (CNonadItem) this.getItem(idx);
         String datid = "Ndate" + myitem.nonadmid;
         String serid = "NonSeries" + myitem.nonadmid;
         String reaid = "NonReason" + myitem.nonadmid;
         String nyrsid = "Nyrs" + myitem.nonadmid;
         String nmosid = "Nmos" + myitem.nonadmid;
         String nwksid = "Nwks" + myitem.nonadmid;
         String ndaysid = "Ndys" + myitem.nonadmid;

         retstr = retstr + "<tr> <td class='edits'>";
         retstr = retstr + "<label class='hidden' for='"+reaid+"'>Reason for non-administration " + Integer.toString(ivac) + "</label>" +
               "<select name='"+reaid+"' id='"+reaid+"' size=1>\n";
         retstr = retstr + "<option value='"+CAppConsts.TagNoValue+"'>" + CAppConsts.TagNoLabel + "</option>\n";
         retstr = retstr + reasons.makeOptions(myitem.reasoncd);
         retstr = retstr + "</select></td>\n";

         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+serid+"'>Series for non-administration " + Integer.toString(ivac) + "</label>" +
               "<select name='"+serid+"' id='"+serid+"' size=1>\n";
         retstr = retstr + "<option value='"+CAppConsts.TagNoValue+"'>" + CAppConsts.TagNoLabel + "</option>\n";
         retstr = retstr + series.makeOptions(myitem.seriescd);
         retstr = retstr + "</select></td>\n";
         retstr = retstr + "<td class='edits'>" +
             "<label class='hidden' for='"+nyrsid+"'>Year value for age at non-administration " + Integer.toString(ivac) + "</label>" +
             "<input type='text' name='"+nyrsid+"' id='"+nyrsid+"' size=4 onchange=\"javascript:setCalcNonDate(this.name)\"" +
             " maxlength=4 value='" + myitem.nageyears +  "'></td>\n";
         retstr = retstr + "<td class='edits'>" +
             "<label class='hidden' for='"+nmosid+"'>Months value for age at non-administration " + Integer.toString(ivac) + "</label>" +
             "<input type='text' name='"+nmosid+"' id='"+nmosid+"' size=4 onchange=\"javascript:setCalcNonDate(this.name)\"" +
             " maxlength=4 value='" + myitem.nagemonths +  "'></td>\n";
         retstr = retstr + "<td class='edits'>" +
             "<label class='hidden' for='"+nwksid+"'>Weeks value for age at non-administration " + Integer.toString(ivac) + "</label>" +
             "<input type='text' name='"+nwksid+"' id='"+nwksid+"' size=4 onchange=\"javascript:setCalcNonDate(this.name)\"" +
             " maxlength=4 value='" + myitem.nageweeks +  "'></td>\n";
         retstr = retstr + "<td class='edits'>" +
             "<label class='hidden' for='"+ndaysid+"'>Days value for age at non-administration " + Integer.toString(ivac) + "</label>" +
             "<input type='text' name='"+ndaysid+"' id='"+ndaysid+"' size=4 onchange=\"javascript:setCalcNonDate(this.name)\"" +
             " maxlength=4 value='" + myitem.nagedays +  "'></td>\n";

         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+datid+"'>Date for non-administration " + Integer.toString(ivac) + "</label>" +
               "<input type='text' name='"+datid+"' id='"+datid+"' size=10 onchange=\"javascript:setCalcNonAge(this.name)\"" +
               " maxlength=" + Integer.toString(CAppConsts.MaxLenDate) + " value='" + myitem.getNonadmDateStr() + "'></td>\n";
         retstr = retstr + "</tr>";
      }

      //int nslot = Math.max(CAppConsts.NewSlotNonAdmin, CAppConsts.NumSlotNonAdmin - getCount());
      int nslot = (this.getCount()==0)?2:1;  //show 2 lines if nothing is there, else show an extra line for entry
     
      for (int idx = 0; idx < nslot; idx++)
      {
         ivac++;
         String myid = "New" + Integer.toString(idx);
         String serid = "NonSeries" + myid;
         String reaid = "NonReason" + myid;
         String datid = "Ndate" + myid;
         String nyrsid = "Nyrs" + myid;
         String nmosid = "Nmos" + myid;
         String nwksid = "Nwks" + myid;
         String ndaysid = "Ndys" + myid;

         retstr = retstr + "<tr><td class='edits'>";
         retstr = retstr + "<label class='hidden' for='"+reaid+"'>Reason for non-administration " + Integer.toString(ivac) + "</label>" +
               "<select name='"+reaid+"' id='"+reaid+"' size=1>\n";
         retstr = retstr + "<option value='"+CAppConsts.TagNoValue+"'>" + CAppConsts.TagNoLabel + "</option>\n";
         retstr = retstr + reasons.makeOptions(CAppConsts.TagNoValue);
         retstr = retstr + "</select></td>\n";

         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+serid+"'>Series for non-administration " + Integer.toString(ivac) + "</label>" +
               "<select name='"+serid+"' id='"+serid+"' size=1>\n";
         retstr = retstr + "<option value='"+CAppConsts.TagNoValue+"'>" + CAppConsts.TagNoLabel + "</option>\n";
         retstr = retstr + series.makeOptions(CAppConsts.TagNoValue);
         retstr = retstr + "</select></td>\n";
         retstr = retstr + "<td class='edits'>" +
             "<label class='hidden' for='"+nyrsid+"'>Year value for age at non-administration " + Integer.toString(ivac) + "</label>" +
             "<input type='text' name='"+nyrsid+"' id='"+nyrsid+"' size=4 onchange=\"javascript:setCalcNonDate(this.name)\"" +
             " maxlength=4 value=''></td>\n";
         retstr = retstr + "<td class='edits'>" +
             "<label class='hidden' for='"+nmosid+"'>Months value for age at non-administration " + Integer.toString(ivac) + "</label>" +
             "<input type='text' name='"+nmosid+"' id='"+nmosid+"' size=4 onchange=\"javascript:setCalcNonDate(this.name)\"" +
             " maxlength=4 value=''></td>\n";
         retstr = retstr + "<td class='edits'>" +
             "<label class='hidden' for='"+nwksid+"'>Weeks value for age at non-administration " + Integer.toString(ivac) + "</label>" +
             "<input type='text' name='"+nwksid+"' id='"+nwksid+"' size=4 onchange=\"javascript:setCalcNonDate(this.name)\"" +
             " maxlength=4 value=''></td>\n";
         retstr = retstr + "<td class='edits'>" +
             "<label class='hidden' for='"+ndaysid+"'>Days value for age at non-administration " + Integer.toString(ivac) + "</label>" +
             "<input type='text' name='"+ndaysid+"' id='"+ndaysid+"' size=4 onchange=\"javascript:setCalcNonDate(this.name)\"" +
             " maxlength=4 value=''></td>\n";
        
         retstr = retstr + "<td class='edits'>" +
               "<label class='hidden' for='"+datid+"'>Date for non-administration " + Integer.toString(ivac) + "</label>" +
               "<input type='text' name='"+datid+"' id='"+datid+"' size=10 onchange=\"javascript:setCalcNonAge(this.name)\"" +
               " maxlength=" + Integer.toString(CAppConsts.MaxLenDate) + " value=''></td>\n";
       retstr = retstr + "</tr>";
  
       }
      retstr = retstr + "</table></dd>\n";
      return(retstr);
   }

   public void updateItem(HttpServletRequest arequest) throws Exception
   {
      for (int idx = this.getCount()-1; idx >= 0; idx--)
      {
         CNonadItem myitem = (CNonadItem) this.getItem(idx);
         String datid = "Ndate" + myitem.nonadmid;
         String serid = "NonSeries" + myitem.nonadmid;
         String reaid = "NonReason" + myitem.nonadmid;
         String nyrsid = "Nyrs" + myitem.nonadmid;
         String nmosid = "Nmos" + myitem.nonadmid;
         String nwksid = "Nwks" + myitem.nonadmid;
         String ndaysid = "Ndys" + myitem.nonadmid;
 
         String datstr = CParser.truncStr(arequest.getParameter(datid), CAppConsts.MaxLenDate);
         if (datstr == null || datstr.length() == 0)
         {
            this.delItem(idx);
            continue;
         }
         String serstr = arequest.getParameter(serid);
         String reastr = arequest.getParameter(reaid);

         myitem.setNonadmDate(datstr);
         myitem.seriescd = serstr;
         myitem.reasoncd = reastr;
         myitem.nageyears = getIntVal(arequest.getParameter(nyrsid));
         myitem.nagemonths = getIntVal(arequest.getParameter(nmosid));
         myitem.nageweeks = getIntVal(arequest.getParameter(nwksid));
         myitem.nagedays = getIntVal(arequest.getParameter(ndaysid));
      }

//      int nslot = Math.max(CAppConsts.NewSlotNonAdmin, CAppConsts.NumSlotNonAdmin - getCount());
      int nslot = (this.getCount()==0)?2:1;

      for (int idx = 0; idx < nslot; idx++)
      {
         CNonadItem myitem = new CNonadItem();
         String myid = "New" + Integer.toString(idx);
         String datid = "Ndate" + myid;
         String serid = "NonSeries" + myid;
         String reaid = "NonReason" + myid;
         String nyrsid = "Nyrs" + myid;
         String nmosid = "Nmos" + myid;
         String nwksid = "Nwks" + myid;
         String ndaysid = "Ndys" + myid;

         String datstr = CParser.truncStr(arequest.getParameter(datid), CAppConsts.MaxLenDate);
         if (datstr == null || datstr.length() == 0) continue; //no data here
         String serstr = arequest.getParameter(serid);
         String reastr = arequest.getParameter(reaid);

         myitem.nonadmid = this.makeNewId("nad", 6);
         myitem.setNonadmDate(datstr);
         myitem.seriescd = serstr;
         myitem.reasoncd = reastr;
         myitem.nageyears = getIntVal(arequest.getParameter(nyrsid));
         myitem.nagemonths = getIntVal(arequest.getParameter(nmosid));
         myitem.nageweeks = getIntVal(arequest.getParameter(nwksid));
         myitem.nagedays = getIntVal(arequest.getParameter(ndaysid));
         
         this.addItem(myitem.nonadmid, myitem);
      }
   }

   public String showDisplay(Connection aconn)
   {
      CCodeDesc series = new CCodeDesc(aconn, "SeriesTbl","SeriesCd","SeriesNm","SeriesCd");
      CCodeDesc reasons = new CCodeDesc(aconn, "NonAdmReasTbl","ReasonCd","ReasonNm","ReasonSrt");

      String retstr = " <table class=\"midtitle\" >" ;
      retstr = retstr + "<tr>" ;
      retstr = retstr + "<td class=\"midtitleleft\" >&nbsp;&nbsp;Non-Administrations</td> </tr> </table>";

      retstr = retstr + "<dd class='details'>\n";
      retstr = retstr + "<table class='factors' summary='non-administrations'>\n";
      retstr = retstr + "<tr>\n";
      retstr = retstr + "<th class='factors' scope='col'>Reason</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Series</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Yrs</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Mos</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Wks</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Days</th>\n";
      retstr = retstr + "<th class='factors' scope='col'>Date</th>\n";
      retstr = retstr + "</tr>\n";

      for (int idx = 0; idx < this.getCount(); idx++)
      {
         CNonadItem myitem = (CNonadItem) this.getItem(idx);
         retstr = retstr + "<tr>\n";
         retstr = retstr + "<td class='factors'>" + reasons.getDescByCode(myitem.reasoncd) + "</td>\n";
         retstr = retstr + "<td class='factors'>" + series.getDescByCode(myitem.seriescd) + "</td>\n";
         retstr = retstr + "<td class='factors'>" + myitem.nageyears + "</td>\n";
         retstr = retstr + "<td class='factors'>" + myitem.nagemonths + "</td>\n";
         retstr = retstr + "<td class='factors'>" + myitem.nageweeks + "</td>\n";
         retstr = retstr + "<td class='factors'>" + myitem.nagedays + "</td>\n";
         retstr = retstr + "<td class='factors'>" + myitem.getNonadmDateStr() + "</td>\n";
         retstr = retstr + "</tr>\n";
      }
      retstr = retstr + "</table></dd>\n";
      return(retstr);
   }

   public String buildWaiverStr(Connection aconn, int achild)
   {
      String retstr = "";
      CMapCode reasonmap = new CMapCode(aconn, "NonAdmReasTbl", "ReasonCd", "ExemptFld", CMapCode.TypeString);

      int shotnum = 1;
      for (int idx = 0; idx < getCount(); idx++)
      {
         CNonadItem myitem = (CNonadItem) this.getItem(idx);
         String exempt = reasonmap.mapCode(myitem.reasoncd);
         if (exempt.equals("Y"))
         {
            retstr = retstr + myitem.buildWaiverStr(aconn, achild, shotnum);
            shotnum++;
         }
      }
      return(retstr);
   }

   public String buildTiterStr(Connection aconn, int achild)
   {
      String retstr = "";
      CMapCode reasonmap = new CMapCode(aconn, "NonAdmReasTbl", "ReasonCd", "ExemptFld", CMapCode.TypeString);

      int shotnum = 1;
      for (int idx = 0; idx < getCount(); idx++)
      {
         CNonadItem myitem = (CNonadItem) this.getItem(idx);
         String exempt = reasonmap.mapCode(myitem.reasoncd);
         if (exempt.equals("Y"))
         {
            retstr = retstr + myitem.buildTiterStr(aconn, achild, shotnum);
            shotnum++;
         }
      }
      return(retstr);
   }

   public String exportList()
   {
      if (this.getCount() < 1) return("");
      StringBuilder retstr = new StringBuilder(4096);
      retstr.append("<NonAdminList>\n");
      for (int idx = 0; idx < getCount(); idx++)
      {
         CNonadItem myitem = (CNonadItem) this.getItem(idx);
         retstr.append(myitem.exportItem());
      }
      retstr.append("</NonAdminList>\n");
      return(retstr.toString());
   }

   private int getIntVal(String sval)
   {
      if (sval == null || sval.length() < 1)
      {
         return 0;
      }
      else
      {
         return (Integer.parseInt(sval));
      }
   }
}
