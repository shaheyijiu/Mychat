package account;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.chat.AddFriendsThree;
import com.example.administrator.chat.ChatActivity;
import com.example.administrator.chat.CloseActivityClass;
import com.example.administrator.chat.Constants;
import com.example.administrator.chat.R;

import Utils.TextUtils;
import app.App;
import domain.User;
import others.LoadUserHeadImage;

/**
 * Created by Administrator on 2016/1/18.
 */
public class UserInformation extends Activity {
    private ImageView iv_back;
    private Button addFriends;
    private String avatar;
    private String sex;
    private String nick;
    private Boolean isFriend = false;
    private LoadUserHeadImage loadUserHeadImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_userinformation);
        CloseActivityClass.activityList.add(this);

        loadUserHeadImage = new LoadUserHeadImage(this,"/sdcard/chat/");

        final String hxid = this.getIntent().getStringExtra("hxid");
        avatar = this.getIntent().getStringExtra("avatar");
        sex = this.getIntent().getStringExtra("sex");
        nick = this.getIntent().getStringExtra("nick");

        if (hxid != null){
            if (App.getInstance().getContactList().containsKey(hxid)){
                isFriend = true;
            }
        }
        initView();
        initListener(hxid);
    }

    public void add(String hxid){
        if (hxid.equals(TextUtils.getStringValue(this, "hxid"))){
            Toast.makeText(getApplicationContext(), "不能和自己聊天。。",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (isFriend){
            Toast.makeText(getApplicationContext(), "已经是好友。。",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("hxid",hxid);
        intent.setClass(UserInformation.this, AddFriendsThree.class);
        startActivity(intent);
    }

    public void initView(){
        ImageView iv_head = (ImageView)findViewById(R.id.iv_head);
        TextView tv_nick = (TextView)findViewById(R.id.tv_nick);
        ImageView tv_sex = (ImageView)findViewById(R.id.iv_sex);
        iv_back = (ImageView)findViewById(R.id.iv_back);
        addFriends = (Button)findViewById(R.id.bt_add);


        if (avatar == null || avatar.equals("") || avatar.equals("0")){
            iv_head.setImageResource(R.drawable.head);
        } else {
            showUserHeadImage(iv_head, avatar);
        }
        if (nick == null || nick.equals("")){
            tv_nick.setText("test");
        } else {
            tv_nick.setText(nick);
        }
        if (sex == null || sex.equals("")){
            tv_sex.setImageResource(R.drawable.ic_sex_male);
        } else if (sex.equals("male")){
            tv_sex.setImageResource(R.drawable.ic_sex_male);
        } else {
            tv_sex.setImageResource(R.drawable.ic_sex_female);
        }

        if(isFriend){
            addFriends.setText("发送消息");
        }

    }

    public void initListener(final String hxid){
        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFriend){
                    add(hxid);
                } else {
                    User user = App.getInstance().getContactList().get(hxid);
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent();

                    String hxid = user.getUserName();
                    String avatar = user.getHeadImage();
                    String nick = user.getUsernick();
                    bundle.putString("hxid", hxid);
                    bundle.putString("avatar", avatar);
                    bundle.putString("nick", nick);
                    intent.putExtras(bundle);
                    intent.setClass(UserInformation.this, ChatActivity.class);
                    startActivity(intent);
                }
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void showUserHeadImage(ImageView imageView,String headImageName ){
        final String url_headImage = Constants.URL_Avatar + headImageName;
        imageView.setTag(url_headImage);
        if(url_headImage != null && !url_headImage.equals("")){
            Bitmap bitmap = loadUserHeadImage.loadImage(imageView, url_headImage, new LoadUserHeadImage.ImageDownloadedCallBack() {
                @Override
                public void onImageDownloaded(ImageView imageView, Bitmap bitmap) {
                    if(imageView.getTag() == url_headImage){
                        imageView.setImageBitmap(bitmap);
                    }
                }
            });
            if (bitmap != null){
                imageView.setImageBitmap(bitmap);//内存或者SD卡返回的头像
            }
        }

    }

    public void back(View view){
        finish();
    }
}
