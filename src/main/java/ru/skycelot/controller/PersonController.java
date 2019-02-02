package ru.skycelot.controller;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class PersonController {

    public String getContactList() {
        String body = "<!DOCTYPE html>\r\n" +
                "<html><head><title>Contact List</title></head><body><h1>Contact list:</h1></body></html>";
        String text = "HTTP/1.0 200 OK\r\n" +
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
        String body = "<!DOCTYPE html>\r\n" +
                "<html><head><title>Contact List</title></head><body><h1>Contact list:</h1></body></html>";
        String text = "HTTP/1.0 200 OK\r\n" +
                "Content-Type:text/html\n" +
                "Content-Length:" + (body.getBytes(StandardCharsets.UTF_8).length + 1) + "\n" +
                "\r\n";
        return text + body;
    }
}
