package server;
import java.net.*;
import java.io.*;

public class DictionaryServer {
    public static void main(String args[]) throws IOException{
        // Register service on port 1234
        ServerSocket s = new ServerSocket(1234);
        while (true){
            Socket s1 = s.accept(); // wait and accept a connection

            // Get a communcation stream associated with the socket
            OutputStream s1out = s1.getOutputStream();
            DataOutputStream dos = new DataOutputStream(s1out);

            // Send over info
            dos.writeUTF("Hi Dave and Anton");

            // Close the connection but not the server socket
            dos.close();
            s1out.close();
            s1.close();
        }
    }
}
