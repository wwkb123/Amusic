package yau.tommy.com.amusic;

public class SongItem {
    private String title;
    private String artist;
    private String songPath;


    public SongItem(String title, String artist, String songPath) {
        this.title = title;
        this.artist = artist;
        this.songPath = songPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }
}
