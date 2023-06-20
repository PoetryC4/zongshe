package com.example.finance.other;

import static com.example.finance.utils.BasicFunctions.createJsonFile;

import java.sql.*;
import java.text.SimpleDateFormat;

public class DBfunctions {
    public static final String URL = "jdbc:mysql://10.0.2.2:3306/finance";
    public static final String USER = "root";
    public static final String PASSWORD = "123456";
    public static boolean isUsrNameExisting(String username) throws ClassNotFoundException, SQLException, InterruptedException {//检查用户名是否存在
        ThreadUser tu=new ThreadUser(username);
        tu.start();
        while(tu.a==null){
            Thread.sleep(100);
        }
        return tu.flag;
    }
    static class ThreadUser extends Thread{
        Integer a;
        boolean flag;
        String username;

        public ThreadUser(String username){
            this.username=username;
        }

        @Override

        public void run(){
            try {
                Class.forName("com.mysql.jdbc.Driver");

                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement stmt = conn.createStatement();
                String sql="select count(*) from user_info where user_name='"+username+"' group by user_name;";
                ResultSet rs = stmt.executeQuery(sql);
                if(rs.next()) {
                    stmt.close();
                    rs.close();
                    conn.close();
                    a=1;
                    flag=true;
                }
                else {
                    stmt.close();
                    rs.close();
                    conn.close();
                    a=1;
                    flag=false;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static boolean isPSWcorrect(String username,String password) throws ClassNotFoundException, SQLException, InterruptedException {//检查用户名对应密码是否正确
        ThreadPSW tp=new ThreadPSW(username,password);
        tp.start();
        while(tp.a==null){
            Thread.sleep(100);
        }
        return tp.flag;
    }
    static class ThreadPSW extends Thread{
        Integer a;
        boolean flag;
        String username;
        String password;

        public ThreadPSW(String username,String password){
            this.password=password;
            this.username=username;
        }

        @Override
        public void run(){
            try {
                Class.forName("com.mysql.jdbc.Driver");

                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement stmt = conn.createStatement();
                String sql="select count(*) from user_info where user_name='"+username+"' and password='"+password+"' group by user_name;";
                ResultSet rs = stmt.executeQuery(sql);
                if(!rs.next()) {
                    stmt.close();
                    rs.close();
                    conn.close();
                    a=1;
                    flag=false;
                }
                else {
                    stmt.close();
                    rs.close();
                    conn.close();
                    a=1;
                    flag=true;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void newUserInsertion(String username,String password,String email) throws SQLException, ClassNotFoundException {//插入新用户信息
        new Thread(){
            @Override
            public void run(){
                try {
                    Class.forName("com.mysql.jdbc.Driver");

                    Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                    Statement stmt = conn.createStatement();
                    String sql="insert into user_info(user_name,user_email,password,is_enabled) values('"+username+"','"+email+"','"+password+"',1);";
                    stmt.execute(sql);
                    stmt.close();
                    conn.close();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return;
    }
    public static boolean isEmailAvailable(String email) throws ClassNotFoundException, SQLException, InterruptedException {//检查邮箱是否已被占用(邮箱格式已检查)
        ThreadEmail te=new ThreadEmail(email);
        te.start();
        while(te.a==null){
            Thread.sleep(100);
        }
        return te.flag;
    }

    static class ThreadEmail extends Thread{
        Integer a;
        boolean flag;
        String email;

        public ThreadEmail(String email){
            this.email=email;
        }
        @Override
        public void run(){
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement stmt = conn.createStatement();
                String sql="select count(*) from user_info where user_email='"+email+"' group by user_email;";
                ResultSet rs = stmt.executeQuery(sql);
                if(!rs.next()) {
                    stmt.close();
                    rs.close();
                    conn.close();
                    a=1;
                    flag =true;
                }
                else {
                    stmt.close();
                    rs.close();
                    conn.close();
                    a=1;
                    flag =false;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static String[] getLatestStock(String ts_code) throws InterruptedException{//根据股票代码(主键)获取最新股票信息
        ThreadStockLatest ts=new ThreadStockLatest(ts_code);
        ts.start();
        while(ts.a==null){
            Thread.sleep(100);
        }
        return ts.strs;
    }
    static class ThreadStockLatest extends Thread{
        Integer a;
        public String[] strs=new String[9];
        private String ts_code;
        public String cntn="{"+"\n"+"    \"getLatestStock\":["+"\n";

        public ThreadStockLatest(String ts_code){
            this.ts_code=ts_code;
        }

        @Override

        public void run(){
            try {
                int lineC=0;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Class.forName("com.mysql.jdbc.Driver");

                //Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/finance", USER, PASSWORD);
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                //System.out.println(conn);
                Statement stmt = conn.createStatement();
                String sql="select * from real_time_stock where ts_code='"+this.ts_code+"' order by trade_date DESC;";
                ResultSet rs = stmt.executeQuery(sql);
                if(rs.next()) {
                    lineC++;
                    if(lineC!=1){
                        this.cntn = this.cntn+",\n";
                    }
                    String trade_date = sdf.format(rs.getDate("trade_date"));
                    //System.out.println(trade_date);
                    this.cntn = this.cntn+"[\""+trade_date+"\","+rs.getFloat("close")+","+rs.getFloat("open")+","+rs.getFloat("high")+","+rs.getFloat("low")+","+rs.getFloat("pre_close")+","+rs.getFloat("change_")+","+rs.getFloat("pct_chg")+","+rs.getFloat("vol")+","+rs.getFloat("amount")+"]";
                    this.strs[0]=trade_date;
                    this.strs[1]=""+rs.getFloat("close");
                    this.strs[2]=""+rs.getFloat("open");
                    this.strs[3]=""+rs.getFloat("high");
                    this.strs[4]=""+rs.getFloat("low");
                    this.strs[5]=""+rs.getFloat("change_");
                    this.strs[6]=""+rs.getFloat("pct_chg");
                    this.strs[7]=""+rs.getFloat("vol");
                    this.strs[8]=""+rs.getFloat("amount");
                }
                this.cntn = this.cntn+"\n    ]\n}";
                //createJsonFile(this.cntn,"D:/Game/projects/login/app/src/main/assets","getLatestStock");
                stmt.close();
                rs.close();
                conn.close();
                a=1;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {//test
        //getLatestStock("000012.SZ");
        getDailyStock("000012.SZ");
        return;
    }
    public static void getDailyStock(String ts_code) throws InterruptedException {//获取单支股票信息(根据股票代码)(不经常调用)
        ThreadStockDaily ts=new ThreadStockDaily(ts_code);
        ts.start();
        while(ts.a==null){
            Thread.sleep(100);
        }
        return;
    }
    static class ThreadStockDaily extends Thread{
        Integer a;
        private String ts_code;
        public String cntn="{"+"\n"+"    \"getDailyStock\":["+"\n";

        public ThreadStockDaily(String ts_code){
            this.ts_code=ts_code;
        }

        @Override

        public void run(){
            try {
                int lineC=0;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Class.forName("com.mysql.jdbc.Driver");

                //Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/finance", USER, PASSWORD);
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                //System.out.println(conn);
                Statement stmt = conn.createStatement();
                String sql="select trade_date,open,close,low,high from real_time_stock where ts_code='"+this.ts_code+"' order by trade_date DESC limit 365;";
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()) {
                    lineC++;
                    if(lineC!=1){
                        this.cntn = this.cntn+",\n";
                    }
                    String trade_date = sdf.format(rs.getDate("trade_date"));
                    //System.out.println(trade_date);
                    this.cntn = this.cntn+"[\""+trade_date+"\","+rs.getFloat("open")+","+rs.getFloat("close")+","+rs.getFloat("low")+","+rs.getFloat("high")+"]";
                }
                    this.cntn = this.cntn+"\n    ]\n}";
                    createJsonFile(this.cntn,"D:/Game/projects/login/app/src/main/assets","getDailyStock");
                    stmt.close();
                    rs.close();
                    conn.close();
                    a=1;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
