package com.example.finance;

import static com.example.finance.utils.BasicFunctions.formatNum;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.finance.api.UserApi;
import com.example.finance.api.tushare.Tushare;
import com.example.finance.common.Colors;
import com.example.finance.utils.GetCurTime;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartPage extends AppCompatActivity {

    private GetCurTime getCurTime;
    private Colors colors;
    private UserApi userApi;

    private String input;
    private List<AbsoluteLayout> charts;
    private List<Map<String, Object>> data;
    private ScrollView sv_resView;
    private LinearLayout ll_res;
    private Toolbar toolbar;
    private TextView tv_backBtn;
    private AbsoluteLayout chartAL;

    private TextView tv_back;
    private TextView tv_noResult;
    private TextView tv_result;
    private Button btn_pre;
    private Button btn_post;
    private TextView tv_pageNumber;

    private Map<Integer,String> IDs;
    private Map<Integer,String> names;

    private Integer page = 1;
    private Integer pageSize = 10;
    private Integer count = 50;

    private Map chartsMap;

    private String userId = "";
    private Map<String,Object> userSettings = null;

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
        setContentView(R.layout.chart_page);
        Intent intent = getIntent();
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if(!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if(res.getCode()==0) {
                    Toast.makeText(ChartPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
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
        
        if(isDark) {
            findViewById(R.id.chartPage_body).setBackgroundColor(colors.colorSuperGray);
            findViewById(R.id.chartPage_line).setBackgroundColor(colors.colorWhite);
            ((TextView)findViewById(R.id.result_text)).setTextColor(colors.colorWhite);
            ((TextView)findViewById(R.id.no_result)).setTextColor(colors.colorWhite);
            ((TextView)findViewById(R.id.pageNumber)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.pre_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.post_arrow)).setTextColor(colors.colorGray);/*
            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_button_green);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_button_green);*/
            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_rect_3_gray);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_rect_3_gray);
        } else {
            findViewById(R.id.chartPage_body).setBackgroundColor(colors.colorWhite);
            findViewById(R.id.chartPage_line).setBackgroundColor(colors.colorGray);
            ((TextView)findViewById(R.id.result_text)).setTextColor(colors.colorGray);
            ((TextView)findViewById(R.id.no_result)).setTextColor(colors.colorGray);
            ((TextView)findViewById(R.id.pageNumber)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.pre_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.post_arrow)).setTextColor(colors.colorGray);/*
            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_button_purple);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_button_purple);*/
            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_rect_3_white);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_rect_3_white);
        }
    }

    private void initView() throws IOException, InterruptedException {

        charts = new ArrayList<AbsoluteLayout>();
        IDs = new HashMap<>();
        names = new HashMap<>();
        data = new ArrayList<>();

        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");

        ll_res = findViewById(R.id.resultViewC);
        tv_pageNumber = findViewById(R.id.pageNumber);
        btn_pre = findViewById(R.id.pre_arrow);
        btn_post = findViewById(R.id.post_arrow);/*
        tv_back = findViewById(R.id.back);
        tv_back.setTypeface(fontAwe);*/
        tv_noResult = findViewById(R.id.no_result);
        tv_result = findViewById(R.id.result_text);
        sv_resView = findViewById(R.id.resultsView);

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        if(userSettings != null) changeMode((int) userSettings.get("isDark") == 1);

        Thread thread = new Thread() {
            @Override
            public void run() {
                String topList = Tushare.tushareApi("top_list","trade_date="+getCurTime.yestodayTime());
                Map map = JSON.parseObject(topList,Map.class);
                if(!map.containsKey("code") || (Integer) map.get("code")==0) {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "请求错误", Toast.LENGTH_LONG).show();
                    Looper.loop();
                } else {
                    chartsMap = ((Map)map.get("data"));
                    count = JSONObject.parseArray(chartsMap.get("items").toString(),String.class).size();
                    ThreadPage threadPage = new ThreadPage();
                    threadPage.start();
                }
            }
        };

        // 启动线程
        thread.start();
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
    private void chartClicked() {
        for(int i = charts.size() -1; i>=0; i--){
            int finalI = i;
            charts.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(ChartPage.this, StockDataPage.class);
                    intent.putExtra("ts_code",IDs.get(finalI));
                    intent.putExtra("stock_name",names.get(finalI));
                    startActivity(intent);
                }
            });
            int finalI1 = i;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View view = charts.get(finalI1);
                    LinearLayout parent = (LinearLayout) view.getParent();
                    if (parent != null) {
                        ll_res.removeView(charts.get(finalI1));
                    }
                    ll_res.addView(charts.get(finalI1),0);
                }
            });
        }
    }
    private void pageTurn() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //System.out.println("mainAL:"+mainAL.toString());
                for (int i = 0; i < charts.size(); i++) {
                    ll_res.removeView(charts.get(i));
                }
                if(chartAL!=null) {
                    ll_res.removeView(chartAL);
                    chartAL = null;
                }
                charts.clear();
                IDs.clear();
                com.example.finance.common.R<Object> res = null;
                List<Map<String, Object>> ls = new ArrayList<>();
                List<Object> items = JSONObject.parseArray(chartsMap.get("items").toString(),Object.class);
                List<String> fields = JSONObject.parseArray(chartsMap.get("fields").toString(),String.class);
                for (int i = (page-1)*pageSize; i < page*pageSize; i++) {
                    Map<String, Object> tmpMap = new HashMap<>();
                    List<Object> item = JSONObject.parseArray(items.get(i).toString(),Object.class);
                    for (int j = 0; j < item.size(); j++) {
                        tmpMap.put(fields.get(j), item.get(j));
                    }
                    ls.add(tmpMap);
                }
                res = com.example.finance.common.R.success(ls);
                if(res.getCode()==0) {
                    Toast.makeText(ChartPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                    return;
                }
                data = (List<Map<String, Object>>) res.getData();
                tv_pageNumber.setText(page+"/"+(int)Math.ceil((float)count/(float)pageSize)+"页");
                if(data.size()!=0) tv_noResult.setVisibility(View.GONE);
                else {
                    btn_post.setVisibility(View.GONE);
                    btn_pre.setVisibility(View.GONE);
                    tv_pageNumber.setVisibility(View.GONE);
                }
                for(int i=0;i<data.size();i++){
                    charts.add(addChartResult(data.get(i), i));
                    IDs.put(i,(String)data.get(i).get("ts_code"));
                    names.put(i,(String)data.get(i).get("name"));
                }
                    chartClicked();
                    chartAL = new AbsoluteLayout(ChartPage.this);
                    chartAL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,140));
                    if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                        chartAL.setBackgroundColor(colors.colorSuperGray);
                    } else {
                        chartAL.setBackgroundColor(colors.colorWhite);
                    }

                    TextView tsCode = new TextView(ChartPage.this);
                    tsCode.setText("代码");
                    tsCode.setTextSize(17);
                    if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                        tsCode.setTextColor(colors.colorWhite);
                    } else{
                        tsCode.setTextColor(colors.colorGray);
                    }
                    //topicTitle.setTextColor(colors.colorGray);
                    tsCode.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,38,0));
                    chartAL.addView(tsCode);

                    TextView name = new TextView(ChartPage.this);
                    name.setText("名称");
                    name.setTextSize(17);
                    if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                        name.setTextColor(colors.colorWhite);
                    } else{
                        name.setTextColor(colors.colorGray);
                    }
                    //topicTitle.setTextColor(colors.colorGray);
                    name.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,258,0));
                    chartAL.addView(name);

                    TextView turnover_rate = new TextView(ChartPage.this);
                    turnover_rate.setText("换手率");
                    turnover_rate.setTextSize(17);
                    if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                        turnover_rate.setTextColor(colors.colorWhite);
                    } else{
                        turnover_rate.setTextColor(colors.colorGray);
                    }
                    //topicTitle.setTextColor(colors.colorGray);
                    turnover_rate.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,428,0));
                    chartAL.addView(turnover_rate);

                    TextView pct_change = new TextView(ChartPage.this);
                    pct_change.setText("涨跌幅");
                    pct_change.setTextSize(17);
                    if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                        pct_change.setTextColor(colors.colorWhite);
                    } else{
                        pct_change.setTextColor(colors.colorGray);
                    }
                    //topicTitle.setTextColor(colors.colorGray);
                    pct_change.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,588,0));
                    chartAL.addView(pct_change);

                    TextView l_amount = new TextView(ChartPage.this);
                    l_amount.setText("成交额");
                    l_amount.setTextSize(17);
                    if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                        l_amount.setTextColor(colors.colorWhite);
                    } else{
                        l_amount.setTextColor(colors.colorGray);
                    }
                    //topicTitle.setTextColor(colors.colorGray);
                    l_amount.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,748,0));
                    chartAL.addView(l_amount);

                    TextView l_buy = new TextView(ChartPage.this);
                    l_buy.setText("买入额");
                    l_buy.setTextSize(17);
                    if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                        l_buy.setTextColor(colors.colorWhite);
                    } else{
                        l_buy.setTextColor(colors.colorGray);
                    }
                    //topicTitle.setTextColor(colors.colorGray);
                    l_buy.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,918,0));
                    chartAL.addView(l_buy);

                    TextView line = new TextView(ChartPage.this);
                    if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                        line.setBackgroundColor(colors.colorWhite);
                    } else{
                        line.setBackgroundColor(colors.colorGray);
                    }
                    line.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT,6,0,104));
                    chartAL.addView(line);

                    LinearLayout parent = (LinearLayout) chartAL.getParent();
                    if (parent != null) {
                        ll_res.removeView(chartAL);
                    }
                    ll_res.addView(chartAL,0);
            }
        });
    }
    private AbsoluteLayout addChartResult(Map<String, Object> map, int i) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###.#");
        AbsoluteLayout AL = new AbsoluteLayout(ChartPage.this);
        AL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,110));
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            AL.setBackgroundColor(i%2 == 1?colors.colorGray:colors.colorSuperGray);
        } else {
            AL.setBackgroundColor(i%2 == 1?colors.colorWhiteish:colors.colorWhite);
        }

        TextView tsCode = new TextView(ChartPage.this);
        tsCode.setText((String)map.get("ts_code"));
        tsCode.setTextSize(14);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            tsCode.setTextColor(colors.colorWhite);
        } else{
            tsCode.setTextColor(colors.colorGray);
        }
        //topicTitle.setTextColor(colors.colorGray);
        tsCode.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,38,12));
        AL.addView(tsCode);

        TextView name = new TextView(ChartPage.this);
        name.setText((String)map.get("name"));
        name.setTextSize(14);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            name.setTextColor(colors.colorWhite);
        } else{
            name.setTextColor(colors.colorGray);
        }
        //topicTitle.setTextColor(colors.colorGray);
        name.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,258,12));
        AL.addView(name);

        TextView turnover_rate = new TextView(ChartPage.this);
        turnover_rate.setText(decimalFormat.format(map.get("turnover_rate")));
        turnover_rate.setTextSize(15);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            turnover_rate.setTextColor(colors.colorWhite);
        } else{
            turnover_rate.setTextColor(colors.colorGray);
        }
        //topicTitle.setTextColor(colors.colorGray);
        turnover_rate.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,428,12));
        AL.addView(turnover_rate);

        TextView pct_change = new TextView(ChartPage.this);
        BigDecimal bigDecimal = new BigDecimal(map.get("pct_change").toString());
        float f2 = bigDecimal.floatValue();
        pct_change.setText(decimalFormat.format(map.get("pct_change")));
        pct_change.setTextSize(15);
        if(f2 > 0) {
            pct_change.setTextColor(colors.colorStockRise);
        } else{
            pct_change.setTextColor(colors.colorStockFall);
        }
        //topicTitle.setTextColor(colors.colorGray);
        pct_change.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,588,12));
        AL.addView(pct_change);

        TextView l_amount = new TextView(ChartPage.this);
        l_amount.setText(formatNum(map.get("l_amount").toString(), false));
        l_amount.setTextSize(15);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            l_amount.setTextColor(colors.colorWhite);
        } else{
            l_amount.setTextColor(colors.colorGray);
        }
        //topicTitle.setTextColor(colors.colorGray);
        l_amount.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,748,12));
        AL.addView(l_amount);

        TextView l_buy = new TextView(ChartPage.this);
        BigDecimal bigDecimal2 = new BigDecimal(map.get("l_buy").toString());
        float f1 = bigDecimal2.floatValue();
        l_buy.setText(formatNum(map.get("l_buy").toString(), false));
        l_buy.setTextSize(15);
        if(f1 > 0) {
            l_buy.setTextColor(colors.colorStockRise);
        } else{
            l_buy.setTextColor(colors.colorStockFall);
        }
        //topicTitle.setTextColor(colors.colorGray);
        l_buy.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,918,12));
        AL.addView(l_buy);

        TextView line = new TextView(ChartPage.this);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            line.setBackgroundColor(colors.colorWhite);
        } else{
            line.setBackgroundColor(colors.colorGray);
        }
        line.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT,6,0,104));
        AL.addView(line);

        return AL;
    }
}
