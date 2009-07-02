/*
 * CAppProps.java
 *
 * Created on December 20, 2007, 4:17 PM
 */

package manapp;

import java.util.Properties;
import java.io.*;

public class CAppProps implements Serializable
{
   private static final long serialVersionUID = 20080822L;
   public boolean ErrMsgEcho = false;
   public String AppDir = "/missing/";
   public String ConfDir = AppDir + "conf/";
   public String LogDir = AppDir + "logs/";
   public String DbConfigFile = ConfDir + "appdb.conf";
   public String RemDbConfigFile = ConfDir + "remappdb.conf";
   public String AuthDbConfig = ConfDir + "authdb.conf";
   public String ErrorLogFile = LogDir + "apperror.log";
   public String LoginWarnFile = ConfDir + "appwarn.txt";
   public String UsageLogFile = LogDir + "applog.log";
   public String SaveRemoteInfo = "N";
   
   /** Creates a new instance of CAppProps */
   public CAppProps(String apropfile)
   {
      try
      {
         InputStream finp = this.getClass().getResourceAsStream(apropfile);
         Properties props = new Properties(); 
         props.load(finp);

         ErrMsgEcho = props.getProperty("ErrMsgEcho").equals("true");
         AppDir = props.getProperty("AppDir");
         ConfDir = AppDir + props.getProperty("ConfDir");
         LogDir = AppDir + props.getProperty("LogDir");
         DbConfigFile = ConfDir + props.getProperty("DbConfigFile");
         RemDbConfigFile = ConfDir + props.getProperty("RemDbConfigFile");
         AuthDbConfig = ConfDir + props.getProperty("AuthDbConfig");
         ErrorLogFile = LogDir + props.getProperty("ErrorLogFile");
         LoginWarnFile = ConfDir + props.getProperty("LoginWarnFile");
         UsageLogFile = LogDir + props.getProperty("UsageLogFile");
         SaveRemoteInfo = props.getProperty("SaveRemoteInfo");
      }
      catch (Exception ex)
      {
         System.err.println("Error fetching properties: " + ex.getMessage());
      }
   }
   
   private void readObject(ObjectInputStream astream) throws ClassNotFoundException, IOException 
   {
      astream.defaultReadObject();
   }

   private void writeObject(ObjectOutputStream astream) throws IOException 
   {
      astream.defaultWriteObject();
   }
}
