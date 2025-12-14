import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Client {
    private JFrame frame = new JFrame("Group Chat");
    private JTextPane chatPane = new JTextPane();
    private JTextField inputField = new JTextField(40);
    private JButton sendButton = new JButton("Send");
    private JList<String> userList = new JList<>();
    private DefaultListModel<String> userModel = new DefaultListModel<>();

    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public Client(String host, int port) {
        buildGUI();
        connectToServer(host, port);
    }

    private void buildGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        chatPane.setEditable(false);
        chatPane.setBackground(new Color(245, 245, 245));
        frame.add(new JScrollPane(chatPane), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Message:"));
        bottomPanel.add(inputField);
        bottomPanel.add(sendButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        userList.setModel(userModel);
        frame.add(new JScrollPane(userList), BorderLayout.EAST);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        frame.pack();
        frame.setSize(700, 500);
        frame.setVisible(true);
    }

    private void connectToServer(String host, int port) {
        new Thread(() -> {
            try {
                Socket socket = new Socket(host, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Get username
                username = JOptionPane.showInputDialog(frame, "Enter your username:", "Username", JOptionPane.PLAIN_MESSAGE);
                if (username == null || username.trim().isEmpty()) System.exit(0);
                out.println(username);

                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("NAMEACCEPTED")) {
                        String name = line.substring(13);
                        SwingUtilities.invokeLater(() -> frame.setTitle("Chat - " + name));
                    } else if (line.startsWith("MESSAGE ")) {
                        String msg = line.substring(8);  // "username: text"
                        appendMessage(msg);
                    } else if (line.startsWith("USERLIST ")) {
                        String[] users = line.substring(9).split(",");
                        updateUserList(users);
                    } else if (line.startsWith("ERROR:")) {
                        JOptionPane.showMessageDialog(frame, line.substring(6), "Error", JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }
                }
            } catch (IOException e) {
                appendMessage("Connection lost: " + e.getMessage());
            }
        }).start();
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            out.println(text);
            //local echo
            appendMessage(username + ": " + text);
            inputField.setText("");
        }
    }

    private void appendMessage(String fullMessage) {
        SwingUtilities.invokeLater(() -> {
            try {
                boolean isOwn = fullMessage.startsWith(username + ":");
                String text = fullMessage.substring(fullMessage.indexOf(":") + 2);

                javax.swing.text.Style style = chatPane.getStyledDocument().addStyle("Style", null);
                javax.swing.text.StyleConstants.setForeground(style, isOwn ? new Color(0, 102, 204) : Color.DARK_GRAY);
                javax.swing.text.StyleConstants.setAlignment(style, isOwn ? javax.swing.text.StyleConstants.ALIGN_RIGHT : javax.swing.text.StyleConstants.ALIGN_LEFT);
                javax.swing.text.StyleConstants.setFontFamily(style, "SansSerif");
                javax.swing.text.StyleConstants.setFontSize(style, 14);

                javax.swing.text.StyledDocument doc = chatPane.getStyledDocument();
                doc.insertString(doc.getLength(), text + "\n", style);

                // Auto-scroll
                chatPane.setCaretPosition(doc.getLength());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateUserList(String[] users) {
        SwingUtilities.invokeLater(() -> {
            userModel.clear();
            for (String u : users) {
                if (!u.isEmpty()) userModel.addElement(u);
            }
        });
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java ChatClient <host> <port>");
            System.exit(1);
        }
        new Client(args[0], Integer.parseInt(args[1]));
    }
}