package dbconn;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Enumeration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Driver;

/**
 * CDbConnMan manages a pool of database connections.
 */
public class CDbConnMan
{
   /** Class name for JDBC connector. */
   private String dbClassNm;
   /** URL for database. */
   private String dbUrl;
   /** Database connection properties. */
   private String dbProps;
   /** Database userid. */
   private String dbUserName;
   /** Database password. */
   private String dbPassword;
   /** Initial size of database connection pool. */
   private int dbPoolInit;
   /** Maximum (surge) size of database connection pool. */
   private int dbPoolMax;
   /** Maximum number of idle connections to keep in pool. */
   private int dbPoolIdleMax;
   /** Current number of idle connections in pool. */
   private int dbPoolIdleSize;
   /** Current number of connections in pool, including connections loaned out. */
   private int dbPoolSize;
   /** Query to use to test database connection.  For example, "Select 1" */
   private String dbTestQry;
   /** Output file into which to write error messages. */
   private String errfile;
   /** List of database connections available to loan out. */
   private ArrayList<Connection> connectionPool = new ArrayList<Connection>(8);

   /**
    * Constructor.
    *
    * @param acfg full path name of configuration file.
    * @param aerr full path name of error log file.
    */
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

         fillPool();
      }
      catch (Exception ex)
      {
         CDbError.logError(errfile, false, "Error creating database connection pool: ", ex);
      }
   }

   /**
    * Fill the connection pool with the initial number of connections.
    */
   private final void fillPool()
   {
      try
      {
         for (int ipl = 0; ipl < dbPoolInit; ipl++)
         {
            Connection myconn = createNewConnection();
            if (myconn == null) throw new Exception("Failed to create connection");
            connectionPool.add(myconn);
            dbPoolSize++;
            dbPoolIdleSize++;
         }
         CDbError.logError(errfile, false, "Initial database (" + dbUrl + ") connection pool size " + Integer.toString(dbPoolSize), null);
      }
      catch (Exception ex)
      {
         CDbError.logError(errfile, false, "Error filling connection pool: ", ex);
      }
   }

   /**
    * Replace any expired connections in the pool.
    *
    * @return number of connections in the pool.
    */
   public synchronized int refreshPool()
   {
      try
      {
         for (int ipl = 0; ipl < dbPoolSize; ipl++)
         {
            Connection myconn = connectionPool.get(ipl);
            if (!testConnection(myconn))
            {
               try
               {
                  if (!myconn.isClosed()) myconn.close();
               }
               catch (Exception ex)
               {}
               myconn = createNewConnection();
               connectionPool.set(ipl, myconn);
            }
         }
         return(dbPoolSize);
      }
      catch (Exception ex)
      {
         CDbError.logError(errfile, false, "Error refreshing connection pool: ", ex);
      }
      return(-dbPoolSize);
   }

   /**
    * Make a Properties object by parsing the properties string from the
    * configuration file
    *
    * @return properties object
    */
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
         keystr = mybuf.substring(0, jdx);
         mybuf = mybuf.substring(jdx + 1);

         jdx = mybuf.indexOf(";");
         if (jdx < 0) jdx = mybuf.length();
         valstr = mybuf.substring(0, jdx);

         myinfo.setProperty(keystr, valstr);

         if (jdx == mybuf.length()) break;
         mybuf = mybuf.substring(jdx + 1);
      }

      return(myinfo);
   }

   /**
    * Make a new connection to the database.
    *
    * @return the JDBC connection object.
    */
   private Connection createNewConnection()
   {
      Connection myconn = null;
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

         myconn = DriverManager.getConnection(dbUrl, dbInfo);
         if (myconn == null)
         {
            CDbError.logError(errfile, false, "DriverManager did not return a connection: " + dbUrl, null);
            return(null);
         }

         if (!testConnection(myconn))
         {
            if (!myconn.isClosed()) myconn.close();
            CDbError.logError(errfile, false, "New connection failed test", null);
            return(null);
         }
      }
      catch (Exception e)
      {
         CDbError.logError(errfile, false, "Error making connection: ", e);
         return(null);
      }
      return(myconn);
   }

   /**
    * Test the referenced database connection by executing a query with it.
    *
    * @param aconn database connection to test.
    * @return whether test was a success.
    */
   private boolean testConnection(Connection aconn)
   {
      try
      {
         Statement stmt = aconn.createStatement();
         stmt.executeQuery(dbTestQry);
         stmt.close();
         return(true);
      }
      catch (Exception ex)
      {}
      return(false);
   }

   /**
    * Get a database connection from the pool.
    *
    * @return a database connection.
    */
   public synchronized Connection getConnection()
   {
      Connection connection = null;

      // Check if there is a connection available. There could be times when all the
      // connections in the pool may be used up
      if (connectionPool.size() > 0)
      {
         connection = connectionPool.get(0);
         connectionPool.remove(0);
         dbPoolIdleSize--;

         if (!testConnection(connection))
         {
            try
            {
               if (!connection.isClosed()) connection.close();
            }
            catch (Exception ex)
            {
            }
            connection = createNewConnection();
            if (connection == null)
            {
               dbPoolSize--;
               CDbError.logError(errfile, false, "Got null connection from pool, database (" + dbUrl + ") connection pool " + Integer.toString(dbPoolSize), null);
            }
         }
      }
      // add a new connection if below the limit
      else if (dbPoolSize < dbPoolMax)
      {
         connection = createNewConnection();
         if (connection == null)
            CDbError.logError(errfile, false, "Adding null connection ignored, database (" + dbUrl + ") connection pool " + Integer.toString(dbPoolSize), null);
         else
         {
            dbPoolSize++;
            CDbError.logError(errfile, false, "Added connection to database (" + dbUrl + ") connection pool " + Integer.toString(dbPoolSize), null);
         }
      }
      else
      {
         CDbError.logError(errfile, false, "Database (" + dbUrl + ") connection pool cannot be extended " + Integer.toString(dbPoolSize), null);
      }
      return(connection);
   }

   /**
    * Return the referenced database connection to the pool.  However, if we already have the maximum number of idle connections
    * in the pool, then just throw away the referenced connection.
    *
    * @param aconn database connection to return to pool.
    */
   public synchronized void returnConnection(Connection aconn)
   {
      if (aconn == null)
      {
         CDbError.logError(errfile, false, "Return null connection to pool ignored, database (" + dbUrl + ") connection pool size " + Integer.toString(dbPoolSize), null);
      }
      else if (dbPoolIdleSize < dbPoolIdleMax)
      {
         // add the connection from the client back to the connection pool
         connectionPool.add(aconn);
         dbPoolIdleSize++;
      }
      else // dispose of surplus connection
      {
         dbPoolSize--;
         CDbError.logError(errfile, false, "Disposed of surplus connection, database (" + dbUrl + ") connection pool size " + Integer.toString(dbPoolSize), null);
         try
         {
            if (!aconn.isClosed()) aconn.close();
         }
         catch (Exception ex)
         {
            CDbError.logError(errfile, false, "CDbConnMan close surplus connection", ex);
         }
      }
   }

   /**
    * Dispose of the connections in the pool.
    */
   public synchronized void shutdown()
   {
      while (connectionPool.size() > 0)
      {
         Connection connection = connectionPool.get(0);
         try
         {
            if (!connection.isClosed()) connection.close();
         }
         catch (Exception ex)
         {
         }
         connectionPool.remove(0);
      }
   }
   
   public synchronized void drivercleanup()
   {
      try {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while(drivers.hasMoreElements()) {
            DriverManager.deregisterDriver(drivers.nextElement());
        }
      } 
      catch(Exception e) {
      }
   }
}
