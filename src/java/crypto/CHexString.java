/*
 * CHexString.java
 *
 * Created on June 4, 2007, 7:31 PM
 */

package crypto;

/** convert between bytes and hex characters
 */
public class CHexString
{
   private static String pseudo[] = {"0", "1", "2", "3", "4", "5", "6", "7",
                         "8", "9", "A", "B", "C", "D", "E", "F"};
   
   /** Creates a new instance of CHexString */
   public CHexString()
   {
   }

   /** Convert an array of bytes into a hex string. 
       @param abytes array of bytes to convert 
       @return string of hexidecimal digits */
   public static String toHexString(byte abytes[]) 
   {
      byte ch = 0x00;
      int i = 0; 

      if (abytes == null || abytes.length <= 0) return("null");

      StringBuffer out = new StringBuffer(abytes.length * 2);
      while (i < abytes.length) 
      {
         ch = (byte) (abytes[i] & 0xF0);  // Strip off high nibble
         ch = (byte) (ch >>> 4);          // shift the bits down
         ch = (byte) (ch & 0x0F);         // must do this if high order bit is on!
         out.append(pseudo[ (int) ch]);   // convert the nibble to a String Character
         ch = (byte) (abytes[i] & 0x0F);  // Strip off low nibble 
         out.append(pseudo[ (int) ch]);   // convert the nibble to a String Character
         i++;
      }
      String rslt = new String(out);
      return rslt;
   }  
   
   /** Convert a hex string into an array of bytes. 
       @param string of hexidecimal digits to convert 
       @return array of bytes */

   public static byte[] toByteArr(String ahexstr)
   {
      byte[] bts = new byte[ahexstr.length() / 2];
      for (int i = 0; i < bts.length; i++) 
      {
         bts[i] = (byte) Integer.parseInt(ahexstr.substring(2*i, 2*i+2), 16);
      }
      return(bts);
   }
}
