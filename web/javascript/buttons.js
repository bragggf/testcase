// page locking 
var isLocked = 1;

// cache button images

CreateUp = new Image(72,32);
CreateUp.src = 'images/CreateUp.gif';
CreateDn = new Image(72,32);
CreateDn.src = 'images/CreateDn.gif';

StatusUp = new Image(72,32);
StatusUp.src = 'images/StatusUp.gif';
StatusDn = new Image(72,32);
StatusDn.src = 'images/StatusDn.gif';

RunallUp = new Image(72,32);
RunallUp.src = 'images/RunallUp.gif';
RunallDn = new Image(72,32);
RunallDn.src = 'images/RunallDn.gif';

LogOffUp = new Image(72,32);
LogOffUp.src = 'images/LogOffUp.gif';
LogOffDn = new Image(72,32);
LogOffDn.src = 'images/LogOffDn.gif';

DetailsUp = new Image(72,32);
DetailsUp.src = 'images/DetailsUp.gif';
DetailsDn = new Image(72,32);
DetailsDn.src = 'images/DetailsDn.gif';

EditUp = new Image(72,32);
EditUp.src = 'images/EditUp.gif';
EditDn = new Image(72,32);
EditDn.src = 'images/EditDn.gif';

ExecuteUp = new Image(72,32);
ExecuteUp.src = 'images/ExecuteUp.gif';
ExecuteDn = new Image(72,32);
ExecuteDn.src = 'images/ExecuteDn.gif';

SaveUp = new Image(72,32);
SaveUp.src = 'images/SaveUp.gif';
SaveDn = new Image(72,32);
SaveDn.src = 'images/SaveDn.gif';

CancelUp = new Image(72,32);
CancelUp.src = 'images/CancelUp.gif';
CancelDn = new Image(72,32);
CancelDn.src = 'images/CancelDn.gif';

ReturnUp = new Image(72,32);
ReturnUp.src = 'images/ReturnUp.gif';
ReturnDn = new Image(72,32);
ReturnDn.src = 'images/ReturnDn.gif';

ScaleUp = new Image(72,32);
ScaleUp.src = 'images/ScaleUp.gif';
ScaleDn = new Image(72,32);
ScaleDn.src = 'images/ScaleDn.gif';

CalcUp = new Image(72,32);
CalcUp.src = 'images/CalcUp.gif';
CalcDn = new Image(72,32);
CalcDn.src = 'images/CalcDn.gif';

function unlockPage()
{
   isLocked = 0;
}

function SwapBtn(x, y)
{
   img = document.getElementById(x);
   img.src = eval(y + '.src');
}

function DoSubmit(aform, abtn)
{
   if (isLocked == 0) 
   {
      isLocked = 1;
      frm = document.getElementById(aform);
      frm.BtnAct.value = abtn;   
      frm.target = "_self";
      frm.submit();
   }
   else
   {
      alert("You have to let selected operations complete.  Otherwise, you may make your session status unstable.");
      isLocked = 0;
   } 
}

function DoDetails(aform, abtn, adet)
{
   frm = document.getElementById(aform);
   frm.DetAct.value = adet;
   DoSubmit(aform, abtn);
}


function DoConfirm(aform, abtn)
{
   if (confirm("Are you sure you want to " + abtn + " this item?"))
   {
      DoSubmit(aform, abtn);	
   }    
}

function ShowItem(aid)
{
   var mynode = document.getElementById(aid); 
   
   if (mynode.style.display=="none")
      mynode.style.display = "block";
   else
      mynode.style.display = "none";	
}   

function HideItem(aid)
{
   var mynode = document.getElementById(aid); 
   mynode.style.display = "none";	
}   

function EnforceMaxLen(aobj, alen)
{
   if (aobj.value.length > alen) 
      aobj.value = aobj.value.substring(0, alen)
}
