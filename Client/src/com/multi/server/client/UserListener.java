package com.multi.server.client;

public interface UserListener {
    public void online(String login);
    public void offline(String login);
}
