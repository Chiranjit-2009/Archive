/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smpl.archive;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class LoginManager {
    public static final String CONFIG_FILE_PATH = ".//Config//config.properties";
    
//    private static final String USERNAME = "superadmin";
//    private static final String PASSWORD = "ATempo@123#xyz:";  
    private String token;

    public String login() throws FileNotFoundException, IOException {
        String token = null;
        Properties properties = new Properties();
//        FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH);
//        properties.load(fis);
        
        String ip = getDatabaseIp();
        String userName =properties.getProperty("db.username");
        String password =properties.getProperty("db.password");
        try {

            String apiUrl = "http://"+ip+":9443/manager/users/login";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            String requestBody = "{ \"user\": \"" + userName + "\", \"password\": \"" + password + "\" }";
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
                this.token=token;
            } else {
                System.out.println("Failed to log in. Status Code: " + status);
                this.token=null;
            }
            connection.disconnect();
        } catch (IOException e) {
        }
        return token;
    }

public String getToken() {
    return token; // Return the stored token
}

public String getDatabaseIp() throws IOException{
   Properties properties = new Properties();
      try(FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)){
        properties.load(fis);
        return properties.getProperty("db.ip"); 
      }
}
    
}
