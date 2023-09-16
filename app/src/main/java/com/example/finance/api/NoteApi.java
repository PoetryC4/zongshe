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

public class NoteApi {

    static class ThreadNote extends Thread{
        Integer a;
        String resStr;
        List<String> parameters;
        List<Object> values;
        String mapping;
        String method;

        public ThreadNote(List<String> parameters,List<Object> values,String mapping,String method){
            this.parameters = parameters;
            this.values = values;
            this.mapping = mapping;
            this.method = method;
        }

        @Override

        public void run(){
            resStr = GetURLContent.RequestWithBody("http","43.139.245.208","9999","/sys/myNotes/"+mapping,parameters,values);
            //System.out.println("resStr:"+resStr);
            a = 1;
        }
    }

    public static R<Object> GetNotesByPage(String userId,Integer page,Integer pageSize,String input) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("id");
        parameters.add("page");
        parameters.add("pageSize");
        parameters.add("input");
        values.add(userId);
        values.add(page);
        values.add(pageSize);
        values.add(input);
        //String[] parameters = {"user_id","page","page_size","input"};
        //ThreadNote tn=new ThreadNote(parameters,values,"getByPage","POST");
        ThreadNote tn=new ThreadNote(parameters,values,"getpage","POST");
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

    public static R<Object> GetNoteById(String userId,String noteId) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("userId");
        parameters.add("noteId");
        values.add(userId);
        values.add(noteId);
        ThreadNote tn=new ThreadNote(parameters,values,"show","POST");
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
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("userId");
        parameters.add("title");
        parameters.add("noteDate");
        parameters.add("content");
        values.add(userId);
        values.add(noteTitle);
        values.add(dateCreated);
        values.add(content);
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
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("userId");
        parameters.add("noteId");
        parameters.add("title");
        parameters.add("content");
        parameters.add("noteDate");
        values.add(userId);
        values.add(noteId);
        values.add(noteTitle);
        values.add(content);
        values.add(newDate);
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
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("userId");
        parameters.add("noteId");
        values.add(userId);
        values.add(noteId);
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
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("id");
        parameters.add("input");
        values.add(userId);
        values.add(input);
        ThreadNote tn=new ThreadNote(parameters,values,"getnum","POST");
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
