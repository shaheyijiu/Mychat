package others;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;

import com.example.administrator.chat.AddFriends;
import com.example.administrator.chat.R;



/**
 * Created by Administrator on 2016/1/17.
 */
public class AddPopupWindows  extends PopupWindow{
    public AddPopupWindows(final Activity context){
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_dialog,null);

        this.setContentView(layout);
        this.setWidth(LayoutParams.WRAP_CONTENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        this.setAnimationStyle(R.style.AnimationPreview);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        LinearLayout addFriends = (LinearLayout)layout.findViewById(R.id.ll_addfriends);
        LinearLayout chatRoom = (LinearLayout)layout.findViewById(R.id.ll_chatroom);

        addFriends.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, AddFriends.class));
                AddPopupWindows.this.dismiss();
            }
        });

        chatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void showPopupWindow(View parent){
        if (!this.isShowing()){
            this.showAsDropDown(parent, -20, 0);
        } else {
            this.dismiss();
        }
    }
}
