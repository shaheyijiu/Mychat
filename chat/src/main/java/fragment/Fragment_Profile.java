package fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.chat.AlbumActivity;
import com.example.administrator.chat.Constants;
import com.example.administrator.chat.R;
import com.example.administrator.chat.Setting;

import Utils.TextUtils;
import account.MyInformation;
import others.LoadUserHeadImage;

/**
 * Created by Administrator on 2015/12/25.
 */
public class Fragment_Profile extends Fragment implements View.OnClickListener {
    private TextView tv_nick;
    private View layout;
    private ImageView headImage;
    private String headImageName;
    private LoadUserHeadImage loadUserHeadImage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = this.getActivity().getLayoutInflater().inflate(R.layout.fragment_profile,null);
        loadUserHeadImage = new LoadUserHeadImage(getContext(),"/sdcard/chat/");
        initView();
        initListener();
        return layout;
    }

    //从sharedPreference中读出用户基本信息并进行显示
    public void initView(){
        tv_nick = (TextView)layout.findViewById(R.id.tv_nick);//昵称
        TextView tv_msg = (TextView)layout.findViewById(R.id.tvmsg);//微信号
        headImage = (ImageView)layout.findViewById(R.id.head);//用户头像

        headImageName = TextUtils.getStringValue(getContext(), Constants.HeadImage_Name);
        String id = TextUtils.getStringValue(getActivity(), Constants.User_Id);
        String nick = TextUtils.getStringValue(getContext(), "nick");

        tv_nick.setText(nick);
        tv_msg.setText("微信号：" + id);

        tv_nick.setText(TextUtils.getStringValue(getContext(), "nick"));
        showUserHeadImage(headImage, headImageName);
    }

    public void initListener(){
        layout.findViewById(R.id.view_user).setOnClickListener(this);//设置
        layout.findViewById(R.id.txt_album).setOnClickListener(this);//自己的朋友圈
        layout.findViewById(R.id.txt_collect).setOnClickListener(this);//收藏
        layout.findViewById(R.id.txt_money).setOnClickListener(this);//钱包
        layout.findViewById(R.id.txt_card).setOnClickListener(this);//卡包
        layout.findViewById(R.id.txt_smail).setOnClickListener(this);//表情
        layout.findViewById(R.id.txt_setting).setOnClickListener(this);//设置
    }
    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case R.id.view_user:
                intent = new Intent(getActivity(), MyInformation.class);
                startActivity(intent);
                break;
            case R.id.txt_setting:
                intent = new Intent(getActivity(), Setting.class);
                startActivity(intent);
                break;
            case R.id.txt_album:
                intent = new Intent(getActivity(), AlbumActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_collect:
                break;
            case R.id.txt_money:
                break;
            case R.id.txt_card:
                break;
            case R.id.txt_smail:
                break;
        }
    }

    private void showUserHeadImage(final ImageView imageView,String imageName){
        final String url_avatar = Constants.URL_Avatar + imageName;
        imageView.setTag(url_avatar);
        if (url_avatar != null && !url_avatar.equals("")) {
            Bitmap bitmap = loadUserHeadImage.loadImage(imageView, url_avatar,
                    new LoadUserHeadImage.ImageDownloadedCallBack() {

                        @Override
                        public void onImageDownloaded(ImageView imageView,
                                                      Bitmap bitmap) {
                            if (imageView.getTag() == url_avatar) {
                                imageView.setImageBitmap(bitmap);

                            }
                        }

                    });
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onResume() {
        headImageName = TextUtils.getStringValue(getContext(), Constants.HeadImage_Name);
        showUserHeadImage(headImage, headImageName);
        refresh();
        super.onResume();
    }

    private void refresh(){
        String newNick = TextUtils.getStringValue(getContext(), "nick");
        tv_nick.setText(newNick);
    }
}
