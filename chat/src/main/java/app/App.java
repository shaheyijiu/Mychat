package app;

import android.app.Application;
import java.util.Map;
import domain.User;
import huanxin.DemoHXSDKHelper;

/**
 * Created by Administrator on 2015/12/24.
 */
public class App extends Application {
    private  static App instance;
    private static DemoHXSDKHelper hxsdkHelper = new DemoHXSDKHelper();
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        hxsdkHelper.onInit(this);
    }
    public static App getInstance(){
        return instance;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, User> contactList) {
        hxsdkHelper.setContactList(contactList);
    }
    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, User> getContactList() {
        return hxsdkHelper.getContactList();
    }

    /**
     * 获取当前登陆用户名
     *
     * @return
     */
    public String getUserName() {
        return hxsdkHelper.getHXId();
    }
}
