/*
 * CTextItem.java
 *
 * Created on July 18, 2008, 4:53 PM
 */

package testcase;

/** expected result text item  */
public class CTextItem
{
   public String expectid;
   public String expecttxt;
   public String resulttxt;

   /** Creates a new instance of CTextItem */
   public CTextItem()
   {
      expectid = "";
      expecttxt = "";
      resulttxt = "";
   }

   public String exportItem()
   {
      StringBuilder retstr = new StringBuilder(128);
      retstr.append("<ExpectResultItem>\n");
      retstr.append("<ExpectTxt>" + expecttxt + "</ExpectTxt>\n");
      retstr.append("<ResultTxt>" + expecttxt + "</ResultTxt>\n");
      retstr.append("</ExpectResultItem>\n");
      return(retstr.toString());
   }
}
