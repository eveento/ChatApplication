package com.multi.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread{
    private final int serverPort;
    private ArrayList<ServerInstances> serverInstances = new ArrayList<>();

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }
    public List<ServerInstances> getServerInstancesList(){
        return serverInstances;
    }

    @Override
    public void run(){
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while (true) {
                System.out.println("About to accept client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from" + clientSocket);
                ServerInstances serverInstances = new ServerInstances(this, clientSocket);
                this.serverInstances.add(serverInstances);
                serverInstances.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeWorker(ServerInstances serverInstances) {
            this.serverInstances.remove(serverInstances);
    }
}
