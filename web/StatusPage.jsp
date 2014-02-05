<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ page import="
testcase.*,
manapp.*,
dbconn.*,
java.sql.*,
java.util.Date,
java.text.SimpleDateFormat
" %>
  <title><%=CAppConsts.WebAppTitle%></title>
  <LINK REL='StyleSheet' HREF='testcase.css' TYPE='text/css' MEDIA='screen,print'>
  <script type="text/javascript" SRC="javascript/buttons.js"></script>
</head>

<body onload='javascript:unlockPage()'>
<div class='topband'>
  <div class='logodiv'>
    <IMG src='<%=CAppConsts.WebAppLogo%>' alt='<%=CAppConsts.WebAppLogoAlt%>' TITLE='<%=CAppConsts.WebAppLogoTitle%>'>
  </div>
  <div class='versdiv'>
    <p class='verstxt'><%=CAppConsts.WebAppAbbr + " " + CAppConsts.WebAppVersion%></p>
  </div>
  <div class='banner'>
    <h1><%=CAppConsts.WebAppTitle%></h1>
  </div>
</div>
<div class='leftband'>
  <div class='btnlist'>
    <A OnMouseDown='javascript:SwapBtn("Create","CreateDn")'
               OnMouseUp='javascript:SwapBtn("Create","CreateUp")'
               HREF='javascript:DoSubmit("StatusForm","Create")'>
               <IMG class='btnimg' name='Create' ID='Create' alt='Create button'
                    src='images/CreateUp.gif'></A>
    <A OnMouseDown='javascript:SwapBtn("Status","StatusDn")'
               OnMouseUp='javascript:SwapBtn("Status","StatusUp")'
               HREF='javascript:DoSubmit("StatusForm","Status")'>
               <IMG class='btnimg' name='Status' ID='Status' alt='Update status button'
                    src='images/StatusUp.gif'></A>
    <A OnMouseDown='javascript:SwapBtn("Runall","RunallDn")'
               OnMouseUp='javascript:SwapBtn("Runall","RunallUp")'
               HREF='javascript:DoSubmit("StatusForm","Runall")'>
               <IMG class='btnimg' name='Runall' ID='Runall' alt='Run all button'
                    src='images/RunallUp.gif'></A>
    <A OnMouseDown='javascript:SwapBtn("Summary","SummaryDn")'
               OnMouseUp='javascript:SwapBtn("Summary","SummaryUp")'
               HREF='javascript:DoSubmit("StatusForm","Summary")'>
               <IMG class='btnimg' name='Summary' ID='Summary' alt='Summary report button'
                    src='images/SummaryUp.gif'></A>

    <A OnMouseDown='javascript:SwapBtn("Import","ImportDn")'
               OnMouseUp='javascript:SwapBtn("Import","ImportUp")'
               HREF='javascript:DoSubmit("StatusForm","Import")'>
               <IMG class='btnimg' name='Import' ID='Import' alt='Import button'
                    src='images/ImportUp.gif'></A>

    <A OnMouseDown='javascript:SwapBtn("Export","ExportDn")'
               OnMouseUp='javascript:SwapBtn("Export","ExportUp")'
               HREF='javascript:DoSubmit("StatusForm","Export")'>
               <IMG class='btnimg' name='Export' ID='Export' alt='Export button'
                    src='images/ExportUp.gif'></A>

    <A OnMouseDown='javascript:SwapBtn("LogOff","LogOffDn")'
               OnMouseUp='javascript:SwapBtn("LogOff","LogOffUp")'
               HREF='javascript:DoSubmit("StatusForm","LogOff")'>
               <IMG class='btnimg' name='LogOff' ID='LogOff' alt='LogOff button'
                    src='images/LogOffUp.gif'></A>
  </div>
</div>
<div class='rightband'>
</div>
<div class='centerband'>
  <form name='StatusForm' id='StatusForm' action='<%=CAppConsts.JspLinkCentral%>' method=post>
  <div class='pickdiv'>
    <input type='hidden' name='ReqAct' id='ReqAct' value='DoStatus'>
    <input type='hidden' name='BtnAct' id='BtnAct' value='error'>
    <input type='hidden' name='DetAct' id='DetAct' value='error'>
<%
   ServletContext scontext = this.getServletContext();
   CDbConnMan dbconnman = (CDbConnMan) scontext.getAttribute("DbConnMan");
   Connection conn = dbconnman.getConnection();

   String mytestgrp = (String) session.getAttribute("CurTestGroup");
   if (mytestgrp == null) mytestgrp = CAppConsts.TagNoValue;

   CCodeDesc testgroups = new CCodeDesc(conn, "TestGroupTbl", "TestGroupId", "TestGroupNm", "TestGroupSrt");
   CCodeDesc forecasters = new CCodeDesc(conn, "ForecasterTbl", "ForecasterId", "ForecasterNm", "ForecasterId");
   
   String myforecaster1=(String) session.getAttribute("CurFC1");
   String myforecaster2=(String) session.getAttribute("CurFC2");
   if (myforecaster1 == null) myforecaster1 = CAppConsts.DefaultForecaster;  //MCIR is default
   if (myforecaster2 == null) myforecaster2 = CAppConsts.TagNoValue; //none
   
%>

    <table class='picktbl' summary=''>
      <tr>
        <th class='picktbl'><label for='TestGroup'>Test Group</label></th>
        <td class='picktbl'><select name='TestGroup' id='TestGroup' size=1
                                    onchange='javascript:DoSubmit("StatusForm", "ChangeTestGroup")'>
              <option value='<%=CAppConsts.TagNoValue%>'><%=CAppConsts.TagNoLabel%></option>
              <%=testgroups.makeOptions(mytestgrp)%>
            </select></td>
        <th class='picktbl'><label for='FC1'>Forecaster#1</label></th>
        <td class='picktbl'><select name='FC1' id='FC1' size=1>
              <option value='<%=CAppConsts.DefaultForecaster%>'><%=forecasters.getDescByCode(CAppConsts.DefaultForecaster)%></option> 
            </select></td>
        <th class='picktbl'><label for='FC2'>Forecaster#2</label></th>
        <td class='picktbl'><select name='FC2' id='FC2' size=1
                                    onchange='javascript:if (value=="<%=CAppConsts.DefaultForecaster%>"){value="<%=CAppConsts.TagNoValue%>";}else{DoSubmit("StatusForm", "ChangeForecaster");}'>
              <option value='<%=CAppConsts.TagNoValue%>'></option>
              <%=forecasters.makeOptions(myforecaster2)%>
            </select></td>
      </tr>
    </table>
  </div>
<%
   if (!CAppConsts.TagNoValue.equals(mytestgrp))
   {
      CTestList testlist = new CTestList();
      testlist.dbReadList(conn, mytestgrp,myforecaster1,myforecaster2);
%>
<%=testlist.showStatus(forecasters.getDescByCode(myforecaster1),(myforecaster2.equals(CAppConsts.TagNoValue)?"":forecasters.getDescByCode(myforecaster2)))%>
<%
   }
   dbconnman.returnConnection(conn);
%>
  </form>
</div>
</body>
</html>
