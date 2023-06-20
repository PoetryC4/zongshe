package com.example.finance;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.UserApi;
import com.example.finance.common.Colors;

import java.io.IOException;
import java.util.Map;

public class AboutUs extends AppCompatActivity {
/*
    private TextView tv_back;*/
    private UserApi userApi;
    private Colors colors;

    private Toolbar toolbar;
    private TextView tv_backBtn;

    private String userId = "";
    private Map<String,Object> userSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if(!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if(res.getCode()==0) {
                    Toast.makeText(AboutUs.this, res.getMsg(), Toast.LENGTH_LONG).show();
                } else {
                    userSettings = (Map<String, Object>) res.getData();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        initView();
        setListeners();
    }
    private void initView() {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");/*
        tv_back = findViewById(R.id.back);
        tv_back.setTypeface(fontAwe);*/

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        if(userSettings != null) changeMode((int) userSettings.get("isDark") == 1);
    }
    private void changeMode(boolean isDark) {
        
        if(isDark) {
            findViewById(R.id.aboutUs_body).setBackgroundColor(colors.colorSuperGray);
            findViewById(R.id.aboutUs_line).setBackgroundColor(colors.colorWhite);
            ((TextView) findViewById(R.id.aboutUs_title)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.aboutUs_content)).setTextColor(colors.colorWhite);
        } else {
            findViewById(R.id.aboutUs_body).setBackgroundColor(colors.colorWhite);
            findViewById(R.id.aboutUs_line).setBackgroundColor(colors.colorGray);
            ((TextView) findViewById(R.id.aboutUs_title)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.aboutUs_content)).setTextColor(colors.colorGray);
        }
    }

    private void setListeners() {
        tv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });/*
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
    }
}
