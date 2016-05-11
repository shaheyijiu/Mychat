package com.example.administrator.chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

import account.UserInformation;
import others.LoadDataFromServer;

/**
 * Created by Administrator on 2016/1/18.
 */
public class AddFriendsTwo extends Activity {
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_addfriendstwo);
        CloseActivityClass.activityList.add(this);

        et = (EditText) findViewById(R.id.et_search);
        ImageView iv_back = (ImageView)findViewById(R.id.iv_back);
        ImageView iv_search = (ImageView)findViewById(R.id.iv_search);
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = et.getText().toString().trim();
                search(uid);
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void search(String uid){
        if (uid == null || uid.equals("")){
            return;
        }
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("正在查找联系人...");
        dialog.show();

        Map<String,String> map = new HashMap<>();
        map.put("uid",uid);
        LoadDataFromServer loadDataFromServer =
                new LoadDataFromServer(this,Constants.URL_Search_User,map);
        loadDataFromServer.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                try {
                    dialog.dismiss();
                    int code = data.getInteger("code");
                    if (code == 1) {
                        JSONObject json = data.getJSONObject("user");
                        String nick = json.getString("nick");
                        String avatar = json.getString("avatar");
                        String sex = json.getString("sex");
                        String hxid = json.getString("hxid");

                        Intent intent = new Intent();
                        intent.putExtra("nick", nick);
                        intent.putExtra("avatar", avatar);
                        intent.putExtra("sex", sex);
                        intent.putExtra("hxid", hxid);
                        intent.setClass(AddFriendsTwo.this, UserInformation.class);
                        startActivity(intent);
                    } else if (code == 2) {
                        Toast.makeText(AddFriendsTwo.this, "用户不存在",
                                Toast.LENGTH_SHORT).show();
                    } else if (code == 3) {

                        Toast.makeText(AddFriendsTwo.this,
                                "服务器查询错误...", Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(AddFriendsTwo.this,
                                "服务器繁忙请重试...", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    Toast.makeText(AddFriendsTwo.this, "数据解析错误...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            /*隐藏软键盘*/
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputMethodManager.isActive()){
                inputMethodManager.hideSoftInputFromWindow(AddFriendsTwo.this.getCurrentFocus().getWindowToken(), 0);
            }
            String uid = et.getText().toString().trim();
            search(uid);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
