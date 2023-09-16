package com.example.finance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finance.api.UserApi;
import com.example.finance.common.Colors;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class UserPageSimple extends AppCompatActivity {

    private Colors colors;
    private UserApi userApi;
    private boolean isLoggedIn;

    private TextView tv_createDate;
    private TextView tv_username;
    private TextView tv_main;
    private TextView tv_tools;
    private TextView tv_favorites;
    private TextView tv_user;
    private TextView tv_history;
    private TextView tv_topics;
    private TextView tv_setting;
    private TextView tv_notes;
    private TextView tv_predictor;
    private TextView tv_warnings;
    private TextView tv_feedback;
    private TextView tv_consultation;
    private TextView tv_friendlyMode;
    private TextView tv_messages;
    private TextView tv_modeSwitch;
    private AbsoluteLayout al_notLoggedIn;
    private AbsoluteLayout al_userAvatar;
    private AbsoluteLayout al_userFunc;
    private AbsoluteLayout al_userContent;
    private Toolbar toolbar;
    private TextView tv_backBtn;
    private LinearLayout ll_blank;
    private LinearLayout ll_blank2;

    private String userId = "";
    private Map<String,Object> userSettings = null;
    private Map<String,Object> userData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page_simple);

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        userId=settings.getString("UserId", "").toString();
        if(!userId.isEmpty()) {
            try {
                com.example.finance.common.R<Object> res = null;
                res = userApi.GetSettingsById(userId);
                if(res.getCode()==0) {
                    Toast.makeText(UserPageSimple.this, res.getMsg(), Toast.LENGTH_LONG).show();
                } else {
                    userSettings = (Map<String, Object>) res.getData();
                    userData = (Map<String, Object>) UserApi.GetDataById(userId).getData();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isLoggedIn = !userId.isEmpty();
        initViews();
        setListeners();
    }

    private void changeMode(boolean isDark) {
        if(isDark) {
            findViewById(R.id.userPageSimple_body).setBackgroundColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.username)).setTextColor(colors.colorBlue);
            ((TextView) findViewById(R.id.createDate)).setTextColor(colors.colorBlue);
            findViewById(R.id.avatar_outline).setBackgroundResource(R.drawable.circle_stroke_blue);
            ((TextView) findViewById(R.id.fans_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.fans_count)).setTextColor(colors.colorBlue);
            ((TextView) findViewById(R.id.follow_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.follow_count)).setTextColor(colors.colorBlue);
            ((TextView) findViewById(R.id.dynamic_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.dynamic_count)).setTextColor(colors.colorBlue);
            ((TextView) findViewById(R.id.favorites_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.history_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.topics_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.setting_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.predictor_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.warnings_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.consultation_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.feedback_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.friendlyMode_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.notes_text)).setTextColor(colors.colorWhite);

            findViewById(R.id.switchBar).setBackgroundColor(colors.colorBlue);
            ((TextView) findViewById(R.id.tool_page_text)).setTextColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.tool_page)).setTextColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.main_page_text)).setTextColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.main_page)).setTextColor(colors.colorSuperGray);
            ((TextView) findViewById(R.id.user_page_text)).setTextColor(colors.colorDarkBlue);
            ((TextView) findViewById(R.id.user_page)).setTextColor(colors.colorDarkBlue);
        } else {
            findViewById(R.id.userPageSimple_body).setBackgroundColor(colors.colorWhite);
            ((TextView) findViewById(R.id.username)).setTextColor(colors.colorRed);
            ((TextView) findViewById(R.id.createDate)).setTextColor(colors.colorRed);
            findViewById(R.id.avatar_outline).setBackgroundResource(R.drawable.circle_stroke_red);
            ((TextView) findViewById(R.id.fans_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.fans_count)).setTextColor(colors.colorRed);
            ((TextView) findViewById(R.id.follow_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.follow_count)).setTextColor(colors.colorRed);
            ((TextView) findViewById(R.id.dynamic_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.dynamic_count)).setTextColor(colors.colorRed);
            ((TextView) findViewById(R.id.favorites_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.history_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.topics_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.setting_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.predictor_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.warnings_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.consultation_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.feedback_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.friendlyMode_text)).setTextColor(colors.colorGray);
            ((TextView) findViewById(R.id.notes_text)).setTextColor(colors.colorGray);

            findViewById(R.id.switchBar).setBackgroundColor(colors.colorLightRed);
            ((TextView) findViewById(R.id.tool_page_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.tool_page)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.main_page_text)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.main_page)).setTextColor(colors.colorWhite);
            ((TextView) findViewById(R.id.user_page_text)).setTextColor(colors.colorRed);
            ((TextView) findViewById(R.id.user_page)).setTextColor(colors.colorRed);
        }
    }

    private void initViews() {
        Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");

        tv_createDate = findViewById(R.id.createDate);
        tv_username = findViewById(R.id.username);
        tv_user = findViewById(R.id.user_page);
        tv_user.setTypeface(fontAwe);
        tv_tools = findViewById(R.id.tool_page);
        tv_tools.setTypeface(fontAwe);
        tv_main = findViewById(R.id.main_page);
        tv_main.setTypeface(fontAwe);
        tv_favorites = findViewById(R.id.favorites);
        tv_history = findViewById(R.id.history);
        tv_topics = findViewById(R.id.topics);
        tv_setting = findViewById(R.id.setting);
        tv_predictor = findViewById(R.id.predictor);
        tv_warnings = findViewById(R.id.warnings);
        tv_feedback = findViewById(R.id.feedback);
        tv_consultation = findViewById(R.id.consultation);
        tv_notes = findViewById(R.id.notes);
        tv_friendlyMode = findViewById(R.id.friendlyMode);
        tv_favorites.setTypeface(fontAwe);
        tv_history.setTypeface(fontAwe);
        tv_topics.setTypeface(fontAwe);
        tv_setting.setTypeface(fontAwe);
        tv_predictor.setTypeface(fontAwe);
        tv_warnings.setTypeface(fontAwe);
        tv_feedback.setTypeface(fontAwe);
        tv_consultation.setTypeface(fontAwe);
        tv_notes.setTypeface(fontAwe);
        tv_friendlyMode.setTypeface(fontAwe);
        al_notLoggedIn = (AbsoluteLayout) findViewById(R.id.not_logged_in);
        al_userAvatar = (AbsoluteLayout) findViewById(R.id.user_avatar);
        al_userContent = (AbsoluteLayout) findViewById(R.id.user_content);
        al_userFunc = (AbsoluteLayout) findViewById(R.id.user_func);
        ll_blank = findViewById(R.id.userPage_blank);
        ll_blank2 = findViewById(R.id.userPage_blank2);

        tv_backBtn = findViewById(R.id.backBtn);
        tv_backBtn.setTypeface(fontAwe);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        tv_messages = findViewById(R.id.messages);
        tv_messages.setTypeface(fontAwe);
        tv_modeSwitch = findViewById(R.id.modeSwitch);
        tv_modeSwitch.setTypeface(fontAwe);
        if (isLoggedIn) {
            al_notLoggedIn.setVisibility(View.GONE);
            tv_username.setText((CharSequence) userData.get("user_name"));
            tv_createDate.setText((CharSequence) userData.get("create_date"));
        }
        else{
            al_userAvatar.setVisibility(View.GONE);
            al_userContent.setVisibility(View.GONE);
            al_userFunc.setVisibility(View.GONE);
            ll_blank2.setVisibility(View.VISIBLE);
        }
        if(userSettings != null && (int) userSettings.get("isDark") == 1) {
            tv_modeSwitch.setText(R.string.fa_sun_o);
            tv_modeSwitch.setHint("1");
            tv_modeSwitch.setTypeface(fontAwe);
        }

        if(userSettings != null) changeMode((int) userSettings.get("isDark") == 1);
    }
    private void setListeners() {
        tv_modeSwitch.setOnClickListener(new View.OnClickListener() {
            final Typeface fontAwe = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
            @Override
            public void onClick(View view) {
                if(Objects.equals(tv_modeSwitch.getHint().toString(),"0")) {
                    tv_modeSwitch.setHint("1");
                    tv_modeSwitch.setText(R.string.fa_sun_o);
                    tv_modeSwitch.setTypeface(fontAwe);

                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                userApi.UpdateSettingDark(userId, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(!userId.isEmpty()) {
                                try {
                                    userSettings = (Map<String, Object>) userApi.GetSettingsById(userId).getData();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                changeMode((int) userSettings.get("isDark") == 1);
                            }
                        }
                    };

                    // 启动线程
                    thread.start();
                } else {
                    tv_modeSwitch.setHint("0");
                    tv_modeSwitch.setText(R.string.fa_moon_o);
                    tv_modeSwitch.setTypeface(fontAwe);
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                userApi.UpdateSettingDark(userId, 0);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(!userId.isEmpty()) {
                                try {
                                    userSettings = (Map<String, Object>) userApi.GetSettingsById(userId).getData();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                changeMode((int) userSettings.get("isDark") == 1);
                            }
                        }
                    };

                    // 启动线程
                    thread.start();
                }
            }
        });
        tv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        al_notLoggedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserPageSimple.this, LoginPage.class);
                startActivity(intent);
            }
        });
        tv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserPageSimple.this, StockMainPage.class);
                startActivity(intent);
            }
        });
        tv_tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserPageSimple.this, ToolsPage.class);
                startActivity(intent);
            }
        });


        tv_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserPageSimple.this, UserFavorites.class);
                startActivity(intent);
            }
        });
        tv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserPageSimple.this, UserHistory.class);
                startActivity(intent);
            }
        });
        tv_topics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserPageSimple.this, TopicsPage.class);
                startActivity(intent);
            }
        });
        tv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserPageSimple.this, UserSetting.class);
                startActivity(intent);
            }
        });
        tv_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserPageSimple.this, UserNotes.class);
                startActivity(intent);
            }
        });
        tv_predictor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserPageSimple.this, PredictorPage.class);
                startActivity(intent);
            }
        });
    }
}
