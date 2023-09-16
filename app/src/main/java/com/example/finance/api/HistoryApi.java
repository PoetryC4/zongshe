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

public class HistoryApi {

    static class ThreadHistory extends Thread{
        Integer a;
        String resStr;
        List<String> parameters;
        List<Object> values;
        String mapping;
        String method;

        public ThreadHistory(List<String> parameters,List<Object> values,String mapping,String method){
            this.parameters = parameters;
            this.values = values;
            this.mapping = mapping;
            this.method = method;
        }

        @Override

        public void run(){
            resStr = GetURLContent.RequestWithBody("http","43.139.245.208","9999","/sys/searchHistory/"+mapping,parameters,values);
            //System.out.println("resStr:"+resStr);
            a = 1;
        }
    }

    public static R<Object> GetHistoryByPage(String userId,Integer page,Integer pageSize,String input) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("userId");
        parameters.add("page");
        parameters.add("pageSize");
        parameters.add("input");
        values.add(userId);
        values.add(page);
        values.add(pageSize);
        values.add(input);
        /*String[] parameters = {"user_id","page","page_size","type","input"};
        String[] values = {String.valueOf(uesrId),String.valueOf(page),String.valueOf(pageSize),type,input};*/
        ThreadHistory th=new ThreadHistory(parameters,values,"search","POST");
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
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("userId");
        parameters.add("sDate");
        parameters.add("tsCode");
        parameters.add("tsName");
        values.add(userId);
        values.add(addDate);
        values.add(tsCode);
        values.add(stock_name);
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
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("user_id");
        parameters.add("history_id");
        values.add(userId);
        values.add(historyId);
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
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("userId");
        parameters.add("input");
        values.add(userId);
        values.add(input);
        ThreadHistory th=new ThreadHistory(parameters,values,"getnum","POST");
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
