package com.example.administrator.chat;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/7.
 */
public class CloseActivityClass {
    public static ArrayList<Activity> activityList = new ArrayList<>();
    public static void exitClient() {
        // 关闭所有Activity
        for (int i = 0; i < activityList.size(); i++) {
            if (null != activityList.get(i)) {
                activityList.get(i).finish();
            }
        }
    }
}
