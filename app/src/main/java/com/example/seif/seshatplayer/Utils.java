package com.example.seif.seshatplayer;

import android.util.Log;
import com.example.seif.seshatplayer.model.Direction;
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
    public static Direction[] ComparePointsToCheckFV(double x1, double y1, double x2, double y2) {
        Direction direction[] = new Direction[2];
        if (x1 > x2)
            direction[0] = Direction.RIGHT;
        else if (x1 < x2)
            direction[0] = Direction.LEFT;
        else
            direction[0] = null;

        if (y1 > y2)
            direction[1] = Direction.DOWN;
        else if (y1 < y2)
            direction[1] = Direction.UP;
        else
            direction[1] = null;
        return direction;
    }
}
