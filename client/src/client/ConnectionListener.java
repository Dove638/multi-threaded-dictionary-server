package client;

public interface ConnectionListener {
    void onConnectionDetailsProvided(String hostname, int port);

}
