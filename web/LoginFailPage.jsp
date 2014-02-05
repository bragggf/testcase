<%@page contentType="text/html; charset=ISO-8859-1"%>
<%@page pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<%@ page import="
manapp.*
" %>
  <title><%=CAppConsts.WebAppTitle%></title>
  <LINK REL='StyleSheet' HREF='testcase.css' TYPE='text/css' MEDIA='screen,print'>
</head>
<body>
<div class='topband'>
  <div class='logodiv'>
    <IMG src='images/AltarumLogo.png' alt='<%=CAppConsts.WebAppLogoAlt%>' TITLE='<%=CAppConsts.WebAppLogoTitle%>'>
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

<p class='Blurb'>Your login attempt has failed.</p>
<%
try
{
   String reason = (String) session.getAttribute("FailReason");
   session.removeAttribute("FailReason");
   if ((reason != null) && (reason.length() > 0))
   {
%>
<p class='Blurb'><%=reason%></p>
<%
   }
   String entrylink = "LoginPage.jsp";
%>
<A href='<%=entrylink%>'>Try again</A>
<%
}
catch (Exception ex)
{
   manapp.CLogError.logError(CAppConsts.ErrorFile, false, "Error producing jsp page: ", ex);
   out.print("An error occurred while producing this page");
}
%>
</div>
</div>
</body>
</html>
