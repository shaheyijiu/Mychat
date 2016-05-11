package com.example.administrator.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.FragmentAdapter;
import app.App;
import domain.InviteMessage;
import domain.InviteMessageDao;
import domain.User;
import domain.UserDao;
import fragment.Fragment_Home;
import others.AddPopupWindows;
import others.LoadDataFromServer;
import view.MainBottomTabIcon;

/**
 * Created by Administrator on 2015/12/4.
 */
public class MainActivity extends ActionBarActivity implements View.OnClickListener{
    private ImageButton ib_add;
    private InviteMessageDao msgDao;
    private List<String> usernames;
    public static MainActivity instance = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        instance = this;
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        msgDao = new InviteMessageDao(this);
        initListener();
        initBroadcast();
        //msgDao.deleteAllMessage();
    }


    public void initBroadcast(){
        // 注册一个接收消息的BroadcastReceiver
        NewMessageBroadcastReceiver msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager
                .getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);

        // 注册一个ack回执消息的BroadcastReceiver
        IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager
                .getInstance().getAckMessageBroadcastAction());
        ackMessageIntentFilter.setPriority(3);
        registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

        // 注册一个透传消息的BroadcastReceiver
//        IntentFilter cmdMessageIntentFilter = new IntentFilter(EMChatManager
//                .getInstance().getCmdMessageBroadcastAction());
//        cmdMessageIntentFilter.setPriority(3);
//        registerReceiver(cmdMessageReceiver, cmdMessageIntentFilter);

        // 注册一个离线消息的BroadcastReceiver
        // IntentFilter offlineMessageIntentFilter = new
        // IntentFilter(EMChatManager.getInstance()
        // .getOfflineMessageBroadcastAction());
        // registerReceiver(offlineMessageReceiver, offlineMessageIntentFilter);

        // setContactListener监听联系人的变化等
        EMContactManager.getInstance().setContactListener(
                new MyContactListener());

        // 注册群聊相关的listener
        //EMGroupManager.getInstance().addGroupChangeListener(
        //        new MyGroupChangeListener());
        // 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
        EMChat.getInstance().setAppInited();
    }
    public void initListener(){
        ib_add = (ImageButton)findViewById(R.id.top_add);
        ImageButton ib_search = (ImageButton)findViewById(R.id.top_search);
        ib_search.setOnClickListener(this);
        ib_add.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.top_add:
                AddPopupWindows addPopupWindows = new AddPopupWindows(MainActivity.this);
                addPopupWindows.showPopupWindow(ib_add);
                break;
            case R.id.top_search:

                break;
        }
    }

    public static class PlaceholderFragment extends Fragment{
        private FragmentAdapter mAdapter;
        private ViewPager mPager;
        private MainBottomTabIcon mTabLayout;

        public PlaceholderFragment(){

        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            setupViews(rootView);
            return rootView;
        }

        private void setupViews(View view) {
            mAdapter = new FragmentAdapter(getFragmentManager());
            mPager = (ViewPager) view.findViewById(R.id.tab_pager);
            mPager.setOffscreenPageLimit(4);
            mPager.setAdapter(mAdapter);
            mTabLayout = (MainBottomTabIcon) view.findViewById(R.id.main_bottom_tablayout);
            mTabLayout.setViewPager(mPager);
        }
    }

    /**
     * 新消息广播接收
     */
    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 注销广播
            abortBroadcast();

            // 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
            String msgId = intent.getStringExtra("msgid");
            //发送方
            String username = intent.getStringExtra("from");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            EMConversation conversation = EMChatManager.getInstance().getConversation(username);
            Fragment_Home home= (Fragment_Home)FragmentAdapter.fragments.get(0);
            home.refresh();
        }
    }

    /**
     * 接收回执消息广播
     */
    private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();
            String msgid = intent.getStringExtra("msgid");
            String from = intent.getStringExtra("from");
            EMConversation conversation = EMChatManager.getInstance().getConversation(from);
            if (conversation != null) {
                // 把message设为已读
                EMMessage msg = conversation.getMessage(msgid);
                if (msg != null) {
                    msg.isAcked = true;
                }
            }

        }
    };

    private class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(List<String> list) {
            //updateContactList();
            //System.out.println("好友增多");
        }

        @Override
        public void onContactDeleted(List<String> list) {

        }

        @Override
        public void onContactInvited(String username, String reason) {
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            // 设置相应status
            msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
            System.out.println("监听到用户请求好友" + username);
            System.out.println("接受到的理由是" + reason);
            if (msg == null || (msg.getReason()).equals("")){

            } else {
                notifyNewIviteMessage(msg);
            }

        }

        @Override
        public void onContactAgreed(String username) {
            System.out.println("好友申请被接受");
            addFriendToList(username);
        }

        @Override
        public void onContactRefused(String s) {

        }
    }

    public void notifyNewIviteMessage(InviteMessage msg){
        msgDao.deleteAllMessage();
        msgDao.saveMessage(msg);
    }

    public void updateContactList(){
        try {
            usernames = EMContactManager.getInstance().getContactUserNames();

        } catch (EaseMobException e) {
            e.printStackTrace();
        }
        String totalUsername = usernames.get(0);
        for (int i = 1;i < usernames.size();i++){
            String split = "66split88";
            totalUsername += split + usernames.get(i);
        }
        totalUsername = totalUsername.replace(Constants.NEW_FRIENDS_USERNAME, "");
        totalUsername = totalUsername.replace(Constants.GROUP_USERNAME, "");

        Map<String, String> map = new HashMap<String, String>();
        map.put("uids", totalUsername);

        LoadDataFromServer loadDataFromServer = new LoadDataFromServer(MainActivity.this,
                Constants.URL_Friends,map);
        loadDataFromServer.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                int id = data.getInteger("code");
                if (id == 1) {
                    JSONArray josnArray = data.getJSONArray("friends");
                    saveFriends(josnArray);
                }
            }
        });
    }

    private void saveFriends(JSONArray josnArray){
        Map<String, User> map = new HashMap<String, User>();
        if (josnArray != null) {
            for (int i = 0; i < josnArray.size(); i++) {
                JSONObject json = josnArray.getJSONObject(i);
                try {
                    String hxid = json.getString("hxid");
                    String fxid = json.getString("fxid");
                    String nick = json.getString("nick");
                    if (nick == null || nick.equals("")){
                        nick = "小柯";
                    }
                    String avatar = json.getString("avatar");
                    String sex = json.getString("sex");
                    String region = json.getString("region");
                    String sign = json.getString("sign");
                    String tel = json.getString("tel");

                    User user = new User();
                    user.setFxid(fxid);
                    user.setUserName(hxid);
                    user.setBeizhu("");
                    user.setUsernick(nick);
                    user.setRegion(region);
                    user.setSex(sex);
                    user.setTel(tel);
                    user.setSign(sign);
                    user.setHeadImage(avatar);
                    map.put(hxid, user);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        // 存入内存
        App.getInstance().setContactList(map);
        // 存入db
        UserDao dao = new UserDao(MainActivity.this);
        dao.clearContactList();
        List<User> users = new ArrayList<User>(map.values());
        dao.saveContactList(users);
    }

    private void addFriendToList(final String hxid) {
        Map<String, String> map_uf = new HashMap<String, String>();
        map_uf.put("hxid", hxid);
        LoadDataFromServer task = new LoadDataFromServer(null,
                Constants.URL_Get_UserInfo, map_uf);
        task.getData(new LoadDataFromServer.DataCallBack() {
            public void onDataCallBack(JSONObject data) {
                try {
                    int code = data.getInteger("code");
                    if (code == 1) {
                        JSONObject json = data.getJSONObject("user");
                        if (json != null && json.size() != 0) {

                        }
                        String nick = json.getString("nick");
                        String avatar = json.getString("avatar");

                        String hxid = json.getString("hxid");
                        String fxid = json.getString("fxid");
                        String region = json.getString("region");
                        String sex = json.getString("sex");
                        String sign = json.getString("sign");
                        String tel = json.getString("tel");

                        User user = new User();
                        user.setUserName(hxid);
                        user.setUsernick(nick);
                        user.setHeadImage(avatar);
                        user.setFxid(fxid);
                        user.setRegion(region);
                        user.setSex(sex);
                        user.setSign(sign);
                        user.setTel(tel);
                        Map<String, User> userlist = App
                                .getInstance().getContactList();
                        Map<String, User> map_temp = new HashMap<String, User>();
                        map_temp.put(hxid, user);
                        userlist.putAll(map_temp);
                        // 存入内存
                        App.getInstance().setContactList(userlist);
                        // 存入db
                        UserDao dao = new UserDao(MainActivity.this);
                        dao.saveContact(user);

                    }

                } catch (com.alibaba.fastjson.JSONException e) {

                    e.printStackTrace();
                }

            }

        });

    }


}
