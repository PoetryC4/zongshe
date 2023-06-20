package com.example.finance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.UserApi;
import com.example.finance.common.Colors;

import java.io.IOException;
import java.util.Map;

public class UserSetting extends AppCompatActivity {

    private Colors colors;
    private UserApi userApi;
    /*
        private TextView tv_back;*/
    private TextView tv_change_PSW;
    private TextView tv_about_us;
    private TextView tv_log_out;
    private Switch swt_nightMode;
    private Switch swt_newbieMode;
    private LinearLayout ll_aboutUsPart;
    private LinearLayout ll_changePSWPart;
    private Toolbar toolbar;
    private TextView tv_backBtn;

    private String userId = "";
    private Map<String,Object> userSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting);
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if(!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if(res.getCode()==0) {
                    Toast.makeText(UserSetting.this, res.getMsg(), Toast.LENGTH_LONG).show();
                } else {
                    userSettings = (Map<String, Object>) res.getData();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        initViews();
        setListeners();
    }

    private void changeMode(boolean isDark) {

        if(isDark) {
            ((TextView) findViewById(R.id.userSetting_title)).setTextColor(colors.colorWhite);
            findViewById(R.id.user_setting_body).setBackgroundColor(colors.colorSuperGray);
            findViewById(R.id.userSetting_lineTitle).setBackgroundColor(colors.colorWhite);
            ((TextView) findViewById(R.id.userSetting_lineAppearance)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.appearance_switch_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.operation_switch_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.userSetting_lineOperation)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.about_us_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.about_us)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.userSetting_lineUs)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.change_PSW_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.change_PSW)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.userSetting_PWD)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.log_out_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.userSetting_lineLogout)).setTextColor(colors.colorWhite);
        } else {
            ((TextView) findViewById(R.id.userSetting_title)).setTextColor(colors.colorGray);
            findViewById(R.id.user_setting_body).setBackgroundColor(colors.colorWhite);
            findViewById(R.id.userSetting_lineTitle).setBackgroundColor(colors.colorGray);
            ((TextView) findViewById(R.id.userSetting_lineAppearance)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.appearance_switch_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.operation_switch_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.userSetting_lineOperation)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.about_us_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.about_us)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.userSetting_lineUs)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.change_PSW_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.change_PSW)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.userSetting_PWD)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.log_out_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.userSetting_lineLogout)).setTextColor(colors.colorGray);
        }
    }


    private void initViews() {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");/*
        tv_back = findViewById(R.id.back);
        tv_back.setTypeface(fontAwe);*/
        tv_log_out = findViewById(R.id.log_out_text);
        tv_change_PSW = findViewById(R.id.change_PSW);
        tv_about_us = findViewById(R.id.about_us);
        swt_nightMode = (Switch) findViewById(R.id.appearance_switch);
        swt_newbieMode = (Switch) findViewById(R.id.operation_switch);
        tv_about_us.setTypeface(fontAwe);
        tv_change_PSW.setTypeface(fontAwe);

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        ll_aboutUsPart = (LinearLayout) findViewById(R.id.about_us_part);
        ll_changePSWPart = (LinearLayout) findViewById(R.id.change_PSW_part);

        if(userSettings != null) {
            changeMode((int) userSettings.get("isDark") == 1);
            swt_newbieMode.setChecked((int) userSettings.get("isNew") == 1);
            swt_nightMode.setChecked((int) userSettings.get("isDark") == 1);
        }
    }

    private void setListeners() {
        swt_nightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                try {
                    userApi.UpdateSettingDark(userId, isChecked?1:0);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!userId.isEmpty()) {
                    try {
                        userSettings = (Map<String, Object>) userApi.GetSettingsById(userId).getData();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    changeMode((int) userSettings.get("isDark") == 1);
                }
            }
        });
        swt_newbieMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                try {
                    userApi.UpdateSettingNew(userId, isChecked?1:0);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        tv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getSharedPreferences("UserInfo", 0);

                SharedPreferences.Editor editor = settings.edit();

                editor.clear();
                editor.apply();

                Intent intent = new Intent();
                intent.setClass(UserSetting.this, StockMainPage.class);
                startActivity(intent);
            }
        });
/*
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        ll_changePSWPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserSetting.this, ChangePwdVerify.class);
                startActivity(intent);
            }
        });
        ll_aboutUsPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(UserSetting.this, AboutUs.class);
                startActivity(intent);
            }
        });/*
        swt_nightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try {
                    userApi.UserSettingChange(userId, "nightMode", b ? "day" : "night");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                swt_nightMode.setSwitchTextAppearance(UserSetting.this, b ? R.style.SwitchTextAppearanceWhite : R.style.SwitchTextAppearanceBlack);
            }
        });
        swt_newbieMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try {
                    userApi.UserSettingChange(userId, "newbieMode", b ? "pro" : "new");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                swt_newbieMode.setSwitchTextAppearance(UserSetting.this, b ? R.style.SwitchTextAppearanceWhite : R.style.SwitchTextAppearanceBlack);
            }
        });*/
    }
}
