package com.example.finance.api;

import com.alibaba.fastjson.JSON;
import com.example.finance.common.Conn;
import com.example.finance.common.R;
import com.example.finance.utils.GetURLContent;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class FavorsApi {

    public static String BASE_URL="http://"+ Conn.ipAddr +"/sys/userStock/";

    static class ThreadFavors extends Thread{
        Integer a;
        String resStr;
        String[] parameters;
        String[] values;
        String mapping;
        String method;

        public ThreadFavors(String[] parameters,String[] values,String mapping,String method){
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

    public static R<Object> GetFavorsByPage(String uesrId,Integer page,Integer pageSize) throws IOException, InterruptedException {
        String[] parameters = {"id","page","pageSize"};
        String[] values = {String.valueOf(uesrId),String.valueOf(page),String.valueOf(pageSize)};
        ThreadFavors tf=new ThreadFavors(parameters,values,"info","GET");
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

    public static R<String> GetFavorsById(String uesrId,String tsCode) throws IOException, InterruptedException {
        String[] parameters = {"uesrId","tsCode"};
        String[] values = {String.valueOf(uesrId),tsCode};
        ThreadFavors tf=new ThreadFavors(parameters,values,"getById","GET");
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
        String[] parameters = {"userId","favorDate","tsCode"};
        String[] values = {String.valueOf(userId),addDate,tsCode};
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
        String[] parameters = {"userId","tsCode"};
        String[] values = {String.valueOf(userId),tsCode};
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

    public static R<Object> GetFavorsCount(String userId,String input,String type) throws IOException, InterruptedException {
        String[] parameters = {"id","input"};
        String[] values = {userId,input};
        ThreadFavors tf=new ThreadFavors(parameters,values,"getnum","GET");
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
