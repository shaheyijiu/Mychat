package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.administrator.chat.ChatActivity;
import com.example.administrator.chat.NewFriendsActivity;
import com.example.administrator.chat.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;


import adapter.ContactAdapter;
import app.App;
import domain.User;
import others.PinyinHelper;
import view.SideBar;

/**
 * Created by Administrator on 2016/1/11.
 */
public class Fragment_Friends extends Fragment {
    private LinearLayout layout;
    private ArrayList<User> userList = new ArrayList<>();
    private ContactAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = (LinearLayout)inflater.inflate(R.layout.contact_list,container,false);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUser();
        setUser(userList);
        ListView listView = (ListView)layout.findViewById(R.id.list);

        View headView = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_header,null);
        listView.addHeaderView(headView, null, false);
        RelativeLayout addFriends = (RelativeLayout)headView.findViewById(R.id.rl_new_friend);
        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewFriendsActivity.class);
                startActivity(intent);
            }
        });

        View footerView = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_footer,null);
        listView.addFooterView(footerView);

        adapter = new ContactAdapter(getContext(),R.layout.contact_list_item,userList);
        listView.setAdapter(adapter);

        SideBar sideBar = (SideBar) layout.findViewById(R.id.sideBar);
        sideBar.setListView(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = userList.get((int) id);
                String hxid =user.getUserName();
                String avatar =  user.getHeadImage();
                String nick = user.getUsernick();
                Bundle bundle = new Bundle();
                bundle.putString("hxid",hxid);
                bundle.putString("avatar",avatar);
                bundle.putString("nick",nick);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(getActivity(), ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setUser(ArrayList<User> list){
        for (int i = 0;i < list.size();i ++){
            User user = list.get(i);
            String userNick = user.getUsernick();
            String word = PinyinHelper.getPinYinHeadChar(userNick).substring(0, 1);
            String pinyinNick = PinyinHelper.getPingYin(userNick);
            user.setHeadChar(word);
            user.setPinyinNick(pinyinNick);
        }
        Collections.sort(list, comparator);
    }

    public void initUser(){
        userList.clear();
        Map<String,User> map = App.getInstance().getContactList();
        for (User user : map.values()){
            userList.add(user);
        }
    }

    Comparator<User> comparator = new Comparator<User>() {
        @Override
        public int compare(User user1, User user2) {
            int result = ((user1.getPinyinNick()).compareTo(user2.getPinyinNick()) > 0) ? 1 :
                    (((user1.getPinyinNick()).compareTo(user2.getPinyinNick()) == 0) ? 0 : -1);
            return result;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initUser();
                setUser(userList);
                adapter.notifyDataSetChanged();
            }
        });
    }

}
