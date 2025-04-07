package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientFrame extends JFrame implements ActionListener {
    // Listener interface to pass connection details.
    public interface ConnectionListener {
        void onConnectionDetailsProvided(String hostname, int port);
    }

    private JTextField hostnameField;
    private JTextField portField;
    private JButton connectButton;
    private ConnectionListener listener;

    /**
     * Constructs the connection GUI.
     * @param listener a callback to receive the hostname and port when Connect is pressed.
     */
    public ClientFrame(ConnectionListener listener) {
        super("Dictionary Client Connection");
        this.listener = listener;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hostname/Address label and text field.
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
        connectButton = new JButton("Connect");
        connectButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(connectButton, gbc);

        pack();
        setLocationRelativeTo(null); // Center the frame.
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String hostname = hostnameField.getText();
        String portText = portField.getText();
        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid port number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Pass the connection details back via the listener.
        if (listener != null) {
            listener.onConnectionDetailsProvided(hostname, port);
        }
        dispose(); // Close this frame.
    }
}