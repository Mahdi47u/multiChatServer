package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

public class ClientHandler  implements Runnable {

    private final Socket socket;
    private final Set<ClientHandler> clients;
    private PrintWriter out;
    public String username;
    private BufferedReader in;
    private boolean active = true;

    public ClientHandler(Socket socket, Set<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }


    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("Welcome ! Enter your username: ");
            username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                username = "Guest" + socket.getPort();
            }
            username = username.trim();

            String joinMsg = "Server > " + username + " has joined the server";
            ChatServer.broadcast(joinMsg, this);
            out.println("you are connected as: " + username);
            out.println("Type your message and press Enter. Type /quit to leave.");

            String message;
            while ((message = in.readLine()) != null) {
                if (message.trim().equalsIgnoreCase("/quit")) {
                    break;
                }
                String formatted = username + " > " + message;
                ChatServer.broadcast(formatted, this);
            }
        } catch (IOException e){

        } finally {
            disconnect();
        }
    }
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public boolean isActive() {
        return active;
    }

    private void disconnect() {
        active = false;
        try {
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {

        }
        String leaveMsg = "Server > " + username + " left the chat.";
        ChatServer.broadcast(leaveMsg, this);
        ChatServer.removeClient(this);
    }
}
