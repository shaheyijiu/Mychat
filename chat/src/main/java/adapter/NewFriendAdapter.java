package adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.example.administrator.chat.Constants;
import com.example.administrator.chat.R;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.App;
import domain.InviteMessage;
import domain.InviteMessageDao;
import domain.User;
import domain.UserDao;
import others.LoadDataFromServer;
import others.LoadUserHeadImage;

/**
 * Created by Administrator on 2016/1/19.
 */
public class NewFriendAdapter extends BaseAdapter {
    private List<InviteMessage> msgList;
    private Context context;
    private LoadUserHeadImage loadUserHeadImage;
    private InviteMessageDao messgeDao;
    public NewFriendAdapter(Context context,List<InviteMessage> msgList){
        this.context = context;
        this.msgList = msgList;
        messgeDao = new InviteMessageDao(context);
        loadUserHeadImage = new LoadUserHeadImage(context,"/sdcard/chat/");
    }
    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public InviteMessage getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = View.inflate(context, R.layout.item_new_friends,null);
        }
        final TextView tv_agree = (TextView)convertView.findViewById(R.id.tv_agree);
        final Button bt_agree = (Button)convertView.findViewById(R.id.bt_agree);
        TextView tv_reason = (TextView)convertView.findViewById(R.id.tv_reason);
        TextView tv_from = (TextView)convertView.findViewById(R.id.tv_name);
        ImageView headImage = (ImageView)convertView.findViewById(R.id.iv_head);

        final InviteMessage msg = msgList.get(position);

        String reason_total = msg.getReason();
        String[] array = reason_total.split("66split88");
        if (array.length == 4){
            String nick = array[0];
            String headImageName = array[1];
            String reason = array[3];
            tv_from.setText(nick);
            if (headImageName.equals("0000")|| headImageName == null || headImageName.equals("0")){
                headImage.setImageResource(R.drawable.head);
            } else {
                showUserHeadImage(headImage,headImageName);
            }
            tv_reason.setText(reason);
        } else {
            tv_from.setText("test");
            headImage.setImageResource(R.drawable.head);
            tv_reason.setText("请求加为好友");
        }
        if (msg.getStatus() != InviteMessage.InviteMesageStatus.AGREED &&
                msg.getStatus() != InviteMessage.InviteMesageStatus.BEAGREED){
            tv_agree.setVisibility(View.GONE);
            bt_agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptInvitation(msg,bt_agree,tv_agree);
                }
            });
        } else {
            tv_agree.setVisibility(View.VISIBLE);
            bt_agree.setVisibility(View.GONE);
        }
        return convertView;
    }
    private void acceptInvitation(final InviteMessage msg,final Button bt,final TextView tv){
        final String from = msg.getFrom();
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("正在同意...");
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMChatManager.getInstance().acceptInvitation(from);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setVisibility(View.VISIBLE);
                            bt.setVisibility(View.GONE);
                            dialog.dismiss();
                            msg.setStatus(InviteMessage.InviteMesageStatus.AGREED);

                            // 更新db
                            ContentValues values = new ContentValues();
                            values.put(InviteMessageDao.COLUMN_NAME_STATUS, msg
                                    .getStatus().ordinal());
                            messgeDao.updateMessage(msg.getId(), values);

                            addFriendToList(msg.getFrom());
                        }
                    });
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            dialog.dismiss();
                            Toast.makeText(context, "同意失败: " ,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
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
                        UserDao dao = new UserDao(context);
                        dao.saveContact(user);

                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }

        });

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
}
