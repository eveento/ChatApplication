package com.multi.server.client;

public interface MessageListener {
    void onMessage(String login, String body);
}
