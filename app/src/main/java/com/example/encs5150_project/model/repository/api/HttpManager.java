package com.example.encs5150_project.model.repository.api;

import java.io.*;
import java.net.*;

public class HttpManager{
    public static String fetchData(String urlString){
        try{
            URL url=new URL(urlString);
            HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuilder stringBuilder=new StringBuilder();
            String line=bufferedReader.readLine();
            while(line!=null){
                stringBuilder.append(line+"\n");
                line=bufferedReader.readLine();
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
