package yau.tommy.com.amusic;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class MusicService extends Service{
    private MediaPlayer mMediaPlayer;
    private static final String TAG = "MyService";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");
        mMediaPlayer = new MediaPlayer();

//        player = MediaPlayer.create(this, R.raw.braincandy);
//        player.setLooping(false); // Set looping
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
        mMediaPlayer.stop();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");

        Uri songUri = Uri.parse(intent.getExtras().getString("songUri"));
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mMediaPlayer.setDataSource(this,songUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mMediaPlayer.prepare();
            mMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
