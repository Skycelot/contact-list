package ru.skycelot.controller;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

public class FrontController {

    public Response service(SocketAddress client, String requestData) {
        HttpRequest request = new HttpRequest();
        String[] lines = requestData.split("\r\n");
        String[] startLine = lines[0].trim().split("\\s+");
        request.setMethod(startLine[0]);
        request.setPath(startLine[1]);
        if (request.getMethod().toUpperCase().equals("POST")) {
            String[] requestParts = requestData.split("\r\n\r\n");
            String body = requestParts[1].trim();
            String[] pairs = body.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                request.getParameters().put(key, value);
            }
        }

        String body = "<!DOCTYPE html>\r\n" +
                "<html><head><title>Contact List</title></head><body><h1>Contact list:</h1></body></html>";
        String text = "HTTP/1.0 200 OK\r\n" +
                "Content-Type:text/html\n" +
                "Content-Length:" + (body.getBytes(StandardCharsets.UTF_8).length + 1) + "\n" +
                "\r\n";
        return new Response(client, text + body);
    }

    public boolean isRequestCompleted(String text) {
        return true;
    }
}
