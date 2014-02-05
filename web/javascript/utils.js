function isIE()
{
   var msie = (navigator.userAgent.indexOf("MSIE")!= -1);
   return(msie);
}

function DoLogin()
{
   var userobj = document.getElementById('UserId');   
   var passobj = document.getElementById('PassWd');   
   var userid = userobj.value;
   var passwd = passobj.value;
   
   if ((userid == null) || (passwd == null))
   {
      alert("Enter your UserId and Password.");
      return;
   }

   frm = document.getElementById('NavForm');
   frm.ReqAct.value = "DoLogin";
   frm.BtnAct.value = "DoLogin";
   frm.target = "_self";
   frm.submit();
}

function DoLogout()
{
   frm = document.getElementById('NavForm');
   frm.ReqAct.value = "DoLogout";   
   frm.BtnAct.value = "DoLogout";   
   frm.target = "_self";
   frm.submit();
}

function DoButton(abtn)
{
   frm = document.getElementById('NavForm');
   frm.BtnAct.value = abtn;   
   frm.target = "_self";
   frm.submit();
}

function DoDetButton(abtn, adet)
{
   frm = document.getElementById('NavForm');
   frm.BtnAct.value = abtn;
   frm.DetAct.value = adet;
   frm.target = "_self";
   frm.submit();
}

function DoHomeBtn(abtn)
{
   frm = document.getElementById('NavForm');
   frm.ReqAct.value = abtn;   
   frm.BtnAct.value = abtn;   
   frm.target = "_self";
   frm.submit();
}

function DoRadioChange(abtn)
{
   frm = document.getElementById('NavForm');
   frm.BtnAct.value = abtn;   
   frm.target = "_self";
   frm.submit();
}

function DoRepChange(abtn)
{
   frm = document.getElementById('NavForm');
   frm.BtnAct.value = abtn;   
   frm.target = "_self";
   frm.submit();
}

function DoDebug()
{
   frm = document.getElementById('NavForm');
   frm.ReqAct.value = "DoDebug";
   frm.BtnAct.value = "DoDebug";
   frm.target = "_self";
   frm.submit();
}

function DoSpread()
{
   frm = document.getElementById('NavForm');
   frm.ReqAct.value = "DoSpread";
   frm.BtnAct.value = "DoSpread";
   frm.target = "_self";
   frm.submit();
}

function DoShowPeriod()
{
   chkm = document.getElementById("ShowPeriod0");
   chkq = document.getElementById("ShowPeriod1");
   chky = document.getElementById("ShowPeriod2");
   chkc = document.getElementById("ShowPeriod3");

   var numper = 0;
   var leftper = 0;

   if (chkm.checked) numper++; else leftper = 1;
   if (chkq.checked) numper++; else if (leftper == 1) leftper = 2;
   if (chky.checked) numper++; else if (leftper == 2) leftper = 3;
   if (chkc.checked) numper++;

   var rghtper = 3;
   if (!chkc.checked) rghtper = 2;
   if (!chky.checked && rghtper == 2) rghtper = 1;
   if (!chkq.checked && rghtper == 1) rghtper = 0;

   var showcell = "table-cell";
   if (isIE()) showcell = "block";

   var tbl = document.getElementById("reptable");
   var cells = tbl.getElementsByTagName("th");
   for (var i = 0; i < cells.length; i++)
   {
      var th = cells[i];
      if (th.id == null) continue;
      var cid = th.id.substr(0,7);


      if (cid == "blktitl")
      {
         th.colSpan = numper.toString();
      }

      else if (cid == "permont")
      {
         if (chkm.checked)
         {
            th.style.display = showcell; // "block"; // "table-cell";
            if (rghtper == 0) th.style.borderRight = "3px solid #9090a0";
            else th.style.borderRight = "1px solid #9090a0";
         }
         else th.style.display = "none";
      }
      else if (cid == "perquar")
      {
         if (chkq.checked) 
         {
            th.style.display = showcell; // "block"; // "table-cell";
            if (leftper == 1) th.style.borderLeft = "3px solid #9090a0";
            else th.style.borderLeft = "1px solid #9090a0";
            if (rghtper == 1) th.style.borderRight = "3px solid #9090a0";
            else th.style.borderRight = "1px solid #9090a0";
         }
         else th.style.display = "none";
      }
      else if (cid == "peryear")
      {
         if (chky.checked)
         {
            th.style.display = showcell; // "block"; // "table-cell";
            if (leftper == 2) th.style.borderLeft = "3px solid #9090a0";
            else th.style.borderLeft = "1px solid #9090a0";
            if (rghtper == 2) th.style.borderRight = "3px solid #9090a0";
            else th.style.borderRight = "1px solid #9090a0";
         }
         else th.style.display = "none";
      }
      else if (cid == "percumm")
      {
         if (chkc.checked)
         {
            th.style.display = showcell; // "block"; // "table-cell";
            if (leftper == 3) th.style.borderLeft = "3px solid #9090a0";
            else th.style.borderLeft = "1px solid #9090a0";
         }
         else th.style.display = "none";
      }
   }

   cells = tbl.getElementsByTagName("td");
   for (i = 0; i < cells.length; i++)
   {
      var td = cells[i];
      cid = td.id.substr(0,7);
      if (cid == "permont")
      {
         if (chkm.checked)
         {
            td.style.display = showcell; // "table-cell";
            td.style.borderLeft = "3px solid #9090a0";
            if (rghtper == 0) td.style.borderRight = "3px solid #9090a0";
            else td.style.borderRight = "1px solid #9090a0";
         }
         else td.style.display = "none";
      }
      else if (cid == "perquar")
      {
         if (chkq.checked)
         {
            td.style.display = showcell; // "table-cell";
            if (leftper == 1) td.style.borderLeft = "3px solid #9090a0";
            else td.style.borderLeft = "1px solid #9090a0";
            if (rghtper == 1) td.style.borderRight = "3px solid #9090a0";
            else td.style.borderRight = "1px solid #9090a0";
         }
         else td.style.display = "none";
      }
      else if (cid == "peryear")
      {
         if (chky.checked)
         {
            td.style.display = showcell; // "table-cell";
            if (leftper == 2) td.style.borderLeft = "3px solid #9090a0";
            else td.style.borderLeft = "1px solid #9090a0";
            if (rghtper == 2) td.style.borderRight = "3px solid #9090a0";
            else td.style.borderRight = "1px solid #9090a0";
         }
         else td.style.display = "none";
      }
      else if (cid == "percumm")
      {
         if (chkc.checked)
         {
            td.style.display = showcell; // "table-cell";
            td.style.borderRight = "3px solid #9090a0";
            if (leftper == 3) td.style.borderLeft = "3px solid #9090a0";
            else td.style.borderLeft = "1px solid #9090a0";
         }
         else td.style.display = "none";
      }
   }
}

function ValidateSaveCase()
{
//required fields
  // chk1 = document.getElementById("Testid");
  // chk2 = document.getElementById("Testgroup");
   chk3 = document.getElementById("TestTitle");
   chk4 = document.getElementById("BirthDate");
   chk5 = document.getElementById("BaseDate");
   chk6 = document.getElementById("Gender");
   
   var msg = "";
   if (chk3.value.length<1) msg=msg+"Case Name,";
   if (chk4.value.length<1) msg=msg+"Patient DOB,";
   if (chk5.value.length<1) msg=msg+"Case Date,";
   if (chk6.value.length<1) msg=msg+"Patient Gender,";
   //check values if needed
   if (msg.length>0) {
       alert("Required fields must be entered to save a case. Missing data for " + msg.substring(0,msg.length-1));
       return false;
   }
   else {
       return true;
   }

    
    
}

