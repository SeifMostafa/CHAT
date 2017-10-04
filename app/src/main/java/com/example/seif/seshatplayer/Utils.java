package com.example.seif.seshatplayer;

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

    public static Direction[] ComparePointsToCheckFV(float x1, float y1, float x2, float y2) {
        Direction direction[] = new Direction[2];
        if (x1 > x2)
            direction[0] = Direction.LEFT;
        else if (x1 < x2)
            direction[0] = Direction.RIGHT;
        else
            direction[0] = null;

        if (y1 > y2)
            direction[1] = Direction.UP;
        else if (y1 < y2)
            direction[1] = Direction.DOWN;
        else
            direction[1] = null;
        return direction;
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

    public static Direction[] clearedRedundancyList(Direction[] GV) {

        // setup for arraylist version .. clean without INIT,END
        ArrayList<Direction> gv = new ArrayList<>();
        for (int i = 1; i < GV.length - 4; ) {

            Direction DX = GV[i];
            Direction DY = GV[i + 1];
            Direction DX_1 = GV[i + 2];
            Direction DY_1 = GV[i + 3];

            if (DX != DX_1 || DY != DY_1) {
                gv.add(GV[i]);
                gv.add(GV[i + 1]);
            }
            i += 2;
        }
        Direction[] result = new Direction[gv.size()];
        gv.toArray(result);
        return result;
    }

}
