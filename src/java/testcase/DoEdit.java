/*
 * DoEdit.java
 *
 * Created on July 16, 2008, 4:59 PM
 */

package testcase;

import manapp.*;
import login.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import dbconn.CDbConnMan;

/**
 *
 * @author lwaisanen
 * @version
 */
public class DoEdit extends HttpServlet
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
         props = new manapp.CAppProps();
         session.setAttribute("AppProps", props);
      }

      ServletContext scontext = this.getServletContext();
      CDbConnMan dbconnman = (CDbConnMan) scontext.getAttribute("DbConnMan");

      String btntxt = request.getParameter("BtnAct");

      if (btntxt != null && btntxt.equals("Cancel"))
      {
         session.setAttribute("CurrAct", "StatusPage");
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }

      if (btntxt != null && btntxt.equals("Save"))
      {
          System.out.println("save request= " +request);
         CTestItem testcase = (CTestItem) session.getAttribute("TestCase");
         try
         {
            testcase.updateItem(request);
            if (CAppConsts.TagNoValue.equals(testcase.testgroupid))
            {
               session.setAttribute("CurrAct", "EditPage");
               RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
               rd.forward(request, response);
               return;
            }
            Connection conn = dbconnman.getConnection();
            testcase.dbWriteItem(conn);
            testcase.dbWriteDetail(conn);
            dbconnman.returnConnection(conn);

         }
         catch (Exception ex)
         {
            CLogError.logError(CAppConsts.ErrorFile, false, "CTestItem.updateItem ", ex);
         }

         session.setAttribute("CurrAct", "StatusPage");
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }

 /*     if (btntxt != null && btntxt.equals("Scale"))
      {
         CTestItem testcase = (CTestItem) session.getAttribute("TestCase");
         try
         {
            testcase.updateItem(request);
            if (CAppConsts.TagNoValue.equals(testcase.testgroupid))
            {
               session.setAttribute("CurrAct", "EditPage");
               RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
               rd.forward(request, response);
               return;
            }
            Connection conn = dbconnman.getConnection();
            testcase.dbWriteItem(conn);
            testcase.dbWriteDetail(conn);

            String oldstr = request.getParameter("HideDate");
            String newstr = request.getParameter("BaseDate");
            if (oldstr == null || oldstr.length() < 8 ||
                newstr == null || newstr.length() < 8)
            {
               dbconnman.returnConnection(conn);
               session.setAttribute("CurrAct", "EditPage");
               RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
               rd.forward(request, response);
               return;
            }
            testcase.updateBaseDate(oldstr, newstr);
            testcase.dbWriteItem(conn);
            testcase.dbWriteDetail(conn);
            dbconnman.returnConnection(conn);
         }
         catch (Exception ex)
         {
            CLogError.logError(CAppConsts.ErrorFile, false, "DoEdit Scale ", ex);
         }

         session.setAttribute("CurrAct", "EditPage");
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }

      if (btntxt != null && btntxt.equals("Calc"))
      {
         CTestItem testcase = (CTestItem) session.getAttribute("TestCase");
         try
         {
            testcase.updateItem(request);
            if (CAppConsts.TagNoValue.equals(testcase.testgroupid))
            {
               session.setAttribute("CurrAct", "EditPage");
               RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
               rd.forward(request, response);
               return;
            }
            Connection conn = dbconnman.getConnection();
            testcase.dbWriteItem(conn);
            testcase.dbWriteDetail(conn);
            dbconnman.returnConnection(conn);
         }
         catch (Exception ex)
         {
            CLogError.logError(CAppConsts.ErrorFile, false, "DoEdit Calc ", ex);
         }

         session.setAttribute("CurrAct", "EditPage");
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }
*/
      // fall through -- return from whence you came
      session.setAttribute("CurrAct", "EditPage");
      RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
      rd.forward(request, response);
      return;
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
