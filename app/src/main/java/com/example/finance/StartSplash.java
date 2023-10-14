package com.example.finance;

import static com.example.finance.utils.MessageUtils.sendMessage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.finance.api.UserApi;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class StartSplash extends AppCompatActivity {

    private Long sleepInterval = 1000 * 1800L;
    private Long splashShowTime = 2000L;
    private String userId = "";
    private Map<String, Object> userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取发短信权限
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.start_splash);

        SharedPreferences settings = getSharedPreferences("AppInfo", MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();

        editor.putString("isAlive", "1");
        editor.apply();
        Thread myThread = new Thread() {//创建子线程
            @Override
            public void run() {
                try {
                    sleep(splashShowTime);
                    SharedPreferences settings1 = getSharedPreferences("AppInfo", 0);
                    Thread warningListener = new Thread() {//创建子线程
                        @Override
                        public void run() {
                            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                            userId = settings.getString("UserId", "").toString();
                            if (!userId.isEmpty()) {
                                try {
                                    userData = (Map<String, Object>) UserApi.GetDataById(userId).getData();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }


                                String isAliveStr = settings1.getString("isAlive", "").toString();
                                boolean isAppAlive = Objects.equals(isAliveStr, "1");
                                while (isAppAlive) {
                                    try {
                                        // TODO 获取当日股票
                                        // TODO 获取用户监听的自选股并对比，若超过阈值，则发送短信
                                        // TODO 改成目标电话号码
                                        boolean send = sendMessage("13666279166", null, "test", null, null);

                                        Looper.prepare();
                                        if (send) {
                                            Toast.makeText(getApplicationContext(), "短信发送成功", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "短信发送失败", Toast.LENGTH_LONG).show();
                                        }
                                        Looper.loop();

                                        Thread.sleep(sleepInterval);
                                        isAliveStr = settings1.getString("isAlive", "").toString();
                                        isAppAlive = Objects.equals(isAliveStr, "1");

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    };
                    // 确保只有一个监听线程
                    String isWarningListenerRunningStr = settings1.getString("isWarningListenerRunning", "").toString();
                    if (!Objects.equals(isWarningListenerRunningStr, "1")) {
                        warningListener.start();

                        SharedPreferences.Editor editor = settings1.edit();

                        editor.putString("isWarningListenerRunning", "1");
                        editor.apply();
                    }
                    Intent it = new Intent(getApplicationContext(), StockMainPage.class);//启动MainActivity
                    intentActivityResultLauncher.launch(it);
                    finish();//关闭当前活动
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();//启动线程
    }

    ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            //此处是跳转的result回调方法
            if (result.getResultCode() == RESULT_CANCELED || (result.getData() != null && result.getResultCode() == Activity.RESULT_OK)) {
                SharedPreferences settings = getSharedPreferences("AppInfo", MODE_PRIVATE);

                SharedPreferences.Editor editor = settings.edit();

                editor.clear();
                editor.apply();
            }
        }
    });
}
