package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.WrapperListAdapter;

import com.easemob.util.DensityUtil;
import com.example.administrator.chat.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import adapter.ContactAdapter;

/**
 * Created by Administrator on 2016/1/13.
 */
public class SideBar extends View {
    private ListView listView;
    private Paint paint;
    private Context context;
    private float height;
    private String[] indexStr = { "↑","☆","A","B","C","D","E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z","#"};

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initPaint();
    }

    public void setListView(ListView listView){
        this.listView = listView;
    }

    public void initPaint(){
        paint = new Paint();
        paint.setColor(Color.DKGRAY);
        paint.setTextSize(DensityUtil.sp2px(context, 10));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        height = getHeight() / indexStr.length;
        float weight = getWidth() / 2;
        for (int i = 0;i < indexStr.length;i++){
            canvas.drawText(indexStr[i],weight,height * (i+1),paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        int index = (int)(y / height);
        String floatChar = indexStr[index];
        TextView textView = (TextView)((View)getParent()).findViewById(R.id.floating_header);
        textView.setText(floatChar);
        textView.setVisibility(View.VISIBLE);

        WrapperListAdapter listAdapter = (WrapperListAdapter)listView.getAdapter();
        ContactAdapter adapter = (ContactAdapter)listAdapter.getWrappedAdapter();
        HashMap<String,Integer> hashMap = adapter.charIndex;
        Iterator iter = hashMap.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String)entry.getKey();

            int value = (int)entry.getValue();
            if (key.equals(floatChar)){
                listView.setSelection(listView.getHeaderViewsCount()+value);
            }
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                setBackgroundColor(Color.parseColor("#606060"));
                break;
            case MotionEvent.ACTION_UP:
                textView.setVisibility(View.INVISIBLE);
                setBackgroundColor(Color.parseColor("#00000000" ));
                break;
            case MotionEvent.ACTION_MOVE:

        }
        return super.onTouchEvent(event);
    }
}
