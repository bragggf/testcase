<%@ page contentType="text/html; charset=ISO-8859-1" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ page import="
login.*,
manapp.*
" %>
  <title><%=CConsts.WebAppTitle%></title>
  <LINK REL='StyleSheet' HREF='testcase.css' TYPE='text/css' MEDIA='screen,print'>
</head>
<body>
<div class='leftband'>
  <IMG src='images/AltarumLogo.png' alt='<%=CConsts.WebAppLogoAlt%>' TITLE='<%=CConsts.WebAppLogoTitle%>'>
</div>
<div class='rightband'>
  <p class='verstxt'><%=CConsts.WebAppAbbr + " " + CConsts.WebAppVersion%></p>
</div>
<div class='centerband'>
  <div class='banner'>
    <h1><%=CConsts.WebAppTitle%></h1>  
  </div>

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
<A href='<%=entrylink%>'>Care to try again?</A>
<%
}
catch (Exception ex)
{
   manapp.CLogError.logError(CConsts.ErrMsgFile, false, "Error producing jsp page: ", ex);
   out.print("An error occurred while producing this page");
}
%>
</div>
</body>
</html>
