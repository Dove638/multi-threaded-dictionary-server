package client;

/**
 * The ConnectionListener interface defines a callback method
 * used to receive connection details (hostname and port) from the GUI.
 */
public interface ConnectionListener {
    /**
     * Called when the user provides connection details and presses Connect.
     */
    public void onConnectionDetailsProvided(String hostname, int port);

}
