/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smpl.archive;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;


public class ArchiveManager {

     // Collection name used for archiving files.
    private static final String COLLECTION_NAME = "Test";
    

    // Checks if a file is already archived.    
    public boolean isFileArchived(String objectName, String token) {
        try {
            String baseUrl = "http://3.144.242.95:9443/manager/objects/info";
            String urlWithParams = baseUrl + "?objectName=" + URLEncoder.encode(objectName, "UTF-8") + "&collectionName=" + URLEncoder.encode(COLLECTION_NAME, "UTF-8");
            System.out.println("Constructed URL: " + urlWithParams);

            URL url = new URL(urlWithParams);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Accept", "application/json");

            int status = connection.getResponseCode();
            System.out.println("GET Request HTTP Status Code: " + status);

            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
//                System.out.println("Response: " + jsonResponse.toString(4)); // Pretty-print JSON

                return true; // Assume file is archived if response is valid
            } else if (status == HttpURLConnection.HTTP_NOT_FOUND) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
 // Archives a specified file and returns the request ID.
     public String archiveFile(String objectName, String token) {
        try {
            String apiUrl = "http://3.144.242.95:9443/manager/requests/archive";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setDoOutput(true);

            JSONObject requestBody = new JSONObject();
            requestBody.put("collectionName", COLLECTION_NAME);
            requestBody.put("comments", "Archiving file");
            requestBody.put("components", new String[]{objectName});
            requestBody.put("media", "NLD_DISK");
            requestBody.put("objectName", objectName);
            requestBody.put("options", "");
            requestBody.put("priority", 50);
            requestBody.put("qos", 0);
            requestBody.put("sourceServer", "TestCifs");

            // Send request to server.
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int status = connection.getResponseCode();
            System.out.println("Archive HTTP Status Code: " + status);

            if (status == HttpURLConnection.HTTP_OK) {
                // Parse the response and return request ID.
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                JSONObject jsonResponse = new JSONObject(response.toString());
//                System.out.println("Archive Response: " + jsonResponse.toString(4));
                return jsonResponse.optString("requestId", "");
            } else {
                System.out.println("Failed to archive file: " + objectName);
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
     // Retrieves archive information based on request ID.
     public JSONObject getArchiveInfo(String requestId, String token) {
        try {
            String apiUrl = "http://3.144.242.95:9443/manager/requests/" + requestId;
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Accept", "application/json");

            int status = connection.getResponseCode();
//            System.out.println("GET Archive Info HTTP Status Code: " + status);

            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

//                JSONObject jsonResponse = new JSONObject(response.toString());
//                System.out.println("Archive Info Response: " + jsonResponse.toString(4)); // Pretty-print JSON
//                return jsonResponse;
                return new JSONObject(response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


