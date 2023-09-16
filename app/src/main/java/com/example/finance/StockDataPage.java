package com.example.finance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.FavorsApi;
import com.example.finance.api.HistoryApi;
import com.example.finance.api.StockApi;
import com.example.finance.api.UserApi;
import com.example.finance.common.Colors;
import com.example.finance.utils.GetCurTime;
import com.example.finance.views.DoubleClickListener;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;

public class StockDataPage extends AppCompatActivity {

    private UserApi userApi;
    private Colors colors;
    private GetCurTime getCurTime;
    private HistoryApi historyApi;
    private StockApi stockApi;
    private FavorsApi favorsApi;
    private Toolbar toolbar;
    private TextView tv_backBtn;

    private WebView webKMap;
    private String ts_code;
    private String stock_name;
    private Map<String,Object> data;
    private Map<String,Object> detailData;
/*
    private TextView tv_back;
    private TextView tv_news;
    private TextView tv_handicap;
    private TextView tv_capital;
    private TextView tv_announcement;
    private TextView tv_finance;
    private TextView tv_briefInfo;*/
    private TextView tv_stockName;
    private TextView tv_tradeDate;
    private TextView tv_change;
    private TextView tv_pctChange;
    private TextView tv_open;
    private TextView tv_close;
    private TextView tv_high;
    private TextView tv_low;
    private TextView tv_vol;
    private TextView tv_amount;
    private TextView tv_favor;
    private TextView tv_predict;
    private LinearLayout ll_details;
    private TextView tv_cashflowPart;
    private TextView tv_incomePart;
    private TextView tv_balancePart;
    private TextView tv_stockData;
    private TextView tv_stockChart;

    private Map<String,Object> stockData = null;
    private int isFavored;
    private int lineCount = 0;
    private int curChosen = 0;

    private String userId = "";
    private Map<String,Object> userSettings = null;

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
            webKMap.loadUrl("javascript:setTsCode('" + ts_code + "')");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_data_page);
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if(!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if(res.getCode()==0) {
                    Toast.makeText(StockDataPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                } else {
                    userSettings = (Map<String, Object>) res.getData();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Intent intent = getIntent();/*
        ts_code = intent.getStringExtra("ts_code");
        stock_name = intent.getStringExtra("stock_name");*/
        ts_code = "000012.SZ";
        stock_name = "test";
        //hzyNote
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    if((historyApi.AddHistory(userId,getCurTime.curTime(),ts_code,stock_name)).getCode()==0) {
                        Looper.prepare();
                        Toast.makeText(StockDataPage.this, "操作错误", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                    try {
                        com.example.finance.common.R<Object> res = null;
                        res = stockApi.GetLatest(ts_code);
                        if(res.getCode()==0) {
                            Looper.prepare();
                            Toast.makeText(StockDataPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        } else {
                            stockData = (Map<String, Object>) res.getData();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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
        initViews();
        setListeners();

        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            tv_balancePart.setClickable(false);
            tv_balancePart.setTextColor(colors.colorSelectedCyan);
            tv_incomePart.setClickable(true);
            tv_incomePart.setTextColor(colors.colorWhite);
            tv_cashflowPart.setClickable(true);
            tv_cashflowPart.setTextColor(colors.colorWhite);
        }else {
            tv_balancePart.setClickable(false);
            tv_balancePart.setTextColor(colors.colorSelectedOrange);
            tv_incomePart.setClickable(true);
            tv_incomePart.setTextColor(colors.colorGray);
            tv_cashflowPart.setClickable(true);
            tv_cashflowPart.setTextColor(colors.colorGray);
        }
        BalanceInit();
    }

    private void KMapInit(){
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
        webKMap.loadUrl("file:///android_asset/ech.htm");
    }
    private void initViews() {

        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        tv_stockChart = findViewById(R.id.stock_chart_col);
        tv_stockData = findViewById(R.id.stock_data_col);
        tv_stockName = findViewById(R.id.stockName);
        tv_predict = findViewById(R.id.predictButton);
        tv_stockName = findViewById(R.id.stockName);
        tv_tradeDate = findViewById(R.id.tradeDate);
        tv_change = findViewById(R.id.absoChange);
        tv_pctChange = findViewById(R.id.percChange);
        tv_low = findViewById(R.id.low);
        tv_high = findViewById(R.id.high);
        tv_open = findViewById(R.id.open);
        tv_close = findViewById(R.id.close);
        tv_vol = findViewById(R.id.vol);
        tv_amount = findViewById(R.id.amount);
        tv_cashflowPart = findViewById(R.id.cashflow_part);
        tv_balancePart = findViewById(R.id.balance_part);
        tv_incomePart = findViewById(R.id.income_part);
        tv_stockName.setText(stock_name);

        if(stockData != null) {
            tv_tradeDate.setText((CharSequence) stockData.get("tradeDate"));

            tv_change.setText(""+Float.parseFloat(String.valueOf(stockData.get("changes")))+"%");
            tv_pctChange.setText(""+Float.parseFloat(String.valueOf(stockData.get("pctChg"))));
            tv_low.setText(""+Float.parseFloat(String.valueOf(stockData.get("low"))));
            tv_high.setText(""+Float.parseFloat(String.valueOf(stockData.get("high"))));
            tv_open.setText(""+Float.parseFloat(String.valueOf(stockData.get("open"))));
            tv_close.setText(""+Float.parseFloat(String.valueOf(stockData.get("close"))));
            tv_amount.setText(FormulatingNumbers(Float.parseFloat(String.valueOf(stockData.get("amount")))));
            tv_vol.setText(FormulatingNumbers(Float.parseFloat(String.valueOf(stockData.get("vol")))));
        }
        tv_stockName.setText("宁德时代");
        tv_tradeDate.setText("2023-06-21");
        tv_change.setText("-0.05%");
        tv_pctChange.setText("-0.8389");
        tv_low.setText("5.91");
        tv_high.setText("5.98");
        tv_open.setText("5.95");
        tv_close.setText("5.91");
        tv_amount.setText(FormulatingNumbers((float) 46538.018));
        tv_vol.setText(FormulatingNumbers((float) 78402.57));

        ll_details = findViewById(R.id.stockInfoDetail);

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        tv_favor = findViewById(R.id.favorButton);
        Thread thread = new Thread() {
            @Override
            public void run() {
                com.example.finance.common.R<String> res = null;
                try {
                    res = FavorsApi.GetFavorsById(userId,ts_code);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(res.getCode()==0) {
                    Looper.prepare();
                    Toast.makeText(StockDataPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                    Looper.loop();
                } else {
                    if(Objects.equals(res.getData(),"N")) {
                        tv_favor.setText("加自选");
                        isFavored = 0;
                    } else {
                        tv_favor.setText("取消自选");
                        isFavored = 1;
                    }
                }
            }
        };
        // 启动线程
        thread.start();
/*
        tv_back = findViewById(R.id.back);
        tv_back.setTypeface(fontAwe);*/
        webKMap = (WebView) findViewById(R.id.KMap);
        KMapInit();

        /*tv_news = findViewById(R.id.news);
        tv_handicap = findViewById(R.id.handicap);
        tv_capital = findViewById(R.id.capital);
        tv_announcement = findViewById(R.id.announcement);
        tv_finance = findViewById(R.id.finance);
        tv_briefInfo = findViewById(R.id.briefIntro);*/
        if(userSettings != null) changeMode((int) userSettings.get("isDark") == 1);
        
    }
    private void changeMode(boolean isDark) {
        
        if(isDark) {
            ((TextView)findViewById(R.id.stock_data_col)).setTextColor(colors.colorSelectedCyan);
            ((TextView)findViewById(R.id.stock_chart_col)).setTextColor(colors.colorWhite);
            findViewById(R.id.stockDataPage_line).setBackgroundColor(colors.colorWhite);
            findViewById(R.id.bars).setBackgroundResource(R.drawable.lined_rectangle_gray);
            findViewById(R.id.stockInfoDetail).setBackgroundColor(colors.colorSuperGray);
            findViewById(R.id.stockDataPage_data).setBackgroundColor(colors.colorBlueish);
            findViewById(R.id.stockDataPage_chart).setBackgroundColor(colors.colorBlueish);
            findViewById(R.id.stockDataPage_body).setBackgroundColor(colors.colorBlue);
            findViewById(R.id.stockDataPage_upper).setBackgroundColor(colors.colorDarkBlue);
            ((TextView)findViewById(R.id.stockName)).setTextColor(colors.colorWhite);
            ((TextView)findViewById(R.id.predictButton)).setTextColor(colors.colorWhite);
            ((TextView)findViewById(R.id.favorButton)).setTextColor(colors.colorWhite);
            findViewById(R.id.stockDataPage_leftPanel).setBackgroundResource(R.drawable.rounded_rect_2_gray);
            ((TextView)findViewById(R.id.percChange)).setTextColor(colors.colorCyan);
            ((TextView)findViewById(R.id.percChangeTxt)).setTextColor(colors.colorWhite);
            ((TextView)findViewById(R.id.absoChange)).setTextColor(colors.colorCyan);
            ((TextView)findViewById(R.id.absoChangext)).setTextColor(colors.colorWhite);
            ((TextView)findViewById(R.id.tradeDate)).setTextColor(colors.colorWhite);
            ((TextView)findViewById(R.id.tradeDateTxt)).setTextColor(colors.colorWhite);
            findViewById(R.id.stockDataPage_rightPanel).setBackgroundResource(R.drawable.rounded_rect_2_gray);
            ((TextView)findViewById(R.id.high)).setTextColor(colors.colorCyan);
            ((TextView)findViewById(R.id.highTxt)).setTextColor(colors.colorWhite);
            ((TextView)findViewById(R.id.low)).setTextColor(colors.colorCyan);
            ((TextView)findViewById(R.id.lowTxt)).setTextColor(colors.colorWhite);
            ((TextView)findViewById(R.id.vol)).setTextColor(colors.colorCyan);
            ((TextView)findViewById(R.id.volTxt)).setTextColor(colors.colorWhite);
            ((TextView)findViewById(R.id.open)).setTextColor(colors.colorCyan);
            ((TextView)findViewById(R.id.openTxt)).setTextColor(colors.colorWhite);
            ((TextView)findViewById(R.id.close)).setTextColor(colors.colorCyan);
            ((TextView)findViewById(R.id.closeTxt)).setTextColor(colors.colorWhite);
            ((TextView)findViewById(R.id.amount)).setTextColor(colors.colorCyan);
            ((TextView)findViewById(R.id.amountTxt)).setTextColor(colors.colorWhite);/*
            ((TextView)findViewById(R.id.news)).setTextColor(colors.colorBlue);
            ((TextView)findViewById(R.id.handicap)).setTextColor(colors.colorWhite);
            ((TextView)findViewById(R.id.capital)).setTextColor(colors.colorWhite);
            ((TextView)findViewById(R.id.announcement)).setTextColor(colors.colorWhite);
            ((TextView)findViewById(R.id.finance)).setTextColor(colors.colorWhite);
            ((TextView)findViewById(R.id.briefIntro)).setTextColor(colors.colorWhite);*/
        } else {
            ((TextView)findViewById(R.id.stock_data_col)).setTextColor(colors.colorSelectedOrange);
            ((TextView)findViewById(R.id.stock_chart_col)).setTextColor(colors.colorNotSelected);
            findViewById(R.id.stockDataPage_line).setBackgroundColor(colors.colorGray);
            findViewById(R.id.bars).setBackgroundResource(R.drawable.lined_rectangle_white);
            findViewById(R.id.stockInfoDetail).setBackgroundColor(colors.colorWhite);
            findViewById(R.id.stockDataPage_data).setBackgroundColor(colors.colorRedish);
            findViewById(R.id.stockDataPage_chart).setBackgroundColor(colors.colorRedish);
            findViewById(R.id.stockDataPage_body).setBackgroundColor(colors.colorRed);
            findViewById(R.id.stockDataPage_upper).setBackgroundColor(colors.colorLightRed);
            ((TextView)findViewById(R.id.stockName)).setTextColor(colors.colorGray);
            ((TextView)findViewById(R.id.predictButton)).setTextColor(colors.colorGray);
            ((TextView)findViewById(R.id.favorButton)).setTextColor(colors.colorGray);
            findViewById(R.id.stockDataPage_leftPanel).setBackgroundResource(R.drawable.rounded_rect_2_white);
            ((TextView)findViewById(R.id.percChange)).setTextColor(colors.colorOrange);
            ((TextView)findViewById(R.id.percChangeTxt)).setTextColor(colors.colorGray);
            ((TextView)findViewById(R.id.absoChange)).setTextColor(colors.colorOrange);
            ((TextView)findViewById(R.id.absoChangext)).setTextColor(colors.colorGray);
            ((TextView)findViewById(R.id.tradeDate)).setTextColor(colors.colorGray);
            ((TextView)findViewById(R.id.tradeDateTxt)).setTextColor(colors.colorGray);
            findViewById(R.id.stockDataPage_rightPanel).setBackgroundResource(R.drawable.rounded_rect_2_white);
            ((TextView)findViewById(R.id.high)).setTextColor(colors.colorOrange);
            ((TextView)findViewById(R.id.highTxt)).setTextColor(colors.colorGray);
            ((TextView)findViewById(R.id.low)).setTextColor(colors.colorOrange);
            ((TextView)findViewById(R.id.lowTxt)).setTextColor(colors.colorGray);
            ((TextView)findViewById(R.id.vol)).setTextColor(colors.colorOrange);
            ((TextView)findViewById(R.id.volTxt)).setTextColor(colors.colorGray);
            ((TextView)findViewById(R.id.open)).setTextColor(colors.colorOrange);
            ((TextView)findViewById(R.id.openTxt)).setTextColor(colors.colorGray);
            ((TextView)findViewById(R.id.close)).setTextColor(colors.colorOrange);
            ((TextView)findViewById(R.id.closeTxt)).setTextColor(colors.colorGray);
            ((TextView)findViewById(R.id.amount)).setTextColor(colors.colorOrange);
            ((TextView)findViewById(R.id.amountTxt)).setTextColor(colors.colorGray);/*
            ((TextView)findViewById(R.id.news)).setTextColor(colors.colorRed);
            ((TextView)findViewById(R.id.handicap)).setTextColor(colors.colorGray);
            ((TextView)findViewById(R.id.capital)).setTextColor(colors.colorGray);
            ((TextView)findViewById(R.id.announcement)).setTextColor(colors.colorGray);
            ((TextView)findViewById(R.id.finance)).setTextColor(colors.colorGray);
            ((TextView)findViewById(R.id.briefIntro)).setTextColor(colors.colorGray);*/
        }
    }

    private void setListeners() {
        tv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_stockChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                    tv_stockChart.setClickable(false);
                    tv_stockChart.setTextColor(colors.colorSelectedCyan);
                    tv_stockData.setClickable(true);
                    tv_stockData.setTextColor(colors.colorWhite);
                } else {
                    tv_stockChart.setClickable(false);
                    tv_stockChart.setTextColor(colors.colorSelectedOrange);
                    tv_stockData.setClickable(true);
                    tv_stockData.setTextColor(colors.colorGray);
                }

                curChosen = 1;
                findViewById(R.id.stockDataPage_data).setVisibility(View.GONE);
                findViewById(R.id.stockDataPage_chart).setVisibility(View.VISIBLE);
            }
        });
        tv_stockData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                    tv_stockData.setClickable(false);
                    tv_stockData.setTextColor(colors.colorSelectedCyan);
                    tv_stockChart.setClickable(true);
                    tv_stockChart.setTextColor(colors.colorWhite);
                } else {
                    tv_stockData.setClickable(false);
                    tv_stockData.setTextColor(colors.colorSelectedOrange);
                    tv_stockChart.setClickable(true);
                    tv_stockChart.setTextColor(colors.colorGray);
                }

                curChosen = 0;
                findViewById(R.id.stockDataPage_chart).setVisibility(View.GONE);
                findViewById(R.id.stockDataPage_data).setVisibility(View.VISIBLE);
            }
        });
        tv_predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(StockDataPage.this,PredictorPage.class);
                intent.putExtra("ts_code",ts_code);
                startActivity(intent);
            }
        });/*
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        tv_favor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        isFavored = isFavored==1?0:1;
                        if(isFavored==1) {
                            tv_favor.setText("取消自选");
                            try {
                                if((favorsApi.AddFavors(userId,getCurTime.curTime(),ts_code)).getCode()==0) {
                                    Looper.prepare();
                                    Toast.makeText(StockDataPage.this, "操作错误", Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            tv_favor.setText("加自选");
                            try {
                                if((favorsApi.DeleteFavors(userId,ts_code)).getCode()==0) {
                                    Looper.prepare();
                                    Toast.makeText(StockDataPage.this, "操作错误", Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };

                // 启动线程
                thread.start();
            }
        });
        tv_incomePart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                    tv_incomePart.setClickable(false);
                    tv_incomePart.setTextColor(colors.colorSelectedCyan);
                    tv_balancePart.setClickable(true);
                    tv_balancePart.setTextColor(colors.colorWhite);
                    tv_cashflowPart.setClickable(true);
                    tv_cashflowPart.setTextColor(colors.colorWhite);
                } else {
                    tv_incomePart.setClickable(false);
                    tv_incomePart.setTextColor(colors.colorSelectedOrange);
                    tv_balancePart.setClickable(true);
                    tv_balancePart.setTextColor(colors.colorGray);
                    tv_cashflowPart.setClickable(true);
                    tv_cashflowPart.setTextColor(colors.colorGray);
                }
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        IncomeInit();
                    }
                };

                // 启动线程
                thread.start();
            }
        });
        tv_balancePart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                    tv_balancePart.setClickable(false);
                    tv_balancePart.setTextColor(colors.colorSelectedCyan);
                    tv_incomePart.setClickable(true);
                    tv_incomePart.setTextColor(colors.colorWhite);
                    tv_cashflowPart.setClickable(true);
                    tv_cashflowPart.setTextColor(colors.colorWhite);
                }else {
                    tv_balancePart.setClickable(false);
                    tv_balancePart.setTextColor(colors.colorSelectedOrange);
                    tv_incomePart.setClickable(true);
                    tv_incomePart.setTextColor(colors.colorGray);
                    tv_cashflowPart.setClickable(true);
                    tv_cashflowPart.setTextColor(colors.colorGray);
                }
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        BalanceInit();
                    }
                };

                // 启动线程
                thread.start();
            }
        });
        tv_cashflowPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userSettings != null && (int) userSettings.get("isDark") == 1) {
                    tv_cashflowPart.setClickable(false);
                    tv_cashflowPart.setTextColor(colors.colorSelectedCyan);
                    tv_balancePart.setClickable(true);
                    tv_balancePart.setTextColor(colors.colorWhite);
                    tv_incomePart.setClickable(true);
                    tv_incomePart.setTextColor(colors.colorWhite);
                } else {
                    tv_cashflowPart.setClickable(false);
                    tv_cashflowPart.setTextColor(colors.colorSelectedOrange);
                    tv_balancePart.setClickable(true);
                    tv_balancePart.setTextColor(colors.colorGray);
                    tv_incomePart.setClickable(true);
                    tv_incomePart.setTextColor(colors.colorGray);
                }

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        CashflowInit();
                    }
                };

                // 启动线程
                thread.start();
            }
        });
        /*
        tv_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_news.setClickable(false);
                tv_news.setTextColor(colors.colorSelectedOrange);
                tv_capital.setClickable(true);
                tv_capital.setTextColor(colors.colorNotSelected);
                tv_handicap.setClickable(true);
                tv_handicap.setTextColor(colors.colorNotSelected);
                tv_announcement.setClickable(true);
                tv_announcement.setTextColor(colors.colorNotSelected);
                tv_finance.setClickable(true);
                tv_finance.setTextColor(colors.colorNotSelected);
                tv_briefInfo.setClickable(true);
                tv_briefInfo.setTextColor(colors.colorNotSelected);
            }
        });
        tv_capital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_capital.setClickable(false);
                tv_capital.setTextColor(colors.colorSelectedOrange);
                tv_news.setClickable(true);
                tv_news.setTextColor(colors.colorNotSelected);
                tv_handicap.setClickable(true);
                tv_handicap.setTextColor(colors.colorNotSelected);
                tv_announcement.setClickable(true);
                tv_announcement.setTextColor(colors.colorNotSelected);
                tv_finance.setClickable(true);
                tv_finance.setTextColor(colors.colorNotSelected);
                tv_briefInfo.setClickable(true);
                tv_briefInfo.setTextColor(colors.colorNotSelected);
                CashflowInit();
            }
        });
        tv_handicap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_handicap.setClickable(false);
                tv_handicap.setTextColor(colors.colorSelectedOrange);
                tv_capital.setClickable(true);
                tv_capital.setTextColor(colors.colorNotSelected);
                tv_news.setClickable(true);
                tv_news.setTextColor(colors.colorNotSelected);
                tv_announcement.setClickable(true);
                tv_announcement.setTextColor(colors.colorNotSelected);
                tv_finance.setClickable(true);
                tv_finance.setTextColor(colors.colorNotSelected);
                tv_briefInfo.setClickable(true);
                tv_briefInfo.setTextColor(colors.colorNotSelected);
                BalanceInit();
            }
        });
        tv_announcement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_announcement.setClickable(false);
                tv_announcement.setTextColor(colors.colorSelectedOrange);
                tv_capital.setClickable(true);
                tv_capital.setTextColor(colors.colorNotSelected);
                tv_handicap.setClickable(true);
                tv_handicap.setTextColor(colors.colorNotSelected);
                tv_news.setClickable(true);
                tv_news.setTextColor(colors.colorNotSelected);
                tv_finance.setClickable(true);
                tv_finance.setTextColor(colors.colorNotSelected);
                tv_briefInfo.setClickable(true);
                tv_briefInfo.setTextColor(colors.colorNotSelected);
            }
        });
        tv_finance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_finance.setClickable(false);
                tv_finance.setTextColor(colors.colorSelectedOrange);
                tv_capital.setClickable(true);
                tv_capital.setTextColor(colors.colorNotSelected);
                tv_handicap.setClickable(true);
                tv_handicap.setTextColor(colors.colorNotSelected);
                tv_announcement.setClickable(true);
                tv_announcement.setTextColor(colors.colorNotSelected);
                tv_news.setClickable(true);
                tv_news.setTextColor(colors.colorNotSelected);
                tv_briefInfo.setClickable(true);
                tv_briefInfo.setTextColor(colors.colorNotSelected);
                IncomeInit();
            }
        });
        tv_briefInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_briefInfo.setClickable(false);
                tv_briefInfo.setTextColor(colors.colorSelectedOrange);
                tv_capital.setClickable(true);
                tv_capital.setTextColor(colors.colorNotSelected);
                tv_handicap.setClickable(true);
                tv_handicap.setTextColor(colors.colorNotSelected);
                tv_announcement.setClickable(true);
                tv_announcement.setTextColor(colors.colorNotSelected);
                tv_finance.setClickable(true);
                tv_finance.setTextColor(colors.colorNotSelected);
                tv_news.setClickable(true);
                tv_news.setTextColor(colors.colorNotSelected);
            }
        });*/
    }
    private String FormulatingNumbers(Float a) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###.#");
        if(a >= 1e4) {
            return decimalFormat.format(a/(float)10000) + "万";
        } else if (a >= 1e8) {
            return decimalFormat.format(a/(float)100000000) + "亿";
        } else return decimalFormat.format(a);
    }
    public TextView TvInit(String text) {
        TextView item = new TextView(StockDataPage.this);
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            item.setBackgroundColor(lineCount%2 == 1?colors.colorGray:colors.colorSuperGray);
            item.setTextColor(colors.colorWhite);
        } else {
            item.setBackgroundColor(lineCount%2 == 1?colors.colorWhiteisher:colors.colorWhite);
            item.setTextColor(colors.colorGray);
        }
        lineCount++;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(60, 10, 0 , 0);
        item.setLayoutParams(layoutParams);
        item.setTextSize(20);
        item.setText(text);
        return item;
    }
    public void CashflowInit() {
        lineCount = 0;
        try {
            com.example.finance.common.R<Object> res = null;
            res = stockApi.GetCashflow(ts_code);
            if(res.getCode()==0) {
                Looper.prepare();
                Toast.makeText(StockDataPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                Looper.loop();
                return;
            } else {
                detailData = (Map<String, Object>) res.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ll_details.removeAllViews();
        DecimalFormat decimalFormat = new DecimalFormat("###,###.#");

        ll_details.addView(TvInit("净利润: "+decimalFormat.format(detailData.get("netProfit"))+"￥"));
        ll_details.addView(TvInit("财务费用: "+decimalFormat.format(detailData.get("finanExp"))+"￥"));
        ll_details.addView(TvInit("销售商品、提供劳务收到的现金: "+decimalFormat.format(detailData.get("cFrSaleSg"))+"￥"));
        ll_details.addView(TvInit("收到的税费返还: "+decimalFormat.format(detailData.get("recpTaxRends"))+"￥"));
        ll_details.addView(TvInit("客户存款和同业存放款项净增加额: "+decimalFormat.format(detailData.get("nDeposIncrFi"))+"￥"));
        ll_details.addView(TvInit("向中央银行借款净增加额: "+decimalFormat.format(detailData.get("nIncrLoansCb"))+"￥"));
        ll_details.addView(TvInit("向其他金融机构拆入资金净增加额: "+decimalFormat.format(detailData.get("nIncBorrOthFi"))+"￥"));
        ll_details.addView(TvInit("收到原保险合同保费取得的现金: "+decimalFormat.format(detailData.get("premFrOrigContr"))+"￥"));
        ll_details.addView(TvInit("保户储金净增加额: "+decimalFormat.format(detailData.get("nIncrInsuredDep"))+"￥"));
        ll_details.addView(TvInit("收到再保业务现金净额: "+decimalFormat.format(detailData.get("nReinsurPrem"))+"￥"));
        ll_details.addView(TvInit("处置交易性金融资产净增加额: "+decimalFormat.format(detailData.get("nIncrDispTfa"))+"￥"));
        ll_details.addView(TvInit("收取利息和手续费净增加额: "+decimalFormat.format(detailData.get("ifcCashIncr"))+"￥"));
        ll_details.addView(TvInit("处置可供出售金融资产净增加额: "+decimalFormat.format(detailData.get("nIncrDispFaas"))+"￥"));
        ll_details.addView(TvInit("拆入资金净增加额: "+decimalFormat.format(detailData.get("nIncrLoansOthBank"))+"￥"));
        ll_details.addView(TvInit("回购业务资金净增加额: "+decimalFormat.format(detailData.get("nCapIncrRepur"))+"￥"));
        ll_details.addView(TvInit("收到其他与经营活动有关的现金: "+decimalFormat.format(detailData.get("cFrOthOperateA"))+"￥"));
        ll_details.addView(TvInit("经营活动现金流入小计: "+decimalFormat.format(detailData.get("cInfFrOperateA"))+"￥"));
        ll_details.addView(TvInit("购买商品、接受劳务支付的现金: "+decimalFormat.format(detailData.get("cPaidGoodsS"))+"￥"));
        ll_details.addView(TvInit("支付给职工以及为职工支付的现金: "+decimalFormat.format(detailData.get("cPaidToForEmpl"))+"￥"));
        ll_details.addView(TvInit("支付的各项税费: "+decimalFormat.format(detailData.get("cPaidForTaxes"))+"￥"));
        ll_details.addView(TvInit("客户贷款及垫款净增加额: "+decimalFormat.format(detailData.get("nIncrCltLoanAdv"))+"￥"));
        ll_details.addView(TvInit("存放央行和同业款项净增加额: "+decimalFormat.format(detailData.get("nIncrDepCbob"))+"￥"));
        ll_details.addView(TvInit("支付原保险合同赔付款项的现金: "+decimalFormat.format(detailData.get("cPayClaimsOrigInco"))+"￥"));
        ll_details.addView(TvInit("支付手续费的现金: "+decimalFormat.format(detailData.get("payHandlingChrg"))+"￥"));
        ll_details.addView(TvInit("支付保单红利的现金: "+decimalFormat.format(detailData.get("payCommInsurPlcy"))+"￥"));
        ll_details.addView(TvInit("支付其他与经营活动有关的现金: "+decimalFormat.format(detailData.get("othCashPayOperAct"))+"￥"));
        ll_details.addView(TvInit("经营活动现金流出小计: "+decimalFormat.format(detailData.get("stCashOutAct"))+"￥"));
        ll_details.addView(TvInit("经营活动产生的现金流量净额: "+decimalFormat.format(detailData.get("nCashflowAct"))+"￥"));
        ll_details.addView(TvInit("收到其他与投资活动有关的现金: "+decimalFormat.format(detailData.get("othRecpRalInvAct"))+"￥"));
        ll_details.addView(TvInit("收回投资收到的现金: "+decimalFormat.format(detailData.get("cDispWithdrwlInvest"))+"￥"));
        ll_details.addView(TvInit("取得投资收益收到的现金: "+decimalFormat.format(detailData.get("cRecpReturnInvest"))+"￥"));
        ll_details.addView(TvInit("处置固定资产、无形资产和其他长期资产收回的现金净额: "+decimalFormat.format(detailData.get("nRecpDispFiolta"))+"￥"));
        ll_details.addView(TvInit("处置子公司及其他营业单位收到的现金净额: "+decimalFormat.format(detailData.get("nRecpDispSobu"))+"￥"));
        ll_details.addView(TvInit("投资活动现金流入小计: "+decimalFormat.format(detailData.get("stotInflowsInvAct"))+"￥"));
        ll_details.addView(TvInit("购建固定资产、无形资产和其他长期资产支付的现金: "+decimalFormat.format(detailData.get("cPayAcqConstFiolta"))+"￥"));
        ll_details.addView(TvInit("投资支付的现金: "+decimalFormat.format(detailData.get("cPaidInvest"))+"￥"));
        ll_details.addView(TvInit("取得子公司及其他营业单位支付的现金净额: "+decimalFormat.format(detailData.get("nDispSubsOthBiz"))+"￥"));
        ll_details.addView(TvInit("支付其他与投资活动有关的现金: "+decimalFormat.format(detailData.get("othPayRalInvAct"))+"￥"));
        ll_details.addView(TvInit("质押贷款净增加额: "+decimalFormat.format(detailData.get("nIncrPledgeLoan"))+"￥"));
        ll_details.addView(TvInit("投资活动现金流出小计: "+decimalFormat.format(detailData.get("stotOutInvAct"))+"￥"));
        ll_details.addView(TvInit("投资活动产生的现金流量净额: "+decimalFormat.format(detailData.get("nCashflowInvAct"))+"￥"));
        ll_details.addView(TvInit("取得借款收到的现金: "+decimalFormat.format(detailData.get("cRecpBorrow"))+"￥"));
        ll_details.addView(TvInit("发行债券收到的现金: "+decimalFormat.format(detailData.get("procIssueBonds"))+"￥"));
        ll_details.addView(TvInit("收到其他与筹资活动有关的现金: "+decimalFormat.format(detailData.get("othCashRecpRalFncAct"))+"￥"));
        ll_details.addView(TvInit("筹资活动现金流入小计: "+decimalFormat.format(detailData.get("stotCashInFncAct"))+"￥"));
        ll_details.addView(TvInit("企业自由现金流量: "+decimalFormat.format(detailData.get("freeCashflow"))+"￥"));
        ll_details.addView(TvInit("偿还债务支付的现金: "+decimalFormat.format(detailData.get("cPrepayAmtBorr"))+"￥"));
        ll_details.addView(TvInit("分配股利、利润或偿付利息支付的现金: "+decimalFormat.format(detailData.get("cPayDistDpcpIntExp"))+"￥"));
        ll_details.addView(TvInit("其中:子公司支付给少数股东的股利、利润: "+decimalFormat.format(detailData.get("inclDvdProfitPaidScMs"))+"￥"));
        ll_details.addView(TvInit("支付其他与筹资活动有关的现金: "+decimalFormat.format(detailData.get("othCashpayRalFncAct"))+"￥"));
        ll_details.addView(TvInit("筹资活动现金流出小计: "+decimalFormat.format(detailData.get("stotCashoutFncAct"))+"￥"));
        ll_details.addView(TvInit("筹资活动产生的现金流量净额: "+decimalFormat.format(detailData.get("nCashFlowsFncAct"))+"￥"));
        ll_details.addView(TvInit("汇率变动对现金的影响: "+decimalFormat.format(detailData.get("effFxFluCash"))+"￥"));
        ll_details.addView(TvInit("现金及现金等价物净增加额: "+decimalFormat.format(detailData.get("nIncrCashCashEqu"))+"￥"));
        ll_details.addView(TvInit("期初现金及现金等价物余额: "+decimalFormat.format(detailData.get("cCashEquBegPeriod"))+"￥"));
        ll_details.addView(TvInit("期末现金及现金等价物余额: "+decimalFormat.format(detailData.get("cCashEquEndPeriod"))+"￥"));
        ll_details.addView(TvInit("吸收投资收到的现金: "+decimalFormat.format(detailData.get("cRecpCapContrib"))+"￥"));
        ll_details.addView(TvInit("其中:子公司吸收少数股东投资收到的现金: "+decimalFormat.format(detailData.get("inclCashRecSaims"))+"￥"));
        ll_details.addView(TvInit("未确认投资损失: "+decimalFormat.format(detailData.get("unconInvestLoss"))+"￥"));
        ll_details.addView(TvInit("加:资产减值准备: "+decimalFormat.format(detailData.get("provDeprAssets"))+"￥"));
        ll_details.addView(TvInit("固定资产折旧、油气资产折耗、生产性生物资产折旧: "+decimalFormat.format(detailData.get("deprFaCogaDpba"))+"￥"));
        ll_details.addView(TvInit("无形资产摊销: "+decimalFormat.format(detailData.get("amortIntangAssets"))+"￥"));
        ll_details.addView(TvInit("长期待摊费用摊销: "+decimalFormat.format(detailData.get("ltAmortDeferredExp"))+"￥"));
        ll_details.addView(TvInit("待摊费用减少: "+decimalFormat.format(detailData.get("decrDeferredExp"))+"￥"));
        ll_details.addView(TvInit("预提费用增加: "+decimalFormat.format(detailData.get("incrAccExp"))+"￥"));
        ll_details.addView(TvInit("处置固定、无形资产和其他长期资产的损失: "+decimalFormat.format(detailData.get("lossDispFiolta"))+"￥"));
        ll_details.addView(TvInit("固定资产报废损失: "+decimalFormat.format(detailData.get("lossScrFa"))+"￥"));
        ll_details.addView(TvInit("公允价值变动损失: "+decimalFormat.format(detailData.get("lossFvChg"))+"￥"));
        ll_details.addView(TvInit("投资损失: "+decimalFormat.format(detailData.get("investLoss"))+"￥"));
        ll_details.addView(TvInit("递延所得税资产减少: "+decimalFormat.format(detailData.get("decrDefIncTaxAssets"))+"￥"));
        ll_details.addView(TvInit("递延所得税负债增加: "+decimalFormat.format(detailData.get("incrDefIncTaxLiab"))+"￥"));
        ll_details.addView(TvInit("存货的减少: "+decimalFormat.format(detailData.get("decrInventories"))+"￥"));
        ll_details.addView(TvInit("经营性应收项目的减少: "+decimalFormat.format(detailData.get("decrOperPayable"))+"￥"));
        ll_details.addView(TvInit("经营性应付项目的增加: "+decimalFormat.format(detailData.get("incrOperPayable"))+"￥"));
        ll_details.addView(TvInit("其他: "+decimalFormat.format(detailData.get("others"))+"￥"));
        ll_details.addView(TvInit("经营活动产生的现金流量净额(间接法): "+decimalFormat.format(detailData.get("imNetCashflowOperAct"))+"￥"));
        ll_details.addView(TvInit("债务转为资本: "+decimalFormat.format(detailData.get("convDebtIntoCap"))+"￥"));
        ll_details.addView(TvInit("一年内到期的可转换公司债券: "+decimalFormat.format(detailData.get("convCopbondsDueWithin1y"))+"￥"));
        ll_details.addView(TvInit("融资租入固定资产: "+decimalFormat.format(detailData.get("faFncLeases"))+"￥"));
        ll_details.addView(TvInit("现金及现金等价物净增加额(间接法): "+decimalFormat.format(detailData.get("imNIncrCashEqu"))+"￥"));
        ll_details.addView(TvInit("拆出资金净增加额: "+decimalFormat.format(detailData.get("netDismCapitalAdd"))+"￥"));
        ll_details.addView(TvInit("代理买卖证券收到的现金净额(元): "+decimalFormat.format(detailData.get("netCashReceSec"))+"￥"));
        ll_details.addView(TvInit("信用减值损失: "+decimalFormat.format(detailData.get("creditImpaLoss"))+"￥"));
        ll_details.addView(TvInit("使用权资产折旧: "+decimalFormat.format(detailData.get("useRightAssetDep"))+"￥"));
        ll_details.addView(TvInit("其他资产减值损失: "+decimalFormat.format(detailData.get("othLossAsset"))+"￥"));
        ll_details.addView(TvInit("现金的期末余额: "+decimalFormat.format(detailData.get("endBalCash"))+"￥"));
        ll_details.addView(TvInit("减:现金的期初余额: "+decimalFormat.format(detailData.get("begBalCash"))+"￥"));

        ll_details.addView(TvInit("减:现金等价物的期初余额: "+decimalFormat.format(detailData.get("begBalCashEqu"))+"￥"));

        ll_details.addView(TvInit("公告日期: " + detailData.get("annDate")));
        ll_details.addView(TvInit("报告期: " + detailData.get("endDate")));
    }

    public void BalanceInit() {
        lineCount = 0;
        try {
            com.example.finance.common.R<Object> res = null;
            res = stockApi.GetBalance(ts_code);
            if(res.getCode()==0) {
                Looper.prepare();
                Toast.makeText(StockDataPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                Looper.loop();
                return;
            } else {
                detailData = (Map<String, Object>) res.getData();
                System.out.println(detailData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ll_details.removeAllViews();
        DecimalFormat decimalFormat = new DecimalFormat("###,###.#");

        ll_details.addView(TvInit("期末总股本: "+decimalFormat.format(detailData.get("totalShare"))+"￥"));
        ll_details.addView(TvInit("资本公积金: " + decimalFormat.format(detailData.get("capRese"))+"￥"));
        ll_details.addView(TvInit("未分配利润: " + decimalFormat.format(detailData.get("undistrPorfit"))+"￥"));
        ll_details.addView(TvInit("盈余公积金: " + decimalFormat.format(detailData.get("surplusRese"))+"￥"));
        ll_details.addView(TvInit("专项储备: " + decimalFormat.format(detailData.get("specialRese"))+"￥"));
        ll_details.addView(TvInit("货币资金: " + decimalFormat.format(detailData.get("moneyCap"))+"￥"));
        ll_details.addView(TvInit("交易性金融资产: " + decimalFormat.format(detailData.get("tradAsset"))+"￥"));
        ll_details.addView(TvInit("应收票据: " + decimalFormat.format(detailData.get("notesReceiv"))+"￥"));
        ll_details.addView(TvInit("应收账款: " + decimalFormat.format(detailData.get("accountsReceiv"))+"￥"));
        ll_details.addView(TvInit("其他应收款: " + decimalFormat.format(detailData.get("othReceiv"))+"￥"));
        ll_details.addView(TvInit("预付款项: " + decimalFormat.format(detailData.get("prepayment"))+"￥"));
        ll_details.addView(TvInit("应收股利: " + decimalFormat.format(detailData.get("divReceiv"))+"￥"));
        ll_details.addView(TvInit("应收利息: " + decimalFormat.format(detailData.get("intReceiv"))+"￥"));
        ll_details.addView(TvInit("存货: " + decimalFormat.format(detailData.get("inventories"))+"￥"));
        ll_details.addView(TvInit("待摊费用: " + decimalFormat.format(detailData.get("amorExp"))+"￥"));
        ll_details.addView(TvInit("一年内到期的非流动资产: " + decimalFormat.format(detailData.get("ncaWithin1y"))+"￥"));
        ll_details.addView(TvInit("结算备付金: " + decimalFormat.format(detailData.get("settRsrv"))+"￥"));
        ll_details.addView(TvInit("拆出资金: " + decimalFormat.format(detailData.get("loantoOthBankFi"))+"￥"));
        ll_details.addView(TvInit("应收保费: " + decimalFormat.format(detailData.get("premiumReceiv"))+"￥"));
        ll_details.addView(TvInit("应收分保账款: " + decimalFormat.format(detailData.get("reinsurReceiv"))+"￥"));
        ll_details.addView(TvInit("应收分保合同准备金: " + decimalFormat.format(detailData.get("reinsurResReceiv"))+"￥"));
        ll_details.addView(TvInit("买入返售金融资产: " + decimalFormat.format(detailData.get("purResaleFa"))+"￥"));
        ll_details.addView(TvInit("其他流动资产: " + decimalFormat.format(detailData.get("othCurAssets"))+"￥"));
        ll_details.addView(TvInit("流动资产合计: " + decimalFormat.format(detailData.get("totalCurAssets"))+"￥"));
        ll_details.addView(TvInit("可供出售金融资产: " + decimalFormat.format(detailData.get("faAvailForSale"))+"￥"));
        ll_details.addView(TvInit("持有至到期投资: " + decimalFormat.format(detailData.get("htmInvest"))+"￥"));
        ll_details.addView(TvInit("长期股权投资: " + decimalFormat.format(detailData.get("ltEqtInvest"))+"￥"));
        ll_details.addView(TvInit("投资性房地产: " + decimalFormat.format(detailData.get("investRealEstate"))+"￥"));
        ll_details.addView(TvInit("定期存款: " + decimalFormat.format(detailData.get("timeDeposits"))+"￥"));
        ll_details.addView(TvInit("其他资产: " + decimalFormat.format(detailData.get("othAssets"))+"￥"));
        ll_details.addView(TvInit("长期应收款: " + decimalFormat.format(detailData.get("ltRec"))+"￥"));
        ll_details.addView(TvInit("固定资产: " + decimalFormat.format(detailData.get("fixAssets"))+"￥"));
        ll_details.addView(TvInit("在建工程: " + decimalFormat.format(detailData.get("cip"))+"￥"));
        ll_details.addView(TvInit("工程物资: " + decimalFormat.format(detailData.get("constMaterials"))+"￥"));
        ll_details.addView(TvInit("固定资产清理: " + decimalFormat.format(detailData.get("fixedAssetsDisp"))+"￥"));
        ll_details.addView(TvInit("生产性生物资产: " + decimalFormat.format(detailData.get("producBioAssets"))+"￥"));
        ll_details.addView(TvInit("油气资产: " + decimalFormat.format(detailData.get("oilAndGasAssets"))+"￥"));
        ll_details.addView(TvInit("无形资产: " + decimalFormat.format(detailData.get("intanAssets"))+"￥"));
        ll_details.addView(TvInit("研发支出: " + decimalFormat.format(detailData.get("rAndD"))+"￥"));
        ll_details.addView(TvInit("商誉: " + decimalFormat.format(detailData.get("goodwill"))+"￥"));
        ll_details.addView(TvInit("长期待摊费用: " + decimalFormat.format(detailData.get("ltAmorExp"))+"￥"));
        ll_details.addView(TvInit("递延所得税资产: " + decimalFormat.format(detailData.get("deferTaxAssets"))+"￥"));
        ll_details.addView(TvInit("发放贷款及垫款: " + decimalFormat.format(detailData.get("decrInDisbur"))+"￥"));
        ll_details.addView(TvInit("其他非流动资产: " + decimalFormat.format(detailData.get("othNca"))+"￥"));
        ll_details.addView(TvInit("非流动资产合计: " + decimalFormat.format(detailData.get("totalNca"))+"￥"));
        ll_details.addView(TvInit("现金及存放中央银行款项: " + decimalFormat.format(detailData.get("cashReserCb"))+"￥"));
        ll_details.addView(TvInit("存放同业和其它金融机构款项: " + decimalFormat.format(detailData.get("deposInOthBfi"))+"￥"));
        ll_details.addView(TvInit("贵金属: " + decimalFormat.format(detailData.get("precMetals"))+"￥"));
        ll_details.addView(TvInit("衍生金融资产: " + decimalFormat.format(detailData.get("derivAssets"))+"￥"));
        ll_details.addView(TvInit("应收分保未到期责任准备金: " + decimalFormat.format(detailData.get("rrReinsUnePrem"))+"￥"));
        ll_details.addView(TvInit("应收分保未决赔款准备金: " + decimalFormat.format(detailData.get("rrReinsOutstdCla"))+"￥"));
        ll_details.addView(TvInit("应收分保寿险责任准备金: " + decimalFormat.format(detailData.get("rrReinsLinsLiab"))+"￥"));
        ll_details.addView(TvInit("应收分保长期健康险责任准备金: " + decimalFormat.format(detailData.get("rrReinsLthinsLiab"))+"￥"));
        ll_details.addView(TvInit("存出保证金: " + decimalFormat.format(detailData.get("refundDepos"))+"￥"));
        ll_details.addView(TvInit("保户质押贷款: " + decimalFormat.format(detailData.get("phPledgeLoans"))+"￥"));
        ll_details.addView(TvInit("存出资本保证金: " + decimalFormat.format(detailData.get("refundCapDepos"))+"￥"));
        ll_details.addView(TvInit("独立账户资产: " + decimalFormat.format(detailData.get("indepAcctAssets"))+"￥"));
        ll_details.addView(TvInit("其中：客户资金存款: " + decimalFormat.format(detailData.get("clientDepos"))+"￥"));
        ll_details.addView(TvInit("其中：客户备付金: " + decimalFormat.format(detailData.get("clientProv"))+"￥"));
        ll_details.addView(TvInit("其中:交易席位费: " + decimalFormat.format(detailData.get("transacSeatFee"))+"￥"));
        ll_details.addView(TvInit("应收款项类投资: " + decimalFormat.format(detailData.get("investAsReceiv"))+"￥"));
        ll_details.addView(TvInit("资产总计: " + decimalFormat.format(detailData.get("totalAssets"))+"￥"));
        ll_details.addView(TvInit("长期借款: " + decimalFormat.format(detailData.get("ltBorr"))+"￥"));
        ll_details.addView(TvInit("短期借款: " + decimalFormat.format(detailData.get("stBorr"))+"￥"));
        ll_details.addView(TvInit("向中央银行借款: " + decimalFormat.format(detailData.get("cbBorr"))+"￥"));
        ll_details.addView(TvInit("吸收存款及同业存放: " + decimalFormat.format(detailData.get("deposIbDeposits"))+"￥"));
        ll_details.addView(TvInit("拆入资金: " + decimalFormat.format(detailData.get("loanOthBank"))+"￥"));
        ll_details.addView(TvInit("交易性金融负债: " + decimalFormat.format(detailData.get("tradingFl"))+"￥"));
        ll_details.addView(TvInit("应付票据: " + decimalFormat.format(detailData.get("notesPayable"))+"￥"));
        ll_details.addView(TvInit("应付账款: " + decimalFormat.format(detailData.get("acctPayable"))+"￥"));
        ll_details.addView(TvInit("预收款项: " + decimalFormat.format(detailData.get("advReceipts"))+"￥"));
        ll_details.addView(TvInit("卖出回购金融资产款: " + decimalFormat.format(detailData.get("soldForRepurFa"))+"￥"));
        ll_details.addView(TvInit("应付手续费及佣金: " + decimalFormat.format(detailData.get("commPayable"))+"￥"));
        ll_details.addView(TvInit("应付职工薪酬: " + decimalFormat.format(detailData.get("payrollPayable"))+"￥"));
        ll_details.addView(TvInit("应交税费: " + decimalFormat.format(detailData.get("taxesPayable"))+"￥"));
        ll_details.addView(TvInit("应付利息: " + decimalFormat.format(detailData.get("intPayable"))+"￥"));
        ll_details.addView(TvInit("应付股利: " + decimalFormat.format(detailData.get("divPayable"))+"￥"));
        ll_details.addView(TvInit("其他应付款: " + decimalFormat.format(detailData.get("othPayable"))+"￥"));
        ll_details.addView(TvInit("预提费用: " + decimalFormat.format(detailData.get("accExp"))+"￥"));
        ll_details.addView(TvInit("递延收益: " + decimalFormat.format(detailData.get("deferredInc"))+"￥"));
        ll_details.addView(TvInit("应付短期债券: " + decimalFormat.format(detailData.get("stBondsPayable"))+"￥"));
        ll_details.addView(TvInit("应付分保账款: " + decimalFormat.format(detailData.get("payableToReinsurer"))+"￥"));
        ll_details.addView(TvInit("保险合同准备金: " + decimalFormat.format(detailData.get("rsrvInsurCont"))+"￥"));
        ll_details.addView(TvInit("代理买卖证券款: " + decimalFormat.format(detailData.get("actingTradingSec"))+"￥"));
        ll_details.addView(TvInit("代理承销证券款: " + decimalFormat.format(detailData.get("actingUwSec"))+"￥"));
        ll_details.addView(TvInit("一年内到期的非流动负债: " + decimalFormat.format(detailData.get("nonCurLiabDue1y"))+"￥"));
        ll_details.addView(TvInit("其他流动负债: " + decimalFormat.format(detailData.get("othCurLiab"))+"￥"));
        ll_details.addView(TvInit("流动负债合计: " + decimalFormat.format(detailData.get("totalCurLiab"))+"￥"));
        ll_details.addView(TvInit("应付债券: " + decimalFormat.format(detailData.get("bondPayable"))+"￥"));
        ll_details.addView(TvInit("长期应付款: " + decimalFormat.format(detailData.get("ltPayable"))+"￥"));
        ll_details.addView(TvInit("专项应付款: " + decimalFormat.format(detailData.get("specificPayables"))+"￥"));
        ll_details.addView(TvInit("预计负债: " + decimalFormat.format(detailData.get("estimatedLiab"))+"￥"));
        ll_details.addView(TvInit("递延所得税负债: " + decimalFormat.format(detailData.get("deferTaxLiab"))+"￥"));
        ll_details.addView(TvInit("递延收益-非流动负债: " + decimalFormat.format(detailData.get("deferIncNonCurLiab"))+"￥"));
        ll_details.addView(TvInit("其他非流动负债: " + decimalFormat.format(detailData.get("othNcl"))+"￥"));
        ll_details.addView(TvInit("非流动负债合计: " + decimalFormat.format(detailData.get("totalNcl"))+"￥"));
        ll_details.addView(TvInit("同业和其它金融机构存放款项: " + decimalFormat.format(detailData.get("deposOthBfi"))+"￥"));
        ll_details.addView(TvInit("衍生金融负债: " + decimalFormat.format(detailData.get("derivLiab"))+"￥"));
        ll_details.addView(TvInit("吸收存款: " + decimalFormat.format(detailData.get("depos"))+"￥"));
        ll_details.addView(TvInit("代理业务负债: " + decimalFormat.format(detailData.get("agencyBusLiab"))+"￥"));
        ll_details.addView(TvInit("其他负债: " + decimalFormat.format(detailData.get("othLiab"))+"￥"));
        ll_details.addView(TvInit("预收保费: " + decimalFormat.format(detailData.get("premReceivAdva"))+"￥"));
        ll_details.addView(TvInit("存入保证金: " + decimalFormat.format(detailData.get("deposReceived"))+"￥"));
        ll_details.addView(TvInit("保户储金及投资款: " + decimalFormat.format(detailData.get("phInvest"))+"￥"));
        ll_details.addView(TvInit("未到期责任准备金: " + decimalFormat.format(detailData.get("reserUnePrem"))+"￥"));
        ll_details.addView(TvInit("未决赔款准备金: " + decimalFormat.format(detailData.get("reserOutstdClaims"))+"￥"));
        ll_details.addView(TvInit("寿险责任准备金: " + decimalFormat.format(detailData.get("reserLinsLiab"))+"￥"));
        ll_details.addView(TvInit("长期健康险责任准备金: " + decimalFormat.format(detailData.get("reserLthinsLiab"))+"￥"));
        ll_details.addView(TvInit("独立账户负债: " + decimalFormat.format(detailData.get("indeptAccLiab"))+"￥"));
        ll_details.addView(TvInit("其中:质押借款: " + decimalFormat.format(detailData.get("pledgeBorr"))+"￥"));
        ll_details.addView(TvInit("应付赔付款: " + decimalFormat.format(detailData.get("indemPayable"))+"￥"));
        ll_details.addView(TvInit("应付保单红利: " + decimalFormat.format(detailData.get("policyDivPayable"))+"￥"));
        ll_details.addView(TvInit("负债合计: " + decimalFormat.format(detailData.get("totalLiab"))+"￥"));
        ll_details.addView(TvInit("减:库存股: " + decimalFormat.format(detailData.get("treasuryShare"))+"￥"));
        ll_details.addView(TvInit("一般风险准备: " + decimalFormat.format(detailData.get("ordinRiskReser"))+"￥"));
        ll_details.addView(TvInit("外币报表折算差额: " + decimalFormat.format(detailData.get("forexDiffer"))+"￥"));

        ll_details.addView(TvInit("少数股东权益: " + decimalFormat.format(detailData.get("minorityInt"))+"￥"));
        ll_details.addView(TvInit("股东权益合计(不含少数股东权益): " + decimalFormat.format(detailData.get("totalHldrEqyExcMinInt"))+"￥"));
        ll_details.addView(TvInit("股东权益合计(含少数股东权益): " + decimalFormat.format(detailData.get("totalHldrEqyIncMinInt"))+"￥"));
        ll_details.addView(TvInit("负债及股东权益总计: " + decimalFormat.format(detailData.get("totalLiabHldrEqy"))+"￥"));
        ll_details.addView(TvInit("长期应付职工薪酬: " + decimalFormat.format(detailData.get("ltPayrollPayable"))+"￥"));
        ll_details.addView(TvInit("其他综合收益: " + decimalFormat.format(detailData.get("othCompIncome"))+"￥"));
        ll_details.addView(TvInit("其他权益工具: " + decimalFormat.format(detailData.get("othEqtTools"))+"￥"));
        ll_details.addView(TvInit("其他权益工具(优先股): " + decimalFormat.format(detailData.get("othEqtToolsPShr"))+"￥"));
        ll_details.addView(TvInit("融出资金: " + decimalFormat.format(detailData.get("lendingFunds"))+"￥"));
        ll_details.addView(TvInit("应收款项: " + decimalFormat.format(detailData.get("accReceivable"))+"￥"));
        ll_details.addView(TvInit("应付短期融资款: " + decimalFormat.format(detailData.get("stFinPayable"))+"￥"));
        ll_details.addView(TvInit("应付款项: " + decimalFormat.format(detailData.get("payables"))+"￥"));
        ll_details.addView(TvInit("持有待售的资产: " + decimalFormat.format(detailData.get("hfsAssets"))+"￥"));
        ll_details.addView(TvInit("持有待售的负债: " + decimalFormat.format(detailData.get("hfsSales"))+"￥"));
        ll_details.addView(TvInit("以摊余成本计量的金融资产: " + decimalFormat.format(detailData.get("costFinAssets"))+"￥"));
        ll_details.addView(TvInit("以公允价值计量且其变动计入其他综合收益的金融资产: " + decimalFormat.format(detailData.get("fairValueFinAssets"))+"￥"));
        ll_details.addView(TvInit("在建工程(合计)(元): " + decimalFormat.format(detailData.get("cipTotal"))+"￥"));
        ll_details.addView(TvInit("其他应付款(合计)(元): " + decimalFormat.format(detailData.get("othPayTotal"))+"￥"));
        ll_details.addView(TvInit("长期应付款(合计)(元): " + decimalFormat.format(detailData.get("longPayTotal"))+"￥"));
        ll_details.addView(TvInit("债权投资(元): " + decimalFormat.format(detailData.get("debtInvest"))+"￥"));
        ll_details.addView(TvInit("其他债权投资(元): " + decimalFormat.format(detailData.get("othDebtInvest"))+"￥"));

        ll_details.addView(TvInit("公告日期: " + detailData.get("annDate")));
        ll_details.addView(TvInit("报告期: " + detailData.get("endDate")));
    }

    public void IncomeInit() {
        lineCount = 0;
        try {
            com.example.finance.common.R<Object> res = null;
            res = stockApi.GetIncome(ts_code);
            if(res.getCode()==0) {
                Looper.prepare();
                Toast.makeText(StockDataPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                Looper.loop();
                return;
            } else {
                detailData = (Map<String, Object>) res.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ll_details.removeAllViews();
        DecimalFormat decimalFormat = new DecimalFormat("###,###.#");

        ll_details.addView(TvInit("基本每股收益: " + decimalFormat.format(detailData.get("basicEps"))+"￥"));
        ll_details.addView(TvInit("稀释每股收益: " + decimalFormat.format(detailData.get("totalRevenue"))+"￥"));

        ll_details.addView(TvInit("营业收入: " + decimalFormat.format(detailData.get("revenue"))+"￥"));
        ll_details.addView(TvInit("利息收入: " + decimalFormat.format(detailData.get("intIncome"))+"￥"));
        ll_details.addView(TvInit("已赚保费: " + decimalFormat.format(detailData.get("premEarned"))+"￥"));
        ll_details.addView(TvInit("手续费及佣金收入: " + decimalFormat.format(detailData.get("commIncome"))+"￥"));
        ll_details.addView(TvInit("手续费及佣金净收入: " + decimalFormat.format(detailData.get("nCommisIncome"))+"￥"));
        ll_details.addView(TvInit("其他经营净收益: " + decimalFormat.format(detailData.get("nOthIncome"))+"￥"));
        ll_details.addView(TvInit("加:其他业务净收益: " + decimalFormat.format(detailData.get("nOthBIncome"))+"￥"));
        ll_details.addView(TvInit("保险业务收入: " + decimalFormat.format(detailData.get("premIncome"))+"￥"));
        ll_details.addView(TvInit("减:分出保费: " + decimalFormat.format(detailData.get("outPrem"))+"￥"));
        ll_details.addView(TvInit("提取未到期责任准备金: " + decimalFormat.format(detailData.get("unePremReser"))+"￥"));
        ll_details.addView(TvInit("其中:分保费收入: " + decimalFormat.format(detailData.get("reinsIncome"))+"￥"));
        ll_details.addView(TvInit("代理买卖证券业务净收入: " + decimalFormat.format(detailData.get("nSecTbIncome"))+"￥"));
        ll_details.addView(TvInit("证券承销业务净收入: " + decimalFormat.format(detailData.get("nSecUwIncome"))+"￥"));
        ll_details.addView(TvInit("受托客户资产管理业务净收入: " + decimalFormat.format(detailData.get("nAssetMgIncome"))+"￥"));
        ll_details.addView(TvInit("其他业务收入: " + decimalFormat.format(detailData.get("othBIncome"))+"￥"));
        ll_details.addView(TvInit("加:公允价值变动净收益: " + decimalFormat.format(detailData.get("fvValueChgGain"))+"￥"));
        ll_details.addView(TvInit("加:投资净收益: " + decimalFormat.format(detailData.get("investIncome"))+"￥"));
        ll_details.addView(TvInit("其中:对联营企业和合营企业的投资收益: " + decimalFormat.format(detailData.get("assInvestIncome"))+"￥"));
        ll_details.addView(TvInit("加:汇兑净收益: " + decimalFormat.format(detailData.get("forexGain"))+"￥"));
        ll_details.addView(TvInit("营业总成本: " + decimalFormat.format(detailData.get("totalCogs"))+"￥"));
        ll_details.addView(TvInit("减:营业成本: " + decimalFormat.format(detailData.get("operCost"))+"￥"));
        ll_details.addView(TvInit("减:利息支出: " + decimalFormat.format(detailData.get("intExp"))+"￥"));
        ll_details.addView(TvInit("减:手续费及佣金支出: " + decimalFormat.format(detailData.get("commExp"))+"￥"));
        ll_details.addView(TvInit("减:营业税金及附加: " + decimalFormat.format(detailData.get("bizTaxSurchg"))+"￥"));
        ll_details.addView(TvInit("减:销售费用: " + decimalFormat.format(detailData.get("sellExp"))+"￥"));
        ll_details.addView(TvInit("减:管理费用: " + decimalFormat.format(detailData.get("adminExp"))+"￥"));
        ll_details.addView(TvInit("减:财务费用: " + decimalFormat.format(detailData.get("finExp"))+"￥"));
        ll_details.addView(TvInit("减:资产减值损失: " + decimalFormat.format(detailData.get("assetsImpairLoss"))+"￥"));
        ll_details.addView(TvInit("退保金: " + decimalFormat.format(detailData.get("premRefund"))+"￥"));
        ll_details.addView(TvInit("赔付总支出: " + decimalFormat.format(detailData.get("compensPayout"))+"￥"));
        ll_details.addView(TvInit("提取保险责任准备金: " + decimalFormat.format(detailData.get("reserInsurLiab"))+"￥"));
        ll_details.addView(TvInit("保户红利支出: " + decimalFormat.format(detailData.get("divPayt"))+"￥"));
        ll_details.addView(TvInit("分保费用: " + decimalFormat.format(detailData.get("reinsExp"))+"￥"));
        ll_details.addView(TvInit("营业支出: " + decimalFormat.format(detailData.get("operExp"))+"￥"));
        ll_details.addView(TvInit("减:摊回赔付支出: " + decimalFormat.format(detailData.get("compensPayoutRefu"))+"￥"));
        ll_details.addView(TvInit("减:摊回保险责任准备金: " + decimalFormat.format(detailData.get("insurReserRefu"))+"￥"));
        ll_details.addView(TvInit("减:摊回分保费用: " + decimalFormat.format(detailData.get("reinsCostRefund"))+"￥"));
        ll_details.addView(TvInit("其他业务成本: " + decimalFormat.format(detailData.get("otherBusCost"))+"￥"));
        ll_details.addView(TvInit("营业利润: " + decimalFormat.format(detailData.get("operateProfit"))+"￥"));
        ll_details.addView(TvInit("加:营业外收入: " + decimalFormat.format(detailData.get("nonOperIncome"))+"￥"));
        ll_details.addView(TvInit("减:营业外支出: " + decimalFormat.format(detailData.get("nonOperExp"))+"￥"));
        ll_details.addView(TvInit("其中:减:非流动资产处置净损失: " + decimalFormat.format(detailData.get("ncaDisploss"))+"￥"));
        ll_details.addView(TvInit("利润总额: " + decimalFormat.format(detailData.get("totalProfit"))+"￥"));
        ll_details.addView(TvInit("所得税费用: " + decimalFormat.format(detailData.get("incomeTax"))+"￥"));
        ll_details.addView(TvInit("净利润(含少数股东损益): " + decimalFormat.format(detailData.get("nIncome"))+"￥"));
        ll_details.addView(TvInit("净利润(不含少数股东损益): " + decimalFormat.format(detailData.get("nIncomeAttrP"))+"￥"));
        ll_details.addView(TvInit("少数股东损益: " + decimalFormat.format(detailData.get("minorityGain"))+"￥"));
        ll_details.addView(TvInit("其他综合收益: " + decimalFormat.format(detailData.get("othComprIncome"))+"￥"));
        ll_details.addView(TvInit("综合收益总额: " + decimalFormat.format(detailData.get("tComprIncome"))+"￥"));
        ll_details.addView(TvInit("归属于母公司(或股东)的综合收益总额: " + decimalFormat.format(detailData.get("comprIncAttrP"))+"￥"));
        ll_details.addView(TvInit("归属于少数股东的综合收益总额: " + decimalFormat.format(detailData.get("comprIncAttrMS"))+"￥"));
        ll_details.addView(TvInit("息税前利润: " + decimalFormat.format(detailData.get("ebit"))+"￥"));
        ll_details.addView(TvInit("息税折旧摊销前利润: " + decimalFormat.format(detailData.get("ebitda"))+"￥"));
        ll_details.addView(TvInit("保险业务支出: " + decimalFormat.format(detailData.get("insuranceExp"))+"￥"));
        ll_details.addView(TvInit("年初未分配利润: " + decimalFormat.format(detailData.get("undistProfit"))+"￥"));
        ll_details.addView(TvInit("可分配利润: " + decimalFormat.format(detailData.get("distableProfit"))+"￥"));
        ll_details.addView(TvInit("研发费用: " + decimalFormat.format(detailData.get("rdExp"))+"￥"));
        ll_details.addView(TvInit("财务费用:利息费用: " + decimalFormat.format(detailData.get("finExpIntExp"))+"￥"));
        ll_details.addView(TvInit("财务费用:利息收入: " + decimalFormat.format(detailData.get("finExpIntInc"))+"￥"));
        ll_details.addView(TvInit("盈余公积转入: " + decimalFormat.format(detailData.get("transferSurplusRese"))+"￥"));
        ll_details.addView(TvInit("住房周转金转入: " + decimalFormat.format(detailData.get("transferHousingImprest"))+"￥"));
        ll_details.addView(TvInit("其他转入: " + decimalFormat.format(detailData.get("transferOth"))+"￥"));
        ll_details.addView(TvInit("调整以前年度损益: " + decimalFormat.format(detailData.get("adjLossgain"))+"￥"));
        ll_details.addView(TvInit("提取法定盈余公积: " + decimalFormat.format(detailData.get("withdraLegalSurplus"))+"￥"));
        ll_details.addView(TvInit("提取法定公益金: " + decimalFormat.format(detailData.get("withdraLegalPubfund"))+"￥"));
        ll_details.addView(TvInit("提取企业发展基金: " + decimalFormat.format(detailData.get("withdraBizDevfund"))+"￥"));
        ll_details.addView(TvInit("提取储备基金: " + decimalFormat.format(detailData.get("withdraReseFund"))+"￥"));
        ll_details.addView(TvInit("提取任意盈余公积金: " + decimalFormat.format(detailData.get("withdraOthErsu"))+"￥"));
        ll_details.addView(TvInit("职工奖金福利: " + decimalFormat.format(detailData.get("workersWelfare"))+"￥"));
        ll_details.addView(TvInit("可供股东分配的利润: " + decimalFormat.format(detailData.get("distrProfitShrhder"))+"￥"));
        ll_details.addView(TvInit("应付优先股股利: " + decimalFormat.format(detailData.get("prfsharePayableDvd"))+"￥"));
        ll_details.addView(TvInit("应付普通股股利: " + decimalFormat.format(detailData.get("comsharePayableDvd"))+"￥"));
        ll_details.addView(TvInit("转作股本的普通股股利: " + decimalFormat.format(detailData.get("capitComstockDiv"))+"￥"));

        ll_details.addView(TvInit("公告日期: " + detailData.get("annDate")));
        ll_details.addView(TvInit("报告期: " + detailData.get("endDate")));
    }
}