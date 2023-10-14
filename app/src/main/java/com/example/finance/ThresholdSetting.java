package com.example.finance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.StockApi;
import com.example.finance.api.UserApi;
import com.example.finance.common.Colors;

import java.io.IOException;
import java.util.Map;

public class ThresholdSetting extends AppCompatActivity {

    private Colors colors;

    private StockApi stockApi;
    private UserApi userApi;

    private TextView tv_tsCode;
    private EditText et_minLower;
    private EditText et_incLower;
    private EditText et_decLower;
    private TextView tv_backBtn;
    private Button btn_submit;

    private String userId;
    private String tsCode;
    private Toolbar toolbar;

    private Map<String, Object> userSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.threshold_setting);
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if (!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if (res.getCode() == 0) {
                    Toast.makeText(ThresholdSetting.this, res.getMsg(), Toast.LENGTH_LONG).show();
                } else {
                    userSettings = (Map<String, Object>) res.getData();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "请先登录", Toast.LENGTH_LONG).show();
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
        }
        // TODO 检测是否为自选股
        initViews();
        Intent intent = getIntent();
        if (intent.getStringExtra("ts_code") != null) {
            tsCode = intent.getStringExtra("ts_code");
            tv_tsCode.setText("股票\n" + tsCode);
        }
        setListeners();
    }

    private void changeMode(boolean isDark) {

        if (isDark) {
            findViewById(R.id.ThresholdSettingBody).setBackgroundColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.tsCodeTitle)).setTextColor(colors.colorRedInput);
            ((TextView) findViewById(R.id.minLowerBoundTxt)).setTextColor(colors.colorRedInput);
            ((TextView) findViewById(R.id.incLowerBoundTxt)).setTextColor(colors.colorRedInput);
            ((TextView) findViewById(R.id.decLowerBoundTxt)).setTextColor(colors.colorRedInput);
            ((EditText) findViewById(R.id.minLowerBound)).setBackgroundResource(R.drawable.edittext_bg_white);
            ((EditText) findViewById(R.id.incLowerBound)).setBackgroundResource(R.drawable.edittext_bg_white);
            ((EditText) findViewById(R.id.decLowerBound)).setBackgroundResource(R.drawable.edittext_bg_white);
            findViewById(R.id.submit_button).setBackgroundResource(R.drawable.rounded_rect_3_gray);

        } else {
            findViewById(R.id.ThresholdSettingBody).setBackgroundColor(colors.colorWhite);
            ((TextView) findViewById(R.id.tsCodeTitle)).setTextColor(colors.colorBlueInput);
            ((TextView) findViewById(R.id.minLowerBoundTxt)).setTextColor(colors.colorBlueInput);
            ((TextView) findViewById(R.id.incLowerBoundTxt)).setTextColor(colors.colorBlueInput);
            ((TextView) findViewById(R.id.decLowerBoundTxt)).setTextColor(colors.colorBlueInput);
            ((EditText) findViewById(R.id.minLowerBound)).setBackgroundResource(R.drawable.edittext_bg_gray);
            ((EditText) findViewById(R.id.incLowerBound)).setBackgroundResource(R.drawable.edittext_bg_gray);
            ((EditText) findViewById(R.id.decLowerBound)).setBackgroundResource(R.drawable.edittext_bg_gray);
            findViewById(R.id.submit_button).setBackgroundResource(R.drawable.rounded_rect_3_white);
        }
    }

    private void initViews() {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");

        tv_tsCode = findViewById(R.id.tsCodeTitle);
        et_minLower = findViewById(R.id.minLowerBound);
        et_incLower = findViewById(R.id.incLowerBound);
        et_decLower = findViewById(R.id.decLowerBound);
        btn_submit = findViewById(R.id.submit_button);

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        if (userSettings != null) changeMode((int) userSettings.get("isDark") == 1);
    }

    private void setListeners() {
        tv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 上传设置
            }
        });
    }
}