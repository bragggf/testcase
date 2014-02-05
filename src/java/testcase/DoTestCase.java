/*
 * DoTestCase.java
 *
 * Created on July 15, 2008, 3:33 PM
 */

package testcase;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import dbconn.CDbConnMan;
import manapp.*;
import login.*;

/** central dispatch servlet */
public class DoTestCase extends HttpServlet
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
         String reqact = (String) request.getParameter("ReqAct");
         if ((reqact != null) && (reqact.equals("DoLogin")))
         {
            RequestDispatcher rd = request.getRequestDispatcher("/DoLogin");
            rd.forward(request, response);
            return;
         }
         response.reset();
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkLoginPage + ".jsp");
         rd.forward(request, response);
         return;
      }      

      String curract = (String) session.getAttribute("CurrAct");
      if ((curract == null) || curract.equals(""))
      {
         curract = request.getParameter("ReqAct");
         if ((curract == null) || (curract.equals(""))) curract = "GetNode";
      }
      session.removeAttribute("CurrAct");
      
      if (curract.equals("DoLogin"))
      {
         RequestDispatcher rd = request.getRequestDispatcher("/DoLogin");
         rd.forward(request, response);
         return;
      }

      if (curract.equals(CAppConsts.LinkLoginPage))
      {
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkLoginPage + ".jsp");
         rd.forward(request, response);
         return;
      }

      if (curract.equals(CAppConsts.LinkLoginFailure))
      {
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkLoginFailure + ".jsp");
         rd.forward(request, response);
         return;
      }

      CUserItem myuser = (CUserItem) session.getAttribute("UserItem");
      if (myuser == null)
      {
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkLoginPage + ".jsp");
         rd.forward(request, response);
         return;
      }

      if (curract.equals(manapp.CAppConsts.LinkPassChange))
      {
         RequestDispatcher rqd = request.getRequestDispatcher(manapp.CAppConsts.LinkPassChange + ".jsp");
         rqd.forward(request, response);
         return;
      }

      if (curract.equals("DoSetpw"))
      {
         RequestDispatcher rqd = request.getRequestDispatcher("DoSetpw");
         rqd.forward(request, response);
         return;
      }

      if (curract.equals(CAppConsts.LinkLoginSuccess))
      {
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkLoginSuccess + ".jsp");
         rd.forward(request, response);
         return;
      }

      if (curract.equals("DoStatus"))
      {
         RequestDispatcher rd = request.getRequestDispatcher("/DoStatus");
         rd.forward(request, response);
         return;
      }      

      if (curract.equals("EditPage"))
      {
         RequestDispatcher rd = request.getRequestDispatcher("EditPage.jsp");
         rd.forward(request, response);
         return;
      }      

      if (curract.equals("DoEdit"))
      {
         RequestDispatcher rd = request.getRequestDispatcher("/DoEdit");
         rd.forward(request, response);
         return;
      }      

      if (curract.equals("DisplayPage"))
      {
         RequestDispatcher rd = request.getRequestDispatcher("DisplayPage.jsp");
         rd.forward(request, response);
         return;
      }      

      if (curract.equals("DoDisplay"))
      {
         RequestDispatcher rd = request.getRequestDispatcher("/DoDisplay");
         rd.forward(request, response);
         return;
      }      

      if (curract.equals("SummaryPage"))
      {
         RequestDispatcher rd = request.getRequestDispatcher("SummaryPage.jsp");
         rd.forward(request, response);
         return;
      }      
   }
   
   public void init() throws ServletException
   {
      // create the connection pool manager
      dbconn.CDbProps props = new dbconn.CDbProps();
      CDbConnMan dbconnman = new CDbConnMan(props.DbConfigFile, props.ErrorLogFile); 
      ServletContext scontext = this.getServletContext();
      scontext.setAttribute("DbConnMan", dbconnman);
      
      CDbConnMan remconnman = new CDbConnMan(props.RemConfigFile, props.ErrorLogFile); 
      scontext.setAttribute("RemConnMan", remconnman);
   }
   
   public void destroy()
   {
      ServletContext scontext = this.getServletContext();
      CDbConnMan dbconnman = (CDbConnMan) scontext.getAttribute("DbConnMan"); 
      dbconnman.shutdown();
      scontext.removeAttribute("DbConnMan");
      
      CDbConnMan actconnman = (CDbConnMan) scontext.getAttribute("ActConnMan"); 
      actconnman.shutdown();
      scontext.removeAttribute("ActConnMan");
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
   // </editor-fold>
}
