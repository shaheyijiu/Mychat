package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.example.administrator.chat.Constants;
import com.example.administrator.chat.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import Utils.SmileUtils;
import app.App;
import domain.User;
import others.LoadUserHeadImage;

/**
 * Created by Administrator on 2016/2/23.
 */
public class ConversionAdapter extends ArrayAdapter<EMConversation> {
    private Context context;
    private List<EMConversation> conversionList;
    private int resource;
    private LoadUserHeadImage loadUserHeadImage;
    public ConversionAdapter(Context context, int resource, List<EMConversation> list) {
        super(context, resource, list);
        this.context = context;
        this.resource = resource;
        this.conversionList = list;
        loadUserHeadImage = new LoadUserHeadImage(getContext(),"/sdcard/chat/");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(resource,null);
        }
        viewHolder.avatar = (ImageView)convertView.findViewById(R.id.iv_headImage);
        viewHolder.tvNick = (TextView)convertView.findViewById(R.id.tv_nick);
        viewHolder.lastMsg = (TextView)convertView.findViewById(R.id.tv_last_msg);
        viewHolder.tvDate = (TextView)convertView.findViewById(R.id.tv_date);
        TextView unread = (TextView)convertView.findViewById(R.id.tv_unread);

        EMConversation conversation = getItem(position);
        EMMessage message = conversation.getLastMessage();
        String userName = conversation.getUserName();
        User user = App.getInstance().getContactList().get(userName);
        if (user != null){
            String nick = user.getUsernick();
            String headImage = user.getHeadImage();
            viewHolder.tvNick.setText(nick);
            showUserHeadImage(viewHolder.avatar,headImage);
        } else {
            if (message.direct == EMMessage.Direct.SEND){
                try {
                    String nick = message.getStringAttribute("toNick");
                    String headImage = message.getStringAttribute("toAvatar");
                    viewHolder.tvNick.setText(nick);
                    showUserHeadImage(viewHolder.avatar,headImage);
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    String nick = message.getStringAttribute("myNick");
                    String headImage = message.getStringAttribute("myAvatar");
                    viewHolder.tvNick.setText(nick);
                    showUserHeadImage(viewHolder.avatar,headImage);
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }

            }
        }
        if (message.getType() == EMMessage.Type.LOCATION){
            viewHolder.lastMsg.setText("[我的位置]");
        } else if (message.getType() == EMMessage.Type.VOICE){
            viewHolder.lastMsg.setText("[语言]");
        } else if (message.getType() == EMMessage.Type.IMAGE){
            viewHolder.lastMsg.setText("[图片]");
        } else if (message.getType() == EMMessage.Type.TXT){
            TextMessageBody txtBody = (TextMessageBody) message.getBody();
            String content = txtBody.getMessage();
            viewHolder.lastMsg.setText(SmileUtils.replace(getContext(),content));
        }
        Date date = new Date(message.getMsgTime());
        DateFormat df1 = DateFormat.getDateInstance();
        viewHolder.tvDate.setText(df1.format(date));

        return convertView;
    }
    public static class ViewHolder{
        private ImageView avatar;
        private TextView tvDate;
        private TextView tvNick;
        private TextView lastMsg;

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
