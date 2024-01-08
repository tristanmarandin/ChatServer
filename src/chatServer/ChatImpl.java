package chatServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ChatImpl extends UnicastRemoteObject implements Chat {
    private List<String> messages;

    protected ChatImpl() throws RemoteException {
        super();
        messages = new ArrayList<>();
    }

    @Override
    public void sendMessage(String message) throws RemoteException {
        System.out.println("Received: " + message);
        messages.add(message);
    }

    @Override
    public List<String> retrieveMessages() throws RemoteException {
        return messages;
    }
}