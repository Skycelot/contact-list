package ru.skycelot.web;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private HttpResponseCode responseCode;
    private final Map<String, String> headers = new HashMap<>();
    private String body;

    public HttpResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(HttpResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public static enum HttpResponseCode {
        OK(200, "OK"),
        MOVED_PERMANENTLY(301, "Moved Permanently"),
        BAD_REQUEST(400, "Bad Request"),
        NOT_FOUND(404, "Not Found"),
        MEHTOD_NOT_ALLOWED(405, "Method Not Allowed");

        private final int code;
        private final String description;

        HttpResponseCode(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}
