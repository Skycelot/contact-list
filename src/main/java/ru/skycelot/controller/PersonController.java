package ru.skycelot.controller;

import ru.skycelot.model.Person;
import ru.skycelot.service.PersonService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    public String getContactList() {
        List<Person> people = personService.getAllPeople();
        StringBuilder body = new StringBuilder("<!DOCTYPE html>\r\n");
        body.append("<html><head><title>Contact List</title></head><body><h1>Contact list:</h1></body></html>");

        String heading = "HTTP/1.0 200 OK\r\n" +
                "Content-Type:text/html\n" +
                "Content-Length:" + (body.getBytes(StandardCharsets.UTF_8).length + 1) + "\n" +
                "\r\n";
        return text + body;
    }

    public String getPersonForm() {
        String body = "<!DOCTYPE html>\r\n" +
                "<html><head><title>Contact List</title></head><body><h1>Contact list:</h1></body></html>";
        String text = "HTTP/1.0 200 OK\r\n" +
                "Content-Type:text/html\n" +
                "Content-Length:" + (body.getBytes(StandardCharsets.UTF_8).length + 1) + "\n" +
                "\r\n";
        return text + body;
    }

    public String createPerson(Map<String, String> formParameters) {
        return "HTTP/1.0 301 Moved Permanently\r\n" +
                "Location:/contact-list\r\n\r\n";
    }
}
