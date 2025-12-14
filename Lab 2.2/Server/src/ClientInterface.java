import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface ClientInterface extends Remote {

    //De server kan deze aaneroepen om een bericht te pushen.
    void receiveMessage(String message) throws RemoteException;

    //De server kan deze aaneroepen om de gebruikerslijst te updaten.
    void updateUserList(Set<String> userList) throws RemoteException;
}