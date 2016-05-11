package fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.andview.refreshview.XRefreshView;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.util.NetUtils;
import com.example.administrator.chat.ChatActivity;
import com.example.administrator.chat.LoginActivity;
import com.example.administrator.chat.MainActivity;
import com.example.administrator.chat.R;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import Utils.TextUtils;
import adapter.ConversionAdapter;
import adapter.FragmentAdapter;
import app.App;
import domain.User;

/**
 * Created by Administrator on 2016/1/18.
 */
public class Fragment_Home extends Fragment {
    private View layout;
    private LinearLayout errorItem;
    private TextView noChat;
    private ListView listView;
    private List<EMConversation> conversionList = new ArrayList<>();
    private ConversionAdapter adapter;
    private XRefreshView refreshView;
    private String[] array = new String[]{"删除该聊天"};
    public static long lastRefreshTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = this.getActivity().getLayoutInflater().inflate(R.layout.fragment_home, null);
        errorItem = (LinearLayout)layout.findViewById(R.id.ll_errorItem);
        noChat = (TextView)layout.findViewById(R.id.txt_nochat);
        refreshView = (XRefreshView) layout.findViewById(R.id.custom_view);

        listView = (ListView)layout.findViewById(R.id.listview);
        // 设置是否可以下拉刷新
        refreshView.setPullRefreshEnable(true);
        // 设置是否可以上拉加载
        refreshView.setPullLoadEnable(false);
        // 设置上次刷新的时间
        refreshView.restoreLastRefreshTime(lastRefreshTime);
        // 设置时候可以自动刷新
        refreshView.setAutoRefresh(false);
        refreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshView.stopRefresh();
                        lastRefreshTime = refreshView.getLastRefreshTime();
                    }
                }, 2000);
            }

            @Override
            public void onLoadMore(boolean isSlience) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refreshView.stopLoadMore();
                    }
                }, 2000);
            }

            @Override
            public void onRelease(float direction) {
                super.onRelease(direction);
                if (direction > 0) {
                    toast("下拉");
                } else {
                    toast("上拉");
                }
            }
        });
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        //注册一个监听连接状态的listener
        EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
    }

    public void initView() {

        if (!TextUtils.isNetWorkConnected(getContext())) {
            errorItem.setVisibility(View.VISIBLE);
        }
        loadConversation();
        if (conversionList.size() == 0){
            noChat.setVisibility(View.VISIBLE);
        } else {
            adapter = new ConversionAdapter(getContext(),R.layout.conversion_list_item,conversionList);
            listView.setAdapter(adapter);
            setListener();
        }

    }

    private void setListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversionList.get((int) id);
                String userName = conversation.getUserName();
                User user = App.getInstance().getContactList().get(userName);
                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                if (user != null) {
                    String hxid = user.getUserName();
                    String avatar = user.getHeadImage();
                    String nick = user.getUsernick();
                    bundle.putString("hxid", hxid);
                    bundle.putString("avatar", avatar);
                    bundle.putString("nick", nick);

                    intent.putExtras(bundle);
                    intent.setClass(getActivity(), ChatActivity.class);
                    startActivity(intent);
                } else {
                    String hxid = conversation.getUserName();
                    bundle.putString("hxid", hxid);
                    bundle.putString("nick", "admin");
                    intent.putExtras(bundle);
                    intent.setClass(getActivity(), ChatActivity.class);
                    startActivity(intent);
                }

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                EMConversation conversation = conversionList.get((int) id);
                final String userName = conversation.getUserName();
                AlertDialog dialog = new AlertDialog.Builder(getContext()).setItems(array,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which == 0) {
                                    //删除和某个user的整个的聊天记录(包括本地)
                                    EMChatManager.getInstance().deleteConversation(userName);
                                    refresh();
                                }
                    }
                }).create();
                dialog.show();
                return true;
            }
        });
    }

    public List<EMConversation> loadConversation(){
        conversionList.clear();
        Hashtable<String, EMConversation> conversations =
                EMChatManager.getInstance().getAllConversations();
        Enumeration e = conversations.elements();
        while( e. hasMoreElements() ){
            EMConversation conversation = (EMConversation)e.nextElement();
            if (!conversionList.contains(conversation)){
                conversionList.add(conversation);
            }
        }
        return conversionList;
    }

    public void refresh(){
        conversionList.clear();
        loadConversation();
        adapter = new ConversionAdapter(getContext(),R.layout.conversion_list_item,conversionList);
        listView.setAdapter(adapter);
        setListener();
    }

    public void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
            //已连接到服务器
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout errorItem = (LinearLayout) FragmentAdapter.fragments.get(0).getView().
                            findViewById(R.id.ll_errorItem);
                    errorItem.setVisibility(View.GONE);
                }
            });
        }
        @Override
        public void onDisconnected(final int error) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                        Dialog alertDialog = new AlertDialog.Builder(getActivity()).
                                setMessage("您的账号在其他账号已被移除").setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MainActivity.instance.finish();
                                        startActivity(new Intent(getActivity(), LoginActivity.class));
                                    }
                                }).create();
                        alertDialog.show();
                    } else if (error == EMError.CONNECTION_CONFLICT) {
                        // 显示帐号在其他设备登陆
                        Dialog alertDialog = new AlertDialog.Builder(getActivity()).
                                setMessage("您的账号在其他账号已登陆").setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MainActivity.instance.finish();
                                        startActivity(new Intent(getActivity(), LoginActivity.class));
                                    }
                                }).create();
                        alertDialog.show();
                    } else {
                        if (NetUtils.hasNetwork(getActivity())) {
                            //连接不到聊天服务器
                            Dialog alertDialog = new AlertDialog.Builder(getActivity()).
                                    setMessage("连接不到服务器").setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).create();
                            alertDialog.show();
                        } else {
                            //当前网络不可用，请检查网络设置
                            LinearLayout errorItem = (LinearLayout) FragmentAdapter.fragments.get(0).getView().
                                    findViewById(R.id.ll_errorItem);
                            errorItem.setVisibility(View.VISIBLE);
                        }

                    }
                }
            });
        }
    }


}
