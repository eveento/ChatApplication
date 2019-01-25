package com.multi.server;

public class ServerMain {

    public static void main(String argc[]) {
        int port = 8808;
        Server server = new Server(port);
        server.start();
    }
}
