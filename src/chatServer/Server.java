package chatServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.NotBoundException;

public class Server {
    private static volatile boolean keepRunning = true;
    private static Registry registry;
    private static Connection connectionService; // Declare as a static field

    public static void main(String[] args) {
        try {
            registry = LocateRegistry.createRegistry(1099);
            connectionService = new ConnectionImpl(); // Initialize here
            Naming.rebind("Connection", connectionService);
            System.out.println("Server active");

            while (keepRunning) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }

            shutdownServer(); // Shutdown server when loop exits

        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void shutdownServer() {
        try {
            System.out.println("Server shutting down...");

            Naming.unbind("Connection");
            System.out.println("Service unbound from the registry.");

            UnicastRemoteObject.unexportObject(connectionService, true);
            System.out.println("Connection service unexported.");

            if (registry != null) {
                UnicastRemoteObject.unexportObject(registry, true);
                System.out.println("RMI registry unexported.");
            }

            System.out.println("Server stopped.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

