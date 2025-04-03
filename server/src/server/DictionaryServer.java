package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DictionaryServer {
    public static void main(String[] args) {
        // Create a shared dictionary instance
        Dictionary dictionary = new Dictionary();

        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Dictionary server started on port 1234...");

            while (true) {
                // Accept a new client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from: " + clientSocket.getInetAddress());

                // Create a new thread (ClientHandler) to handle the persistent connection.
                new Thread(new ClientHandler(clientSocket, dictionary)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
