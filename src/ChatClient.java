package src;

import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 8080;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final BufferedReader consoleInput;

    public ChatClient() {
        consoleInput = new BufferedReader(new InputStreamReader(System.in));
    }

    public void start() {
        try {
            // Prompt for username before connecting
            System.out.println("=== Multi-Client Chat ===");
            System.out.print("Enter your username: ");
            String username = consoleInput.readLine();

            if (username == null || username.trim().isEmpty()) {
                username = "Anonymous";
            }

            // Connect to server
            socket = new Socket(SERVER_IP, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to chat server!");
            System.out.println("Type your messages below (type '/quit' to quit):");
            System.out.println("===============================================\n");

            // Send username to server as first message
            out.println(username);

            // Start thread to receive messages from server
            Thread receiveThread = new Thread(new ReceiveHandler());
            receiveThread.setDaemon(true);
            receiveThread.start();

            // Main thread: Send messages to server
            String message;
            while (true) {
                message = consoleInput.readLine();

                if (message == null || message.trim().equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye!");
                    break;
                }

                if (message.trim().isEmpty()) {
                    continue;
                }

                // Send message to server (server will broadcast it)
                out.println(message);
            }

        } catch (ConnectException e) {
            System.err.println("Cannot connect to server. Is the server running?");
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    // Thread to receive and display messages from server
    private class ReceiveHandler implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    // Protocol: "USERNAME|MESSAGE"
                    String[] parts = message.split("\\|", 2);

                    if (parts.length == 2) {
                        String sender = parts[0];
                        String content = parts[1];

                        // Format message display
                        if (sender.equals("SYSTEM")) {
                            System.out.println("[SYSTEM] " + content);
                        } else {
                            System.out.println("[" + sender + "]: " + content);
                        }
                    } else {
                        System.out.println(message);
                    }
                }

                System.out.println("\nServer closed connection.");

            } catch (IOException e) {
                System.out.println("\nConnection lost.");
            }
        }
    }

    // Clean disconnect
    private void disconnect() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (consoleInput != null) consoleInput.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error during disconnect: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.start();
    }
}