package dbconn;

import java.util.*;
import java.sql.*;

public class CDbConnMan
{
   private String dbClassNm;
   private String dbUrl;
   private String dbProps;
   private String dbUserName;
   private String dbPassword;
   private int dbPoolInit;
   private int dbPoolMax;
   private int dbPoolIdleMax;
   private int dbPoolIdleSize;
   private int dbPoolSize;
   private String dbTestQry;
   private String errfile;
   private Vector<Connection> connectionPool = new Vector<Connection>(8,8);

    public CDbConnMan(String acfg, String aerr)
    {
       errfile = aerr;
       try  
       {
          CDbConfig dbconf = new CDbConfig(acfg, errfile);
          dbClassNm = dbconf.dbClassNm; 
          dbUrl = dbconf.dbUrl;
          dbProps = dbconf.dbProps;
          dbUserName = dbconf.dbUserName;
          dbPassword = dbconf.dbPassword;
          dbPoolInit = dbconf.dbPoolInit;
          dbPoolMax = dbconf.dbPoolMax;
          dbPoolIdleMax = dbconf.dbPoolIdleMax;
          dbTestQry = dbconf.dbTestQry;
          dbPoolSize = 0;
          dbPoolIdleSize = 0;
          while(connectionPool.size() < dbPoolInit)
          {
              connectionPool.addElement(createNewConnection());
              dbPoolSize++;
              dbPoolIdleSize++;
          }
          CDbError.logError(errfile, false, "Initial database connection pool size " + Integer.toString(dbPoolSize), null);
       }
       catch (Exception ex)
       {
          CDbError.logError(errfile, false, "Error reading database connection information: ", ex);
       }
    }

    /** Make a Properties object by parsing the properties string from the configuration file
     @return properties object*/
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
    private Connection createNewConnection()
    {
       Connection myConn = null;
       try
       {
          try
          {
             Class<?> myclass = Class.forName(dbClassNm);
             if (myclass == null)
             {
                CDbError.logError(errfile, false, "Class loader did not return a class for " + dbClassNm, null);
                return(null);
             }
          }
          catch (ClassNotFoundException cex)
          {
             CDbError.logError(errfile, false, "Class loader did not return a class for " + dbClassNm, null);
             return(null);
          }
          Properties dbInfo = makeProps();
          if (dbUserName != null) dbInfo.setProperty("user", dbUserName);
          if (dbPassword != null) dbInfo.setProperty("password", dbPassword);
          
          myConn = DriverManager.getConnection(dbUrl, dbInfo);
          if (myConn == null)
          {
             CDbError.logError(errfile, false, "DriverManager did not return a connection: " + dbUrl, null);
             return(null);
          }
       }
       catch (Exception e)
       {
          CDbError.logError(errfile, false, "Error making connection: ", e);
       }
       return myConn;
    }
    
    protected boolean testConnection(Connection aconn)
    {
       try
       {
          Statement stmt = aconn.createStatement();
          stmt.executeQuery(dbTestQry);
          stmt.close();
          return(true);
       }
       catch (Exception ex)
       {
       }
       return(false);
    }
    
    public synchronized Connection getConnection()
    {
        Connection connection = null;

        //Check if there is a connection available. There are times when all the connections in the pool may be used up
        if(connectionPool.size() > 0)
        {
            connection = (Connection) connectionPool.firstElement();
            connectionPool.removeElementAt(0);
            dbPoolIdleSize--;
            
            if (!testConnection(connection))
            {
               connection = createNewConnection();
            }
        }
        // add a new connection if below the limit
        else if (dbPoolSize < dbPoolMax)
        {
           connection = createNewConnection();
           dbPoolSize++;
           CDbError.logError(errfile, false, "Added connection to database connection pool " + Integer.toString(dbPoolSize), null);
        }
        else
        {
           CDbError.logError(errfile, false, "Database connection pool cannot be extended", null);
        }
        return connection;
    }

    public synchronized void returnConnection(Connection connection)
    {
        if (dbPoolIdleSize < dbPoolIdleMax)
        {
           // add the connection from the client back to the connection pool
           connectionPool.addElement(connection);
           dbPoolIdleSize++;
        }
        else  // dispose of surplus connection
        {
           dbPoolSize--;
           CDbError.logError(errfile, false, "Disposed of surplus connection, database connection pool size " + Integer.toString(dbPoolSize), null);
           try
           {
              if (!connection.isClosed()) connection.close();
           }
           catch (Exception ex)
           {
              CDbError.logError(errfile, false, "CDbConnMan close surplus connection", ex);
           }
        }
    }
    
    public synchronized void shutdown()
    {
       while(connectionPool.size() > 0)
       {
          Connection connection = connectionPool.firstElement();
          try
          {
             if (!connection.isClosed()) connection.close();
          }
          catch (Exception ex)
          {}
          connectionPool.removeElementAt(0);
       }
    }
}