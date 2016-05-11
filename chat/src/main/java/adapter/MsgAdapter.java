package adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Spannable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.PathUtil;
import com.example.administrator.chat.Constants;
import com.example.administrator.chat.R;

import Utils.SmileUtils;
import Utils.TextUtils;
import others.BitmapCache;
import others.LoadImageTask;
import others.LoadUserHeadImage;
import others.VoicePlayer;

/**
 * Created by Administrator on 2016/1/22.
 */
public class MsgAdapter extends BaseAdapter {
   // private ArrayList<MessageDao> list = new ArrayList<>();
    private Context context;
    private EMConversation conversation;
    private BitmapCache cache;
    private Activity activity;

    public MsgAdapter(Context context,EMConversation conversation,Bundle bundle){
        this.context = context;
        this.activity = (Activity)context;
        this.conversation = conversation;
    }
    @Override
    public int getCount() {
        return conversation.getMsgCount();
    }

    @Override
    public EMMessage getItem(int position) {
        return conversation.getMessage(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EMMessage message = conversation.getMessage(position);
        ViewHolder viewHolder = new ViewHolder();
        //String hxid = TextUtils.getStringValue(context, "hxid");
        String myAvatarPath = "/sdcard/chat/" + TextUtils.getStringValue(
                context, Constants.HeadImage_Name);
        Bitmap myBitmap = BitmapFactory.decodeFile(myAvatarPath);


        if (message.direct == EMMessage.Direct.SEND  && message.getType() == EMMessage.Type.TXT) {
            convertView = View.inflate(context, R.layout.row_send_message, null);
            TextMessageBody txtBody = (TextMessageBody) message.getBody();
            Spannable spannable = SmileUtils.replace(context,txtBody.getMessage());
            
            initView(convertView,viewHolder);
            viewHolder.tv_msg = (TextView) convertView.findViewById(R.id.tv_send_msg);

            viewHolder.tv_msg.setText(spannable);
            viewHolder.iv_avatar.setImageBitmap(myBitmap);

            convertView.setTag(viewHolder);
        }
        if (message.direct == EMMessage.Direct.SEND  && message.getType() == EMMessage.Type.IMAGE){
            convertView = View.inflate(context, R.layout.row_send_image, null);
            initView(convertView,viewHolder);
            viewHolder.iv_msg = (ImageView)convertView.findViewById(R.id.iv_send_msg);

            viewHolder.iv_avatar.setImageBitmap(myBitmap);
            convertView.setTag(viewHolder);
        }
        if (message.direct == EMMessage.Direct.SEND && message.getType() == EMMessage.Type.VOICE){
            convertView = View.inflate(context, R.layout.row_send_voice, null);
            initView(convertView,viewHolder);
            viewHolder.iv_voice = (ImageView)convertView.findViewById(R.id.iv_voice);
            viewHolder.iv_avatar.setImageBitmap(myBitmap);
            viewHolder.tv_time = (TextView)convertView.findViewById(R.id.tv_time);
            viewHolder.tv_time.setVisibility(View.VISIBLE);

            VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
            viewHolder.tv_time.setText(voiceBody.getLength() + "\"");
            convertView.setTag(viewHolder);
        }
        if (message.direct == EMMessage.Direct.SEND && message.getType() == EMMessage.Type.LOCATION){
            convertView = View.inflate(context, R.layout.row_send_location, null);
            initView(convertView, viewHolder);
            viewHolder.tv_msg = (TextView)convertView.findViewById(R.id.tv_send_msg);
            viewHolder.iv_avatar.setImageBitmap(myBitmap);

            LocationMessageBody locationBody = (LocationMessageBody)message.getBody();
            String locationAddress = locationBody.getAddress();
            viewHolder.tv_msg.setText(locationAddress);
            convertView.setTag(viewHolder);
        }
        if (message.direct == EMMessage.Direct.RECEIVE && message.getType() == EMMessage.Type.TXT){
            convertView = View.inflate(context,R.layout.row_receive_txt,null);
            viewHolder.iv_avatar = (ImageView)convertView.findViewById(R.id.iv_receive_avatar);
            viewHolder.tv_msg = (TextView)convertView.findViewById(R.id.tv_receive_msg);
            try {
                String headImage = message.getStringAttribute("myAvatar");
                showUserHeadImage(viewHolder.iv_avatar, headImage);
                TextMessageBody textBoday = (TextMessageBody)message.getBody();
                Spannable spannable = SmileUtils.replace(context, textBoday.getMessage());
                viewHolder.tv_msg.setText(spannable);
            } catch (EaseMobException e) {
                viewHolder.iv_avatar.setImageResource(R.drawable.head);
                TextMessageBody textBoday = (TextMessageBody)message.getBody();
                Spannable spannable = SmileUtils.replace(context,textBoday.getMessage());
                viewHolder.tv_msg.setText(spannable);
                e.printStackTrace();
            }
            convertView.setTag(viewHolder);
        }
        if (message.direct == EMMessage.Direct.RECEIVE && message.getType() == EMMessage.Type.IMAGE){
            convertView = View.inflate(context,R.layout.row_receive_image,null);
            viewHolder.iv_avatar = (ImageView)convertView.findViewById(R.id.iv_receive_avatar);
            viewHolder.iv_msg = (ImageView)convertView.findViewById(R.id.iv_receive_msg);
            try {
                String headImage = message.getStringAttribute("myAvatar");
                showUserHeadImage(viewHolder.iv_avatar, headImage);
            } catch (EaseMobException e) {
                viewHolder.iv_avatar.setImageResource(R.drawable.head);
                e.printStackTrace();
            }
            convertView.setTag(viewHolder);
        }
        if (message.direct == EMMessage.Direct.RECEIVE && message.getType() == EMMessage.Type.VOICE){
            convertView = View.inflate(context,R.layout.row_receive_voice,null);
            viewHolder.iv_avatar = (ImageView)convertView.findViewById(R.id.iv_receive_avatar);
            viewHolder.iv_voice = (ImageView)convertView.findViewById(R.id.iv_voice);
            try {
                String headImage = message.getStringAttribute("myAvatar");
                showUserHeadImage(viewHolder.iv_avatar, headImage);
            } catch (EaseMobException e) {
                viewHolder.iv_avatar.setImageResource(R.drawable.head);
                e.printStackTrace();
            }
            convertView.setTag(viewHolder);
        }
        if (message.direct == EMMessage.Direct.RECEIVE && message.getType() == EMMessage.Type.LOCATION){
            convertView = View.inflate(context,R.layout.row_receive_location,null);
            viewHolder.iv_avatar = (ImageView)convertView.findViewById(R.id.iv_receive_avatar);
            viewHolder.tv_msg = (TextView)convertView.findViewById(R.id.tv_receive_msg);
            try {
                String headImage = message.getStringAttribute("myAvatar");
                showUserHeadImage(viewHolder.iv_avatar, headImage);
                LocationMessageBody locationBody = (LocationMessageBody)message.getBody();
                String locationAddress = locationBody.getAddress();
                viewHolder.tv_msg.setText(locationAddress);
            } catch (EaseMobException e) {
                viewHolder.iv_avatar.setImageResource(R.drawable.head);
                LocationMessageBody locationBody = (LocationMessageBody)message.getBody();
                String locationAddress = locationBody.getAddress();
                viewHolder.tv_msg.setText(locationAddress);
                e.printStackTrace();
            }
            convertView.setTag(viewHolder);
        }

        //执行消息
        switch (message.getType()) {
            case IMAGE:
                handleImageMessage(message,viewHolder);
                break;
            case TXT:
               handleTxtMessage(message,viewHolder);
                break;
            case VOICE:
                handleVoiceMessage(message,viewHolder);
                break;
            case LOCATION:
                handleLocationMessage(message,viewHolder);
                break;

        }
        return convertView;
    }

    private void showImage(ImageView iv,EMMessage message,String filePath,String thumbRemoteUrl){
        String thumbImageName;
        String thumbnailImagePath;
        String imageName = filePath.substring(filePath.lastIndexOf("/")+1,filePath.length());
        if(thumbRemoteUrl != null){
            thumbImageName= thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1,
                    thumbRemoteUrl.length());
            thumbnailImagePath = PathUtil.getInstance().getImagePath()+"/"+ "th" + thumbImageName;
        } else {
            thumbnailImagePath = PathUtil.getInstance().getImagePath()+"/"+ "th" + imageName;
        }
        Bitmap bitmap = BitmapCache.getInstance().getBitmapFromMemCache(thumbnailImagePath);
        if (bitmap != null){
            iv.setImageBitmap(bitmap);
        } else {
            new LoadImageTask().execute(thumbnailImagePath, filePath,
                     iv, message,activity );
        }
    }

    private void initView(View convertView,ViewHolder viewHolder){
        viewHolder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
        viewHolder.iv_status = (ImageView) convertView.findViewById(R.id.msg_status);
        viewHolder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_send_avatar);
    }

    private void handleTxtMessage(EMMessage message,ViewHolder holder){
        if (message.direct == EMMessage.Direct.SEND){
            switch (message.status){
                case SUCCESS:
                    holder.pb.setVisibility(View.GONE);
                    holder.iv_status.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.pb.setVisibility(View.GONE);
                    holder.iv_status.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.iv_status.setVisibility(View.GONE);
                    break;
                default:
                    // 发送消息
                    sendMsgInBackground(message, holder);
            }
        }
    }

    private void handleImageMessage(EMMessage message,ViewHolder holder){
        if (message.direct == EMMessage.Direct.SEND){
            try {
                String filePath = message.getStringAttribute("filePath");
                if (filePath != null)
                    showImage(holder.iv_msg,message,filePath,null);
            } catch (EaseMobException e) {
                e.printStackTrace();
            }
            switch (message.status){
                case SUCCESS:
                    holder.pb.setVisibility(View.GONE);
                    holder.iv_status.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.pb.setVisibility(View.GONE);
                    holder.iv_status.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.iv_status.setVisibility(View.GONE);
                    break;
                default:
                    // 发送消息
                    sendPictureInBackground(message, holder);
            }
        }
        if (message.direct == EMMessage.Direct.RECEIVE){
            ImageMessageBody imageBody = (ImageMessageBody)message.getBody();
            if (message.status == EMMessage.Status.INPROGRESS){
                holder.iv_msg.setImageResource(R.drawable.default_image);
            } else {
                try {
                    String filePath = message.getStringAttribute("filePath");
                    String remotePath = imageBody.getRemoteUrl();

                    String thumbRemoteUrl = imageBody.getThumbnailUrl();
                    holder.iv_msg.setImageResource(R.drawable.default_image);
                    //showImage(holder.iv_msg, message, filePath);
                    showImage(holder.iv_msg, message, remotePath,thumbRemoteUrl);
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleVoiceMessage(EMMessage message,ViewHolder holder){
        if (message.direct == EMMessage.Direct.SEND){
            switch (message.status){
                case SUCCESS:
                    holder.pb.setVisibility(View.GONE);
                    holder.iv_status.setVisibility(View.GONE);
                    holder.iv_voice.setOnClickListener(new
                            VoicePlayer(message, holder.iv_voice, activity,this));
                    break;
                case FAIL:
                    holder.pb.setVisibility(View.GONE);
                    holder.iv_status.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.iv_status.setVisibility(View.GONE);
                    break;
                default:
                    // 发送消息
                    sendVoiceInBackground(message, holder);
            }
        } else {
            holder.iv_voice.setOnClickListener(new
                    VoicePlayer(message, holder.iv_voice, activity,this));
        }
    }

    private void handleLocationMessage(EMMessage message,ViewHolder holder){
        if (message.direct == EMMessage.Direct.SEND){
            switch (message.status){
                case SUCCESS:
                    holder.pb.setVisibility(View.GONE);
                    holder.iv_status.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.pb.setVisibility(View.GONE);
                    holder.iv_status.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.iv_status.setVisibility(View.GONE);
                    break;
                default:
                    // 发送消息
                    sendLocationInBackground(message, holder);
            }
        }
        if (message.direct == EMMessage.Direct.RECEIVE){
            return ;
        }
    }

    private void sendMsgInBackground(final EMMessage message, final ViewHolder holder){
        holder.pb.setVisibility(View.VISIBLE);
        holder.iv_status.setVisibility(View.GONE);
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {
                updateSendedView();
            }

            @Override
            public void onError(int i, String s) {
                updateSendedView();
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

    private void sendPictureInBackground(final EMMessage message, final ViewHolder holder){
        holder.pb.setVisibility(View.VISIBLE);
        holder.iv_status.setVisibility(View.GONE);
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {
                updateSendedView();
            }

            @Override
            public void onError(int i, String s) {
                updateSendedView();
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

    private void sendVoiceInBackground(final EMMessage message, final ViewHolder holder){
        holder.pb.setVisibility(View.VISIBLE);
        holder.iv_status.setVisibility(View.GONE);
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {
                updateSendedView();
            }

            @Override
            public void onError(int i, String s) {
                updateSendedView();
            }

            @Override
            public void onProgress(int i, String s) {
                updateSendedView();
            }
        });
    }

    private void sendLocationInBackground(final EMMessage message, final ViewHolder holder){
        holder.pb.setVisibility(View.VISIBLE);
        holder.iv_status.setVisibility(View.GONE);
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {
                updateSendedView();
            }

            @Override
            public void onError(int i, String s) {
                updateSendedView();
            }

            @Override
            public void onProgress(int i, String s) {
                updateSendedView();
            }
        });
    }
    private void updateSendedView(){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private int calculateInSampleSize(BitmapFactory.Options opt,
                                      int reqWidth,int reqHeight){
        final int height = opt.outHeight;
        final int width = opt.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width >reqWidth){
            final int halfHeight = height / 2;
            final int halfWidth = width / 2 ;
            while((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth){
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static class ViewHolder{
        private ImageView iv_avatar;
        private ImageView iv_status;
        private ImageView iv_voice;
        private ImageView iv_msg;
        private ProgressBar pb;
        private TextView tv_msg;
        private TextView tv_time;
    }

    private void showUserHeadImage(final ImageView imageView,String imageName){
        if (imageName == null || imageName.equals("")){
            imageView.setImageResource(R.drawable.head);
            return;
        }
        final String url_avatar = Constants.URL_Avatar + imageName;
        LoadUserHeadImage loadUserHeadImage =
                new LoadUserHeadImage(context,"/sdcard/chat/");
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
