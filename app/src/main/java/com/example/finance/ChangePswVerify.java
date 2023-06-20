package com.example.finance;

import static com.example.finance.utils.BasicFunctions.send_code_func;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.UserApi;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ChangePswVerify extends AppCompatActivity {

    private UserApi userApi;

    private int email_verify_code=-1;
    private boolean isEmailValid=false;

    private TextView tv_sendCode;//发送验证码
    private TextView tv_back;
    private Button btn_continue;
    private EditText et_email;
    private EditText et_code;
    private TextView tv_emailFeedback;
    private TextView tv_codeFeedback;
    private Toolbar toolbar;
    private TextView tv_backBtn;
    @SuppressLint("HandlerLeak")
    private Handler mhandler = new Handler(){
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
        setContentView(R.layout.change_psw_verify);
        initViews();
        setListeners();
    }
    private void initViews() {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        tv_back = findViewById(R.id.back);
        tv_back.setTypeface(fontAwe);
        tv_sendCode = findViewById(R.id.verify_send_code);
        btn_continue = (Button) findViewById(R.id.continue_button);
        et_email = (EditText) findViewById(R.id.verify_email);
        et_code = (EditText) findViewById(R.id.verify_code);
        tv_emailFeedback = findViewById(R.id.verify_email_feedback);
        tv_codeFeedback = findViewById(R.id.verify_code_feedback);

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    private void setListeners() {

        tv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                    if(res.getCode() == 1) {
                        tv_emailFeedback.setText("该邮箱不存在");
                    }
                    if(res.getCode() == 0) {

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
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtEmailS = et_email.getText().toString();
                String txtCodeS = "";
                System.out.println(txtEmailS);

                if(!isEmailValid){
                    tv_emailFeedback.setText("该邮箱无效");
                }
                else{
                    if(email_verify_code!=-1&&Integer.parseInt(et_code.getText().toString())==email_verify_code){
                        //监听按钮，如果点击，就跳转
                        Intent intent = new Intent();
                        //前一个（MainActivity.this）是目前页面，后面一个是要跳转的下一个页面
                        intent.setClass(ChangePswVerify.this, ChangePsw.class);
                        startActivity(intent);
                    }
                    else{
                        tv_codeFeedback.setText("验证码不正确");
                    }
                }
            }
        });
    }
}
