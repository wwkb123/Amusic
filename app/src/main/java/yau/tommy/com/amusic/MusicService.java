package yau.tommy.com.amusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener{
    private MediaPlayer mMediaPlayer;
    private MediaController mediaController;
    private MediaSession mediaSession;
    private MediaSessionManager mediaSessionManager;
    public static final String ACTION_NEXT = "action_next";



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
        Notification.MediaStyle style = new Notification.MediaStyle();



        RemoteViews expendedView = new RemoteViews(this.getPackageName(),R.layout.notification);
        //set the button listeners
        setListeners(this, expendedView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(new NotificationChannel(ANDROID_CHANNEL_ID, "Amusic", NotificationManager.IMPORTANCE_DEFAULT));
            builder = new Notification.Builder(this, ANDROID_CHANNEL_ID)
                    .setContentTitle("Now Playing")
                    .setContentText("")
                    .setSmallIcon(R.drawable.icon)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .addAction(generateAction(R.drawable.baseline_skip_next_black_18dp,"Next",ACTION_NEXT))
                    .setStyle(style)
                    //.setCustomBigContentView(expendedView)
                    .setAutoCancel(true);

            style.setShowActionsInCompactView(0);
            Notification notification = builder.build();
            startForeground(NOTIFICATION_ID, notification);
        } else {
            compatBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("Now Playing")
                    .setContentText("")
                    .setSmallIcon(R.drawable.icon)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            Notification notification = compatBuilder.build();
            startForeground(NOTIFICATION_ID, notification);
        }

//        player = MediaPlayer.create(this, R.raw.braincandy);
//        player.setLooping(false); // Set looping
    }

    public void setListeners(Context ctx, RemoteViews view){

        //prev listener
        Intent prev = new Intent(ctx,NotificationHelper.class);
        prev.putExtra("DO", "prev");
        PendingIntent pPrev = PendingIntent.getActivity(ctx, 0, prev, 0);
        view.setOnClickPendingIntent(R.id.prevButton, pPrev);

        //play/pause listener
        Intent playPause = new Intent(ctx, NotificationHelper.class);
        playPause.putExtra("DO", "playPause");
        PendingIntent pPlayPause = PendingIntent.getActivity(ctx, 1, playPause, 0);
        view.setOnClickPendingIntent(R.id.playButton2, pPlayPause);

        //next listener
        Intent next = new Intent(ctx, NotificationHelper.class);
        next.putExtra("DO", "next");
        PendingIntent pNext = PendingIntent.getActivity(ctx, 2, next, 0);
        view.setOnClickPendingIntent(R.id.nextButton, pNext);


    }

    public void handleIntent(Intent intent){
        if(intent == null || intent.getAction() == null){
            return;
        }

        String action = intent.getAction();
        if(action.equalsIgnoreCase(ACTION_NEXT)){
            Toast.makeText(this, "NEXT", Toast.LENGTH_LONG).show();
            mediaController.getTransportControls().skipToNext();
        }
    }

    public Notification.Action generateAction(int icon, String title, String intentAction){
        Intent intent = new Intent(getApplicationContext(),MusicService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),1,intent,0);
        return new Notification.Action.Builder(icon,title,pendingIntent).build();

    }

    public void buildNotification(Notification.Action action){
        Notification.MediaStyle style = new Notification.MediaStyle();
        Intent intent = new Intent(getApplicationContext(),MusicService.class);

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

    public void initMediaSession(){
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mediaSession = new MediaSession(getApplicationContext(), "session");

        mediaController = new MediaController(getApplicationContext(),mediaSession.getSessionToken());

        mediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onSkipToNext(){
                super.onSkipToNext();
                Log.e("callback","hihihi");
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
        if(mediaSessionManager == null){
            initMediaSession();
        }
        handleIntent(intent);

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
    public boolean onUnbind(Intent intent){
        mediaSession.release();
        return super.onUnbind(intent);
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
