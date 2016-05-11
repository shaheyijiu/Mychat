package com.example.administrator.chat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import adapter.NewFriendAdapter;
import domain.InviteMessage;
import domain.InviteMessageDao;

/**
 * Created by Administrator on 2016/1/19.
 */
public class NewFriendsActivity extends Activity {
    private InviteMessageDao msgDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newfriends);
        msgDao = new InviteMessageDao(this);
        init();
    }

    public void init(){
        List<InviteMessage> inviteMessageList = new ArrayList<>();
        ListView listView = (ListView)findViewById(R.id.listview);
        inviteMessageList = msgDao.getMessageList();
        NewFriendAdapter adapter = new NewFriendAdapter(this,inviteMessageList);
        listView.setAdapter(adapter);
    }

    public void back(View view){
        finish();
    }
}
