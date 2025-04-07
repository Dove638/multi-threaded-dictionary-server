package client;

import javax.swing.SwingUtilities;
import java.net.*;
import java.io.*;
import java.util.concurrent.CountDownLatch;

public class DictionaryClient {
    String hostname;
    int port;
    Socket socket;
    DataOutputStream dos;
    DataInputStream dis;

    public DictionaryClient() {
        // Hostname and port will be set via the GUI.
    }

    public static void main(String[] args) {
        // Use a CountDownLatch to wait for the GUI to provide connection details.
        final CountDownLatch latch = new CountDownLatch(1);
        final DictionaryClient client = new DictionaryClient();

        // Launch the connection GUI on the Swing thread.
        SwingUtilities.invokeLater(() -> {
            new ClientFrame((host, port) -> {
                client.hostname = host;
                client.port = port;
                latch.countDown();
            });
        });

        // Wait until the user has provided connection details.
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Try to connect using the provided hostname and port.
        if (client.connect()) {
            System.out.println("Connected to dictionary server at " + client.hostname + ":" + client.port);
            // Launch the operations frame on the Swing thread.
            SwingUtilities.invokeLater(() -> new DictionaryOperationsFrame(client));
        } else {
            System.out.println("Failed to connect to dictionary server.");
        }
    }

    /**
     * Attempts to open a socket connection using the hostname and port.
     * @return true if connection is successful; false otherwise.
     */
    public boolean connect() {
        try {
            socket = new Socket(hostname, port);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Closes the connection and streams.
     */
    public void disconnect() {
        try {
            if (dos != null) dos.close();
            if (dis != null) dis.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters for the streams to allow the operations frame to send/receive data.
    public DataOutputStream getOutputStream() {
        return dos;
    }

    public DataInputStream getInputStream() {
        return dis;
    }
}