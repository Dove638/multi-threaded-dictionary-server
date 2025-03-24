package client;
import java.net.*;
import java.io.*;

public class DictionaryClient {
    public static void main(String args[]) throws IOException{
        // Register service on specified port
        Socket s1 = new Socket("localhost", 1234);

        // Get an input file handle from the ocket and read the input
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
