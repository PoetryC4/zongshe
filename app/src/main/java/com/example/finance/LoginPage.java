package com.example.finance;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.UserApi;

import java.io.IOException;

public class LoginPage extends AppCompatActivity {

    private UserApi userApi;

    private EditText et_pwd;
    private EditText et_username;
    private TextView tv_register;//转到注册
    private Button btn_login;//提交登录
    private TextView tv_usernameFeedback;
    private TextView tv_pwdFeedback;
    private TextView tv_forgetPwd;

    private Toolbar toolbar;
    private TextView tv_backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        initViews();
        setListeners();
    }

    private void initViews() {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        et_pwd = (EditText) findViewById(R.id.login_password);
        et_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //psw1.setTransformationMethod(PasswordTransformationMethod.getInstance());
        tv_register = findViewById(R.id.go_to_register);//转到注册
        btn_login = (Button) findViewById(R.id.login_button);//提交登录
        et_username = findViewById(R.id.login_username);
        tv_usernameFeedback = findViewById(R.id.login_username_feedback);
        tv_pwdFeedback = findViewById(R.id.login_password_feedback);
        tv_forgetPwd = findViewById(R.id.forget_password);

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
    }

    private void setListeners() {

        tv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //进行监听
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //监听按钮，如果点击，就跳转
                Intent intent = new Intent();
                //前一个（MainActivity.this）是目前页面，后面一个是要跳转的下一个页面
                intent.setClass(LoginPage.this, RegisterPage.class);
                startActivity(intent);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usrName = et_username.getEditableText().toString();
                String PSW = et_pwd.getEditableText().toString();

                if (!usrName.equals("") && !PSW.equals("")) {
                    com.example.finance.common.R<String> res = null;
                    try {
                        res = userApi.CheckLogin(usrName, PSW);
                        System.out.println(res);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (res.getCode() == 1) {
                        Toast.makeText(getApplicationContext(), res.getData(), Toast.LENGTH_LONG).show();
                        SharedPreferences settings = getSharedPreferences("UserInfo", MODE_PRIVATE);

                        SharedPreferences.Editor editor = settings.edit();

                        try {
                            editor.putString("UserId", (String) (userApi.GetIdByName(usrName)).getData());
                            editor.apply();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent();
                        intent.setClass(LoginPage.this, UserPageSimple.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), res.getMsg(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        tv_forgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginPage.this, ChangePwdVerify.class);
                startActivity(intent);
            }
        });
    }
}