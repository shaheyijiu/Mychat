package com.example.administrator.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;

import Utils.TextUtils;

/**
 * Created by Administrator on 2016/1/16.
 */
public class Setting extends Activity implements View.OnClickListener{
    private String[] array = new String[]{"退出当前账号","关闭微信"};
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initListener();
    }

    private void initListener(){
        TextView quit = (TextView)findViewById(R.id.tv_quit);
        quit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.tv_quit:
                dialog = new AlertDialog.Builder(this).
                        setItems(array, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 1){
                                    quitAccount();
                                }
                            }
                        }).create();
                dialog.show();
                break;
        }
    }

    private void quitAccount(){
        dialog.cancel();
        final ProgressDialog pd = new ProgressDialog(Setting.this);
        pd.setMessage("正在退出登陆..");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        EMChatManager.getInstance().logout(new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Setting.this.runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        TextUtils.putBooleanValue(Setting.this, Constants.Login_State, false);
//                        Intent intent = new Intent();
//                        intent.setClass(Setting.this, MainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //注意本行的FLAG设置//startActivity(intent);
                        // 重新显示登陆页面
                        MainActivity.instance.finish();
                        finish();
                        startActivity(new Intent(Setting.this, LoginActivity.class));

                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub

            }
        });
    }
}
