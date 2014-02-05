/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testcase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import manapp.CAppConsts;
import manapp.CLogError;
import java.util.HashMap;
import java.util.ArrayList;
/**
 *
 * @author cfulper
 */
public class CForecasters {
   private int MAXFC =10;  //max of 10 forecasters 
   private static int FID = 1;
   private static int FNAME =2;
   private static int FURL=3;
   private static int FDESC=4;
   private static String[][] fcasterinfo;
   
   public CForecasters(Connection aconn)
   {
       
       fcasterinfo = new String[MAXFC][FDESC+1];
       dbLoadList(aconn);
   }
   private void dbLoadList(Connection aconn)
   {     
        
      try
      {
         String qstr = "Select ForecasterId, ForecasterNm, ServiceUrl, ServiceDesc From forecastertbl";
         Statement qstmt = aconn.createStatement();
         ResultSet rset = qstmt.executeQuery(qstr);
         int fcount = 0;
         while (rset.next() && fcount<=MAXFC)
         {
            fcasterinfo[fcount][FID] = rset.getString(1);
            fcasterinfo[fcount][FNAME] = rset.getString(2);
            fcasterinfo[fcount][FURL] = rset.getString(3);
            fcasterinfo[fcount][FDESC] = rset.getString(4);
            
            fcount = fcount+1;
         }
         rset.close();
         qstmt.close();
      }
      catch (Exception ex)
      {
         CLogError.logError(CAppConsts.ErrorFile, false, "CForecasters.dbLoadList cannot load list. ", ex);
      }
   } 
   
   public static String getFCname(String fcid)
   {   int i=0;
       
       while(i<fcasterinfo.length && !(fcid.equals(fcasterinfo[i][FID]))) ++i;
       if (i<fcasterinfo.length)
         return(fcasterinfo[i][FNAME]);
       else         
         return(""); // CForecasters.getFCname cannot find fc name. 

          
   }
   
   public static String getFCurl(String fcid)
   {   int i=0;
       
       while(i<fcasterinfo.length && !(fcid.equals(fcasterinfo[i][FID]))) ++i;
       if (i<fcasterinfo.length)
         return(fcasterinfo[i][FURL]);
       else         
         return(""); // CForecasters.getFCurl cannot find fc url. 

          
   }         

   public static String getFCdesc(String fcid)
   {   int i=0;
       
       while(i<fcasterinfo.length && !(fcid.equals(fcasterinfo[i][FID]))) ++i;
       if (i<fcasterinfo.length)
         return(fcasterinfo[i][FDESC]);
       else         
         return(""); // CForecasters.getFCdesc cannot find fc desc. 

          
   }
      

}
