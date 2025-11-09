# Multi-Client Chat Server in Java

This is a **real-time chat application** written in pure Java.  
It allows multiple users to connect to one server and talk to each other at the same time.

## What It Does
- Supports many clients connecting simultaneously  
- Each user chooses a username when joining  
- All messages are sent to every connected user (broadcast)  
- Shows clear notifications when someone joins or leaves  
- Handles disconnects safely — no errors or frozen program  
- Uses a simple format: `Username > message`

## Features
- One thread per client for smooth performance  
- Thread-safe message broadcasting  
- Clean and easy-to-read code  
- No external libraries — only standard Java  

## How to Run
```bash
javac *.java
java ChatServer          # Start the server
java ChatClient          # Start a client (open many terminals)
```

## Files
- `ChatServer.java`     → Accepts connections and manages users  
- `ClientHandler.java`  → Handles one client (reads/writes messages)  
- `ChatClient.java`     → Lets you type and see messages live  

## What I Learned
- TCP sockets and networking  
- Multithreading in Java  
- Synchronization for safe shared data  
- Proper resource cleanup (closing sockets)  
- Building a working client-server system  

