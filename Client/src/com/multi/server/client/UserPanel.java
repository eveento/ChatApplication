package com.multi.server.client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserPanel extends JPanel implements UserListener {

    private final ChatClient client;
    private JList<String> userList;
    private DefaultListModel<String> userModelList;

    public UserPanel(ChatClient chatClient) {
        this.client = chatClient;
        this.client.addUserListener(this);

        userModelList = new DefaultListModel<>();
        userList = new JList<>(userModelList);
        setLayout(new BorderLayout());
        add(userList, BorderLayout.CENTER);

        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    String login = userList.getSelectedValue();
                    MessagePanel messagePanel = new MessagePanel(chatClient, login);
                    JFrame jFrame = new JFrame("Message: " + login);
                    jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    jFrame.setSize(500, 500);
                    jFrame.getContentPane().add(messagePanel, BorderLayout.CENTER);
                    jFrame.setVisible(true);
                }
            }
        });
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient("localhost", 8808);
        UserPanel userPanel = new UserPanel(chatClient);
        JFrame jFrame = new JFrame("User");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(450, 700);
        jFrame.getContentPane().add(new JScrollPane(userPanel), BorderLayout.CENTER);
        jFrame.setVisible(true);
    }

    @Override
    public void online(String login) {
        userModelList.addElement(login);
    }

    @Override
    public void offline(String login) {
        userModelList.removeElement(login);
    }
}
