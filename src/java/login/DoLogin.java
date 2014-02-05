/*
 * DoLogin.java
 *
 * Created on June 2, 2005, 12:52 PM
 */

package login;

import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.text.*;
import java.util.Date;
import manapp.CAppConsts;
import dbconn.CDbConnMan;

/**
 * Servlet to process login requests.
 */
public class DoLogin extends HttpServlet
{
   private static final long serialVersionUID = 20080416L;
   
   /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
   protected void processRequest(HttpServletRequest request, HttpServletResponse response)
   throws ServletException, IOException
   {
      HttpSession session = request.getSession(true);
      CLoginProps lgprops = new CLoginProps(); 
      String myip = request.getRemoteAddr();

      String user = request.getParameter("UserId");
      user = user.substring(0, Math.min(user.length(), lgprops.MaxUserLeng));
      user = user.toLowerCase();
      if (!user.matches("[a-z0-9_\\.]+"))
      {
         String failreason = "Invalid user name/password combination.";
         logUsage(lgprops, myip, user, "Failure", failreason);
         session.setAttribute("FailReason", failreason);
         RequestDispatcher rd = request.getRequestDispatcher(manapp.CAppConsts.LinkLoginFailure + ".jsp");
         rd.forward(request, response);
         return;
      }

      String passwd = request.getParameter("PassWd"); 
      passwd = passwd.substring(0, Math.min(passwd.length(), lgprops.MaxPassLeng));

      ServletContext scontext = this.getServletContext();
      CDbConnMan dbconnman = (CDbConnMan) scontext.getAttribute("DbConnMan");   
      Connection conn = dbconnman.getConnection(); 
      CValidUser valuser = new CValidUser();
      boolean isval = valuser.isValidUser(conn, user, passwd);
      dbconnman.returnConnection(conn);
      
      if (!isval)
      {
         String failreason = valuser.failreason;
         logUsage(lgprops, myip, user, "Failure", failreason);
         session.setAttribute("FailReason", failreason);
         RequestDispatcher rd = request.getRequestDispatcher(manapp.CAppConsts.LinkLoginFailure + ".jsp");
         rd.forward(request, response);
         return;
      }
      
      CUserItem myuser = new CUserItem();
      myuser.setUserId(user);
      myuser.setRole(valuser.role);
      session.setAttribute("UserItem", myuser);
      
      if (valuser.failreason.length() > 0)
      {
         logUsage(lgprops, myip, user, "Success", "Invoke Password Change");
         session.setAttribute("CurrAct", manapp.CAppConsts.LinkPassChange);
         session.setAttribute("PwChange", CValidUser.PwChangeRequire);
         RequestDispatcher rd = request.getRequestDispatcher(manapp.CAppConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }

      logUsage(lgprops, myip, user, "Success", "");
      session.setAttribute("CurrAct", manapp.CAppConsts.LinkLoginSuccess);
      RequestDispatcher rd = request.getRequestDispatcher(manapp.CAppConsts.LinkCentral);
      rd.forward(request, response);
   }

   protected synchronized void logUsage(CLoginProps aprops, String aip, String auser, String astatus, String areason)
   {
      try
      {
         SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
         Date dt = new Date();
         String datestr = df.format(dt);

         FileOutputStream logfos = new FileOutputStream(aprops.UsageLogFile, true);
         PrintWriter logout = new PrintWriter(logfos);
         logout.println(datestr + "|" + aip + "|" + auser + "|" + manapp.CAppConsts.WebAppAbbr + " " + manapp.CAppConsts.WebAppVersion + "|" + astatus + "|" + areason);
         logout.close();
      }
      catch (Exception ex)
      {
         dbconn.CDbError.logError(aprops.ErrorLogFile, false, "DoLogin.logUsage: ", ex);
      }
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
      return "Login request processing";
   }
   // </editor-fold>
}
