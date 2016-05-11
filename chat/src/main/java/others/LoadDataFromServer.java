package others;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import android.os.Handler;
import android.widget.Toast;

import internal.org.apache.http.entity.mime.MultipartEntity;
import internal.org.apache.http.entity.mime.content.FileBody;
import internal.org.apache.http.entity.mime.content.StringBody;

/**
 * Created by Administrator on 2016/1/7.
 */
public class LoadDataFromServer {
    private String url;
    private Map<String, String> map = null;
    private boolean hasArray;
    private Context context;

    public LoadDataFromServer(Context context, String url, Map<String, String> map) {
        this.url = url;
        hasArray = false;
        this.map = map;
        this.context = context;
    }

    @SuppressLint("HandlerLeak")
    public void getData(final DataCallBack dataCallBack) {
       final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what ==111 && dataCallBack != null){
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if (jsonObject != null) {
                        dataCallBack.onDataCallBack(jsonObject);
                    } else {
                        Toast.makeText(context, "访问服务器出错...", Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();
                MultipartEntity mpEntity = new MultipartEntity();
                Set keys = map.keySet();
                if (keys != null) {
                    Iterator iterator = keys.iterator();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        String value = map.get(key);

                        if (key.equals("file")) {
                            File file = new File(value);
                            FileBody fileBody = new FileBody(file);
                            mpEntity.addPart(key, fileBody);
                        } else {
                            try {
                                mpEntity.addPart(key, new StringBody(value,
                                        Charset.forName("UTF-8")));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (hasArray) {

                }
                httpClient.getParams().setParameter(
                        CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);

                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);

                HttpPost post = new HttpPost(url);
                post.setEntity(mpEntity);
                StringBuilder builder = new StringBuilder();
                try{
                    HttpResponse httpResponse = httpClient.execute(post);
                    if (httpResponse.getStatusLine().getStatusCode() == 200){
                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(httpResponse.getEntity().getContent(),
                                        Charset.forName("UTF-8")));

                        for (String s = bufferedReader.readLine(); s != null; s = bufferedReader
                                .readLine()) {
                            builder.append(s);
                        }
                        String builder_BOM = jsonTokener(builder.toString());
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject = JSONObject.parseObject(builder_BOM);
                            Message msg = new Message();
                            msg.what = 111;
                            msg.obj = jsonObject;
                            handler.sendMessage(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private String jsonTokener(String in) {
        // consume an optional byte order mark (BOM) if it exists
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        return in;
    }
    public interface DataCallBack{
        void onDataCallBack(JSONObject data);
    }
}
