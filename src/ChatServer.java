package src;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 8080;
    private static final String HOST = "127.0.0.1";
    private static final Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server...");
        System.out.println("Listening on port " + PORT + "...\n");

       try( ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(HOST))){

           while(true){
               Socket socket = serverSocket.accept();
               ClientHandler handler = new ClientHandler(socket, clients);
               clients.add(handler);
               new Thread(handler).start();
               System.out.println("New client connected! Total: " + clients.size());
           }
       } catch (IOException e) {
           System.err.println("Server error: " + e.getMessage());
           e.printStackTrace();
       }

    }

    public static void broadcast(String message, ClientHandler exclude){
        synchronized(clients){
            for(ClientHandler client : clients){
                if(client != exclude && client.isActive()){
                    client.sendMessage(message);
                }
            }
        }
    }

    public static void removeClient(ClientHandler client){
        clients.remove(client);
        System.out.println(client.username + " left. Active users: " + clients.size());    }
}
