import javax.swing.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;

public class Client extends UnicastRemoteObject implements ClientInterface {

    private JFrame frame = new JFrame("Group Chat");
    private JTextPane chatPane = new JTextPane();
    private JTextField inputField = new JTextField(40);
    private JButton sendButton = new JButton("Send");
    private JList<String> userList = new JList<>();
    private DefaultListModel<String> userModel = new DefaultListModel<>();

    private ServerInterface server;
    private String username;

    public Client(String host, int port) throws RemoteException {
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
        try {
            Registry myRegistry = LocateRegistry.getRegistry(host, port);
            server = (ServerInterface) myRegistry.lookup("ChatServer");

            //Vraag om de username
            username = JOptionPane.showInputDialog(frame, "Enter your username:", "Username", JOptionPane.PLAIN_MESSAGE);
            if (username == null || username.trim().isEmpty()) System.exit(0);

            //Registreer de client's eigen remote object
            server.registerClient(this, username);

            frame.setTitle("Chat - " + username);

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    try {
                        server.unregisterClient(username);
                    } catch (RemoteException e) { /* ... */ }
                    System.exit(0);
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Fout bij RMI connectie: " + e.getMessage(), "Fout", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            try {
                server.sendMessage(username, text);
                inputField.setText("");

                // Lokale echo
                appendMessage(username + ": " + text, true);

            } catch (RemoteException re) {
                JOptionPane.showMessageDialog(frame, "Verbinding met server verloren.", "Fout", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void receiveMessage(String fullMessage) throws RemoteException {
        appendMessage(fullMessage, false); // Berichten van anderen
    }

    @Override
    public void updateUserList(Set<String> users) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            userModel.clear();
            for (String u : users) {
                if (!u.isEmpty()) userModel.addElement(u);
            }
        });
    }

    private void appendMessage(String fullMessage, boolean isOwn) {
        SwingUtilities.invokeLater(() -> {
            try {
                String textToDisplay;

                    textToDisplay = fullMessage;



                javax.swing.text.Style style = chatPane.getStyledDocument().addStyle("Style", null);
                javax.swing.text.StyleConstants.setForeground(style, isOwn ? new Color(0, 102, 204) : Color.DARK_GRAY);
                javax.swing.text.StyleConstants.setAlignment(style, isOwn ? javax.swing.text.StyleConstants.ALIGN_RIGHT : javax.swing.text.StyleConstants.ALIGN_LEFT);
                javax.swing.text.StyleConstants.setFontFamily(style, "SansSerif");
                javax.swing.text.StyleConstants.setFontSize(style, 14);

                javax.swing.text.StyledDocument doc = chatPane.getStyledDocument();
                doc.insertString(doc.getLength(), textToDisplay + "\n", style);

                chatPane.setCaretPosition(doc.getLength());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}