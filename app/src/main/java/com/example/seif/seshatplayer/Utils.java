package com.example.seif.seshatplayer;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.seif.seshatplayer.model.Direction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
    public static Direction[] getDirectionsFromAssets(Context context,String filename){
        Direction[] result ;
        Stack<Direction> directions = new Stack<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("filename")));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                switch (mLine.charAt(0)) {
                    case 'I':
                        directions.push(Direction.INIT);
                        break;
                    case 'E':
                        directions.push(Direction.END);
                        break;
                    case 'L':
                        directions.push(Direction.LEFT);
                        break;
                    case 'R':
                        directions.push(Direction.RIGHT);
                        break;
                    case 'U':
                        directions.push(Direction.UP);
                        break;
                    case 'D':
                        directions.push(Direction.DOWN);
                        break;
                    case 'S':
                        directions.push(Direction.SAME);
                        break;
                    case 'n':
                        directions.push(null);
                        break;
                }

            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        result = new Direction[directions.size()];
        return directions.toArray(result);
    }


}
