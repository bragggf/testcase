/*
 * DoSetpw.java
 *
 * Created on June 5, 2008, 1:31 PM
 */

package login;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import dbconn.CDbConnMan;
import java.sql.Connection;

/**
 * Servlet to handle user password changes.
 */
public class DoSetpw extends HttpServlet
{
   private static final long serialVersionUID = 20080604L;

   protected void processRequest(HttpServletRequest request, HttpServletResponse response)
   throws ServletException, IOException
   {
      HttpSession session = request.getSession(false);
      if (session == null)
      {
         RequestDispatcher rd = request.getRequestDispatcher(manapp.CAppConsts.LinkLoginPage + ".jsp");
         rd.forward(request, response);
         return;
      }
      
      CUserItem myuser = (CUserItem) session.getAttribute("UserItem");
      if (myuser == null)
      {
         session.invalidate();
         RequestDispatcher rd = request.getRequestDispatcher(manapp.CAppConsts.LinkLoginPage + ".jsp");
         rd.forward(request, response);
         return;
      }
      
      String btntxt = request.getParameter("BtnAct");
      
      if (btntxt != null && btntxt.equals("Cancel"))
      {
         String tmp = (String) session.getAttribute("PwChange");
         session.removeAttribute("PwChange");
         
         if (tmp != null && CValidUser.PwChangeOptional.equals(tmp)) 
         {
            session.setAttribute("CurrAct", manapp.CAppConsts.LinkLoginSuccess);
            RequestDispatcher rd = request.getRequestDispatcher(manapp.CAppConsts.LinkCentral);
            rd.forward(request, response);
            return;
         }
         else
         {
            session.invalidate();
            RequestDispatcher rd = request.getRequestDispatcher(manapp.CAppConsts.LinkLoginPage + ".jsp");
            rd.forward(request, response);
            return;  
         }
      }
      
      if (btntxt != null && btntxt.equals("Save"))
      {
         String savemsg = ""; 
         String oldpass = request.getParameter("OldPass");
         if (oldpass == null) savemsg = "Enter old password.  ";
         String newpass = request.getParameter("NewPass");
         if (newpass == null) savemsg = savemsg + "Enter new password.  ";
         String confpass = request.getParameter("ConfPass");
         if (confpass == null) savemsg = savemsg + "Confirm new password.";
         
         if (savemsg.length() > 0)
         { 
            session.setAttribute("SaveMsg", savemsg);
            session.setAttribute("CurrAct", manapp.CAppConsts.LinkPassChange);
            RequestDispatcher rd = request.getRequestDispatcher(manapp.CAppConsts.LinkCentral);
            rd.forward(request, response);
            return;
         }
         
         ServletContext scontext = this.getServletContext();
         CDbConnMan dbconnman = (CDbConnMan) scontext.getAttribute("DbConnMan");   
         Connection conn = dbconnman.getConnection();
         savemsg = CPassWd.savePassWord(conn, myuser.getUserId(), oldpass, newpass, confpass);
         dbconnman.returnConnection(conn);
         
         if (savemsg.length() > 0)
         { 
            session.setAttribute("SaveMsg", savemsg);
            session.setAttribute("CurrAct", manapp.CAppConsts.LinkPassChange);
            RequestDispatcher rd = request.getRequestDispatcher(manapp.CAppConsts.LinkCentral);
            rd.forward(request, response);
            return;
         }
         
         session.removeAttribute("PwChange");
         session.setAttribute("CurrAct", manapp.CAppConsts.LinkLoginSuccess);
         RequestDispatcher rd = request.getRequestDispatcher(manapp.CAppConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }
      
      // wtf -- punt
      session.removeAttribute("DbConn");
      session.removeAttribute("UserItem");
      session.removeAttribute("SaveMsg");
      session.removeAttribute("CurrAct");
      session.removeAttribute("PwChange");
      session.invalidate();
      RequestDispatcher rd = request.getRequestDispatcher(manapp.CAppConsts.LinkLoginPage + ".jsp");
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
