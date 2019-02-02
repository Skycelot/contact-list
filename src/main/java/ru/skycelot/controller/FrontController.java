package ru.skycelot.controller;

import java.net.SocketAddress;

public class FrontController {

    private final PersonController personController;

    public FrontController(PersonController personController) {
        this.personController = personController;
    }

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

        if (request.getPath().equals("/contact-list")) {
            if (request.getMethod().equalsIgnoreCase("GET")) {
                String responseData = personController.getContactList();
            }
        } else if (request.getPath().equals("/contact-list/new")) {
            if (request.getMethod().equalsIgnoreCase("GET")) {
                String responseData = personController.getPersonForm();
            } else if (request.getMethod().equalsIgnoreCase("POST")) {
                String responseData = personController.createPerson(request.getParameters());
            }
        }
        return new Response(client, requestData);
    }

    public boolean isRequestCompleted(String text) {
        return true;
    }
}
