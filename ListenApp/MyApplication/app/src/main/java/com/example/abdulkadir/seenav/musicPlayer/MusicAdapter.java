package com.example.abdulkadir.seenav.musicPlayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abdulkadir.seenav.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by yinuoyang on 11/3/16.
 */

public class MusicAdapter extends BaseAdapter{

    private List<MusicItem> list;
    private LayoutInflater infla;
    private int Resources;
    private Context mContext;

    public MusicAdapter(List<MusicItem> list, int resID, Context context){
        this.list = list;
        Resources = resID;
        mContext = context;
        infla = LayoutInflater.from(mContext);

    }

    @Override
    public int getCount() {


        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list != null ? list.get(position): null ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = infla.inflate(Resources,parent,false);
        }

        MusicItem item = list.get(position);

        TextView title = (TextView) convertView.findViewById(R.id.music_title);
        title.setText(item.name);
        TextView createTime = (TextView) convertView.findViewById(R.id.music_duration);
        String times = Utils.convertMsecondToTime(item.duration);

        createTime.setText(times);

        //change the time format


        ImageView thumb = (ImageView) convertView.findViewById(R.id.music_thumb);
        if(thumb != null) {
            if (item.thumb != null) {
                thumb.setImageBitmap(item.thumb);
            } else {
                thumb.setImageResource(R.mipmap.ic_launcher);
            }
        }

        return convertView;
    };
}
