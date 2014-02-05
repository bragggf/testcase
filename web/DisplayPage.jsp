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
dbconn.*,
java.sql.*,
java.util.Date,
java.text.SimpleDateFormat
" %>
  <title><%=CAppConsts.WebAppTitle%></title>
  <LINK REL='StyleSheet' HREF='testcase.css' TYPE='text/css' MEDIA='screen,print'>
  <script type="text/javascript" SRC="javascript/WRecSet.js"></script>
  <script type="text/javascript" SRC="javascript/selfuns.js"></script>
  <script type="text/javascript" SRC="javascript/buttons.js"></script>
  <script type="text/javascript" SRC="javascript/vaccine.js"></script>
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
    <A OnMouseDown='javascript:SwapBtn("Return","ReturnDn")' 
               OnMouseUp='javascript:SwapBtn("Return","ReturnUp")'
               HREF='javascript:DoSubmit("DisplayForm","Cancel")'>
               <IMG class='btnimg' name='Return' ID='Return' alt='Return button' 
                    src='images/ReturnUp.gif'></A>
  </div>
</div>
<div class='rightband'>
</div>
  
<div class='centerband'>
  <form name='DisplayForm' id='DisplayForm' action='<%=CAppConsts.JspLinkCentral%>' method=post>
  <div class='pickdiv'>
    <input type='hidden' name='ReqAct' id='ReqAct' value='DoDisplay'>
    <input type='hidden' name='BtnAct' id='BtnAct' value='error'>
    <input type='hidden' name='DetAct' id='DetAct' value='error'>
<% 
   //CUserItem myuser = (CUserItem) session.getAttribute("UserItem");

   ServletContext scontext = this.getServletContext();
   CDbConnMan dbconnman = (CDbConnMan) scontext.getAttribute("DbConnMan");   

   String mytestgrp = (String) session.getAttribute("CurTestGroup");
   if (mytestgrp == null) mytestgrp = CAppConsts.TagNoValue;
   String myfc1 = (String) session.getAttribute("CurFC1");
   if (myfc1 == null) myfc1 = CAppConsts.DefaultForecaster;
   String myfc2 = (String) session.getAttribute("CurFC2");
   if (myfc2 == null) myfc2 = CAppConsts.TagNoValue;
   String viewfc = (String) session.getAttribute("ViewResults");
   if (viewfc == null) viewfc = CAppConsts.DefaultForecaster;

   Connection conn = dbconnman.getConnection(); 
  // CCodeDesc testgroups = new CCodeDesc(conn, "TestGroupTbl", "TestGroupId", "TestGroupNm", "TestGroupSrt");

   CTestItem testcase = (CTestItem) session.getAttribute("TestCase");
   testcase.dbReadDetail(conn);
%>


    <%=testcase.showDisplay(conn,viewfc)%>

    </dl>
  </div> 
<% 
   dbconnman.returnConnection(conn);
%>
</form>
</div>
</body>
</html>
