<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ page import="
java.sql.*,
manapp.*,
dbconn.*,
testcase.*
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
    <A OnMouseDown='javascript:SwapBtn("Save","SaveDn")'
               OnMouseUp='javascript:SwapBtn("Save","SaveUp")'
               HREF='javascript:DoSubmit("ImportForm","Import")'>
               <IMG class='btnimg' name='Save' ID='Save' alt='Save button'
                    src='images/SaveUp.gif'></A>
    <A OnMouseDown='javascript:SwapBtn("Cancel","CancelDn")'
               OnMouseUp='javascript:SwapBtn("Cancel","CancelUp")'
               HREF='javascript:DoSubmit("ImportForm","Cancel")'>
               <IMG class='btnimg' name='Cancel' ID='Cancel' alt='Cancel button'
                    src='images/CancelUp.gif'></A>
  </div>
</div>

<div class='rightband'>
</div>

<%
   ServletContext scontext = this.getServletContext();
   CDbConnMan dbconnman = (CDbConnMan) scontext.getAttribute("DbConnMan");
   Connection conn = dbconnman.getConnection();

   String mytestgrp = (String) session.getAttribute("CurTestGroup");
   if (mytestgrp == null) mytestgrp = CAppConsts.TagNoValue;

   CCodeDesc testgroups = new CCodeDesc(conn, "TestGroupTbl", "TestGroupId", "TestGroupNm", "TestGroupSrt");
   dbconnman.returnConnection(conn);
%>

<div class='centerband'>
  <form name='ImportForm' id='ImportForm' action='DoImport' ENCTYPE="multipart/form-data" method=post>
    <div class='pickdiv'>
      <input type='hidden' name='ReqAct' id='ReqAct' value='DoImport'>
      <input type='hidden' name='BtnAct' id='BtnAct' value='Import'>
      <input type='hidden' name='DetAct' id='DetAct' value='error'>

      <table summary=''>
        <tr>
          <th><LABEL for='file'>Test Case file:</LABEL></th>
          <td><INPUT type=FILE name='file' id='file'></td>
        </tr>
      <tr>
        <th class='picktbl'><label for='TestGroup'>Test Group</label></th>
        <td class='picktbl'><select name='TestGroup' id='TestGroup' size=1
                                    onchange='javascript:DoSubmit("StatusForm", "ChangeTestGroup")'>
              <option value='<%=CAppConsts.TagNoValue%>'><%=CAppConsts.TagNoLabel%></option>
              <%=testgroups.makeOptions(mytestgrp)%>
            </select></td>
      </tr>
        </tr>
      </table>
    </div>
  </form>
</div>
</body>
</html>