package com.example.finance.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

public class GetURLContent {
    public static String RequestWithBody(String protocol, String host, String port, String path, List<String> names, List<Object> values){

        URL url = null;
        try {
            url = new URL(protocol+"://"+host+":"+port+path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println(url);
        // 打开和URL之间的连接
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            con.setRequestMethod("POST");//请求post方式
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        con.setUseCaches(false); // Post请求不能使用缓存
        con.setDoInput(true);// 设置是否从HttpURLConnection输入，默认值为 true
        con.setDoOutput(true);// 设置是否使用HttpURLConnection进行输出，默认值为 false

        //设置header内的参数 connection.setRequestProperty("健, "值");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("isTree", "true");
        con.setRequestProperty("isLastPage", "true");

        //设置body内的参数，put到JSONObject中
        JSONObject param = new JSONObject();
        if(names!=null && values!=null) {
            for (int i = 0; i < names.size(); i++) {
                param.put(names.get(i), values.get(i));
            }
        }

        con.setConnectTimeout(1000);
        // 建立实际的连接
        try {
            con.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 得到请求的输出流对象
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(con.getOutputStream(),"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (writer == null) return "E";
        try {
            writer.write(param.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 获取服务端响应，通过输入流来读取URL的响应
        InputStream is = null;
        try {
            is = con.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(is == null) return "E";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuffer sbf = new StringBuffer();
        String strRead = null;
        while (true) {
            try {
                if (!((strRead = reader.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            sbf.append(strRead);
            sbf.append("\r\n");
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 关闭连接
        con.disconnect();

        return sbf.toString();
    }
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
