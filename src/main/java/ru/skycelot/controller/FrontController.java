package ru.skycelot.controller;

import java.net.SocketAddress;

public class FrontController {

    private final HttpRequestParser httpRequestParser;
    private final PersonController personController;

    public FrontController(HttpRequestParser httpRequestParser, PersonController personController) {
        this.httpRequestParser = httpRequestParser;
        this.personController = personController;
    }

    public Response service(SocketAddress client, String requestData) {
        HttpRequest request = httpRequestParser.parse(requestData);
        String responseData = "HTTP/1.0 400 Bad request\r\n\r\n";
        if (request.getPath().equals("/contact-list")) {
            if (request.getMethod().equalsIgnoreCase("GET")) {
                responseData = personController.getContactList();
            }
        } else if (request.getPath().equals("/contact-list/new")) {
            if (request.getMethod().equalsIgnoreCase("GET")) {
                responseData = personController.getPersonForm();
            } else if (request.getMethod().equalsIgnoreCase("POST")) {
                responseData = personController.createPerson(request.getParameters());
            }
        }
        return new Response(client, responseData);
    }

    public boolean isRequestCompleted(String text) {
        return true;
    }
}
