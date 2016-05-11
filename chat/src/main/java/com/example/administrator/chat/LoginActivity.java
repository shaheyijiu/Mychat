package com.example.administrator.chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.TextUtils;
import app.App;
import domain.User;
import domain.UserDao;
import others.LoadDataFromServer;

/**
 * Created by Administrator on 2015/12/4.
 */
public class LoginActivity extends Activity implements View.OnClickListener{
    private Button register;
    private Button login;
    private EditText et_phoneNumber;
    private EditText et_password;
    private Context context;
    ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        context = this;
        initControl();
        setListener();
    }

    public void initControl(){
        register = (Button) findViewById(R.id.btn_qtregister);
        login = (Button)findViewById(R.id.btn_qtlogin);
        et_phoneNumber = (EditText) findViewById(R.id.phone);
        et_password = (EditText) findViewById(R.id.password);
    }
    public void setListener(){
        register.setOnClickListener(this);
        login.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_qtlogin:
                login();
                break;
            case R.id.btn_qtregister:
                startActivity(new Intent(LoginActivity.this,
                        RegisterActivity.class));
        }
    }
    public void login(){
        String phone = et_phoneNumber.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (android.text.TextUtils.isEmpty(phone) || android.text.TextUtils.isEmpty(password)) {
            Toast.makeText(context, "请填写核心信息！",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isMobileNO(phone)) {
            Toast.makeText(context, "用户名错误！ ", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String,String> map = new HashMap<>();
        map.put("usertel",phone);
        map.put("password", password);

        pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage(getString(R.string.Is_landing));
        pd.show();

        TextUtils.putStringValue(context, Constants.User_Id, phone);
        LoadDataFromServer loadDataFromServer = new LoadDataFromServer(context,Constants.URL_Login,map);
        loadDataFromServer.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                try {
                    int code = data.getInteger("code");
                    if (code == 1) {
                        JSONObject json = data.getJSONObject("user");
                        loginInHX(json);
                    } else if (code == 2) {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this,
                                "账号或密码错误...", Toast.LENGTH_SHORT)
                                .show();
                    } else if (code == 3) {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this,
                                "服务器端注册失败...", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this,
                                "服务器繁忙请重试...", Toast.LENGTH_SHORT)
                                .show();
                    }
                } catch (Exception e) {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this, "数据解析错误...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    public void loginInHX(final JSONObject json){
        final String nick = json.getString("nick");
        final String hxid = json.getString("hxid");
        final String password = json.getString("password");
        EMChatManager.getInstance().login(hxid, password, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                try {
                    // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                    // ** manually load all local groups and
                    //因为只限手机号注册，所以强制将手机号作为id
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();
                    TextUtils.putStringValue(context, "nick", nick);
                    TextUtils.putStringValue(context, "hxid", hxid);
                    TextUtils.putStringValue(context, "password", password);
                    TextUtils.putBooleanValue(context, Constants.Login_State, true);
                    // 处理好友和群组
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initializeContacts(json);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    // 取好友或者群聊失败，不让进入主页面
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            // DemoHXSDKHelper.getInstance().logout(true,null);
                            // Toast.makeText(getApplicationContext(), R.string.login_failure_failed, 1).show();
                        }
                    });
                    return;
                }
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(final int code, final String message) {
                if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void initializeContacts(final JSONObject json){
        List<String> usernames = null;
        try {
            usernames = EMContactManager.getInstance().getContactUserNames();
        } catch (EaseMobException e) {
            e.printStackTrace();
        }
        if (usernames != null && usernames.size() > 0){
            String totalUsername = usernames.get(0);
            for (int i = 1;i < usernames.size();i++){
                String split = "66split88";
                totalUsername += split + usernames.get(i);
            }

            totalUsername = totalUsername.replace(Constants.NEW_FRIENDS_USERNAME, "");
            totalUsername = totalUsername.replace(Constants.GROUP_USERNAME, "");

            Map<String, String> map = new HashMap<String, String>();
            map.put("uids", totalUsername);

            LoadDataFromServer loadDataFromServer = new LoadDataFromServer(LoginActivity.this,
                                Constants.URL_Friends,map);
            loadDataFromServer.getData(new LoadDataFromServer.DataCallBack() {
                @Override
                public void onDataCallBack(JSONObject data) {
                    int code = data.getInteger("code");
                    if (code == 1) {
                        JSONArray josnArray = data.getJSONArray("friends");
                        saveMyInfo(json);
                        saveFriends(josnArray);
                        // 进入主页面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (code == 2) {
                        Toast.makeText(LoginActivity.this, "获取好友列表失败,请重试...", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Toast.makeText(LoginActivity.this, "服务器繁忙请重试...", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
        } else {
            saveMyInfo(json);
            saveFriends(null);
            // 进入主页面
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

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
                        nick = "test";
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
        UserDao dao = new UserDao(LoginActivity.this);
        dao.clearContactList();
        List<User> users = new ArrayList<User>(map.values());
        dao.saveContactList(users);
    }

    private void saveMyInfo(JSONObject json){
        try {
            String hxid = json.getString("hxid");
            String fxid = json.getString("fxid");
            String nick = json.getString("nick");
            String avatar = json.getString("avatar");
            String password = json.getString("password");
            String sex = json.getString("sex");
            String region = json.getString("region");
            String sign = json.getString("sign");
            String tel = json.getString("tel");

            TextUtils.putStringValue(LoginActivity.this, "hxid", hxid);
            TextUtils.putStringValue(LoginActivity.this, "fxid", fxid);
            TextUtils.putStringValue(LoginActivity.this, "nick", nick);
            TextUtils.putStringValue(LoginActivity.this, Constants.HeadImage_Name, avatar);
            TextUtils.putStringValue(LoginActivity.this, "password", password);
            TextUtils.putStringValue(LoginActivity.this, "sex", sex);
            TextUtils.putStringValue(LoginActivity.this, "region", region);
            TextUtils.putStringValue(LoginActivity.this, "sign", sign);
            TextUtils.putStringValue(LoginActivity.this, "tel", tel);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }
}
