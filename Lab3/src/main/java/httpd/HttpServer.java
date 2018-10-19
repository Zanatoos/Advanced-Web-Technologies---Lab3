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
    private String mimeTypeFile;
    private ServerSocket server = null;
    private boolean isRunning = true;
    private HttpLog log;

    public HttpServer(int port, String hostIp, String webRoot, String mimeTypeFile, String logFile)
    {
        this.port = port;
        this.hostIp = hostIp;
        this.webRoot = webRoot;
        this.mimeTypeFile = mimeTypeFile;
        this.log  = new HttpLog(logFile);
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

                System.out.println("Opening socket...");

                Thread t = new Thread(new HttpConnection(client, this.webRoot, this.mimeTypeFile, log));
                t.start();

            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        try {
          server.close();
          isRunning = false;
        } catch(Exception e) {}
    }

}
