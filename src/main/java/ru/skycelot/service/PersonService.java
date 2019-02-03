package ru.skycelot.service;

import ru.skycelot.dao.PersonDao;
import ru.skycelot.model.Person;

import java.util.List;

public class PersonService {

    private final PersonDao personDao;

    public PersonService(PersonDao personDao) {
        this.personDao = personDao;
    }

    public List<Person> getAllPeople() {
        return personDao.getAllPeople();
    }

    public void createPerson(Person person) {
        personDao.createPerson(person);
    }
}
