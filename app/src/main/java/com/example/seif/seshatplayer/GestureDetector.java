package com.example.seif.seshatplayer;

import android.graphics.Point;
import android.util.Log;

import com.example.seif.seshatplayer.model.Direction;

/**
 * Created by seif on 10/9/17.
 */

public class GestureDetector {
    public static final int STRICT = 0;
    public static final int NORMAL = 1;
    public static final int LOW = -1;

    private int priority;
    private Direction [] gesture;

    public GestureDetector(Direction[] Gesture,int Priority){
        this.gesture = Gesture;
        this.priority = Priority;
    }

    public GestureDetector(Direction [] Gesture){
        this.gesture = Gesture;
        this.priority = NORMAL;
    }

    public boolean check(Point[] touchedPoints){
        boolean isDetected = false;
        if(touchedPoints.length >= 2 && gesture.length >= 2) {
            for (int i = 1; i < touchedPoints.length - 1; i++) {
                Direction [] currentDirections = ComparePointsToCheckFV(touchedPoints[i-1].x,touchedPoints[i-1].y,touchedPoints[i].x,touchedPoints[i].y);
                for(int j=1;j<3;j++){

                }
            }

        }else {
            if(gesture.length<=2) Log.e("GestureDetector","check" + "shortage in gesture data");
            if(touchedPoints.length<=2) Log.e("GestureDetector","check" + "shortage in touched points data");
            return isDetected;
        }


        return  isDetected;
    }


    private Direction[] ComparePointsToCheckFV(float x1, float y1, float x2, float y2) {
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
