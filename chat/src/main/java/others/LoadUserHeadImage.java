package others;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import android.os.Handler;

import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/1/7.
 */
public class LoadUserHeadImage {
    // 最大线程数
    private static final int MAX_THREAD_NUM = 5;
    private FileUtil fileUtil;//文件缓存
    private BitmapCache bitmapCache;//内存缓存
    // 线程池
    private ExecutorService threadPools = null;

    public  LoadUserHeadImage(Context context,String local_image_path){
        bitmapCache = new BitmapCache();
        fileUtil = new FileUtil(context,local_image_path);
        threadPools = Executors.newFixedThreadPool(MAX_THREAD_NUM );
    }


    /**
     * 从内存，SD卡，网络读取头像
     * @param imageView
     * @param imageUrl
     * @param imageDownloadedCallBack
     * @return
     */
    @SuppressLint("HandlerLeak")
    public Bitmap loadImage(final ImageView imageView,final String imageUrl,
                        final ImageDownloadedCallBack imageDownloadedCallBack){
        final String fileName = imageUrl.substring(imageUrl.lastIndexOf("/")+1);
        final String filepath = fileUtil.getAbsolutePath() + fileName;

        //先从内存缓存中获取Bitmap
        Bitmap bitmap = bitmapCache.getBitmapFromMemCache(imageUrl);
        if(bitmap != null){
            return bitmap;
        } else {
        }

        // 从文件中找
        if (fileUtil.isBitmapExists(fileName)){
            android.net.Uri uri=android.net.Uri.parse(filepath);
            String kk = uri.getEncodedPath();
            bitmap = BitmapFactory.decodeFile(kk);
            bitmapCache.addBitmapToMemoary(imageUrl, bitmap);
            return bitmap;
        }

        //从网络中下载
        if (imageUrl != null && !imageUrl.equals("")){
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 111 && imageDownloadedCallBack != null){
                        Bitmap bitmap2 = (Bitmap)msg.obj;
                        imageDownloadedCallBack.onImageDownloaded(imageView,bitmap2);
                    }
                }
            };
            Thread thread = new Thread(){
                @Override
                public void run() {
                    HttpServer httpServer = HttpServer.getInstance();
                    InputStream inputStream = httpServer.getStream(imageUrl);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = 5;
                    Bitmap bitmap1 = BitmapFactory.decodeStream(inputStream,null,options);

                    if (bitmap1 != null){
                        bitmapCache.addBitmapToMemoary(imageUrl, bitmap1);
                        fileUtil.saveBitmap(fileName, bitmap1);

                        Message message = new Message();
                        message.what = 111;
                        message.obj = bitmap1;
                        handler.sendMessage(message);
                    }
                }
            };
            threadPools.execute(thread);
        }
        return null;
    }

    /**
     * 图片下载完成回调接口
     *
     */
    public interface ImageDownloadedCallBack {
        void onImageDownloaded(ImageView imageView, Bitmap bitmap);

    }
}
