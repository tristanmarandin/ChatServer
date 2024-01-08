package chatServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Receiver extends Remote {
    void receiveMessage(String message) throws RemoteException;

    String getClientName() throws RemoteException;
}