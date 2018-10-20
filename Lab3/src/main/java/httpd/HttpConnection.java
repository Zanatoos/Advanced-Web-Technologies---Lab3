package httpd;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/*
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.io.PrintWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;*/

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.util.regex.*;
import java.io.*;

public class HttpConnection implements Runnable {

  private Socket client;
  private String webRoot;
  private String mimeTypeFile;
  private String page;
  private String status;
  private String request;
  private String bodyResponse;
  private HttpLog log;
  private String index;
  private boolean isDirectory;

  public HttpConnection(Socket client, String webRoot, String mimeTypeFile, HttpLog log, String index) {
    this.client = client;
    this.webRoot = webRoot;
    this.mimeTypeFile = mimeTypeFile;
    this.page  = null;
    this.status = "HTTP/1.0 200 OK";
    this.request = "";
    this.bodyResponse = null;
    this.log = log;
    this.index = index;
    this.isDirectory = false;
  }

  private void checkPath() {
    if(!new File(this.webRoot+this.page).exists()) {
      this.status = "HTTP/1.0 404 Not found";
      this.bodyResponse = "<HEAD> <TITLE> File not found </TITLE> </ HEAD><BODY> <H1> File not found </H1>The requested resource is not present on this server.<P></BODY>";
    } else {
      this.isDirectory = new File(this.webRoot+this.page).isDirectory();
    }
  }

  private String getHtmlTree(String path) {
    File folder;
    File[] lists;
    String output = null;

    try {
      output = "<html><header><title>Directory Tree</title></header><body><h1>Directory Tree</h1><p><table><tr></tr><td>Name</td><td>Path</td><td>Size</td><td>Last modified</td>";
      folder = new File(this.webRoot+path);
      lists = folder.listFiles();

      for(File file: lists) {
        output += String.format("<tr><td><a href='%s'>%s</a></td><td>%s</td><td>%s</td><td>%s</td></tr>", path+file.getName(), file.getName(), file.getPath(), file.length(), new Date(file.lastModified()));
      }


    } catch(Exception e) {
      e.printStackTrace();
    }

    return String.format("%s</table></p></body></html>", output);
  }

  private void checkRecievedRequest() {
    InputStream is = null;
    BufferedReader br = null;
    String line;
    String output = "";
    Pattern p;
    Matcher m;
    String[] headerRequest;

    try {
      headerRequest = new String[11];
      is = this.client.getInputStream();
      br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

      // Output recieved request from the navigator
      while( (line = br.readLine()) != null) {
        output += line+"\n";
        // Prevent line from reaching the value -1 which will make us unable to send a response
        if(line.isEmpty())
          break;
      }

      p = Pattern.compile("(GET\\s(.+)\\sHTTP\\/1\\.1)\nHost:\\s(.+)\\nConnection:\\s(.+)\\n(Purpose:\\s(.+)\\n)?(Cache-Control:\\s(.+)\\n)?Upgrade-Insecure-Requests:\\s(.+)\\nUser-Agent:\\s(.+)\\nAccept:\\s(.+)\\n(Referer:\\s(.+)\\n)?Accept-Encoding:\\s(.+)\\nAccept-Language:\\s(.+)\\nCookie:\\s(.+)");
      m = p.matcher(output);

      System.out.print(output);
      if(m.find()) {
        for(int i =1;i<11;i++) {
          //System.out.println("MATCH N°"+i+": "+m.group(i));
          headerRequest[i] = m.group(i);
        }

        this.request = headerRequest[1];

        if(headerRequest[2].equals("/"))
          this.page = String.format("/%s", this.index);
        else if(headerRequest[2].substring(headerRequest[2].length() - 1).equals("/")) {
          this.page = headerRequest[2];
          this.bodyResponse = getHtmlTree(headerRequest[2]);
        } else
          this.page = headerRequest[2];
      } else {
        this.status = "HTTP/1.0 400 Bad Request";
        this.bodyResponse = "<HEAD><TITLE>Bad Request</TITLE></HEAD><BODY><H1>Bad Request</H1>Votre navigateur Internet a envoyé une requête que ce serveur ne peut pas traiter.<P></BODY>";
      }

      //System.out.println(headerRequest[1]+" === > "+this.page);
      this.checkPath();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  private String getMimeFileType() {
    BufferedReader br = null;
    Pattern p;
    Matcher m;
    String line;
    String[] arr;
    String mimeType = null;

    try  {
      p = Pattern.compile("^.+(\\Q.\\E\\w+)$");
      m = p.matcher(this.page);
      if(m.find()) {
        br = new BufferedReader(new FileReader(new File(this.mimeTypeFile)));
        //System.out.println("------------------------------ :"+m.group(1));
        while ((line = br.readLine()) != null) {
            arr = line.split("=");
            if(arr[0].equals(m.group(1))) {
              mimeType = arr[1];
              break;
            }
        }

        if(mimeType == null)
          mimeType = "text/plain";
      }
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      try { br.close(); } catch(Exception e) {}
    }

    return mimeType;
  }

  private void addRequestToLog(String date) {
    String address = this.client.getRemoteSocketAddress().toString();
    this.log.add(address, date, this.request, this.status);
  }

  public void run() {
    this.checkRecievedRequest();

    PrintWriter pw = null;
    SimpleDateFormat formater;
    Date today;
    BufferedInputStream bis;
    int i;
    char c;

    try {
      //System.out.println(this.webRoot+this.page+"\n"+this.status+"\n"+this.bodyResponse);
      today = new Date();
      formater = new SimpleDateFormat("'Date:' EEEE, d MMM yyyy hh:mm:ss z");
      pw = new PrintWriter(this.client.getOutputStream(), true);

      pw.println(this.status);
      pw.println(formater.format(today));
      pw.println("Server: JavaHttp/1.0");
      pw.println("Content-type: "+this.getMimeFileType());
      pw.println("");

      if(this.status.indexOf("OK") != -1 && !this.isDirectory) {
        bis = new BufferedInputStream(new FileInputStream(this.webRoot+this.page));
        while((i = bis.read()) != -1) {
          c = (char) i;
          pw.print(c);
          //System.out.print(c);
        }
      } else {
        pw.print(this.bodyResponse);
      }

      addRequestToLog(formater.format(today));
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      try {
        pw.close();
        this.client.close();
        System.out.println("Closing socket -------------------------");
      } catch(Exception e) {}
    }
  }
}
