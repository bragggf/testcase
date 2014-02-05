<%@page contentType="text/html; charset=ISO-8859-1"%>
<%@page pageEncoding="ISO-8859-1"%>
<%         
   response.setHeader("Expires", "Tuesday, 28 December 2004 12:00:00 GMT");
   response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
   response.addHeader("Cache-Control", "post-check=0, pre-check=0");
   response.setHeader("Pragma", "no-cache");         
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<%@ page import="
java.io.*,
login.*,
manapp.*
" %>
  <title><%=CAppConsts.WebAppTitle%></title>
  <LINK REL='StyleSheet' HREF='testcase.css' TYPE='text/css' MEDIA='screen,print'>
  <script type="text/javascript" SRC="javascript/buttons.js"></script>
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
  </div>
</div>  
<div class='leftband'>
</div>
<div class='rightband'>
</div>
<div class='centerband'>
  <div class='pickdiv'>

<%
CLoginProps props = new CLoginProps();

try
{
   BufferedReader finp = new BufferedReader(new FileReader(props.LoginPageAboveFile));
   String buf = finp.readLine();
   while (buf != null)
   {
      out.println(buf);
      buf = finp.readLine();
   }
   finp.close();
}
catch (Exception ex)
{
}
%>
     
     
  <form name='NavForm' id='NavForm' action='<%=CAppConsts.JspLinkCentral%>' method=post>
  <div>
  <INPUT type='hidden' name='ReqAct' value='DoLogin'>
  <INPUT type='hidden' name='BtnAct' value='DoLogin'>
  <div>
    <table summary=''>
      <tr>
        <td>&nbsp;</td>
        <th>Login</th>
      </tr> 
      <tr>
        <th><label for='UserId'>UserId:</label></th>
        <td><input type='text' name='UserId' id='UserId' size='24' maxlength='<%=props.MaxUserLeng%>' value=''></td>
      </tr>
      <tr>
        <th><label for='PassWd'>Password:</label></th>
        <td><input type='password' name='PassWd' id='PassWd' size='24' maxlength='<%=props.MaxPassLeng%>' value=''></td> 
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td><INPUT TYPE='button' name='Submit' value='Submit' onclick='javascript:DoLogin()'></td>
      </tr>
    </table>
  </div>
  </div>
  </form>
<%
try
{
   BufferedReader finp = new BufferedReader(new FileReader(props.LoginPageBelowFile));
   String buf = finp.readLine();
   while (buf != null)
   {
      out.println(buf);
      buf = finp.readLine();
   }
   finp.close();
}
catch (Exception ex)
{}
%>
      
      
  </div>
</div>
  
  
</body>
</html>
