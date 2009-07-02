<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ page import="
testcase.*,
login.*,
manapp.*,
java.sql.*,
java.util.Date,
java.text.SimpleDateFormat
" %>
  <title><%=CConsts.WebAppTitle%></title>
  <LINK REL='StyleSheet' HREF='testcase.css' TYPE='text/css' MEDIA='screen,print'>
  <script type="text/javascript" SRC="javascript/WRecSet.js"></script>
  <script type="text/javascript" SRC="javascript/selfuns.js"></script>
  <script type="text/javascript" SRC="javascript/buttons.js"></script>
  <script type="text/javascript" SRC="javascript/vaccine.js"></script>
</head>

<body onload='javascript:unlockPage()'>
<div class='leftband'>
<IMG src='images/AltarumLogo.png' alt='<%=CConsts.WebAppLogoAlt%>' TITLE='<%=CConsts.WebAppLogoTitle%>'>
<div class='btnlist'>
<A OnMouseDown='javascript:SwapBtn("Save","SaveDn")' 
               OnMouseUp='javascript:SwapBtn("Save","SaveUp")'
               HREF='javascript:DoSubmit("EditForm","Save")'>
               <IMG class='btnimg' name='Save' ID='Save' alt='Save button' 
                    src='images/SaveUp.gif'></A>
<A OnMouseDown='javascript:SwapBtn("Cancel","CancelDn")' 
               OnMouseUp='javascript:SwapBtn("Cancel","CancelUp")'
               HREF='javascript:DoSubmit("EditForm","Cancel")'>
               <IMG class='btnimg' name='Cancel' ID='Cancel' alt='Cancel button' 
                    src='images/CancelUp.gif'></A>
</div>
</div>
<div class='rightband'>
  <p class='verstxt'><%=CConsts.WebAppAbbr + " " + CConsts.WebAppVersion%></p>
</div>
<form name='EditForm' id='EditForm' action='<%=CConsts.JspLinkCentral%>' method=post>
<div class='centerband'>
  <div class='banner'>
    <h1><%=CConsts.WebAppTitle%></h1>  
  </div>
<% 
   CUserItem myuser = (CUserItem) session.getAttribute("UserItem");

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

   CTestItem testcase = (CTestItem) session.getAttribute("TestCase");
   testcase.dbReadDetail(dbconn.getConnection());
%>
  <div class='pickdiv'>
    <input type='hidden' name='ReqAct' id='ReqAct' value='DoEdit'>
    <input type='hidden' name='BtnAct' id='BtnAct' value='error'>
    <input type='hidden' name='DetAct' id='DetAct' value='error'>


    <dl class='details'>
      <dt class='details'>Test Group</dt>
<% 
   if (CConsts.TagNoValue.equals(mytestgrp))
   {
%>
      <dd class='details'><select name='TestGroup' id='TestGroup' size=1 
                                    onchange='javascript:DoSubmit("StatusForm", "ChangeTestGroup")'>
              <option value='<%=CConsts.TagNoValue%>'><%=CConsts.TagNoLabel%></option>
              <%=testgroups.makeOptions(mytestgrp)%>
            </select></dd>
<% 
   }
   else
   {
%>
      <dd class='details'><%=testgroups.getDescByCode(mytestgrp)%></dd>
<% 
   }
%>
      <%=testcase.showEdit(dbconn.getConnection())%>

    </dl>
  </div> 
</div> 
</form>
</body>
</html>
