package ru.skycelot.web;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.Instant;

public class Client {
    private final SocketChannel connection;
    private final Instant connectedOn;
    private ByteArrayOutputStream request;
    private boolean requestCompleted;
    private ByteBuffer response;

    public Client(SocketChannel connection, Instant connectedOn, ByteArrayOutputStream request) {
        this.connection = connection;
        this.connectedOn = connectedOn;
        this.request = request;
    }

    public SocketChannel getConnection() {
        return connection;
    }

    public Instant getConnectedOn() {
        return connectedOn;
    }

    public ByteArrayOutputStream getRequest() {
        return request;
    }

    public boolean isRequestCompleted() {
        return requestCompleted;
    }

    public void inputCompleted() {
        requestCompleted = true;
        request = null;
    }

    public ByteBuffer getResponse() {
        return response;
    }

    public void setResponse(ByteBuffer response) {
        this.response = response;
    }
}
