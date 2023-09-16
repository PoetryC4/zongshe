package com.example.finance.api.tushare;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class Tushare {
    public static String tushareApi(String apiName,String... params) {
        String jsonResponse = null;
        String modifiedJsonString = null;
        try {
            String apiUrl = "http://api.tushare.pro";
            JSONObject jsonObject = new JSONObject();
            for (String param : params) {
                String[] parts = param.split("=");
                String key = null;
                String value = null;
                if (parts.length == 2) {
                    key = parts[0];
                    value = parts[1];
                }
                jsonObject.put(key, value);
            }

            String jsonString = jsonObject.toString();

            String jsonData = "{\"api_name\": " + '"' + apiName + '"' + ", \"token\": " + '"' + "099d503dd05ac189ba998bf3404df708bdb38d73171b9b32ec70db26" + '"' + ", \"params\": " + jsonString + "}";


            // 创建URL对象
            URL url = new URL(apiUrl);

            // 创建HTTP连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为POST
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // 发送JSON数据
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 获取响应数据
            StringBuilder response;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

            }
            jsonResponse = response.toString();
            JsonObject jsonObject2 = JsonParser.parseString(jsonResponse).getAsJsonObject();
            jsonObject2.addProperty("code", "1");
            modifiedJsonString = jsonObject2.toString();
            connection.disconnect();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return modifiedJsonString;
    }

}

