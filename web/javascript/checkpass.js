  
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
      if (curchr.search(/[~`!@#$%\^&\*\(\)_=\-+\[\]\{\}\|\\:;"'<,>\.\?\/]/) != -1)
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
   for (idx = 0; idx < apass.length; idx++)
   {
      var achr = apass.charAt(idx);
      var fnd = 0;
      for (jdx = 0; jdx < bpass.length; jdx++)
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

function DoSetPw(aform, abtn)
{
   frm = document.getElementById(aform);

   // is the form filled out
   if ((frm.OldPass.value == "") || (frm.NewPass.value == "") || (frm.ConfPass.value == ""))
   { 
      alert("You must enter your current password, a new password, and confirm the new password.");
      return;
   }

   // has the pssword been changed
   if (frm.OldPass.valuevalue == frm.NewPass.value)
   {
      alert("You cannot reuse your current password.");
      frm.NewPass.value = "";
      frm.ConfPass.value = "";
      return;
   }

   // has the password been confirmed
   if (frm.NewPass.value != frm.ConfPass.value)
   {
      alert("You have not confirmed your new password.");
      return;
   }

   // minimum length
   if (frm.NewPass.value.length < 8)
   {
      alert("Your new password must be at least eight characters.");
      return;
   }
   
   var numtyp = CheckLower(frm.NewPass.value);
   var tottyp = numtyp;
   if (numtyp < 2)
   {
      alert("Your new password must use at least two lowercase characters.");
      return;
   }

   numtyp = CheckUpper(frm.NewPass.value);
   tottyp = tottyp + numtyp;
   if (numtyp < 2)
   {
      alert("Your new password must use at least two uppercase characters.");
      return;
   }

   numtyp = CheckDigit(frm.NewPass.value);
   tottyp = tottyp + numtyp;
   if (numtyp < 2)
   {
      alert("Your new password must use at least two digit characters.");
      return;
   }

   numtyp = CheckSpecial(frm.NewPass.value);
   tottyp = tottyp + numtyp;
   if (numtyp < 2)
   {
      alert("Your new password must use at least two special characters.");
      return;
   }

   if (tottyp != frm.NewPass.value.length)
   {
      alert("Your new password contains disallowed characters.");
      return;
   }

   if (CheckDiff(frm.NewPass.value, frm.OldPass.value) < 2)
   {
      alert("Your new password must contain at least two characters that were not used in you old password.");
      return;
   }

   DoSubmit(aform, abtn);	
}
