package ru.skycelot.controller;

import java.net.SocketAddress;
import java.nio.channels.Selector;

public class FrontController {

    private final Selector selector;

    public FrontController(Selector selector) {
        this.selector = selector;
    }

    public void requestData(SocketAddress client, String request) {

    }
}
