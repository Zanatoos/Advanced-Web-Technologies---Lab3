package httpd;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.io.PrintWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HttpServer
{
    private int port = 8080;
    private String hostIp = "127.0.0.1";
    private String webRoot;
    private ServerSocket server = null;
    private boolean isRunning = true;
    private String page = "index.html";

	public HttpServer()
    {
		try {
            server = new ServerSocket(this.port, 100, InetAddress.getByName(this.hostIp));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public HttpServer(int port, String hostIp, String webRoot)
    {
        this.port = port;
        this.hostIp = hostIp;
        this.webRoot = webRoot;
	 }

    public void exec() {
       try {
            server = new ServerSocket(this.port, 100, InetAddress.getByName(this.hostIp));
        } catch(Exception e) {
            e.printStackTrace();
            throw new Error("DEAD");
        }

        while(isRunning == true) {
            Socket client = null;

            try {
                client = server.accept();

                //System.out.println("Connexion client reçue.");

                //checkRequest(client);
                writeResponse(client);

            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                try { client.close(); } catch(Exception e) {}
            }
        }
    }

    private void writeResponse(Socket client) {
      String indexPath;
      PrintWriter pw = null;
      SimpleDateFormat formater = null;
      Date today = new Date();
      String status = "HTTP/1.0 200 OK";
      BufferedInputStream bis = null;
      int i;
      char c;

      try {
        indexPath = this.webRoot+this.page;
        formater = new SimpleDateFormat("'Date:' EEEE, d MMM yyyy hh:mm:ss z");
        pw = new PrintWriter(client.getOutputStream(), true);
        bis = new BufferedInputStream(new FileInputStream(indexPath));
        // Send response


        pw.println(status);
        pw.println(formater.format(today));
        pw.println("Server: JavaHttp/1.0");
        pw.println("Content-type: text/html");
        pw.print("\r\n");
        while((i = bis.read()) != -1) {
          c = (char) i;
          pw.print(c);
        }

      } catch(Exception e) {
        e.printStackTrace();
      } finally {
        try { pw.close(); } catch(Exception e) {}
      }
    }

    private void checkRequest(Socket client) {
      try {

        InputStream is = client.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line;

        // Output recieved request from the navigator
        while( (line = br.readLine()) != null) {
          System.out.println(line);
        }

      } catch(Exception e) {
        e.printStackTrace();
      }
    }

    public void close() {
        this.isRunning = false;
        this.server = null;
    }
}
