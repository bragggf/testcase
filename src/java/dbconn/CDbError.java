/*
 * CLogError.java
 *
 * Created on February 14, 2005, 1:33 PM
 */

package dbconn;

import java.io.*;
import java.text.*;
import java.util.Date;

public class CDbError
{

   /** Creates a new instance of CLogError */
   public CDbError()
   {
   }

   /** write error message to referenced file
    * @param aerrfile name of error log file
    * @param aecho whether or not to echo error message to System.err
    * @param astr message string
    * @param aex exception
    */
   static public synchronized void logError(String aerrfile, boolean aecho, String astr, Exception aex)
   {
      try
      {
         manapp.CAppProps props = new manapp.CAppProps();
         String errfile = aerrfile;
         if (errfile == null) errfile = props.ErrorLogFile;
         SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         Date dt = new Date();
         String datestr = df.format(dt);

         FileOutputStream errfos = new FileOutputStream(errfile, true);
         PrintWriter errout = new PrintWriter(errfos);
         if (aex != null)
         {
            errout.println(datestr + "|" + astr + aex.toString());
            if (aecho) System.err.println(datestr + "|" + astr + aex.toString());
         }
         else
         {
            errout.println(datestr + "|" + astr);
            if (aecho) System.err.println(datestr + "|" + astr);
         }
         errout.close();
      }
      catch (Exception e)
      {
         System.err.println("Error in logError: " + e.getMessage());
         if (aex != null)
            System.err.println("when handling: " + astr + aex.toString());
         else
            System.err.println("when handling: " + astr);
      }
   }
}
