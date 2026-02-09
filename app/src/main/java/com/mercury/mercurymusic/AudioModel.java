package com.mercury.mercurymusic;

import java.io.Serializable;

public class AudioModel implements Serializable {

    String path;
    String title;
    String duration;
    String artist;
    String album;
    String album_ID;

    public AudioModel(String path, String title, String duration, String artist, String album, String album_ID){
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.artist = artist;
        this.album = album;
        this.album_ID = album_ID;
    }
    public String getPath(){
        return path;
    }
    public String getTitle(){
        return title;
    }
    public String getDuration(){
        return duration;
    }
    public String getArtist(){
        return artist;
    }
    public String getAlbum(){
        return album;
    }
    public String getAlbum_ID(){
        return album_ID;
    }
}
