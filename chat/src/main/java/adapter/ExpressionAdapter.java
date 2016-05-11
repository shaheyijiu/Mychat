package adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.administrator.chat.R;

import java.util.List;

/**
 * Created by Administrator on 2016/1/25.
 */
public class ExpressionAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> list;
    public ExpressionAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.context = context;
        this.list = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = View.inflate(context, R.layout.row_expression,null);

            String expressionName = getItem(position);
            int id = context.getResources().getIdentifier(expressionName,"drawable",getContext().getPackageName());
            ImageView iv = (ImageView)convertView.findViewById(R.id.iv_expression);
            iv.setImageResource(id);
        }
        return convertView;
    }
}
