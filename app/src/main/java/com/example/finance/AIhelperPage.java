package com.example.finance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finance.api.UserApi;
import com.example.finance.common.Colors;

import java.io.IOException;
import java.util.Map;

public class AIhelperPage extends AppCompatActivity {

    private UserApi userApi;
    private Colors colors;
    private TextView tv_backBtn;
    private WebView webAI;

    private String userId = "";
    private Map<String, Object> userSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_helper);
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if (!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if (res.getCode() == 0) {
                    Toast.makeText(AIhelperPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                } else {
                    userSettings = (Map<String, Object>) res.getData();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Intent intent = getIntent();
        initViews();
        setListeners();

    }

    private void AIInit() {
        WebSettings settings = webAI.getSettings();
        webAI.setWebViewClient(new MyWebViewClient());

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        settings.setAllowUniversalAccessFromFileURLs(true);

        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setUseWideViewPort(true);
        settings.setAllowFileAccess(true);

        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        webAI.loadUrl("file:///android_asset/langchain/index.html");
    }

    private void initViews() {

        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        webAI = findViewById(R.id.AIhelperWeb);
        AIInit();

        /*tv_news = findViewById(R.id.news);
        tv_handicap = findViewById(R.id.handicap);
        tv_capital = findViewById(R.id.capital);
        tv_announcement = findViewById(R.id.announcement);
        tv_finance = findViewById(R.id.finance);
        tv_briefInfo = findViewById(R.id.briefIntro);*/
        if (userSettings != null) changeMode((int) userSettings.get("isDark") == 1);

    }

    private void changeMode(boolean isDark) {

        if (isDark) {
            findViewById(R.id.AIhelperBody).setBackgroundColor(colors.colorBlue);
        } else {
            findViewById(R.id.AIhelperBody).setBackgroundColor(colors.colorRed);
        }
    }

    private void setListeners() {
        tv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //在这里执行你想调用的js函数
            view.evaluateJavascript("javascript:getDataFromNative('" + userId + "')", new ValueCallback() {
                @Override
                public void onReceiveValue(Object o) {

                }

            });

        }
    }
}