package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.administrator.chat.Constants;
import com.example.administrator.chat.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import domain.User;
import others.LoadUserHeadImage;

/**
 * Created by Administrator on 2016/1/11.
 */
public class ContactAdapter extends ArrayAdapter<User> implements SectionIndexer{
    private int resource;
    private List<User> userList;
    private LoadUserHeadImage loadUserHeadImage;
    private ArrayList<String> allHeadChar = new ArrayList<>();
    public HashMap<String,Integer> charIndex = new HashMap<>();


    public ContactAdapter(Context context, int resource,List<User> objects) {
        super(context, resource,objects);
        this.resource = resource;
        userList = objects;
        loadUserHeadImage = new LoadUserHeadImage(context,"/sdcard/chat/");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(resource,null);
        }
        ImageView userImage = (ImageView)convertView.findViewById(R.id.iv_headImage);
        TextView userNick = (TextView)convertView.findViewById(R.id.iv_userName);

        User user = userList.get(position);

        String nick = user.getUsernick();
        if (nick == null || nick.equals("")) {
            userNick.setText("test");
        } else {
            userNick.setText(nick);
        }

        String headImageName = user.getHeadImage();

        if (headImageName == null || headImageName.equals("") || headImageName.equals("0")){
            userImage.setImageResource(R.drawable.head);
        } else {
            showUserHeadImage(userImage,headImageName);
        }

        String headChar = user.getHeadChar();
        if (position != 0 ){
            if (headChar.equals(userList.get(position-1).getHeadChar())){
                (convertView.findViewById(R.id.tv_head)).setVisibility(View.GONE);
                convertView.findViewById(R.id.gap).setVisibility(View.VISIBLE);
            } else {
                allHeadChar.add(headChar);
                charIndex.put(headChar,position);
                (convertView.findViewById(R.id.tv_head)).setVisibility(View.VISIBLE);
                ((TextView)convertView.findViewById(R.id.tv_head)).setText(user.getHeadChar());
                (convertView.findViewById(R.id.gap)).setVisibility(View.GONE);
            }
        } else {
            allHeadChar.add(headChar);
            charIndex.put(headChar,position);
            convertView.findViewById(R.id.tv_head).setVisibility(View.VISIBLE);
            ((TextView)convertView.findViewById(R.id.tv_head)).setText(user.getHeadChar());
            convertView.findViewById(R.id.gap).setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
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
