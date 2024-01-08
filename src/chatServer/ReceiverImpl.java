package chatServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ReceiverImpl extends UnicastRemoteObject implements Receiver {
    private String clientName;

    public ReceiverImpl(String clientName, Emitter emitter) throws RemoteException {
        super();
        this.clientName = clientName;
    }

    @Override
    public void receiveMessage(String message) throws RemoteException {
        // Handle received message
        System.out.println("Received message: " + message);
    }

    @Override
    public String getClientName() throws RemoteException {
        return this.clientName;
    }
}