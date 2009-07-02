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
import manapp.CConsts;

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
      manapp.CAppProps props = new manapp.CAppProps(CConsts.AppPropFile); 

      String user = request.getParameter("UserId");
      user = user.substring(0, Math.min(user.length(), CConsts.MaxUserLeng));
      user = user.toLowerCase();
      
      String failreason = "";
      SimpleDateFormat edf = new SimpleDateFormat("dd MMMM yyyy");
      
      try
      {
         String passwd = request.getParameter("PassWd"); 
         passwd = passwd.substring(0, Math.min(passwd.length(), CConsts.MaxPassLeng));
         String passhash = crypto.CMd5Hash.toHash(passwd);

         java.util.Date pwdate = new java.util.Date(0L);

         manapp.CDbConnect dbconn = new manapp.CDbConnect(props.AuthDbConfig, props.ErrorLogFile, props.ErrMsgEcho);
         Connection conn = dbconn.getConnection();
         if (conn == null) 
         {
            failreason = "Database is not available.";
            logUsage(props, user, CConsts.LoginFailure, failreason);
            session.setAttribute("FailReason", failreason);
            response.reset();
            session.setAttribute("CurrAct", CConsts.LinkLoginFailure);
            RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
            rd.forward(request, response);
            return;
         }
         
         CUserItem myuser = new CUserItem();
         myuser.setUserId(user);
         
         String qstr = "Select PassHash,Testcase,PwChangeTm,LastFailure,LastSuccess,NumFailures,NumSuccess" +
                       " From UserTbl Where UserId=?";
         PreparedStatement pstmt = conn.prepareStatement(qstr);
         pstmt.setString(1, user);
         ResultSet rset = pstmt.executeQuery();
         
         if (rset.next())
         {
            myuser.setPassHash(rset.getString(1));
            
            myuser.setRole(rset.getString(2));
            
            java.sql.Timestamp tstamp = rset.getTimestamp(3);
            if (rset.wasNull()) tstamp = new java.sql.Timestamp(0L);
            pwdate = new java.util.Date(tstamp.getTime());
            myuser.setPwDate(pwdate);
            
            tstamp = rset.getTimestamp(4);
            if (rset.wasNull()) tstamp = new java.sql.Timestamp(0L);
            myuser.setLastFailure(new java.util.Date(tstamp.getTime()));

            tstamp = rset.getTimestamp(5);
            if (rset.wasNull()) tstamp = new java.sql.Timestamp(0L);
            myuser.setLastSuccess(new java.util.Date(tstamp.getTime()));
            
            int num = rset.getInt(6);
            if (rset.wasNull()) num = 0;
            myuser.setNumFailures(num);
            
            num = rset.getInt(7);
            if (rset.wasNull()) num = 0;
            myuser.setNumSuccess(num);

            rset.close();
            pstmt.close();
         }
         else
         {
            rset.close();
            pstmt.close();
            dbconn.shutDown();
            
            failreason = "Invalid userid.";
            logUsage(props, user, CConsts.LoginFailure, failreason);
            failreason = "Invalid userid/password combination.";
            session.setAttribute("FailReason", failreason);
            response.reset();
            session.setAttribute("CurrAct", CConsts.LinkLoginFailure);
            RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
            rd.forward(request, response);
            return;
         }

         int faillock = myuser.getFailLocked(conn);
         if (faillock == CConsts.FailLockPerm)
         {
            dbconn.shutDown();
            failreason = "Account is locked.";
            session.setAttribute("FailReason", failreason);
            response.reset();
            session.setAttribute("CurrAct", CConsts.LinkLoginFailure);
            RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
            rd.forward(request, response);
            return;
         }
         else if (faillock == CConsts.FailLockTemp)
         {
            dbconn.shutDown();
            failreason = "Account is temporarily locked.";
            session.setAttribute("FailReason", failreason);
            response.reset();
            session.setAttribute("CurrAct", CConsts.LinkLoginFailure);
            RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
            rd.forward(request, response);
            return;
         }
         
         if (!passhash.equals(myuser.getPassHash()))
         {
            myuser.dbFailure(conn);
            dbconn.shutDown();
            failreason = "Invalid userid/password combination.";
            session.setAttribute("FailReason", failreason);
            response.reset();
            session.setAttribute("CurrAct", CConsts.LinkLoginFailure);
            RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
            rd.forward(request, response);
            return;
         }

         if (CConsts.RoleNone.equals(myuser.getRole()))
         {
            dbconn.shutDown();
            failreason = "Inactive userid.";
            logUsage(props, user, CConsts.LoginFailure, failreason);
            session.setAttribute("FailReason", failreason);
            response.reset();
            session.setAttribute("CurrAct", CConsts.LinkLoginFailure);
            RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
            rd.forward(request, response);
            return;
         }

         if ((!CConsts.RoleAdmin.equals(myuser.getRole())) && (!CConsts.RoleUser.equals(myuser.getRole())))
         {
            dbconn.shutDown();
            failreason = "User not authorized.";
            logUsage(props, user, CConsts.LoginFailure, failreason);
            session.setAttribute("FailReason", failreason);
            response.reset();
            session.setAttribute("CurrAct", CConsts.LinkLoginFailure);
            RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
            rd.forward(request, response);
            return;
         }

         // at this point, we have a valid user.
         myuser.dbSuccess(conn);
         session.setAttribute("UserItem", myuser);
         dbconn.shutDown();
         
         // check if password has expired
         Date today = new Date();
         pwdate.setTime(pwdate.getTime() + CConsts.MilsecDay * CConsts.PwLifeDays);
         if (today.getTime() > pwdate.getTime())
         {
            failreason = "Password has expired.";
            logUsage(props, user, CConsts.LoginFailure, failreason);
            session.setAttribute("CurrAct", CConsts.LinkPassChange);
            session.setAttribute("PwChange", CConsts.PwChangeRequire);
            response.reset();
            RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
            rd.forward(request, response);
            return;
         }
         
         String pwexpstr = "Your password will expire on " + edf.format(pwdate);
         session.setAttribute("PwExpire", pwexpstr);
         
         logUsage(props, user, CConsts.LoginSuccess, failreason);
         session.setAttribute("CurrAct", CConsts.LinkLoginSuccess);
         response.reset();
         RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }
      catch (Exception ex)
      {
         failreason = "Login exception occurred.";
         logUsage(props, user, CConsts.LoginFailure, failreason + ex.getMessage());
         session.setAttribute("FailReason", failreason);
         response.reset();
         session.setAttribute("CurrAct", CConsts.LinkLoginFailure);
         RequestDispatcher rd = request.getRequestDispatcher(CConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }
   }
   
   protected synchronized void logUsage(manapp.CAppProps aprops, String auser, String astatus, String areason)
   {
      try
      {
         SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
         Date dt = new Date();
         String datestr = df.format(dt);
         
         FileOutputStream logfos = new FileOutputStream(aprops.UsageLogFile, true);
         PrintWriter logout = new PrintWriter(logfos);
         logout.println(datestr + "|" + auser + "|" + CConsts.WebAppTitle + "|" + astatus + "|" + areason);
         logout.close();
      }
      catch (Exception ex)
      {
         manapp.CLogError.logError(CConsts.ErrMsgFile, false, "DoLogin.logUsage: ", ex);
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
