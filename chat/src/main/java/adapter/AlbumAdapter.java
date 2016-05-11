package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.administrator.chat.R;

import java.util.List;

/**
 * Created by Administrator on 2016/3/10.
 */
public class AlbumAdapter extends ArrayAdapter {
    private List<String> list;
    private Context context;
    private int  resource;
    public AlbumAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.list = objects;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(resource,null);
        }
        viewHolder.imageView = (ImageView)convertView.findViewById(R.id.iv_item);

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(list.get(position),opt);
        opt.inSampleSize = calculateInSampleSize(opt,80,80);

        //这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        final Bitmap bitmap1 = BitmapFactory.decodeFile(list.get(position), opt);
        //viewHolder.imageView.setImageBitmap(list.get(position));
        viewHolder.imageView.setImageBitmap(bitmap1);
        convertView.setTag(viewHolder);
        return convertView;
    }
    public static class ViewHolder{
        ImageView imageView;
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
}
