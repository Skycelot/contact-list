package ru.skycelot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Client {

    public static void main(String[] args) throws IOException {
        URL url = new URL("http://127.0.0.1:8080/contact-list");
        // URL url = new URL("http://127.0.0.1:8080/contact-list");
        // URL url = new URL("http://127.0.0.1:8080/contact-list");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        int responseCode = connection.getResponseCode();
        InputStream response = connection.getErrorStream();
        boolean success = response == null;
        if (success) {
            response = connection.getInputStream();
        }
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        int dataByte;
        while ((dataByte = response.read()) != -1) {
            data.write(dataByte);
        }
        String responseText = new String(data.toByteArray(), StandardCharsets.UTF_8);
        System.out.println("Response code: " + responseCode);
        System.out.println("Response: " + responseText);
    }
}
