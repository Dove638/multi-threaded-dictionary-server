package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * The DictionaryOperationsFrame provides a GUI for interacting with the dictionary server.
 * Users can perform operations such as querying, adding, removing, appending, and updating words.
 */
public class DictionaryOperationsFrame extends JFrame implements ActionListener {
    private final DictionaryClient client;
    private final JButton queryButton;
    private final JButton addButton;
    private final JButton removeButton;
    private final JButton appendButton;
    private final JButton updateButton;
    private final JButton exitButton;

    /**
     * Constructs the operations GUI with all dictionary commands.
     */
    public DictionaryOperationsFrame(DictionaryClient client) {
        super("Dictionary Operations");
        this.client = client;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Query button.
        queryButton = new JButton("Query Word");
        queryButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(queryButton, gbc);

        // Add button.
        addButton = new JButton("Add Word");
        addButton.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(addButton, gbc);

        // Remove button.
        removeButton = new JButton("Remove Word");
        removeButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(removeButton, gbc);

        // Append button.
        appendButton = new JButton("Append Meaning");
        appendButton.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(appendButton, gbc);

        // Update button.
        updateButton = new JButton("Update Meaning");
        updateButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(updateButton, gbc);

        // Exit button.
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(exitButton, gbc);

        pack();
        setLocationRelativeTo(null); // Center the frame.
        setVisible(true);
    }


    /**
     * Handles button actions for dictionary operations.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == exitButton) {
            client.disconnect();
            System.exit(0);
        }
        else if (source == queryButton) {
            String word = JOptionPane.showInputDialog(this, "Enter the word to query:");
            if (word != null && !word.trim().isEmpty()) {
                String request = "QUERY:" + word;
                sendRequest(request, "Query Response");
            }
        }
        else if (source == addButton) {
            JTextField wordField = new JTextField();
            JTextField meaningsField = new JTextField();
            Object[] message = {
                    "Word:", wordField,
                    "Meanings (separated by semicolons):", meaningsField
            };
            int option = JOptionPane.showConfirmDialog(this, message, "Add Word", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String word = wordField.getText();
                String meanings = meaningsField.getText();
                if (word.trim().isEmpty() || meanings.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Both word and meanings must be provided.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String request = "ADD:" + word + ":" + meanings;
                sendRequest(request, "Add Word Response");
            }
        }
        else if (source == removeButton) {
            String word = JOptionPane.showInputDialog(this, "Enter the word to remove:");
            if (word != null && !word.trim().isEmpty()) {
                String request = "REMOVE:" + word;
                sendRequest(request, "Remove Word Response");
            }
        }
        else if (source == appendButton) {
            JTextField wordField = new JTextField();
            JTextField newMeaningField = new JTextField();
            Object[] message = {
                    "Existing Word:", wordField,
                    "New Meaning:", newMeaningField
            };
            int option = JOptionPane.showConfirmDialog(this, message, "Append Meaning", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String word = wordField.getText();
                String newMeaning = newMeaningField.getText();
                if (word.trim().isEmpty() || newMeaning.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Both fields must be provided.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String request = "APPEND:" + word + ":" + newMeaning;
                sendRequest(request, "Append Meaning Response");
            }
        }
        else if (source == updateButton) {
            JTextField wordField = new JTextField();
            JTextField oldMeaningField = new JTextField();
            JTextField newMeaningField = new JTextField();
            Object[] message = {
                    "Word:", wordField,
                    "Existing Meaning:", oldMeaningField,
                    "New Meaning:", newMeaningField
            };
            int option = JOptionPane.showConfirmDialog(this, message, "Update Meaning", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String word = wordField.getText();
                String oldMeaning = oldMeaningField.getText();
                String newMeaning = newMeaningField.getText();
                if (word.trim().isEmpty() || oldMeaning.trim().isEmpty() || newMeaning.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields must be provided.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String request = "UPDATE:" + word + ":" + oldMeaning + ":" + newMeaning;
                sendRequest(request, "Update Meaning Response");
            }
        }
    }

    /**
     * Sends a command request to the dictionary server and shows the response.
     */
    private void sendRequest(String request, String title) {
        try {
            client.getOutputStream().writeUTF(request);
            String response = client.getInputStream().readUTF();
            JOptionPane.showMessageDialog(this, response, title, JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error communicating with server: " + ex.getMessage(), "Communication Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}