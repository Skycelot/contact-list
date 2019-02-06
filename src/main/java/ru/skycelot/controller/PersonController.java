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
        response.getHeaders().put("Content-Type", "text/html;charset=utf-8");
        StringBuilder body = new StringBuilder("<!DOCTYPE html>\r\n<html><head><title>Contact List</title></head><body><h1>Contact list:</h1>");
        body.append("<a href=\"/contact-list/new\">New person</a>");
        body.append("<table><tr><th>Last Name</th><th>First Name</th><th>Birth Date</th><th>Phone Number</th></tr>");
        for (Person person: people) {
            body.append("<tr><td>").append(person.getLastName());
            body.append("</td><td>").append(person.getFirstName());
            body.append("</td><td>").append(person.getBirthdate());
            body.append("</td><td>").append(person.getPhoneNumber());
            body.append("</td></tr>");
        }
        body.append("</table></body></html>");
        response.setBody(body.toString());
        return response;
    }

    public HttpResponse getPersonForm() {
        HttpResponse response = new HttpResponse();
        response.setResponseCode(HttpResponse.HttpResponseCode.OK);
        response.getHeaders().put("Content-Type", "text/html;charset=utf-8");
        StringBuilder body = new StringBuilder("<!DOCTYPE html>\r\n<html><head><title>Contact List</title></head><body><h1>New person:</h1>");
        body.append("<form action=\"/contact-list/new\" method=\"POST\">");
        body.append("<div><label>Last Name:<input name=\"last-name\" /></label></div>");
        body.append("<div><label>First Name:<input name=\"first-name\" /></label></div>");
        body.append("<div><label>Birth Date:<input name=\"birth-date\" /></label></div>");
        body.append("<div><label>Phone Number:<input name=\"phone-number\" /></label></div>");
        body.append("<div><input type=\"submit\" /></div>");
        body.append("</form</body></html>");
        response.setBody(body.toString());
        return response;
    }

    public HttpResponse createPerson(Map<String, String> formParameters) {
        Person person = new Person();
        person.setLastName(formParameters.get("last-name"));
        person.setFirstName(formParameters.get("first-name"));
        person.setBirthdate(formParameters.get("birth-date"));
        person.setPhoneNumber(formParameters.get("phone-number"));
        personService.createPerson(person);
        HttpResponse response = new HttpResponse();
        response.setResponseCode(HttpResponse.HttpResponseCode.MOVED_PERMANENTLY);
        response.getHeaders().put("Location", "/contact-list");
        return response;
    }
}
