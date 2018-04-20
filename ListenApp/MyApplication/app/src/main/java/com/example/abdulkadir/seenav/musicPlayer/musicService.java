package com.example.abdulkadir.seenav.musicPlayer;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinuoyang on 11/11/16.
 */

public class musicService extends Service {


    private List<MusicItem> mPlayList;
    private ContentResolver mResolver;

    @Override
    public void onCreate() {
        super.onCreate();

        //获取ContentProvider的解析器，避免以后每次使用的时候都要重新获取
        mResolver = getContentResolver();
        //保存播放列表
        mPlayList = new ArrayList<MusicItem>();
        initPlayList();
    }

    private void initPlayList() {
        mPlayList.clear();

        Cursor cursor = mResolver.query(
                PlayerListContent.CONTENT_SONGS_URI,
                null,
                null,
                null,
                null);

        if(cursor != null){
            while(cursor.moveToNext()) {
                String songUri = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.SONG_URI));
                String albumUri = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.ALBUM_URI));
                String name = cursor.getString(cursor.getColumnIndex(DbHelper.NAME));
                long playedTime = cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.LAST_PLAY_TIME));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.DURATION));
                //URI uri = new URI("");
                //MusicItem item = new MusicItem(Uri.parse(songUri), Uri.parse(albumUri), name, duration,uri);
                //mPlayList.add(item);
            }
        }
    }

    public List<MusicItem> getPlayList() {
        return mPlayList;
    }


    public class MusicServiceIBinder extends Binder {

        public void addPlayList(List<MusicItem> items) {
            addPlayListInner(items);
        }

        public void addPlayList(MusicItem item) {
            addPlayListInner(item);
        }

        public void play() {
            playInner();

        }

        public void playNext() {
            playNextInner();

        }

        public void playPre() {
            playPreInner();
        }

        public void pause() {
            pauseInner();
        }

        public void seekTo(int pos) {
            seekToInner(pos);
        }

        public void registerOnStateChangeListener(OnStateChangeListener l) {
            registerOnStateChangeListenerInner(l);

        }

        private void addPlayListInner(MusicItem item) {

            //判断列表中是否已经存储过该音乐，如果存储过就不管它
            if(mPlayList.contains(item)) {
                return;
            }

            //添加到播放列表的第一个位置
            mPlayList.add(0, item);
            //将音乐信息保存到ContentProvider中
            insertMusicItemToContentProvider(item);
        }

        private void addPlayListInner(List<MusicItem> items) {

            //清空数据库中的playlist_table
            mResolver.delete(PlayerListContent.CONTENT_SONGS_URI, null, null);
            //清空缓存的播放列表
            mPlayList.clear();

            //将每首音乐添加到播放列表的缓存和数据库中
            for (MusicItem item : items) {
                //利用现成的代码，便于代码的维护
                addPlayListInner(item);
            }
        }

        //访问ContentProvider，保存一条数据
        private void insertMusicItemToContentProvider(MusicItem item) {

            ContentValues cv = new ContentValues();
            cv.put(DbHelper.NAME, item.name);
            cv.put(DbHelper.DURATION, item.duration);
            //cv.put(DbHelper.LAST_PLAY_TIME, item.playedTime);
            cv.put(DbHelper.SONG_URI, item.songUri.toString());
            cv.put(DbHelper.ALBUM_URI, item.albumUri.toString());
            Uri uri = mResolver.insert(PlayerListContent.CONTENT_SONGS_URI, cv);
        }

        public void unregisterOnStateChangeListener(OnStateChangeListener l) {
            unregisterOnStateChangeListenerInner(l);
        }

        public MusicItem getCurrentMusic() {
            return getCurrentMusicInner();
        }

        public boolean isPlaying() {
            return isPlayingInner();
        }

        public List<MusicItem> getPlayList() {
            return null;
        }

    }

    //真正实现功能的方法
    public void addPlayListInner(List<MusicItem> items) {

    }

    public void addPlayListInner(MusicItem item) {

    }

    public void playNextInner() {
    }

    public void playInner() {

    }

    public void playPreInner() {
    }

    public void pauseInner() {

    }

    public void seekToInner(int pos) {

    }

    public void registerOnStateChangeListenerInner(OnStateChangeListener l) {

    }

    public void unregisterOnStateChangeListenerInner(OnStateChangeListener l) {

    }

    public MusicItem getCurrentMusicInner() {
        return null;
    }

    public boolean isPlayingInner() {
        return false;
    }

    //创建Binder实例
    private final IBinder mBinder = new MusicServiceIBinder();

    @Override
    public IBinder onBind(Intent intent) {
        //当组件bindService()之后，将这个Binder返回给组件使用
        return mBinder;
    }

}