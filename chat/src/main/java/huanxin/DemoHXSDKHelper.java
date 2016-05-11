package huanxin;

import java.util.Map;

import domain.User;

/**
 * Created by Administrator on 2016/1/14.
 */
public class DemoHXSDKHelper extends HXSDKHelper {
    private Map<String, User> contactList;

    @Override
    protected HXSDKModel createModel() {
        return new DemoHXSDKModel(appContext);
    }


    @Override
    protected void initHXOptions() {
        super.initHXOptions();
        // you can also get EMChatOptions to set related SDK options
        // EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        //此处可以进行自己的SDK配置
    }

    /**
     * get demo HX SDK Model
     */
    public DemoHXSDKModel getModel() {
        return (DemoHXSDKModel) hxModel;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, User> contactList) {
        this.contactList = contactList;
    }

    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, User> getContactList() {
        if (getHXId() != null && contactList == null) {
            contactList = getModel().getContactList();
        }
        return contactList;
    }
}
