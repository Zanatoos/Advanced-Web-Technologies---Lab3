package httpd;

import java.net.ServerSocket;
import java.util.Arrays;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConfigReader
{

    private String fileName;
    private String port;
    private String host;
    private String webRoot;
    private String mimeTypeFile;
    private String logFile;
    private String index;

	public ConfigReader( String fileName )
    {
        this.fileName = fileName;
        this.host = "";
        this.port = "";
        this.webRoot = "";
        this.mimeTypeFile = "";
        this.logFile = "";
        this.index = "";
    }

    public String[] read() {
        BufferedReader br = null;
		    FileReader fr = null;

        try {

    			fr = new FileReader(this.fileName);
    			br = new BufferedReader(fr);



			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
        if (sCurrentLine.indexOf("=") != -1) {
            String[] arr = sCurrentLine.split("=");

            if (arr[0].equals("port"))
                this.port = arr[1];
            else if (arr[0].equals("webRoot"))
                this.webRoot = arr[1];
            else if (arr[0].equals("host"))
                this.host = arr[1];
            else if (arr[0].equals("mimetypefile"))
                this.mimeTypeFile = arr[1];
            else if (arr[0].equals("logfile"))
                this.logFile = arr[1];
            else if (arr[0].equals("index"))
                this.index = arr[1];
            else
              System.out.println("Config file : Unknown parameter.");
          }
  			}
  		} catch (IOException e) {

  			e.printStackTrace();

  		} finally {
  			try {
  				if (br != null)
  					br.close();
  				if (fr != null)
  					fr.close();
  			} catch (IOException ex) {
  				ex.printStackTrace();
  			}
  		}

      String[] re = {this.port, this.host, this.webRoot, this.mimeTypeFile, this.logFile, this.index};

      return re;
    }
}
