
function getTextValue(atxtid)
{
   var txtobj = document.getElementById(atxtid);
   if (txtobj == null) return("");
   return(txtobj.value);
}

function setTextValue(atxtid, aval)
{
   var txtobj = document.getElementById(atxtid);
   if (txtobj != null) txtobj.value = aval;
}
  
function CheckLower(apass)
{
   var cnt = 0;
   var idx = 0;
   while (idx < apass.length)
   {
      var curchr = apass.charAt(idx);
      if (curchr.search(/[a-z]/) != -1)
      {
         cnt = cnt + 1;
      }
      idx = idx + 1;
   }
   return(cnt);
}
  
function CheckUpper(apass)
{
   var cnt = 0;
   var idx = 0;
   while (idx < apass.length)
   {
      var curchr = apass.charAt(idx);
      if (curchr.search(/[A-Z]/) != -1)
      {
         cnt = cnt + 1;
      }
      idx = idx + 1;
   }
   return(cnt);
}

function CheckDigit(apass)
{
   var cnt = 0;
   var idx = 0;
   while (idx < apass.length)
   {
      var curchr = apass.charAt(idx);
      if (curchr.search(/[0-9]/) != -1)
      {
         cnt = cnt + 1;
      }
      idx = idx + 1;
   }
   return(cnt);
}
  
function CheckSpecial(apass)
{
   var cnt = 0;
   var idx = 0;
   while (idx < apass.length)
   {
      var curchr = apass.charAt(idx);
      if (curchr.search(/[\x21-\x2f\x3a-\x40\x5b-\x60\x7b-\x7e]/) != -1)
      {
         cnt = cnt + 1;
      }
      idx = idx + 1;
   }
   return(cnt);
}

function CheckDiff(apass, bpass)
{
   var mat = 0;
   for (var idx = 0; idx < apass.length; idx++)
   {
      var achr = apass.charAt(idx);
      for (var jdx = 0; jdx < bpass.length; jdx++)
      {
         bchr = bpass.charAt(jdx);
         if (achr == bchr) 
         {
            mat = mat + 1;
            break;
         }
      }
   }
   return(apass.length - mat);
}

function DoSetPw(abtn, aminlen, aminlc, aminuc, amindig, aminsp, aminnew)
{
   var myoldpass = getTextValue('OldPass'); 
   var mynewpass = getTextValue('NewPass'); 
   var myconfpass = getTextValue('ConfPass'); 
	
	frm = document.getElementById('NavForm');

   // is the form filled out
   if ((myoldpass == "") || (mynewpass == "") || (myconfpass == ""))
   { 
      alert("You must enter your current password, a new password, and confirm the new password.");
      return;
   }

   // has the pssword been changed
   if (myoldpass == mynewpass)
   {
      alert("You cannot reuse your current password.");
      setTextValue("NewPass", "");
      setTextValue("ConfPass", "");
      return;
   }

   // has the password been confirmed
   if (mynewpass != myconfpass)
   {
      alert("You have not confirmed your new password.");
      return;
   }

   // minimum length
   if (mynewpass.length < aminlen)
   {
      alert("Your new password needs to be longer.");
      return;
   }
   
   var numtyp = CheckLower(mynewpass);
   var tottyp = numtyp;
   if (numtyp < aminlc)
   {
      alert("Your new password needs more lowercase characters.");
      return;
   }

   numtyp = CheckUpper(mynewpass);
   tottyp = tottyp + numtyp;
   if (numtyp < aminuc)
   {
      alert("Your new password needs more uppercase characters.");
      return;
   }

   numtyp = CheckDigit(mynewpass);
   tottyp = tottyp + numtyp;
   if (numtyp < amindig)
   {
      alert("Your new password needs more digit characters.");
      return;
   }

   numtyp = CheckSpecial(mynewpass);
   tottyp = tottyp + numtyp;
   if (numtyp < aminsp)
   {
      alert("Your new password needs more special characters.");
      return;
   }

   if (tottyp != mynewpass.length)
   {
      alert("Your new password contains disallowed characters.");
      return;
   }

   if (CheckDiff(mynewpass, myoldpass) < aminnew)
   {
      alert("Your new password needs more characters that were not used in you old password.");
      return;
   }

   DoButton(abtn);	
}
