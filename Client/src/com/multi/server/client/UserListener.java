package com.multi.server.client;

public interface UserListener {
    void online(String login);
    void offline(String login);
}
