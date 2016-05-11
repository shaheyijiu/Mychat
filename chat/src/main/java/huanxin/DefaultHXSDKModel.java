package huanxin;

/**
 * Created by Administrator on 2016/1/14.
 */
public class DefaultHXSDKModel extends HXSDKModel {

    @Override
    public void setSettingMsgNotification(boolean paramBoolean) {

    }

    @Override
    public boolean getSettingMsgNotification() {
        return false;
    }

    @Override
    public void setSettingMsgSound(boolean paramBoolean) {

    }

    @Override
    public boolean getSettingMsgSound() {
        return false;
    }

    @Override
    public void setSettingMsgVibrate(boolean paramBoolean) {

    }

    @Override
    public boolean getSettingMsgVibrate() {
        return false;
    }

    @Override
    public void setSettingMsgSpeaker(boolean paramBoolean) {

    }

    @Override
    public boolean getSettingMsgSpeaker() {
        return false;
    }

    @Override
    public boolean saveHXId(String hxId) {
        return false;
    }

    @Override
    public String getHXId() {
        return null;
    }

    @Override
    public boolean savePassword(String pwd) {
        return false;
    }

    @Override
    public String getPwd() {
        return null;
    }

    @Override
    public String getAppProcessName() {
        return "com.example.administrator.chat";
    }
}
