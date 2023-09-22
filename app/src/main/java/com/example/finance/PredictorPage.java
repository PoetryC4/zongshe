package com.example.finance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

public class PredictorPage extends AppCompatActivity {

    private StockApi stockApi;

    private UserApi userApi;
    private WebView webKMap;
    private Colors colors;

    private EditText et_codeInput;
    private EditText et_daysInput;/*
    private TextView tv_back;*/
    private Toolbar toolbar;
    private TextView tv_backBtn;
    private Button btn_predict;

    private String ts_code = "";
    private String prd_dur = "";
    private String userId = "";
    private Map<String, Object> userSettings = null;

    private class WebViewClientKMap extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //在这里执行你想调用的js函数
            if (!ts_code.isEmpty())
                webKMap.loadUrl("javascript:setTsCode('" + ts_code + "')");/*
            if(!prd_dur.isEmpty())
            webKMap.loadUrl("javascript:setPrdDur('" + (int)Math.round(Float.parseFloat(prd_dur)) + "')");*/
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.predictor_page);
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if (!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if (res.getCode() == 0) {
                    Toast.makeText(PredictorPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
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
        if (intent.getStringExtra("ts_code") != null) {
            ts_code = intent.getStringExtra("ts_code");
            et_codeInput.setText(ts_code);
        }
        setListeners();
    }

    private void initViews() {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");/*
        tv_back = findViewById(R.id.back);
        tv_back.setTypeface(fontAwe);*/
        btn_predict = findViewById(R.id.predict_button);
        webKMap = (WebView) findViewById(R.id.KMap);
        et_codeInput = (EditText) findViewById(R.id.name_input);
        et_daysInput = (EditText) findViewById(R.id.days_input);

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        if (userSettings != null) changeMode((int) userSettings.get("isDark") == 1);
    }

    private void changeMode(boolean isDark) {

        if (isDark) {
            findViewById(R.id.predictor_body).setBackgroundColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.predictor_title)).setTextColor(colors.colorWhite);
            findViewById(R.id.predictor_line).setBackgroundColor(colors.colorWhite);
            findViewById(R.id.predictor_panel).setBackgroundResource(R.drawable.rounded_rect_1_gray);
            findViewById(R.id.name_input).setBackgroundResource(R.drawable.edittext_bg_white);
            ((TextView) findViewById(R.id.name_txt)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.days_txt)).setTextColor(colors.colorWhite);
            ((EditText) findViewById(R.id.days_input)).setTextColor(colors.colorWhite);
            ((EditText) findViewById(R.id.days_input)).setHintTextColor(colors.colorWhite);
            ((EditText) findViewById(R.id.name_input)).setTextColor(colors.colorWhite);
            ((EditText) findViewById(R.id.name_input)).setHintTextColor(colors.colorWhite);
        } else {
            findViewById(R.id.predictor_body).setBackgroundColor(colors.colorWhite);
            ((TextView) findViewById(R.id.predictor_title)).setTextColor(colors.colorGray);
            findViewById(R.id.predictor_line).setBackgroundColor(colors.colorGray);
            findViewById(R.id.predictor_panel).setBackgroundResource(R.drawable.rounded_rect_1_white);
            findViewById(R.id.name_input).setBackgroundResource(R.drawable.edittext_bg_gray);
            ((TextView) findViewById(R.id.name_txt)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.days_txt)).setTextColor(colors.colorGray);
            ((EditText) findViewById(R.id.days_input)).setTextColor(colors.colorGray);
            ((EditText) findViewById(R.id.days_input)).setHintTextColor(colors.colorGray);
            ((EditText) findViewById(R.id.name_input)).setTextColor(colors.colorGray);
            ((EditText) findViewById(R.id.name_input)).setHintTextColor(colors.colorGray);

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
        btn_predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ts_code = et_codeInput.getText().toString();
                        /*try {
                            com.example.finance.common.R<Object> res = null;
                            res = stockApi.GetCashflow(ts_code);
                            if(res.getCode()==0) {
                                Looper.prepare();
                                Toast.makeText(PredictorPage.this, "获取code对应股票错误", Toast.LENGTH_LONG).show();
                                Looper.loop();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                btn_predict.setClickable(false);
                KMapInit();
                btn_predict.setClickable(true);
            }
        });
    }

    private void KMapInit() {
        WebChromeClient webChromeClient = new WebChromeClient();
        webKMap.setWebChromeClient(webChromeClient);
        WebSettings settings = webKMap.getSettings();
        webKMap.setWebViewClient(new WebViewClientKMap());

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        settings.setAllowUniversalAccessFromFileURLs(true);

        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setUseWideViewPort(true);

        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        webKMap.loadUrl("file:///android_asset/prd.htm");
    }
}
