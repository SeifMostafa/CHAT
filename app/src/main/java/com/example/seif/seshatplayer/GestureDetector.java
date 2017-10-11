package com.example.seif.seshatplayer;

import android.graphics.Point;
import android.util.Log;

import com.example.seif.seshatplayer.model.Direction;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by seif on 10/9/17.
 */

public class GestureDetector {
    public static final int STRICT = 1;
    public static final int NORMAL = 2;
    public static final int LOW = 3;


    ArrayList<Direction> userGuidedVector;

    boolean skipinit = false;
    float prev_x, prev_y;
    private float priority;
    private Direction[] gesture;

    public GestureDetector(Direction[] Gesture, float Priority) {
        this.gesture = Gesture;
        this.gesture = clearedRedundancyList(this.gesture);
        this.priority = Priority;
        userGuidedVector = new ArrayList<>();
    }


    private Direction[] clearedRedundancyList(Direction[] GV) {

        // setup for arraylist version .. clean without INIT,END
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
        Direction[] result = new Direction[gv.size()];
        gv.toArray(result);
        return result;
    }

    public boolean check(Point[] TouchedPoints) {
        boolean isDetected = false;
        if (TouchedPoints.length >= 2 && gesture.length >= 2) {
            int DirectionIndexInGesture = 0;
            for (int i = 1; i < TouchedPoints.length; i++) {
                Direction[] currentDirections = ComparePointsToCheckFV(TouchedPoints[i - 1].x, TouchedPoints[i - 1].y, TouchedPoints[i].x, TouchedPoints[i].y);
                if (currentDirections[0] != gesture[DirectionIndexInGesture] || currentDirections[0] != gesture[DirectionIndexInGesture + 1]) {
                    return isDetected;
                } else DirectionIndexInGesture += 2;
            }
            isDetected = true;
        } else {
            if (gesture.length <= 2) Log.e("GestureDetector", "check" + "shortage in gesture data");
            if (TouchedPoints.length <= 2)
                Log.e("GestureDetector", "check" + "shortage in touched points data");
            return isDetected;
        }
        return isDetected;
    }

    public Boolean check() {

        Boolean isDetected = null;

        if (userGuidedVector.size() >= gesture.length) {
            isDetected = CompareGuidedVector();
        } else {
            Log.i("GestureDetector", "check" + " shortage in touched points data::" + userGuidedVector.size() + "," + gesture.length);
        }
        return isDetected;
    }


    public Boolean appendpoint(float x, float y) {
        Boolean result = Appending2UserGuidedVector(prev_x, prev_y, x, y);

        prev_x = x;
        prev_y = y;

        return result;
    }

    private Boolean Appending2UserGuidedVector(final float prev_x, final float prev_y, final float cur_x, final float cur_y) {

        Direction[] tempDirections = ComparePointsToCheckFV(prev_x, prev_y, cur_x, cur_y);


        Direction temp_direction_x = tempDirections[tempDirections.length - 1];
        Direction temp_direction_y = tempDirections[tempDirections.length - 2];

        if (userGuidedVector.size() >= 2) {

            tempDirections = FilterDirectionsByTolerance(tempDirections, prev_x, prev_y, cur_x, cur_y, priority * 128, gesture[userGuidedVector.size() - 2], gesture[userGuidedVector.size() - 1]);
            Direction direction_x = userGuidedVector.get(userGuidedVector.size() - 1);
            Direction direction_y = userGuidedVector.get(userGuidedVector.size() - 2);

            if ((direction_x != temp_direction_x || direction_y != temp_direction_y) && temp_direction_x != null && temp_direction_y != null) {
                userGuidedVector.addAll(Arrays.asList(tempDirections));
            }
        } else if (skipinit) {
            if (temp_direction_x != null && temp_direction_y != null) {
                userGuidedVector.addAll(Arrays.asList(tempDirections));
            }
        } else {
            skipinit = true;
        }
        Log.i("GestureDetector", "appendind" + " SZSs::" + userGuidedVector.size() + "," + gesture.length);
        Boolean checkResult = check();



        if (checkResult != null) {
            if (checkResult) {
                Log.i("GestureDetector", "appendind" + " SZSs::OK");
                return true;
            } else {
                Log.i("GestureDetector", "appendind" + " SZSs::NO");
                userGuidedVector.clear();
                return false;
            }
        }else return checkResult;
    }

    private boolean CompareGuidedVector() {
        if (!gesture.equals(null) && !userGuidedVector.equals(null)) {
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
        } else {
            if (gesture.equals(null))
                Log.e("GestureDetector", "CompareGuidedVector: " + "gesture is null");
            if (userGuidedVector.equals(null))
                Log.e("GestureDetector", "CompareGuidedVector: " + "userGuidedVector is null");
            return false;
        }
    }

    private Direction[] ComparePointsToCheckFV(float x1, float y1, float x2, float y2) {

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

    private Direction[] FilterDirectionsByTolerance(Direction[] input, float x1, float y1, float x2, float y2, float tolerance, Direction XDirectionShouldBe, Direction YDirectionShouldBe) {

        Direction[] output = input;

        if (Math.abs(x1 - x2) < tolerance && XDirectionShouldBe == Direction.RIGHT)
            output[0] = Direction.RIGHT;

        else if (Math.abs(x1 - x2) < tolerance && XDirectionShouldBe == Direction.LEFT)
            output[0] = Direction.LEFT;

        if (Math.abs(y1 - y2) < tolerance && YDirectionShouldBe == Direction.DOWN)
            output[1] = Direction.DOWN;

        else if (Math.abs(x1 - x2) < tolerance && YDirectionShouldBe == Direction.UP)
            output[1] = Direction.UP;

        return output;
    }

}
