import java.io.*;
import java.net.Socket;
import java.util.Set;

public class ClientThread implements Runnable {
    private final Socket socket;
    private final Server server;
    private PrintWriter out;
    private String username;

    public ClientThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 1. Ask for username
            out.println("SUBMIT NAME");
            username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                out.println("ERROR: Empty name");
                return;
            }

            synchronized (server) {
                if (server.isNameTaken(username)) {
                    out.println("ERROR: Name already in use");
                    return;
                }
                server.addUser(new User(username, this));
            }

            System.out.println("[SERVER] " + username + " joined from " + socket.getRemoteSocketAddress());


            out.println("NAME ACCEPTED " + username);
            server.sendUserListToAll();


            server.broadcast("SERVER: " + username + " has joined the chat.", this);


            String input;
            while ((input = in.readLine()) != null) {
                if (input.toLowerCase().startsWith("/quit")) {
                    break;
                }
                server.broadcast(username + ": " + input, this);
            }

        } catch (IOException e) {
            System.out.println("[SERVER] Client error: " + e.getMessage());
        } finally {
            if (username != null) {
                server.removeUser(username);
                server.broadcast("SERVER: " + username + " has left the chat.", null);
                sendUserListToAll();
                System.out.println("[SERVER] " + username + " disconnected.");
            }
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void sendUserList() {
        StringBuilder list = new StringBuilder("USERLIST ");
        for (User u : server.getUsers()) {
            list.append(u.getUsername()).append(",");
        }
        if (list.length() > 9) list.setLength(list.length() - 1);
        out.println(list.toString());
    }

    private void sendUserListToAll() {
        server.sendUserListToAll();
    }

    public String getUsername() { return username; }
}