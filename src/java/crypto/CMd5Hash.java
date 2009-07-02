/*
 * CMd5Hash.java
 *
 * Created on August 28, 2006, 18:40 PM
 *
 * larry.waisanen@altarum.org
 */

package crypto;

import java.security.MessageDigest;

/** MD5 hashing to hex string. 
 */
public class CMd5Hash
{
   /** Create a new instance of CMd5Hash */
   public CMd5Hash()
   {
   }   
   
   /** Convert a string into a string of hexidecimal digits representing the
       MD5 hash of the string.
       @param astr string to hash
       @return string of hexidecimal digits representing the hash */
   public static String toHash(String astr)
   {
      try
      {
         byte[] mybytes = astr.getBytes();
         MessageDigest md = MessageDigest.getInstance("MD5");
         byte[] hash = md.digest(mybytes);
         return(CHexString.toHexString(hash));
      }
      catch (Exception e)
      {
         return("hashing_failed");
      }
   }
}