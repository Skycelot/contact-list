package ru.skycelot.web;

import java.net.SocketAddress;

public class Response {

    private final SocketAddress client;
    private final String text;

    public Response(SocketAddress client, String text) {
        this.client = client;
        this.text = text;
    }

    public SocketAddress getClient() {
        return client;
    }

    public String getText() {
        return text;
    }
}
