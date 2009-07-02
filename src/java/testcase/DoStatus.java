/*
 * DoStatus.java
 *
 * Created on July 16, 2008, 2:31 PM
 */

package testcase;

import manapp.*;
import login.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author lwaisanen
 * @version
 */
public class DoStatus extends HttpServlet
{
   /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
   protected void processRequest(HttpServletRequest request, HttpServletResponse response)
   throws ServletException, IOException
   {
      HttpSession session = request.getSession(false);
      if (session == null)
      {
         session.setAttribute("CurrAct", CConsts.LinkLoginPage);
         RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }
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
      String btntxt = request.getParameter("BtnAct");
      
      if (btntxt != null && btntxt.equals("LogOff"))
      {
         dbconn.shutDown();
         session.removeAttribute("DbConn");
         session.removeAttribute("UserItem");
         session.removeAttribute("CurTestGroup");
         session.invalidate();
         RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
         rd.forward(request, response);
         return;  
      }      

      String mytestgrp = request.getParameter("TestGroup");
      if (mytestgrp == null) mytestgrp = CConsts.TagNoValue;
      session.setAttribute("CurTestGroup", mytestgrp);
      
      if (btntxt != null && btntxt.equals("ChangeTestGroup"))
      {
         session.setAttribute("CurrAct", "StatusPage");
         RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }      
      
      if (btntxt != null && btntxt.equals("Status"))
      {
         session.setAttribute("CurrAct", "StatusPage");
         RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }      
      
      CTestList testlist = new CTestList();
      testlist.dbReadList(dbconn.getConnection(), mytestgrp);
      
      if (btntxt != null && btntxt.equals("Create"))
      {
         session.removeAttribute("TestCase");
         CTestItem testitem = new CTestItem();
         testitem.testgroupid = mytestgrp;
         testitem.testid = testlist.makeNewId(mytestgrp, "test", 8);
         
         testitem.createby = myuser.getUserId();
         session.setAttribute("TestCase", testitem);
         session.setAttribute("CurrAct", "EditPage");
         RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }      
      
      if (btntxt != null && btntxt.startsWith("Edit"))
      {
         String mytestid = btntxt.substring(4);
         String mykey = mytestgrp + "|" + mytestid;
         CTestItem testitem = (CTestItem) testlist.getObject(mykey);
         session.setAttribute("TestCase", testitem);
         session.setAttribute("CurrAct", "EditPage");
         RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }      
      
      if (btntxt != null && btntxt.startsWith("Detail"))
      {
         String mytestid = btntxt.substring(6);
         String mykey = mytestgrp + "|" + mytestid;
         CTestItem testitem = (CTestItem) testlist.getObject(mykey);
         session.setAttribute("TestCase", testitem);
         session.setAttribute("CurrAct", "DisplayPage");
         RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }      
      
      if (btntxt != null && btntxt.startsWith("Execute"))
      {
         String mytestid = btntxt.substring(7);
         String mykey = mytestgrp + "|" + mytestid;
         CTestItem testitem = (CTestItem) testlist.getObject(mykey);
         testitem.testresult = CConsts.StatusSent;
         testitem.dbWriteItem(dbconn.getConnection());
         
         Thread runtest = new CRunTest(props, mytestgrp, mytestid);
         runtest.setPriority(Thread.MIN_PRIORITY);
         runtest.start();
        
         session.setAttribute("CurrAct", "StatusPage");
         RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }      
      
      if (btntxt != null && btntxt.equals("Runall"))
      {
         for (int idx = 0; idx < testlist.getCount(); idx++)
         {
            CTestItem testitem = (CTestItem) testlist.getItem(idx);
            testitem.testresult = CConsts.StatusSent;
            testitem.dbWriteItem(dbconn.getConnection());
         }
         
         Thread runtest = new CRunTest(props, mytestgrp, CConsts.TagNoValue);
         runtest.setPriority(Thread.MIN_PRIORITY);
         runtest.start();
         
         session.setAttribute("CurrAct", "StatusPage");
         RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }      
      
      if (btntxt != null && btntxt.startsWith("Summary"))
      {
         session.setAttribute("CurrAct", "SummaryPage");
         RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }      

      // fall through -- return from whence you came
      session.setAttribute("CurrAct", "StatusPage");
      RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
      rd.forward(request, response);
   }
   
   // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
   /** Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
   throws ServletException, IOException
   {
      processRequest(request, response);
   }
   
   /** Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
   throws ServletException, IOException
   {
      processRequest(request, response);
   }
   
   /** Returns a short description of the servlet.
    */
   public String getServletInfo()
   {
      return "Short description";
   }
   // </editor-fold>
}
