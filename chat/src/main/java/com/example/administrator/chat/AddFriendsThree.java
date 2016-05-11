package com.example.administrator.chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;

import Utils.TextUtils;

/**
 * Created by Administrator on 2016/1/18.
 */
public class AddFriendsThree extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriendsthree);

        Button send = (Button)findViewById(R.id.bt_send);
        final EditText et = (EditText)findViewById(R.id.et_msg);
        final String hxid = this.getIntent().getStringExtra("hxid");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = et.getText().toString().trim();
                askForFriend(hxid,msg);
            }
        });
    }

    private void askForFriend(final String hxid,final String msg){
        final ProgressDialog dialog = new ProgressDialog(AddFriendsThree.this);
        dialog.setMessage("正在发送...");
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String nick = TextUtils.getStringValue(AddFriendsThree.this, "nick");
                String avatar = TextUtils.getStringValue(AddFriendsThree.this, Constants.HeadImage_Name);
                long time = System.currentTimeMillis();
                String myReason = msg;
                if (msg == null && msg.equals("")){
                    myReason = "请求加你为好友";
                }
                if (avatar == null || avatar.equals("")){
                    avatar = "0000";//表示没有设置头像
                }
                final String reason = nick + "66split88" + avatar + "66split88"
                        + String.valueOf(time)+"66split88" + myReason;
                try{
                    System.out.println("发送的理由是" + reason);
                    EMContactManager.getInstance().addContact(hxid, reason);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "发送请求成功,等待对方验证", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "发送请求成功,等待对方验证", Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    });
                }

            }
        }).start();
    }

    public void back(View view){
        finish();
    }
}
