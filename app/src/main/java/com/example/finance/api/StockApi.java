package com.example.finance.api;

import android.os.Build;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.example.finance.common.Conn;
import com.example.finance.common.R;
import com.example.finance.utils.GetURLContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StockApi {
    public static String BASE_URL="http://"+ Conn.ipAddrHost+":"+ Conn.ipAddrPort +"/sys/";

    static class ThreadStock_2 extends Thread{
        Integer a;
        String resStr;
        String[] parameters;
        String[] values;
        String mapping;
        String method;

        public ThreadStock_2(String[] parameters,String[] values,String mapping,String method){
            this.parameters = parameters;
            this.values = values;
            this.mapping = mapping;
            this.method = method;
        }

        @Override

        public void run(){
            StringBuilder url = new StringBuilder(BASE_URL + mapping + "?");
            for (int i = 0; i < parameters.length; i++) {
                url.append(parameters[i]);
                url.append("=");
                url.append(values[i]);
                if(i != parameters.length - 1) url.append("&");
            }
            System.out.println("url:"+url);
            resStr = GetURLContent.getContent(url.toString(),this.method);
            //System.out.println("resStr:"+resStr);
            a = 1;
        }
    }

    static class ThreadStock extends Thread{
        Integer a;
        String resStr;
        List<String> parameters;
        List<Object> values;
        String mapping;
        String method;

        public ThreadStock(List<String> parameters,List<Object> values,String mapping,String method){
            this.parameters = parameters;
            this.values = values;
            this.mapping = mapping;
            this.method = method;
        }

        @Override

        public void run(){
            resStr = GetURLContent.RequestWithBody("http","43.139.245.208","9999","/sys/"+mapping,parameters,values);
            //System.out.println("resStr:"+resStr);
            a = 1;
        }
    }

/*    public static R<Object> GetLatest(String tsCode) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("tsCode");
        values.add(tsCode);
        ThreadStock ts=new ThreadStock(parameters,values,"daily/info","GET");
        ts.start();
        while(ts.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", ts.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(ts.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }*/

    public static R<Object> GetLatest(String tsCode) throws IOException, InterruptedException {
        String[] parameters = {"tsCode"};
        String[] values = {tsCode};
        ThreadStock_2 ts=new ThreadStock_2(parameters,values,"daily/info","POST");
        ts.start();
        while(ts.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", ts.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(ts.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }

    public static R<Object> GetAlikeNames(String input,Integer amount) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("input");
        parameters.add("amount");
        values.add(input);
        values.add(amount);
        ThreadStock ts=new ThreadStock(parameters,values,"stockInfo/like","GET");
        ts.start();
        while(ts.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", ts.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(ts.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }

    public static R<Object> GetAlikeAll(String input,Integer page,Integer pageSize) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("input");
        parameters.add("page");
        parameters.add("pageSize");
        values.add(input);
        values.add(page);
        values.add(pageSize);
        ThreadStock ts=new ThreadStock(parameters,values,"stockInfo/show","GET");
        ts.start();
        while(ts.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", ts.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(ts.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }
/*    public static R<Object> GetAlikeCount(String input) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("input");
        values.add(input);
        ThreadStock ts=new ThreadStock(parameters,values,"stockInfo/getnum","GET");
        ts.start();
        while(ts.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", ts.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(ts.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }*/

    public static R<Object> GetAlikeCount(String input) throws IOException, InterruptedException {
        /*String[] parameters = {"input"};
        String[] values = {input};*/
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("input");
        values.add(input);
        ThreadStock ts=new ThreadStock(parameters,values,"stockInfo/getnum","POST");
        ts.start();
        while(ts.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", ts.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(ts.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }
    /*public static R<Object> GetCashflow(String tsCode) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("id");
        values.add(tsCode);
        ThreadStock ts=new ThreadStock(parameters,values,"cashFlow/show","GET");
        ts.start();
        while(ts.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", ts.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(ts.resStr,Map.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(map.get("data") == null || !Objects.equals(map.get("data").getClass().getTypeName(),"com.alibaba.fastjson.JSONArray")) return R.error("E");
        }
        List<Map<String,String>> lst = (List<Map<String,String>>) JSONArray.parse(((JSONArray)map.get("data")).toJSONString());
        if(!map.containsKey("code") || (Integer) map.get("code")==0 || lst.size()==0) {
            return R.error("获取错误!");
        }
        return R.success(lst.get(0));
    }

    public static R<Object> GetBalance(String tsCode) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("id");
        values.add(tsCode);
        ThreadStock ts=new ThreadStock(parameters,values,"balanceSheet/show","GET");
        ts.start();
        while(ts.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", ts.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(ts.resStr,Map.class);
        List<Map<String,String>> lst = (List<Map<String,String>>) JSONArray.parse(((JSONArray)map.get("data")).toJSONString());
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(lst.get(0));
    }

    public static R<Object> GetIncome(String tsCode) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("id");
        values.add(tsCode);
        ThreadStock ts=new ThreadStock(parameters,values,"income/show","GET");
        ts.start();
        while(ts.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", ts.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(ts.resStr,Map.class);
        List<Map<String,String>> lst = (List<Map<String,String>>) JSONArray.parse(((JSONArray)map.get("data")).toJSONString());
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(lst.get(0));
    }*/
    public static R<Object> GetCashflow(String tsCode) throws IOException, InterruptedException {
        String[] parameters = {"id"};
        String[] values = {tsCode};
        ThreadStock_2 ts=new ThreadStock_2(parameters,values,"cashFlow/show","POST");
        ts.start();
        while(ts.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", ts.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(ts.resStr,Map.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(map.get("data") == null || !Objects.equals(map.get("data").getClass().getTypeName(),"com.alibaba.fastjson.JSONArray")) return R.error("E");
        }
        List<Map<String,String>> lst = (List<Map<String,String>>) JSONArray.parse(((JSONArray)map.get("data")).toJSONString());
        if(!map.containsKey("code") || (Integer) map.get("code")==0 || lst.size()==0) {
            return R.error("获取错误!");
        }
        return R.success(lst.get(0));
    }

    public static R<Object> GetBalance(String tsCode) throws IOException, InterruptedException {
        String[] parameters = {"id"};
        String[] values = {tsCode};
        ThreadStock_2 ts=new ThreadStock_2(parameters,values,"balanceSheet/show","POST");
        ts.start();
        while(ts.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", ts.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(ts.resStr,Map.class);
        List<Map<String,String>> lst = (List<Map<String,String>>) JSONArray.parse(((JSONArray)map.get("data")).toJSONString());
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(lst.get(0));
    }

    public static R<Object> GetIncome(String tsCode) throws IOException, InterruptedException {
        String[] parameters = {"id"};
        String[] values = {tsCode};
        ThreadStock_2 ts=new ThreadStock_2(parameters,values,"income/show","POST");
        ts.start();
        while(ts.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", ts.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(ts.resStr,Map.class);
        List<Map<String,String>> lst = (List<Map<String,String>>) JSONArray.parse(((JSONArray)map.get("data")).toJSONString());
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(lst.get(0));
    }
}
