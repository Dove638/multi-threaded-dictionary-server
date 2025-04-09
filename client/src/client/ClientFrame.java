package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientFrame extends JFrame implements ActionListener {
    // Define a listener interface for passing connection details.
    private JTextField hostnameField;
    private JTextField portField;
    private JButton connectButton;
    private ConnectionListener listener;

    /**
     * Constructs the connection GUI.
     * @param listener a callback that will receive the hostname and port when the user clicks Connect.
     */
    public ClientFrame(ConnectionListener listener) {
        super("Dictionary Client Connection");

        this.listener = listener;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
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

        // Connect button.
        connectButton = new JButton("Connect Now");
        connectButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(connectButton, gbc);

        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String hostname = hostnameField.getText();
        String portText = portField.getText();
        int port;
        try {
            port = Integer.parseInt(portText);
        }
        // In the case the port number passed was is not an integer display the error message to user
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