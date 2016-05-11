package others;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by Administrator on 2016/1/7.
 */
public class BitmapCache {
    private LruCache<String, Bitmap> mMemoryCache;
    private static BitmapCache bitmapCache = null;

    public BitmapCache(){
        int maxMemory = (int)Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String,Bitmap>(mCacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    public static synchronized BitmapCache getInstance() {
        if (bitmapCache == null) {
            bitmapCache = new BitmapCache();
        }
        return bitmapCache;

    }

    public void addBitmapToMemoary(String key, Bitmap bitmap){
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从内存缓存中获取一个Bitmap
     * @param key
     * @return
     */
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
