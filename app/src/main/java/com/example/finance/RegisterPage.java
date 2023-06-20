package com.example.finance;

import static com.example.finance.utils.BasicFunctions.send_code_func;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.UserApi;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RegisterPage extends AppCompatActivity {

    private UserApi userApi;

    private int email_verify_code=-1;
    private boolean isEmailValid=false;

    private TextView tv_sendCode;//发送验证码
    private EditText et_username;
    private EditText et_email;
    private EditText et_code;
    private TextView tv_usernameFeedback;
    private TextView tv_pwdFeedback;
    private TextView tv_pwdVerifyFeedback;
    private TextView tv_emailFeedback;
    private TextView tv_codeFeedback;
    private EditText et_pwd;
    private EditText et_pwdVerify;
    private TextView tv_login;
    private Button btn_register;//提交注册
    private Toolbar toolbar;
    private TextView tv_backBtn;

    @SuppressLint("HandlerLeak")
    private  Handler mhandler = new Handler(){
        public void handleMessage(Message message){
            if(message.what>0){
                tv_sendCode.setText("有效时间"+message.what+"秒");
            }
            else{
                tv_sendCode.setClickable(true);
                tv_sendCode.setText("发送验证码");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //这个是获取布局文件的，这里是你下一个页面的布局文件//注意这个是跳转界面的不能设置错，应该是第一个
        setContentView(R.layout.register_page);
        initViews();
        setListeners();
    }
    private void initViews() {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        tv_sendCode = findViewById(R.id.register_send_code);
        et_username = (EditText) findViewById(R.id.register_username);
        et_email = (EditText) findViewById(R.id.register_email);
        et_code = (EditText) findViewById(R.id.register_code);
        tv_usernameFeedback = findViewById(R.id.register_username_feedback);
        tv_pwdFeedback = findViewById(R.id.register_password_feedback);
        tv_pwdVerifyFeedback = findViewById(R.id.register_confirmPSW_feedback);
        tv_emailFeedback = findViewById(R.id.register_email_feedback);
        tv_codeFeedback = findViewById(R.id.register_code_feedback);
        et_pwd = (EditText) findViewById(R.id.register_password);
        et_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //psw1.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_pwdVerify = (EditText) findViewById(R.id.register_confirmPSW);
        et_pwdVerify.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //psw2.setTransformationMethod(PasswordTransformationMethod.getInstance());

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        tv_login = findViewById(R.id.go_to_login);//转到登录
        btn_register = (Button) findViewById(R.id.register_button);//提交注册
    }
    private void setListeners() {

        tv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        et_pwd.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 输入的内容变化的监听
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // 输入前的监听

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 输入后的监听
                String txtPSWS = et_pwd.getText().toString();

                boolean isPSWTypesEnough = false;
                boolean PSWlowerCase = false;
                boolean PSWupperCase = false;
                boolean PSWnumber = false;

                if(txtPSWS.length()<6){
                    tv_pwdFeedback.setText("密码长度不得小于6");
                }
                else{
                    tv_pwdFeedback.setText("");
                    char preCh = 0;
                    int consi = 0;
                    for(int i=0;i<txtPSWS.length();i++){
                        if(consi>=3){
                            tv_pwdFeedback.setText("密码不得有连续三个相同字符");
                            break;
                        } else {
                            tv_pwdFeedback.setText("");
                        }
                        if(txtPSWS.charAt(i)!=preCh){
                            preCh=txtPSWS.charAt(i);
                            consi=1;
                        }
                        else{
                            consi++;
                        }
                        if(txtPSWS.charAt(i)<='9'&&txtPSWS.charAt(i)>='0') PSWnumber=true;
                        if(txtPSWS.charAt(i)<='z'&&txtPSWS.charAt(i)>='a') PSWlowerCase=true;
                        if(txtPSWS.charAt(i)<='Z'&&txtPSWS.charAt(i)>='A') PSWupperCase=true;
                        if(i==txtPSWS.length()-1){
                            isPSWTypesEnough=PSWlowerCase&&PSWupperCase&&PSWnumber;
                            if(!isPSWTypesEnough){
                                tv_pwdFeedback.setText("密码至少要有一个大写字母，一个小写字母，一个数字");
                            } else {
                                tv_pwdFeedback.setText("");
                            }
                        }
                        else {
                            tv_pwdFeedback.setText("");
                        }
                    }
                }

            }
        });

        et_pwdVerify.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 输入的内容变化的监听
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // 输入前的监听

            }

            @Override
            public void afterTextChanged(Editable s) {

                String txtPSWS = et_pwd.getText().toString();
                String txtConfirmPSWS = et_pwdVerify.getText().toString();

                if(!txtPSWS.equals(txtConfirmPSWS)){
                    tv_pwdVerifyFeedback.setText("两次输入密码不一致!");
                } else {
                    tv_pwdVerifyFeedback.setText("");
                }
            }
        });

        et_email.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 输入的内容变化的监听
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // 输入前的监听

            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailTxt = et_email.getText().toString();
                if (!emailTxt.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")) {
                    tv_emailFeedback.setText("不正确的邮箱格式");
                }
                else {
                    tv_emailFeedback.setText("");
                    if(!emailTxt.equals("")) {
                        // 输入后的监听
                        com.example.finance.common.R<String> res = null;
                        try {
                            res = userApi.CheckEmail(emailTxt);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        tv_emailFeedback.setText(res.getMsg());
                    } else {
                        tv_emailFeedback.setText("");
                    }
                }
            }
        });

        et_username.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 输入的内容变化的监听
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // 输入前的监听

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 输入后的监听

                String txtUsrNameS = et_username.getText().toString();

                for(int i=0;i<txtUsrNameS.length();i++){
                    if(!(txtUsrNameS.charAt(i)=='_'||(txtUsrNameS.charAt(i)<='Z'&&txtUsrNameS.charAt(i)>='A')||(txtUsrNameS.charAt(i)<='z'&&txtUsrNameS.charAt(i)>='a')||(txtUsrNameS.charAt(i)<='9'&&txtUsrNameS.charAt(i)>='0'))){
                        tv_usernameFeedback.setText("用户名只能由数字，大小写字母和下划线组成");
                        return;
                    }
                }
                tv_usernameFeedback.setText("");

                if(!txtUsrNameS.equals("")) {
                    com.example.finance.common.R<String> res = null;
                    try {
                        res = userApi.CheckUsername(txtUsrNameS);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    tv_usernameFeedback.setText(res.getMsg());
                } else {
                    tv_usernameFeedback.setText("");
                }

            }
        });
        //进行监听
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //监听按钮，如果点击，就跳转
                Intent intent = new Intent();
                //前一个（MainActivity.this）是目前页面，后面一个是要跳转的下一个页面
                intent.setClass(RegisterPage.this, LoginPage.class);
                startActivity(intent);
            }
        });


        tv_sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailTxt = et_email.getText().toString();
                if(!emailTxt.equals("")) {
                    // 输入后的监听
                    com.example.finance.common.R<String> res = null;
                    try {
                        res = userApi.CheckEmail(emailTxt);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    tv_emailFeedback.setText(res.getMsg());
                    if(res.getCode() == 1) {

                        String txtEmailS = et_email.getText().toString();

                        email_verify_code=(int)Math.floor(Math.random()*90000+10000);
                        System.out.println(email_verify_code);
                        int rs=send_code_func(txtEmailS,email_verify_code);
                        if(rs==-1){
                            tv_emailFeedback.setText("无效邮箱地址");
                        }
                        else {
                            tv_emailFeedback.setText("");
                            tv_sendCode.setClickable(false);
                            isEmailValid=true;

                            Timer timer=new Timer();
                            timer.schedule(new TimerTask() {
                                public int sec=60;
                                @Override
                                public void run() {
                                    Message message = new Message();
                                    message.what = sec;
                                    mhandler.sendMessage(message);
                                    if(sec==0){
                                        email_verify_code=-1;
                                        timer.cancel();
                                    }
                                    sec--;
                                }
                            },1000,1000);

                        }
                    }
                }

            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txtUsrNameS = et_username.getText().toString();
                String txtPSWS = et_pwd.getText().toString();
                String txtConfirmPSWS = et_pwdVerify.getText().toString();
                String txtEmailS = et_email.getText().toString();
                if(txtConfirmPSWS.equals("")||txtPSWS.equals("")||txtEmailS.equals("")||txtUsrNameS.equals("")) return;

                boolean isPSWTypesEnough = false;
                boolean PSWlowerCase = false;
                boolean PSWupperCase = false;
                boolean PSWnumber = false;

                for(int i=0;i<txtUsrNameS.length();i++){
                    if(!(txtUsrNameS.charAt(i)=='_'||(txtUsrNameS.charAt(i)<='Z'&&txtUsrNameS.charAt(i)>='A')||(txtUsrNameS.charAt(i)<='z'&&txtUsrNameS.charAt(i)>='a')||(txtUsrNameS.charAt(i)<='9'&&txtUsrNameS.charAt(i)>='0'))){
                        tv_usernameFeedback.setText("用户名只能由数字，大小写字母和下划线组成");
                        break;
                    }
                }
                tv_usernameFeedback.setText("");
                if(txtPSWS.length()<6){
                    tv_pwdFeedback.setText("密码长度不得小于6");
                }
                else{
                    tv_pwdFeedback.setText("");
                    char preCh = 0;
                    int consi = 0;
                    for(int i=0;i<txtPSWS.length();i++){
                        if(consi>=3){
                            tv_pwdFeedback.setText("密码不得有连续三个相同字符");
                            break;
                        } else {
                            tv_pwdFeedback.setText("");
                        }
                        if(txtPSWS.charAt(i)!=preCh){
                            preCh=txtPSWS.charAt(i);
                            consi=1;
                        }
                        else{
                            consi++;
                        }
                        if(txtPSWS.charAt(i)<='9'&&txtPSWS.charAt(i)>='0') PSWnumber=true;
                        if(txtPSWS.charAt(i)<='z'&&txtPSWS.charAt(i)>='a') PSWlowerCase=true;
                        if(txtPSWS.charAt(i)<='Z'&&txtPSWS.charAt(i)>='A') PSWupperCase=true;
                        if(i==txtPSWS.length()-1){
                            isPSWTypesEnough=PSWlowerCase&&PSWupperCase&&PSWnumber;
                            if(!isPSWTypesEnough){
                                tv_pwdFeedback.setText("密码至少要有一个大写字母，一个小写字母，一个数字");
                            } else {
                                tv_pwdFeedback.setText("");
                            }
                        }
                    }
                }

                if(!txtPSWS.equals(txtConfirmPSWS)){
                    tv_pwdVerifyFeedback.setText("两次输入密码不一致!");
                } else {
                    tv_pwdVerifyFeedback.setText("");
                }
                try {
                    isEmailValid = userApi.CheckEmail(txtEmailS).getCode()==1;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!isEmailValid){
                    tv_emailFeedback.setText("该邮箱已被占用");
                }
                else{
                    tv_emailFeedback.setText("");
                    tv_codeFeedback.setText("");
                    if(email_verify_code!=-1&&!et_code.getText().toString().equals("")&&Integer.parseInt(et_code.getText().toString())==email_verify_code){
                        try {
                            com.example.finance.common.R<String> res = null;
                            res = userApi.UserRegister(txtUsrNameS,txtPSWS,txtEmailS);
                            if (res.getCode() == 0) {
                                Toast.makeText(RegisterPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                                return;
                            }
                            Toast.makeText(getApplicationContext(), res.getData(), Toast.LENGTH_LONG).show();
                            finish();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        tv_codeFeedback.setText("验证码不正确");
                    }
                }
            }
        });
    }
}