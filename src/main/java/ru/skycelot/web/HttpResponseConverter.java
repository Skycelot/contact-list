package ru.skycelot.web;

import java.nio.charset.StandardCharsets;

public class HttpResponseConverter {

    private final static String NEW_LINE = "\r\n";

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
            System.arraycopy(bodyBytes, 0, responseBytes, 0, bodyBytes.length);
        } else {
            responseBytes = headingBytes;
        }
        return responseBytes;
    }
}
