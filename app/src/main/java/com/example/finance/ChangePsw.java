package com.example.finance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ChangePsw extends AppCompatActivity {

    private EditText et_changPwd;
    private EditText et_pwdVerify;
    private TextView tv_back;
    private Button btn_submit;
    private TextView tv_pwdFeedback;
    private TextView tv_pwdVerifyFeedback;
    private Toolbar toolbar;
    private TextView tv_backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_psw);
        initViews();
        setListeners();
    }
    private void initViews() {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        tv_back = findViewById(R.id.back);
        tv_back.setTypeface(fontAwe);
        btn_submit = (Button) findViewById(R.id.finish_button);//提交注册
        et_changPwd = (EditText) findViewById(R.id.change_password);
        et_pwdVerify = (EditText) findViewById(R.id.change_confirmPSW);
        tv_pwdFeedback = findViewById(R.id.change_password_feedback);
        tv_pwdVerifyFeedback = findViewById(R.id.change_confirmPSW_feedback);
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
        et_changPwd.addTextChangedListener(new TextWatcher() {

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
                String txtPSWS = et_changPwd.getText().toString();

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

                String txtPSWS = et_changPwd.getText().toString();
                String txtConfirmPSWS = et_pwdVerify.getText().toString();

                if(!txtPSWS.equals(txtConfirmPSWS)){
                    tv_pwdVerifyFeedback.setText("两次输入密码不一致!");
                } else {
                    tv_pwdVerifyFeedback.setText("");
                }
            }
        });


        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtPSWS = et_changPwd.getText().toString();
                String txtConfirmPSWS = et_pwdVerify.getText().toString();

                boolean isPSWLengthValid = true;
                boolean isPSWTypesEnough = false;
                boolean PSWlowerCase = false;
                boolean PSWupperCase = false;
                boolean PSWnumber = false;
                boolean isPSWConsistent = false;
                boolean isPSWValid = false;

                boolean isPSWConfirmValid = true;

                if(txtPSWS.length()<6){
                    isPSWLengthValid=false;
                    tv_pwdFeedback.setText("密码长度不得小于6");
                }
                else{
                    char preCh = 0;
                    int consi = 0;
                    for(int i=0;i<txtPSWS.length();i++){
                        if(consi>=3){
                            isPSWConsistent=true;
                            tv_pwdFeedback.setText("密码不得有连续三个相同字符");
                            break;
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
                            }
                        }
                    }
                }
                isPSWValid=isPSWLengthValid&&isPSWTypesEnough&&(!isPSWConsistent);
                if(!txtPSWS.equals(txtConfirmPSWS)){
                    tv_pwdVerifyFeedback.setText("两次输入密码不一致!");
                }
                else {
                    SharedPreferences settings = getSharedPreferences("UserInfo", 0);

                    SharedPreferences.Editor editor = settings.edit();

                    editor.clear();
                    editor.apply();
                    //System.out.println("changePSW success");
                    Intent intent = new Intent();
                    //前一个（MainActivity.this）是目前页面，后面一个是要跳转的下一个页面
                    intent.setClass(ChangePsw.this, LoginPage.class);
                    startActivity(intent);
                }
            }
        });
    }
}
