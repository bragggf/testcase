<%-- 
    Document   : LoginPage
    Description: Login page
    Created on : Dec 8, 2008, 12:03:46 PM
    Author     : lwaisanen
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <%@ page import="login.*,manapp.*" %>
  <title><%=manapp.CAppConsts.WebAppTitle%></title>
  <LINK REL='StyleSheet' HREF='testcase.css' TYPE='text/css' MEDIA='screen,print'>
  <script type="text/javascript" SRC="javascript/utils.js"></script>
  <script type="text/javascript" SRC="javascript/checkpass.js"></script> 
</head> 

<body>
<div class='topband'>
  <div class='logodiv'>
    <IMG src='<%=CAppConsts.WebAppLogo%>' alt='<%=CAppConsts.WebAppLogoAlt%>' TITLE='<%=CAppConsts.WebAppLogoTitle%>'>
  </div>
  <div class='versdiv'>
    <p class='verstxt'><%=CAppConsts.WebAppAbbr + " " + CAppConsts.WebAppVersion%></p>
  </div>
  <div class='banner'>
    <h1><%=CAppConsts.WebAppTitle%></h1>  
    <h2>Set Password</h2>
  </div>
</div>  
<div class='leftband'>
</div>
<div class='rightband'>
</div>
   
<div class='centerband'>
  <div class='pickdiv'>
<%
   login.CLoginProps lgprops = new login.CLoginProps();
   String pwchg = (String) session.getAttribute("PwChange");
   if (pwchg == null || pwchg.equals(CValidUser.PwChangeRequire))
   {
%>
     <p class='pwexpire'>Your password has expired.</p>
<%
   }    
%>
Your new password must be at least <%=lgprops.MinPassLeng%> characters in length.  It must contain
      <ul>
        <li>At least <%=lgprops.MinPassUpper%> upper case characters</li>
        <li>At least <%=lgprops.MinPassLower%> lower case characters</li>
        <li>At least <%=lgprops.MinPassDigit%> numeric characters</li>
        <li>At least <%=lgprops.MinPassSpecial%> special characters ~ ` ! @ # $ % ^ &amp; * ( ) _ - + = { } [ ] &lt; &gt; , . ? / | \ : ; " ' </li>
        <li>At least <%=lgprops.MinPassDiff%> characters that were not used in your old password.</li>
      </ul>

  <form name='NavForm' id='NavForm' action='<%=manapp.CAppConsts.JspLinkCentral%>' method=post>
  <div>
    <input type='hidden' name='ReqAct' id='ReqAct' value='DoSetpw'>
    <input type='hidden' name='BtnAct' id='BtnAct' value='error'>
    <input type='hidden' name='DetAct' id='DetAct' value='error'>

    <table summary=''>
      <tr>
        <th class='logrowhead'><label for='OldPass'>Old Password:</label></th>
        <td class='picktbl'><input type='password' name='OldPass' id='OldPass' size='20' maxlength='<%=Integer.toString(lgprops.MaxPassLeng)%>' value=''></td> 
        <td>&nbsp;</td>
      </tr>

      <tr>
        <th class='logrowhead'><label for='NewPass'>New Password:</label></th>
        <td class='picktbl'><input type='password' name='NewPass' id='NewPass' size='20' maxlength='<%=Integer.toString(lgprops.MaxPassLeng)%>' value=''></td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <th class='logrowhead'><label for='ConfPass'>Confirm New Password:</label></th>
        <td class='picktbl'><input type='password' name='ConfPass' id='ConfPass' size='20' maxlength='<%=Integer.toString(lgprops.MaxPassLeng)%>' value=''></td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td class='picktbl'><input type='button' name='SaveBtn' value='Save'
            onclick='javascript:DoSetPw("Save",<%=lgprops.MinPassLeng%>,<%=lgprops.MinPassLower%>,<%=lgprops.MinPassUpper%>,<%=lgprops.MinPassDigit%>,<%=lgprops.MinPassSpecial%>,<%=lgprops.MinPassDiff%>)'></td>
        <td class='picktbl'><input type='button' name='CancelBtn' value='Cancel' onclick='javascript:DoButton("Cancel")'></td>
      </tr>
    </table>
  </div>
  </form>
</div>
</div>
</body>
</html>
