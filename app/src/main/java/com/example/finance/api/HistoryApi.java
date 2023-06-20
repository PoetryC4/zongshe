package com.example.finance.api;

import com.alibaba.fastjson.JSON;
import com.example.finance.common.Conn;
import com.example.finance.common.R;
import com.example.finance.utils.GetURLContent;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class HistoryApi {

    public static String BASE_URL="http://"+ Conn.ipAddr +"/sys/searchHistory/";

    static class ThreadHistory extends Thread{
        Integer a;
        String resStr;
        String[] parameters;
        String[] values;
        String mapping;
        String method;

        public ThreadHistory(String[] parameters,String[] values,String mapping,String method){
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

    public static R<Object> GetHistoryByPage(String uesrId,Integer page,Integer pageSize,String input) throws IOException, InterruptedException {
        String[] parameters = {"userId","page","pageSize","input"};
        String[] values = {String.valueOf(uesrId),String.valueOf(page),String.valueOf(pageSize),input};
        /*String[] parameters = {"user_id","page","page_size","type","input"};
        String[] values = {String.valueOf(uesrId),String.valueOf(page),String.valueOf(pageSize),type,input};*/
        ThreadHistory th=new ThreadHistory(parameters,values,"search","GET");
        th.start();
        while(th.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", th.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(th.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }
    
    public static R<String> AddHistory(String userId,String addDate,String tsCode,String stock_name) throws IOException, InterruptedException {
        String[] parameters = {"userId","sDate","tsCode","tsName"};
        String[] values = {String.valueOf(userId),addDate,tsCode,stock_name};
        ThreadHistory th=new ThreadHistory(parameters,values,"add","POST");
        th.start();
        while(th.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", th.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(th.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("添加错误!");
        }
        return R.success("添加成功!");
    }

    public static R<String> DeleteHistory(Integer userId,String historyId) throws IOException, InterruptedException {
        String[] parameters = {"user_id","history_id"};
        String[] values = {String.valueOf(userId),historyId};
        ThreadHistory th=new ThreadHistory(parameters,values,"delete","POST");
        th.start();
        while(th.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", th.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(th.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("删除错误!");
        }
        return R.success("删除成功!");
    }

    public static R<Object> GetHistoryCount(String userId,String input,String type) throws IOException, InterruptedException {
        String[] parameters = {"userId","input"};
        String[] values = {userId,input};
        ThreadHistory th=new ThreadHistory(parameters,values,"getnum","GET");
        th.start();
        while(th.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", th.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(th.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }

}
