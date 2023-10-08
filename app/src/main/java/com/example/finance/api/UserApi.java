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

public class UserApi {

    //public static String BASE_URL="http://10.0.2.2:6765/user/";
    public static String BASE_URL_2="http://"+ Conn.ipAddrHost+":"+ Conn.ipAddrPort +"/sys/brosehistory/";
    static class ThreadUser extends Thread{
        Integer a;
        String resStr;
        List<String> parameters;
        List<Object> values;
        String mapping;
        String method;

        public ThreadUser(List<String> parameters,List<Object> values,String mapping,String method){
            this.parameters = parameters;
            this.values = values;
            this.mapping = mapping;
            this.method = method;
        }

        @Override

        public void run(){
            resStr = GetURLContent.RequestWithBody("http","43.139.245.208","9999","/sys/brosehistory/"+mapping,parameters,values);
            //System.out.println("resStr:"+resStr);
            a = 1;
        }
    }
    static class ThreadUser_2 extends Thread{
        Integer a;
        String resStr;
        List<String> parameters;
        List<Object> values;
        String mapping;
        String method;

        public ThreadUser_2(List<String> parameters,List<Object> values,String mapping,String method){
            this.parameters = parameters;
            this.values = values;
            this.mapping = mapping;
            this.method = method;
        }

        @Override

        public void run(){
            // resStr = GetURLContent.RequestWithBody("http","10.0.2.2","6765","/user/"+mapping,parameters,values);
            resStr = GetURLContent.RequestWithBody("http","43.139.245.208","6765","/user/"+mapping,parameters,values);
            //System.out.println("resStr:"+resStr);
            a = 1;
        }
    }
    static class ThreadUser_3 extends Thread{
        Integer a;
        String resStr;
        String[] parameters;
        String[] values;
        String mapping;
        String method;

        public ThreadUser_3(String[] parameters,String[] values,String mapping,String method){
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
    static class ThreadUser_4 extends Thread{
        Integer a;
        String resStr;
        List<String> parameters;
        List<Object> values;
        String mapping;
        String method;

        public ThreadUser_4(List<String> parameters,List<Object> values,String mapping,String method){
            this.parameters = parameters;
            this.values = values;
            this.mapping = mapping;
            this.method = method;
        }

        @Override

        public void run(){
            // resStr = GetURLContent.RequestWithBody("http","10.0.2.2","6765","/user/"+mapping,parameters,values);
            resStr = GetURLContent.RequestWithBody("http","43.139.245.208","9999","/sys/brosehistory/"+mapping,parameters,values);
            //System.out.println("resStr:"+resStr);
            a = 1;
        }
    }
    public static R<String> CheckLogin(String username, String password) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("username");
        parameters.add("password");
        values.add(username);
        values.add(password);
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
            else if(Objects.equals((String) map.get("msg"), "s")) return R.error("账户被冻结");
            else return R.error("发生错误!");
        }
        return R.success("登陆成功");
    }

    public static R<String> CheckUsername(String username) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("username");
        values.add(username);
        ThreadUser_2 tu=new ThreadUser_2(parameters,values,"getUsername","POST");
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
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("email");
        values.add(email);
        ThreadUser_2 tu=new ThreadUser_2(parameters,values,"getEmail","POST");
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
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("username");
        parameters.add("password");
        parameters.add("email");
        values.add(username);
        values.add(password);
        values.add(email);
        //password = MD5Util.stringMD5(password);
        ThreadUser_2 tu=new ThreadUser_2(parameters,values,"register","POST");
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
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("email");
        parameters.add("password");
        values.add(email);
        values.add(password);
        //password = MD5Util.stringMD5(password);
        ThreadUser_2 tu=new ThreadUser_2(parameters,values,"updatePwd","POST");
        tu.start();
        while(tu.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) return R.error("修改失败");
        return R.success("修改成功");
    }

/*    public static R<Object> UserSearchHistory(String userId) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("userid");
        values.add(userId);
        ThreadUser tu=new ThreadUser(parameters,values,"search","POST");
        tu.start();
        while(tu.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) return R.error("获取失败");
        return R.success(map.get("data"));
    }*/

    public static R<Object> UserSearchHistory(String userId) throws IOException, InterruptedException {
        /*List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("userid");
        values.add(userId);*/
        String[] parameters = new String[]{"userid"};
        String[] values = new String[]{userId};
        ThreadUser_3 tu=new ThreadUser_3(parameters,values,"search","POST");
        tu.start();
        while(tu.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) return R.error("获取失败");
        return R.success(map.get("data"));
    }

    public static R<String> AddUserSearchHistory(String userId,String input,String addDate) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("userid");
        parameters.add("history");
        parameters.add("date");
        values.add(userId);
        values.add(input);
        values.add(addDate);
        ThreadUser tu=new ThreadUser(parameters,values,"add","POST");
        tu.start();
        while(tu.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu.resStr,Map.class);
        if(!map.containsKey("code")) return R.error("添加失败");
        return R.success("添加成功");
    }
/*    public static R<String> DeleteUserSearchHistory(String userId) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("id");
        values.add(userId);
        ThreadUser tu=new ThreadUser(parameters,values,"clean","DELETE");
        tu.start();
        while(tu.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) return R.error("添加失败");
        return R.success("添加成功");
    }*/

    public static R<String> DeleteUserSearchHistory(String userId) throws IOException, InterruptedException {
        String[] parameters = {"id"};
        String[] values = {userId};
        ThreadUser_3 tu=new ThreadUser_3(parameters,values,"clean","DELETE");
        tu.start();
        while(tu.a==null){
            Thread.sleep(100);
        }
        if (Objects.equals("E", tu.resStr)) return R.error("获取错误!");
        Map map = JSON.parseObject(tu.resStr,Map.class);
        if(!map.containsKey("code") || (Integer) map.get("code")==0) return R.error("添加失败");
        return R.success("添加成功");
    }

    public static R<Object> GetIdByName(String username) throws IOException, InterruptedException {
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("username");
        values.add(username);
        ThreadUser_2 tu=new ThreadUser_2(parameters,values,"getId","POST");
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
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("user_id");
        values.add(user_id);
        ThreadUser_2 tu=new ThreadUser_2(parameters,values,"getUser","POST");
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
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("user_id");
        values.add(userId);
        ThreadUser_2 tu=new ThreadUser_2(parameters,values,"setting","POST");
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
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("user_id");
        parameters.add("is_dark");
        values.add(userId);
        values.add(isDark);
        ThreadUser_2 tu=new ThreadUser_2(parameters,values,"setting/dark","POST");
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
        List<String> parameters = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parameters.add("user_id");
        parameters.add("is_new");
        values.add(userId);
        values.add(isNew);
        ThreadUser_2 tu=new ThreadUser_2(parameters,values,"setting/new","POST");
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
