package chatServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class DialogImpl extends UnicastRemoteObject implements Dialog {
    private List<String> messages;
    private ConnectionImpl connectionService; // Reference to the ConnectionImpl service

    // Modified constructor to accept ConnectionImpl
    protected DialogImpl(ConnectionImpl connectionService) throws RemoteException {
        super();
        this.connectionService = connectionService; // Initialize the ConnectionImpl reference
        messages = new ArrayList<>();
    }

    @Override
    public void sendMessage(String message) throws RemoteException {
        System.out.println(message);
        messages.add(message);

        // Forward the message to all connected clients
        connectionService.forwardMessage(message);
    }

    @Override
    public List<String> retrieveMessages() throws RemoteException {
        return messages;
    }
}
