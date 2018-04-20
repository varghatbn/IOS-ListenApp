package com.example.abdulkadir.seenav.musicPlayer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.content.Context;
import android.widget.ListView;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinuoyang on 11/3/16.
 */

public class MusicUpdateTask extends AsyncTask<Object, MusicItem, Void> {
    Context context;
    List<MusicItem> mDataList;
    ListView mView;
    public MusicUpdateTask(Context context1, ListView mView, List<MusicItem> mDataList){
        //passing the context object inside of the method
        context = context1;
        this.mView = mView;

        this.mDataList = mDataList;
    };

    @Override
    protected Void doInBackground(Object... params){
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String Data = MediaStore.Audio.Media.DATA;
        String ID = MediaStore.Audio.Media._ID;
        String Title = MediaStore.Audio.Media.TITLE;
        String Album = MediaStore.Audio.Albums.ALBUM_ID;
        String Duration = MediaStore.Audio.Media.DURATION;

        String[] searchKey = new String[]{
                ID,
                Title,
                Data,
                Duration
        };


        String location = MediaStore.Audio.Media.DATA +" like \"%" + "/Music " +"%\"";

        System.out.println(location);

        String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        ContentResolver resolve = context.getContentResolver();


        Cursor cursor = resolve
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Audio.Media.TITLE,
                                MediaStore.Audio.Media.DURATION,
                                MediaStore.Audio.Media.ALBUM,
                                MediaStore.Audio.Media.ARTIST,
                                MediaStore.Audio.Media._ID,
                                MediaStore.Audio.Media.DATA,
                                MediaStore.Audio.Media.DISPLAY_NAME }, null,
                        null, null);

        while(cursor.moveToNext()){



            //get path of song
            String path = cursor.getString(cursor.getColumnIndexOrThrow(Data));

            //get id of song
            String id = cursor.getString(cursor.getColumnIndexOrThrow(ID));

            Uri musicUri = Uri.withAppendedPath(uri,id);

            String n = "file:///" + path;
            String link;

            try {
                link = URLEncoder.encode(n, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError("UTF-8 is unknown");
                // or 'throw new AssertionError("Impossible things are happening today. " +
                //                              "Consider buying a lottery ticket!!");'
            }
            URI uriLink = URI.create(link);

            String name = cursor.getString(cursor.getColumnIndex(Title));


            long duration = cursor.getLong(cursor.getColumnIndexOrThrow(Duration));

            int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM));

            Uri albumUri = ContentUris.withAppendedId(Uri.parse("content://media/Internal/audio/albumart"),albumId);

            MusicItem item = new MusicItem(musicUri,null,name,duration,uriLink);

            publishProgress(item);
        }

        cursor.close();
        return null;
    };

    @Override
    protected void onProgressUpdate(MusicItem... values){

        MusicItem data = values[0];


        mDataList.add(data);

        MusicAdapter adapter = (MusicAdapter) mView.getAdapter();
        adapter.notifyDataSetChanged();
    };
}
