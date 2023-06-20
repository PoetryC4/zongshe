package com.example.finance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.NoteApi;
import com.example.finance.api.UserApi;
import com.example.finance.common.Colors;
import com.example.finance.utils.GetCurTime;

import java.io.IOException;
import java.util.Map;

public class NoteModify extends AppCompatActivity {

    private UserApi userApi;
    private GetCurTime getCurTime;
    private NoteApi noteApi;
    private Colors colors;
/*
    private TextView tv_back;*/
    private TextView tv_date;
    private EditText et_content;
    private EditText et_title;
    private TextView tv_save;
    private TextView tv_delete;
    private Toolbar toolbar;
    private TextView tv_backBtn;

    private Map<String,Object> noteData = null;

    private String userId = "";
    private String noteId = "";
    private Map<String,Object> userSettings = null;
    private int isNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_modify);
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId = settings.getString("UserId", "").toString();
        if(!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if(res.getCode()==0) {
                    Toast.makeText(NoteModify.this, res.getMsg(), Toast.LENGTH_LONG).show();
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
        isNew = Integer.parseInt(intent.getStringExtra("new"));
        if(isNew == 0)noteId = intent.getStringExtra("note_id");
        initViews();
        setListeners();
    }
    private void initViews() {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");/*
        tv_back = findViewById(R.id.back);
        tv_back.setTypeface(fontAwe);*/
        tv_date = findViewById(R.id.date);
        et_content = findViewById(R.id.content);
        et_title = findViewById(R.id.title);
        tv_save = findViewById(R.id.save_button);
        tv_delete = findViewById(R.id.delete_button);

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        if(isNew == 0) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = noteApi.GetNoteById(userId,noteId);
                if (res.getCode()==0) {
                    Toast.makeText(NoteModify.this, res.getMsg(), Toast.LENGTH_LONG).show();
                    tv_date.setText("");
                    tv_date.setVisibility(View.GONE);
                    tv_delete.setVisibility(View.GONE);
                    et_content.setText("");
                    et_title.setText("");
                } else {
                    noteData = (Map<String, Object>) res.getData();
                    tv_date.setText((CharSequence) noteData.get("date"));
                    et_content.setText((CharSequence) noteData.get("content"));
                    et_title.setText((CharSequence) noteData.get("title"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            tv_date.setText("");
            tv_date.setVisibility(View.GONE);
            tv_delete.setVisibility(View.GONE);
            et_content.setText("");
            et_title.setText("");
        }
        if(userSettings != null) changeMode((int) userSettings.get("isDark") == 1);
    }
    private void changeMode(boolean isDark) {
        
        if(isDark) {
            findViewById(R.id.title).setBackgroundResource(R.drawable.edittext_bg_white);
            findViewById(R.id.content).setBackgroundResource(R.drawable.edittext_bg_white);
            findViewById(R.id.noteModify_body).setBackgroundColor(colors.colorSuperGray);
            findViewById(R.id.noteModify_line).setBackgroundColor(colors.colorWhite);
            ((EditText) findViewById(R.id.title)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.date)).setTextColor(colors.colorWhite);
            ((EditText) findViewById(R.id.content)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.save_button)).setTextColor(colors.colorCyan);
            ((TextView) findViewById(R.id.delete_button)).setTextColor(colors.colorCyan);
        } else {
            findViewById(R.id.title).setBackgroundResource(R.drawable.edittext_bg_gray);
            findViewById(R.id.content).setBackgroundResource(R.drawable.edittext_bg_gray);
            findViewById(R.id.noteModify_body).setBackgroundColor(colors.colorWhite);
            findViewById(R.id.noteModify_line).setBackgroundColor(colors.colorGray);
            ((EditText) findViewById(R.id.title)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.date)).setTextColor(colors.colorGray);
            ((EditText) findViewById(R.id.content)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.save_button)).setTextColor(colors.colorOrange);
            ((TextView) findViewById(R.id.delete_button)).setTextColor(colors.colorOrange);
        }
    }

    private void setListeners() {

        tv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(noteApi.DeleteNote(userId,noteId).getCode()==0) {
                        Toast.makeText(getApplicationContext(),"操作错误", Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = et_content.getEditableText().toString();
                String title = et_title.getEditableText().toString();
                String date;
                if(isNew == 1) {
                    date = GetCurTime.curTime();
                    try {
                        if(noteApi.SaveNote(userId,title,date,content).getCode()==0) {
                            Toast.makeText(getApplicationContext(),"操作错误", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"操作成功", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    date = (String) noteData.get("date");
                    try {
                        if(noteApi.UpdateNote(userId,noteId,title,content, GetCurTime.curTime_2()).getCode()==0) {
                            Toast.makeText(getApplicationContext(),"操作错误", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"操作成功", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                finish();
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
