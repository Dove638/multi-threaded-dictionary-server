package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * The ClientFrame class provides a simple GUI for entering
 * hostname and port connection details for the dictionary client.
 * Once the user submits the information via the Connect button,
 * it passes the details to a ConnectionListener.
 */
public class ClientFrame extends JFrame implements ActionListener {
    // Text fields for hostname and port
    private final JTextField hostnameField;
    private final JTextField portField;

    // Button to trigger connection
    private final JButton connectButton;

    // Listener to notify the main client application of user input
    private final ConnectionListener listener;

    /**
     * Constructs the connection GUI.
     */
    public ClientFrame(ConnectionListener listener) {
        super("Dictionary Client Connection");

        this.listener = listener;

        // Basic JFrame setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        // Configure layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hostname label and text field.
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Hostname/Address:"), gbc);
        hostnameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(hostnameField, gbc);

        // Port Number label and text field.
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Port Number:"), gbc);
        portField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(portField, gbc);

        // Connect button setup
        connectButton = new JButton("Connect Now");
        connectButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(connectButton, gbc);

        // Pressing Enter anywhere in the form will click the "Connect Now" button
        getRootPane().setDefaultButton(connectButton);

        pack(); // Resize window to fit components
        setVisible(true); // Show the window
    }


    /**
     * Called when the user clicks the Connect button.
     * Validates the port input and notifies the ConnectionListener
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String hostname = hostnameField.getText();
        String portText = portField.getText();
        int port;
        try {
            port = Integer.parseInt(portText);
        }
        // In the case the port number passed was not an integer display the error message to user
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid port number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Pass the provided connection details back via the listener.
        if (listener != null) {
            listener.onConnectionDetailsProvided(hostname, port);
        }
        dispose(); // Close the connection frame.
    }
}