import java.rmi.registry.LocateRegistry; import
        java.rmi.registry.Registry;
public class Main {
    private void startServer() {
        try {
            // create on port 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // create a new service named CounterService
            registry.rebind("CounterService", new CounterImpl());

            System.out.println("RMI Server is running...");

            // 💡 NEW LINE: Block the thread to prevent immediate exit
            // This is just one way. You could also use a Scanner and wait for user input.
            // Using Thread.sleep(Long.MAX_VALUE) is a simple, albeit crude, way to block.
            Thread.sleep(Long.MAX_VALUE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Main main = new Main();
        main.startServer();
    }
}
