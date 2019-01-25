package com.multi.server;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

public class ServerInstances extends Thread {

    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;
    private HashSet<String> room = new HashSet<>();

    public ServerInstances(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    private void clientSocketHandler() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String text;
        while ((text = reader.readLine()) != null) {
            String[] tokens = StringUtils.split(text);
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if ("logoff".equals(cmd) || "quit".equalsIgnoreCase(text)) {
                    LogOffHandler();
                    break;
                } else if ("login".equalsIgnoreCase(cmd)) {
                    LogInHandler(outputStream, tokens);
                } else if ("msg".equalsIgnoreCase(cmd)) {
                    String[] tokensMsg = StringUtils.split(text, null, 3);
                    MessageHandler(tokensMsg);
                }else if("join".equalsIgnoreCase(cmd)){
                    joinRoomHandler(tokens);
                }else if("leave".equalsIgnoreCase(cmd)){
                    leaveHandler(tokens);
                }
                    else {
                    String msg = "unknown: " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }
        clientSocket.close();
    }

    private void leaveHandler(String[] tokens) {
        if(tokens.length>1){
            String room = tokens[1];
            this.room.remove(room);
        }
    }

    public boolean isRoomMember(String topic){
        return room.contains(topic);
    }
    private void joinRoomHandler(String[] tokens) {
        if(tokens.length>1){
            String room = tokens[1];
            this.room.add(room);
        }
    }

    private void MessageHandler(String[] tokens) throws IOException {
        String send = tokens[1];
        String body = tokens[2];

        boolean isRoom = send.charAt(0) == '@';//create room

        List<ServerInstances> serverInstancesList = server.getServerInstancesList();
        for (ServerInstances serverInstances : serverInstancesList) {
            if(isRoom){
                if(serverInstances.isRoomMember(send)){
                    String msg = "msg " + send+ ": " +login + " " + body + "\n";
                    serverInstances.send(msg);
                }
            }else {
                if (send.equalsIgnoreCase(serverInstances.getLogin())) {
                    String msg = "msg " + login + " " + body + "\n";
                    serverInstances.send(msg);
                }
            }
        }
    }

    private void LogOffHandler() throws IOException {
        server.removeWorker(this);
        List<ServerInstances> serverInstances = server.getServerInstancesList();
        String msg = "offline " + login + "\n";
        for (ServerInstances serverInstancesWorker : serverInstances) {
            if (!login.equals(serverInstancesWorker.getLogin())) {
                serverInstancesWorker.send(msg);
            }
            clientSocket.close();
        }
    }

    public String getLogin() {
        return login;
    }

    private void LogInHandler(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];

            //Init User
            if ((login.equals("guest") && password.equals("guest")) ||
                    login.equals("damian") && password.equals("damian")||
                    login.equals("pawel") && password.equals("pawel")) {

                String msg = "User logged in successfully\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User logged in successfully: "+login+"\n");

                List<ServerInstances> serverInstancesList = server.getServerInstancesList();

                for (ServerInstances serverInstances : serverInstancesList) {
                    if (serverInstances.getLogin() != null) {
                        if (!login.equals(serverInstances.getLogin())) {
                            String msgStatus = "online " + serverInstances.getLogin() + "\n";
                            send(msgStatus);
                        }
                    }
                }

                String statusMsg = "online "+ login + "\n";
                for (ServerInstances serverInstances : serverInstancesList) {
                    if (!login.equals(serverInstances.getLogin())) {
                        serverInstances.send(statusMsg);
                    }
                }
            } else {
                String msg = "error login\n";
                outputStream.write(msg.getBytes());
                System.err.println("Login failed for: "+ login);
            }
        }
    }

    private void send(String msg) throws IOException {
        if (login != null) {
            outputStream.write(msg.getBytes());
        }
    }

    @Override
    public void run() {
        try {
            clientSocketHandler();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
