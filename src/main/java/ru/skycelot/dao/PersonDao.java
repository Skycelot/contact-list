package ru.skycelot.dao;

import ru.skycelot.model.Person;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class PersonDao {

    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public PersonDao(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public List<Person> getAllPeople() {
        List<Person> people = new LinkedList<>();
        try (Connection db = openConnection()) {
            String query = "SELECT id, last_name, first_name, birth_date, phone_number FROM person ORDER BY last_name, first_name";
            try (PreparedStatement statement = db.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Person person = new Person();
                        person.setId(resultSet.getLong(1));
                        person.setLastName(resultSet.getString(2));
                        person.setFirstName(resultSet.getString(3));
                        person.setBirthdate(resultSet.getString(4));
                        person.setPhoneNumber(resultSet.getString(5));
                        people.add(person);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return people;
    }

    public Long createPerson(Person person) {
        Long id;
        try (Connection db = openConnection()) {
            String query = "INSERT INTO person (last_name, first_name, birth_date, phone_number) VALUES (?,?,?,?)";
            try (PreparedStatement statement = db.prepareStatement(query)) {
                statement.setString(1, person.getLastName());
                statement.setString(2, person.getFirstName());
                statement.setString(3, person.getBirthdate());
                statement.setString(4, person.getPhoneNumber());
                int insertedRows = statement.executeUpdate();
                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    resultSet.next();
                    id = resultSet.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public void createTable() {
        try (Connection db = openConnection()) {
            String query = "CREATE TABLE person (" +
                    "id IDENTITY, " +
                    "first_name VARCHAR(200)," +
                    "last_name VARCHAR(200)," +
                    "birth_date VARCHAR(8)," +
                    "phone_number VARCHAR(11)," +
                    "CONSTRAINT person_id PRIMARY KEY (id)" +
                    ")";
            try (PreparedStatement statement = db.prepareStatement(query)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
}
