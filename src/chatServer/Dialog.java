package chatServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Dialog extends Remote {
    void sendMessage(String message) throws RemoteException;
    List<String> retrieveMessages() throws RemoteException;
}