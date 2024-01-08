package chatServer;

import javax.swing.*;
import java.awt.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;


public class ChatApplication implements MessageListener {
    private JFrame mainFrame;
    private JTextArea[] clientMessageAreas = new JTextArea[3];

    // Constructor
    public ChatApplication() {
        setupMainFrame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatApplication());
    }

    private void setupMainFrame() {
        mainFrame = new JFrame("VideoGame Room Chat");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout()); // Use BorderLayout for the main frame layout

        // Server panel with toggle button
        JPanel serverPanel = new JPanel();
        serverPanel.setBackground(Color.BLACK);
        JToggleButton serverToggle = new JToggleButton("Start Server");
        serverToggle.setForeground(Color.DARK_GRAY);
        serverToggle.setBackground(Color.BLACK);
        serverToggle.addActionListener(e -> {
            if (serverToggle.isSelected()) {
                serverToggle.setText("Stop Server");
                startServer();
            } else {
                serverToggle.setText("Start Server");
                stopServer();
            }
        });
        serverPanel.add(serverToggle);

        // Add server panel to the frame at the top (North)
        mainFrame.add(serverPanel, BorderLayout.NORTH);

        // Client panels
        JPanel clientPanel = new JPanel();
        clientPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Use FlowLayout to arrange clients side by side
        clientPanel.setBackground(Color.BLACK);

        for (int i = 0; i < 3; i++) {
            final int clientIndex = i; // Final variable for use in lambda
            JPanel singleClientPanel = new JPanel(); // Declare singleClientPanel within the loop
            singleClientPanel.setLayout(new BoxLayout(singleClientPanel, BoxLayout.Y_AXIS)); // Vertical BoxLayout for each client's components
            singleClientPanel.setBackground(Color.DARK_GRAY);

            JButton clientButton = new JButton("Launch & Connect Client " + (i + 1));
            clientButton.addActionListener(e -> {
                connectClient(clientIndex); // Pass the index of the client
                clientButton.setText("Client Connected"); // Change the button text
                clientButton.setEnabled(false); // Disable the button
            });
            singleClientPanel.setBackground(Color.DARK_GRAY);
            clientButton.setBackground(Color.BLACK);

            JTextArea messageArea = new JTextArea(10, 20);
            messageArea.setEditable(false);
            messageArea.setBackground(Color.BLACK);
            messageArea.setForeground(Color.GREEN);
            clientMessageAreas[i] = messageArea;
            JScrollPane scrollPane = new JScrollPane(messageArea); // Wrap the JTextArea in a JScrollPane
            JTextField messageInput = new JTextField("Enter your message here...", 18);
            messageInput.setForeground(Color.WHITE);
            messageInput.setBackground(Color.BLACK);
            messageInput.setCaretColor(Color.GREEN);

            messageInput.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    if (messageInput.getText().equals("Enter your message here...")) {
                        messageInput.setText("");
                        messageInput.setForeground(Color.GREEN);
                    }
                }
                public void focusLost(FocusEvent e) {
                    if (messageInput.getText().isEmpty()) {
                        messageInput.setForeground(Color.WHITE);
                        messageInput.setText("Enter your message here...");
                    }
                }
            });
            messageInput.addActionListener(e -> {
                String message = messageInput.getText();
                if (!message.isEmpty()) {
                    try {
                        clients[clientIndex].sendMessageToServer(message); // Assuming Client has a method to send messages
                    } catch (RemoteException ex) {
                        ex.printStackTrace(); // Handle the exception appropriately
                    }
                    messageInput.setText(""); // Clear the input after sending
                }
            });


            // Add components to the singleClientPanel
            singleClientPanel.add(clientButton);
            singleClientPanel.add(scrollPane);
            singleClientPanel.add(messageInput);

            // Add singleClientPanel to the clientPanel
            clientPanel.add(singleClientPanel);
        }

        // Add client panel to the frame in the center
        mainFrame.add(clientPanel, BorderLayout.CENTER);

        mainFrame.pack(); // Pack the components
        mainFrame.setMinimumSize(new Dimension(800, 600)); // Ensure the frame has a reasonable minimum size
        mainFrame.setVisible(true); // Display the frame
    }


    private void startServer() {
        new Thread(() -> {
            String[] args = {}; // Any arguments you would pass to the main method
            chatServer.Server.main(args);
        }).start();
    }   

    private void stopServer() {
        chatServer.Server.shutdownServer();
        closeApplication();
    }

    // Array to store client instances
    private Client[] clients = new Client[3];

    @Override
    public void onMessageReceived(int clientIndex, String message) {
        SwingUtilities.invokeLater(() -> {
            JTextArea messageArea = clientMessageAreas[clientIndex]; // Assuming you have a reference to each JTextArea
            messageArea.append(message + "\n");
        });
    }

    private void connectClient(int clientIndex) {
        try {
            // Create client instance
            clients[clientIndex] = new Client(this, clientIndex);

            // Set client name - This can be modified based on your application needs
            String clientName = "Client" + (clientIndex + 1);
            clients[clientIndex].setClientName(clientName); // Assuming there is a method in Client to set the name

            // Lookup the Connection service
            Connection connectionService = (Connection) Naming.lookup("rmi://localhost:1099/Connection");

            // Assign the connectionService to the client instance
            clients[clientIndex].setConnectionService(connectionService);

            // Connect the client to the server
            clients[clientIndex].connect(); // Assuming there is a method in Client to connect to the server
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    public void closeApplication() {
        if (mainFrame != null) {
            mainFrame.dispose();
        }
    }

}
