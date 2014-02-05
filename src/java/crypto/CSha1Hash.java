/*
 * CSha1Hash.java
 * 
 * Created on Jan 28, 2009, 1:30:50 PM
 * 
 * By lwaisanen
 */

package crypto;

import java.security.MessageDigest;

/** SHA-1 hashing to hex string. 
 */
public class CSha1Hash 
{

   public CSha1Hash()
   {
   }
   
   /** Convert a string into a string of hexidecimal digits representing the
       SHA-1 hash of the string.
       @param astr string to hash
       @return string of hexidecimal digits representing the hash */
   public static String toHash(String astr)
   {
      try
      {
         byte[] mybytes = astr.getBytes();
         MessageDigest md = MessageDigest.getInstance("SHA-1");
         byte[] hash = md.digest(mybytes);
         return(CHexString.toHexString(hash));
      }
      catch (Exception e)
      {
         return("hashing_failed");
      }
   }
}
