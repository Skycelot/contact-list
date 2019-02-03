package ru.skycelot;

import ru.skycelot.controller.PersonController;
import ru.skycelot.dao.PersonDao;
import ru.skycelot.service.PersonService;
import ru.skycelot.web.*;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Launcher {

    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();
        Queue<Response> responses = new LinkedBlockingQueue<>();
        HttpRequestConverter httpRequestConverter = new HttpRequestConverter();
        PersonDao personDao = new PersonDao("jdbc:h2:file:/home/mbrunmaier/var/contact-list", "user", "password");
        PersonService personService = new PersonService(personDao);
        PersonController personController = new PersonController(personService);
        FrontController frontController = new FrontController(httpRequestConverter, personController);
        RequestsExecutor requestsExecutor = new RequestsExecutor(Executors.newFixedThreadPool(10), selector, responses, frontController);
        NetworkListener networkListener = new NetworkListener("0.0.0.0", 8080, selector, responses, requestsExecutor);
        networkListener.service();
    }
}
