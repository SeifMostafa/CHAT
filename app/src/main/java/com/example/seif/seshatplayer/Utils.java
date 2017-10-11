package com.example.seif.seshatplayer;

import android.util.DisplayMetrics;
import android.util.Log;

import com.example.seif.seshatplayer.model.Direction;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by seif on 9/13/17.
 */

public class Utils {

    public static String getCurrentTime() {

        final Calendar c = Calendar.getInstance();
        int currentTime = (c.get(Calendar.HOUR_OF_DAY) * 10000) +
                (c.get(Calendar.MINUTE) * 100) +
                (c.get(Calendar.SECOND));
        Log.w("TIME:", String.valueOf(currentTime));
        return (String.valueOf(currentTime));

    }



    public static ArrayList<Direction> clearedRedundancyList(ArrayList<Direction> dirtyList) {
        for (int i = 0; i < dirtyList.size() - 1; i++) {
            Direction d = dirtyList.get(i);
            Direction d2 = dirtyList.get(i + 1);
            if (d == null && d2 == null) {
                dirtyList.remove(i);
                dirtyList.remove(i + 1);
            }
        }
        return dirtyList;
    }



}
