package com.example.finance;

import static com.example.finance.utils.BasicFunctions.isNumeric;
import static com.example.finance.utils.MessageUtils.sendMessage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.UserApi;
import com.example.finance.common.Colors;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ChangePhone extends AppCompatActivity {

    private UserApi userApi;
    private Colors colors;

    private int phone_verify_code = -1;
    private boolean isEmailValid = false;

    private TextView tv_sendCode;//发送验证码
    /*private TextView tv_back;*/
    private Button btn_continue;
    private EditText et_phone;
    private EditText et_code;
    private TextView tv_phoneFeedback;
    private TextView tv_codeFeedback;
    private Toolbar toolbar;
    private TextView tv_backBtn;
    private String userId = "";
    private Map<String, Object> userSettings = null;
    @SuppressLint("HandlerLeak")
    private Handler mhandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what > 0) {
                tv_sendCode.setText("有效时间" + message.what + "秒");
            } else {
                tv_sendCode.setClickable(true);
                tv_sendCode.setText("发送验证码");
            }
        }
    };

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_phone_verify);
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if (!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if (res.getCode() == 0) {
                    Toast.makeText(ChangePhone.this, res.getMsg(), Toast.LENGTH_LONG).show();
                } else {
                    userSettings = (Map<String, Object>) res.getData();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            finish();
        }
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        initViews();
        setListeners();
    }

    private void initViews() {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");/*
        tv_back = findViewById(R.id.back);
        tv_back.setTypeface(fontAwe);*/
        tv_sendCode = findViewById(R.id.verify_send_code);
        btn_continue = (Button) findViewById(R.id.continue_button);
        et_phone = (EditText) findViewById(R.id.verify_phone);
        et_code = (EditText) findViewById(R.id.verify_code);
        tv_phoneFeedback = findViewById(R.id.verify_phone_feedback);
        tv_codeFeedback = findViewById(R.id.verify_code_feedback);

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        if (userSettings != null) changeMode((int) userSettings.get("isDark") == 1);
    }

    private void changeMode(boolean isDark) {

        if (isDark) {
            findViewById(R.id.changePhone_body).setBackgroundColor(colors.colorSuperGray);
            findViewById(R.id.changePhone_line).setBackgroundColor(colors.colorWhite);
            ((TextView) findViewById(R.id.phone_txt)).setTextColor(colors.colorRedInput);
            ((TextView) findViewById(R.id.code_txt)).setTextColor(colors.colorRedInput);
            ((TextView) findViewById(R.id.verify_send_code)).setTextColor(colors.colorRedInput);
            ((EditText) findViewById(R.id.verify_phone)).setTextColor(colors.colorWhite);
            ((EditText) findViewById(R.id.verify_code)).setTextColor(colors.colorWhite);
            ((Button) findViewById(R.id.continue_button)).setTextColor(colors.colorSuperGray);
        } else {
            findViewById(R.id.changePhone_body).setBackgroundColor(colors.colorWhite);
            findViewById(R.id.changePhone_line).setBackgroundColor(colors.colorGray);
            ((TextView) findViewById(R.id.phone_txt)).setTextColor(colors.colorBlueInput);
            ((TextView) findViewById(R.id.code_txt)).setTextColor(colors.colorBlueInput);
            ((TextView) findViewById(R.id.verify_send_code)).setTextColor(colors.colorBlueInput);
            ((EditText) findViewById(R.id.verify_phone)).setTextColor(colors.colorGray);
            ((EditText) findViewById(R.id.verify_code)).setTextColor(colors.colorGray);
            ((Button) findViewById(R.id.continue_button)).setTextColor(colors.colorWhite);
        }
    }

    private void setListeners() {

        tv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        et_phone.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 输入的内容变化的监听
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 输入前的监听

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneTxt = et_phone.getText().toString();
                if (isNumeric(phoneTxt)) {
                    tv_phoneFeedback.setText("不正确的手机号格式");
                } else {
                    tv_phoneFeedback.setText("");
                    if (!phoneTxt.equals("")) {
                        // 输入后的监听
                        com.example.finance.common.R<String> res = null;
                        try {
                            res = userApi.CheckEmail(phoneTxt);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        tv_phoneFeedback.setText(res.getMsg());
                    } else {
                        tv_phoneFeedback.setText("");
                    }
                }
            }
        });
/*
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/

        tv_sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        // 在这里定义线程执行的任务
                        String phoneTxt = et_phone.getText().toString();
                        if (!phoneTxt.equals("")) {
                            if (isNumeric(phoneTxt)) {
                                tv_phoneFeedback.setText("不正确的手机号格式");
                                return;
                            }

                            String txtMessage = et_phone.getText().toString();

                            phone_verify_code = (int) Math.floor(Math.random() * 90000 + 10000);
                            System.out.println(phone_verify_code);
                            boolean send = sendMessage(phoneTxt, null, String.valueOf(phone_verify_code), null, null);
                            Looper.prepare();
                            if (send) {
                                Toast.makeText(getApplicationContext(), "短信发送成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "短信发送失败，请重试", Toast.LENGTH_LONG).show();
                            }
                            Looper.loop();
                            if (!send) {
                                tv_phoneFeedback.setText("短信发送失败");
                            } else {

                                tv_phoneFeedback.setText("");
                                tv_sendCode.setClickable(false);

                                Timer timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    public int sec = 60;

                                    @Override
                                    public void run() {
                                        Message message = new Message();
                                        message.what = sec;
                                        mhandler.sendMessage(message);
                                        if (sec == 0) {
                                            phone_verify_code = -1;
                                            timer.cancel();
                                        }
                                        sec--;
                                    }
                                }, 1000, 1000);

                            }
                        }
                    }
                };

                // 启动线程
                thread.start();

            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneTxt = et_phone.getText().toString();
                if (phone_verify_code != -1 && Integer.parseInt(et_code.getText().toString()) == phone_verify_code) {
                    try {
                        com.example.finance.common.R<String> res = null;
                        res = userApi.UpdatePhone(email, phoneTxt);
                        if (res.getCode() == 0) {
                            Looper.prepare();
                            Toast.makeText(ChangePhone.this, res.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                            return;
                        } else {
                            Looper.prepare();
                            Toast.makeText(ChangePhone.this, "修改成功", Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finish();
                } else {
                    tv_codeFeedback.setText("验证码不正确");
                }
            }
        });
    }
}
