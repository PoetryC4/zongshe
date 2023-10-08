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

public class TopicApi {

    public static String BASE_URL="http://"+ Conn.ipAddrHost+":"+Conn.ipAddrPort +"/sys/news/";

    static class ThreadTopic_2 extends Thread{
        Integer a;
        String resStr;
        String[] parameters;
        String[] values;
        String mapping;
        String method;

        public ThreadTopic_2(String[] parameters,String[] values,String mapping,String method){
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
    static class ThreadTopic extends Thread{
        Integer a;
        String resStr;
        List<String> parameters;
        List<Object> values;
        String mapping;
        String method;

        public ThreadTopic(List<String> parameters,List<Object> values,String mapping,String method){
            this.parameters = parameters;
            this.values = values;
            this.mapping = mapping;
            this.method = method;
        }

        @Override

        public void run(){
            resStr = GetURLContent.RequestWithBody("http","43.139.245.208","9999","/sys/news/"+mapping,parameters,values);
            //System.out.println("resStr:"+resStr);
            a = 1;
        }
    }

    public static R<Object> GetTopicsByPage(String input,Integer page,Integer pageSize) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("input");
        parameters.add("page");
        parameters.add("pageSize");
        values.add(input);
        values.add(page);
        values.add(pageSize);
        ThreadTopic tt=new ThreadTopic(parameters,values,"new","POST");
        tt.start();
        while(tt.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tt.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tt.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }

    /*public static R<Object> GetTopicById(String topicId) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("id");
        values.add(topicId);
        ThreadTopic tt=new ThreadTopic(parameters,values,"getone","GET");
        tt.start();
        while(tt.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tt.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tt.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }*/

    public static R<Object> GetTopicCount(String input) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("input");
        values.add(input);
        ThreadTopic tt=new ThreadTopic(parameters,values,"newnum","GET");
        tt.start();
        while(tt.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tt.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tt.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }
    public static R<Object> GetTopicById(String topicId) throws IOException, InterruptedException {
        /*String[] parameters = {"newsId"};
        String[] values = {topicId};*/
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("newsId");
        values.add(topicId);
        ThreadTopic tt=new ThreadTopic(parameters,values,"getone","POST");
        tt.start();
        while(tt.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tt.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tt.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }

    /*public static R<Object> GetTopicCount(String input) throws IOException, InterruptedException {
        String[] parameters = {"input"};
        String[] values = {input};
        ThreadTopic_2 tt=new ThreadTopic_2(parameters,values,"newnum","GET");
        tt.start();
        while(tt.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tt.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tt.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }*/
}
