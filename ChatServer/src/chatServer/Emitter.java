package chatServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Emitter extends Remote {
    void sendMessageToServer (String message) throws RemoteException;
    void receiveMessageFromServer (String message) throws RemoteException;
}