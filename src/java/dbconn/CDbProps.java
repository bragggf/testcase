/*
 * CDbProps.java
 *
 * Created on December 20, 2007, 4:17 PM
 */

package dbconn;

import java.util.Properties;
import java.io.*;

public class CDbProps
{
   final static public String PropFile = "db.properties";
   protected String AppDir = "/apps/no/";
   public String DbConfigFile = "db.conf";
   public String DebugLogFile = "dbdebug.txt";
   public String RemConfigFile = "rem.conf";
   public String ErrorLogFile = "dberror.txt";

   /** Creates a new instance of CDbProps */
   public CDbProps()
   {
      try
      {
         InputStream finp = this.getClass().getResourceAsStream(CDbProps.PropFile);
         Properties props = new Properties();
         props.load(finp);

         AppDir = props.getProperty("AppDir");
         DbConfigFile = props.getProperty("DbConfigFile");
         RemConfigFile = props.getProperty("RemConfigFile");
         DebugLogFile = props.getProperty("DebugLogFile");
         ErrorLogFile = props.getProperty("ErrorLogFile");
         finp.close();
      }
      catch (Exception ex)
      {
         System.err.println("CDbProps error fetching properties: " + ex.getMessage());
      }
   }
}
