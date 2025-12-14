import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface ServerInterface extends Remote {

    //De client geeft zijn eigen remote object (de callback) mee.
    void registerClient(ClientInterface client, String username) throws RemoteException;

    //De client stuurt een bericht naar de server.
    void sendMessage(String username, String message) throws RemoteException;

    //De client verbreekt de verbinding met de server.
    void unregisterClient(String username) throws RemoteException;
}