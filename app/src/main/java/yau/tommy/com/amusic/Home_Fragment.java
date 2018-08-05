package yau.tommy.com.amusic;


import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * Created by Yiu Chung Yau on 7/31/18.
 */

public class Home_Fragment extends Fragment {

    private ListView playlistView;
    private MusicAdapter mAdapter;
    private ArrayList<SongItem> songList;
    private MediaPlayer mMediaPlayer;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mMediaPlayer = new MediaPlayer();

        songList = new ArrayList<>();
        getSongs();
        initializeUI(view);



        return view;
    }

    public void getSongs(){
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri,null, null,null,null);

        
        if(songCursor != null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do{
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                SongItem songItem = new SongItem(currentTitle,currentArtist,1);
                songList.add(songItem);

            }while(songCursor.moveToNext());

        }
    }
    public void initializeUI(View view){

        playlistView = view.findViewById(R.id.playlist);
        mAdapter = new MusicAdapter(getActivity(),R.layout.list_view,songList);
        playlistView.setAdapter(mAdapter);
    }

}
