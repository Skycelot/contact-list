package ru.skycelot.web;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequestConverter {

    private static final Pattern CONTENT_LENGTH_VALUE = Pattern.compile("Content-Length:\\s*(\\d+)\\s+");

    public HttpRequest parse(byte[] requestBytes) {
        HttpRequest request = new HttpRequest();
        Map<String, String> headers = new HashMap<>();
        String requestData = new String(requestBytes, StandardCharsets.UTF_8);
        String[] lines = requestData.split("\r\n");
        String[] startLine = lines[0].trim().split("\\s+");
        request.setMethod(HttpRequest.HttpMethod.findByName(startLine[0]));
        request.setPath(startLine[1]);
        if (request.getMethod() == HttpRequest.HttpMethod.POST) {
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

    public boolean isRequestCompleted(byte[] data) {
        boolean completed = false;
        String text = new String(data, StandardCharsets.UTF_8);
        if (text.contains("\r\n\r\n")) {
            Matcher textMatcher = CONTENT_LENGTH_VALUE.matcher(text);
            int contentLength;
            if (textMatcher.find()) {
                String contentLengthText = textMatcher.group(1);
                contentLength = Integer.parseInt(contentLengthText);
                String[] requestParts = text.split("\r\n\r\n");
                String body = requestParts[1];
                int bodyLength = body.getBytes(StandardCharsets.UTF_8).length;
                completed = contentLength == bodyLength;
            } else {
                completed = true;
            }
        }
        return completed;
    }
}
