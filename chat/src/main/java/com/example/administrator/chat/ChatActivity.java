package com.example.administrator.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.VoiceRecorder;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

import Utils.CommonUtils;
import Utils.SmileUtils;
import Utils.TextUtils;
import account.UserInformation;
import adapter.ExpressionAdapter;
import adapter.ExpressionPagerAdapter;
import adapter.MsgAdapter;
import app.App;
import domain.MessageDao;
import domain.User;
import others.FileUtil;

/**
 * Created by Administrator on 2016/1/22.
 */
public class ChatActivity extends Activity implements View.OnClickListener{
    private EditText et_msg;
    private Button bt_send;
    private Button bt_more;
    private Button bt_voice;
    private Button bt_speak;
    private Button bt_keyboard;
    private ImageView iv_detail;
    private ImageView emotion_normal;
    private ImageView emotion_checked;
    private ImageView btn_take_picture;
    private ImageView mic_image;
    private LinearLayout ll_more;
    private LinearLayout ll_btn_container;
    private LinearLayout ll_face_container;
    private View ll_location;
    private RelativeLayout recording_container;

    private ListView listView;
    private ArrayList<MessageDao> messageList = new ArrayList<>();
    private MsgAdapter adapter;
    private EMConversation conversation;
    private String friendHxid;
    private String myAvatar;
    private Bundle bundle;
    private InputMethodManager imm;
    private FileUtil fileUtil;//保存拍照图片
    private String file;
    private VoiceRecorder voiceRecorder;
    private Drawable[] micImages;
    private PowerManager.WakeLock wakeLock;
    private String nick;
    private String[] array = new String[]{"删除"};

    private static final int REQUEST_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_MAP = 2;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mic_image.setImageDrawable(micImages[msg.what]);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chatactivity);
        CloseActivityClass.exitClient();

        initView();
        initListener();
        initControl();
    }

    @SuppressLint("SdCardPath")
    private void initView(){
        et_msg = (EditText)findViewById(R.id.et_msg);
        bt_send = (Button)findViewById(R.id.bt_send);
        bt_more = (Button)findViewById(R.id.bt_more);
        bt_voice = (Button)findViewById(R.id.bt_voice);
        bt_speak = (Button)findViewById(R.id.bt_speak);
        bt_keyboard = (Button)findViewById(R.id.bt_keyboard);
        listView = (ListView)findViewById(R.id.lv_msg);
        emotion_normal = (ImageView)findViewById(R.id.iv_emoticons_normal);
        emotion_checked = (ImageView)findViewById(R.id.iv_emoticons_checked);
        btn_take_picture = (ImageView)findViewById(R.id.btn_take_picture);
        mic_image = (ImageView)findViewById(R.id.mic_image);
        iv_detail = (ImageView)findViewById(R.id.iv_detail);
        ViewPager expressionViewPager = (ViewPager)findViewById(R.id.vPager);
        ll_more = (LinearLayout)findViewById(R.id.ll_more);
        ll_btn_container = (LinearLayout)findViewById(R.id.ll_btn_container);
        ll_face_container = (LinearLayout)findViewById(R.id.ll_face_container);
        recording_container = (RelativeLayout)findViewById(R.id.recording_container);
        ll_location = findViewById(R.id.ll_location);
        TextView tv_nick = (TextView)findViewById(R.id.tv_nick);

        fileUtil = new FileUtil(ChatActivity.this,"/sdcard/chat/picture_capture/");
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");

        voiceRecorder = new VoiceRecorder(handler);
        nick = TextUtils.getStringValue(this, "nick");
        // 动画资源文件,用于录制语音时
        micImages = new Drawable[] {
                getResources().getDrawable(R.drawable.record_animate_01),
                getResources().getDrawable(R.drawable.record_animate_02),
                getResources().getDrawable(R.drawable.record_animate_03),
                getResources().getDrawable(R.drawable.record_animate_04),
                getResources().getDrawable(R.drawable.record_animate_05),
                getResources().getDrawable(R.drawable.record_animate_06),
                getResources().getDrawable(R.drawable.record_animate_07),
                getResources().getDrawable(R.drawable.record_animate_08),
                getResources().getDrawable(R.drawable.record_animate_09),
                getResources().getDrawable(R.drawable.record_animate_10),
                getResources().getDrawable(R.drawable.record_animate_11),
                getResources().getDrawable(R.drawable.record_animate_12),
                getResources().getDrawable(R.drawable.record_animate_13),
                getResources().getDrawable(R.drawable.record_animate_14), };

        View v1 = getGridView(1);
        View v2 = getGridView(2);
        List<View> views = new ArrayList<>();
        views.add(v1);
        views.add(v2);
        ExpressionPagerAdapter expressionPagerAdapter =
                new ExpressionPagerAdapter(this,views);
        expressionViewPager.setAdapter(expressionPagerAdapter);

        bundle = this.getIntent().getExtras();
        friendHxid = bundle.getString("hxid");
        String friendNick = bundle.getString("nick");
        tv_nick.setText(friendNick);
        myAvatar = TextUtils.getStringValue(
                this, Constants.HeadImage_Name);

        conversation = EMChatManager.getInstance().getConversation(
                friendHxid);
    }

    private void initListener(){
        et_msg.setOnClickListener(this);
        bt_send.setOnClickListener(this);
        bt_more.setOnClickListener(this);
        bt_voice.setOnClickListener(this);
        bt_keyboard.setOnClickListener(this);
        btn_take_picture.setOnClickListener(this);
        emotion_normal.setOnClickListener(this);
        emotion_checked.setOnClickListener(this);
        iv_detail.setOnClickListener(this);
        ll_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ChatActivity.this, BaiduMapActivity.class);
                startActivityForResult(intent, REQUEST_CODE_MAP);
            }
        });
        bt_speak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    recordVoice();
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        voiceRecorder.discardRecording();
                    } else {
                        finishRecordVoice();
                    }
                    return false;
                }

                return false;
            }
        });

    }

    private void initControl(){
        adapter = new MsgAdapter(ChatActivity.this,conversation,bundle);
        listView.setAdapter(adapter);
        listView.setSelection(listView.getCount() - 1);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                final String userName = conversation.getUserName();
                EMMessage message = conversation.getMessage((int) id);
                final String msgId = message.getMsgId();
                AlertDialog dialog = new AlertDialog.Builder(ChatActivity.this).setItems(array,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which == 0) {
                                    conversation.removeMessage(msgId);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }).create();
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.et_msg:
                if (ll_more.isShown()){
                    ll_more.setVisibility(View.GONE);
                }
                bt_more.setVisibility(View.GONE);
                bt_send.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_send:
                sendText();
                adapter.notifyDataSetChanged();
                listView.setSelection(listView.getCount() - 1);
                break;
            case R.id.iv_emoticons_normal:
                imm.hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                emotion_normal.setVisibility(View.INVISIBLE);
                emotion_checked.setVisibility(View.VISIBLE);
                ll_more.setVisibility(View.VISIBLE);
                ll_face_container.setVisibility(View.VISIBLE);
                ll_btn_container.setVisibility(View.GONE);
                break;
            case R.id.iv_emoticons_checked:
                emotion_normal.setVisibility(View.VISIBLE);
                emotion_checked.setVisibility(View.INVISIBLE);
                ll_more.setVisibility(View.GONE);
                break;
            case R.id.bt_more:
                imm.hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                ll_more.setVisibility(View.VISIBLE);
                ll_face_container.setVisibility(View.GONE);
                ll_btn_container.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_take_picture:
                String filePath = fileUtil.getAbsolutePath();
                file = filePath + App.getInstance().getUserName()
                                + System.currentTimeMillis() + ".jpg";
                startActivityForResult(
                        new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                                MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(file))),
                        REQUEST_CAPTURE_IMAGE);
                break;
            case R.id.bt_voice:
                imm.hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                bt_keyboard.setVisibility(View.VISIBLE);
                bt_speak.setVisibility(View.VISIBLE);
                et_msg.setVisibility(View.GONE);
                emotion_normal.setVisibility(View.GONE);
                emotion_checked.setVisibility(View.GONE);
                bt_voice.setVisibility(View.GONE);
                break;
            case R.id.bt_keyboard:
                bt_voice.setVisibility(View.VISIBLE);
                et_msg.setVisibility(View.VISIBLE);
                emotion_normal.setVisibility(View.VISIBLE);
                emotion_checked.setVisibility(View.INVISIBLE);
                bt_speak.setVisibility(View.GONE);
                bt_keyboard.setVisibility(View.GONE);
                break;
            case R.id.iv_detail:
                String userName = conversation.getUserName();
                User user = App.getInstance().getContactList().get(userName);
                if(user != null){
                    Intent intent = new Intent(ChatActivity.this, UserInformation.class);
                    intent.putExtra("nick", user.getUsernick());
                    intent.putExtra("avatar",user.getHeadImage());
                    intent.putExtra("sex", user.getSex());
                    intent.putExtra("hxid", user.getUserName());
                    startActivity(intent);
                } else{
                    Toast.makeText(ChatActivity.this,"用户不存在",Toast.LENGTH_SHORT).show();
                }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CAPTURE_IMAGE:
                    if (file == null ){
                        System.out.println("file为空");
                    }else {
                        System.out.println("file不为空");
                    }
                    sendImage(file);
                    break;
                case REQUEST_CODE_MAP:
                    double latitude = data.getDoubleExtra("latitude", 0);
                    double longitude = data.getDoubleExtra("longitude", 0);
                    String locationAddress = data.getStringExtra("address");
                    if (locationAddress != null && !locationAddress.equals("")){
                        sendLocation(locationAddress, latitude, longitude);
                    } else {
                        Toast.makeText(this, "无法获取到您的位置信息！", Toast.LENGTH_SHORT)
                                .show();
                    }
            }
        }
    }

    private void recordVoice(){
        if (!CommonUtils.isExitsSdcard()){
            Toast.makeText(ChatActivity.this, "发送语音需要sdcard支持！",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        wakeLock.acquire();
        recording_container.setVisibility(View.VISIBLE);
        voiceRecorder.startRecording(null, friendHxid, getApplicationContext());
    }

    private void finishRecordVoice(){
        if (wakeLock.isHeld())
            wakeLock.release();
        recording_container.setVisibility(View.INVISIBLE);
        int length = voiceRecorder.stopRecoding();
        if (length > 0) {
            sendVoice(voiceRecorder.getVoiceFilePath(),
                    voiceRecorder.getVoiceFileName(friendHxid),
                    Integer.toString(length), false);
        } else if (length == EMError.INVALID_FILE) {
            Toast.makeText(getApplicationContext(), "无录音权限",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "录音时间太短",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void sendLocation(String locationAddress,double latitude,double longitude){
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
        LocationMessageBody locBody = new LocationMessageBody(locationAddress, latitude, longitude);
        message.addBody(locBody);
        message.setReceipt(friendHxid);
        if(nick != null || nick.equals("")){
            message.setAttribute("myNick", nick);
        }
       if(myAvatar != null || myAvatar.equals("")){
           message.setAttribute("myAvatar",myAvatar);
       }
        //message.setAttribute("toNick",friendNick);
        //message.setAttribute("toAvatar",friendAvatar);
        conversation.addMessage(message);
        adapter.notifyDataSetChanged();
        listView.setSelection(listView.getCount() - 1);
    }

    private void sendVoice(String filePath,String voiceName,String length,boolean isResend){
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
        int len = Integer.parseInt(length);
        VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
        message.addBody(body);
        message.setReceipt(friendHxid);
        if(nick != null || nick.equals("")){
            message.setAttribute("myNick", nick);
        }
        if(myAvatar != null || myAvatar.equals("")){
            message.setAttribute("myAvatar",myAvatar);
        }
       // message.setAttribute("toNick",friendNick);
       // message.setAttribute("toAvatar",friendAvatar);
        conversation.addMessage(message);
        adapter.notifyDataSetChanged();
        listView.setSelection(listView.getCount() - 1);
    }

    private void sendImage(String filePath){
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        ImageMessageBody body = new ImageMessageBody(new File(filePath));
        message.addBody(body);
        message.setAttribute("filePath", filePath);//image保存的路径
        if(nick != null || nick.equals("")){
            message.setAttribute("myNick", nick);
        }
        if(myAvatar != null || myAvatar.equals("")){
            message.setAttribute("myAvatar",myAvatar);
        }
       // message.setAttribute("toNick",friendNick);
        //message.setAttribute("toAvatar",friendAvatar);
        message.setReceipt(friendHxid);
        conversation.addMessage(message);
        adapter.notifyDataSetChanged();
        listView.setSelection(listView.getCount() - 1);
    }

    private void sendText(){
        String msg = et_msg.getText().toString().trim();
        et_msg.setText("");
        if (msg.equals("")||msg == null){
            return;
        }
        //这里是扩展自文本消息，如果这个自定义的消息需要用到语音或者图片等，可以扩展自语音、图片消息，亦或是位置消息。
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        TextMessageBody txtBody = new TextMessageBody(msg);
        message.addBody(txtBody);

//        // 增加自己特定的属性,目前sdk支持int,boolean,String这三种属性，可以设置多个扩展属性
//        message.setAttribute("attribute1", "value");
//        message.setAttribute("attribute2", true);
        if(nick != null || nick.equals("")){
            message.setAttribute("myNick", nick);
        }
        if(myAvatar != null || myAvatar.equals("")){
            message.setAttribute("myAvatar",myAvatar);
        }
       // message.setAttribute("toNick",friendNick);
       // message.setAttribute("toAvatar", friendAvatar);
        message.setReceipt(friendHxid);
        conversation.addMessage(message);
        listView.setSelection(listView.getCount() - 1);
    }

    private ArrayList<String> getExpression(){
        ArrayList<String> list = new ArrayList<>();
        for (int i  = 1;i <= 35;i++){
            String s = "ee_"+i;
            list.add(s);
        }
        return  list;
    }

    private View getGridView(int i){
        View view = View.inflate(this,R.layout.layout_gridview,null);
        GridView gridView = (GridView)view.findViewById(R.id.grid);
        List<String> list = new ArrayList<>();
        if (i == 1){
            list = getExpression().subList(0,20);
        } else {
            list = getExpression().subList(20,getExpression().size());
        }
        final ExpressionAdapter adapter = new ExpressionAdapter(ChatActivity.this,1,list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fileName = adapter.getItem(position);
                try {
                    Class cls = Class.forName("Utils.SmileUtils");
                    Field file = cls.getField(fileName);
                    et_msg.append(SmileUtils.replace(ChatActivity.this,
                            (String)file.get(null)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("按下了back键   onBackPressed()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wakeLock.isHeld())
            wakeLock.release();
        try {
            // 停止录音
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
                recording_container.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void back(View view){
        finish();
    }
}
