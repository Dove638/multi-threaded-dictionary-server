package client;

/**
 * The {@code ConnectionListener} interface defines a callback method
 * used to receive connection details (hostname and port) from the GUI.
 * <p>
 * Implement this interface to handle user input from {@link ClientFrame}.
 */
public interface ConnectionListener {
    /**
     * Called when the user provides connection details and presses Connect.
     *
     * @param hostname The hostname or IP address entered by the user.
     * @param port     The port number entered by the user.
     */
    void onConnectionDetailsProvided(String hostname, int port);

}
