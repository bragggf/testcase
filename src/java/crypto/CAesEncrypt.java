/*
 * CAesEncrypt.java
 *
 * Created on June 14, 2007, 8:07 PM
 */

package crypto;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/** AES encryption and decryption */
public class CAesEncrypt
{
   private static final String KEY_STRING = "43E1BF55D0B09FE13CCF88DC40B1F7D4";

   public static String generateKey()
   {
      try
      {
          // get the key generator
         KeyGenerator keygen = KeyGenerator.getInstance("AES");
         keygen.init(128); // 192 and 256 bits may not be available
          // Generate the secret key.
         SecretKey skey = keygen.generateKey();
         byte[] bytes = skey.getEncoded();
         return CHexString.toHexString(bytes);
      }
      catch( Exception e )
      {
         System.err.println("CAesEncrypt.generateKey: " + e);
         return(null);
      }
   }
   
   public static String encrypt(String astr)
   {
      Key key = getKey(KEY_STRING);
      return(encrypt(key, astr)); 
   }

   public static String encrypt(Key akey, String astr)
   {
      try
      {
         // Create the cipher 
         Cipher cipher = Cipher.getInstance("AES");
          // Initialize the cipher for encryption
         cipher.init(Cipher.ENCRYPT_MODE, akey);
          // Our cleartext as bytes
         byte[] cleartext = astr.getBytes();
          // Encrypt the cleartext
         byte[] ciphertext = cipher.doFinal(cleartext);
          // Return a String representation of the cipher text
         return CHexString.toHexString( ciphertext );
      }
      catch( Exception e )
      {
         System.err.println("CAesEncrypt.encrypt: " + e);
         return null;
      }
   }
   
   public static String decrypt(String astr)
   {
      Key key = getKey(KEY_STRING);
      return(decrypt(key, astr)); 
   }
   
   public static String decrypt(Key akey, String astr)
   {
      try
      {
          // Create the cipher 
         Cipher cipher = Cipher.getInstance("AES");
          // cleartext into bytes
         byte[] ciphertext = CHexString.toByteArr(astr);
          // Initialize the same cipher for decryption
         cipher.init(Cipher.DECRYPT_MODE, akey);
          // Decrypt the ciphertext
         byte[] cleartext = cipher.doFinal(ciphertext);
          // Return the clear text
         return new String(cleartext);
      }
      catch( Exception e )
      {
         System.err.println("CAesEncrypt.decrypt: " + e);
         return null;
      }
   }

   public static Key getKey(String ahex)
   {
      try
      {
         byte[] bytes = CHexString.toByteArr(ahex);
         SecretKeySpec skeySpec = new SecretKeySpec(bytes, "AES");      
         return skeySpec;
      }
      catch( Exception e )
      {
         System.err.println("CAesEncrypt.getKey: " + e);
         return null;
      }
   }
} 
