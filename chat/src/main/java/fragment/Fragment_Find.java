package fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.chat.MipcaActivityCapture;
import com.example.administrator.chat.R;



/**
 * Created by Administrator on 2016/1/8.
 */
public class Fragment_Find extends Fragment implements View.OnClickListener{
    private final static int SCANNIN_GREQUEST_CODE = 1;
    private View layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = this.getActivity().getLayoutInflater().inflate(R.layout.fragment_find,null);
        initListener();
        return layout;
    }

    public void initListener(){
        layout.findViewById(R.id.tv_scan).setOnClickListener(this);//扫一扫
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tv_scan:
                Intent intent = new Intent();
                intent.setClass(getActivity(), MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if(resultCode == -1){
                }
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }
}
