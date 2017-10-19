package com.example.seif.seshatplayer;

import android.util.Log;

import com.example.seif.seshatplayer.model.Direction;

import java.util.ArrayList;

/**
 * Created by seif on 10/9/17.
 */

public class GestureDetector {
    public static final int STRICT = 1;
    public static final int NORMAL = 2;
    public static final int LOW = 3;

    public static final int START = 4;
    public static final int MIDDLE = 5;
    public static final int END = 6;

    ArrayList<Direction> userGuidedVector;
    boolean skipinit = false;
    float prev_x = 0, prev_y = 0;
    private int priority;
    private Direction[] gesture;

    public GestureDetector(Direction[] Gesture, int Priority) {
        this.gesture = clearedRedundancyList(Gesture);
        if (this.gesture.length < 2) Log.e("GestureDetector", "Hasn't gesture to detect!");
        this.priority = Priority;
        userGuidedVector = new ArrayList<>();
    }


    private Direction[] clearedRedundancyList(Direction[] GV) {

        // no redundancy of same two directions(x,y) and without INIT,END
        ArrayList<Direction> gv = new ArrayList<>();
        for (int i = 1; i < GV.length - 4; ) {
            Direction DX = GV[i];
            Direction DY = GV[i + 1];
            Direction DX_1 = GV[i + 2];
            Direction DY_1 = GV[i + 3];
            if (!DX.equals(DX_1) || !DY.equals(DY_1) || i == 1) {
                gv.add(GV[i]);
                gv.add(GV[i + 1]);
            }
            i += 2;
        }
        if (gv.size() == 0 && GV.length > 2) {
            gv.add(GV[1]);
            gv.add(GV[2]);
        }
        Direction[] result = new Direction[gv.size()];
        gv.toArray(result);
        Log.i("GestureDetector", "clearedRedundancyList: gesture.size= " + gv.size());
        return result;
    }

    public boolean check() {
        boolean isDetected = false;
        if (userGuidedVector.size() >= gesture.length) {
            for (int i = 0; i < gesture.length; ) {
                Direction d_X = userGuidedVector.get(i);
                Direction d_Y = userGuidedVector.get(i + 1);
                Direction ORG_d_X = gesture[i];
                Direction ORG_d_Y = gesture[i + 1];

                Log.i("GestureDetector", "CompareGuidedVector" + "XD:  " + ORG_d_X);
                Log.i("GestureDetector", "CompareGuidedVector" + "UXD:  " + d_X);
                Log.i("GestureDetector", "CompareGuidedVector" + "YD:  " + ORG_d_Y);
                Log.i("GestureDetector", "CompareGuidedVector" + "UYD:" + d_Y);

                try {
                    if ((d_X != ORG_d_X || d_Y != ORG_d_Y)) {
                        return false;
                    }
                    i += 2;
                } catch (Exception e) {
                    Log.e("GestureDetector", "CompareGuidedVector" + e.toString());
                }
            }
            return true;
        } else Log.i("GestureDetector", "check" + "shortage in touched points data");
        return isDetected;
    }

    public void appendpoint(float x, float y, int PointPosition) {

        if (PointPosition == START) {
            if (prev_x != 0 && prev_y != 0)
                Appending2UserGuidedVector(prev_x, prev_y, x, y);
            else {
                prev_x = x;
                prev_y = y;
            }
        } else {
            // middle .. while moving/dragging
            Appending2UserGuidedVector(prev_x, prev_y, x, y);
        }
        prev_x = x;
        prev_y = y;
    }

    private void Appending2UserGuidedVector(final float prev_x, final float prev_y, final float cur_x, final float cur_y) {
        Direction XDirection, YDirection;
        int DirectionReqIndex = userGuidedVector.size();
        Direction XDirectionShouldBe, YDirectionShouldBe;
        if(DirectionReqIndex>=gesture.length){
            XDirectionShouldBe = gesture[DirectionReqIndex - 2];
            YDirectionShouldBe = gesture[DirectionReqIndex - 1];
        }else{
            XDirectionShouldBe = gesture[DirectionReqIndex];
            YDirectionShouldBe = gesture[DirectionReqIndex + 1];
        }


        if (prev_x > cur_x) {
            if (Math.abs(prev_x - cur_x) < priority && XDirectionShouldBe == Direction.LEFT)
                XDirection = Direction.LEFT;
            else
                XDirection = Direction.RIGHT;
        } else {
            if (Math.abs(prev_x - cur_x) < priority && XDirectionShouldBe == Direction.RIGHT)
                XDirection = Direction.RIGHT;
            else
                XDirection = Direction.LEFT;
        }
        if (Math.abs(prev_x - cur_x) < priority * 2 && XDirectionShouldBe == Direction.SAME)
            XDirection = Direction.SAME;


        if (prev_y > cur_y) {
            if (Math.abs(prev_y - cur_y) < priority && YDirectionShouldBe == Direction.UP)
                YDirection = Direction.UP;
            else
                YDirection = Direction.DOWN;
        } else {
            if (Math.abs(prev_y - cur_y) < priority && YDirectionShouldBe == Direction.DOWN)
                YDirection = Direction.DOWN;
            else
                YDirection = Direction.UP;
        }
        if (Math.abs(prev_y - cur_y) < priority * 2 && YDirectionShouldBe == Direction.SAME)
            YDirection = Direction.SAME;


        if (skipinit) {
            Direction direction_x = userGuidedVector.get(userGuidedVector.size() - 2);
            Direction direction_y = userGuidedVector.get(userGuidedVector.size() - 1);
            if ((direction_x != XDirection || direction_y != YDirection)) {
                userGuidedVector.add(XDirection);
                userGuidedVector.add(YDirection);
            }
        } else {
            userGuidedVector.add(XDirection);
            userGuidedVector.add(YDirection);
            skipinit = true;
        }

        Log.i("GestureDetector", "Appending2UserGuidedVector: " + userGuidedVector.size());
    }

}