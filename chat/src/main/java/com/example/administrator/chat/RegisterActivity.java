package com.example.administrator.chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;

import java.util.HashMap;
import java.util.Map;

import Utils.TextUtils;
import database.DatabaseHelper;
import others.LoadDataFromServer;

/**
 * Created by Administrator on 2015/12/15.
 */
public class RegisterActivity extends Activity{
    private EditText et_phoneNumber;
    private EditText et_password;
    private Context context;
    DatabaseHelper databaseHelper;
    ProgressDialog pd = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;

        databaseHelper = new DatabaseHelper(this,"account.db3");
        et_phoneNumber = (EditText)findViewById(R.id.et_usertel);
        et_password = (EditText)findViewById(R.id.et_password);
        Button register = (Button)findViewById(R.id.btn_register);
        EditText et_code = (EditText)findViewById(R.id.et_code);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regisetrAccount();
            }
        });
    }

    public void regisetrAccount(){
        final String phone = et_phoneNumber.getText().toString().trim();
        final String password = et_password.getText().toString().trim();
        if (android.text.TextUtils.isEmpty(phone) || android.text.TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "请填写核心信息！",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isMobileNO(phone)) {
            Toast.makeText(RegisterActivity.this, "请使用手机号码注册账户！ ", Toast.LENGTH_SHORT).show();
            return;
        }
        pd = new ProgressDialog(this);
        pd.setMessage("正在注册..");
        pd.show();

        Map<String,String> map = new HashMap<>();
        map.put("image", "false");
        map.put("usernick","test");
        map.put("usertel",phone);
        map.put("password", password);

        LoadDataFromServer loadDataFromServer = new LoadDataFromServer(context,Constants.URL_Register_Tel,map);
        loadDataFromServer.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                try {
                    int code = data.getInteger("code");
                    if (code == 1){
                        String hxid = data.getString("hxid");
                        register(hxid, password);
                    }else if (code == 2) {
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this,
                                "该手机号码已被注册...", Toast.LENGTH_SHORT)
                                .show();
                    } else if (code == 3) {
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this,
                                "服务器端注册失败...", Toast.LENGTH_SHORT)
                                .show();
                    } else if (code == 4) {
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this,
                                "头像传输失败...", Toast.LENGTH_SHORT).show();
                    } else if (code == 5) {
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this,
                                "返回环信id失败...", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this,
                                "服务器繁忙请重试...", Toast.LENGTH_SHORT)
                                .show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "数据解析错误...",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void register(final String hxid,final String password){
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMChatManager.getInstance().createAccountOnServer(hxid, password);
                    TextUtils.putStringValue(context, "hxid", hxid);
                    TextUtils.putStringValue(context, "password", password);
                    finish();
                } catch (final EaseMobException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            int errorCode=e.getErrorCode();

                            if(errorCode== EMError.NONETWORK_ERROR){
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.USER_ALREADY_EXISTS){
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.UNAUTHORIZED){
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }
}
