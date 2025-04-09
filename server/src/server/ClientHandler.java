package server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The {@code ClientHandler} class handles communication with a single client
 * connected to the Dictionary server. It supports concurrent processing of
 * client commands through a thread pool.
 *
 * <p>Supported commands include: QUERY, ADD, REMOVE, APPEND, and UPDATE.</p>
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Dictionary dictionary;

    // Thread pool to handle individual requests on this persistent connection
    private ExecutorService requestPool = Executors.newCachedThreadPool();

    // Lock to synchronize writes to the output stream
    private final Object writeLock = new Object();

    /**
     * Constructs a new {@code ClientHandler}.
     *
     * @param clientSocket The client socket connected to the server.
     * @param dictionary   The shared dictionary used for processing requests.
     */
    public ClientHandler(Socket clientSocket, Dictionary dictionary) {
        this.clientSocket = clientSocket;
        this.dictionary = dictionary;
    }

    /**
     * Entry point for the client handler thread. Continuously listens for and
     * processes client requests until the connection is closed or the client sends "EXIT".
     */
    @Override
    public void run() {
        try (
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())
        ) {
            // Continuously read requests from the persistent connection.
            while (true) {
                String request = dis.readUTF();

                // Handle an "EXIT" command to break the loop and end connection.
                if (request.equalsIgnoreCase("EXIT")) {
                    break;
                }

                // Process each request in its own thread from the pool.
                requestPool.execute(() -> {
                    String response = processRequest(request);

                    // Synchronize output to prevent interleaving responses.
                    synchronized (writeLock) {
                        try {
                            dos.writeUTF(response);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        catch (IOException e) {
            System.out.println("Client disconnected: " + clientSocket.getInetAddress());
        }
        finally {
            requestPool.shutdown(); // Clean up the thread pool
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Processes a client request based on a simple text-based protocol.
     * The format is {@code COMMAND:arg1:arg2:...}
     *
     * @param request The raw request string from the client.
     * @return The response string to be sent back to the client.
     */
    private String processRequest(String request) {
        String[] tokens = request.split(":");
        String command = tokens[0].toUpperCase();

        switch (command) {
            case "QUERY":
                if (tokens.length < 2) {
                    return "Error: No word provided for query.";
                }
                String queryWord = tokens[1];
                var meanings = dictionary.query(queryWord);
                return (meanings != null) ? "Meanings: " + meanings.toString() : "Error: Word not found.";

            case "ADD":
                if (tokens.length < 3) {
                    return "Error: Insufficient parameters for ADD.";
                }

                String newWord = tokens[1];
                String meaningsStr = tokens[2];

                // Expecting multiple meanings separated by semicolons.
                String[] meaningsArr = meaningsStr.split(";");
                java.util.Set<String> meaningsSet = new java.util.HashSet<>();
                for (String m : meaningsArr) {
                    if (!m.trim().isEmpty()) {
                        meaningsSet.add(m.trim());
                    }
                }

                try {
                    boolean added = dictionary.addWord(newWord, meaningsSet);
                    if (added){
                        try {
                            dictionary.saveToFile();
                        }
                        catch (IOException e){
                            return "Success: Word added, but error saving file: " + e.getMessage();
                        }
                        return "Success: Word added.";
                    }
                    else{
                        return "Error: Word already exists.";
                    }
                }
                catch (IllegalArgumentException e) {
                    return "Error: " + e.getMessage(); // Will need to expand on this error
                }

            case "REMOVE":
                if (tokens.length < 2) {
                    return "Error: No word provided for REMOVE.";
                }
                String removeWord = tokens[1];
                boolean removed = dictionary.removeWord(removeWord);
                if (removed) {
                    try {
                        dictionary.saveToFile();
                    }
                    catch (IOException e) {
                        return "Success: Word removed, but error saving file: " + e.getMessage();
                    }
                    return "Success: Word removed.";
                }
                else {
                    return "Error: Word not found.";
                }

            case "APPEND":
                if (tokens.length < 3) {
                    return "Error: Insufficient parameters for APPEND.";
                }
                String existWord = tokens[1];
                String newMeaning = tokens[2];
                boolean appended = dictionary.addMeaning(existWord, newMeaning);
                if (appended) {
                    try {
                        dictionary.saveToFile();
                    } catch (IOException e) {
                        return "Success: Meaning added, but error saving file: " + e.getMessage();
                    }
                    return "Success: Meaning added.";
                } else {
                    return "Error: Word not found or meaning already exists.";
                }

            case "UPDATE":
                if (tokens.length < 4) {
                    return "Error: Insufficient parameters for UPDATE.";
                }
                String updateWord = tokens[1];
                String oldMeaning = tokens[2];
                String updatedMeaning = tokens[3];
                boolean updated = dictionary.updateMeaning(updateWord, oldMeaning, updatedMeaning);
                if (updated) {
                    try {
                        dictionary.saveToFile();
                    } catch (IOException e) {
                        return "Success: Meaning updated, but error saving file: " + e.getMessage();
                    }
                    return "Success: Meaning updated.";
                } else {
                    return "Error: Word or old meaning not found.";
                }

            default:
                return "Error: Unknown command.";
        }
    }
}