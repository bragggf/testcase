<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%         
   response.setHeader("Expires", "Tuesday, 28 December 2004 12:00:00 GMT");
   response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
   response.addHeader("Cache-Control", "post-check=0, pre-check=0");
   response.setHeader("Pragma", "no-cache");         
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ page import="
java.io.*,
login.*,
manapp.*
" %>
  <title><%=CConsts.WebAppTitle%></title>
  <LINK REL='StyleSheet' HREF='testcase.css' TYPE='text/css' MEDIA='screen,print'>

<script type="text/javascript">
function DoAgreeBox()
{      
   if (document.LoginForm.agreebox.checked == true)
      document.LoginForm.Submit.disabled=false;
   else
      document.LoginForm.Submit.disabled=true;
}
</script>
<%
   CAppProps props = new CAppProps(CConsts.AppPropFile); 
%>
</head>
<body>
<div class='leftband'>
  <IMG src='images/AltarumLogo.png' alt='<%=CConsts.WebAppLogoAlt%>' TITLE='<%=CConsts.WebAppLogoTitle%>'>
</div>
<div class='rightband'>
  <p class='verstxt'><%=CConsts.WebAppAbbr + " " + CConsts.WebAppVersion%></p>
</div>
<div class='centerband'>
<form name='LoginForm' action='<%=CConsts.JspLinkCentral%>' method=post>
<div>
  <INPUT type=HIDDEN name=ReqAct value='DoLogin'>

  <div class='banner'>
    <h1><%=CConsts.WebAppTitle%></h1>  
  </div>

<% 
try
{
   FileReader frd = new FileReader(props.LoginWarnFile);
   BufferedReader finp = new BufferedReader(frd);

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
   out.println("An error occurred while attempting to include warning message.");   
}
%>
  
  <div class='AgreeWarnDiv'>
     <p>&nbsp; In order to access this system, you must indicate agreement with these 
     conditions by checking this box.
     &nbsp;&nbsp;<input type=checkbox name='agreebox' id='agreebox' onclick='javascript:DoAgreeBox()'>&nbsp;<label for='agreebox'>I agree.</label></p>
  </div>

  <p class='Blurb'>This web application uses session to maintain state information, and uses 
  a session cookie to identify the session.  It also uses JavaScript to control navigation. 
  If your browser is configured to not allow session cookies or to not allow JavaScript for 
  this site, you will be unable to use this application.</p>
  <div>
    <table summary=''>
      <tr>
        <td>&nbsp;</td>
        <th class='logcolhead'>Login</th>
      </tr>
      <tr>
        <th class='logrowhead'><label for='UserId'>UserId:</label></th>
        <td><input type=text name='UserId' id='UserId' size='12' maxlength='70' value=''></td>
      </tr>
      <tr>
        <th class='logrowhead'><label for='PassWd'>Password:</label></th>
        <td><input type=password name='PassWd' id='PassWd' size='12' maxlength='20' value=''></td> 
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td><INPUT TYPE=SUBMIT name='Submit' value='Submit'></td>
      </tr>
    </table>
  </div>
</div>
</form>
</div>
<SCRIPT type="text/javascript">
javascript:DoAgreeBox()
</SCRIPT>
</body>
</html>
