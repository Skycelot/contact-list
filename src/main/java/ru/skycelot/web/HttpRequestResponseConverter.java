package ru.skycelot.web;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequestResponseConverter {

    private static final Pattern CONTENT_LENGTH_VALUE = Pattern.compile("Content-Length:\\s*(\\d+)\\s+");
    private static final String NEW_LINE = "\r\n";
    private static final String BODY_DELIMITER = NEW_LINE + NEW_LINE;

    public HttpRequest parse(byte[] requestBytes) {
        HttpRequest request = new HttpRequest();
        Map<String, String> headers = new HashMap<>();
        String requestData = new String(requestBytes, StandardCharsets.UTF_8);
        String[] lines = requestData.split(NEW_LINE);
        String[] startLine = lines[0].trim().split("\\s+");
        request.setMethod(HttpRequest.HttpMethod.findByName(startLine[0]));
        request.setPath(startLine[1]);
        if (request.getMethod() == HttpRequest.HttpMethod.POST) {
            String[] requestParts = requestData.split(BODY_DELIMITER);
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
        if (text.contains(BODY_DELIMITER)) {
            Matcher textMatcher = CONTENT_LENGTH_VALUE.matcher(text);
            int contentLength;
            if (textMatcher.find()) {
                String contentLengthText = textMatcher.group(1);
                contentLength = Integer.parseInt(contentLengthText);
                String[] requestParts = text.split(BODY_DELIMITER);
                String body = requestParts[1];
                int bodyLength = body.getBytes(StandardCharsets.UTF_8).length;
                completed = contentLength <= bodyLength;
            } else {
                completed = true;
            }
        }
        return completed;
    }

    public byte[] toByteArray(HttpResponse response) {
        byte[] bodyBytes = null;
        if (response.getBody() != null && !response.getBody().trim().isEmpty()) {
            bodyBytes = response.getBody().trim().getBytes(StandardCharsets.UTF_8);
            response.getHeaders().put("Content-Length", Integer.toString(bodyBytes.length));
        }
        StringBuilder heading = new StringBuilder("HTTP/1.0 ");
        heading.append(response.getResponseCode().getCode()).append(' ');
        heading.append(response.getResponseCode().getDescription()).append(NEW_LINE);
        response.getHeaders().entrySet().stream().
                map(entry -> entry.getKey() + ": " + entry.getValue() + NEW_LINE).
                forEach(header -> heading.append(header));
        heading.append(NEW_LINE);
        byte[] headingBytes = heading.toString().getBytes(StandardCharsets.UTF_8);
        byte[] responseBytes;
        if (bodyBytes != null) {
            responseBytes = new byte[headingBytes.length + bodyBytes.length];
            System.arraycopy(headingBytes, 0, responseBytes, 0, headingBytes.length);
            System.arraycopy(bodyBytes, 0, responseBytes, headingBytes.length, bodyBytes.length);
        } else {
            responseBytes = headingBytes;
        }
        return responseBytes;
    }
}
