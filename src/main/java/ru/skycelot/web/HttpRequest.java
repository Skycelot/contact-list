package ru.skycelot.web;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private HttpMethod method;
    private String path;
    private final Map<String, String> parameters = new HashMap<>();

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public static enum HttpMethod {
        GET, POST;

        public static HttpMethod findByName(String name) {
            name = name.toUpperCase();
            for (HttpMethod method : values()) {
                if (method.name().equals(name)) {
                    return method;
                }
            }
            return null;
        }
    }
}
