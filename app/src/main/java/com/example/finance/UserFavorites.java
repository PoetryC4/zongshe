package com.example.finance;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.FavorsApi;
import com.example.finance.api.UserApi;
import com.example.finance.common.Colors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserFavorites extends AppCompatActivity {

    private UserApi userApi;
    private Colors colors;
    private FavorsApi favorsApi;

    private String input = "";
    private List<AbsoluteLayout> favorsAL;
    private List<Map<String, Object>> favorsData;
    private ScrollView sv_resView;
    private LinearLayout ll_res;
/*
    private TextView tv_back;*/
    private TextView tv_pageNumber;
    private Button btn_pre;
    private Button btn_post;
    private TextView tv_noResult;
    private EditText et_favorsInput;
    private TextView tv_favorsSearch;
    private Toolbar toolbar;
    private TextView tv_backBtn;

    private Map<Integer,String> IDs;
    private Integer page = 1;
    private Integer pageSize = 10;
    private Integer count = 50;

    private String userId = "";
    private String type = "类型1";
    private Map<String,Object> userSettings = null;
    private Map<Integer,String> names;

    private int color_gray;

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
        setContentView(R.layout.user_favorites);
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if(!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if(res.getCode()==0) {
                    Toast.makeText(UserFavorites.this, res.getMsg(), Toast.LENGTH_LONG).show();
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
            findViewById(R.id.userFavorites_body).setBackgroundColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.userFavorites_title)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.favors_search)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.favors_input)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.favors_input)).setBackgroundResource(R.drawable.edittext_bg_white);
            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_rect_3_gray);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_rect_3_gray);
            ((TextView) findViewById(R.id.pre_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.post_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.no_result)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.pageNumber)).setTextColor(colors.colorWhite);
            findViewById(R.id.userFavorites_line).setBackgroundColor(colors.colorWhite);

        } else {
            findViewById(R.id.userFavorites_body).setBackgroundColor(colors.colorWhite);
            ((TextView) findViewById(R.id.userFavorites_title)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.favors_search)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.favors_input)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.favors_input)).setBackgroundResource(R.drawable.edittext_bg_gray);
            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_rect_3_white);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_rect_3_white);
            ((TextView) findViewById(R.id.pre_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.post_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.no_result)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.pageNumber)).setTextColor(colors.colorGray);
            findViewById(R.id.userFavorites_line).setBackgroundColor(colors.colorGray);
        }
    }

    private void initViews() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    com.example.finance.common.R<Object> res = null;
                    res = favorsApi.GetFavorsCount(userId,input,type);
                    if(res.getCode()==0) {
                        Looper.prepare();
                        Toast.makeText(UserFavorites.this, res.getMsg(), Toast.LENGTH_LONG).show();
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
        thread.start();
        tv_noResult = findViewById(R.id.no_result);
        favorsAL = new ArrayList<>();
        IDs = new HashMap<>();
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");/*
        tv_back = findViewById(R.id.back);
        tv_back.setTypeface(fontAwe);*/
        btn_post = findViewById(R.id.post_arrow);
        btn_pre = findViewById(R.id.pre_arrow);
        ll_res = findViewById(R.id.resultViewC);
        sv_resView = findViewById(R.id.resultsView);
        tv_favorsSearch = findViewById(R.id.favors_search);
        tv_favorsSearch.setTypeface(fontAwe);
        et_favorsInput = findViewById(R.id.favors_input);
        tv_pageNumber = findViewById(R.id.pageNumber);
        color_gray = Color.rgb(65,65,65);
        names = new HashMap<>();

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        if(userSettings != null) changeMode((int) userSettings.get("isDark") == 1);
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
/*
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/

        et_favorsInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean isEnter = event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        input = et_favorsInput.getText().toString();
                        Looper.prepare();
                        Toast.makeText(UserFavorites.this, "您输入的是"+input, Toast.LENGTH_LONG).show();
                        Looper.loop();
                        try {
                            com.example.finance.common.R<Object> res = null;
                            res = favorsApi.GetFavorsCount(userId,input,"0");
                            if(res.getCode()==0) {
                                Looper.prepare();
                                Toast.makeText(UserFavorites.this, res.getMsg(), Toast.LENGTH_LONG).show();
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
                            res = favorsApi.GetFavorsByPage(userId,page,pageSize);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(res.getCode()==0) {
                            Looper.prepare();
                            Toast.makeText(UserFavorites.this, res.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        } else {
                            favorsData = (List<Map<String, Object>>) res.getData();
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
        tv_favorsSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Toast.makeText(UserFavorites.this, "您输入的是"+input, Toast.LENGTH_LONG).show();
                        Looper.loop();
                        input = et_favorsInput.getText().toString();
                        try {
                            com.example.finance.common.R<Object> res = null;
                            res = favorsApi.GetFavorsCount(userId,input,"0");
                            if(res.getCode()==0) {
                                Looper.prepare();
                                Toast.makeText(UserFavorites.this, res.getMsg(), Toast.LENGTH_LONG).show();
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
                            res = favorsApi.GetFavorsByPage(userId,page,pageSize);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(res.getCode()==0) {
                            Looper.prepare();
                            Toast.makeText(UserFavorites.this, res.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                            return;
                        }
                        favorsData = (List<Map<String, Object>>) res.getData();
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
    private void favorsClicked() {
        for(int i=favorsAL.size() - 1;i>=0;i--){
            int finalI = i;
            favorsAL.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            //此处是跳转的result回调方法
                            if (result.getData() != null && result.getResultCode() == Activity.RESULT_OK) {
                                pageTurn();
                            }
                        }
                    });
                    Intent intent = new Intent(UserFavorites.this, StockDataPage.class);
                    intent.putExtra("ts_code",IDs.get(finalI));
                    intent.putExtra("stock_name",names.get(finalI));
                    intentActivityResultLauncher.launch(intent);/*
                    Intent intent = new Intent();
                    intent.setClass(UserFavorites.this, StockDataPage.class);
                    intent.putExtra("ts_code",IDs.get(finalI));
                    intent.putExtra("stock_name",names.get(finalI));
                    startActivity(intent);*/
                }
            });
            ll_res.addView(favorsAL.get(i),0);
        }
    }
    private void pageTurn() {
        for (int i = 0; i < favorsAL.size(); i++) {
            ll_res.removeView(favorsAL.get(i));
        }
        favorsAL.clear();
        IDs.clear();
        com.example.finance.common.R<Object> res = null;
        try {
            res = favorsApi.GetFavorsByPage(userId,page,pageSize);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(res.getCode()==0) {
            Looper.prepare();
            Toast.makeText(UserFavorites.this, res.getMsg(), Toast.LENGTH_LONG).show();
            Looper.loop();
            return;
        }
        favorsData = (List<Map<String, Object>>) res.getData();
        tv_pageNumber.setText(page+"/"+(int)Math.ceil((float)count/(float)pageSize)+"页");
        if(favorsData.size()!=0) tv_noResult.setVisibility(View.GONE);
        else {
            btn_post.setVisibility(View.GONE);
            btn_pre.setVisibility(View.GONE);
            tv_pageNumber.setVisibility(View.GONE);
        }
        for(int i=0;i<favorsData.size();i++){
            favorsAL.add(addFavorsResult(favorsData.get(i), i));
            IDs.put(i,(String)favorsData.get(i).get("tsCode"));
            names.put(i,(String)favorsData.get(i).get("stoName"));
        }
        favorsClicked();
    }
    private AbsoluteLayout addFavorsResult(Map<String, Object> map, int i) {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        AbsoluteLayout AL = new AbsoluteLayout(UserFavorites.this);
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

        TextView ID = new TextView(UserFavorites.this);
        ID.setText((String)map.get("tsCode"));
        ID.setTextSize(22);
        if(userSettings != null && (int) userSettings.get("isDark") == 0) {
            ID.setTextColor(colors.colorGray);
        } else{
            ID.setTextColor(colors.colorWhite);
        }
        ID.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,38,10));
        AL.addView(ID);

        TextView date = new TextView(UserFavorites.this);
        date.setText((String)map.get("favorDate"));
        date.setTextSize(20);
        if(userSettings != null && (int) userSettings.get("isDark") == 0) {
            date.setTextColor(colors.colorGray);
        } else{
            date.setTextColor(colors.colorWhite);
        }
        date.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,38,100));
        AL.addView(date);


        TextView go = new TextView(UserFavorites.this);
        go.setTextSize(27);
        go.setText(R.string.fa_chevron_right);
        go.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,970,50));
        go.setTypeface(fontAwe);
        if(userSettings != null && (int) userSettings.get("isDark") == 0) {
            go.setTextColor(colors.colorGray);
        } else{
            go.setTextColor(colors.colorWhite);
        }
        //go.setTextColor(color_gray);
        AL.addView(go);
        TextView line = new TextView(UserFavorites.this);
        if(userSettings != null && (int) userSettings.get("isDark") == 0) {
            line.setBackgroundColor(colors.colorGray);
        } else{
            line.setBackgroundColor(colors.colorWhite);
        }
        line.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT,6,0,184));
        AL.addView(line);
        return AL;
    }
}
