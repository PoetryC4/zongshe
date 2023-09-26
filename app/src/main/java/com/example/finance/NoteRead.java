package com.example.finance;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
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
import java.util.Map;

public class NoteRead extends AppCompatActivity {

    private UserApi userApi;
    private NoteApi noteApi;
    private Colors colors;
    /*
        private TextView tv_back;*/
    private TextView tv_date;
    private TextView tv_content;
    private TextView tv_title;
    private TextView tv_modify;
    private Toolbar toolbar;
    private TextView tv_backBtn;

    private Map<String, Object> noteData = null;

    private String userId = "";
    private String noteId = "";
    private Map<String, Object> userSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_read);
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if (!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if (res.getCode() == 0) {
                    Toast.makeText(NoteRead.this, res.getMsg(), Toast.LENGTH_LONG).show();
                } else {
                    userSettings = (Map<String, Object>) res.getData();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Intent intent = getIntent();
        noteId = intent.getStringExtra("note_id");
        initViews();
        setListeners();
    }

    private void initViews() {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");/*
        tv_back = findViewById(R.id.back);
        tv_back.setTypeface(fontAwe);*/
        tv_date = findViewById(R.id.date);
        tv_content = findViewById(R.id.content);
        tv_title = findViewById(R.id.title);
        tv_modify = findViewById(R.id.modify_button);
        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    com.example.finance.common.R<Object> res = null;
                    res = noteApi.GetNoteById(userId, noteId);
                    if (res.getCode() == 0) {
                        Looper.prepare();
                        Toast.makeText(NoteRead.this, res.getMsg(), Toast.LENGTH_LONG).show();
                        Looper.loop();
                    } else {
                        noteData = (Map<String, Object>) res.getData();
                        tv_date.setText((CharSequence) noteData.get("date"));
                        tv_content.setText((CharSequence) noteData.get("content"));
                        tv_title.setText((CharSequence) noteData.get("title"));
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
        if (userSettings != null) changeMode((int) userSettings.get("isDark") == 1);
    }

    private void changeMode(boolean isDark) {

        if (isDark) {
            findViewById(R.id.noteRead_body).setBackgroundColor(colors.colorSuperGray);
            findViewById(R.id.noteRead_line).setBackgroundColor(colors.colorWhite);
            ((TextView) findViewById(R.id.title)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.date)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.content)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.modify_button)).setTextColor(colors.colorCyan);
        } else {
            findViewById(R.id.noteRead_body).setBackgroundColor(colors.colorWhite);
            findViewById(R.id.noteRead_line).setBackgroundColor(colors.colorGray);
            ((TextView) findViewById(R.id.title)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.date)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.content)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.modify_button)).setTextColor(colors.colorOrange);
        }
    }

    private void setListeners() {

        tv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        //此处是跳转的result回调方法
                        if (result.getData() != null && result.getResultCode() == Activity.RESULT_OK) {
                            finish();
                        }
                    }
                });
                Intent intent = new Intent(NoteRead.this, NoteModify.class);
                intent.putExtra("new", "0");
                intent.putExtra("note_id", noteId);
                intentActivityResultLauncher.launch(intent);
                /*Intent intent = new Intent();
                intent.setClass(NoteRead.this, NoteModify.class);
                intent.putExtra("new","0");
                intent.putExtra("note_id",noteId);
                startActivity(intent);*/
            }
        });
/*
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
    }
}
