package others;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2016/1/7.
 */
public class FileUtil {
    private String local_image_path;
    public FileUtil(Context context,String local_image_path){
        this.local_image_path = local_image_path;
    }

    public String getAbsolutePath(){
        File file = new File(local_image_path);
        if (!file.exists()){
            file.mkdirs();
        }
        return local_image_path;
    }

    /**
     * Checks if external storage is available for read and write
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * 保存图片到文件路径中
     */
    public void saveBitmap(String fileName,Bitmap bitmap){
        if (!isExternalStorageWritable()){
            return;
        }
        if(bitmap == null){
            return;
        }

        try{
            File file = new File(local_image_path,fileName);
            FileOutputStream outputStream = new FileOutputStream(file);

            if((fileName.indexOf("png") != -1)||(fileName.indexOf("PNG") != -1))
            {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            }  else{
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            }

            outputStream.flush();
            outputStream.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     *判断图片是否存在于文件路径中
     */
    public boolean isBitmapExists(String filename) {
        File dir =new File(local_image_path);
        if(!dir.exists()){
            dir.mkdirs();
        }
        //context.getExternalFilesDir(null);
        File file = new File(dir, filename);

        return file.exists();
    }
}
