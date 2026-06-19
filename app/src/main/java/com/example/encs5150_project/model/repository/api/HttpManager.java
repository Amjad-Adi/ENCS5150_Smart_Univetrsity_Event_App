package com.example.encs5150_project.model.repository.api;

import android.util.Log;

import java.io.*;
import java.net.*;

public class HttpManager {
    public static String fetchData(String urlString) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(10000);
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            return stringBuilder.toString();

        } catch (IOException e) {
            Log.e("HttpManager", "Network error: " + e.getMessage());
            return null;
        }
    }
}