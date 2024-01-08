package chatServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Connection extends Remote {
    Receiver connect(String clientName, Emitter emitter) throws RemoteException;
    Dialog createDialog() throws RemoteException;
    void forwardMessage(String message) throws RemoteException;
    void disconnect(String clientName, Emitter emitter) throws RemoteException;
}

