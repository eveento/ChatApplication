package com.multi.server.client;

public interface MessageListener {
    public void onMessage(String login, String body);
}
