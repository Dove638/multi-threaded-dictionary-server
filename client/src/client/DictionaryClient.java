package client;

import javax.swing.SwingUtilities;
import java.net.*;
import java.io.*;
import java.util.concurrent.CountDownLatch;


/**
 * The DictionaryClient class handles the client-side logic
 * of connecting to the dictionary server and launching the GUI for operations.
 * It opens a connection to the server using details provided by the user via a GUI,
 * then passes the communication streams to the operations frame.
 */
public class DictionaryClient {
    private String hostname;
    private int port;
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;


    /**
     * Constructs a new DictionaryClient.
     * The connection details (hostname and port) will be set via the GUI.
     */
    public DictionaryClient() {
    }

    /**
     * Launches the client application.
     * Starts with a connection GUI, then connects to the server,
     * and launches the dictionary operations interface.
     */
    public static void main(String[] args) {
        // Use a CountDownLatch to wait for the GUI to provide connection details.
        final CountDownLatch latch = new CountDownLatch(1);
        final DictionaryClient client = new DictionaryClient();

        // Launch the connection GUI on the Swing thread.
        SwingUtilities.invokeLater(() -> new ClientFrame((host, port) -> {
            client.hostname = host;
            client.port = port;
            latch.countDown(); // Release the latch when user provides input
        }));

        // Wait until the user has provided connection details.
        try {
            latch.await();
        }
        catch (InterruptedException e) {
            System.out.println("An error occurred while waiting for user input.");
            e.printStackTrace();
        }

        // Try to connect using the provided hostname and port.
        if (client.connect()) {
            System.out.println("Connected to dictionary server at " + client.hostname + ":" + client.port);
            // Launch the operations frame on the Swing thread.
            SwingUtilities.invokeLater(() -> new DictionaryOperationsFrame(client));
        }
        else {
            System.out.println("Failed to connect to dictionary server.");
        }
    }


    /**
     * Attempts to open a socket connection using the hostname and port.
     */
    private boolean connect() {
        try {
            socket = new Socket(hostname, port);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            return true;
        }
        catch (IOException e) {
            System.out.println("Failed to connect to dictionary server.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Closes the connection and streams.
     */
    protected void disconnect() {
        try {
            if (dos != null) dos.close();
            if (dis != null) dis.close();
            if (socket != null) socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the output stream for sending requests to the server.
     */
    protected DataOutputStream getOutputStream() {
        return dos;
    }

    /**
     * Returns the input stream for receiving responses from the server.
     */
    protected DataInputStream getInputStream() {
        return dis;
    }
}