package ru.skycelot.web;

import java.nio.charset.StandardCharsets;

public class HttpResponseConverter {

    private final static String NEW_LINE = "\r\n";

    public byte[] toByteArray(HttpResponse response) {
        StringBuilder responseData = new StringBuilder("HTTP/1.0 ");
        responseData.append(response.getResponseCode().getCode()).append(' ');
        responseData.append(response.getResponseCode().getDescription()).append(NEW_LINE);
        response.getHeaders().entrySet().stream().
                map(entry -> entry.getKey() + ": " + entry.getValue() + NEW_LINE).
                forEach(header -> responseData.append(header));
        if (response.getBody() != null && !response.getBody().trim().isEmpty()) {
            responseData.append(NEW_LINE);
            //TODO
        }
        return responseData.toString().getBytes(StandardCharsets.UTF_8);
    }
}
