/*
 * DoStatus.java
 *
 * Created on July 16, 2008, 2:31 PM
 */

package testcase;

import java.io.*;
import java.sql.Connection;
import javax.servlet.*;
import javax.servlet.http.*;

import manapp.*;
import login.*;
import dbconn.CDbConnMan;

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
         session.setAttribute("CurrAct", CAppConsts.LinkLoginPage);
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }
      CUserItem myuser = (CUserItem) session.getAttribute("UserItem");

      manapp.CAppProps props = (manapp.CAppProps) session.getAttribute("AppProps");
      if (props == null) 
      {
         props = new manapp.CAppProps(CAppConsts.AppPropFile);
         session.setAttribute("AppProps", props);
      }

      ServletContext scontext = this.getServletContext();
      CDbConnMan dbconnman = (CDbConnMan) scontext.getAttribute("DbConnMan");   
      CDbConnMan remconnman = (CDbConnMan) scontext.getAttribute("RemConnMan");   
      
      String btntxt = request.getParameter("BtnAct");
      
      if (btntxt != null && btntxt.equals("LogOff"))
      {
         session.removeAttribute("UserItem");
         session.removeAttribute("CurTestGroup");
         session.invalidate();
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
         rd.forward(request, response);
         return;  
      }      

      String mytestgrp = request.getParameter("TestGroup");
      if (mytestgrp == null) mytestgrp = CAppConsts.TagNoValue;
      session.setAttribute("CurTestGroup", mytestgrp);
      
      if (btntxt != null && btntxt.equals("ChangeTestGroup"))
      {
         session.setAttribute("CurrAct", "StatusPage");
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }      
      
      if (btntxt != null && btntxt.equals("Status"))
      {
         session.setAttribute("CurrAct", "StatusPage");
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }      
      
      CTestList testlist = new CTestList();
      Connection conn = dbconnman.getConnection(); 
      testlist.dbReadList(conn, mytestgrp);
      dbconnman.returnConnection(conn);
      
      if (btntxt != null && btntxt.equals("Create"))
      {
         session.removeAttribute("TestCase");
         CTestItem testitem = new CTestItem();
         testitem.testgroupid = mytestgrp;
         testitem.testid = testlist.makeNewId(mytestgrp, "test", 8);
         
         testitem.createby = myuser.getUserId();
         session.setAttribute("TestCase", testitem);
         session.setAttribute("CurrAct", "EditPage");
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
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
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
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
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }      
      
      if (btntxt != null && btntxt.startsWith("Execute"))
      {
         String mytestid = btntxt.substring(7);
         String mykey = mytestgrp + "|" + mytestid;
         CTestItem testitem = (CTestItem) testlist.getObject(mykey);
         testitem.testresult = CAppConsts.StatusSent;
         
         conn = dbconnman.getConnection(); 
         testitem.dbWriteItem(conn);
         dbconnman.returnConnection(conn);
         
         Thread runtest = new CRunTest(props, dbconnman, remconnman, mytestgrp, mytestid);
         runtest.setPriority(Thread.MIN_PRIORITY);
         runtest.start();
        
         session.setAttribute("CurrAct", "StatusPage");
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }      
      
      if (btntxt != null && btntxt.equals("Runall"))
      {
         for (int idx = 0; idx < testlist.getCount(); idx++)
         {
            CTestItem testitem = (CTestItem) testlist.getItem(idx);
            testitem.testresult = CAppConsts.StatusSent;
            conn = dbconnman.getConnection(); 
            testitem.dbWriteItem(conn);
            dbconnman.returnConnection(conn);
         }
         
         Thread runtest = new CRunTest(props, dbconnman, remconnman, mytestgrp, CAppConsts.TagNoValue);
         runtest.setPriority(Thread.MIN_PRIORITY);
         runtest.start();
         
         session.setAttribute("CurrAct", "StatusPage");
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }      
      
      if (btntxt != null && btntxt.startsWith("Summary"))
      {
         session.setAttribute("CurrAct", "SummaryPage");
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }      

      // fall through -- return from whence you came
      session.setAttribute("CurrAct", "StatusPage");
      RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
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
