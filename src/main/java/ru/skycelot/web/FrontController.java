package ru.skycelot.web;

import ru.skycelot.controller.PersonController;

import java.net.SocketAddress;

public class FrontController {

    private final HttpRequestResponseConverter httpRequestResponseConverter;
    private final PersonController personController;

    public FrontController(HttpRequestResponseConverter httpRequestResponseConverter, PersonController personController) {
        this.httpRequestResponseConverter = httpRequestResponseConverter;
        this.personController = personController;
    }

    public Response service(SocketAddress client, byte[] requestData) {
        HttpRequest request = httpRequestResponseConverter.parse(requestData);
        HttpResponse response = new HttpResponse();
        try {
            if (request.getPath().equals("/contact-list")) {
                if (request.getMethod() == HttpRequest.HttpMethod.GET) {
                    response = personController.getContactList();
                } else {
                    response.setResponseCode(HttpResponse.HttpResponseCode.MEHTOD_NOT_ALLOWED);
                    response.getHeaders().put("Allow", "GET");
                }
            } else if (request.getPath().equals("/contact-list/new")) {
                if (request.getMethod() == HttpRequest.HttpMethod.GET) {
                    response = personController.getPersonForm();
                } else if (request.getMethod() == HttpRequest.HttpMethod.POST) {
                    response = personController.createPerson(request.getParameters());
                } else {
                    response.setResponseCode(HttpResponse.HttpResponseCode.MEHTOD_NOT_ALLOWED);
                    response.getHeaders().put("Allow", "GET,POST");
                }
            } else {
                response.setResponseCode(HttpResponse.HttpResponseCode.NOT_FOUND);
            }
        } catch (Exception e) {
            response.setResponseCode(HttpResponse.HttpResponseCode.INTERNAL_SERVER_ERROR);
            response.getHeaders().put("Content-Type", "text/plain;charset=utf-8");
            response.setBody(e.toString());
        }
        byte[] responseBytes = httpRequestResponseConverter.toByteArray(response);
        return new Response(client, responseBytes);
    }

    public boolean isRequestCompleted(byte[] data) {
        return httpRequestResponseConverter.isRequestCompleted(data);
    }
}
