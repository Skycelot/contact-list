package ru.skycelot.controller;

import ru.skycelot.model.Person;
import ru.skycelot.service.PersonService;
import ru.skycelot.web.HttpResponse;

import java.util.List;
import java.util.Map;

public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    public HttpResponse getContactList() {
        List<Person> people = personService.getAllPeople();
        HttpResponse response = new HttpResponse();
        response.setResponseCode(HttpResponse.HttpResponseCode.OK);
        response.getHeaders().put("Content-Type", "text/html");
        response.setBody("<!DOCTYPE html>\r\n<html><head><title>Contact List</title></head><body><h1>Contact list:</h1></body></html>");
        return response;
    }

    public HttpResponse getPersonForm() {
        HttpResponse response = new HttpResponse();
        response.setResponseCode(HttpResponse.HttpResponseCode.OK);
        response.getHeaders().put("Content-Type", "text/html");
        response.setBody("<!DOCTYPE html>\r\n<html><head><title>Contact List</title></head><body><h1>Contact list:</h1></body></html>");
        return response;
    }

    public HttpResponse createPerson(Map<String, String> formParameters) {
        HttpResponse response = new HttpResponse();
        response.setResponseCode(HttpResponse.HttpResponseCode.MOVED_PERMANENTLY);
        response.getHeaders().put("Location", "/contact-list");
        return response;
    }
}
