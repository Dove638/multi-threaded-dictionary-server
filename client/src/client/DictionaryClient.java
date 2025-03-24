package client;
import java.net.*;
import java.io.*;

public class DictionaryClient {
    public static void main(String args[]) throws IOException{
        // Usage: java DictionaryClient <message> <hostname> <port number>
        String msg = args[0];
        String hostname = args[1];
        int serverPort = Integer.parseInt(args[2]);


        // Open a socket with user specified host and port number
        Socket s1 = new Socket(hostname, serverPort);

        // Sending a request




        // Get an input file handle from the rocket and read the input
        InputStream s1ln = s1.getInputStream();
        DataInputStream dis  = new DataInputStream(s1ln);
        String st = new String(dis.readUTF());

        // Print to console the input
        System.out.println(st);

        // When done, just close the connection and exit
        dis.close();
        s1ln.close();
        s1.close();
    }
}
