package com.example.administrator.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

import Utils.TextUtils;

public class SplashActivity extends Activity {
    private static final int sleepTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        int RunCount = TextUtils.getIntValue(this, "RUN_COUNT");
        if (RunCount == 0) {

        } else {
            TextUtils.putIntValue(this, "RUN_COUNT", RunCount++);
        }

        boolean isLogin = TextUtils.getBooleanValue(SplashActivity.this,
                Constants.Login_State);

    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.getBooleanValue(SplashActivity.this,
                        Constants.Login_State)){
                    long start = System.currentTimeMillis();
                    //为了保证进入主页面后本地会话和群组都load完毕
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();
                    long costTime = System.currentTimeMillis() - start;
                    if (sleepTime > costTime) {
                        try {
                            Thread.sleep(sleepTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    getLogin();
                    finish();
                } else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }) .start();
    }

    private void getLogin() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
