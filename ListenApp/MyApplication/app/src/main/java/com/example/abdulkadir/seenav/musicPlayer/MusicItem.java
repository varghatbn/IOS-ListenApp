package com.example.abdulkadir.seenav.musicPlayer;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.net.URI;

/**
 * Created by yinuoyang on 11/2/16.
 */

public class MusicItem {

    public String name;
    Uri songUri;
    Uri albumUri;
    public URI Uri;
    public Bitmap thumb;
    long duration;

    @Override
    public boolean equals(Object o) {
        MusicItem another = (MusicItem) o;

        //音乐的Uri相同，则说明两者相同
        return another.songUri.equals(this.songUri);
    }

    MusicItem(Uri songUri, Uri albumUri, String strName, long duration,URI uri) {
        this.name = strName;
        this.songUri = songUri;
        this.duration = duration;
        this.albumUri = albumUri;
        this.Uri = uri;
    }


}
