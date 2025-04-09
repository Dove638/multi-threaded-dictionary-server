package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * The {@code DictionaryServer} class is the entry point for the dictionary server application.
 * It starts the server, loads the dictionary from the provided file, and listens for client connections.
 * Each client connection is handled in a separate thread.
 *
 * <p>Usage: {@code java DictionaryServer <port> <dictionaryFilePath>}
 */
public class DictionaryServer {

    /**
     * The main method that launches the dictionary server.
     *
     * @param args Command-line arguments: port number and path to the dictionary file.
     * @throws IllegalArgumentException if incorrect arguments are provided.
     */
    public static void main(String[] args) {
        // Validate that two arguments are provided: port and dictionary file path
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException("Usage: DictionaryServer <port> <dictionaryFilePath>");
        }
        // Parse command-line arguments
        int serverPort = Integer.parseInt(args[0]);
        String dictionaryFilePath = args[1];

        // Start the server with the provided port and dictionary
        StartServer(serverPort, dictionaryFilePath);
    }

    /**
     * Starts the dictionary server on the specified port and loads the dictionary.
     *
     * @param port               The port number the server listens on.
     * @param dictionaryFilePath The file path of the dictionary to load.
     */
    private static void StartServer(int port, String dictionaryFilePath) {
        // Create the loader instance
        Dictionary dictionary = null;

        try {
            // Attempt to load the dictionary from the file
            dictionary = new Dictionary(dictionaryFilePath);
            dictionary.loadInitialDictionary();
        }
        catch (Exception e) {
            // Handle any errors during dictionary loading
            System.err.println("Error loading dictionary: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        // Start the server socket and continuously listen for client connections
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Dictionary server started on port: " + port);

            // Accept and handle each client connection in a new thread
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from: " + clientSocket.getInetAddress());

                // Handle client request using a new thread to allow concurrent clients
                new Thread(new ClientHandler(clientSocket, dictionary)).start();
            }
        }
        catch (IOException e) {
            // Handle server socket errors
            e.printStackTrace();
        }
    }
}
