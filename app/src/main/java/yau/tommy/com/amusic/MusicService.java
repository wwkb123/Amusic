package yau.tommy.com.amusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener{
    private MediaPlayer mMediaPlayer;
    private static final String TAG = "MyService";
    private static final String ANDROID_CHANNEL_ID = "yau.tommy.com.amusic.MusicService";
    private static final int NOTIFICATION_ID = 555;
    NotificationManager nm;
    Notification.Builder builder;
    NotificationCompat.Builder compatBuilder;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(new NotificationChannel(ANDROID_CHANNEL_ID, "Amusic", NotificationManager.IMPORTANCE_DEFAULT));
            builder = new Notification.Builder(this, ANDROID_CHANNEL_ID)
                    .setContentTitle("Now Playing")
                    .setContentText("")
                    .setSmallIcon(R.mipmap.amusic_icon)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            startForeground(NOTIFICATION_ID, notification);
        } else {
            compatBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("Now Playing")
                    .setContentText("")
                    .setSmallIcon(R.mipmap.amusic_icon)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            Notification notification = compatBuilder.build();
            startForeground(NOTIFICATION_ID, notification);
        }

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

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");

        Uri songUri = Uri.parse(intent.getExtras().getString("songUri"));
        String songTitle = intent.getExtras().getString("songTitle");

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setContentText(songTitle);
            notification = builder.build();
        }else{
            compatBuilder.setContentText(songTitle);
            notification = compatBuilder.build();
        }
        startForeground(NOTIFICATION_ID, notification);

        if(mMediaPlayer!=null){
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.reset();
            }
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                File filePath = new File(songUri.toString());
                FileInputStream is = new FileInputStream(filePath);
                mMediaPlayer.setDataSource(is.getFD());
            } catch (IOException e) {
                e.printStackTrace();
            }

            mMediaPlayer.prepareAsync();
        }

        return START_STICKY;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Invoked when the media source is ready for playback.
        playMedia();
    }

    private void playMedia() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

}
