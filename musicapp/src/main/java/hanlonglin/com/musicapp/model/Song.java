package hanlonglin.com.musicapp.model;

import java.io.Serializable;

import hanlonglin.com.musicapp.adapter.SongAdapter;

public class Song implements Serializable {
    public static final int FROM_HTTP = 0;
    public static final int FROM_FILE = 1;
    public static final int FROM_ASERT = 2;

    String name;
    String singer;
    String url;
    int from;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getFrom() {
        return from;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public Song(String name, String singer, String url, int from) {
        this.name = name;
        this.singer = singer;
        this.url = url;
        this.from = from;
    }

    //比较
    public boolean equals(Song song) {
        if (this.getFrom() == song.getFrom()
                && this.getUrl().equals(song.getUrl())
                && this.getName().equals(song.getName()))
            return true;
        return false;
    }
}
