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
public class DoStatus extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            session.setAttribute("CurrAct", CAppConsts.LinkLoginPage);
            RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
            rd.forward(request, response);
            return;
        }
        CUserItem myuser = (CUserItem) session.getAttribute("UserItem");

        manapp.CAppProps props = (manapp.CAppProps) session.getAttribute("AppProps");
        if (props == null) {
            props = new manapp.CAppProps();
            session.setAttribute("AppProps", props);
        }

        ServletContext scontext = this.getServletContext();
        CDbConnMan dbconnman = (CDbConnMan) scontext.getAttribute("DbConnMan");
        CDbConnMan remconnman = (CDbConnMan) scontext.getAttribute("RemConnMan");

        String btntxt = request.getParameter("BtnAct");

        if (btntxt != null && btntxt.equals("LogOff")) {
            session.removeAttribute("UserItem");
            session.removeAttribute("CurTestGroup");
            session.removeAttribute("CurFC1");
            session.removeAttribute("CurFC2");
            session.removeAttribute("ViewResults");

            session.invalidate();
            RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
            rd.forward(request, response);

            return;
        }

        String mytestgrp = request.getParameter("TestGroup");
        String fc1 = request.getParameter("FC1");
        String fc2 = request.getParameter("FC2");

        if (mytestgrp == null) {
            mytestgrp = CAppConsts.TagNoValue;
        }
        if (fc1 == null) {
            fc1 = CAppConsts.DefaultForecaster;
        }
        if (fc2 == null) {
            fc2 = CAppConsts.TagNoValue;
        }
        session.setAttribute("CurTestGroup", mytestgrp);
        session.setAttribute("CurFC1", fc1);
        session.setAttribute("CurFC2", fc2);
        session.setAttribute("ViewResults", fc1);  //default is to view fc1 results

        if (btntxt != null && (btntxt.equals("ChangeTestGroup") || btntxt.equals("ChangeForecaster"))) {
            session.setAttribute("CurrAct", "StatusPage");
            RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
            rd.forward(request, response);
            return;
        }

        if (btntxt != null && btntxt.equals("Status")) {
            session.setAttribute("CurrAct", "StatusPage");
            RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
            rd.forward(request, response);
            return;
        }

        CTestList testlist = new CTestList();
        Connection conn = dbconnman.getConnection();
        testlist.dbReadList(conn, mytestgrp, fc1, fc2);
        dbconnman.returnConnection(conn);

        if (btntxt != null && btntxt.equals("Create")) {
            if (mytestgrp.equals(CAppConsts.TagNoValue)) {
                session.setAttribute("CurrAct", "StatusPage");
                RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
                rd.forward(request, response);
                return;
            } else {
                session.removeAttribute("TestCase");
                CTestItem testitem = new CTestItem();
                testitem.testgroupid = mytestgrp;
                testitem.testid = testlist.makeNewTestId(mytestgrp, "test", 8);

                testitem.createby = myuser.getUserId();
                session.setAttribute("TestCase", testitem);
                session.setAttribute("CurrAct", "EditPage");
                RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
                rd.forward(request, response);
                return;
            }
        }

        if (btntxt != null && btntxt.startsWith("Edit")) {
            String mytestid = btntxt.substring(4);
            String mykey = mytestgrp + "|" + mytestid;
            CTestItem testitem = (CTestItem) testlist.getObject(mykey);
            session.setAttribute("TestCase", testitem);
            session.setAttribute("CurrAct", "EditPage");
            RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
            rd.forward(request, response);
            return;
        }

        if (btntxt != null && (btntxt.startsWith("FC1Detail") || btntxt.startsWith("FC2Detail"))) {
            String mytestid = btntxt.substring(9);
            String mykey = mytestgrp + "|" + mytestid;
            CTestItem testitem = (CTestItem) testlist.getObject(mykey);
            session.setAttribute("TestCase", testitem);
            session.setAttribute("CurrAct", "DisplayPage");
            String fcaster = btntxt.substring(0, 3);
            if (fcaster.equals("FC2")) {
                //view results of fcaster2
                session.setAttribute("ViewResults", fc2);

            } else {
                //view results of fcaster1
                session.setAttribute("ViewResults", fc1);
            }
            RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
            rd.forward(request, response);
            return;
        }

        if (btntxt != null && (btntxt.startsWith("FC1Execute") || btntxt.startsWith("FC2Execute"))) {
            String mytestid = btntxt.substring(10);
            String mykey = mytestgrp + "|" + mytestid;
            int runopt;
            conn = dbconnman.getConnection();
            dbconnman.returnConnection(conn);
//         new CForecasters(conn);
            CTestItem testitem = (CTestItem) testlist.getObject(mykey);
            //testitem.testresult = CAppConsts.StatusSent;
            String fcaster = btntxt.substring(0, 3);
            if (fcaster.equals("FC2")) {
                testitem.fc2result = CAppConsts.StatusSent;
                testitem.dbWriteResult2(conn);
                runopt = CAppConsts.RunFC2;
            } else {
                testitem.fc1result = CAppConsts.StatusSent;
                testitem.dbWriteResult1(conn); // instead of write item 
                runopt = CAppConsts.RunFC1;
            }


            Thread runtest = new CRunTest(props, dbconnman, remconnman, mytestgrp, mytestid, fc1, fc2, runopt);
            runtest.setPriority(Thread.MIN_PRIORITY);
            runtest.start();

            session.setAttribute("CurrAct", "StatusPage");
            RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
            rd.forward(request, response);
            return;
        }

        if (btntxt != null && btntxt.equals("Runall")) {
            int runopt = CAppConsts.RunFC1;
            for (int idx = 0; idx < testlist.getCount(); idx++) {
                CTestItem testitem = (CTestItem) testlist.getItem(idx);
                //testitem.testresult = CAppConsts.StatusSent;

                conn = dbconnman.getConnection();

                if (fc2.equals(CAppConsts.TagNoValue)) {
                    testitem.fc1result = CAppConsts.StatusSent;
                    testitem.dbWriteResult1(conn); // instead of write item 
                    runopt = CAppConsts.RunFC1;
                } else {
                    testitem.fc1result = CAppConsts.StatusSent;
                    testitem.dbWriteResult1(conn);
                    testitem.fc2result = CAppConsts.StatusSent;
                    testitem.dbWriteResult2(conn);
                    runopt = CAppConsts.RunBothFC;
                }

                dbconnman.returnConnection(conn);
            }

            Thread runtest = new CRunTest(props, dbconnman, remconnman, mytestgrp, CAppConsts.TagNoValue, fc1, fc2, runopt);
            runtest.setPriority(Thread.MIN_PRIORITY);
            runtest.start();

            session.setAttribute("CurrAct", "StatusPage");
            RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
            rd.forward(request, response);
            return;
        }

        if (btntxt != null && btntxt.startsWith("Summary")) {
            session.setAttribute("CurrAct", "SummaryPage");
            RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
            rd.forward(request, response);
            return;
        }

        if (btntxt != null && btntxt.startsWith("Import")) {
            session.setAttribute("CurrAct", "ImportPage");
            RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
            rd.forward(request, response);
            return;
        }

        if (btntxt != null && btntxt.startsWith("Export")) {
            conn = dbconnman.getConnection();
            String xstr = testlist.exportList(conn);
            dbconnman.returnConnection(conn);

            response.setContentType("application/x-download");
            response.setHeader("content-disposition", "attachment; filename=TestCaseList.xml");
            response.setContentLength((int) xstr.length());
            OutputStream outp = response.getOutputStream();
            byte[] bytes = xstr.getBytes();
            outp.write(bytes, 0, bytes.length);
            outp.close();
            return;
        }





        // fall through -- return from whence you came
        session.setAttribute("CurrAct", "StatusPage");
        RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
        rd.forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
