import java.rmi.RemoteException;

public class Main{

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Client <host> <port>");
            System.exit(1);
        }
        try {
            new Client(args[0], Integer.parseInt(args[1]));
        } catch (RemoteException e) {
            System.err.println("Fout bij opstarten client: " + e.getMessage());
            System.exit(1);
        }
    }
}

