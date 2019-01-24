package com.multi.server.client;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ChatClient {
    private final int serverPort;
    private final String serverName;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferIn;

    private ArrayList<MessageListener> messageListeners = new ArrayList<>();
    private ArrayList<UserListener> userListeners = new ArrayList<>();

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ChatClient chatClient = new ChatClient("localhost", 8808);
        chatClient.addUserListener(new UserListener() {
            @Override
            public void online(String login) {
                System.out.println("Online: " + login);
            }

            @Override
            public void offline(String login) {
                System.out.println("Offline: " + login);
            }
        });
        chatClient.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String login, String body) {
                System.out.println("You got a message from "+ login + "->" + body);
            }
        });
        if (!chatClient.connect()) {
            System.err.println("Connect failed!!!!");
        } else {
            System.out.println("Connect client successfully");
            if (chatClient.login("damian", "damian")) {
                System.out.println("Login successful");

                chatClient.msg("damian", "Siemka");
            } else {
                System.out.println("Login failed");
            }
//            chatClient.logoff();
        }
    }

    public void msg(String sendTo, String body) throws IOException {
        String cmd = "msg "+ sendTo + " "+ body + "\n";
        serverOut.write(cmd.getBytes());
    }

    public void logoff() throws IOException {
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());
    }

    public boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());
        String response = bufferIn.readLine();

        System.out.println("Response Line: " + response);

        if ("ok login".equalsIgnoreCase(response)) {
            startMessageReader();
            return true;
        } else {
            return false;
        }
    }

    private void startMessageReader() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                readMessageReapeter();
            }
        };
        thread.start();
    }

    private void readMessageReapeter() {
        try {
            String line;
            while (((line = bufferIn.readLine()) != null)) {
                String[] tokens = StringUtils.split(line);
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if("online".equalsIgnoreCase(cmd)){
                        handleOnline(tokens);
                    }else if ("offline".equalsIgnoreCase(cmd)){
                        handleOffline(tokens);
                    }else if("msg".equalsIgnoreCase(cmd)){
                        String[] tokensMsg = StringUtils.split(line, null, 3);
                        handleMessage(tokensMsg);
                    }
                }
            }
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(String[] tokensMsg) {
        String login =tokensMsg[1];
        String body = tokensMsg[2];
        for(MessageListener listener: messageListeners){
            listener.onMessage(login, body);
        }
    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for(UserListener  userListener : userListeners){
            userListener.offline(login);
        }
    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for(UserListener  userListener : userListeners){
            userListener.online(login);
        }
    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            System.out.println("Client port is " + socket.getLocalPort());
            this.bufferIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUserListener(UserListener listener) {
        userListeners.add(listener);
    }

    public void removeListener(UserListener listener) {
        userListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener){
        messageListeners.add(listener);
    }
    public void removeMessageListener(MessageListener listener){
        messageListeners.remove(listener);
    }
}
