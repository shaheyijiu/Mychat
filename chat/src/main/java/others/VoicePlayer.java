package others;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.VoiceMessageBody;
import com.example.administrator.chat.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2016/2/20.
 */
public class VoicePlayer implements OnClickListener {
    private EMMessage message;
    private ImageView iv;
    private Activity activity;
    private MediaPlayer mediaPlayer;
    private VoiceMessageBody voiceBody;
    private AnimationDrawable voiceAnimation = null;
    private Context context;
    private BaseAdapter adapter;
    private boolean isPlaying = false;

    public VoicePlayer(EMMessage message,ImageView iv,Activity activity,BaseAdapter adapter){
        this.message = message;
        this.iv = iv;
        this.activity = activity;
        this.adapter = adapter;
        voiceBody = (VoiceMessageBody) message.getBody();
    }
    @Override
    public void onClick(View v) {
        if (message.direct == EMMessage.Direct.SEND){
            play(voiceBody.getLocalUrl());
        } else {
            if (message.status == EMMessage.Status.SUCCESS){
                File file = new File(voiceBody.getLocalUrl());
                if (file.exists() && file.isFile()){
                    play(voiceBody.getLocalUrl());
                } else {
                    System.err.println(" VoicePlayer file not exist");
                }
            }else if (message.status == EMMessage.Status.INPROGRESS) {
                Toast.makeText(activity, "正在下载语音，稍后点击", Toast.LENGTH_SHORT).show();
            } else if (message.status == EMMessage.Status.FAIL) {
                Toast.makeText(activity, "正在下载语音，稍后点击", Toast.LENGTH_SHORT).show();
                new AsyncTask<Void,Void,Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {
                        EMChatManager.getInstance().asyncFetchMessage(message);
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);
                        adapter.notifyDataSetChanged();
                    }

                }.execute();
            }
        }

    }

    private void play(String filePath){
        mediaPlayer = new MediaPlayer();
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        if (EMChatManager.getInstance().getChatOptions().getUseSpeaker()) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        } else {
            audioManager.setSpeakerphoneOn(false);// 关闭扬声器
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    stop();
                }
            });
            isPlaying = true;
            mediaPlayer.start();
            showAnimation();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void showAnimation(){
        if (message.direct == EMMessage.Direct.SEND){
            iv.setImageResource(R.drawable.voice_to_icon);
        } else {
            iv.setImageResource(R.drawable.voice_from_icon);
        }
        voiceAnimation = (AnimationDrawable) iv.getDrawable();
        voiceAnimation.start();
    }

    private void stop(){
        voiceAnimation.stop();
        if (message.direct == EMMessage.Direct.RECEIVE) {
            iv.setImageResource(R.drawable.chatfrom_voice_playing);
        } else {
            iv.setImageResource(R.drawable.chatto_voice_playing);
        }
        // stop play voice
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        isPlaying = false;
    }
}
