<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ page import="
testcase.*,
manapp.*,
java.sql.*,
java.util.Date,
java.text.SimpleDateFormat
" %>
  <title><%=CConsts.WebAppTitle%></title>
  <LINK REL='StyleSheet' HREF='testcase.css' TYPE='text/css' MEDIA='screen,print'>
  <script type="text/javascript" SRC="javascript/buttons.js"></script>
</head>

<body onload='javascript:unlockPage()'>
<div class='leftband'>
<IMG src='images/AltarumLogo.png' alt='<%=CConsts.WebAppLogoAlt%>' TITLE='<%=CConsts.WebAppLogoTitle%>'>
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
<A OnMouseDown='javascript:SwapBtn("LogOff","LogOffDn")' 
               OnMouseUp='javascript:SwapBtn("LogOff","LogOffUp")'
               HREF='javascript:DoSubmit("StatusForm","LogOff")'>
               <IMG class='btnimg' name='LogOff' ID='LogOff' alt='LogOff button' 
                    src='images/LogOffUp.gif'></A>
</div>
</div>
<div class='rightband'>
  <p class='verstxt'><%=CConsts.WebAppAbbr + " " + CConsts.WebAppVersion%></p>
</div>
<form name='StatusForm' id='StatusForm' action='<%=CConsts.JspLinkCentral%>' method=post>
<div class='centerband'>
  <div class='banner'>
    <h1><%=CConsts.WebAppTitle%></h1>  
  </div>
<% 
   manapp.CAppProps props = (manapp.CAppProps) session.getAttribute("AppProps");
   if (props == null) 
   {
      props = new manapp.CAppProps(CConsts.AppPropFile);
      session.setAttribute("AppProps", props);
   }

   manapp.CDbConnect dbconn = (manapp.CDbConnect) session.getAttribute("DbConn");
   if (dbconn == null) 
   {
      dbconn = new manapp.CDbConnect(props.DbConfigFile, props.ErrorLogFile, props.ErrMsgEcho);
      session.setAttribute("DbConn", dbconn);
   }

   String mytestgrp = (String) session.getAttribute("CurTestGroup");
   if (mytestgrp == null) mytestgrp = CConsts.TagNoValue;

   CCodeDesc testgroups = new CCodeDesc(dbconn.getConnection(), "TestGroupTbl", "TestGroupId", "TestGroupNm", "TestGroupSrt");
%>
  <div class='pickdiv'>
    <input type='hidden' name='ReqAct' id='ReqAct' value='DoStatus'>
    <input type='hidden' name='BtnAct' id='BtnAct' value='error'>
    <input type='hidden' name='DetAct' id='DetAct' value='error'>

    <table class='picktbl' summary=''>
      <tr>
        <th class='picktbl'><label for='TestGroup'>Test Group</label></th>
        <td class='picktbl'><select name='TestGroup' id='TestGroup' size=1 
                                    onchange='javascript:DoSubmit("StatusForm", "ChangeTestGroup")'>
              <option value='<%=CConsts.TagNoValue%>'><%=CConsts.TagNoLabel%></option>
              <%=testgroups.makeOptions(mytestgrp)%>
            </select></td>
      </tr>
    </table>
  </div> 
<% 
   if (!CConsts.TagNoValue.equals(mytestgrp))
   {
      CTestList testlist = new CTestList();
      testlist.dbReadList(dbconn.getConnection(), mytestgrp);
%>
     <%=testlist.showStatus()%>
<% 
   }
%>

</div> 
</form>
</body>
</html>
