package httpd;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.io.*;

public class HttpLog {

  private String logFile;

  public HttpLog(String logFile) {
    this.logFile = logFile;
  }

  public void add(String address, String date, String request, String status) {
    PrintWriter pw = null;
    String output;


    try {
      pw = new PrintWriter(new BufferedWriter(new FileWriter(this.logFile, true)));
      output = String.format("[%s] [%s] [%s] [%s]", address, date, request, status);
      pw.println(output);
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      try { pw.close(); } catch(Exception e ) {}
    }
  }

}
