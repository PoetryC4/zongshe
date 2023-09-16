package com.example.finance;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.finance.api.FavorsApi;
import com.example.finance.api.StockApi;
import com.example.finance.api.TopicApi;
import com.example.finance.api.UserApi;
import com.example.finance.common.Colors;
import com.example.finance.utils.GetURLContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockMainPage extends AppCompatActivity {

    private UserApi userApi;
    private Colors colors;
    private TopicApi topicApi;
    private FavorsApi favorsApi;
    
    private TextView tv_user;
    private TextView tv_tools;
    private TextView tv_main;
    private TextView tv_searchStock;
    private TextView tv_upperBar;
    private TextView tv_favors;
    private TextView tv_topics;
    private TextView tv_charts;
    private ScrollView sv_mainContent;
    private Toolbar toolbar;
    private TextView tv_backBtn;
    private AbsoluteLayout chartAL;

    private List<Map<String,Object>> proStockData;
    private List<AbsoluteLayout> proStockList;
    private List<Map<String,Object>> topicsData;
    private List<AbsoluteLayout> topicsList;
    private String userId = "";
    private Map<String,Object> userSettings = null;

    private Integer page = 1;
    private Integer pageSize = 10;
    private Integer count = 50;
    private TextView tv_pageNumber;
    private Button btn_pre;
    private Button btn_post;
    private TextView tv_noResult;
    private LinearLayout ll_res;
    private List<AbsoluteLayout> mainAL;
    private List<Map<String, Object>> mainData;
    private Map<Integer,String> IDs;
    private Map<Integer,String> names;

    private int curChosen = 0;

    private class ThreadPage extends Thread{

        public ThreadPage(){
            ;
        }

        @Override
        public void run(){
            pageTurn();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_main_page);
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if(!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if(res.getCode()==0) {
                    Toast.makeText(StockMainPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
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
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        tv_user = findViewById(R.id.user_page);
        tv_user.setTypeface(fontAwe);
        tv_tools = findViewById(R.id.tool_page);
        tv_tools.setTypeface(fontAwe);
        tv_main = findViewById(R.id.main_page);
        tv_main.setTypeface(fontAwe);
        tv_upperBar = findViewById(R.id.upperBar);
        tv_searchStock = findViewById(R.id.search_icon);
        tv_searchStock.setTypeface(fontAwe);
        tv_favors = findViewById(R.id.main_favors);
        tv_topics = findViewById(R.id.main_topics);
        tv_charts = findViewById(R.id.main_charts);
        sv_mainContent = findViewById(R.id.main_content);
        btn_post = findViewById(R.id.post_arrow);
        btn_pre = findViewById(R.id.pre_arrow);
        ll_res = findViewById(R.id.main_show);
        tv_pageNumber = findViewById(R.id.pageNumber);
        tv_noResult = findViewById(R.id.no_result);
        mainAL = new ArrayList<>();
        IDs = new HashMap<>();
        names = new HashMap<>();

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        if(userSettings != null) changeMode((int) userSettings.get("isDark") == 1);

        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            tv_favors.setClickable(false);
            tv_favors.setTextColor(colors.colorGray);
            tv_topics.setClickable(true);
            tv_topics.setTextColor(colors.colorSelectedCyan);
            tv_charts.setClickable(true);
            tv_charts.setTextColor(colors.colorWhite);
        } else {
            tv_favors.setClickable(false);
            tv_favors.setTextColor(colors.colorGray);
            tv_topics.setClickable(true);
            tv_topics.setTextColor(colors.colorSelectedOrange);
            tv_charts.setClickable(true);
            tv_charts.setTextColor(colors.colorGray);
        }

        curChosen = 0;
        try {
            com.example.finance.common.R<Object> res = null;
            res = topicApi.GetTopicCount("");
            if(res.getCode()==0) {
                Toast.makeText(StockMainPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
            } else {
                count = (Integer) res.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ThreadPage threadPage = new ThreadPage();
        threadPage.start();
    }
    private void changeMode(boolean isDark) {
        if(isDark) {
            findViewById(R.id.stockMainPage_body).setBackgroundColor(colors.colorSuperGray);
            findViewById(R.id.stockMainPage_upper).setBackgroundColor(colors.colorBlue);
            ((TextView)findViewById(R.id.search_icon)).setTextColor(colors.colorSuperGray);
            findViewById(R.id.upperBar).setBackgroundResource(R.drawable.rounded_rect_3_gray);
            findViewById(R.id.stockMainPage_line).setBackgroundColor(colors.colorWhite);
            ((TextView)findViewById(R.id.main_favors)).setTextColor(colors.colorSelectedCyan);
            ((TextView)findViewById(R.id.main_charts)).setTextColor(colors.colorWhite);
            ((TextView)findViewById(R.id.main_topics)).setTextColor(colors.colorWhite);

            findViewById(R.id.switchBar).setBackgroundColor(colors.colorBlue);
            ((TextView) findViewById(R.id.tool_page_text)).setTextColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.tool_page)).setTextColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.user_page_text)).setTextColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.user_page)).setTextColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.main_page_text)).setTextColor(colors.colorDarkBlue);
            ((TextView) findViewById(R.id.main_page)).setTextColor(colors.colorDarkBlue);

            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_rect_3_gray);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_rect_3_gray);
            ((TextView) findViewById(R.id.pre_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.post_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.no_result)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.pageNumber)).setTextColor(colors.colorWhite);
        } else {
            findViewById(R.id.stockMainPage_body).setBackgroundColor(colors.colorWhite);
            findViewById(R.id.stockMainPage_upper).setBackgroundColor(colors.colorRed);
            ((TextView)findViewById(R.id.search_icon)).setTextColor(colors.colorWhite);
            findViewById(R.id.upperBar).setBackgroundResource(R.drawable.rounded_rect_3_white);
            findViewById(R.id.stockMainPage_line).setBackgroundColor(colors.colorGray);
            ((TextView)findViewById(R.id.main_favors)).setTextColor(colors.colorSelectedOrange);
            ((TextView)findViewById(R.id.main_charts)).setTextColor(colors.colorNotSelected);
            ((TextView)findViewById(R.id.main_topics)).setTextColor(colors.colorNotSelected);

            findViewById(R.id.switchBar).setBackgroundColor(colors.colorLightRed);
            ((TextView) findViewById(R.id.tool_page_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.tool_page)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.user_page_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.user_page)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.main_page_text)).setTextColor(colors.colorRed);
            ((TextView) findViewById(R.id.main_page)).setTextColor(colors.colorRed);

            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_rect_3_white);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_rect_3_white);
            ((TextView) findViewById(R.id.pre_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.post_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.no_result)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.pageNumber)).setTextColor(colors.colorGray);
        }
    }

    private void setListeners() {
        tv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(StockMainPage.this, UserPageSimple.class);
                startActivity(intent);
            }
        });
        tv_upperBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(StockMainPage.this, StockSearchPage.class);
                intent.putExtra("searchInput","");
                startActivity(intent);
            }
        });
        tv_searchStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(StockMainPage.this, StockSearchPage.class);
                intent.putExtra("searchInput","");
                startActivity(intent);
            }
        });
        tv_tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(StockMainPage.this, ToolsPage.class);
                startActivity(intent);
            }
        });

        tv_topics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                    tv_topics.setClickable(false);
                    tv_topics.setTextColor(colors.colorSelectedCyan);
                    tv_favors.setClickable(true);
                    tv_favors.setTextColor(colors.colorWhite);
                    tv_charts.setClickable(true);
                    tv_charts.setTextColor(colors.colorWhite);
                } else {
                    tv_topics.setClickable(false);
                    tv_topics.setTextColor(colors.colorSelectedOrange);
                    tv_favors.setClickable(true);
                    tv_favors.setTextColor(colors.colorGray);
                    tv_charts.setClickable(true);
                    tv_charts.setTextColor(colors.colorGray);
                }

                curChosen = 0;
                try {
                    com.example.finance.common.R<Object> res = null;
                    res = topicApi.GetTopicCount("");
                    if(res.getCode()==0) {
                        Toast.makeText(StockMainPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                    } else {
                        count = (Integer) res.getData();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ThreadPage threadPage = new ThreadPage();
                threadPage.start();
            }
        });
        tv_favors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                            tv_favors.setClickable(false);
                            tv_favors.setTextColor(colors.colorSelectedCyan);
                            tv_topics.setClickable(true);
                            tv_topics.setTextColor(colors.colorWhite);
                            tv_charts.setClickable(true);
                            tv_charts.setTextColor(colors.colorWhite);
                        } else {
                            tv_favors.setClickable(false);
                            tv_favors.setTextColor(colors.colorSelectedOrange);
                            tv_topics.setClickable(true);
                            tv_topics.setTextColor(colors.colorGray);
                            tv_charts.setClickable(true);
                            tv_charts.setTextColor(colors.colorGray);
                        }

                        curChosen = 1;
                        try {
                            com.example.finance.common.R<Object> res = null;
                            res = favorsApi.GetFavorsCount(userId,"","");
                            if(res.getCode()==0) {
                                Toast.makeText(StockMainPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                            } else {
                                count = (Integer) res.getData();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ThreadPage threadPage = new ThreadPage();
                        threadPage.start();
                    }
                };

                // 启动线程
                thread.start();
            }
        });
        tv_charts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                            tv_charts.setClickable(false);
                            tv_charts.setTextColor(colors.colorSelectedCyan);
                            tv_topics.setClickable(true);
                            tv_topics.setTextColor(colors.colorWhite);
                            tv_favors.setClickable(true);
                            tv_favors.setTextColor(colors.colorWhite);
                        } else {
                            tv_charts.setClickable(false);
                            tv_charts.setTextColor(colors.colorSelectedOrange);
                            tv_topics.setClickable(true);
                            tv_topics.setTextColor(colors.colorGray);
                            tv_favors.setClickable(true);
                            tv_favors.setTextColor(colors.colorGray);
                        }

                        curChosen = 2;
                       /* try {
                            com.example.finance.common.R<Object> res = null;
                            res = favorsApi.GetChartsCount(userId,"","");
                            if(res.getCode()==0) {
                                Toast.makeText(StockMainPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                            } else {
                                count = (Integer) res.getData();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        ThreadPage threadPage = new ThreadPage();
                        threadPage.start();*/
                    }
                };

                // 启动线程
                thread.start();
                ThreadPage threadPage = new ThreadPage();
                threadPage.start();
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
                ThreadPage threadPage = new ThreadPage();
                threadPage.start();
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
                ThreadPage threadPage = new ThreadPage();
                threadPage.start();
            }
        });
    }
    private void mainClicked() {
        for(int i = mainAL.size() -1; i>=0; i--){
            int finalI = i;
            mainAL.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    switch(curChosen) {
                        case 0:{
                            intent.setClass(StockMainPage.this, TopicPage.class);
                            intent.putExtra("topicID",IDs.get(finalI));
                            break;
                        }
                        case 1:{
                            intent.setClass(StockMainPage.this, StockDataPage.class);
                            intent.putExtra("ts_code",IDs.get(finalI));
                            intent.putExtra("stock_name",names.get(finalI));
                            break;
                        }
                        case 2:{
                            break;
                        }
                        default:{
                            break;
                        }
                    }
                    startActivity(intent);
                }
            });
            ll_res.addView(mainAL.get(i),0);
        }
    }
    private void pageTurn() {
        for (int i = 0; i < mainAL.size(); i++) {
            ll_res.removeView(mainAL.get(i));
        }
        if(chartAL!=null) {
            ll_res.removeView(chartAL);
            chartAL = null;
        }
        mainAL.clear();
        IDs.clear();
        com.example.finance.common.R<Object> res = null;
        try {
            switch(curChosen) {
                case 0:{
                    res = topicApi.GetTopicsByPage("",page,pageSize);
                    break;
                }
                case 1:{
                    res = favorsApi.GetFavorsByPage(userId,page,pageSize);
                    break;
                }
                case 2:{
                    //res = favorsApi.GetChartsByPage(userId,page,pageSize);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(res.getCode()==0) {
            Looper.prepare();
            Toast.makeText(StockMainPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
            Looper.loop();
            return;
        }
        mainData = (List<Map<String, Object>>) res.getData();
        tv_pageNumber.setText(page+"/"+(int)Math.ceil((float)count/(float)pageSize)+"页");
        if(mainData.size()!=0) tv_noResult.setVisibility(View.GONE);
        else {
            btn_post.setVisibility(View.GONE);
            btn_pre.setVisibility(View.GONE);
            tv_pageNumber.setVisibility(View.GONE);
        }
        for(int i=0;i<mainData.size();i++){
            switch (curChosen) {
                case 0:{
                    mainAL.add(addTopicResult(mainData.get(i), i));
                    IDs.put(i,"" + mainData.get(i).get("newsId"));
                    break;
                }
                case 1:{
                    mainAL.add(addFavorsResult(mainData.get(i), i));
                    IDs.put(i,(String)mainData.get(i).get("tsCode"));
                    names.put(i,(String)mainData.get(i).get("stoName"));
                    break;
                }
                case 2:{//hzyTo

                    mainAL.add(addChartResult(mainData.get(i), i));
                    IDs.put(i,(String)mainData.get(i).get("tsCode"));
                    names.put(i,(String)mainData.get(i).get("stoName"));
                    break;
                }
            }
        }
        if(curChosen == 2) {
            chartAL = new AbsoluteLayout(StockMainPage.this);
            chartAL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,190));
            if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                chartAL.setBackgroundColor(colors.colorSuperGray);
            } else {
                chartAL.setBackgroundColor(colors.colorWhite);
            }

            TextView tsCode = new TextView(StockMainPage.this);
            tsCode.setText("代码");
            tsCode.setTextSize(25);
            if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                tsCode.setTextColor(colors.colorWhite);
            } else{
                tsCode.setTextColor(colors.colorGray);
            }
            //topicTitle.setTextColor(colors.colorGray);
            tsCode.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,38,0));
            chartAL.addView(tsCode);

            TextView name = new TextView(StockMainPage.this);
            name.setText("名称");
            name.setTextSize(25);
            if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                name.setTextColor(colors.colorWhite);
            } else{
                name.setTextColor(colors.colorGray);
            }
            //topicTitle.setTextColor(colors.colorGray);
            name.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,218,0));
            chartAL.addView(name);

            TextView turnover_rate = new TextView(StockMainPage.this);
            turnover_rate.setText("换手率");
            turnover_rate.setTextSize(25);
            if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                turnover_rate.setTextColor(colors.colorWhite);
            } else{
                turnover_rate.setTextColor(colors.colorGray);
            }
            //topicTitle.setTextColor(colors.colorGray);
            turnover_rate.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,398,0));
            chartAL.addView(turnover_rate);

            TextView pct_change = new TextView(StockMainPage.this);
            pct_change.setText("涨跌幅");
            pct_change.setTextSize(25);
            if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                pct_change.setTextColor(colors.colorWhite);
            } else{
                pct_change.setTextColor(colors.colorGray);
            }
            //topicTitle.setTextColor(colors.colorGray);
            pct_change.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,578,0));
            chartAL.addView(pct_change);

            TextView l_amount = new TextView(StockMainPage.this);
            l_amount.setText("成交额");
            l_amount.setTextSize(25);
            if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                l_amount.setTextColor(colors.colorWhite);
            } else{
                l_amount.setTextColor(colors.colorGray);
            }
            //topicTitle.setTextColor(colors.colorGray);
            l_amount.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,758,0));
            chartAL.addView(l_amount);

            TextView l_buy = new TextView(StockMainPage.this);
            l_buy.setText("买入额");
            l_buy.setTextSize(25);
            if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                l_buy.setTextColor(colors.colorWhite);
            } else{
                l_buy.setTextColor(colors.colorGray);
            }
            //topicTitle.setTextColor(colors.colorGray);
            l_buy.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,938,0));
            chartAL.addView(l_buy);

            TextView line = new TextView(StockMainPage.this);
            if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                line.setBackgroundColor(colors.colorWhite);
            } else{
                line.setBackgroundColor(colors.colorGray);
            }
            line.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT,6,0,184));
            chartAL.addView(line);

            ll_res.addView(chartAL,0);
        }
        mainClicked();
    }

    private AbsoluteLayout addTopicResult(Map<String, Object> map, int i) {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        AbsoluteLayout AL = new AbsoluteLayout(StockMainPage.this);
        AL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,190));
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            AL.setBackgroundColor(i%2 == 1?colors.colorGray:colors.colorSuperGray);
        } else {
            AL.setBackgroundColor(i%2 == 1?colors.colorWhiteish:colors.colorWhite);
        }

        TextView topicTitle = new TextView(StockMainPage.this);
        topicTitle.setText((String)map.get("title"));
        topicTitle.setTextSize(25);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            topicTitle.setTextColor(colors.colorWhite);
        } else{
            topicTitle.setTextColor(colors.colorGray);
        }
        //topicTitle.setTextColor(colors.colorGray);
        topicTitle.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,38,0));
        AL.addView(topicTitle);

        TextView topicChannel = new TextView(StockMainPage.this);
        topicChannel.setText((String)map.get("channels"));
        topicChannel.setTextSize(17);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            topicChannel.setTextColor(colors.colorWhite);
        } else{
            topicChannel.setTextColor(colors.colorGray);
        }
        //topicChannel.setTextColor(colors.colorGray);
        topicChannel.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,238,0));
        AL.addView(topicChannel);

        TextView topicDate = new TextView(StockMainPage.this);
        topicDate.setText((String)map.get("datetime"));
        topicDate.setTextSize(20);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            topicDate.setTextColor(colors.colorWhite);
        } else{
            topicDate.setTextColor(colors.colorGray);
        }
        //topicDate.setTextColor(colors.colorGray);
        topicDate.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,38,100));
        AL.addView(topicDate);
        TextView line = new TextView(StockMainPage.this);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            line.setBackgroundColor(colors.colorWhite);
        } else{
            line.setBackgroundColor(colors.colorGray);
        }
        line.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT,6,0,184));
        AL.addView(line);

        TextView go = new TextView(StockMainPage.this);
        go.setTextSize(27);
        go.setText(R.string.fa_chevron_right);
        go.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,970,50));
        go.setTypeface(fontAwe);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            go.setTextColor(colors.colorWhite);
        } else{
            go.setTextColor(colors.colorGray);
        }
        AL.addView(go);

        return AL;
    }

    private AbsoluteLayout addFavorsResult(Map<String, Object> map, int i) {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        AbsoluteLayout AL = new AbsoluteLayout(StockMainPage.this);
        AL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,190));
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            AL.setBackgroundColor(i%2 == 1?colors.colorGray:colors.colorSuperGray);
        } else {
            AL.setBackgroundColor(i%2 == 1?colors.colorWhiteish:colors.colorWhite);
        }
/*
        TextView stockName = new TextView(UserFavorites.this);
        stockName.setText((String)map.get("stock_name"));
        stockName.setTextSize(25);
        if(userSettings != null && (int) userSettings.get("isDark") == 0) {
            stockName.setTextColor(colors.colorWhite);
        } else{
            stockName.setTextColor(colors.colorGray);
        }
        //stockName.setTextColor(color_gray);
        stockName.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,38,0));
        AL.addView(stockName);*/

        TextView ID = new TextView(StockMainPage.this);
        ID.setText((String)map.get("tsCode"));
        ID.setTextSize(22);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            ID.setTextColor(colors.colorWhite);
        } else{
            ID.setTextColor(colors.colorGray);
        }
        ID.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,38,10));
        AL.addView(ID);

        TextView date = new TextView(StockMainPage.this);
        date.setText((String)map.get("favorDate"));
        date.setTextSize(20);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            date.setTextColor(colors.colorWhite);
        } else{
            date.setTextColor(colors.colorGray);
        }
        date.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,38,100));
        AL.addView(date);


        TextView go = new TextView(StockMainPage.this);
        go.setTextSize(27);
        go.setText(R.string.fa_chevron_right);
        go.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,970,50));
        go.setTypeface(fontAwe);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            go.setTextColor(colors.colorWhite);
        } else{
            go.setTextColor(colors.colorGray);
        }
        //go.setTextColor(color_gray);
        AL.addView(go);
        TextView line = new TextView(StockMainPage.this);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            line.setBackgroundColor(colors.colorWhite);
        } else{
            line.setBackgroundColor(colors.colorGray);
        }
        line.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT,6,0,184));
        AL.addView(line);
        return AL;
    }
    private AbsoluteLayout addChartResult(Map<String, Object> map, int i) {
        AbsoluteLayout AL = new AbsoluteLayout(StockMainPage.this);
        AL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,190));
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            AL.setBackgroundColor(i%2 == 1?colors.colorGray:colors.colorSuperGray);
        } else {
            AL.setBackgroundColor(i%2 == 1?colors.colorWhiteish:colors.colorWhite);
        }

        TextView tsCode = new TextView(StockMainPage.this);
        tsCode.setText((String)map.get("ts_code"));
        tsCode.setTextSize(25);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            tsCode.setTextColor(colors.colorWhite);
        } else{
            tsCode.setTextColor(colors.colorGray);
        }
        //topicTitle.setTextColor(colors.colorGray);
        tsCode.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,38,0));
        AL.addView(tsCode);

        TextView name = new TextView(StockMainPage.this);
        name.setText((String)map.get("name"));
        name.setTextSize(25);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            name.setTextColor(colors.colorWhite);
        } else{
            name.setTextColor(colors.colorGray);
        }
        //topicTitle.setTextColor(colors.colorGray);
        name.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,218,0));
        AL.addView(name);

        TextView turnover_rate = new TextView(StockMainPage.this);
        turnover_rate.setText((String)map.get("turnover_rate"));
        turnover_rate.setTextSize(25);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            turnover_rate.setTextColor(colors.colorWhite);
        } else{
            turnover_rate.setTextColor(colors.colorGray);
        }
        //topicTitle.setTextColor(colors.colorGray);
        turnover_rate.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,398,0));
        AL.addView(turnover_rate);

        TextView pct_change = new TextView(StockMainPage.this);
        pct_change.setText((String)map.get("pct_change"));
        pct_change.setTextSize(25);
        if(Float.parseFloat((String)map.get("pct_change")) > 0) {
            pct_change.setTextColor(colors.colorStockRise);
        } else{
            pct_change.setTextColor(colors.colorStockFall);
        }
        //topicTitle.setTextColor(colors.colorGray);
        pct_change.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,578,0));
        AL.addView(pct_change);

        TextView l_amount = new TextView(StockMainPage.this);
        l_amount.setText((String)map.get("l_amount"));
        l_amount.setTextSize(25);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            l_amount.setTextColor(colors.colorWhite);
        } else{
            l_amount.setTextColor(colors.colorGray);
        }
        //topicTitle.setTextColor(colors.colorGray);
        l_amount.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,758,0));
        AL.addView(l_amount);

        TextView l_buy = new TextView(StockMainPage.this);
        l_buy.setText((String)map.get("l_buy"));
        l_buy.setTextSize(25);
        if(Float.parseFloat((String)map.get("l_buy")) > 0) {
            l_buy.setTextColor(colors.colorStockRise);
        } else{
            l_buy.setTextColor(colors.colorStockFall);
        }
        //topicTitle.setTextColor(colors.colorGray);
        l_buy.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,938,0));
        AL.addView(l_buy);

        TextView line = new TextView(StockMainPage.this);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            line.setBackgroundColor(colors.colorWhite);
        } else{
            line.setBackgroundColor(colors.colorGray);
        }
        line.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT,6,0,184));
        AL.addView(line);

        return AL;
    }

}