package com.example.seif.seshatplayer;

import android.util.Log;

import com.example.seif.seshatplayer.model.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Stack;

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


    /*public static ArrayList<Direction> clearedRedundancyList(ArrayList<Direction> dirtyList) {

        ArrayList<Direction> result = new ArrayList<>();
        //  dirtyList.remove(Direction.INIT);
        Stack<Direction>stack_dirtyList = new Stack<>();
        stack_dirtyList.addAll(dirtyList);

        if (dirtyList.size() > 4) {
            for (int i = 0; i < stack_dirtyList.size(); i++) {
                if ((stack_dirtyList.get(i) == stack_dirtyList.get(i + 2)) && (stack_dirtyList.get(i + 1) == stack_dirtyList.get(i + 3))) {
                    dirtyList.remove(i);
                    dirtyList.remove(i + 1);
                } else {
                    result.add(dirtyList.get(i));
                    result.add(dirtyList.get(i + 1));
                }
            }
        } else {
            return dirtyList;
        }
        return result;
    }*/
    public static ArrayList<Direction>  clearedRedundancyList(ArrayList<Direction> dirtyList) {
        for (int i = 0; i < dirtyList.size() - 1; i++) {
            Direction d = dirtyList.get(i);
            Direction d2 = dirtyList.get(i + 1);
            if (d == null && d2 == null) {
                dirtyList.remove(d);
                dirtyList.remove(d2);
            }
        }
        return dirtyList;
    }

    public static Direction[] clearedRedundancyList(Direction[] GV) {

        // setup for arraylist version .. clean without INIT,END
        ArrayList<Direction> gv = new ArrayList<>();
        for(int i=1;i<GV.length -3;i=+2){

            Direction DX = GV[i];
            Direction DY = GV[i+1];
            Direction DX_1 = GV[i+2];
            Direction DY_1 = GV[i+3];

             if(DX != DX_1 || DY != DY_1){
                 gv.add(GV[i]);
                 gv.add(GV[i+1]);
             }
        }

        Log.i("clearedRedundancyList","l"+gv.size());
        Direction [] result  = new Direction[gv.size()];
        gv.toArray(result);
       return result;
    }

}
