/*
 * CParser.java
 *
 * Created on March 6, 2008, 5:53 PM
 */

package testcase;

/** parse delimited string */
public class CParser
{
   /** Creates a new instance of CParser */
   public CParser()
   {
   }
   
   public static String getToken(String abuf, String adelim) 
   {
      int sep = abuf.indexOf(adelim);
      if (sep < 0) return(abuf);  // return the whole string if delimiter is not found
      return(abuf.substring(0, sep));
   }

   public static String getRemnant(String abuf, String adelim)
   {
      int sep = abuf.indexOf(adelim);
      if (sep < 0) return("");
      return(abuf.substring(sep+1));
   }
   
   public static String prepadStr(String astr, String apad, int alen)
   {
      String rstr = astr;
      while (rstr.length() < alen)
         rstr = apad + rstr;
      return(rstr);
   }
   
   public static String postpadStr(String astr, String apad, int alen)
   {
      String rstr = astr;
      while (rstr.length() < alen)
         rstr = rstr + apad;
      return(rstr);
   }
   
   public static String truncStr(String astr, int alen)
   {
      if (astr == null) return("");
      String retstr = astr;
      if (retstr.length() > alen) retstr = retstr.substring(0, alen);
      return(retstr);
   }
}
