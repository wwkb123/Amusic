package yau.tommy.com.amusic;


import android.content.ContentResolver;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
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

        playlistView = view.findViewById(R.id.playlist);
        mAdapter = new MusicAdapter(getActivity(),R.layout.list_view,songList);
        playlistView.setAdapter(mAdapter);
        playlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mMediaPlayer!= null && mMediaPlayer.isPlaying()){
                    mMediaPlayer.stop();
                }
                SongItem currentSong = songList.get(i);

                mMediaPlayer = new MediaPlayer();
                Uri songUri = Uri.parse(currentSong.getSongPath());
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mMediaPlayer.setDataSource(getContext(),songUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                    TextView txtCurrSong = getActivity().findViewById(R.id.currSong);
                    txtCurrSong.setText(currentSong.getTitle());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        ImageButton playButton = getActivity().findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMediaPlayer!= null && mMediaPlayer.isPlaying()){
                    mMediaPlayer.stop();
                }
            }
        });
    }

}
