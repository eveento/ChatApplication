package com.multi.server.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MessagePanel extends JPanel implements MessageListener {
    private final ChatClient client;
    private final String login;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> messageList = new JList<>(listModel);
    private JTextField inputField = new JTextField();

    public MessagePanel(ChatClient chatClient, String login) {
        this.client  = chatClient;
        this.login = login;

        chatClient.addMessageListener(this);
        setLayout(new BorderLayout());
        add(messageList, BorderLayout.CENTER);
        add(new JScrollPane(messageList),BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text =inputField.getText();
                    chatClient.msg(login, text);
                    listModel.addElement("You: " + text);
                    inputField.setText("");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onMessage(String login, String body) {
        if(login.equalsIgnoreCase(login)) { //filter out msg from other users
            String line = login + ": " + body;
            listModel.addElement(line);
        }
    }
}
