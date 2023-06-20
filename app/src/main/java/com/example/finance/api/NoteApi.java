package com.example.finance.api;

import com.alibaba.fastjson.JSON;
import com.example.finance.common.Conn;
import com.example.finance.common.R;
import com.example.finance.utils.GetURLContent;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class NoteApi {

    public static String BASE_URL="http://"+ Conn.ipAddr +"/sys/myNotes/";

    static class ThreadNote extends Thread{
        Integer a;
        String resStr;
        String[] parameters;
        String[] values;
        String mapping;
        String method;

        public ThreadNote(String[] parameters,String[] values,String mapping,String method){
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

    public static R<Object> GetNotesByPage(String uesrId,Integer page,Integer pageSize,String input) throws IOException, InterruptedException {
        //String[] parameters = {"user_id","page","page_size","input"};
        String[] parameters = {"id","page","pageSize","input"};
        String[] values = {uesrId,String.valueOf(page),String.valueOf(pageSize),input};
        //ThreadNote tn=new ThreadNote(parameters,values,"getByPage","GET");
        ThreadNote tn=new ThreadNote(parameters,values,"getpage","GET");
        tn.start();
        while(tn.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tn.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tn.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }

    public static R<Object> GetNoteById(String uesrId,String noteId) throws IOException, InterruptedException {
        String[] parameters = {"userId","noteId"};
        String[] values = {uesrId,noteId};
        ThreadNote tn=new ThreadNote(parameters,values,"show","GET");
        tn.start();
        while(tn.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tn.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tn.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }

    public static R<String> SaveNote(String userId,String noteTitle,String dateCreated,String content) throws IOException, InterruptedException {
        String[] parameters = {"userId","title","noteDate","content"};
        String[] values = {userId,noteTitle,dateCreated,content};
        ThreadNote tn=new ThreadNote(parameters,values,"add","POST");
        tn.start();
        while(tn.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tn.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tn.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("保存错误!");
        }
        return R.success("保存成功!");
    }

    public static R<String> UpdateNote(String userId,String noteId,String noteTitle,String content,String newDate) throws IOException, InterruptedException {
        String[] parameters = {"userId","noteId","title","content","noteDate"};
        String[] values = {userId,noteId,noteTitle,content,newDate};
        ThreadNote tn=new ThreadNote(parameters,values,"update","POST");
        tn.start();
        while(tn.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tn.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tn.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("修改错误!");
        }
        return R.success("修改成功!");
    }

    public static R<String> DeleteNote(String userId,String noteId) throws IOException, InterruptedException {
        String[] parameters = {"userId","noteId"};
        String[] values = {userId,noteId};
        ThreadNote tn=new ThreadNote(parameters,values,"delete","DELETE");
        tn.start();
        while(tn.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tn.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tn.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("删除错误!");
        }
        return R.success("删除成功!");
    }


    public static R<Object> GetNoteCount(String userId,String input) throws IOException, InterruptedException {
        String[] parameters = {"id","input"};
        String[] values = {userId,input};
        ThreadNote tn=new ThreadNote(parameters,values,"getnum","GET");
        tn.start();
        while(tn.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tn.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tn.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            return R.error("获取错误!");
        }
        return R.success(map.get("data"));
    }
}
