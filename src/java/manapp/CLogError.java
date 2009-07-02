/*
 * CLogError.java
 *
 * Created on February 14, 2005, 1:33 PM
 */

package manapp;

import java.io.*;
import java.text.*;
import java.util.Date;

public class CLogError
{
   
   /** Creates a new instance of CLogError */
   public CLogError()
   {
   }
   
   static public synchronized void logError(String aerrfile, boolean aecho, String astr, Exception aex)
   {
      try
      {
         SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         Date dt = new Date();
         String datestr = df.format(dt);

         FileOutputStream errfos = new FileOutputStream(aerrfile, true);
         PrintWriter errout = new PrintWriter(errfos);
         if (aex != null)
         {
            errout.println(datestr + "|" + astr + aex.getMessage());
            if (aecho) System.err.println(datestr + "|" + astr + aex.getMessage());
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
            System.err.println("when handling: " + astr + aex.getMessage());
         else
            System.err.println("when handling: " + astr);
      }
   }
}
