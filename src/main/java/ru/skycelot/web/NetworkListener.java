package ru.skycelot.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.Instant;
import java.util.*;

public class NetworkListener {

    private final String bindingIp;
    private final int port;
    private final ByteBuffer buffer = ByteBuffer.allocate(64 * 1024);
    private final Selector selector;
    private final RequestsExecutor requestsExecutor;
    private final Map<SocketAddress, Client> clients = new LinkedHashMap<>();
    private final Queue<Response> newResponses;

    public NetworkListener(String bindingIp, int port, Selector selector, Queue<Response> newResponses, RequestsExecutor requestsExecutor) {
        this.bindingIp = bindingIp;
        this.port = port;
        this.selector = selector;
        this.requestsExecutor = requestsExecutor;
        this.newResponses = newResponses;
    }

    public void service() throws IOException {

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(bindingIp, port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select(1000);
            if (Thread.interrupted()) break;
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> keysIterator = keys.iterator();
            while (keysIterator.hasNext()) {
                SelectionKey key = keysIterator.next();
                if (key.isAcceptable()) {
                    acceptSocket(serverChannel);
                } else if (key.isReadable()) {
                    readFromSocket(key);
                } else if (key.isWritable()) {
                    writeToSocket(key);
                }
                keysIterator.remove();
            }
            checkOldConnections();
            addResponses();
        }
    }

    private void acceptSocket(ServerSocketChannel serverChannel) {
        try {
            SocketChannel clientChannel = serverChannel.accept();
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
        } catch (Exception e) {
            System.out.println("Accept socket exception: " + e);
        }
    }

    private void readFromSocket(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            Client client = clients.computeIfAbsent(channel.getRemoteAddress(), (address) -> new Client(
                    channel, Instant.now(), new ByteArrayOutputStream()
            ));
            if (!client.isRequestCompleted()) {
                int readBytes;
                do {
                    readBytes = channel.read(buffer);
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        client.getRequest().write(buffer.get());
                    }
                    buffer.clear();
                } while (readBytes == buffer.capacity());
                byte[] request = client.getRequest().toByteArray();
                if (requestsExecutor.isRequestCompleted(request)) {
                    client.inputCompleted();
                    channel.shutdownInput();
                    requestsExecutor.queueRequest(channel.getRemoteAddress(), request);
                }
            }
        } catch (Exception e) {
            System.out.println("Read socket exception: " + e);
        }
    }

    private void writeToSocket(SelectionKey key) {
        SocketChannel channel = null;
        Client client = null;
        try {
            channel = (SocketChannel) key.channel();
            client = clients.get(channel.getRemoteAddress());
            channel.write(client.getResponse());
            if (!client.getResponse().hasRemaining()) {
                clients.remove(channel.getRemoteAddress());
                channel.close();
            }
        } catch (Exception e) {
            System.out.println("Write socket exception: " + e);
            if (client != null) {
                clients.remove(client);
            }
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    private void checkOldConnections() {
        Iterator<Map.Entry<SocketAddress, Client>> clientIterator = clients.entrySet().iterator();
        while (clientIterator.hasNext()) {
            Client client = clientIterator.next().getValue();
            if (!client.isRequestCompleted() &&
                    Instant.now().toEpochMilli() - client.getConnectedOn().toEpochMilli() > 30000) {
                try {
                    client.getConnection().close();
                } catch (Exception e) {
                    System.out.println("Close socket exception: " + e);
                }
                clientIterator.remove();
            }
        }
    }

    private void addResponses() throws ClosedChannelException {
        Response response;
        while ((response = newResponses.poll()) != null) {
            Client client = clients.get(response.getClient());
            client.setResponse(ByteBuffer.wrap(response.getData()));
            client.getConnection().register(selector, SelectionKey.OP_WRITE);
        }
    }
}
