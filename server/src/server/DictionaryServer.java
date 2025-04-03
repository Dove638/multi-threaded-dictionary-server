package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentMap;
import java.util.Set;

public class DictionaryServer {
    public static void main(String[] args) {
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException("Usage: DictionaryServer <port> <dictionaryFilePath>");
        }
        int serverPort = Integer.parseInt(args[0]);
        String dictionaryFilePath = args[1];

        StartServer(serverPort, dictionaryFilePath);
    }

    public static void StartServer(int port, String dictionaryFilePath) {
        // Create the loader instance
        DictionaryLoader loader = new CSVDictionaryLoader();
        Dictionary dictionary = null;

        try {
            // Load the dictionary using the loader
            ConcurrentMap<String, Set<String>> loadedData = loader.loadInitialDictionary(dictionaryFilePath);
            dictionary = new Dictionary(loadedData);
        } catch (Exception e) {
            System.err.println("Error loading dictionary: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // Start the server socket and listen for client connections
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Dictionary server started on port: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from: " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket, dictionary)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
