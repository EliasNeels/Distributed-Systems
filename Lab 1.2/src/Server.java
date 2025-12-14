// ChatServer.java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private final int port;
    private final Set<User> users = new HashSet<>();
    private final Set<ClientThread> threads = new HashSet<>();

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("[SERVER] Starting chat server on port " + port + "...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[SERVER] Listening for clients...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientThread thread = new ClientThread(clientSocket, this);
                threads.add(thread);
                new Thread(thread).start();
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Could not start server: " + e.getMessage());
        }
    }

    public synchronized void addUser(User user) {
        users.add(user);
        threads.add(user.getThread());
    }

    public synchronized void removeUser(String username) {
        users.removeIf(u -> u.getUsername().equals(username));
        threads.removeIf(t -> t.getUsername() != null && t.getUsername().equals(username));
    }

    public synchronized boolean isNameTaken(String name) {
        return users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(name));
    }

    public synchronized Set<User> getUsers() {
        return new HashSet<>(users);
    }

    public void broadcast(String message, ClientThread exclude) {
        String sender = exclude != null ? exclude.getUsername() : "SERVER";
        String prefixed = "MESSAGE " + sender + ": " + message;
        System.out.println("[BROADCAST] " + message);
        for (ClientThread t : threads) {
            if (t != exclude) {
                t.sendMessage(prefixed);
            }
        }
    }

    public void sendUserListToAll() {
        for (ClientThread t : threads) {
            t.sendUserList();
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java ChatServer <port>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        new Server(port).start();
    }
}