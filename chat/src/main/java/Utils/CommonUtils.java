package Utils;

/**
 * Created by Administrator on 2016/2/20.
 */
public class CommonUtils {
    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
}
