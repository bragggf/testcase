/*
 * CMapCode.java
 *
 * Created on July 21, 2008, 4:58 PM
 */

package testcase;

import manapp.*;
import java.sql.*;

class CMapItem
{
   public String codeval;
   public String mapval;
}

/** list that maps a code field into a mapped field  */
public class CMapCode extends CStringList
{
   protected String tablenm;
   protected String codefld;
   protected String mapfld;
   protected int maptyp;

   public static final int TypeString = 1;
   public static final int TypeInteger = 2;
   
   /** Creates a new instance of CMapCode */
   public CMapCode(Connection aconn, String atable, String acode, String amap, int atype)
   {
      super(true);
      tablenm = atable;
      codefld = acode;
      mapfld = amap;
      maptyp = atype;
      dbReadList(aconn);
   }
   
   public void dbReadList(Connection aconn)
   {
      try
      {
         String qstr = "Select " + codefld + "," + mapfld + " From " + tablenm + " Order by " + codefld;
         Statement qstmt = aconn.createStatement();
         ResultSet rset = qstmt.executeQuery(qstr);

         while (rset.next())
         {
            CMapItem myitem = new CMapItem();
            myitem.codeval = rset.getString(1);
            if (maptyp == CMapCode.TypeString) myitem.mapval = rset.getString(2);
            else if (maptyp == CMapCode.TypeInteger) myitem.mapval = Integer.toString(rset.getInt(2));
            this.addItem(myitem.codeval, myitem);
         }
         rset.close();
         qstmt.close();
      }
      catch (Exception ex)
      {
         CLogError.logError(CConsts.ErrMsgFile, false, "CMapCode.dbReadList cannot read list. ", ex);
      }
   }
   
   public String mapCode(String aval)
   {
      CMapItem myitem = (CMapItem) this.getObject(aval);
      if (myitem == null) return("null");
      return(myitem.mapval);
   }
   
   public String unmapCode(String aval)
   {
      for (int idx = 0; idx < this.getCount(); idx++)
      {
         CMapItem myitem = (CMapItem) this.getItem(idx);
         if (myitem.mapval.equals(aval)) return(myitem.codeval);
      }
      return(CConsts.TagNoValue);
   }
}
