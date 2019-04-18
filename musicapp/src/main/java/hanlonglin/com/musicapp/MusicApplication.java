package hanlonglin.com.musicapp;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import hanlonglin.com.musicapp.model.Song;

public class MusicApplication extends Application {

    private static MusicApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }

    public static MusicApplication getInstance() {
        return instance;
    }

    //全局变量
    List<Song> currentSonglist=new ArrayList<>();

    public List<Song> getCurrentSonglist() {
        return currentSonglist;
    }

    public void setCurrentSonglist(List<Song> currentSonglist) {
        this.currentSonglist = currentSonglist;
    }
}
