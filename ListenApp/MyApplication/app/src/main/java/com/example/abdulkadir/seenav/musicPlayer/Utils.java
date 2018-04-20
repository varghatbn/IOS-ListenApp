package com.example.abdulkadir.seenav.musicPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yinuoyang on 11/3/16.
 */

public class Utils {


    public static String convertMsecondToTime(long time){
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");

        Date date = new Date(time);

        String times = format.format(date);

        return times;
    };
}
