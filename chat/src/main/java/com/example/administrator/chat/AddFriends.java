package com.example.administrator.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2016/1/17.
 */
public class AddFriends extends Activity implements View.OnClickListener{
    private RelativeLayout addFriends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends);
        CloseActivityClass.activityList.add(this);
        initView();
        initListener();
    }

    private void initView(){
        addFriends = (RelativeLayout)findViewById(R.id.rl_search);
    }
    private void initListener(){
        addFriends.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.rl_search:
                Intent intent = new Intent(AddFriends.this,AddFriendsTwo.class);
                startActivity(intent);
                break;
        }
    }
}
