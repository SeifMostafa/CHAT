package com.example.seif.seshatplayer;

import android.util.Log;

import com.example.seif.seshatplayer.model.Direction;

import java.util.ArrayList;

/**
 * Created by seif on 10/9/17.
 */

public class GestureDetector {

    double successPercentage = 0;
    private ArrayList<Direction> mUserGuidedVector;
    private Direction[] gesture;

    public GestureDetector(Direction[] Gesture) {
        this.gesture = Gesture;
        if (this.gesture.length < 2) Log.e("GestureDetector", "Hasn't gesture to detect!");
      //  for (Direction direction : gesture) Log.i("GestureDetector", "Direction " + direction);
        // this.priority = Priority;
        //  mUserGuidedVector = new ArrayList<>();
    }

    public boolean check(ArrayList<Direction>mUserGV) {
        successPercentage =0;
        double progressStep = (100.0 / (double)gesture.length );
        this.mUserGuidedVector = mUserGV;
        boolean isDetected = false;
        if (mUserGuidedVector.size() >= gesture.length) {
            for (int i = 0; i < gesture.length - 1; ) {
                Direction d_X = mUserGuidedVector.get(i);
                Direction d_Y = mUserGuidedVector.get(i + 1);
                Direction ORG_d_X = gesture[i];
                Direction ORG_d_Y = gesture[i + 1];

               /* Log.i("GestureDetector", "CompareGuidedVector" + "XD:  " + ORG_d_X);
                Log.i("GestureDetector", "CompareGuidedVector" + "UXD:  " + d_X);
                Log.i("GestureDetector", "CompareGuidedVector" + "YD:  " + ORG_d_Y);
                Log.i("GestureDetector", "CompareGuidedVector" + "UYD:" + d_Y);*/

                try {
                    if (ORG_d_X == Direction.NOMATTER || ORG_d_Y == Direction.NOMATTER) {
                        if (ORG_d_X == Direction.NOMATTER && ORG_d_Y != Direction.NOMATTER) {
                            if (d_Y != ORG_d_Y) {
                                if (!approximateCheck(ORG_d_X, ORG_d_Y, i)) return isDetected;
                            }
                        } else if (ORG_d_X != Direction.NOMATTER && ORG_d_Y == Direction.NOMATTER) {
                            if (d_X != ORG_d_X) {
                                if (!approximateCheck(ORG_d_X, ORG_d_Y, i)) return isDetected;
                            }
                        }
                    } else {
                        if ((d_X != ORG_d_X || d_Y != ORG_d_Y)) {
                            if (!approximateCheck(ORG_d_X, ORG_d_Y, i)) return isDetected;
                        }
                    }
                    i += 2;
                    successPercentage+=(progressStep*2);
                } catch (Exception e) {
                    Log.e("GestureDetector", "CompareGuidedVector" + e.toString());
                }
            }
            isDetected = true;
           // resetRequested = true;
            //mUserGuidedVector.clear();
        } else {
            Log.i("GestureDetector", "check" + "shortage in touched points data");
        }
        return isDetected;
    }

    private boolean approximateCheck(Direction XDirection, Direction YDirection, int index) {
        boolean isDetected = false;
        if (index + 3 <= mUserGuidedVector.size()) {
            Log.i("GestureDetector:: ","approximateCheck:: am here!");
            Direction nextDx, nextDy, cuurentDx, cuurentDy;

            nextDx = mUserGuidedVector.get(index + 2);
            nextDy = mUserGuidedVector.get(index + 3);

            cuurentDx = mUserGuidedVector.get(index);
            cuurentDy = mUserGuidedVector.get(index + 1);

            if (nextDx == Direction.SAME || cuurentDx == Direction.SAME) {
                if (nextDx == Direction.SAME) {
                    if ((XDirection == cuurentDx || XDirection == Direction.NOMATTER) && (YDirection == cuurentDy || YDirection == nextDy || YDirection == Direction.NOMATTER))
                        isDetected = true;
                } else {
                    if ((XDirection == nextDx  || XDirection == Direction.NOMATTER)&& (YDirection == cuurentDy || YDirection == nextDy || YDirection == Direction.NOMATTER))
                        isDetected = true;
                }
            } else if (nextDy == Direction.SAME || cuurentDy == Direction.SAME) {
                if (nextDy == Direction.SAME) {
                    if ((YDirection == cuurentDy  || YDirection == Direction.NOMATTER) && (XDirection == cuurentDx || XDirection == nextDx ||XDirection == Direction.NOMATTER))
                        isDetected = true;
                } else {
                    if ((YDirection == nextDy  || YDirection == Direction.NOMATTER)&& (XDirection == cuurentDx || XDirection == nextDx || XDirection == Direction.NOMATTER))
                        isDetected = true;
                }
            }
        }
        return isDetected;
    }

    public double getSuccessPercentage(){
        return successPercentage;
    }
}