package com.example.finance.api;

import com.alibaba.fastjson.JSON;
import com.example.finance.common.Conn;
import com.example.finance.common.R;
import com.example.finance.utils.GetURLContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FavorsApi {

    public static String BASE_URL ="http://"+ Conn.ipAddrHost+":"+ Conn.ipAddrPort +"/sys/userStock/";

    static class ThreadFavors extends Thread{
        Integer a;
        String resStr;
        List<String> parameters;
        List<Object> values;
        String mapping;
        String method;

        public ThreadFavors(List<String> parameters,List<Object> values,String mapping,String method){
            this.parameters = parameters;
            this.values = values;
            this.mapping = mapping;
            this.method = method;
        }

        @Override

        public void run(){
            resStr = GetURLContent.RequestWithBody("http","43.139.245.208","9999","/sys/userStock/"+mapping,parameters,values);
            //System.out.println("resStr:"+resStr);
            a = 1;
        }
    }

    static class ThreadFavors_2 extends Thread{
        Integer a;
        String resStr;
        List<String> parameters;
        List<Object> values;
        String mapping;
        String method;

        public ThreadFavors_2(List<String> parameters,List<Object> values,String mapping,String method){
            this.parameters = parameters;
            this.values = values;
            this.mapping = mapping;
            this.method = method;
        }

        @Override

        public void run(){
            StringBuilder url = new StringBuilder(BASE_URL + mapping + "?");
            for (int i = 0; i < parameters.size(); i++) {
                url.append(parameters.get(i));
                url.append("=");
                url.append(values.get(i));
                if(i != parameters.size() - 1) url.append("&");
            }
            System.out.println("url:"+url);
            resStr = GetURLContent.getContent(url.toString(),this.method);
            //System.out.println("resStr:"+resStr);
            a = 1;
        }
    }

    public static R<Object> GetFavorsByPage(String userId,Integer page,Integer pageSize) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("userId");
        parameters.add("page");
        parameters.add("pageSize");
        values.add(userId);
        values.add(page);
        values.add(pageSize);
        ThreadFavors tf=new ThreadFavors(parameters,values,"info","POST");
        tf.start();
        while(tf.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tf.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tf.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }

    public static R<String> GetFavorsById(String userId,String tsCode) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("userId");
        parameters.add("tsCode");
        values.add(userId);
        values.add(tsCode);
        ThreadFavors tf=new ThreadFavors(parameters,values,"getById","POST");
        tf.start();
        while(tf.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tf.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tf.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.success("N");
        }
        return R.success("Y");
    }

    public static R<String> AddFavors(String userId,String addDate,String tsCode) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("userId");
        parameters.add("favorDate");
        parameters.add("tsCode");
        values.add(userId);
        values.add(addDate);
        values.add(tsCode);
        ThreadFavors tf=new ThreadFavors(parameters,values,"add","POST");
        tf.start();
        while(tf.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tf.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tf.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("添加错误!");
        }
        return R.success("添加成功!");
    }

    public static R<String> DeleteFavors(String userId,String tsCode) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("userId");
        parameters.add("tsCode");
        values.add(userId);
        values.add(tsCode);
        ThreadFavors tf=new ThreadFavors(parameters,values,"delete","POST");
        tf.start();
        while(tf.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tf.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tf.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("删除错误!");
        }
        return R.success("删除成功!");
    }

    /*public static R<Object> GetFavorsCount(String userId,String input,String type) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("id");
        // parameters.add("input");
        values.add(userId);
        // values.add(input);
        ThreadFavors tf=new ThreadFavors(parameters,values,"getnum","POST");
        tf.start();
        while(tf.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tf.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tf.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }*/
    public static R<Object> GetFavorsCount(String userId,String input,String type) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("id");
        parameters.add("input");
        values.add(userId);
        values.add(input);
        ThreadFavors_2 tf=new ThreadFavors_2(parameters,values,"getnum","POST");
        tf.start();
        while(tf.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tf.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tf.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }
}
