package com.example.abdulkadir.seenav.musicPlayer;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by yinuoyang on 11/2/16.
 */

public class PlayerListContent extends ContentProvider {

    private DbHelper mDBHelper;

    private static final String SCHEME = "content://";

    private static final String PATH_SONGS = "/songs";

    public static final String AUTHORITY = "com.anddle.PlayListContentProvider";


    //"content://com.anddle.PlayListContentProvider/songs"

    public static final Uri CONTENT_SONGS_URI = Uri.parse(SCHEME + AUTHORITY + PATH_SONGS);
    @Override
    public boolean onCreate() {

        mDBHelper = new DbHelper(getContext());

        return true;
    }

    @Override
    public String getType(Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri result = null;
        //通过DBHelper获取写数据库的方法
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        //将要数据ContentValues插入到数据库中
        long id = db.insert(DbHelper.PLAYLIST_TABLE_NAME, null, values);

        if(id > 0) {
            //根据返回到id值组合成该数据项对应的Uri地址,
            //假设id为8，那么这个Uri地址类似于content://com.anddle.PlayListContentProvider/songs/8
            result = ContentUris.withAppendedId(CONTENT_SONGS_URI, id);
        }

        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {


        //通过DBHelper获取写数据库的方法
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        //清空playlist_table表，并将删除的数据条数返回
        int count = db.delete(DbHelper.PLAYLIST_TABLE_NAME, selection, selectionArgs);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        //通过DBHelper获取写数据库的方法
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        //更新数据库的指定项
        int count = db.update(DbHelper.PLAYLIST_TABLE_NAME, values, selection, selectionArgs);

        return count;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        //通过DBHelper获取读数据库的方法
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        //查询数据库中的数据项
        Cursor cursor = db.query(DbHelper.PLAYLIST_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

        return cursor;
    }
}
