
/**
 * class to handle ENCTYPE="multipart/form-data" requests
 * to support file uploads.
 *
 * Adapted from:
 *   Java Servlet Programming by Jason Hunter
 *   Example 4-18 Pages 112-121.
 */
package upload;

import java.io.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletInputStream;


public class MultipartRequest
{
   private static final int DefMaxPostSize = 4 * 1024 * 1024;  // max packet size for DB is set to 4MB. MediumBlob is 2^24 bytes
   private HttpServletRequest fRequest;  // the request
   private File fSaveDir;            // output directory
   private int fMaxSize;             // uploaded file size limit
   private Hashtable fParameters = new Hashtable();  // name - value
   private Hashtable fFiles = new Hashtable();       // name - uploaded file

   public MultipartRequest(HttpServletRequest aRequest, String aSaveDir) throws IOException
   {
      this(aRequest, aSaveDir, DefMaxPostSize);
   }

   public MultipartRequest(HttpServletRequest aRequest, String aSaveDir, int aMaxSize) throws IOException
   {
      // require parameters
      if (aRequest == null) throw new IOException("null request");
      if (aSaveDir == null) throw new IOException("null save directory");
      if (aMaxSize <= 0) throw new IOException("invalid MaxSize");

      // save parameter values
      fRequest = aRequest;
      fSaveDir = new File(aSaveDir);
      fMaxSize = aMaxSize;

      // require valid writable save directory
      if (!fSaveDir.isDirectory()) throw new IOException("not a directory: " + fSaveDir);
      if (!fSaveDir.canWrite()) throw new IOException("directory not writable: " + fSaveDir);

      // parse request into parameters and files
      readRequest();
   }

   public Enumeration getParameterNames()
   {
      return fParameters.keys();
   }

   public Enumeration getFileNames()
   {
      return fFiles.keys();
   }

   public String getParameter(String aName)
   {
      try
      {
         String param = (String) fParameters.get(aName);
         if (param.equals("")) return null;
         return param;
      }
      catch (Exception e)
      {
         return null;
      }
   }

   public String getFilesystemName(String aName)
   {
      try
      {
         UploadedFile file = (UploadedFile) fFiles.get(aName);
         return file.getFilesystemName();
      }
      catch (Exception e)
      {
         return null;
      }
   }

   public String getContentType(String aName)
   {
      try
      {
         UploadedFile file = (UploadedFile) fFiles.get(aName);
         return file.getContentType();
      }
      catch (Exception e)
      {
         return null;
      }
   }

   public File getFile(String aName)
   {
      try
      {
         UploadedFile file = (UploadedFile) fFiles.get(aName);
         return file.getFile();
      }
      catch (Exception e)
      {
         return null;
      }
   }

   protected void readRequest() throws IOException
   {
      // content type must be "multpart/form-data"
      String type = null;
      String type1 = fRequest.getContentType();           // normal
      String type2 = fRequest.getHeader("Content-Type");  // WebSphere workaround
      // If one value is null, choose the other value
      if (type1 == null && type2 != null) type = type2;
      else if (type2 == null && type1 != null) type = type1;
      else if (type1 != null && type2 != null)       // choose the longer value
         type = (type1.length() > type2.length() ? type1 : type2);

      if (type == null) throw new IOException("Content type is null");
      else if (!type.toLowerCase().startsWith("multipart/form-data")) throw new IOException("Content not multipart/form-data: " + type);

      // check content length
      int length = fRequest.getContentLength();
      if (length > fMaxSize) throw new IOException("Content length " + length + " exceeds limit " + fMaxSize);

      // get boundary string
      String boundary = extractBoundary(type);
      if (boundary == null) throw new IOException("Separation boundary not found");

      // construct special input stream
      MultipartStreamHandler in = new MultipartStreamHandler(fRequest.getInputStream(), boundary, length);

      // read first line -- should be first boundary
      String line = in.readLine();
      if (line == null) throw new IOException("Corrupt form data:  premature end");

      // verify that the line is the boundary
      if (!line.startsWith(boundary)) throw new IOException("Corrupt form data:  missing leading boundary");

      // loop over each part
      boolean done = false;
      while (!done)
      {
         done = readNextPart(in, boundary);
      }
   }

   protected boolean readNextPart(MultipartStreamHandler aStream, String aBoundary) throws IOException
   {
      // try to read the first line
      String line = aStream.readLine();
      if (line == null) return true;
      else if (line.length() == 0) return true;

      // parse first line -- looks something like this:
      // content-disposition: form data;  name="field"; filename="file1.txt"
      String[] dispInfo = extractDispositionInfo(line);
//    String disposition = dispInfo[0];
      String name = dispInfo[1];
      String filename = dispInfo[2];

      // next line -- either empty or Content-Type followed by empty line
      line = aStream.readLine();
      if (line == null) return true;  // nothing left, we are done

      // get content type
      String contentType = extractContentType(line);
      if (contentType != null)
      {
         line = aStream.readLine();  // eat the empty line
         if (line == null || line.length() > 0) throw new IOException("Malformed line after content type: " + line);
      }
      else  // not specified so assume a default content type
         contentType = "text/plain";  // rfc1867 says this is the default

      // read the content
      if (filename == null)   // it is a parameter
      {
         String value = readParameter(aStream, aBoundary);
         fParameters.put(name, value);
      }
      else    // it is a file
      {
         readAndSaveFile(aStream, aBoundary, filename);
         if (filename.equals("unknown"))
            fFiles.put(name, new UploadedFile(null, null, null));
         else
            fFiles.put(name, new UploadedFile(fSaveDir.toString(), filename, contentType));
      }
      return false;  // we are not done -- there may be more
  }

   protected String readParameter(MultipartStreamHandler aStream, String aBoundary) throws IOException
   {
      StringBuilder sbuf = new StringBuilder(32768);
      String line;

      while ((line = aStream.readLine()) != null)
      {
         if (line.startsWith(aBoundary)) break;
         sbuf.append(line + "\r\n");
      }

      if (sbuf.length() == 0) return null;

      sbuf.setLength(sbuf.length() - 2);
      return sbuf.toString();
   }

   protected void readAndSaveFile(MultipartStreamHandler aStream, String aBoundary, String aFilename) throws IOException
   {
      File f = new File(fSaveDir + File.separator + aFilename);
      FileOutputStream fout = new FileOutputStream(f);
      BufferedOutputStream bout = new BufferedOutputStream(fout, 8196);
      byte[] bbuf = new byte[8196];
      int result;
      String line;

      // ServletInputStream adds /r/n to the last line -- we need to clip those
      boolean rnflag = false;
      while ((result = aStream.readLine(bbuf, 0, bbuf.length)) != -1)
      {
         if (result > 2 && bbuf[0] == '-' && bbuf[1] == '-')  // look for boundary
         {
            line = new String(bbuf, 0, result, "ISO-8859-1");
            if (line.startsWith(aBoundary)) break;
         }

         if (rnflag)   // pending deferred "\r\n" for all but last line
         {
            bout.write('\r');
            bout.write('\n');
            rnflag = false;
         }

         if (result >= 2 && bbuf[result-2] == '\r' && bbuf[result-1] == '\n')
         {
            bout.write(bbuf, 0, result-2);   // clip "\r\n"
            rnflag = true;  // indicate deferred write is pending
         }
         else
         {
            bout.write(bbuf, 0, result);
         }
      }

      bout.flush();
      bout.close();
      fout.close();
   }

   private String extractBoundary(String aLine)
   {
      int index = aLine.lastIndexOf("boundary=");
      if (index == -1) return null;
      String boundary = aLine.substring(index + 9); // 9 is length of "boundary="
      // the real boundary is preceded by an extra "--"
      boundary = "--" + boundary;
      return boundary;
   }

/**
 * parse disposition information string that looks something like
 * content-disposition: form-data;  name="field"; filename="file1.txt"
 */

   private String[] extractDispositionInfo(String aLine) throws IOException
   {
      String[] retval = new String[3];
      String myline = aLine.toLowerCase();

      // get content disposition -- should be "form-data"
      int start = myline.indexOf("content-disposition: ");
      int end = myline.indexOf(";");
      if (start == -1 || end == -1) throw new IOException("Content disposition info corrupt: " + aLine);
      String disposition = myline.substring(start+21, end);
      if (!disposition.equals("form-data")) throw new IOException("Invalid content disposition: " + disposition);

      // get field name
      start = myline.indexOf("name=\"", end);  // start where previous ended
      end = myline.indexOf("\"", start + 7);
      if (start == -1 || end == -1) throw new IOException("Content disposition info corrupt: " + aLine);
      String name = aLine.substring(start+6, end);

      // get file name if provided
      String filename = null;
      start = myline.indexOf("filename=\"", end+2);
      end = myline.indexOf("\"", start+10);
      if (start != -1 && end != -1)
      {
         filename = aLine.substring(start+10, end);
         // clip path if present
         int slash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
         if (slash > -1) filename = filename.substring(slash+1);
         if (filename.equals("")) filename = "unknown";
      }

      // return a string array
      retval[0] = disposition;
      retval[1] = name;
      retval[2] = filename;
      return retval;
   }

   private String extractContentType(String aLine) throws IOException
   {
      String contentType = null;
      String myline = aLine.toLowerCase();

      // get content type, if provided
      if (myline.startsWith("content-type"))
      {
         int start = myline.indexOf(" ");
         if (start == -1) throw new IOException("Corrupt content type: " + aLine);
         contentType = myline.substring(start+1);
      }
      else if (myline.length() != 0) throw new IOException("Malformed line after disposition: " + aLine);

      return contentType;
   }
}

class UploadedFile
{
   private String fFileDir;
   private String fFileName;
   private String fContType;

   UploadedFile(String aFileDir, String aFileName, String aContType)
   {
      this.fFileDir = aFileDir;
      this.fFileName = aFileName;
      this.fContType = aContType;
   }

   public String getContentType()
   {
      return fContType;
   }

   public String getFilesystemName()
   {
      return fFileName;
   }

   public File getFile()
   {
      if (fFileDir == null || fFileName == null)
         return null;
      else
         return new File(fFileDir + File.separator + fFileName);
   }
}

/**
 * class to handle reading multipart/form-data from an input stream.
 * tracks bytes read and compares to limit.
 */

class MultipartStreamHandler
{
   ServletInputStream fInStream;
   String fBoundary;
   int fNumExpected;
   int fNumRead = 0;
   byte[] fBuff = new byte[8186];

   public MultipartStreamHandler(ServletInputStream aStream, String aBoundary, int aExpected)
   {
      this.fInStream = aStream;
      this.fBoundary = aBoundary;
      this.fNumExpected = aExpected;
   }

   public String readLine() throws IOException
   {
      StringBuilder sbuf = new StringBuilder(32768);
      int result;

      do
      {
         result = this.readLine(fBuff, 0, fBuff.length);
         if (result != -1) sbuf.append(new String(fBuff,0,result, "ISO-8859-1"));
      } while (result == fBuff.length);  // loop if buffer was filled

      if (sbuf.length() == 0) return null;

      sbuf.setLength(sbuf.length() - 2);
      return sbuf.toString();
   }

   public int readLine(byte aBuff[], int aOff, int aLen) throws IOException
   {
      if (fNumRead >= fNumExpected)
      {
         return -1;
      }
      else
      {
         int result = fInStream.readLine(aBuff, aOff, aLen);
         if (result > 0) fNumRead += result;
         return result;
      }
   }
}