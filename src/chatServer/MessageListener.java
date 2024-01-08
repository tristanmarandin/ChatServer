package chatServer;

public interface MessageListener {
    void onMessageReceived(int clientIndex, String message);
}