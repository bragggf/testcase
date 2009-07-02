/*
 * CDosevItem.java
 *
 * Created on July 24, 2008, 3:16 PM
 */

package testcase;

/** dose evaluation item */
public class CDosevItem
{
   public String shotid;
   public String seriescd;
   public int dosenum;
   public String validflag;
   public String invalidcd;
   
   /** Creates a new instance of CDosevItem */
   public CDosevItem()
   {
      shotid = "";
      dosenum = 0;
      validflag = "";
      invalidcd = "";
   }
}
