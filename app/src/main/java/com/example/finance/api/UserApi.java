package com.example.finance.api;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.finance.common.Conn;
import com.example.finance.common.R;
import com.example.finance.utils.MD5Util;
import com.example.finance.utils.GetURLContent;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class UserApi {

    public static String BASE_URL_2="http://"+ Conn.ipAddr +"/sys/brosehistory/";
    public static String BASE_URL="http://43.139.245.208:6765/user/";

    static class ThreadUser extends Thread{
        Integer a;
        String resStr;
        String[] parameters;
        String[] values;
        String mapping;
        String method;

        public ThreadUser(String[] parameters,String[] values,String mapping,String method){
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
    static class ThreadUser_2 extends Thread{
        Integer a;
        String resStr;
        String[] parameters;
        String[] values;
        String mapping;
        String method;

        public ThreadUser_2(String[] parameters,String[] values,String mapping,String method){
            this.parameters = parameters;
            this.values = values;
            this.mapping = mapping;
            this.method = method;
        }

        @Override

        public void run(){
            StringBuilder url = new StringBuilder(BASE_URL_2 + mapping + "?");
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

    public static R<String> CheckLogin(String username, String password) throws IOException, InterruptedException {
        String[] parameters = {"username","password"};
        String[] values = {username,MD5Util.stringMD5(password)};
        ThreadUser tu=new ThreadUser(parameters,values,"login","POST");
        tu.start();
        while(tu.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) {
            if(Objects.equals((String) map.get("msg"), "n")) return R.error("用户不存在");
            else if(Objects.equals((String) map.get("msg"), "p")) return R.error("密码不正确");
            else return R.error("发生错误!");
        }
        return R.success("登陆成功");
    }

    public static R<String> CheckUsername(String username) throws IOException, InterruptedException {
        String[] parameters = {"username"};
        String[] values = {username};
        ThreadUser tu=new ThreadUser(parameters,values,"getUsername","GET");
        tu.start();
        while(tu.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) return R.error("该用户名已存在");
        return R.success("");
    }

    public static R<String> CheckEmail(String email) throws IOException, InterruptedException {
        String[] parameters = {"email"};
        String[] values = {email};
        ThreadUser tu=new ThreadUser(parameters,values,"getEmail","GET");
        tu.start();
        while(tu.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) return R.error("该邮箱已被占用");
        else return R.success("");
    }

    public static R<String> UserRegister(String username, String password, String email) throws IOException, InterruptedException {
        password = MD5Util.stringMD5(password);
        String[] parameters = {"username","password","email"};
        String[] values = {username,password,email};
        ThreadUser tu=new ThreadUser(parameters,values,"register","POST");
        tu.start();
        while(tu.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) return R.error("注册失败");
        return R.success("注册成功");
    }
    public static R<String> UpdatePwd(String email, String password) throws IOException, InterruptedException {
        password = MD5Util.stringMD5(password);
        String[] parameters = {"email","password"};
        String[] values = {email,password};
        ThreadUser tu=new ThreadUser(parameters,values,"updatePwd","POST");
        tu.start();
        while(tu.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) return R.error("修改失败");
        return R.success("修改成功");
    }

    public static R<Object> UserSearchHistory(String userId) throws IOException, InterruptedException {
        String[] parameters = {"userid"};
        String[] values = {userId};
        ThreadUser_2 tu_2=new ThreadUser_2(parameters,values,"search","GET");
        tu_2.start();
        while(tu_2.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu_2.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu_2.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) return R.error("获取失败");
        return R.success(map.get("data"));
    }

    public static R<String> AddUserSearchHistory(String userId,String input,String addDate) throws IOException, InterruptedException {
        String[] parameters = {"userid","history","date"};
        String[] values = {userId,input,addDate};
        ThreadUser_2 tu_2=new ThreadUser_2(parameters,values,"add","POST");
        tu_2.start();
        while(tu_2.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu_2.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu_2.resStr,Map.class);
        if(!map.containsKey("code")) return R.error("添加失败");
        return R.success("添加成功");
    }
    public static R<String> DeleteUserSearchHistory(String userId) throws IOException, InterruptedException {
        String[] parameters = {"id"};
        String[] values = {userId};
        ThreadUser_2 tu_2=new ThreadUser_2(parameters,values,"clean","DELETE");
        tu_2.start();
        while(tu_2.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu_2.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu_2.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) return R.error("添加失败");
        return R.success("添加成功");
    }

    public static R<Object> GetIdByName(String username) throws IOException, InterruptedException {
        String[] parameters = {"username"};
        String[] values = {username};
        ThreadUser tu=new ThreadUser(parameters,values,"getId","POST");
        tu.start();
        while(tu.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) return R.error("添加失败");
        return R.success(map.get("data"));
    }
    public static R<Object> GetDataById(String user_id) throws IOException, InterruptedException {
        String[] parameters = {"user_id"};
        String[] values = {user_id};
        ThreadUser tu=new ThreadUser(parameters,values,"getUser","POST");
        tu.start();
        while(tu.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) return R.error("获取失败");
        return R.success(map.get("data"));
    }

    public static R<Object> GetSettingsById(String userId) throws IOException, InterruptedException {
        String[] parameters = {"user_id"};
        String[] values = {userId};
        ThreadUser tu=new ThreadUser(parameters,values,"setting","GET");
        tu.start();
        while(tu.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) return R.error("获取失败");
        return R.success(map.get("data"));
    }
    public static R<Object> UpdateSettingDark(String userId, Integer isDark) throws IOException, InterruptedException {
        String[] parameters = {"user_id","is_dark"};
        String[] values = {userId, String.valueOf(isDark)};
        ThreadUser tu=new ThreadUser(parameters,values,"setting/dark","GET");
        tu.start();
        while(tu.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) return R.error("获取失败");
        return R.success(map.get("data"));
    }
    public static R<Object> UpdateSettingNew(String userId, Integer isNew) throws IOException, InterruptedException {
        String[] parameters = {"user_id","is_new"};
        String[] values = {userId, String.valueOf(isNew)};
        ThreadUser tu=new ThreadUser(parameters,values,"setting/new","GET");
        tu.start();
        while(tu.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) return R.error("获取失败");
        return R.success(map.get("data"));
    }
}
