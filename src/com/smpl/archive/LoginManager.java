/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smpl.archive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class LoginManager {

    private static final String USERNAME = "superadmin";
    private static final String PASSWORD = "ATempo@123#xyz:";

    public String login() {
        String token = null;
        try {
            String apiUrl = "http://3.144.242.95:9443/manager/users/login";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String requestBody = "{ \"user\": \"" + USERNAME + "\", \"password\": \"" + PASSWORD + "\" }";
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int status = connection.getResponseCode();
            System.out.println("Login HTTP Status Code: " + status);

            if (status == HttpURLConnection.HTTP_OK) {
                StringBuilder response;
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }
                token = response.toString().replace("{\"token\":\"", "").replace("\"}", "");
                System.out.println("Bearer Token: " + token);
            } else {
                System.out.println("Failed to log in. Status Code: " + status);
            }
            connection.disconnect();
        } catch (IOException e) {
        }
        return token;
    }

    String getToken() {
        return null;
        
    }

    
}
