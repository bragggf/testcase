/*
 * CDbConfig.java
 *
 * Created on October 4, 2006, 7:02 PM
 */

package dbconn;

import java.io.*;
import java.security.*;

/**
 * Database connection configuration.
 * Reads and parses a database connection configuration file of the following form:
<code>
#
dbClassNm=com.ibm.db2.jcc.DB2Driver
dbDriver=jdbc:db2://
dbHost=214.3.107.12 
dbPortSep=:
dbPort=50000 
dbUrlSep=/
dbDatabase=MCFAS 
dbTestQry=Select 1
dbProps=
dbUserPath=/prod/mcfas/bpfmfiles/dbuser.txt
#
</code>
 */
public class CDbConfig
{
   /** Class name for JDBC connector. */
   public String dbClassNm;
   /** URL for database connection built from component parts. */
   public String dbUrl;
   /** JDBC driver string database connection. */
   public String dbDriver;
   /** host for database connection. */
   public String dbHost;
   /** separator between Host and port number. */
   public String dbPortSep;
   /** port for database connection. */
   public String dbPort;
   /** separator between URL and database name. */
   public String dbUrlSep;
   /** name of database for database connection. */
   public String dbDatabase;
   /** test query that can be executed to test for database connectivity. */
   public String dbTestQry;
   /** database properties information */
   public String dbProps;
   /** name of file containing login information for database user. */
   public String dbUserPath;
   /** database user name. */
   public String dbUserName;
   /** database user password. */
   public String dbPassword;
   /** initial size of database connection pool */
   public int dbPoolInit;
   /** maximum size of database connection pool */
   public int dbPoolMax;
   /** maximum number of idle connections to keep in pool */
   public int dbPoolIdleMax;
   
   protected String errfile;
   
   /** Creates a new instance of CDbConfig 
       @param acfg name of database connection configuration file; 
       @param aerr name of error log file. */
   public CDbConfig(String acfg, String aerr)
   {
      errfile = aerr;
      dbClassNm = "myclass.has.noname";
      dbDriver = "jdbc:nothing:";
      dbHost = "0.0.0.0";
      dbPortSep = ":";
      dbPort = "0";
      dbUrlSep = "/";
      dbDatabase = "missing";
      dbUrl = dbDriver + dbHost + dbPortSep + dbPort + dbUrlSep + dbDatabase;
      dbTestQry = "Select 1";
      dbProps = "";
      dbUserPath = "";
      dbUserName = "";
      dbPassword = "";
      dbPoolInit = 2;
      dbPoolMax = 4;
      dbPoolIdleMax = 3;
      readConfig(acfg);
   }
   
   /** Read database connection configuration information from text file with referenced name. 
       @param acfg name of database connection configuration file. */
   public void readConfig(String acfg) 
   {
      try
      {
         FileReader frd = new FileReader(acfg);
         BufferedReader finp = new BufferedReader(frd);
      
         String buf = "#";
         while (buf != null)
         {
            buf = finp.readLine();
            if (buf == null) break;
            buf = buf.trim();
            if (buf.length() < 1) continue;
            if (buf.substring(0,1).equals("#")) continue;
            int sep = buf.indexOf("=");
            if (sep < 0) continue;

            String fldtoken = buf.substring(0, sep);
            String value = buf.substring(sep+1);
            
            if (fldtoken.equals("dbClassNm"))
               dbClassNm = value;
            else if (fldtoken.equals("dbDriver"))
               dbDriver = value;
            else if (fldtoken.equals("dbHost"))
               dbHost = value;
            else if (fldtoken.equals("dbPortSep"))
               dbPortSep = value;
            else if (fldtoken.equals("dbPort"))
               dbPort = value;
            else if (fldtoken.equals("dbUrlSep"))
               dbUrlSep = value;
            else if (fldtoken.equals("dbDatabase"))
               dbDatabase = value;
            else if (fldtoken.equals("dbTestQry"))
               dbTestQry = value;
            else if (fldtoken.equals("dbProps"))
               dbProps = value;
            else if (fldtoken.equals("dbUserPath")) 
               dbUserPath = value;
            else if (fldtoken.equals("dbPoolInit"))
               dbPoolInit = Integer.parseInt(value);
            else if (fldtoken.equals("dbPoolMax"))
               dbPoolMax = Integer.parseInt(value);
            else if (fldtoken.equals("dbPoolIdleMax"))
               dbPoolIdleMax = Integer.parseInt(value);
            if (dbPoolIdleMax < dbPoolInit) dbPoolIdleMax = dbPoolInit;
            
         }
         dbUrl = dbDriver + dbHost + dbPortSep + dbPort + dbUrlSep + dbDatabase;
         if (!dbUserPath.equals(""))
         {
            FileReader prd = new FileReader(dbUserPath);
            BufferedReader pinp = new BufferedReader(prd);
            String rawuser = pinp.readLine();
            String rawpass = pinp.readLine();
            pinp.close();
            
            String dbUserKey = "dbUrl=dbDriver+dbHost+dbPortSep+dbPort+dbUrlSep+dbDatabase";
            String keystr = crypto.CMd5Hash.toHash(dbUserKey);
            Key mykey = crypto.CAesEncrypt.getKey(keystr);
            dbUserName = crypto.CAesEncrypt.decrypt(mykey, rawuser);
            dbPassword = crypto.CAesEncrypt.decrypt(mykey, rawpass);
         }
         finp.close();
      }
      catch (Exception ex)
      {
         CDbError.logError(errfile, false, "CDbConfig.readConfig failed ", ex);
      }
   }
}
