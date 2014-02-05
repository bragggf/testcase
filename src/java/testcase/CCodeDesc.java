/*
 * CCodeDesc.java
 *
 * Created on June 13, 2005, 12:03 PM
 */

package testcase;

import manapp.*;
import java.sql.*;

class CDescItem
{
   public String mastval;
   public String codeval;
   public String descval;
}


/** Mapping of codes and descriptions read from a description table.
 */
public class CCodeDesc extends CStringList
{
   /** Name of code field in description table. */
   private String codefld;
   /** Name of description field in description table. */
   private String descfld;
   /** Name of sort field in description table. */
   private String sortfld;
   /** Name of description table. */
   private String tablenm;
   /** Name of master field */
   private String mastfld;
   /** name of master table */
   private String masttbl;
   /** Filler for missing description. */
   final static public String DescNotFound = CAppConsts.TagNoLabel;
   /** Filler for missing code. */
   final static public String CodeNotFound = CAppConsts.TagNoValue;

   final static public String TokDropBegRec = "[";   
   final static public String TokDropEndRec = "]";   
   final static public String TokDropSepRec = "|";   
   
   /** Creates a new instance of CCodeDesc */
   public CCodeDesc()
   {
      super();
      setMetaData("", "", "", "", "", "");
   }

   /** Creates a new instance of CCodeDesc with referenced description table information. 
       @param atable name of description table.
       @param acode name of code field.
       @param adesc name of description field.
       @param asort name of sort field. */
   public CCodeDesc(String atable, String acode, String adesc, String asort)
   {
      super();
      setMetaData(atable, acode, adesc, asort, "", "");
   }

   /** Creates a new instance of CCodeDesc with referenced description table information and
       reads data from database. 
       @param aconn database connection.
       @param atable name of description table.
       @param acode name of code field.
       @param adesc name of description field.
       @param asort name of sort field. */
   public CCodeDesc(Connection aconn, String atable, String acode, String adesc, String asort)
   {
      super();
      setMetaData(atable, acode, adesc, asort, "", "");
      dbReadList(aconn);
   }

   public CCodeDesc(Connection aconn, String atable, String acode, String adesc, String asort, String amasttbl,String amastfld)
   {
      super();
      setMetaData(atable, acode, adesc, asort, amasttbl, amastfld);
      dbReadList(aconn);
   }

   /** Get code at referenced index. 
       @param aidx index of item in the list. 
       @return associated code. */
   public String getCode(int aidx)
   {
      if (aidx < 0 || aidx >= getCount()) return(CodeNotFound);
      CDescItem myitem = (CDescItem) this.getItem(aidx);
      return(myitem.codeval);
   }
   /** Get description at referenced index. 
       @param aidx index of item in the list. 
       @return associated description. */
   public String getDesc(int aidx)
   {
      if (aidx < 0 || aidx >= getCount()) return(DescNotFound);
      CDescItem myitem = (CDescItem) this.getItem(aidx);
      return(myitem.descval);
   }
   
   /** Get description for referenced code. 
       @param acode associated code. 
       @return associated description. */
   public String getDescByCode(String acode)
   {
      return(getDescByCode(CAppConsts.TagNoValue, acode));
   }
   /** Get description for referenced master and code. 
       @param amast associated master value. 
       @param acode associated code. 
       @return associated description. */
   public String getDescByCode(String amast, String acode)
   {
      if (acode == null) return(DescNotFound);
      int idx = this.getIndex(amast + "|" + acode);
      return(getDesc(idx));
   }
   
   /** Get name of code field. 
       @return name of code field. */
   public String getCodeFld()
   {
      return(codefld);
   }
   /** Set name of code field. 
       @param astr name of code field. */
   public void setCodeFld(String astr)
   {
      codefld = astr;
   }
   
   /** Get name of description field. 
       @return name of description field. */
   public String getDescFld()
   {
      return(descfld);
   }
   /** Set name of description field. 
       @param astr name of description field. */
   public void setDescFld(String astr)
   {
      descfld = astr;
   }
   
   /** Get name of sort field. 
       @return name of sort field. */
   public String getSortFld()
   {
      return(sortfld);
   }
   /** Set name of sort field. 
       @param astr name of sort field. */
   public void setSortFld(String astr)
   {
      sortfld = astr;
   }
   
   /** Get name of master field. 
       @return name of master field. */
   public String getMastFld()
   {
      return(mastfld);
   }
   /** Set name of master field. 
       @param astr name of master field. */
   public void setMastFld(String astr)
   {
      mastfld = astr;
   }
   
   /** Get name of master table. 
       @return name of master table. */
   public String getMastTbl()
   {
      return(masttbl);
   }
   /** Set name of master table. 
       @param astr name of master table. */
   public void setMastTbl(String astr)
   {
      masttbl = astr;
   }
   
   /** Get name of description table. 
       @return name of description table. */
   public String getTableNm()
   {
      return(tablenm);
   }
   /** Set name of description table. 
       @param astr name of description table. */
   public void setTableNm(String astr)
   {
      tablenm = astr;
   }
   
   /** Set description table information. 
       @param atable name of description table.
       @param acode name of code field.
       @param adesc name of description field.
       @param asort name of sort field. */
   public void setMetaData(String atable, String acode, String adesc, String asort, String amastbl, String amasfld)
   {
      setTableNm(atable);
      setCodeFld(acode);
      setDescFld(adesc);
      setSortFld(asort);
      setMastTbl(amastbl);
      setMastFld(amasfld);
   }
   
   /** Read codes and descriptions from database. 
       @param aconn database connection. */
   public void dbReadList(Connection aconn)
   {
      synchronized(aconn)
      {
      try
      {
         String qstr = "";
           // if master table and field are defined, join the description table
         if (mastfld.length() > 0 && masttbl.length() > 0)
         {
            qstr = "Select a." + mastfld + ",a." + codefld + ",b." + descfld;
            if (!codefld.equals(sortfld))  qstr = qstr + ",b." + sortfld;
            qstr = qstr + " From " + masttbl + " a," + tablenm + " b";
            qstr = qstr + " Where a." + codefld + "=b." + codefld;
            qstr = qstr + " Order by a." + mastfld + ",b." + sortfld;
         }
         else
         {
            qstr = "Select " + codefld + "," + descfld;
            if (!codefld.equals(sortfld))  qstr = qstr + "," + sortfld;
            qstr = qstr + " From " + tablenm;
            qstr = qstr + " Order by " + sortfld;
         }
         
         Statement qstmt = aconn.createStatement();
         ResultSet rset = qstmt.executeQuery(qstr);

         while (rset.next())
         {
            CDescItem myitem = new CDescItem();
            if (mastfld.length() > 0 && masttbl.length() > 0)
            {
               myitem.mastval = rset.getString(1);
               myitem.codeval = rset.getString(2);
               myitem.descval = rset.getString(3);
            }
            else 
            {
               myitem.mastval = CAppConsts.TagNoValue;
               myitem.codeval = rset.getString(1);
               myitem.descval = rset.getString(2);
            }
            String mykey = myitem.mastval + "|" + myitem.codeval;
            this.addItem(mykey, myitem);
         }
         rset.close();
         qstmt.close();
      }
      catch (Exception ex)
      {
         CLogError.logError(CAppConsts.ErrorFile, false, "CCodeDesc.dbReadList " + tablenm + " ", ex);
      }
      }
   }

   /** Build options to populate html select object from codes and descriptions. 
       @param acode code for option to set to selected. 
       @return html markup string containing list of select options. */
   public String makeOptions(String acode)
   {
      return(makeOptions(CAppConsts.TagNoValue, acode));
   }

   /** Build options to populate html select object from codes and descriptions. 
       @param amast master value. 
       @param acode code for option to set to selected. 
       @return html markup string containing list of select options. */
   public String makeOptions(String amast, String acode)
   {
      String retstr = "";
      for (int idx = 0; idx < getCount(); idx++)
      {
         CDescItem myitem = (CDescItem) this.getItem(idx);
         if (!amast.equals(myitem.mastval)) continue;
         retstr = retstr + "<option" + (acode.equals(myitem.codeval)?" SELECTED ":" ") +
               "value='" + myitem.codeval + "'>" + myitem.descval + "</option>";
      }
      return(retstr);
   }
   
   public String makeDataDrop()
   {
      String retstr = "";
      for (int idx = 0; idx < getCount(); idx++)
      {
         CDescItem myitem = (CDescItem) getItem(idx);
         retstr = retstr + CCodeDesc.TokDropBegRec + myitem.mastval + 
                           CCodeDesc.TokDropSepRec + myitem.codeval + 
                           CCodeDesc.TokDropSepRec + myitem.descval + 
                           CCodeDesc.TokDropSepRec + CCodeDesc.TokDropEndRec;
      }
      return(retstr);
   }
}
