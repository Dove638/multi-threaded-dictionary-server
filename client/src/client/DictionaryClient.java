package client;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class DictionaryClient {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Prompt user for server connection details
        System.out.print("Enter the server hostname: ");
        String hostname = scanner.nextLine();

        System.out.print("Enter the server port number: ");
        int serverPort = Integer.parseInt(scanner.nextLine());

        // Open a socket connection with the specified host and port
        Socket socket = new Socket(hostname, serverPort);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        System.out.println("Connected to dictionary server at " + hostname + ":" + serverPort);

        boolean exit = false;
        while (!exit) {
            // Display menu options to the user
            System.out.println("\nChoose an operation:");
            System.out.println("1. Query the meaning(s) of a word");
            System.out.println("2. Add a new word");
            System.out.println("3. Remove an existing word");
            System.out.println("4. Add an additional meaning to an existing word");
            System.out.println("5. Update an existing meaning of a word");
            System.out.println("6. Exit");

            System.out.print("Enter your choice (1-6): ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 6.");
                continue;
            }

            String request = "";
            switch (choice) {
                case 1:
                    // Query operation
                    System.out.print("Enter the word to query: ");
                    String queryWord = scanner.nextLine();
                    request = "QUERY:" + queryWord;
                    break;
                case 2:
                    // Add a new word
                    System.out.print("Enter the word to add: ");
                    String newWord = scanner.nextLine();
                    System.out.print("Enter the meaning(s) (for multiple meanings, separate with semicolons ';'): ");
                    String meanings = scanner.nextLine();
                    if(meanings.trim().isEmpty()){
                        System.out.println("Error: Meaning(s) cannot be empty.");
                        continue;
                    }
                    request = "ADD:" + newWord + ":" + meanings;
                    break;
                case 3:
                    // Remove an existing word
                    System.out.print("Enter the word to remove: ");
                    String removeWord = scanner.nextLine();
                    request = "REMOVE:" + removeWord;
                    break;
                case 4:
                    // Add an additional meaning to an existing word
                    System.out.print("Enter the existing word: ");
                    String existWord = scanner.nextLine();
                    System.out.print("Enter the new meaning to add: ");
                    String newMeaning = scanner.nextLine();
                    request = "APPEND:" + existWord + ":" + newMeaning;
                    break;
                case 5:
                    // Update an existing meaning of a word
                    System.out.print("Enter the word: ");
                    String word = scanner.nextLine();
                    System.out.print("Enter the existing meaning: ");
                    String oldMeaning = scanner.nextLine();
                    System.out.print("Enter the new meaning: ");
                    String updatedMeaning = scanner.nextLine();
                    request = "UPDATE:" + word + ":" + oldMeaning + ":" + updatedMeaning;
                    break;
                case 6:
                    // Exit the application
                    exit = true;
                    continue;  // Skip sending any request
                default:
                    System.out.println("Invalid choice. Please select a number between 1 and 6.");
                    continue;
            }

            // Send the request to the server
            dos.writeUTF(request);

            // Wait for and display the response from the server
            String response = dis.readUTF();
            System.out.println("Server response: " + response);
        }

        // Close streams and the socket
        dos.close();
        dis.close();
        socket.close();
        scanner.close();
        System.out.println("Connection closed. Goodbye!");
    }
}