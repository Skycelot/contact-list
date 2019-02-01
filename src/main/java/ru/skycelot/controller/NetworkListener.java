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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class NetworkListener {

    private final ByteBuffer buffer = ByteBuffer.allocate(64 * 1024);
    private final Selector selector;
    private final FrontController frontController;
    private final Map<SocketAddress, ByteBuffer> requests = new HashMap<>();
    private final Map<SocketAddress, ByteBuffer> responses = new HashMap<>();

    public NetworkListener(Selector selector, FrontController frontController) {
        this.selector = selector;
        this.frontController = frontController;
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
                    buffer.clear();
                    int readBytes = channel.read(buffer);
                    buffer.flip();
                    ByteArrayOutputStream data = new ByteArrayOutputStream();
                    while (buffer.hasRemaining()) {
                        data.write(buffer.get());
                    }
                    String text = new String(data.toByteArray(), StandardCharsets.UTF_8);
                    System.out.println("Request size: " + readBytes);
                    System.out.println("Request text: " + text);
                    channel.register(selector, SelectionKey.OP_WRITE);
                } else if (key.isWritable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    buffer.clear();
                    byte[] data = "HTTP/1.0 200 OK".getBytes(StandardCharsets.UTF_8);
                    for (int offset = 0; offset < data.length; offset++) {
                        buffer.put(data[offset]);
                    }
                    buffer.flip();
                    int writtenBytes = channel.write(buffer);
                    System.out.println("Response size: " + writtenBytes);
                    channel.close();
                }
                keysIterator.remove();
            }
        }
    }
}
