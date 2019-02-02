package ru.skycelot;

import ru.skycelot.controller.*;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Launcher {

    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();
        Queue<Response> responses = new LinkedBlockingQueue<>();
        PersonController personController = new PersonController();
        FrontController frontController = new FrontController(personController);
        RequestsExecutor requestsExecutor = new RequestsExecutor(Executors.newFixedThreadPool(10), selector, responses, frontController);
        NetworkListener networkListener = new NetworkListener(selector, responses, requestsExecutor);
        networkListener.service();
    }
}
