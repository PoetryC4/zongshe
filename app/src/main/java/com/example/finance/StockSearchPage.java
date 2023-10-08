package com.example.finance;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.StockApi;
import com.example.finance.api.UserApi;
import com.example.finance.common.Colors;
import com.example.finance.utils.GetCurTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StockSearchPage extends AppCompatActivity {

    private GetCurTime getCurTime;
    private StockApi stockApi;
    private Colors colors;
    private UserApi userApi;

    private String searchInput;
    /*
        private TextView tv_back;*/
    private TextView tv_Delete;
    private EditText et_Input;
    private ListView lv_Tips;
    private TextView tv_Search;
    private TextView tv_HistoryClear;
    private TextView tv_tips;
    private Toolbar toolbar;
    private TextView tv_backBtn;

    private ArrayAdapter<String> autoComAdapter;
    private ArrayAdapter<String> historyAdapter;

    private String userId = "";
    private Map<String, Object> userSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_search_page);
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if (!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if (res.getCode() == 0) {
                    Toast.makeText(StockSearchPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
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
        Intent intent = getIntent();
        searchInput = intent.getStringExtra("searchInput");
        setHistoryAdapter("1");
        lv_Tips.setAdapter(historyAdapter);
        setListener();
    }

    private void changeMode(boolean isDark) {

        if (isDark) {
            findViewById(R.id.stockSearch_body).setBackgroundColor(colors.colorSuperGray);
            findViewById(R.id.stockSearch_upper).setBackgroundColor(colors.colorBlue);
            ((TextView) findViewById(R.id.search_input)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.tips)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.history_clear)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.history_clear)).setBackgroundColor(colors.colorGrayish);
            //((TextView) findViewById(R.id.title)).setBackgroundColor(colors.colorGrayish);
        } else {
            findViewById(R.id.stockSearch_body).setBackgroundColor(colors.colorWhite);
            findViewById(R.id.stockSearch_upper).setBackgroundColor(colors.colorRed);
            ((TextView) findViewById(R.id.search_input)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.tips)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.history_clear)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.history_clear)).setBackgroundColor(colors.colorWhiteish);
            //((TextView) findViewById(R.id.title)).setBackgroundColor(colors.colorWhiteish);
        }
    }

    private void initView() {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");/*
        tv_back = findViewById(R.id.back);
        tv_back.setTypeface(fontAwe);*/
        tv_Search = findViewById(R.id.stock_search);
        tv_Delete = findViewById(R.id.content_delete);
        et_Input = (EditText) findViewById(R.id.search_input);
        et_Input.setText(searchInput);
        lv_Tips = (ListView) findViewById(R.id.listView);
        tv_HistoryClear = findViewById(R.id.history_clear);
        tv_HistoryClear.setTypeface(fontAwe);
        tv_Delete.setTypeface(fontAwe);

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        if (searchInput == null || searchInput.isEmpty()) {
            tv_Delete.setVisibility(View.GONE);
        } else {
            et_Input.setText(searchInput);
            tv_Delete.setVisibility(View.VISIBLE);
        }
        tv_Search.setTypeface(fontAwe);
        tv_tips = findViewById(R.id.tips);
        if (userSettings != null) changeMode((int) userSettings.get("isDark") == 1);
    }

    private void setListener() {
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
        tv_HistoryClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            com.example.finance.common.R<String> res = null;
                            res = userApi.DeleteUserSearchHistory(userId);
                            if (res.getCode() == 0) {
                                Looper.prepare();
                                Toast.makeText(StockSearchPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
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
                finish();
            }
        });
        lv_Tips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = lv_Tips.getAdapter().getItem(i).toString();
                et_Input.setText(selected);
                et_Input.setSelection(et_Input.length());
                lv_Tips.setVisibility(View.GONE);//点了之后消失
                startSearch(selected);
            }
        });
        tv_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_Input.setText(" ");
                tv_Delete.setVisibility(View.GONE);
            }
        });
        et_Input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lv_Tips.setVisibility(View.VISIBLE);
            }
        });/*
        et_Input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(et_Input.getText().toString().isEmpty()) {
                    et_Input.setHint(getRecomHint());
                    lv_Tips.setAdapter(historyAdapter);
                    tv_Delete.setVisibility(View.GONE);
                    tv_HistoryClear.setVisibility(View.VISIBLE);
                    tv_tips.setText("搜索历史");
                }
                else{
                    try {
                        setAutoComAdapter(et_Input.getText().toString());
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    lv_Tips.setAdapter(autoComAdapter);
                    tv_Delete.setVisibility(View.VISIBLE);
                    tv_HistoryClear.setVisibility(View.GONE);
                    tv_tips.setText("可能的结果");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
        et_Input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    lv_Tips.setVisibility(View.GONE);
                    startSearch(et_Input.getText().toString());
                }
                return false;//返回true，保留软键盘。false，隐藏软键盘
            }
        });
        tv_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_Input.getText().toString().isEmpty()) {
                    et_Input.setText(getRecomHint());
                }
                startSearch(et_Input.getText().toString());
            }
        });
    }

    private String getRecomHint() {
        //获取热搜
        return "1024";
    }

    private void setAutoComAdapter(String text) throws IOException, InterruptedException {
        //模糊搜索 可能的结果
        com.example.finance.common.R<Object> res = stockApi.GetAlikeNames(text, 7);
        if (res.getCode() == 0) {
            Looper.prepare();
            Toast.makeText(StockSearchPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
            Looper.loop();
        } else {
            List<String> resL = (List<String>) res.getData();
        /*List<String> resL = new ArrayList<>();
        resL.add("A");
        resL.add("B");*/
            if (userSettings != null && (int) userSettings.get("isDark") == 0) {
                autoComAdapter = new ArrayAdapter<String>(this, R.layout.my_simple_expandable_list_item_white, resL);
            } else {
                autoComAdapter = new ArrayAdapter<String>(this, R.layout.my_simple_expandable_list_item_gray, resL);
            }
        }
    }

    private void setHistoryAdapter(String userID) {
        //获取搜索历史
        com.example.finance.common.R<Object> res = null;
        try {
            res = userApi.UserSearchHistory(userId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (res.getCode() == 0) {
            Toast.makeText(StockSearchPage.this, res.getMsg(), Toast.LENGTH_LONG).show();
        } else {
            List<String> resL = new ArrayList<>();
            for (int i = 0; i < ((List<Map<String, Object>>) res.getData()).size(); i++) {
                resL.add((String) ((List<Map<String, Object>>) res.getData()).get(i).get("history"));
            }
            System.out.println(resL.toString());
        /*List<String> resL = new ArrayList<>();
        resL.add("E");
        resL.add("F");*/
            //historyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,resL);
            if (userSettings != null && (int) userSettings.get("isDark") == 0) {
                historyAdapter = new ArrayAdapter<String>(this, R.layout.my_simple_expandable_list_item_white, resL);
            } else {
                historyAdapter = new ArrayAdapter<String>(this, R.layout.my_simple_expandable_list_item_gray, resL);
            }
        }
    }

    private void startSearch(String input) {/*
        try {
            com.example.finance.common.R<String> res = null;
            res = userApi.AddUserSearchHistory(userId,input, getCurTime.curTime());
            if(res.getCode() == 0) {
                Toast.makeText(StockSearchPage.this, "操作错误", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        Intent intent = new Intent(StockSearchPage.this, StockSearchResult.class);
        intent.putExtra("searchInput", input);
        intentActivityResultLauncher.launch(intent);/*
        Intent intent = new Intent();
        intent.setClass(StockSearchPage.this, StockSearchResult.class);
        intent.putExtra("searchInput",input);
        startActivity(intent);*/
    }
    ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            //此处是跳转的result回调方法
            if (result.getResultCode() == RESULT_CANCELED || (result.getData() != null && result.getResultCode() == Activity.RESULT_OK)) {
                setHistoryAdapter("1");
                lv_Tips.setAdapter(historyAdapter);
            }
        }
    });
}
