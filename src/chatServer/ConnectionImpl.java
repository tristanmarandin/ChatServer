package chatServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ConnectionImpl extends UnicastRemoteObject implements Connection {
    private List<Emitter> emitters; // List of connected clients (for sending messages)
    private List<Receiver> receivers; // List of connected clients (for managing and getting names)
    private DialogImpl dialogService;

    public ConnectionImpl() throws RemoteException {
        super();
        emitters = new ArrayList<>();
        receivers = new ArrayList<>();
        dialogService = new DialogImpl(this);
    }

    @Override
    public Receiver connect(String clientName, Emitter emitter) throws RemoteException {
        Receiver receiver = new ReceiverImpl(clientName, emitter);
        receivers.add(receiver);
        emitters.add(emitter);
        return receiver;
    }

    @Override
    public Dialog createDialog() throws RemoteException {
        return dialogService;
    }

    @Override
    public void forwardMessage(String message) {
        for (Emitter emitter : emitters) {
            try {
                emitter.receiveMessageFromServer (message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void disconnect(String clientName, Emitter emitter) throws RemoteException {
        Receiver toRemove = null;
        for (Receiver receiver : receivers) {
            try {
                if (clientName.equals(receiver.getClientName())) {
                    toRemove = receiver;
                    break;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (toRemove != null) {
            receivers.remove(toRemove);
            emitters.remove(emitter);
            System.out.println("Client " + clientName + " disconnected.");
        }
        else {
            System.out.println("didn't worked");
            for (Receiver receiver : receivers) {
                System.out.println(receiver.getClientName());
            }
        }
    }
}
