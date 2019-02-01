package ru.skycelot;

import ru.skycelot.controller.FrontController;
import ru.skycelot.controller.NetworkListener;
import ru.skycelot.controller.RequestsExecutor;
import ru.skycelot.controller.Response;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Launcher {

    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();
        Queue<Response> responses = new LinkedBlockingQueue<>();
        FrontController frontController = new FrontController();
        RequestsExecutor requestsExecutor = new RequestsExecutor(Executors.newFixedThreadPool(10), selector, responses);
        NetworkListener networkListener = new NetworkListener(selector, responses, requestsExecutor);
        networkListener.service();
    }
}
