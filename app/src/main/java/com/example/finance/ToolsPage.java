package com.example.finance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.UserApi;
import com.example.finance.common.Colors;

import java.io.IOException;
import java.util.Map;

public class ToolsPage extends AppCompatActivity {

    private UserApi userApi;
    private Colors colors;

    private TextView tv_predictor;
    private TextView tv_notes;
    private TextView tv_user;
    private TextView tv_tools;
    private TextView tv_main;
    private TextView tv_warnings;
    private TextView tv_ai_helper;
    private TextView tv_charts;
    private TextView tv_searchIcon;
    private TextView tv_upperBar;
    private Toolbar toolbar;
    private TextView tv_backBtn;
    private String userId = "";

    private AbsoluteLayout al_predictor;
    private AbsoluteLayout al_warning;
    private AbsoluteLayout al_notes;
    private AbsoluteLayout al_ai_helper;
    private AbsoluteLayout al_charts;

    private Map<String,Object> userSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tools_page);
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if(!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if(res.getCode()==0) {
                    Toast.makeText(ToolsPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
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
            findViewById(R.id.tools_body).setBackgroundColor(colors.colorSuperGray);
            findViewById(R.id.tools_head).setBackgroundColor(colors.colorBlue);
            ((TextView) findViewById(R.id.predictor_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.warnings_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.notes_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.ai_helper_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.charts_text)).setTextColor(colors.colorWhite);

            ((TextView) findViewById(R.id.search_icon)).setTextColor(colors.colorGray);
            findViewById(R.id.upperBar).setBackgroundResource(R.drawable.rounded_rect_3_gray);
            ((TextView) findViewById(R.id.predictor)).setTextColor(colors.colorBlue);
            ((TextView) findViewById(R.id.warnings)).setTextColor(colors.colorBlue);
            ((TextView) findViewById(R.id.notes)).setTextColor(colors.colorBlue);
            ((TextView) findViewById(R.id.ai_helper)).setTextColor(colors.colorBlue);
            ((TextView) findViewById(R.id.charts)).setTextColor(colors.colorBlue);
            findViewById(R.id.tools_box).setBackgroundResource(R.drawable.rounded_rect_1_gray);

            findViewById(R.id.switchBar).setBackgroundColor(colors.colorBlue);
            ((TextView) findViewById(R.id.user_page_text)).setTextColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.user_page)).setTextColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.main_page_text)).setTextColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.main_page)).setTextColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.tool_page_text)).setTextColor(colors.colorDarkBlue);
            ((TextView) findViewById(R.id.tool_page)).setTextColor(colors.colorDarkBlue);
        } else {
            findViewById(R.id.tools_body).setBackgroundColor(colors.colorWhite);
            findViewById(R.id.tools_head).setBackgroundColor(colors.colorRed);
            ((TextView) findViewById(R.id.predictor_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.warnings_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.notes_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.ai_helper_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.charts_text)).setTextColor(colors.colorGray);

            ((TextView) findViewById(R.id.search_icon)).setTextColor(colors.colorWhite);
            findViewById(R.id.upperBar).setBackgroundResource(R.drawable.rounded_rect_3_white);
            ((TextView) findViewById(R.id.predictor)).setTextColor(colors.colorRed);
            ((TextView) findViewById(R.id.warnings)).setTextColor(colors.colorRed);
            ((TextView) findViewById(R.id.notes)).setTextColor(colors.colorRed);
            ((TextView) findViewById(R.id.ai_helper)).setTextColor(colors.colorRed);
            ((TextView) findViewById(R.id.charts)).setTextColor(colors.colorRed);
            findViewById(R.id.tools_box).setBackgroundResource(R.drawable.rounded_rect_1_white);

            findViewById(R.id.switchBar).setBackgroundColor(colors.colorLightRed);
            ((TextView) findViewById(R.id.user_page_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.user_page)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.main_page_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.main_page)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.tool_page_text)).setTextColor(colors.colorRed);
            ((TextView) findViewById(R.id.tool_page)).setTextColor(colors.colorRed);
        }
    }

    private void initViews() {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");

        tv_predictor = findViewById(R.id.predictor);
        tv_predictor.setTypeface(fontAwe);
        tv_notes = findViewById(R.id.notes);
        tv_notes.setTypeface(fontAwe);
        tv_user = findViewById(R.id.user_page);
        tv_user.setTypeface(fontAwe);
        tv_tools = findViewById(R.id.tool_page);
        tv_tools.setTypeface(fontAwe);
        tv_main = findViewById(R.id.main_page);
        tv_main.setTypeface(fontAwe);
        tv_warnings = findViewById(R.id.warnings);
        tv_warnings.setTypeface(fontAwe);
        tv_ai_helper = findViewById(R.id.ai_helper);
        tv_ai_helper.setTypeface(fontAwe);
        tv_charts = findViewById(R.id.charts);
        tv_charts.setTypeface(fontAwe);
        tv_searchIcon = findViewById(R.id.search_icon);
        tv_searchIcon.setTypeface(fontAwe);
        tv_upperBar = findViewById(R.id.upperBar);
        al_predictor = findViewById(R.id.tools_predictor);
        al_warning = findViewById(R.id.tools_warning);
        al_notes = findViewById(R.id.tools_notes);
        al_ai_helper = findViewById(R.id.tools_ai_helper);
        al_charts = findViewById(R.id.tools_charts);

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        if(userSettings != null && (int)userSettings.get("isNew") == 1) {
            al_predictor.setVisibility(View.GONE);
            al_warning.setVisibility(View.GONE);
        }
        if(userSettings != null) changeMode((int) userSettings.get("isDark") == 1);
        //setSupportActionBar(toolbar);
    }
    private void setListeners() {
        tv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ToolsPage.this, StockSearchPage.class);
                intent.putExtra("searchInput","");
                startActivity(intent);
            }
        });
        tv_upperBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ToolsPage.this, StockSearchPage.class);
                intent.putExtra("searchInput","");
                startActivity(intent);
            }
        });
        al_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ToolsPage.this, UserNotes.class);
                startActivity(intent);
            }
        });

        al_predictor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ToolsPage.this, PredictorPage.class);
                startActivity(intent);
            }
        });
        al_ai_helper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ToolsPage.this, AIhelperPage.class);
                startActivity(intent);
            }
        });
        al_charts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ToolsPage.this, ChartPage.class);
                startActivity(intent);
            }
        });


        tv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ToolsPage.this, UserPageSimple.class);
                startActivity(intent);
            }
        });
        tv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ToolsPage.this, StockMainPage.class);
                startActivity(intent);
            }
        });
    }
}
