package ru.skycelot.web;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestConverter {

    public HttpRequest parse(byte[] requestData) {
        HttpRequest request = new HttpRequest();
        Map<String, String> headers = new HashMap<>();
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
        return request;
    }

    public boolean isRequestCompleted(String text) {
        return true;
    }
}
