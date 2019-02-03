package ru.skycelot.web;

import ru.skycelot.controller.PersonController;

import java.net.SocketAddress;

public class FrontController {

    private final HttpRequestConverter httpRequestConverter;
    private final PersonController personController;

    public FrontController(HttpRequestConverter httpRequestConverter, PersonController personController) {
        this.httpRequestConverter = httpRequestConverter;
        this.personController = personController;
    }

    public Response service(SocketAddress client, String requestData) {
        HttpRequest request = httpRequestConverter.parse(requestData);
        String responseData;
        if (request.getPath().equals("/contact-list")) {
            if (request.getMethod().equalsIgnoreCase("GET")) {
                responseData = personController.getContactList();
            } else {
                responseData = "HTTP/1.0 405 Method Not Allowed\r\nAllow:GET\r\n\r\n";
            }
        } else if (request.getPath().equals("/contact-list/new")) {
            if (request.getMethod().equalsIgnoreCase("GET")) {
                responseData = personController.getPersonForm();
            } else if (request.getMethod().equalsIgnoreCase("POST")) {
                responseData = personController.createPerson(request.getParameters());
            } else {
                responseData = "HTTP/1.0 405 Method Not Allowed\r\nAllow:GET,POST\r\n\r\n";
            }
        } else {
            responseData = "HTTP/1.0 404 Not Found\r\n\r\n";
        }
        return new Response(client, responseData);
    }

    public boolean isRequestCompleted(String text) {
        return true;
    }
}
