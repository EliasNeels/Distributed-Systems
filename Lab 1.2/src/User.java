public class User {
    private final String username;
    private final ClientThread thread;

    public User(String username, ClientThread thread) {
        this.username = username;
        this.thread = thread;
    }

    public String getUsername() { return username; }
    public ClientThread getThread() { return thread; }

    @Override
    public String toString() { return username; }
}