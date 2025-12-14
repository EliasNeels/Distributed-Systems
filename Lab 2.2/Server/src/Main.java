import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    private void startServer() {
        try {
            int port = 1099;

            Registry registry = LocateRegistry.createRegistry(port);

            registry.rebind("ChatServer", new ServerImpl());

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("RMI Chat Server is klaar onder de naam ChatServer");
    }

    public static void main(String[] args) {
        new Main().startServer();
    }
}