package com.example.administrator.chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Utils.TextUtils;
import others.LoadDataFromServer;

/**
 * Created by Administrator on 2016/1/20.
 */
public class UpdateNickActivity extends Activity {
    private EditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatenick);

        Button save = (Button)findViewById(R.id.bt_save);
        et = (EditText)findViewById(R.id.et_nick);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
    }

    private void update(){
        final String newNick = et.getText().toString().trim();
        if (newNick == null || newNick.equals("") || newNick.equals("0")){
            Toast.makeText(this,"昵称不能为空",Toast.LENGTH_LONG).show();

        }
        Map<String,String> map = new HashMap<>();
        String hxid = TextUtils.getStringValue(this, "hxid");
        map.put("newNick", newNick);
        map.put("hxid", hxid);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("正在更新...");
        dialog.show();

        LoadDataFromServer loadDataFromServer = new LoadDataFromServer(this,Constants.URL_UPDATE_Nick,map);
        loadDataFromServer.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                dialog.dismiss();
                int code = data.getInteger("code");
                if (code == 1) {
                    TextUtils.putStringValue(UpdateNickActivity.this, "nick", newNick);
                    finish();
                } else {
                    Toast.makeText(UpdateNickActivity.this, "更新失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void back(View view){
        finish();
    }
}
