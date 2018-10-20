package httpd;

import java.net.ServerSocket;
import java.util.Arrays;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main
{


	public static void main( String[] args )
    {
        String fileName = args[0];
        ConfigReader fr = new ConfigReader(fileName);

        String[] configParam = fr.read();


        try {
            HttpServer server = new HttpServer(Integer.parseInt(configParam[0]), configParam[1], configParam[2], configParam[3], configParam[4], configParam[5]);
            System.out.println("Open Server on the port : "+configParam[0]);
            server.exec();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
