package ru.skycelot.controller;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class RequestsExecutor {

    private final ExecutorService executor;
    private final Selector selector;
    private final Queue<Response> responses;

    public RequestsExecutor(ExecutorService executor, Selector selector, Queue<Response> responses) {
        this.executor = executor;
        this.selector = selector;
        this.responses = responses;
    }

    public void requestData(SocketAddress client, String data) {
        HttpRequest request = new HttpRequest();
        String[] lines = data.split("\r\n");
        String[] startLine = lines[0].trim().split("\\s+");
        request.setMethod(startLine[0]);
        request.setPath(startLine[1]);
        for (int offset = 1; offset < lines.length; offset++) {
            String headerLine = lines[offset].trim();
            if (headerLine.isEmpty()) {
                break;
            }
            String[] keyValue = headerLine.split(":");
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();
            request.getHeaders().put(key, value);
        }
        new CompletableFuture<>().
                supplyAsync(() -> {
                            String body = "<!DOCTYPE html>\r\n" +
                                    "<html><head><title>Contact List</title></head><body><h1>Contact list:</h1></body></html>";
                            String text = "HTTP/1.0 200 OK\r\n" +
                                    "Content-Type:text/html\n" +
                                    "Content-Length:" + (body.getBytes(StandardCharsets.UTF_8).length + 1) + "\n" +
                                    "\r\n";
                            return new Response(client, ByteBuffer.wrap((text + body).getBytes(StandardCharsets.UTF_8)));
                        }
                        , executor).
                thenAccept((response) -> {
                            responses.add(response);
                            selector.wakeup();
                        }
                );
    }

    public boolean isRequestCompleted(String text) {
        return text.endsWith("\r\n\r\n");
    }
}
