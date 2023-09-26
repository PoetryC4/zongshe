package com.example.finance;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class StartSplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.start_splash);
        Thread myThread = new Thread() {//创建子线程
            @Override
            public void run() {
                try {
                    sleep(1111);
                    Intent it = new Intent(getApplicationContext(), StockMainPage.class);//启动MainActivity
                    startActivity(it);
                    finish();//关闭当前活动
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();//启动线程
    }
}
