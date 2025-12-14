import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerImpl extends UnicastRemoteObject implements ServerInterface {

    private final Map<String, ClientInterface> clients = new ConcurrentHashMap<>();

    public ServerImpl() throws RemoteException {
        super();
    }

    @Override
    public synchronized void registerClient(ClientInterface client, String username) throws RemoteException {
        if (clients.containsKey(username.toLowerCase())) {
            throw new RemoteException("ERROR: Name already in use");
        }

        clients.put(username, client);
        System.out.println("[SERVER] " + username + " joined.");

        broadcast("SERVER: " + username + " is de chat gejoind.", null);
        sendUserListToAll();
    }

    @Override
    public synchronized void sendMessage(String username, String message) throws RemoteException {
        String fullMessage = username + ": " + message;
        System.out.println("[BROADCAST] " + fullMessage);

        broadcast(fullMessage, username);
    }

    @Override
    public synchronized void unregisterClient(String username) throws RemoteException {
        clients.remove(username);
        System.out.println("[SERVER] " + username + " disconnected.");

        broadcast("SERVER: " + username + " heeft de chat verlaten.", null);
        sendUserListToAll();
    }

    //Interne helper methode voor callbacks
    private void broadcast(String message, String excludeUsername) {
        for (Map.Entry<String, ClientInterface> entry : clients.entrySet()) {
            if (entry.getKey().equals(excludeUsername)) continue;

            try {
                entry.getValue().receiveMessage(message); // CALLBACK
            } catch (RemoteException e) {
                System.err.println("Fout bij sturen naar " + entry.getKey() + ": " + e.getMessage());
                clients.remove(entry.getKey());
            }
        }
    }

    private void sendUserListToAll() {
        Set<String> userNames = clients.keySet();
        for (ClientInterface client : clients.values()) {
            try {
                client.updateUserList(userNames); //CALLBACK
            } catch (RemoteException e) {
                System.err.println("Fout bij updaten gebruikerslijst: " + e.getMessage());
            }
        }
    }
}