package chatServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Chat extends Remote {
    void sendMessage(String message) throws RemoteException;
    List<String> retrieveMessages() throws RemoteException;
}