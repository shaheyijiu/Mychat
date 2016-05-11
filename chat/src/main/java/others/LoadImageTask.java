package others;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.easemob.chat.EMMessage;
import com.easemob.util.ImageUtils;
import com.easemob.util.PathUtil;

/**
 * Created by Administrator on 2016/2/19.
 */
public class LoadImageTask extends AsyncTask<Object,Void,Bitmap> {
    EMMessage message;
    private ImageView iv;
    @Override
    protected Bitmap doInBackground(Object... params) {
        String ThumbnailImagePath = (String)params[0];
        String filePath = (String)params[1];
        iv = (ImageView)params[2];
        message = (EMMessage)params[3];
        Activity activity = (Activity)params[4];

        final Bitmap bitmap = BitmapCache.getInstance().getBitmapFromMemCache(ThumbnailImagePath);
        if (bitmap != null){
            return bitmap;
        } else {
            if (message.direct == EMMessage.Direct.SEND){
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(filePath,opt);
                opt.inSampleSize = calculateInSampleSize(opt,80,80);

                //这次再真正地生成一个有像素的，经过缩放了的bitmap
                opt.inJustDecodeBounds = false;
                final Bitmap bitmap1 = BitmapFactory.decodeFile(filePath, opt);
                BitmapCache.getInstance().addBitmapToMemoary(ThumbnailImagePath,bitmap1);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv.setImageBitmap(bitmap1);
                    }
                });
                return bitmap1;
            } else {
                String imageName = filePath.substring(filePath.lastIndexOf("/")+1,filePath.length());
                String path = PathUtil.getInstance().getImagePath()+"/"+ "th" + imageName;
                Bitmap bitmap1 = ImageUtils.decodeScaleImage(path, 80, 80);
                if (bitmap1 == null){
                    System.out.println("loadImageTask bitmap1 为空");
                }
                return bitmap1;
            }

        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null){
            iv.setImageBitmap(bitmap);
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

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
