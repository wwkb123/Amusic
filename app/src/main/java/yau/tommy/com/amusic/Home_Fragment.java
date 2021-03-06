package yau.tommy.com.amusic;


import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;


/**
 * Created by Yiu Chung Yau on 7/31/18.
 */

public class Home_Fragment extends Fragment {

    private ListView playlistView;
    private MusicAdapter mAdapter;
    private ArrayList<SongItem> songList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        songList = new ArrayList<>();
        getSongs();
        initializeUI(view);



        return view;
    }

    public void getSongs(){
        ContentResolver contentResolver = getActivity().getContentResolver();

        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = null;
        String sortOrder = null;
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
        String[] selectionArgsMp3 = new String[]{ mimeType };

        Cursor songCursor = contentResolver.query(songUri,projection, selectionMimeType,selectionArgsMp3,sortOrder);
//        for(String name:songCursor.getColumnNames()){
//            Log.e("Cursor",name);
//        }

        if(songCursor != null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do{
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentPath = songCursor.getString(songPath);
                SongItem songItem = new SongItem(currentTitle,currentArtist,currentPath);

                songList.add(songItem);

            }while(songCursor.moveToNext());

        }
    }
    public void initializeUI(View view){
        final ToggleButton playButton = getActivity().findViewById(R.id.playButton);
        playlistView = view.findViewById(R.id.playlist);
        mAdapter = new MusicAdapter(getActivity(),R.layout.list_view,songList);
        playlistView.setAdapter(mAdapter);
        playlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                SongItem currentSong = songList.get(i);
                //Uri songUri = Uri.parse(currentSong.getSongPath());

                Intent intent = new Intent(getActivity(), MusicService.class);
                intent.putExtra("songUri",currentSong.getSongPath());
                intent.putExtra("songTitle",currentSong.getTitle());
                intent.putExtra("songArtist",currentSong.getArtist());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(getActivity(),intent);
                }else{
                    getActivity().startService(intent);
                }

                playButton.setChecked(true);
                TextView txtCurrSong = getActivity().findViewById(R.id.currSong);
                txtCurrSong.setText(currentSong.getTitle());

            }
        });


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("state",playButton.isChecked()+"");
                if(playButton.isChecked()){
                    Log.e("state1",playButton.isChecked()+"");
                    //playButton.setChecked(false); //toggle the button's state
                }else{
                    Log.e("state2",playButton.isChecked()+"");

                }

                if(isServiceRunning(MusicService.class)){
                    getActivity().stopService(new Intent(getActivity(), MusicService.class));
                }


            }
        });
    }

    private boolean isServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
