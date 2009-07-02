//*******************************************************************
// selfuns.js -- some functions to manipulate SELECT objects
//*******************************************************************

function SelectFirst(aselobj)
{
   if (aselobj)
   {
      var ndx = aselobj.length - 1;
      for (var idx = ndx; idx > 0; idx--)
         aselobj.options[idx].selected = false;
      if (ndx >= 0) aselobj.options[0].selected = true;
   }
}

function SelectAll(aselobj)
{
   if (aselobj)
   {
      var ndx = aselobj.length - 1;
      for (var idx = ndx; idx >= 0; idx--)
         aselobj.options[idx].selected = true;
   }
}

function SwapOptions(aselobj, adx, bdx)
{
   if (bdx < 0) return;
   if (bdx >= aselobj.length) return;
   var tmpcode = aselobj.options[adx].value;
   var tmpdesc = aselobj.options[adx].text;
   var tmpsel = aselobj.options[adx].selected;
   aselobj.options[adx].value = aselobj.options[bdx].value;
   aselobj.options[adx].text = aselobj.options[bdx].text;
   aselobj.options[adx].selected = aselobj.options[bdx].selected;
   aselobj.options[bdx].value = tmpcode;
   aselobj.options[bdx].text = tmpdesc;
   aselobj.options[bdx].selected = tmpsel;
}

function MoveOrder(selobj, adir)
{
   ndx = aselobj.length;
   if (adir < 0)
   {
      for (idx = 0; idx < ndx; idx++)
      {
         if (aselobj.options[idx].selected)
         {
            SwapOptions(aselobj, idx, idx-1);
         }
      }
   }
   else
   {
      for (idx = ndx-1; idx >= 0; idx--)
      {
         if (aselobj.options[idx].selected)
         {
            SwapOptions(aselobj, idx, idx+1);
         }
      }
   }
}

function setSelValue(aselid, aval)
{
   var selobj = document.getElementById(aselid);
   var ndx = selobj.length;

   for (var idx = 0; idx < ndx; idx++)
   {
      if (selobj.options[idx].value == aval)
         selobj.options[idx].selected = true;
      else
         selobj.options[idx].selected = false;
   }
}

function getSelValue(aselid)
{
   var selobj = document.getElementById(aselid);
   var ndx = selobj.length;
   for (var idx = 0; idx < ndx; idx++)
   {
      if (selobj.options[idx].selected)
      {
         retval = selobj.options[idx].value;
         return(retval);
      }
   }
   return("");
}

function getSelIndex(aselid)
{
   var selobj = document.getElementById(aselid);
   var ndx = selobj.length;
   for (var idx = 0; idx < ndx; idx++)
   {
      if (selobj.options[idx].selected)
      {
         return(idx);
      }
   }
   return(-1);
}

function getTextValue(atxtid)
{
   var txtobj = document.getElementById(atxtid);
   return(txtobj.value);
}

function setTextValue(atxtid, aval)
{
   var txtobj = document.getElementById(atxtid);
   txtobj.value = aval;
}

function updatePText(apid, astr)
{
   pnode = document.getElementById(apid);
   if (pnode.hasChildNodes())
   {
      txt = pnode.firstChild;
      txt.data = astr;
   }
   else
   {
      newnode = createTextNode(astr);
      pnode.appendChild(newnode);
   }
}

function getPText(apid)
{
   pnode = document.getElementById(apid);
   if (pnode.hasChildNodes())
   {
      txt = pnode.firstChild;
      return(txt.data);
   }
   return("");
}

function fixSelClass(aselid, abadval)
{
   var selobj = document.getElementById(aselid);
   if (getSelValue(aselid) == abadval)
   {
      selobj.className = 'OptBad';
   }
   else
   {
      selobj.className = 'OptNorm';
   }
}
