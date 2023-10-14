package com.example.finance;


import static com.example.finance.utils.MessageUtils.sendMessage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
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
import java.util.Map;
import java.util.Objects;

public class LoginPage extends AppCompatActivity {

    private Long sleepInterval = 1000 * 1800L;

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
    private Map<String, Object> userData;

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

                        String userId = null;
                        try {
                            userId = (userApi.GetIdByName(usrName)).getData().toString();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        editor.putString("UserId", userId);
                        editor.apply();

                        //开启预警监听

                        SharedPreferences settings1 = getSharedPreferences("AppInfo", 0);
                        String finalUserId = userId;
                        Thread warningListener = new Thread() {//创建子线程
                            @Override
                            public void run() {
                                SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                                if (finalUserId != null) {
                                    try {
                                        userData = (Map<String, Object>) UserApi.GetDataById(finalUserId).getData();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }


                                    String isAliveStr = settings1.getString("isAlive", "").toString();
                                    boolean isAppAlive = Objects.equals(isAliveStr, "1");
                                    while (isAppAlive) {
                                        try {
                                            // TODO 获取当日股票
                                            // TODO 获取用户监听的自选股并对比，若超过阈值，则发送短信
                                            // TODO 改成目标电话号码
                                            boolean send = sendMessage("13666279166", null, "test", null, null);

                                            Looper.prepare();
                                            if (send) {
                                                Toast.makeText(getApplicationContext(), "短信发送成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "短信发送失败", Toast.LENGTH_LONG).show();
                                            }
                                            Looper.loop();

                                            Thread.sleep(sleepInterval);
                                            isAliveStr = settings1.getString("isAlive", "").toString();
                                            isAppAlive = Objects.equals(isAliveStr, "1");

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        };
                        // 确保只有一个监听线程
                        String isWarningListenerRunningStr = settings1.getString("isWarningListenerRunning", "").toString();
                        if (!Objects.equals(isWarningListenerRunningStr, "1")) {
                            warningListener.start();

                            SharedPreferences.Editor editor1 = settings1.edit();

                            editor1.putString("isWarningListenerRunning", "1");
                            editor1.apply();
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