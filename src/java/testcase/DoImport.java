/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testcase;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;

import manapp.*;
import upload.*;
import dbconn.*;

/**
 *
 * @author lwaisanen
 */
public class DoImport extends HttpServlet
{
   private static final int MaxUpSize = 1024 * 1024;  // an arbitrary limit ro upload size
   private static final String XmlSchemaFile = "TestCaseList.xsd";

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

      manapp.CAppProps props = (manapp.CAppProps) session.getAttribute("AppProps");
      if (props == null)
      {
         props = new manapp.CAppProps();
         session.setAttribute("AppProps", props);
      }

      MultipartRequest mreq = new MultipartRequest(request, props.ImportDir, DoImport.MaxUpSize);

      String btntxt = mreq.getParameter("BtnAct");

      if (btntxt != null && btntxt.equals("Cancel"))
      {
         session.setAttribute("CurrAct", "StatusPage");
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }

      if (btntxt != null && btntxt.startsWith("Import"))
      {
         try
         {
            String mygroup = mreq.getParameter("TestGroup");
            String fc1 = mreq.getParameter("FC1");
            String fc2 = mreq.getParameter("FC2");

            if (mygroup == null) mygroup = CAppConsts.TagNoValue;
            Enumeration files = mreq.getFileNames();
            if (files.hasMoreElements())
            {
               String fname = (String) files.nextElement();
               File finp = mreq.getFile(fname);

               if (finp != null)
               {
                  // compile the schema
                  String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
                  SchemaFactory sfactory = SchemaFactory.newInstance(language);
                  StreamSource ss = new StreamSource(new File(props.ConfDir + DoImport.XmlSchemaFile));
                  Schema schema = sfactory.newSchema(ss);

                  // set up the parser
                  SAXParserFactory pfactory = SAXParserFactory.newInstance();
                  pfactory.setSchema(schema);
                  SAXParser saxParser = pfactory.newSAXParser();
                  CParseXml handler = new CParseXml();
                  // parse the file
                  handler.setTestGroup(mygroup);
                  saxParser.parse(finp, handler);
                  CTestList newlist = handler.getTestList();
                  // get existing cases
                  ServletContext scontext = this.getServletContext();
                  CDbConnMan dbconnman = (CDbConnMan) scontext.getAttribute("DbConnMan");
                  Connection conn = dbconnman.getConnection();
                  CTestList testlist = new CTestList();
                  testlist.dbReadList(conn, mygroup,fc1,fc2);

                  for (int idx = 0; idx < newlist.getCount(); idx++)
                  {
                     CTestItem newitem = (CTestItem) newlist.getItem(idx);
                     String mykey = testlist.makeNewId(mygroup, "test", 8);
//System.err.println(newitem.testid + " --> " + mykey + " * " + CParser.getRemnant(mykey, "|"));
                     newitem.testid = CParser.getRemnant(mykey, "|");
                     newitem.dbWriteItem(conn);
                     newitem.dbWriteDetail(conn);
                     CTestItem myitem = new CTestItem();
                     myitem.copyItem(newitem);
//System.err.println(" add " + myitem.makeKey());
                     testlist.addItem(myitem.makeKey(), myitem);
                  }
                  dbconnman.returnConnection(conn);
               }
            }
         }
         catch (Exception ex)
         {
            CLogError.logError(props.ErrorLogFile, props.ErrMsgEcho, "DoImport exception. ", ex);
         }

         session.setAttribute("CurrAct", "StatusPage");
         RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
         rd.forward(request, response);
         return;
      }

      // fall through -- return from whence you came
      session.setAttribute("CurrAct", "ImportPage");
      RequestDispatcher rd = request.getRequestDispatcher(CAppConsts.LinkCentral);
      rd.forward(request, response);

   }

   // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
   /**
    * Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    * @throws ServletException if a servlet-specific error occurs
    * @throws IOException if an I/O error occurs
    */
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException
   {
      processRequest(request, response);
   }

   /**
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    * @throws ServletException if a servlet-specific error occurs
    * @throws IOException if an I/O error occurs
    */
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException
   {
      processRequest(request, response);
   }
}
