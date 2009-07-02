
function ChangeVaccine(avacid, amfrid)
{
   var locdrop = document.getElementById('mfrdatadrop');
   var locrecset = new WRecSet(locdrop.value); 
   var loccode = getSelValue(avacid);
   var selobj = document.getElementById(amfrid);
   var selcode = getSelValue(amfrid);
   locrecset.WPopQueryEOptions(0, loccode, 1, 2, selobj, null, selcode);
}