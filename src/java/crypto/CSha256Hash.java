/*
 * CSha256Hash.java
 * 
 * Created on Jan 28, 2009, 1:38:48 PM
 * 
 * By lwaisanen
 */

package crypto;

import java.security.MessageDigest;

public class CSha256Hash 
{

   public CSha256Hash()
   {
   }
   
   /** Convert a string into a string of hexidecimal digits representing the
       SHA-256 hash of the string.
       @param astr string to hash
       @return string of hexidecimal digits representing the hash */
   public static String toHash(String astr)
   {
      try
      {
         byte[] mybytes = astr.getBytes();
         MessageDigest md = MessageDigest.getInstance("SHA-256");
         byte[] hash = md.digest(mybytes);
         return(CHexString.toHexString(hash));
      }
      catch (Exception e)
      {
         return("hashing_failed");
      }
   }
}
