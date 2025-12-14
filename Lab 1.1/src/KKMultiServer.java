import java.io.IOException;
import java.net.ServerSocket;

public class KKMultiServer {
    public static void main(String[] args) throws IOException {

        int portNumber = Integer.parseInt(args[0]);

        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
        ) {
            System.out.println("Knock! Knock! Multi-server listening on port " + portNumber);

            while (true) {
                new KKMultiServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
