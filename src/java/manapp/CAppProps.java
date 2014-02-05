/*
 * CAppProps.java
 *
 * Created on December 20, 2007, 4:17 PM
 */

package manapp;

import java.util.Properties;
import java.io.*;

public class CAppProps
{
   final static public String PropFile = "app.properties";
   protected String AppDir = "/apps/no/";
   public boolean ErrMsgEcho = false;
   public String ConfDir = AppDir + "conf/";
   public String LogDir = AppDir + "logs/";
   public String ImportDir = AppDir + "import/";
   public String ErrorLogFile = LogDir + "apperror.log";
   public String UsageLogFile = LogDir + "applog.log";
   public String SaveRemoteInfo = "N";

   /** Creates a new instance of CAppProps */
   public CAppProps()
   {
      try
      {
         InputStream finp = this.getClass().getResourceAsStream(CAppProps.PropFile);
         Properties props = new Properties();
         props.load(finp);

         AppDir = props.getProperty("AppDir");
         ErrMsgEcho = props.getProperty("ErrMsgEcho").equals("true");
         ConfDir = AppDir + props.getProperty("ConfDir");
         LogDir = AppDir + props.getProperty("LogDir");
         ImportDir = AppDir + props.getProperty("ImportDir");
         ErrorLogFile = LogDir + props.getProperty("ErrorLogFile");
         UsageLogFile = LogDir + props.getProperty("UsageLogFile");
         SaveRemoteInfo = props.getProperty("SaveRemoteInfo");
         finp.close();
      }
      catch (Exception ex)
      {
         System.err.println("Error fetching properties: " + ex.getMessage());
      }
   }
}
