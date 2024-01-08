package chatServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.server.UnicastRemoteObject;
import java.net.MalformedURLException;


public class Client extends UnicastRemoteObject implements Receiver, Emitter {
    private static final long serialVersionUID = 1L; // Recommended for Serializable classes

    private MessageListener messageListener;
    private int clientIndex;
    private String clientName;
    
    transient private Dialog dialogService; // Dialog component, transient as it shouldn't be serialized
    transient private Connection connectionService;

    // Constructor
    public Client(MessageListener messageListener, int clientIndex) throws RemoteException {
        super();
        this.messageListener = messageListener;
        this.clientIndex = clientIndex;
    }
    
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter your name: ");
            String clientName = reader.readLine(); // <-- Get client's name

            // Look up the Connection service
            Connection connectionService = (Connection) Naming.lookup("rmi://localhost:1099/Connection");

            // Create a client instance
            Client clientInstance = new Client(null, 0);
            clientInstance.clientName = clientName; 
            
            // Assign the connectionService to the client instance
            clientInstance.connectionService = connectionService;

            // Connect the client to the server
            clientInstance.dialogService = clientInstance.connectionService.createDialog();
            Receiver receiver = clientInstance.connectionService.connect(clientName, clientInstance);

            while (true) {
                // Read a message from the console
                //System.out.print("Enter message: ");
                String message = reader.readLine();

                if ("exit".equalsIgnoreCase(message)) {
                    clientInstance.disconnect(); // Call disconnect method
                    break; // Exit the loop to end the client application
                }

                // Forward the message to the server
                clientInstance.sendMessageToServer(message);
            }
        } catch (NotBoundException | IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void receiveMessage(String message) throws RemoteException {
        System.out.println("Received message: " + message);
        if (messageListener != null) {
            messageListener.onMessageReceived(clientIndex, message);
        }
    }

    @Override
    public void receiveMessageFromServer(String message) throws RemoteException {
        // Handle received message
        System.out.println(message);
        if (messageListener != null) {
            messageListener.onMessageReceived(clientIndex, message);
        }
    }

    @Override
    public void sendMessageToServer(String message) throws RemoteException {
        if(dialogService != null) {
            dialogService.sendMessage(clientName + ": " + message);
        } else {
            System.err.println("Error: dialogService is null!");
        }
    }

    @Override
    public String getClientName() throws RemoteException {
        return this.clientName;
    }

    // Method to set client name
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    // Method to set connection service
    public void setConnectionService(Connection connectionService) {
        this.connectionService = connectionService;
    }

    // Method to connect to the server
    public void connect() throws RemoteException, NotBoundException, MalformedURLException {
        dialogService = connectionService.createDialog();
        Receiver receiver = connectionService.connect(clientName, this);
    }

    private void disconnect() throws RemoteException {
        connectionService.disconnect(clientName, this); // Notify the server about the disconnection
        System.out.println("Disconnected from the server.");
    }
}