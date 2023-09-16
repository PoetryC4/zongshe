package com.example.finance.utils;

import com.sun.mail.util.MailSSLSocketFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;


public class BasicFunctions {

    public static StringBuffer formatNum(String num, Boolean b) {
        StringBuffer sb = new StringBuffer();
        BigDecimal b0 = new BigDecimal("100");
        BigDecimal b1 = new BigDecimal("10000");
        BigDecimal b2 = new BigDecimal("100000000");
        BigDecimal b3 = new BigDecimal(num);

        String formatNumStr = "";
        String unit = "";

        // 以百为单位处理
        if (b) {
            if (b3.compareTo(b0) == 0 || b3.compareTo(b0) == 1) {
                return sb.append("99+");
            }
            return sb.append(num);
        }

        // 以万为单位处理
        if (b3.compareTo(b1) == -1) {
            formatNumStr = b3.toString();
        } else if ((b3.compareTo(b1) == 0 && b3.compareTo(b1) == 1)
                || b3.compareTo(b2) == -1) {
            unit = "万";

            formatNumStr = b3.divide(b1).toString();
        } else if (b3.compareTo(b2) == 0 || b3.compareTo(b2) == 1) {
            unit = "亿";
            formatNumStr = b3.divide(b2).toString();

        }
        if (!"".equals(formatNumStr)) {
            int i = formatNumStr.indexOf(".");
            if (i == -1) {
                sb.append(formatNumStr).append(unit);
            } else {
                i = i + 1;
                String v = formatNumStr.substring(i, i + 1);
                if (!v.equals("0")) {
                    sb.append(formatNumStr.substring(0, i + 1)).append(unit);
                } else {
                    sb.append(formatNumStr.substring(0, i - 1)).append(unit);
                }
            }
        }
        if (sb.length() == 0)
            return sb.append("0");
        return sb;
    }

    public static boolean createJsonFile(String jsonString, String filePath, String fileName) {
        // 标记文件生成是否成功
        //System.out.println(jsonString);
        boolean flag = true;

        // 拼接文件完整路径
        String fullPath = filePath + File.separator + fileName + ".json";

        // 生成json格式文件
        try {
            // 保证创建一个新文件
            File file = new File(fullPath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            System.out.println(fullPath);
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();

            // 字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            //System.out.println(file.getCanonicalPath());
            write.write(jsonString);
            write.flush();
            write.close();
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }

        // 返回是否成功的标记
        return flag;
    }
    public static boolean isNumeric(final CharSequence cs) {
        // 判断是否为空，如果为空则返回false
        if (cs.length() == 0) {
            return false;
        }
        // 通过 length() 方法计算cs传入进来的字符串的长度，并将字符串长度存放到sz中
        final int sz = cs.length();
        // 通过字符串长度循环
        for (int i = 0; i < sz; i++) {
            // 判断每一个字符是否为数字，如果其中有一个字符不满足，则返回false
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        // 验证全部通过则返回true
        return true;
    }
    public static int send_code_func(String email,int code) {
        if (!email.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")) {
            return -1;
        }
        final int[] tmp = new int[1];
        tmp[0]=-2;
        new Thread(){
            @Override
            public void run(){
                // 收件人电子邮箱
                String to = email;

                // 发件人电子邮箱
                String from = "473240934@qq.com";
                String username = "473240934@qq.com";

                String host = "smtp.qq.com";

                String password = "rptztjtiqcsjbgjj";

                // 使用QQ邮箱时配置
                Properties prop = new Properties();
                prop.setProperty("mail.host", "smtp.qq.com");    // 设置QQ邮件服务器
                prop.setProperty("mail.transport.protocol", "smtp");      // 邮件发送协议
                prop.setProperty("mail.smtp.auth", "true");      // 需要验证用户名和密码
                // 关于QQ邮箱，还要设置SSL加密，其他邮箱不需要
                MailSSLSocketFactory sf = null;
                try {
                    sf = new MailSSLSocketFactory();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
                assert sf != null;
                sf.setTrustAllHosts(true);
                prop.put("mail.smtp.ssl.enable", "true");
                prop.put("mail.smtp.ssl.socketFactory", sf);

                // 创建定义整个邮件程序所需的环境信息的 Session 对象，QQ才有，其他邮箱就不用了
                Session session = Session.getDefaultInstance(prop, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        // 发件人邮箱用户名，授权码
                        return new PasswordAuthentication(username, password);
                    }
                });

                // 开启 Session 的 debug 模式，这样就可以查看程序发送 Email 的运行状态
                session.setDebug(true);

                // 通过 session 得到 transport 对象
                Transport ts = null;
                try {
                    ts = session.getTransport();
                    // 使用邮箱的用户名和授权码连上邮箱服务器
                    ts.connect(host, username, password);

                    // 创建邮件，写邮件
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(from)); // 指明邮件的发件人
                    message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));   // 指明邮件的收件人
                    message.setSubject("某App的邮箱验证邮件");     // 邮件主题
                    message.setContent("<p>验证码为:<b>"+code+"<b><p>", "text/html;charset=utf-8");    // 邮件内容

                    // 发送邮件
                    ts.sendMessage(message, message.getAllRecipients());
                    System.out.println("邮件发送成功");
                    // 释放资源
                    ts.close();
                    tmp[0]=1;
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                } catch (AddressException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

            }
        }.start();
        return tmp[0];
    }
}
