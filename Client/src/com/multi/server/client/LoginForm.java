package com.multi.server.client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LoginForm extends JFrame{

    private final ChatClient chatClient;
    JTextField loginField = new JTextField();
    JPasswordField jPasswordField = new JPasswordField();
    JButton jButton = new JButton("Log in");

    public LoginForm(){
        super("Log in");

        this.chatClient = new ChatClient("localhost", 8808);
        chatClient.connect();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.add(loginField);
        jPanel.add(jPasswordField);
        jPanel.add(jButton);

        jButton.addActionListener(e -> logInToApp());

        getContentPane().add(jPanel, BorderLayout.CENTER);

        pack(); //autosize

        setVisible(true);

    }

    private void logInToApp() {
        String login = loginField.getText();
        String pass = jPasswordField.getText();

        try {
            if(chatClient.login(login, pass)){
                UserPanel userPanel = new UserPanel(chatClient);
                JFrame jFrame = new JFrame("User");
                jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                jFrame.setSize(400, 600);
                jFrame.getContentPane().add(new JScrollPane(userPanel), BorderLayout.CENTER);
                jFrame.setVisible(true);
                setVisible(false);
            }else {
                JOptionPane.showMessageDialog(this, "Invalid user or passworld");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){

        LoginForm loginForm = new LoginForm();
        loginForm.setVisible(true);
        }

}
