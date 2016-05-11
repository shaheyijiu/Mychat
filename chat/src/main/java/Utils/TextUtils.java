package Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/12/4.
 */
public class TextUtils {
    //RegisterActiviter 保存了"hxid"和"password
    /*
     *获取默认的SharedPreferences
     */
    private static SharedPreferences getSharedPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void putIntValue(Context context,String key,int value){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putInt(key,value);
        editor.commit();
    }
    public static void putStringValue(Context context,String key,String value){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static String getStringValue(Context context,String key){
        return getSharedPreference(context).getString(key, "");
    }
    public static int getIntValue(Context context,String key){
        return getSharedPreference(context).getInt(key, 0);
    }
    public static  void putBooleanValue(Context context,String key,Boolean state){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putBoolean(key, state);
        editor.commit();
    }
    public static Boolean getBooleanValue(Context context, String key) {
        return getSharedPreference(context).getBoolean(key, false);
    }

    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(17[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     *判断是否有网络
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }



}
