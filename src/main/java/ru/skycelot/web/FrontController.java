package ru.skycelot.web;

import ru.skycelot.controller.PersonController;

import java.net.SocketAddress;

public class FrontController {

    private final HttpRequestConverter httpRequestConverter;
    private final HttpResponseConverter httpResponseConverter;
    private final PersonController personController;

    public FrontController(HttpRequestConverter httpRequestConverter, HttpResponseConverter httpResponseConverter, PersonController personController) {
        this.httpRequestConverter = httpRequestConverter;
        this.httpResponseConverter = httpResponseConverter;
        this.personController = personController;
    }

    public Response service(SocketAddress client, byte[] requestData) {
        HttpRequest request = httpRequestConverter.parse(requestData);
        HttpResponse response = new HttpResponse();
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
        byte[] responseBytes = httpResponseConverter.toByteArray(response);
        return new Response(client, responseBytes);
    }

    public boolean isRequestCompleted(byte[] text) {
        return true;
    }
}
