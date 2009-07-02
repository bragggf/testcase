//*******************************************************************
// WRecSet.js -- local record set object
//*******************************************************************

//*******************************************************************
// WMakeArray() -- create an array object of the indicated length. 
//*******************************************************************

function WMakeArray(num)
{
   this.length = num;
   for (var i = 0; i < num; i++)
   {
      this[i] = "";
   }
}

//*******************************************************************
// WGetItem() -- return the contents of the requested row and column.
//*******************************************************************

function WGetItem(r, c)
{
   if ((c >= this.cols) || (c < 0)) return "";
   if (this.rows == 0) return "";
   if ((r >= this.rows) || (r < 0)) return "";
   return this.arrdata[r * this.cols + c];
}

//*******************************************************************
// WGetCol() -- return the contents of the requested column from the
//               current record.
//*******************************************************************

function WGetCol(col)
{
   return this.WGetItem(this.currow, col);
}

//*******************************************************************
// WNextRec() -- advance current record to next record, but stick at
//               the last record.
//*******************************************************************

function WNextRec()
{
   this.WSetRow(this.currow + 1);
}

//*******************************************************************
// WPrevRec() -- retreat current record to previous record, but stick
//               at the first record.
//*******************************************************************

function WPrevRec()
{
   this.WSetRow(this.currow - 1);
}

//*******************************************************************
// WHomeRec() -- make the first record the current record.
//*******************************************************************

function WHomeRec()
{
   this.WSetRow(0);
}

//*******************************************************************
// WEndRec() -- make the last record the current record.
//*******************************************************************

function WEndRec()
{
   this.WSetRow(this.rows - 1);
}

//*******************************************************************
// WSetRow() -- make the indicated record the current record.
//*******************************************************************

function WSetRow(num)
{
   if (num < 0) 
      this.currow = 0;
   else if (num >= this.rows)
      this.currow = this.rows - 1;
   else
      this.currow = num;
}

//*******************************************************************
// WQueryField() -- return the contents of the requested field from
//                  the first record (starting with the current record)
//                  whose key field is the indicated value.  
//                  Make that record current.
//*******************************************************************

function WQueryField(keycol, keyval, rqstcol)
{
   for (var irow = this.currow; irow < this.rows; irow++)
   {
      if (this.WGetItem(irow, keycol) == keyval)
      {
         this.WSetRow(irow);
         return this.WGetCol(rqstcol);
      }
   }
   return "";
}

//*******************************************************************
// WQueryLField() -- return the contents of the requested field from
//                   the first record (starting with the current record)
//                   whose key field contains the indicated value.  
//                   Make that record current.
//*******************************************************************

function WQueryLField(keycol, keyval, rqstcol)
{
   for (var irow = this.currow; irow < this.rows; irow++)
   {
      var mykey = this.WGetItem(irow, keycol)
      if (mykey.indexOf(keyval, 0) >= 0)
      {
         this.WSetRow(irow);
         return this.WGetCol(rqstcol);
      }
   }
   return "";
}

//*******************************************************************
// WPopQueryLOptions() -- populate the referenced select object with
//                  the code and descriptions from records whose 
//                  indicated key field contains the indicated value.
//*******************************************************************

function WPopQueryLOptions(keycol, keyval, codecol, desccol, selobj, altobj, selcode)
{
   if (selobj)
   {
      // clear the select object
      var ndx = selobj.length - 1;
      for (var idx = ndx; idx >= 0; idx--)
      {
         selobj.options[idx] = null;
      }

      // find records that contain target
      for (var irow = 0; irow < this.rows; irow++)
      {
         var mykey = this.WGetItem(irow, keycol).toLowerCase();
         if (mykey.indexOf(keyval.toLowerCase(), 0) >= 0)
         {
            var mycode = this.WGetItem(irow,codecol);
            var issel = false;
            if (selcode && (mycode == selcode)) issel = true;

            // skip if already in alternate object
            if (altobj)
            {
               var found = 0;
               var nalt = altobj.length;
               for (var ialt = 0; ialt < nalt; ialt++)
               {
                  if (altobj.options[ialt].value == mycode)
                  {
                     found = 1;
                     break;
                  }
               }
               if (found == 0)  
                  selobj.options[selobj.length] = new Option(this.WGetItem(irow,desccol), mycode, false, issel);
            }
            else 
               selobj.options[selobj.length] = new Option(this.WGetItem(irow,desccol), mycode, false, issel);
         }
      }
   }
   else
   {
      alert('No select object');
   }
}

//*******************************************************************
// WPopQueryEOptions() -- populate the referenced select object with
//                  the code and descriptions from records whose 
//                  indicated key field is equal to the indicated value.
//*******************************************************************

function WPopQueryEOptions(keycol, keyval, codecol, desccol, selobj, altobj, selcode)
{
   if (selobj)
   {
      // clear the select object
      var ndx = selobj.length - 1;
      for (var idx = ndx; idx >= 0; idx--)
      {
         selobj.options[idx] = null;
      }

      // find records that contain target
      for (var irow = 0; irow < this.rows; irow++)
      {
         var mykey = this.WGetItem(irow, keycol).toLowerCase();
         if (mykey == keyval.toLowerCase())
         {
            var mycode = this.WGetItem(irow,codecol);
            var issel = false;
            if (selcode && (mycode == selcode)) issel = true;

            // skip if already in alternate object
            if (altobj)
            {
               var found = 0;
               var nalt = altobj.length;
               for (var ialt = 0; ialt < nalt; ialt++)
               {
                  if (altobj.options[ialt].value == mycode)
                  {
                     found = 1;
                     break;
                  }
               }
               if (found == 0)  
                  selobj.options[selobj.length] = new Option(this.WGetItem(irow,desccol), mycode, false, issel);
            }
            else 
               selobj.options[selobj.length] = new Option(this.WGetItem(irow,desccol), mycode, false, issel);
         }
      }
   }
   else
   {
      alert('No select object');
   }
}

//*******************************************************************
// get the value in column rcol 
// where the value in acol is akey and the value in bcol is bkey
//*******************************************************************

function WGet2KeyValue(acol, akey, bcol, bkey, rcol)
{
   for (var irow = 0; irow < this.rows; irow++)
   {
      if ((this.WGetItem(irow, acol) == akey) && (this.WGetItem(irow, bcol) == bkey))
         return(this.WGetItem(irow, rcol));
   }
   return("");
}

//**********************************************************************
// WRecSet() -- construct a structure to hold a set of records and 
//               populate it by parsing the referenced raw data.
//               Raw data looks like the following:
//                (a1|b1|...|m1|)(a2|b2|...|m2|)...(an|bn|...|mn|)
//               Note the "separator" after the last field in each record.
//**********************************************************************

function WRecSet(arawdata)
{
   // properties
   this.rows = 0;
   this.cols = 0;
   this.currow = 0;
   this.arrdata = 0;
      // methods
   this.WGetItem = WGetItem;
   this.WGetCol = WGetCol;
   this.WNextRec = WNextRec;
   this.WPrevRec = WPrevRec;
   this.WHomeRec = WHomeRec;
   this.WEndRec = WEndRec;
   this.WSetRow = WSetRow;
   this.WQueryField = WQueryField;
   this.WQueryLField = WQueryLField;
   this.WPopQueryEOptions = WPopQueryEOptions;
   this.WPopQueryLOptions = WPopQueryLOptions;
   this.WGet2KeyValue = WGet2KeyValue;

   // some parsing constants
   var rowbeg = "[";
   var rowend = "]"
   var colsep = "|";

   // bail if no data
   if (arawdata == null) return;
   if (arawdata.length == 0) return;

   // count records by counting row start tags
   var curpos = 0;
   var ipos = 0;

   while ((ipos = arawdata.indexOf(rowbeg, curpos)) >= 0)
   {
      this.rows++;
      curpos = ipos + rowbeg.length;
   }
  
   if (this.rows <= 0) return;

   // count fields by counting column separators in the first record
   var endpos = arawdata.indexOf(rowend, 0);
   for (ipos = 0; ipos < endpos; ipos++)
   {
      var iend = ipos + colsep.length;
      var sptr = arawdata.substring(ipos, iend);
      if (sptr == colsep) this.cols++;
   }
   
   // create the array to hold the data
   this.arrdata = new WMakeArray(this.cols * this.rows);

      // populate the array with substrings
   curpos = 0;
   var rownum = 0;
   while((ipos = arawdata.indexOf(rowbeg, curpos)) >= 0)  // each row
   {
      curpos = ipos + rowbeg.length;
      endpos = arawdata.indexOf(rowend, curpos);

      var currec = arawdata.substring(curpos, endpos);
      var colnum = 0;
      var fldpos = 0;
      while((ipos = currec.indexOf(colsep, fldpos)) >= 0) // each field in row
      {
         var curfld = currec.substring(fldpos, ipos);
         fldpos = ipos + colsep.length;
         this.arrdata[rownum * this.cols + colnum] = curfld;
         colnum++;

         if (fldpos == currec.length) break;
         if (colnum == this.cols) break;
      }

      curpos = endpos + rowend.length;
      rownum++;
      if (rownum == this.rows) break;
   }
}


//**********************************************************************
// make a record set from the data in the hidden field whose id is adrop.
// populate the options for the select (dropdown) object whose id is acmb
// with the items that contain the string found in text object afilt,
// excluding the items already found as options in select object altcmb.
//
// data drop is assumed to contain two fields per record (code and desc).
//**********************************************************************

function DoFilter(adrop, afilt, acmb, altcmb)
{
   datdrop = document.getElementById(adrop);
   filtxt = document.getElementById(afilt);
   selcmb = document.getElementById(acmb);
   if (altcmb) notsel = document.getElementById(altcmb);
   else notsel = null;

   recset = new WRecSet(datdrop.value); 
   recset.WPopQueryLOptions(1, filtxt.value, 0, 1, selcmb, notsel);
}
