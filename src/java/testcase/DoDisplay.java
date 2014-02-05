/*
 * DoDisplay.java
 *
 * Created on July 17, 2008, 5:42 PM
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
public class DoDisplay extends HttpServlet
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
      
      String btntxt = request.getParameter("BtnAct");
      
      if (btntxt != null && btntxt.equals("Cancel"))
      {
         session.setAttribute("CurrAct", "StatusPage");
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }      
      
      // fall through -- return from whence you came
      session.setAttribute("CurrAct", "DisplayPage");
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
