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
  <div class='tablediv'>
  <input type='hidden' name='ReqAct' id='ReqAct' value='DoDisplay'>
  <input type='hidden' name='BtnAct' id='BtnAct' value='error'>
  <input type='hidden' name='DetAct' id='DetAct' value='error'>
<% 
   ServletContext scontext = this.getServletContext();
   CDbConnMan dbconnman = (CDbConnMan) scontext.getAttribute("DbConnMan");   

   Connection conn = dbconnman.getConnection(); 
   CCodeDesc testgroups = new CCodeDesc(conn, "TestGroupTbl", "TestGroupId", "TestGroupNm", "TestGroupSrt");
   
   java.text.DecimalFormat percfmt = new java.text.DecimalFormat("##0%");

%>

  <table class='result' summary='test case status summary'>
    <tr>
      <th class='result' scope=col>Test Group</th>
      <th class='result' scope=col>Cases</th>
      <th class='result' scope=col>Pass</th>
      <th class='result' scope=col>Fail</th>
      <th class='result' scope=col>Not Run</th>
    </tr> 
<% 
   int totpass = 0;
   int totfail = 0;
   int totnone = 0;
   int totcase = 0;
   
   for (int igrp = 0; igrp < testgroups.getCount(); igrp++)
   {
      String grpcode = testgroups.getCode(igrp);
      String grpname = testgroups.getDesc(igrp);
      CTestList testlist = new CTestList();
      testlist.dbReadList(conn, grpcode,CAppConsts.DefaultForecaster,"");

      int passcnt = testlist.getStatusCnt(CAppConsts.StatusPass);
      int failcnt = testlist.getStatusCnt(CAppConsts.StatusFail);
      int casecnt = testlist.getCount();
      int nonecnt = casecnt - passcnt - failcnt;

      totpass = totpass + passcnt;
      totfail = totfail + failcnt;
      totnone = totnone + nonecnt;
      totcase = totcase + casecnt;
      
      String passper = "--";
      String failper = "--";
      String noneper = "--";
      
      if (casecnt > 0)
      {
         passper = percfmt.format( ((double)passcnt)/((double)casecnt) );
         failper = percfmt.format( ((double)failcnt)/((double)casecnt) );
         noneper = percfmt.format( ((double)nonecnt)/((double)casecnt) );
      }
%>
    <tr>
      <td class='result'><%=grpname%></td>
      <td class='resultnum'><%=Integer.toString(casecnt)%></td>
      <td class='resultnum'><%=passper%></td>
      <td class='resultnum'><%=failper%></td>
      <td class='resultnum'><%=noneper%></td>
    </tr>
<% 
   }
   
   dbconnman.returnConnection(conn);
   String tpassper = "--";
   String tfailper = "--";
   String tnoneper = "--";
     
   if (totcase > 0)
   {
      tpassper = percfmt.format( ((double)totpass)/((double)totcase) );
      tfailper = percfmt.format( ((double)totfail)/((double)totcase) );
      tnoneper = percfmt.format( ((double)totnone)/((double)totcase) );
   }
%>
    <tr>
      <td class='result'>Total</td>
      <td class='resultnum'><%=Integer.toString(totcase)%></td>
      <td class='resultnum'><%=tpassper%></td>
      <td class='resultnum'><%=tfailper%></td>
      <td class='resultnum'><%=tnoneper%></td>
    </tr>
  </table>
  </div> 
  </form>
</div> 
</body>
</html>
