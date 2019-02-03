package ru.skycelot.web;

import java.net.SocketAddress;

public class Response {

    private final SocketAddress client;
    private final byte[] data;

    public Response(SocketAddress client, byte[] data) {
        this.client = client;
        this.data = data;
    }

    public SocketAddress getClient() {
        return client;
    }

    public byte[] getData() {
        return data;
    }
}
