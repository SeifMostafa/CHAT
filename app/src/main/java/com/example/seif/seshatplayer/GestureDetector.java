package com.example.seif.seshatplayer;

import android.graphics.Point;
import android.util.Log;

import com.example.seif.seshatplayer.model.Direction;
import static java.lang.Math.floorDiv;
import java.util.ArrayList;
import java.util.Arrays;

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
    float prev_x, prev_y;
    private int priority;
    private Direction[] gesture;
    private ArrayList<Point> tp;

    public GestureDetector(Direction[] Gesture, int Priority) {
        this.gesture = Gesture;
        Log.i("gesture", " " + gesture.length);

        this.gesture = clearedRedundancyList(this.gesture);
        this.priority = Priority;
        tp = new ArrayList<>();
        userGuidedVector = new ArrayList<>();
        for (Direction direction : gesture) Log.i("gesture", " " + direction);
    }

    public GestureDetector(Direction[] Gesture) {
        this.gesture = Gesture;
        this.priority = NORMAL;
        tp = new ArrayList<>();
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
        Log.i("gesture", " " + gv.size());

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

    public boolean check() {
        boolean isDetected = false;
        if (userGuidedVector.size() >= gesture.length) {
            isDetected = CompareGuidedVector();
        } else Log.i("GestureDetector", "check" + "shortage in touched points data");

        userGuidedVector.clear();
        return isDetected;
    }

    public void appendpoint(float x, float y, int PointPosition) {
        tp.add(new Point((int) x, (int) y));

        if (PointPosition == START) {
            Appending2UserGuidedVector(prev_x, prev_y, x, y);
        } else if (PointPosition == END) {
            check();
        } else {
            Appending2UserGuidedVector(prev_x, prev_y, x, y);
        }
        prev_x = x;
        prev_y = y;
    }

    private void Appending2UserGuidedVector(final float prev_x, final float prev_y, final float cur_x, final float cur_y) {

        Direction[] tempDirections = ComparePointsToCheckFV(prev_x, prev_y, cur_x, cur_y);
        int DirectionReqIndex =tp.size();
        if(DirectionReqIndex < gesture.length-1)
        tempDirections = FilterDirectionsByTolerance(tempDirections, prev_x, prev_y, cur_x, cur_y, priority * R.dimen.activity_word_textsize, gesture[DirectionReqIndex], gesture[DirectionReqIndex+1]);



        Direction temp_direction_x = tempDirections[tempDirections.length - 1];
        Direction temp_direction_y = tempDirections[tempDirections.length - 2];

        if (userGuidedVector.size() >= 2) {
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

        Log.i("GestureDetector", "Appending2UserGuidedVector: " + userGuidedVector.size());
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
                Log.i("GestureDetector", "CompareGuidedVector: " + "gesture is null");
            if (userGuidedVector.equals(null))
                Log.i("GestureDetector", "CompareGuidedVector: " + "userGuidedVector is null");
            return false;
        }
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
