package com.example.finance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.HistoryApi;
import com.example.finance.api.UserApi;
import com.example.finance.common.Colors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHistory extends AppCompatActivity {

    private UserApi userApi;
    private Colors colors;
    private HistoryApi historyApi;

    private String input = "";
    private List<AbsoluteLayout> historyAL;
    private List<Map<String, Object>> historyData;
    private ScrollView sv_resView;
    private LinearLayout ll_res;
/*
    private TextView tv_back;*/
    private TextView tv_pageNumber;
    private Button btn_pre;
    private Button btn_post;
    private TextView tv_noResult;
    private EditText et_historyInput;
    private TextView tv_historySearch;
    private Toolbar toolbar;
    private TextView tv_backBtn;

    private Map<Integer,String> IDs;
    private Integer page = 1;
    private Integer pageSize = 10;
    private Integer count = 0;

    private String userId = "";
    private String type = "类型1";
    private Map<String,Object> userSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_history);

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if(!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if(res.getCode()==0) {
                    Toast.makeText(UserHistory.this, res.getMsg(), Toast.LENGTH_LONG).show();
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
            findViewById(R.id.userHistory_body).setBackgroundColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.userHistory_title)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.history_search)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.history_input)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.history_input)).setBackgroundResource(R.drawable.edittext_bg_white);
            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_rect_3_gray);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_rect_3_gray);
            ((TextView) findViewById(R.id.pre_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.post_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.no_result)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.pageNumber)).setTextColor(colors.colorWhite);
            findViewById(R.id.userHistory_line).setBackgroundColor(colors.colorWhite);

        } else {
            findViewById(R.id.userHistory_body).setBackgroundColor(colors.colorWhite);
            ((TextView) findViewById(R.id.userHistory_title)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.history_search)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.history_input)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.history_input)).setBackgroundResource(R.drawable.edittext_bg_gray);
            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_rect_3_white);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_rect_3_white);
            ((TextView) findViewById(R.id.pre_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.post_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.no_result)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.pageNumber)).setTextColor(colors.colorGray);
            findViewById(R.id.userHistory_line).setBackgroundColor(colors.colorGray);
        }
    }

    private void initViews() {
        try {
            com.example.finance.common.R<Object> res = null;
            res = historyApi.GetHistoryCount(userId,input,type);
            if(res.getCode()==0) {
                Toast.makeText(UserHistory.this, res.getMsg(), Toast.LENGTH_LONG).show();
            } else {
                count = (Integer) res.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tv_noResult = findViewById(R.id.no_result);
        historyAL = new ArrayList<>();
        IDs = new HashMap<>();
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");/*
        tv_back = findViewById(R.id.back);
        tv_back.setTypeface(fontAwe);*/
        btn_post = findViewById(R.id.post_arrow);
        btn_pre = findViewById(R.id.pre_arrow);
        ll_res = findViewById(R.id.resultViewC);
        tv_pageNumber = findViewById(R.id.pageNumber);
        sv_resView = findViewById(R.id.resultsView);
        tv_historySearch = findViewById(R.id.history_search);
        tv_historySearch.setTypeface(fontAwe);
        et_historyInput = findViewById(R.id.history_input);

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        if(userSettings != null) changeMode((int) userSettings.get("isDark") == 1);
        pageTurn();
    }
    private void setListeners() {
        tv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page++;
                if(page>Math.ceil((float)count/(float)pageSize)) {
                    page--;
                    return;
                }
                pageTurn();
            }
        });
        btn_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page--;
                if(page<=0) {
                    page++;
                    return;
                }
                pageTurn();
            }
        });/*
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        et_historyInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean isEnter = event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
                input = et_historyInput.getText().toString();
                Toast.makeText(UserHistory.this, "您输入的是"+input, Toast.LENGTH_LONG).show();
                com.example.finance.common.R<Object> res = null;
                try {
                    res = historyApi.GetHistoryByPage(userId,page,pageSize,input);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(res.getCode()==0) {
                    Toast.makeText(UserHistory.this, res.getMsg(), Toast.LENGTH_LONG).show();
                    return false;
                }
                historyData = (List<Map<String, Object>>) res.getData();
                pageTurn();
                return isEnter;

            }
        });
        tv_historySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input = et_historyInput.getText().toString();
                Toast.makeText(UserHistory.this, "您输入的是"+input, Toast.LENGTH_LONG).show();
                com.example.finance.common.R<Object> res = null;
                try {
                    res = historyApi.GetHistoryByPage(userId,page,pageSize,input);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(res.getCode()==0) {
                    Toast.makeText(UserHistory.this, res.getMsg(), Toast.LENGTH_LONG).show();
                    return;
                }
                historyData = (List<Map<String, Object>>) res.getData();
                pageTurn();
            }
        });
    }
    private void historyClicked() {
        for(int i=0;i<historyAL.size();i++){
            int finalI = i;
            historyAL.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(UserHistory.this, StockDataPage.class);
                    intent.putExtra("ts_code",IDs.get(finalI));
                    startActivity(intent);
                }
            });
            ll_res.addView(historyAL.get(i),0);
        }
    }
    private void pageTurn() {
        for (int i = 0; i < historyAL.size(); i++) {
            ll_res.removeView(historyAL.get(i));
        }
        historyAL.clear();
        IDs.clear();
        com.example.finance.common.R<Object> res = null;
        try {
            res = historyApi.GetHistoryByPage(userId,page,pageSize,input);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(res.getCode()==0) {
            Toast.makeText(UserHistory.this, res.getMsg(), Toast.LENGTH_LONG).show();
            return;
        }
        historyData = (List<Map<String, Object>>) res.getData();
        tv_pageNumber.setText(page+"/"+(int)Math.ceil((float)count/(float)pageSize)+"页");
        if(historyData.size()!=0) {
            btn_post.setVisibility(View.VISIBLE);
            btn_pre.setVisibility(View.VISIBLE);
            tv_pageNumber.setVisibility(View.VISIBLE);
            tv_noResult.setVisibility(View.GONE);
        }
        else {
            btn_post.setVisibility(View.GONE);
            btn_pre.setVisibility(View.GONE);
            tv_pageNumber.setVisibility(View.GONE);
            tv_noResult.setVisibility(View.VISIBLE);
        }
        for(int i=0;i<historyData.size();i++){
            historyAL.add(addStockResult(historyData.get(i), i));
            IDs.put(i,(String)historyData.get(i).get("tsCode"));
        }
        historyClicked();
    }
    private AbsoluteLayout addStockResult(Map<String, Object> map, int i) {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        AbsoluteLayout AL = new AbsoluteLayout(UserHistory.this);
        AL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,190));
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            AL.setBackgroundColor(i%2 == 1?colors.colorGrayish:colors.colorGray);
        } else {
            AL.setBackgroundColor(i%2 == 1?colors.colorWhiteish:colors.colorWhite);
        }

        TextView stockName = new TextView(UserHistory.this);
        stockName.setText((String)map.get("tsName"));
        stockName.setTextSize(25);
        if(userSettings != null && (int) userSettings.get("isDark") == 0) {
            stockName.setTextColor(colors.colorGray);
        } else{
            stockName.setTextColor(colors.colorWhite);
        }
        stockName.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,38,0));
        AL.addView(stockName);

        TextView ID = new TextView(UserHistory.this);
        ID.setText((String)map.get("tsCode"));
        ID.setTextSize(17);
        if(userSettings != null && (int) userSettings.get("isDark") == 0) {
            ID.setTextColor(colors.colorGray);
        } else{
            ID.setTextColor(colors.colorWhite);
        }
        ID.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,338,30));
        AL.addView(ID);

        TextView date = new TextView(UserHistory.this);
        date.setText((String)map.get("sDate"));
        date.setTextSize(20);
        if(userSettings != null && (int) userSettings.get("isDark") == 0) {
            date.setTextColor(colors.colorGray);
        } else{
            date.setTextColor(colors.colorWhite);
        }
        date.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,38,100));
        AL.addView(date);

        TextView line = new TextView(UserHistory.this);
        if(userSettings != null && (int) userSettings.get("isDark") == 0) {
            line.setBackgroundColor(colors.colorGray);
        } else{
            line.setBackgroundColor(colors.colorWhite);
        }
        line.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT,6,0,184));
        AL.addView(line);

        TextView go = new TextView(UserHistory.this);
        go.setTextSize(27);
        go.setText(R.string.fa_chevron_right);
        go.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,970,50));
        go.setTypeface(fontAwe);
        if(userSettings != null && (int) userSettings.get("isDark") == 0) {
            go.setTextColor(colors.colorGray);
        } else{
            go.setTextColor(colors.colorWhite);
        }
        AL.addView(go);

        return AL;
    }
}
