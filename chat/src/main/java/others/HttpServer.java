package others;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.InputStream;

/**
 * Created by Administrator on 2016/1/7.
 */
public class HttpServer {
    private static HttpServer httpServer;
    private static final int timeoutConnection = 3000;
    private static final int timeoutSocket = 5000;
    public HttpServer(){

    }
    public static HttpServer getInstance(){
        if (httpServer == null){
            httpServer = new HttpServer();
        }
        return httpServer;
    }

    public InputStream getStream(String url){
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, HttpServer.timeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

        HttpClient client = new DefaultHttpClient(httpParams);
        HttpGet get = new HttpGet(url);

        try{
            HttpResponse httpResponse = client.execute(get);
            int status = httpResponse.getStatusLine().getStatusCode();
            if(status == 200){
                return httpResponse.getEntity().getContent();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
