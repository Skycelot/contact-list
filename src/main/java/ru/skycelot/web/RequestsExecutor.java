package ru.skycelot.web;

import java.net.SocketAddress;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class RequestsExecutor {

    private final ExecutorService executor;
    private final Selector selector;
    private final Queue<Response> responses;
    private final FrontController frontController;

    public RequestsExecutor(ExecutorService executor, Selector selector, Queue<Response> responses, FrontController frontController) {
        this.executor = executor;
        this.selector = selector;
        this.responses = responses;
        this.frontController = frontController;
    }

    public void queueRequest(SocketAddress client, byte[] data) {
        new CompletableFuture<>().
                supplyAsync(() -> frontController.service(client, data)
                        , executor).
                thenAccept((response) -> {
                            responses.add(response);
                            selector.wakeup();
                        }
                );
    }

    public boolean isRequestCompleted(byte[] data) {
        return frontController.isRequestCompleted(data);
    }
}
