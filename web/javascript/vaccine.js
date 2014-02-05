
function ChangeVaccine(avacid, amfrid)
{
        var locdrop = document.getElementById('mfrdatadrop');
        var locrecset = new WRecSet(locdrop.value);
        var loccode = getSelValue(avacid);
        var selobj = document.getElementById(amfrid);
        var selcode = getSelValue(amfrid);
        locrecset.WPopQueryEOptions(0, loccode, 1, 2, selobj, null, selcode);
        selobj.options[selobj.length] = new Option("","",false,true);
        
}

function setCalcAge()
{       var maxage=100;
        var ldob = Date.parse(document.getElementById('BirthDate').value); 
        var base = Date.parse(document.getElementById('BaseDate').value);
        var lage = {yrs:0,mos:0,wks:0,days:0};
        var tooold= new Date(base.getFullYear()- maxage, base.getMonth(), base.getDate());
//        alert ("too old = " + tooold );
//alert("base="+base);
//first validate...
    //cannot have age if born in the future - but should check before calling this function and report to user
        if (!dateValidateMask(document.getElementById('BirthDate'),base,tooold,true)) {
    //        alert('Invalid date !');
    //        document.getElementById('BirthDate').focus();
            return false;
        }
    //    if (base<ldob) {
    //        alert('Birthdate cannot be in the future !');
    //        document.getElementById('BirthDate').focus();
    //        return false;
    //    }
    //    if (base.getYear()-ldob.getYear() > maxage) {
    //        document.getElementById('BirthDate').focus();
    //        alert('Patient is over maximum age!');
    //        return false;
    //    }

        getAge(ldob, base, lage);
        document.getElementById('AgeYrs').value = lage.yrs;
        document.getElementById('AgeMos').value = lage.mos;
        document.getElementById('AgeWks').value = lage.wks;
        document.getElementById('AgeDays').value = lage.days;

}

function getAge(ldob, ldate, lage)
{           

        var yrs=0;
        var mos=0;
        var wks=0;
        var days=0;

        //break out parts
        var dobyr = ldob.getYear();
        var dobmo = ldob.getMonth();
        var dobday = ldob.getDate();
        var asmtyr = ldate.getYear();
        var asmtmo = ldate.getMonth();
        var asmtday = ldate.getDate();
        //days
        if (asmtday - dobday >= 0) 
        {
           days = asmtday - dobday;
        }
        else 
        {// borrow days from the month
           if (asmtmo > 1) 
           {
               days = asmtday + Date.getDaysInMonth(asmtyr,asmtmo-1) - dobday; // depends on the month - use DaysinMonth function
               asmtmo = asmtmo - 1;
           }
           else
           { //borrow from year too
               days = asmtday + Date.getDaysInMonth(asmtyr, 12-1) - dobday; // since it must be january we borrow from december
               asmtmo = (asmtmo + 12) - 1;
               asmtyr = asmtyr - 1;
           }
        }
        // months 
        if (asmtmo - dobmo >= 0)
        {
           mos = asmtmo - dobmo;
        } 
        else
        { //borrow from year
           mos = (asmtmo + 12) - dobmo;
           asmtyr = asmtyr - 1;
        }
        //years
        yrs = asmtyr - dobyr;
        if (yrs > 1)
        {  // keep age as years
           //nothing
        }
        else  // less than 2 years old
        {
           //if (mos > 2)  // *commenting this line out so that under age 2 is described in months
           {// for under age 1 and under convert to months
              mos = mos + (yrs * 12);
              yrs = 0;
           } 
           //else  // *commenting this line out so that under age 2 is described in months
           { //for 2 or less months convert to weeks and days
             if (mos === 1)
             {
                days = days + Date.getDaysInMonth(dobyr, dobmo);
             } 
             else
             {   if (mos === 2)
                 { //convert each month to how many days it has 
                   days = days + Date.getDaysInMonth(dobyr, dobmo);
                   days = days + Date.getDaysInMonth(ldate.getYear(), ldate.getMonth());
                 }
             }
             if (mos <=2) // *added this line for fix making under age 2yrs be described in months
             {                                        // *
               if (days > 7)
               {
                wks = wks + Math.floor(days / 7);
                days = days % 7;
               }
               mos = 0;
             }                                         // *  
           }
        }
        if (yrs > 1 && mos === 12)
        {
           mos = 0;
           yrs = yrs + 1;
        }
        lage.yrs= yrs;
        lage.mos= mos;
        lage.wks= wks;
        lage.days= days;
     
}

function setCalcDob()
{   //first validate
        var lyr = document.getElementById('AgeYrs').value;
        var lmo = document.getElementById('AgeMos').value;
        var lwk = document.getElementById('AgeWks').value;
        var lday = document.getElementById('AgeDays').value;
        var base = document.getElementById('BaseDate').value;
        if (base !== null) {
          tmpdob = getDOBfromAge(base, ((lyr === null)?0:lyr), ((lmo === null)?0:lmo), ((lwk === null)?0:lwk), ((lday === null)?0:lday));
          document.getElementById('BirthDate').value = tmpdob;
        }
        
}

//use the age at assessment to figure out dob
function getDOBfromAge(basedate, yrs, mos, wks, dys) {
        var newdate = new Date();
        var base = Date.parse(basedate);
 //       alert('vals passed in=' +  basedate + " : " + yrs + " : " + mos + " : " + wks + " : " + dys);
 //       alert('base date=' + base);
 //       alert('base year=' + base.getFullYear());
        
        dys = parseInt(dys) + (parseInt(wks) * 7);
        
//        alert("dys="+dys);
//use the date function math

        newdate.setYear(base.getFullYear() - yrs);
        newdate.setMonth(base.getMonth() - mos);
        newdate.setDate(base.getDate() - dys);
 //       alert('new date=' + newdate.toString("MM/dd/yyyy"));
        return newdate.toString("MM/dd/yyyy");
}
          
function setAgeCalcMethod(mode)
{   //default to using dates so disable age fields -- but remember to enable if we want to submit field
    var disdate = false;
    var disage = true;
    
    if (mode==2) //in this mode disable dates and enable ages
    {  disdate = true;
       disage = false;
    }
  
    //setting for dob date
    document.getElementById('AgeYrs').disabled=disage;
    document.getElementById('AgeMos').disabled=disage;
    document.getElementById('AgeWks').disabled=disage;
    document.getElementById('AgeDays').disabled=disage;
    document.getElementById('BirthDate').disabled=disdate;
    
    //setting for vac date
    var lsty = getElementsByPartialName('Vyrs');
    var l = lsty.length;
    for (var i = 0; i < l; i++) lsty[i].disabled=disage;
        
    var lstm = getElementsByPartialName('Vmos');
    for (var i = 0; i < l; i++) lstm[i].disabled=disage;
         
    var lstw = getElementsByPartialName('Vwks');
    for (var i = 0; i < l; i++) lstw[i].disabled=disage;
           
    var lstd = getElementsByPartialName('Vdys');
    for (var i = 0; i < l; i++) lstd[i].disabled=disage;
           
    var lstv = getElementsByPartialName('Vdate');
    for (var i = 0; i < l; i++) lstv[i].disabled=disdate;
 
    //setting for nonadmins
    var lsty = getElementsByPartialName('Nyrs');
    var l = lsty.length;
    for (var i = 0; i < l; i++) lsty[i].disabled=disage;
           
    var lstm = getElementsByPartialName('Nmos');
    for (var i = 0; i < l; i++) lstm[i].disabled=disage;
           
    var lstw = getElementsByPartialName('Nwks');
    for (var i = 0; i < l; i++) lstw[i].disabled=disage;
           
    var lstd = getElementsByPartialName('Ndys');
    for (var i = 0; i < l; i++) lstd[i].disabled=disage;
           
    var lstv = getElementsByPartialName('Ndate');
    for (var i = 0; i < l; i++) lstv[i].disabled=disdate;
       
                 

    //setting for overdue
    var lsty = getElementsByPartialName('Oyrs');
    var l = lsty.length;
    for (var i = 0; i < l; i++) lsty[i].disabled=disage;
           
    var lstm = getElementsByPartialName('Omos');
    for (var i = 0; i < l; i++) lstm[i].disabled=disage;
           
    var lstw = getElementsByPartialName('Owks');
    for (var i = 0; i < l; i++) lstw[i].disabled=disage;
           
    var lstd = getElementsByPartialName('Odys');
    for (var i = 0; i < l; i++) lstd[i].disabled=disage;
           
    var lstv = getElementsByPartialName('Odate');
    for (var i = 0; i < l; i++) lstv[i].disabled=disdate;
                    
                    
    //setting for recommended
    var lsty = getElementsByPartialName('Ryrs');
    var l = lsty.length;
    for (var i = 0; i < l; i++) lsty[i].disabled=disage;
           
    var lstm = getElementsByPartialName('Rmos');
    for (var i = 0; i < l; i++) lstm[i].disabled=disage;
           
    var lstw = getElementsByPartialName('Rwks');
    for (var i = 0; i < l; i++) lstw[i].disabled=disage;
           
    var lstd = getElementsByPartialName('Rdys');
    for (var i = 0; i < l; i++) lstd[i].disabled=disage;
           
    var lstv = getElementsByPartialName('Rdate');
    for (var i = 0; i < l; i++) lstv[i].disabled=disdate;
                    
                 
   //setting for accelerated
    var lsty = getElementsByPartialName('Ayrs');
    var l = lsty.length;
    for (var i = 0; i < l; i++) lsty[i].disabled=disage;
           
    var lstm = getElementsByPartialName('Amos');
    for (var i = 0; i < l; i++) lstm[i].disabled=disage;
           
    var lstw = getElementsByPartialName('Awks');
    for (var i = 0; i < l; i++) lstw[i].disabled=disage;
           
    var lstd = getElementsByPartialName('Adys');
    for (var i = 0; i < l; i++) lstd[i].disabled=disage;
           
    var lstv = getElementsByPartialName('Adate');
    for (var i = 0; i < l; i++) lstv[i].disabled=disdate;
    
   //eval items auto calculations 
    var lstd = getTdElementsByPartialName('EAdate');
    var l = lstd.length;
     for (var i = 0; i < l; i++) {
       var nameProp = lstd[i].getAttribute('name');
       var textProp = lstd[i].innerText;
       setCalcEvalAge(nameProp, textProp);
    }
    var lstd = getTdElementsByPartialName('ERdate');
    var l = lstd.length;
     for (var i = 0; i < l; i++) {
       var nameProp = lstd[i].getAttribute('name');
       var textProp = lstd[i].innerText;
       setCalcEvalAge(nameProp, textProp);
    }
    var lstd = getTdElementsByPartialName('EOdate');
    var l = lstd.length;
     for (var i = 0; i < l; i++) {
       var nameProp = lstd[i].getAttribute('name');
       var textProp = lstd[i].innerText;
       setCalcEvalAge(nameProp, textProp);
    }

                    
}
//enabled disabled fields so they can be submitted
function EnableAllElements(form) 
{  
    var elem = document.getElementById(form).elements;
    for(var i = 0; i < elem.length; i++)
 { 
     if (elem[i].type ==='text') elem[i].disabled = false;
 }
} 

function setAgeMode()
{
    var mode= document.getElementById('AgeMethod').value;
//    alert('mode= ' + mode);
 //   setInitVals();
    if (mode!=="2") //by age
    {
        mode=1; //default to date
        document.getElementById('AgeMethod').value=mode;
        setInitVals();
    }    
    setAgeCalcMethod(mode);
}

function setInitVals()
{
   
    setCalcAge();
    //set age for vaccinations
    var lstv = getElementsByPartialName('Vdate');
    for (var i = 0; i < l; i++) setCalcVacAge( lstv[i]);
    var lstv = getElementsByPartialName('Ndate');
    for (var i = 0; i < l; i++) setCalcNonAge( lstv[i]);
    var lstv = getElementsByPartialName('Odate');
    for (var i = 0; i < l; i++) setCalcDueAge( lstv[i]);
    var lstv = getElementsByPartialName('Rdate');
    for (var i = 0; i < l; i++) setCalcDueAge( lstv[i]);
    var lstv = getElementsByPartialName('Adate');
    for (var i = 0; i < l; i++) setCalcDueAge( lstv[i]);
    
       //eval items auto calculations 
    var lstd = getTdElementsByPartialName('EAdate');
    var l = lstd.length;
     for (var i = 0; i < l; i++) {
       var nameProp = lstd[i].getAttribute('name');
       var textProp = lstd[i].innerText;
       setCalcEvalAge(nameProp, textProp);
    }
    var lstd = getTdElementsByPartialName('ERdate');
    var l = lstd.length;
     for (var i = 0; i < l; i++) {
       var nameProp = lstd[i].getAttribute('name');
       var textProp = lstd[i].innerText;
       setCalcEvalAge(nameProp, textProp);
    }
    var lstd = getTdElementsByPartialName('EOdate');
    var l = lstd.length;
     for (var i = 0; i < l; i++) {
       var nameProp = lstd[i].getAttribute('name');
       var textProp = lstd[i].innerText;
       setCalcEvalAge(nameProp, textProp);
     }  
    
}
function setCalcVacAge(myitem)
{
//   alert("setCalcVacAge : "+ myitem);
    
   var ldob = Date.parse(document.getElementById('BirthDate').value);
//   alert("ldob : "+ ldob);
   var vacdate = Date.parse(document.getElementById(myitem).value);
   
//   alert("vacdate : "+vacdate);
   var vdatelen = 5;//based on the common part of the string being "Vdate"
   var famname = myitem.substr(vdatelen);
//   alert("famnamee : "+famname);
   
   var vage = {yrs:0,mos:0,wks:0,days:0};


     getAge(ldob, vacdate, vage);
     //set values of ymwd to corresponding fields
     document.getElementById('Vyrs'+famname).value = vage.yrs;
     document.getElementById('Vmos'+famname).value = vage.mos;
     document.getElementById('Vwks'+famname).value = vage.wks;
     document.getElementById('Vdys'+famname).value = vage.days;
   
}

function setCalcNonAge(myitem)
{
//   alert("setCalcNonAge : "+ myitem);
    
   var ldob = Date.parse(document.getElementById('BirthDate').value);
//   alert("ldob : "+ ldob);
   var nonadmdate = Date.parse(document.getElementById(myitem).value);
   
//   alert("nonadmindate : "+nonadmdate);
   var ndatelen = 5;//based on the common part of the string being "Ndate"
   var famname = myitem.substr(ndatelen);
//   alert("famnamee : "+famname);
   
   var nage = {yrs:0,mos:0,wks:0,days:0};


     getAge(ldob, nonadmdate, nage);
     //set values of ymwd to corresponding fields
     document.getElementById('Nyrs'+famname).value = nage.yrs;
     document.getElementById('Nmos'+famname).value = nage.mos;
     document.getElementById('Nwks'+famname).value = nage.wks;
     document.getElementById('Ndys'+famname).value = nage.days;
   
}

function setCalcDueAge(myitem)
{
//   alert("setCalcDueAge : "+ myitem);
     var duetyp = myitem.substr(0,1);  //can by A for accelerated, R for recommended, O for overdue
     var ldob = Date.parse(document.getElementById('BirthDate').value);
//   alert("ldob : "+ ldob);
     var duedate = Date.parse(document.getElementById(myitem).value);
   //alert("setCalcDueAge : "+ myitem + " : " + duetyp);
   
     var ndatelen = 5;  //based on the common part of the string being "Adate"
     var famname = myitem.substr(ndatelen);
//   alert("famnamee : "+famname);
   
     var age = {yrs:0,mos:0,wks:0,days:0};

     getAge(ldob, duedate, age);
     //set values of ymwd to corresponding fields
     document.getElementById(duetyp+'yrs'+famname).value = age.yrs;
     document.getElementById(duetyp+'mos'+famname).value = age.mos;
     document.getElementById(duetyp+'wks'+famname).value = age.wks;
     document.getElementById(duetyp+'dys'+famname).value = age.days;
   
}

function setCalcEvalAge(myitem, mydate)
{
   //alert("setCalcEvalAge : "+ myitem);
     var duetyp = myitem.substr(0,2);  //can by EA for accelerated, ER for recommended, EO for overdue
     var ldob = Date.parse(document.getElementById('BirthDate').value);
   //alert("ldob : "+ ldob);
   var duedate = Date.parse(mydate);
   //alert("setCalcEvalAge : "+ myitem + " : " + duetyp);
   //alert("setCalcEvalAge duedate: "+  duedate);
   
     var ndatelen = 6;  //based on the common part of the string being "EAdate"
     var famname = myitem.substr(ndatelen);
   //alert("famnamee : "+famname);
   
     var age = {yrs:0,mos:0,wks:0,days:0};

     getAge(ldob, duedate, age);
     //set values of ymwd to corresponding fields
     document.getElementById(duetyp+'yrs'+famname).innerText = age.yrs;
     document.getElementById(duetyp+'mos'+famname).innerText = age.mos;
     document.getElementById(duetyp+'wks'+famname).innerText = age.wks;
     document.getElementById(duetyp+'dys'+famname).innerText = age.days;
   
}

function setCalcVacDate(myitem)
{   //get age at vaccination and compare to birth date to get the corresponding date
   // alert("calcVacDate : "+ myitem);
    
   var vdatelen = 4;//based on the common part of the string being "Vyrs" or "Vwks" or "Vdys"
   var famname = myitem.substr(vdatelen);
 //  alert("fam=" + famname);
        var lyr = document.getElementById('Vyrs'+famname).value;
        var lmo = document.getElementById('Vmos'+famname).value;
        var lwk = document.getElementById('Vwks'+famname).value;
        var lday = document.getElementById('Vdys'+famname).value;
  //      var base = document.getElementById('BaseDate').value;
        var ldob = document.getElementById('BirthDate').value;
 
 //  alert ("ldob: " + ldob);
//   alert ("test: " + (ldob !== null));
        if (ldob !== null) {
          var vdat = getVacDateFromAge(ldob, lyr, lmo, lwk, lday);
          document.getElementById('Vdate'+famname).value = vdat;
        }
        
}
function setCalcNonDate(myitem)
{   //get age at non administration and compare to birth date to get the corresponding date
   // alert("setCalcNonDate : "+ myitem);
    
   var vdatelen = 4;//based on the common part of the string being "Nyrs" or "Nwks" or "Ndys"
   var famname = myitem.substr(vdatelen);
 //  alert("fam=" + famname);
        var lyr = document.getElementById('Nyrs'+famname).value;
        var lmo = document.getElementById('Nmos'+famname).value;
        var lwk = document.getElementById('Nwks'+famname).value;
        var lday = document.getElementById('Ndys'+famname).value;
  //      var base = document.getElementById('BaseDate').value;
        var ldob = document.getElementById('BirthDate').value;
 
 //  alert ("ldob: " + ldob);
//   alert ("test: " + (ldob !== null));
        if (ldob !== null) {
          var vdat = getVacDateFromAge(ldob, lyr, lmo, lwk, lday);
          document.getElementById('Ndate'+famname).value = vdat;
        }
        
}
function setCalcDueDate(myitem)
{   //get age due for shot and set the corresponding date
   // alert("setCalcDueDate : "+ myitem);
    
   var duetyp = myitem.substr(0,1);  //can by A for accelerated, R for recommended, O for overdue
   //alert("setCalcDueDate : "+ myitem + " : " + duetyp);
   var vdatelen = 4;  //based on the common part of the string being "Ryrs" or "Rwks" or "Rdys"
   var famname = myitem.substr(vdatelen);
 //  alert("fam=" + famname);
        var lyr = document.getElementById(duetyp+'yrs'+famname).value;
        var lmo = document.getElementById(duetyp+'mos'+famname).value;
        var lwk = document.getElementById(duetyp+'wks'+famname).value;
        var lday = document.getElementById(duetyp+'dys'+famname).value;
  //      var base = document.getElementById('BaseDate').value;
        var ldob = document.getElementById('BirthDate').value;
 

        if (ldob !== null) {
          var vdat = getVacDateFromAge(ldob, lyr, lmo, lwk, lday);
          document.getElementById(duetyp+'date'+famname).value = vdat;
        }
        
}

//return INPUT elements that have names that start with that string
function getElementsByPartialName(partialName) {
         var retVal = new Array();
         var elems = document.getElementsByTagName("input");  //limiting List to INPUT types
 //        var offset = partialName.length;
         
         for(var i = 0; i < elems.length; i++) {
             var nameProp = elems[i].getAttribute('name');
 //            alert("namprop="+ nameProp + " should match partialName="+ partialName);
 //            alert(nameProp.substr(0, partialName.length));
             if(!(nameProp == null) && (nameProp.substr(0,partialName.length) == partialName))
                 { 
                 retVal.push(elems[i]);
                 }
         }

         return retVal;
     } 
//return elements that have names that start with that string
function getTdElementsByPartialName(partialName) {
         var retVal = new Array();
         var elems = document.getElementsByTagName("td"); ///limiting to TD 
 //        var offset = partialName.length;
         
         for(var i = 0; i < elems.length; i++) {
             var nameProp = elems[i].getAttribute('name');
 //            alert("namprop="+ nameProp + " should match partialName="+ partialName);
 //            alert(nameProp.substr(0, partialName.length));
             if(!(nameProp == null) && (nameProp.substr(0,partialName.length) == partialName))
                 { 
                 retVal.push(elems[i]);
                 }
         }

         return retVal;
     } 
     
function getVacDateFromAge(dobdate, yrs, mos, wks, dys)
{ var dob= Date.parse(dobdate);
  var newdate= new Date();
 //  alert("lyr: " + yrs+ " lmo: " + mos+ " lwk: " +wks + " ldy: " +dys);
    
  if (yrs==="")  yrs = 0;
  if (mos==="")  mos = 0;
  if (wks==="")  wks = 0;
  if (dys==="")  dys = 0;

  dys = parseInt(dys)+(parseInt(wks)*7);
 // alert ("add to "+dob);
 // alert("y: " + yrs+ " m: " + mos+ " w: " +wks + " d: " +dys);
//  newdate.add(yrs).years();
//  newdate.add(mos).months();
//  newdate.add(dys).days();
//  alert(newdate);
  
   
    newdate.setYear(dob.getFullYear() + parseInt(yrs));
    newdate.setMonth(dob.getMonth() + parseInt(mos));
    newdate.setDate(dob.getDate() + dys);
 //   alert('new date=' + newdate.toString("MM/dd/yyyy"));

  return newdate.toString("MM/dd/yyyy");
  
}

///date validation

function datechekc(mit) {
     var v = mit.value;
        if (v.match(/^\d{2}$/) !== null) {
            mit.value = v + '/';
        } else if (v.match(/^\d{2}\/\d{2}$/) !== null) {
            mit.value = v + '/';
        }
}



function dateValidateMask (element, bef, aft, showmsg){ //, checkFormat, validFormat) {
var dateMask=/^(\d{2})[/](\d{2})[/](\d{4})$/;
var dateMask2=/^(\d{1})[/](\d{1})[/](\d{4})$/;
var dateNumbers=/^(0[1-9]|1[012])[/](0[1-9]|[12][0-9]|3[01])[/](19|20|21)\d\d$/;

    if(element!=undefined){
        //var docForm = document.forms[0];
        //setFieldBGColor(element.name, 'white', docForm);
        var value = element.value;
        if(value)  {
            var isInvalidFormat = false;
            if(value.length > 0){
               value = value.replace(/\-/g,'\/');
               var newValue = value.split('\/');
               if(newValue[1]===undefined) newValue[1] = '';
               if(newValue[2]===undefined) newValue[2] = '';
               if (newValue[0].length === 1) newValue[0] = '0' + newValue[0];
               if (newValue[1].length === 1) newValue[1] = '0' + newValue[1];
               if (newValue[2].length === 2) newValue[2] = '20' + newValue[2];
               value = newValue[0] + '/' + newValue[1] + '/' +  newValue[2];
               if (!dateNumbers.exec(value)){
                    isInvalidFormat = true;
               }
               element.value = value;
 //               } else if (!dateMask.exec(value)) {
 //                   isInvalidFormat = true;
            }
            if (isInvalidFormat){
               //setFieldBGColor(element.name, '#ffcccc', docForm);
               if (showmsg===true) {
                alert (value + ' is an invalid Date. (Valid format is mm/dd/yyyy)');
                element.focus();
                element.value = '';
               }
               return false;
            } 
            //good date but must be within range
           // alert("bef = "+ bef + " and aft = " + aft);
           // alert ("Date is valid? " + Date.parse(element.value).between(new Date(bef), new Date(aft)));
           var mydate = Date.parse(element.value);
//             alert("Before " + bef + " and after " + aft);
           if (mydate > bef) {
               if (showmsg===true) {
                alert (value + ' Event dates must occur between DOB and case date. DOB cannot be in the future.');
                element.focus();
                element.value = '';
                return false;
               } 
            } 
           
            if (mydate<aft) {
               if (showmsg===true) {
                alert (value + ' Event dates or age too far back for case date');
                element.focus();
                element.value = '';
                return false;
               } 
            }

              
        }
    }
    return true;
}


function datecharsonly(e){
var kcode=e.charCode? e.charCode : e.keyCode;
   if (kcode!==8 && kcode!==47){ //if the key isn't the backspace key or the slash for dates (which we should allow)
     if (kcode<48||kcode>57) //if not a number
       return false; //disable key press
   }
}
function numbersonly(e){
var kcode=e.charCode? e.charCode : e.keyCode;
   if (kcode!==8 ){ //if the key isn't the backspace key 
     if (kcode<48||kcode>57) //if not a number
       return false; //disable key press
   }
}
