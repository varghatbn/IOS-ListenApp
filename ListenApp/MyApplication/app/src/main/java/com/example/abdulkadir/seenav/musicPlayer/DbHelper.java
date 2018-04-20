package com.example.abdulkadir.seenav.musicPlayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yinuoyang on 11/13/16.
 */

public class DbHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "playlist.db";
    private final static int DB_VERSION = 1;
    public final static String PLAYLIST_TABLE_NAME = "playlist_table";

    public final static String ID = "id";
    public final static String NAME = "name";
    public final static String LAST_PLAY_TIME = "last_play_time";
    public final static String SONG_URI = "song_uri";
    public final static String ALBUM_URI = "album_uri";
    public final static String DURATION = "duration";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建播放列表的存储表项
        String PLAYLIST_TABLE_CMD = "CREATE TABLE " + PLAYLIST_TABLE_NAME
                + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NAME +" VARCHAR(256),"
                + LAST_PLAY_TIME +" LONG,"
                + SONG_URI +" VARCHAR(128),"
                + ALBUM_URI +" VARCHAR(128),"
                + DURATION + " LONG"
                + ");" ;
        db.execSQL(PLAYLIST_TABLE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //如果遇到数据库更新，我们简单的处理为删除以前的表，重新创建一张
        db.execSQL("DROP TABLE IF EXISTS "+ PLAYLIST_TABLE_NAME);
        onCreate(db);
    }

}