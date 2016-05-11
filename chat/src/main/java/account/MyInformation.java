package account;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.administrator.chat.Constants;
import com.example.administrator.chat.R;
import com.example.administrator.chat.UpdateNickActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Utils.TextUtils;
import others.LoadDataFromServer;
import others.LoadUserHeadImage;


/**
 * Created by Administrator on 2015/12/25.
 */
public class MyInformation extends Activity implements View.OnClickListener{
    private RelativeLayout userHead;
    private RelativeLayout rlnick;
    private ImageView headImage;
    private String imageName;
    private TextView nick;
    private String hxid;

    private static final int REQUESTCODE_PICK = 1;
    private static final int REQUESTCODE_CUTTING = 2;

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 480;
    private static int output_Y = 480;

    private LoadUserHeadImage  loadUserHeadImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_information);
        loadUserHeadImage = new LoadUserHeadImage(this,"/sdcard/chat/");
        initView();
        initListener();
    }

    public void initView(){
        userHead = (RelativeLayout)findViewById(R.id.head);
        rlnick = (RelativeLayout)findViewById(R.id.rl_nick);

        headImage = (ImageView)findViewById(R.id.head_image);
        nick = (TextView)findViewById(R.id.tv_nick);
        TextView wxid = (TextView)findViewById(R.id.wx_id);

        String headImageName = TextUtils.getStringValue(MyInformation.this, Constants.HeadImage_Name);
        hxid = TextUtils.getStringValue(MyInformation.this, "hxid");

        String id = TextUtils.getStringValue(MyInformation.this, Constants.User_Id);
        wxid.setText(id);

        if (headImageName.equals("0") || headImageName == null){
            headImage.setImageResource(R.drawable.head);
        } else {
            showUserHeadImage(headImage, headImageName);
        }

    }

    public void initListener(){
        userHead.setOnClickListener(this);
        rlnick.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.head :
                getNowTime();
                choseImageFromAblum();
                break;
            case R.id.rl_nick:
                Intent intent = new Intent();
                intent.setClass(MyInformation.this, UpdateNickActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void choseImageFromAblum(){
        getNowTime();
        imageName = getNowTime() + ".png";
        Intent pickIntent = new Intent(Intent.ACTION_PICK,null);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, REQUESTCODE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode){
            case REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPhotoZoom(Uri uri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop","true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", false);

        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File("/sdcard/chat/", imageName)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    private void setPicToView(Intent picdata) {
        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/chat/"
                + imageName);
        headImage.setImageBitmap(bitmap);
        updateUserHeadImage(imageName);
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

    public void updateUserHeadImage(final String imageName){
        Map<String,String> map = new HashMap<String,String>();
        if ((new File("/sdcard/chat/"+imageName)).exists()){
            map.put("file","/sdcard/chat/"+imageName);
            map.put("image", imageName);
        } else {
            return;
        }
        map.put("hxid",hxid);
        LoadDataFromServer loadData = new LoadDataFromServer(
                MyInformation.this,Constants.URL_UPDATE_Avatar,map);
        loadData.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                try {
                    int code = data.getInteger("code");
                    if (code == 1) {
                        TextUtils.putStringValue(MyInformation.this,
                                Constants.HeadImage_Name, imageName);
                    } else if (code == 2) {
                        Toast.makeText(MyInformation.this, "更新失败...",
                                Toast.LENGTH_SHORT).show();
                    } else if (code == 3) {
                        Toast.makeText(MyInformation.this, "图片上传失败...",
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(MyInformation.this, "服务器繁忙请重试...",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(MyInformation.this, "数据解析错误...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

    }

    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh(){
        String newNick = TextUtils.getStringValue(MyInformation.this, "nick");
        nick.setText(newNick);
    }

}
