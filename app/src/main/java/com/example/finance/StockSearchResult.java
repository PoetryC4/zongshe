package com.example.finance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.StockApi;
import com.example.finance.api.UserApi;
import com.example.finance.common.Colors;
import com.example.finance.utils.GetCurTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockSearchResult extends AppCompatActivity {

    private Colors colors;
    private GetCurTime getCurTime;
    private UserApi userApi;
    private StockApi stockApi;

    private String input;
    private List<AbsoluteLayout> stocks;
    private List<Map<String, Object>> data;
    private ScrollView sv_resView;
    private LinearLayout ll_res;
    private Toolbar toolbar;
    private TextView tv_backBtn;

    private TextView tv_back;
    private TextView tv_searchStock;
    private TextView tv_upperBar;
    private TextView tv_noResult;
    private TextView tv_result;
    private Button btn_pre;
    private Button btn_post;
    private TextView tv_pageNumber;

    private Map<Integer, String> IDs;
    private Map<Integer, String> names;

    private Integer page = 1;
    private Integer pageSize = 10;
    private Integer count = 50;

    private String userId = "";
    private Map<String, Object> userSettings = null;

    private class ThreadPage extends Thread {

        public ThreadPage() {
            ;
        }

        @Override
        public void run() {
            pageTurn();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_search_result);
        Intent intent = getIntent();
        input = intent.getStringExtra("searchInput");
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if (!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if (res.getCode() == 0) {
                    Toast.makeText(StockSearchResult.this, res.getMsg(), Toast.LENGTH_LONG).show();
                } else {
                    userSettings = (Map<String, Object>) res.getData();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            initView();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setListener();
    }

    private void changeMode(boolean isDark) {

        if (isDark) {
            findViewById(R.id.stockSearchRes_body).setBackgroundColor(colors.colorSuperGray);
            findViewById(R.id.stockSearchRes_upper).setBackgroundColor(colors.colorBlue);
            ((TextView) findViewById(R.id.search_icon)).setTextColor(colors.colorSuperGray);
            findViewById(R.id.upperBar).setBackgroundResource(R.drawable.rounded_rect_3_gray);
            ((TextView) findViewById(R.id.result_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.upperBar)).setTextColor(colors.colorGray);
            findViewById(R.id.stockSearchRes_line).setBackgroundColor(colors.colorWhite);
            ((TextView) findViewById(R.id.no_result)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.pageNumber)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.pre_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.post_arrow)).setTextColor(colors.colorGray);/*
            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_button_green);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_button_green);*/
            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_rect_3_gray);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_rect_3_gray);
        } else {
            findViewById(R.id.stockSearchRes_body).setBackgroundColor(colors.colorWhite);
            findViewById(R.id.stockSearchRes_upper).setBackgroundColor(colors.colorRed);
            ((TextView) findViewById(R.id.search_icon)).setTextColor(colors.colorWhite);
            findViewById(R.id.upperBar).setBackgroundResource(R.drawable.rounded_rect_3_white);
            ((TextView) findViewById(R.id.upperBar)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.result_text)).setTextColor(colors.colorGray);
            findViewById(R.id.stockSearchRes_line).setBackgroundColor(colors.colorGray);
            ((TextView) findViewById(R.id.no_result)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.pageNumber)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.pre_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.post_arrow)).setTextColor(colors.colorGray);/*
            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_button_purple);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_button_purple);*/
            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_rect_3_white);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_rect_3_white);
        }
    }

    private void initView() throws IOException, InterruptedException {

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    if (userApi.AddUserSearchHistory(userId, input, getCurTime.curTime()).getCode() == 0) {
                        Looper.prepare();
                        Toast.makeText(StockSearchResult.this, "操作错误", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // 启动线程
        thread.start();

        stocks = new ArrayList<AbsoluteLayout>();
        IDs = new HashMap<>();
        names = new HashMap<>();
        data = new ArrayList<>();

        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");

        Thread thread2 = new Thread() {
            @Override
            public void run() {
                try {
                    com.example.finance.common.R<Object> res = null;
                    res = stockApi.GetAlikeCount(input);
                    if (res.getCode() == 0) {
                        Looper.prepare();
                        Toast.makeText(StockSearchResult.this, res.getMsg(), Toast.LENGTH_LONG).show();
                        Looper.loop();
                    } else {
                        count = (Integer) res.getData();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // 启动线程
        thread2.start();
        ll_res = findViewById(R.id.resultViewC);
        tv_pageNumber = findViewById(R.id.pageNumber);
        btn_pre = findViewById(R.id.pre_arrow);
        btn_post = findViewById(R.id.post_arrow);/*
        tv_back = findViewById(R.id.back);
        tv_back.setTypeface(fontAwe);*/
        tv_upperBar = findViewById(R.id.upperBar);
        tv_upperBar.setText(input);
        tv_upperBar.setTextColor(Color.rgb(255, 255, 255));
        tv_searchStock = findViewById(R.id.search_icon);
        tv_searchStock.setTypeface(fontAwe);
        colors.colorGray = Color.rgb(65, 65, 65);
        colors.colorStockRise = Color.rgb(219, 74, 57);
        colors.colorStockFall = Color.rgb(87, 171, 118);
        tv_noResult = findViewById(R.id.no_result);
        tv_result = findViewById(R.id.result_text);
        sv_resView = findViewById(R.id.resultsView);

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        if (userSettings != null) changeMode((int) userSettings.get("isDark") == 1);
        ThreadPage threadPage = new ThreadPage();
        threadPage.start();
    }

    private void setListener() {
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
                if (page > Math.ceil((float) count / (float) pageSize)) {
                    page--;
                    return;
                }
                ThreadPage threadPage = new ThreadPage();
                threadPage.start();
            }
        });
        btn_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page--;
                if (page <= 0) {
                    page++;
                    return;
                }
                ThreadPage threadPage = new ThreadPage();
                threadPage.start();
            }
        });/*
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        tv_upperBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(StockSearchResult.this, StockSearchPage.class);
                intent.putExtra("searchInput", "");
                startActivity(intent);
            }
        });
        tv_searchStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(StockSearchResult.this, StockSearchPage.class);
                intent.putExtra("searchInput", "");
                startActivity(intent);
            }
        });
    }

    private void stockClicked() {
        for (int i = stocks.size() - 1; i >= 0; i--) {
            int finalI = i;
            stocks.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(StockSearchResult.this, StockDataPage.class);
                    intent.putExtra("ts_code", IDs.get(finalI));
                    intent.putExtra("stock_name", names.get(finalI));
                    startActivity(intent);
                }
            });
            ll_res.addView(stocks.get(i), 0);
        }
    }

    private void pageTurn() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < stocks.size(); i++) {
                    ll_res.removeView(stocks.get(i));
                }
                com.example.finance.common.R<Object> res = null;
                stocks.clear();
                IDs.clear();
                try {
                    res = stockApi.GetAlikeAll(input, page, pageSize);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (res.getCode() == 0) {
                    Toast.makeText(StockSearchResult.this, res.getMsg(), Toast.LENGTH_LONG).show();
                    return;
                }
                data = (List<Map<String, Object>>) res.getData();
                tv_pageNumber.setText(page + "/" + (int) Math.ceil((double) count / (double) pageSize) + "页");
                if (input != null && !input.isEmpty()) {
                    tv_result.setText("\"" + input + "\"的搜索结果");
                }
                if (data.size() != 0) tv_noResult.setVisibility(View.GONE);
                else {
                    btn_post.setVisibility(View.GONE);
                    btn_pre.setVisibility(View.GONE);
                    tv_pageNumber.setVisibility(View.GONE);
                }
                for (int i = 0; i < data.size(); i++) {
                    stocks.add(addStockResult(data.get(i), i));
                    IDs.put(i, (String) data.get(i).get("tsCode"));
                    names.put(i, (String) data.get(i).get("stoName"));
                }
                stockClicked();
            }
        });
    }

    private AbsoluteLayout addStockResult(Map<String, Object> map, int i) {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        AbsoluteLayout AL = new AbsoluteLayout(StockSearchResult.this);
        AL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 190));
        if (userSettings != null && (int) userSettings.get("isDark") == 1) {
            AL.setBackgroundColor(i % 2 == 1 ? colors.colorGrayish : colors.colorGray);
        } else {
            AL.setBackgroundColor(i % 2 == 1 ? colors.colorWhiteish : colors.colorWhite);
        }

        TextView content = new TextView(StockSearchResult.this);
        content.setText((String) map.get("stoName"));
        content.setTextSize(25);
        if (userSettings != null && (int) userSettings.get("isDark") == 0) {
            content.setTextColor(colors.colorGray);
        } else {
            content.setTextColor(colors.colorWhite);
        }
        content.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT, 38, 8));
        AL.addView(content);

        TextView ID = new TextView(StockSearchResult.this);
        ID.setText((String) map.get("tsCode"));
        ID.setTextSize(17);
        if (userSettings != null && (int) userSettings.get("isDark") == 0) {
            ID.setTextColor(colors.colorGray);
        } else {
            ID.setTextColor(colors.colorWhite);
        }
        //ID.setTextColor(colors.colorGray);
        ID.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT, 38, 100));
        AL.addView(ID);

        /*TextView curPrice = new TextView(StockSearchResult.this);
        curPrice.setText(Double.toString((double)map.get("cur_price")));
        curPrice.setTextSize(17);
        if((double)map.get("price_chg")>=0)
        curPrice.setTextColor(colors.colorStockRise);
        else curPrice.setTextColor(colors.colorStockFall);
        curPrice.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,258,90));
        AL.addView(curPrice);*/


        TextView go = new TextView(StockSearchResult.this);
        go.setTextSize(27);
        go.setText(R.string.fa_chevron_right);
        go.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT, 970, 50));
        go.setTypeface(fontAwe);
        if (userSettings != null && (int) userSettings.get("isDark") == 0) {
            go.setTextColor(colors.colorGray);
        } else {
            go.setTextColor(colors.colorWhite);
        }
        //go.setTextColor(colors.colorGray);
        AL.addView(go);

        TextView details = new TextView(StockSearchResult.this);
        details.setTextSize(25);
        details.setText("行情");
        details.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT, 820, 50));
        details.setTypeface(fontAwe);
        if (userSettings != null && (int) userSettings.get("isDark") == 0) {
            details.setTextColor(colors.colorGray);
        } else {
            details.setTextColor(colors.colorWhite);
        }
        //details.setTextColor(colors.colorGray);
        AL.addView(details);


        TextView line = new TextView(StockSearchResult.this);
        if (userSettings != null && (int) userSettings.get("isDark") == 0) {
            line.setBackgroundColor(colors.colorGray);
        } else {
            line.setBackgroundColor(colors.colorWhite);
        }
        line.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT, 6, 0, 184));
        AL.addView(line);

        return AL;
    }
}
