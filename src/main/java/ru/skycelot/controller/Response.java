package ru.skycelot.controller;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class Response {

    private final SocketAddress client;
    private final ByteBuffer data;

    public Response(SocketAddress client, ByteBuffer data) {
        this.client = client;
        this.data = data;
    }

    public SocketAddress getClient() {
        return client;
    }

    public ByteBuffer getData() {
        return data;
    }
}
