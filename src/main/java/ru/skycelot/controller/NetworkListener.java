package ru.skycelot.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class NetworkListener {

    private final ByteBuffer buffer = ByteBuffer.allocate(64 * 1024);
    private final Selector selector;
    private final RequestsExecutor requestsExecutor;
    private final Map<SocketAddress, SocketChannel> clients = new HashMap<>();
    private final Map<SocketAddress, ByteArrayOutputStream> requests = new HashMap<>();
    private final Queue<Response> newResponses;
    private final Map<SocketAddress, ByteBuffer> responses = new HashMap<>();

    public NetworkListener(Selector selector, Queue<Response> newResponses, RequestsExecutor requestsExecutor) {
        this.selector = selector;
        this.requestsExecutor = requestsExecutor;
        this.newResponses = newResponses;
    }

    public void service() throws IOException {

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress("0.0.0.0", 8080));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (!Thread.interrupted()) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> keysIterator = keys.iterator();
            while (keysIterator.hasNext()) {
                SelectionKey key = keysIterator.next();
                if (key.isAcceptable()) {
                    SocketChannel clientChannel = serverChannel.accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("Got a client connection");
                } else if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    clients.put(channel.getRemoteAddress(), channel);
                    buffer.clear();
                    int readBytes = channel.read(buffer);
                    buffer.flip();
                    ByteArrayOutputStream data = requests.computeIfAbsent(channel.getRemoteAddress(), (address) -> new ByteArrayOutputStream());
                    while (buffer.hasRemaining()) {
                        data.write(buffer.get());
                    }
                    String text = new String(data.toByteArray(), StandardCharsets.UTF_8);
                    if (requestsExecutor.isRequestCompleted(text)) {
                        requests.remove(channel.getRemoteAddress());
                        requestsExecutor.requestData(channel.getRemoteAddress(), text);
                        System.out.println("Request size: " + data.size());
                        System.out.println("Request: " + text);
                    } else {
                        requests.put(channel.getRemoteAddress(), data);
                        System.out.println("Request part with size: " + readBytes);
                    }
                } else if (key.isWritable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer responseBuffer = responses.get(channel.getRemoteAddress());
                    int writtenBytes = channel.write(responseBuffer);
                    System.out.println("Response size: " + writtenBytes);
                    if (!responseBuffer.hasRemaining()) {
                        responses.remove(channel.getRemoteAddress());
                        channel.close();
                    }
                }
                keysIterator.remove();
            }

            Response response;
            while ((response = newResponses.poll()) != null) {
                responses.put(response.getClient(), response.getData());
                SocketChannel channel = clients.get(response.getClient());
                channel.register(selector, SelectionKey.OP_WRITE);
            }
        }
    }
}
