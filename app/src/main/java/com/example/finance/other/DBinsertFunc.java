package com.example.finance.other;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.Date;

public class DBinsertFunc {
    public static final String URL = "jdbc:mysql://10.0.2.2:3306/finance";
    public static final String USER = "root";
    public static final String PASSWORD = "123456";
    private static void dataInsertion(String path,String sql,Connection conn,String types) throws IOException, ParseException {
        //String sql = "INSERT INTO real_time_stock values(?,?,?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ptmt = conn.prepareStatement(sql);
            int pl=0,pr=0,lineC=1,count=1;
            try (Scanner sc = new Scanner(new FileReader(path))) {
                while (sc.hasNextLine()) {  //按行读取字符串
                    String line = sc.nextLine();
                    //System.out.println(line);
                    if(lineC==1) {
                        lineC++;
                        continue;
                    }
                    pl=0;
                    pr=0;
                    count=1;
                    while(pl<line.length()) {
                        if(pr==line.length()||line.charAt(pr)==',') {
                            switch(types.charAt(count-1)) {
                                case 'n':{
                                    if(pl==pr) {
                                        ptmt.setNull(count,Types.INTEGER);
                                    }
                                    else {
                                        ptmt.setInt(count,Integer.valueOf(line.substring(pl, pr)));
                                    }
                                    break;
                                }
                                case 'v':{
                                    if(pl==pr) {
                                        ptmt.setNull(count,Types.VARCHAR);
                                    }
                                    else {
                                        ptmt.setString(count,line.substring(pl, pr));
                                    }
                                    break;
                                }
                                case 'd':{
                                    if(pl==pr) {
                                        ptmt.setNull(count,Types.DATE);
                                    }
                                    else {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                                        Date date = sdf.parse(line.substring(pl, pr));
                                        long lg = date.getTime(); //日期转时间戳
                                        ptmt.setDate(count,new java.sql.Date(lg) );
                                    }
                                    break;
                                }
                                case 'f':{
                                    if(pl==pr) {
                                        ptmt.setNull(count,Types.FLOAT);
                                    }
                                    else {
                                        ptmt.setFloat(count,Float.valueOf(line.substring(pl, pr)));
                                    }
                                    break;
                                }
                                case 'i':{
                                    if(pl==pr) {
                                        ptmt.setNull(count,Types.INTEGER);
                                    }
                                    else {
                                        ptmt.setInt(count,Integer.parseInt(line.substring(pl, pr)));
                                    }
                                    break;
                                }
                                default:{
                                    break;
                                }
                            }
                            count++;
                            pr++;
                            pl=pr;
                        }
                        else pr++;
                    }
                    ptmt.execute();
                    lineC++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static boolean realTimeStockIns(){
        new Thread(){
            @Override
            public void run(){
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");

                    Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                    Statement stmt = conn.createStatement();
                    dataInsertion("./statistics/stock_daily/000012.SZ_daily_info.csv","INSERT INTO real_time_stock values(?,?,?,?,?,?,?,?,?,?,?)",conn,"vdfffffffff");
                    stmt.close();
                    conn.close();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return true;
    }
}
