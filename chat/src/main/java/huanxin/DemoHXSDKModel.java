package huanxin;

import android.content.Context;

import java.util.Map;

import Utils.TextUtils;
import domain.User;
import domain.UserDao;


/**
 * Created by Administrator on 2016/1/14.
 */
public class DemoHXSDKModel extends DefaultHXSDKModel {
    private Context context;
    public DemoHXSDKModel(Context context){
        this.context = context;
    }
    // demo will switch on debug mode
    @Override
    public boolean isDebugMode(){
        return true;
    }

    public Map<String, User> getContactList() {
        // TODO Auto-generated method stub
        UserDao dao = new UserDao(context);
        return dao.getContactList();
    }

    @Override
    public String getHXId() {
        return TextUtils.getStringValue(context, "hxid");
    }


}
