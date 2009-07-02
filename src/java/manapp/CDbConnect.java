/*
 * CDbConnect.java
 *
 * Created on March 21, 2003, 2:20 PM
 */

package manapp;

import java.sql.*;
import java.io.*;
import java.util.Properties;

/** Database connection wrapper */

public class CDbConnect implements Serializable
{
   private static final long serialVersionUID = 20080822L;
   private boolean valid = false;
   transient private Connection theConnection;
   private String dbClassNm;
   private String dbUrl;
   private String dbTableNm;
   private String dbProps;
   private String dbUserName;
   private String dbPassword;
   private String errfile;
   private boolean errecho;

   /** Creates a new instance of CDbConnect */
   public CDbConnect(String aconf, String aerr, boolean aecho)
   {
      errfile = aerr;
      errecho = aecho;
      theConnection = null;
      try  
      {
         CDbConfig dbconf = new CDbConfig(aconf, aerr, aecho);
         dbClassNm = dbconf.dbClassNm; 
         dbUrl = dbconf.dbUrl;
         dbTableNm = dbconf.dbTableNm;
         dbProps = dbconf.dbProps;
         dbUserName = dbconf.dbUserName;
         dbPassword = dbconf.dbPassword;
      }
      catch (Exception e)
      {
         CLogError.logError(errfile, errecho, "Error reading database connection information: ", e);
      }
   }
   
   private void readObject(ObjectInputStream astream) throws ClassNotFoundException, IOException 
   {
      astream.defaultReadObject();
      theConnection = null;
   }

   private void writeObject(ObjectOutputStream astream) throws IOException 
   {
      astream.defaultWriteObject();
   }

   /** Make a Properties object by parsing the properties string from the configuration file */
   private Properties makeProps()
   {
      Properties myinfo = new Properties();
      String keystr = "";
      String valstr = "";
      String mybuf = dbProps;

      while (mybuf != null)
      {
         int jdx = mybuf.indexOf("=");
         if (jdx < 0) break;
         keystr = mybuf.substring(0,jdx);
         mybuf = mybuf.substring(jdx+1);

         jdx = mybuf.indexOf(";");
         if (jdx < 0) jdx = mybuf.length();
         valstr = mybuf.substring(0,jdx);
         
         myinfo.setProperty(keystr, valstr);
         
         if (jdx == mybuf.length()) break;    
         mybuf = mybuf.substring(jdx+1);
      }      
      
      return(myinfo);
   }
   
   /** Make a connection to the database. 
       @return the JDBC connection object. */
   private Connection makeConnection()
   {
      Connection myConn = null;
      shutDown();

      try
      {
         try
         {
            Class myclass = Class.forName(dbClassNm);
            if (myclass == null)
            {
               CLogError.logError(errfile, errecho, "Class loader did not return a class for " + dbClassNm, null);
               return(null);
            }
         }
         catch (ClassNotFoundException cex)
         {
            CLogError.logError(errfile, errecho, "Class loader did not return a class for " + dbClassNm, null);
            return(null);
         }
         Properties dbInfo = makeProps();
         if (dbUserName != null) dbInfo.setProperty("user", dbUserName);
         if (dbPassword != null) dbInfo.setProperty("password", dbPassword);
         
         myConn = DriverManager.getConnection(dbUrl, dbInfo);
         if (myConn == null)
         {
            CLogError.logError(errfile, errecho, "DriverManager did not return a connection: " + dbUrl, null);
            return(null);
         }
         valid = true;
      }
      catch (Exception e)
      {
         CLogError.logError(errfile, errecho, "Error making connection: ", e);
      }
      return myConn;
   }

   /** Get a connection to the database. 
       @return the JDBC connection object. */
   public Connection getConnection()
   {
      if (theConnection == null)
         theConnection = makeConnection();
      else 
      {
         try  // test connection validity by executing a jdbc operation.
         {
            Statement stmt = theConnection.createStatement();
            String sqlstr = "Select count(*) from " + dbTableNm;
            stmt.executeQuery(sqlstr);
            stmt.close();
         }
         catch (Exception e)
         {
            shutDown();
            theConnection = makeConnection();
            CLogError.logError(errfile, errecho, "CDbConnect-->Database connection has been reset. ", e);
         }
      }
      return theConnection;
   }

   /** Get URL for database. 
       @return the URL for the database. */
   public String getDbUrl()
   {
      return dbUrl;
   }
   /** Set URL for database. 
       @param aurl the URL for the database. */
   public void setDbUrl(String aurl)
   {
      if (!dbUrl.equals(aurl))
      {
         shutDown();
         dbUrl = aurl;
      }
   }

   /** Get name of table in database that can be queried to test database connectivity. 
       @return the name of a database table. */
   public String getDbTable()
   {
      return dbTableNm;
   }
   /** Set name of table in database that can be queried to test database connectivity. 
       @param atbl the name of a database table. */
   public void setDbTable(String atbl)
   {
         dbTableNm = atbl;
   }


   /** Get class name of JDBC driver. 
       @return the class name of the JDBC driver. */
   public String getDbClass()
   {
      return dbClassNm;
   }
   /** Set class name of JDBC driver. 
       @param adbclass the class name of the JDBC driver. */
   public void setDbClass(String adbclass)
   {
      try
      {
         Class.forName(adbclass);
      }
      catch (Exception e)
      {
         CLogError.logError(errfile, errecho, "Error loading class:", e);
      }
   }

   /** Close the connection to the database. */
   public void shutDown()
   {
      try
      {
         valid = false;
         if (theConnection != null)
         {
            theConnection.close();
            theConnection = null;
         }
      }
      catch (Exception e)
      {
         CLogError.logError(errfile, errecho, "CDbConnect.shutDown", e);
      }
   }

   /** Reset the connection to the database. */
   public void reset()
   {
      try
      {
         valid = false;
         theConnection.close();
         theConnection = makeConnection();
         valid = true;
      }
      catch (Exception e)
      {
         CLogError.logError(errfile, errecho, "Unable to reset connection: ", e);
      }
   }

   /** Return whether or we believe we have a valid connection to the database. */
   public boolean isValid()
   {
      return valid;
   }
}

