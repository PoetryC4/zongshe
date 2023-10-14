package com.example.finance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.TopicApi;
import com.example.finance.api.UserApi;
import com.example.finance.common.Colors;

import java.io.IOException;
import java.util.Map;

public class TopicPage extends AppCompatActivity {

    private TopicApi topicApi;
    private UserApi userApi;
    private Colors colors;
    private String topicID;
    /*
        private TextView tv_back;*/
    private TextView tv_title;
    private TextView tv_content;
    private TextView tv_channel;
    private TextView tv_date;
    private Toolbar toolbar;
    private TextView tv_backBtn;
    private String userId = "";
    private Map<String, Object> userSettings = null;

    private Map<String, Object> topicData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_page);
        Intent intent = getIntent();
        topicID = intent.getStringExtra("topicID");
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if (!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if (res.getCode() == 0) {
                    Toast.makeText(TopicPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
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

        if (isDark) {
            findViewById(R.id.topic_line).setBackgroundColor(colors.colorWhite);
            findViewById(R.id.topic_body).setBackgroundColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.title)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.content)).setTextColor(colors.colorWhite);
        } else {
            findViewById(R.id.topic_line).setBackgroundColor(colors.colorGray);
            findViewById(R.id.topic_body).setBackgroundColor(colors.colorWhite);
            ((TextView) findViewById(R.id.title)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.content)).setTextColor(colors.colorGray);
        }
    }

    private void initViews() {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");/*
        tv_back = findViewById(R.id.back);
        tv_back.setTypeface(fontAwe);*/

        tv_title = findViewById(R.id.title);
        tv_content = findViewById(R.id.content);
        tv_date = findViewById(R.id.topic_date);
        tv_channel = findViewById(R.id.channel);

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        if (userSettings != null) changeMode((int) userSettings.get("isDark") == 1);
        //setSupportActionBar(toolbar);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里定义线程执行的任务
                try {
                    com.example.finance.common.R<Object> res = null;
                    res = topicApi.GetTopicById(topicID);
                    if (res.getCode() == 0) {
                        Looper.prepare();
                        Toast.makeText(TopicPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                        Looper.loop();
                    } else {
                        topicData = (Map<String, Object>) res.getData();
                        System.out.println(topicData.toString());
                        tv_date.setText((CharSequence) topicData.get("datetime"));
                        tv_content.setText((CharSequence) topicData.get("content"));
                        tv_title.setText((CharSequence) topicData.get("title"));
                        tv_channel.setText((CharSequence) topicData.get("channals"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

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
