package com.example.finance;

import android.app.Activity;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.NoteApi;
import com.example.finance.api.UserApi;
import com.example.finance.common.Colors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserNotes extends AppCompatActivity {

    private UserApi userApi;
    private Colors colors;
    private NoteApi noteApi;

    private String input = "";
    private List<AbsoluteLayout> noteAL;
    private List<Map<String, Object>> noteData;
    private ScrollView sv_resView;
    private LinearLayout ll_res;
/*
    private TextView tv_back;*/
    private TextView tv_addNotes;
    private TextView tv_pageNumber;
    private Button btn_pre;
    private Button btn_post;
    private TextView tv_noResult;
    private EditText et_noteInput;
    private TextView tv_noteSearch;
    private Toolbar toolbar;
    private TextView tv_backBtn;

    private Map<Integer,String> IDs;
    private Integer page = 1;
    private Integer pageSize = 10;
    private Integer count = 0;
    
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
        setContentView(R.layout.user_notes);

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId=settings.getString("UserId", "").toString();
        if(!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if(res.getCode()==0) {
                    Toast.makeText(UserNotes.this, res.getMsg(), Toast.LENGTH_LONG).show();
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
            findViewById(R.id.userNotes_body).setBackgroundColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.userNotes_title)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.add_notes)).setTextColor(colors.colorBlue);
            ((TextView) findViewById(R.id.note_search)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.note_input)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.note_input)).setBackgroundResource(R.drawable.edittext_bg_white);
            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_rect_3_gray);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_rect_3_gray);
            ((TextView) findViewById(R.id.pre_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.post_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.no_result)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.pageNumber)).setTextColor(colors.colorWhite);
            findViewById(R.id.userNotes_line).setBackgroundColor(colors.colorWhite);

        } else {
            findViewById(R.id.userNotes_body).setBackgroundColor(colors.colorWhite);
            ((TextView) findViewById(R.id.userNotes_title)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.add_notes)).setTextColor(colors.colorLightRed);
            ((TextView) findViewById(R.id.note_search)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.note_input)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.note_input)).setBackgroundResource(R.drawable.edittext_bg_gray);
            findViewById(R.id.pre_arrow).setBackgroundResource(R.drawable.rounded_rect_3_white);
            findViewById(R.id.post_arrow).setBackgroundResource(R.drawable.rounded_rect_3_white);
            ((TextView) findViewById(R.id.pre_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.post_arrow)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.no_result)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.pageNumber)).setTextColor(colors.colorGray);
            findViewById(R.id.userNotes_line).setBackgroundColor(colors.colorGray);
        }
    }

    private void initViews() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    com.example.finance.common.R<Object> res = null;
                    res = noteApi.GetNoteCount(userId,input);
                    if(res.getCode()==0) {
                        Looper.prepare();
                        Toast.makeText(UserNotes.this, res.getMsg(), Toast.LENGTH_LONG).show();
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
        noteAL = new ArrayList<>();
        IDs = new HashMap<>();
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");/*
        tv_back = findViewById(R.id.back);
        tv_back.setTypeface(fontAwe);*/
        btn_post = findViewById(R.id.post_arrow);
        btn_pre = findViewById(R.id.pre_arrow);
        ll_res = findViewById(R.id.resultViewC);
        tv_pageNumber = findViewById(R.id.pageNumber);
        sv_resView = findViewById(R.id.resultsView);
        tv_addNotes = findViewById(R.id.add_notes);
        tv_noteSearch = findViewById(R.id.note_search);
        tv_noteSearch.setTypeface(fontAwe);
        et_noteInput = findViewById(R.id.note_input);

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
        });/*
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        et_noteInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean isEnter = event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        input = et_noteInput.getText().toString();
                        Looper.prepare();
                        Toast.makeText(UserNotes.this, "您输入的是"+input, Toast.LENGTH_LONG).show();
                        Looper.loop();
                        try {
                            com.example.finance.common.R<Object> res = null;
                            res = noteApi.GetNoteCount(userId,input);
                            if(res.getCode()==0) {
                                Looper.prepare();
                                Toast.makeText(UserNotes.this, res.getMsg(), Toast.LENGTH_LONG).show();
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
                            res = noteApi.GetNotesByPage(userId,page,pageSize,input);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(res.getCode()==0) {
                            Looper.prepare();
                            Toast.makeText(UserNotes.this, res.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                        } else {
                            noteData = (List<Map<String, Object>>) res.getData();
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
        tv_noteSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        input = et_noteInput.getText().toString();
                        Looper.prepare();
                        Toast.makeText(UserNotes.this, "您输入的是"+input, Toast.LENGTH_LONG).show();
                        Looper.loop();
                        try {
                            com.example.finance.common.R<Object> res = null;
                            res = noteApi.GetNoteCount(userId,input);
                            if(res.getCode()==0) {
                                Looper.prepare();
                                Toast.makeText(UserNotes.this, res.getMsg(), Toast.LENGTH_LONG).show();
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
                            res = noteApi.GetNotesByPage(userId,page,pageSize,input);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(res.getCode()==0) {
                            Looper.prepare();
                            Toast.makeText(UserNotes.this, res.getMsg(), Toast.LENGTH_LONG).show();
                            Looper.loop();
                            return;
                        }
                        noteData = (List<Map<String, Object>>) res.getData();
                        page = 1;
                        ThreadPage threadPage = new ThreadPage();
                        threadPage.start();
                    }
                };

                // 启动线程
                thread.start();
            }
        });
        tv_addNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserNotes.this, NoteModify.class);
                intent.putExtra("new","1");
                startActivity(intent);
                System.out.println("dsdf");
            }
        });
    }
    private void noteClicked() {
        //for(int i=0;i<noteAL.size();i++)
        for(int i=noteAL.size()-1;i>=0;i--){
            int finalI = i;
            noteAL.get(i).setOnClickListener(new View.OnClickListener() {
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
                    Intent intent = new Intent(UserNotes.this, NoteRead.class);
                    intent.putExtra("note_id",IDs.get(finalI));
                    intentActivityResultLauncher.launch(intent);/*
                    Intent intent = new Intent();
                    intent.setClass(UserNotes.this, NoteRead.class);
                    intent.putExtra("note_id",IDs.get(finalI));
                    startActivity(intent);*/
                }
            });
            ll_res.addView(noteAL.get(i),0);
        }
    }
    private void pageTurn() {
        //ll_res.removeAllViews();
        for (int i = 0; i < noteAL.size(); i++) {
            ll_res.removeView(noteAL.get(i));
        }
        noteAL.clear();
        IDs.clear();
        com.example.finance.common.R<Object> res = null;
        try {
            res = noteApi.GetNotesByPage(userId,page,pageSize,input);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(res.getCode()==0) {
            Looper.prepare();
            Toast.makeText(UserNotes.this, res.getMsg(), Toast.LENGTH_LONG).show();
            Looper.loop();
            return;
        }
        noteData = (List<Map<String, Object>>) res.getData();
        tv_pageNumber.setText(page+"/"+(int)Math.ceil((float)count/(float)pageSize)+"页");
        if(noteData.size()!=0) {
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
        for(int i=0;i<noteData.size();i++){
            noteAL.add(addNoteResult(noteData.get(i), i));
            IDs.put(i,""+noteData.get(i).get("noteId"));
        }
        noteClicked();
    }
    private AbsoluteLayout addNoteResult(Map<String, Object> map, int i) {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        AbsoluteLayout AL = new AbsoluteLayout(UserNotes.this);
        AL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,190));
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            AL.setBackgroundColor(i%2 == 1?colors.colorGray:colors.colorSuperGray);
        } else {
            AL.setBackgroundColor(i%2 == 1?colors.colorWhiteish:colors.colorWhite);
        }

        TextView title = new TextView(UserNotes.this);
        title.setText((String)map.get("title"));
        title.setTextSize(25);
        if(userSettings != null && (int) userSettings.get("isDark") == 0) {
            title.setTextColor(colors.colorGray);
        } else{
            title.setTextColor(colors.colorWhite);
        }
        //title.setTextColor(colors.colorGray);
        title.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,38,0));
        AL.addView(title);

        TextView date = new TextView(UserNotes.this);
        //date.setText((String)map.get("created_date"));
        date.setText((String)map.get("noteDate"));
        date.setTextSize(20);
        if(userSettings != null && (int) userSettings.get("isDark") == 0) {
            date.setTextColor(colors.colorGray);
        } else{
            date.setTextColor(colors.colorWhite);
        }
        //date.setTextColor(colors.colorGray);
        date.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,38,100));
        AL.addView(date);


        TextView go = new TextView(UserNotes.this);
        go.setTextSize(27);
        go.setText(R.string.fa_chevron_right);
        go.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,970,50));
        go.setTypeface(fontAwe);
        if(userSettings != null && (int) userSettings.get("isDark") == 0) {
            go.setTextColor(colors.colorGray);
        } else{
            go.setTextColor(colors.colorWhite);
        }
        //go.setTextColor(colors.colorGray);
        AL.addView(go);
        TextView line = new TextView(UserNotes.this);
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
