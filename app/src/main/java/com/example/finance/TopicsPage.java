package com.example.finance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.TopicApi;
import com.example.finance.api.UserApi;
import com.example.finance.common.Colors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopicsPage extends AppCompatActivity {

    private UserApi userApi;
    private Colors colors;
    private TopicApi topicApi;

    private String input = "";
    private List<AbsoluteLayout> topicAL;
    private List<Map<String, Object>> topicData;
    private ScrollView sv_resView;
    private LinearLayout ll_res;
    private Toolbar toolbar;
    private TextView tv_backBtn;
    /*
        private TextView tv_back;*/
    private TextView tv_pageNumber;
    private Button btn_pre;
    private Button btn_post;
    private TextView tv_noResult;
    private EditText et_topicInput;
    private TextView tv_topicSearch;

    private Map<Integer, String> IDs;
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
        setContentView(R.layout.topics_page);
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if (!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if (res.getCode() == 0) {
                    Toast.makeText(TopicsPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
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
            findViewById(R.id.topics_body).setBackgroundColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.topics_title)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.topic_search)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.topic_input)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.topic_input)).setBackgroundResource(R.drawable.edittext_bg_white);
            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_rect_3_gray);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_rect_3_gray);
            ((TextView) findViewById(R.id.pre_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.post_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.no_result)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.pageNumber)).setTextColor(colors.colorWhite);
            findViewById(R.id.topics_line).setBackgroundColor(colors.colorWhite);

        } else {
            findViewById(R.id.topics_body).setBackgroundColor(colors.colorWhite);
            ((TextView) findViewById(R.id.topics_title)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.topic_search)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.topic_input)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.topic_input)).setBackgroundResource(R.drawable.edittext_bg_gray);
            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_rect_3_white);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_rect_3_white);
            ((TextView) findViewById(R.id.pre_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.post_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.no_result)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.pageNumber)).setTextColor(colors.colorGray);
            findViewById(R.id.topics_line).setBackgroundColor(colors.colorGray);
        }
    }

    private void initViews() {
        try {
            com.example.finance.common.R<Object> res = null;
            res = topicApi.GetTopicCount(input);
            if (res.getCode() == 0) {
                Toast.makeText(TopicsPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
            } else {
                count = (Integer) res.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tv_noResult = findViewById(R.id.no_result);
        topicAL = new ArrayList<>();
        IDs = new HashMap<>();
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");/*
        tv_back = findViewById(R.id.back);
        tv_back.setTypeface(fontAwe);*/
        btn_post = findViewById(R.id.post_arrow);
        btn_pre = findViewById(R.id.pre_arrow);
        ll_res = findViewById(R.id.resultViewC);
        tv_pageNumber = findViewById(R.id.pageNumber);
        sv_resView = findViewById(R.id.resultsView);
        tv_topicSearch = findViewById(R.id.topic_search);
        tv_topicSearch.setTypeface(fontAwe);
        et_topicInput = findViewById(R.id.topic_input);

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        if (userSettings != null) changeMode((int) userSettings.get("isDark") == 1);
        ThreadPage threadPage = new ThreadPage();
        threadPage.start();
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
                if (page > Math.ceil(count / pageSize)) {
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
        et_topicInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean isEnter = event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        input = et_topicInput.getText().toString();
                        Looper.prepare();
                        Toast.makeText(TopicsPage.this, "您输入的是" + input, Toast.LENGTH_LONG).show();
                        Looper.loop();
                        try {
                            com.example.finance.common.R<Object> res = null;
                            res = topicApi.GetTopicCount(input);
                            if (res.getCode() == 0) {
                                Looper.prepare();
                                Toast.makeText(TopicsPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                                Looper.loop();
                            } else {
                                count = (Integer) res.getData();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        com.example.finance.common.R<Object> res = null;
                        try {
                            res = topicApi.GetTopicsByPage(input, page, pageSize);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (res.getCode() == 0) {
                            Looper.prepare();
                            Toast.makeText(TopicsPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        } else {
                            topicData = (List<Map<String, Object>>) res.getData();
                            page = 1;
                            ThreadPage threadPage = new ThreadPage();
                            threadPage.start();
                        }
                    }
                };

                // 启动线程
                thread.start();
                return isEnter;

            }
        });
        tv_topicSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        input = et_topicInput.getText().toString();
                        Looper.prepare();
                        Toast.makeText(TopicsPage.this, "您输入的是" + input, Toast.LENGTH_LONG).show();
                        Looper.loop();
                        try {
                            com.example.finance.common.R<Object> res = null;
                            res = topicApi.GetTopicCount(input);
                            if (res.getCode() == 0) {
                                Looper.prepare();
                                Toast.makeText(TopicsPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                                Looper.loop();
                            } else {
                                count = (Integer) res.getData();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        com.example.finance.common.R<Object> res = null;
                        try {
                            res = topicApi.GetTopicsByPage(input, page, pageSize);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (res.getCode() == 0) {
                            Looper.prepare();
                            Toast.makeText(TopicsPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                            return;
                        }
                        topicData = (List<Map<String, Object>>) res.getData();
                        page = 1;
                        ThreadPage threadPage = new ThreadPage();
                        threadPage.start();
                    }
                };

                // 启动线程
                thread.start();
            }
        });
    }

    private void topicClicked() {
        for (int i = topicAL.size() - 1; i >= 0; i--) {
            int finalI = i;
            topicAL.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(TopicsPage.this, TopicPage.class);
                    intent.putExtra("topicID", IDs.get(finalI));
                    startActivity(intent);
                }
            });
            ll_res.addView(topicAL.get(i), 0);
        }
    }

    private void pageTurn() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < topicAL.size(); i++) {
                    ll_res.removeView(topicAL.get(i));
                }
                topicAL.clear();
                IDs.clear();
                com.example.finance.common.R<Object> res = null;
                try {
                    res = topicApi.GetTopicsByPage(input, page, pageSize);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (res.getCode() == 0) {
                    Toast.makeText(TopicsPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
                    return;
                }
                topicData = (List<Map<String, Object>>) res.getData();
                tv_pageNumber.setText(page + "/" + (int) Math.ceil((float) count / (float) pageSize) + "页");
                if (topicData.size() != 0) {
                    tv_noResult.setVisibility(View.GONE);
                    btn_post.setVisibility(View.VISIBLE);
                    btn_pre.setVisibility(View.VISIBLE);
                    tv_pageNumber.setVisibility(View.VISIBLE);
                }
                else {
                    tv_noResult.setVisibility(View.VISIBLE);
                    btn_post.setVisibility(View.GONE);
                    btn_pre.setVisibility(View.GONE);
                    tv_pageNumber.setVisibility(View.GONE);
                }
                for (int i = 0; i < topicData.size(); i++) {
                    topicAL.add(addTopicResult(topicData.get(i), i));
                    IDs.put(i, "" + topicData.get(i).get("newsId"));
                }
                topicClicked();
            }
        });
    }

    private AbsoluteLayout addTopicResult(Map<String, Object> map, int i) {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        AbsoluteLayout AL = new AbsoluteLayout(TopicsPage.this);
        AL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 190));
        if (userSettings != null && (int) userSettings.get("isDark") == 1) {
            AL.setBackgroundColor(i % 2 == 1 ? colors.colorGray : colors.colorSuperGray);
        } else {
            AL.setBackgroundColor(i % 2 == 1 ? colors.colorWhiteish : colors.colorWhite);
        }

        TextView topicTitle = new TextView(TopicsPage.this);
        topicTitle.setText((String) map.get("title"));
        topicTitle.setTextSize(25);
        if (userSettings != null && (int) userSettings.get("isDark") == 1) {
            topicTitle.setTextColor(colors.colorWhite);
        } else {
            topicTitle.setTextColor(colors.colorGray);
        }
        //topicTitle.setTextColor(colors.colorGray);
        topicTitle.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT, 38, 0));
        AL.addView(topicTitle);

        TextView topicChannel = new TextView(TopicsPage.this);
        topicChannel.setText((String) map.get("channels"));
        topicChannel.setTextSize(17);
        if (userSettings != null && (int) userSettings.get("isDark") == 1) {
            topicChannel.setTextColor(colors.colorWhite);
        } else {
            topicChannel.setTextColor(colors.colorGray);
        }
        //topicChannel.setTextColor(colors.colorGray);
        topicChannel.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT, 238, 0));
        AL.addView(topicChannel);

        TextView topicDate = new TextView(TopicsPage.this);
        topicDate.setText((String) map.get("datetime"));
        topicDate.setTextSize(20);
        if (userSettings != null && (int) userSettings.get("isDark") == 1) {
            topicDate.setTextColor(colors.colorWhite);
        } else {
            topicDate.setTextColor(colors.colorGray);
        }
        //topicDate.setTextColor(colors.colorGray);
        topicDate.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT, 38, 100));
        AL.addView(topicDate);
        TextView line = new TextView(TopicsPage.this);
        if (userSettings != null && (int) userSettings.get("isDark") == 1) {
            line.setBackgroundColor(colors.colorWhite);
        } else {
            line.setBackgroundColor(colors.colorGray);
        }
        line.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT, 6, 0, 184));
        AL.addView(line);

        TextView go = new TextView(TopicsPage.this);
        go.setTextSize(27);
        go.setText(R.string.fa_chevron_right);
        go.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT, 970, 50));
        go.setTypeface(fontAwe);
        if (userSettings != null && (int) userSettings.get("isDark") == 1) {
            go.setTextColor(colors.colorWhite);
        } else {
            go.setTextColor(colors.colorGray);
        }
        AL.addView(go);

        return AL;
    }
}
