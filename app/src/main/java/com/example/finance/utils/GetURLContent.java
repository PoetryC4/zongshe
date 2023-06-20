package com.example.finance.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class GetURLContent {
    public static String getContent(String url,String method) {
        BufferedReader reader = null;
        String bookHSONString = null;

        HttpURLConnection conn = null;
        URL requestUrl = null;
        try {
            requestUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            conn = (HttpURLConnection) requestUrl.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod(method);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setConnectTimeout(1000);
        try {
            conn.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream inputstream = null;
        try {
            inputstream = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(inputstream == null) return "E";
        reader = new BufferedReader(new InputStreamReader(inputstream));

        StringBuilder builder = new StringBuilder();

        String line = "";
        while(true){
            try {
                if (!((line= reader.readLine())!=null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            builder.append(line);
            builder.append("\n");
        }
        if(builder.length()==0){
            return null;
        }
        bookHSONString = builder.toString();

        if(conn!=null) conn.disconnect();
        if(reader!=null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bookHSONString;
    }
}
