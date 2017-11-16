package com.example.seif.seshatplayer;

import android.util.Log;

import com.example.seif.seshatplayer.model.Direction;

import java.util.ArrayList;

/**
 * Created by seif on 10/9/17.
 */

public class GestureDetector {

    private Direction[] gesture;
    public double THRESHOLD = 60;


    public GestureDetector(Direction[] Gesture) {
        this.gesture = Gesture;
        if (this.gesture.length < 2) Log.e("GestureDetector", "Hasn't gesture to detect!");
        THRESHOLD += 40/this.gesture.length;
    }

    public boolean check(ArrayList<Direction> mUserGV) {
        double successPercentage = 100;
        double progressStep = (100.0 / (double) gesture.length);
        boolean isDetected = false;
        try {
            if (mUserGV.size() >= gesture.length) {
                for (int i = 0; i < gesture.length - 1; i += 2 ) {
                    Direction d_X = mUserGV.get(i);
                    Direction d_Y = mUserGV.get(i + 1);
                    Direction ORG_d_X = gesture[i];
                    Direction ORG_d_Y = gesture[i + 1];

                    /*Log.i("GestureDetector", "CompareGuidedVector" + "XD:  " + ORG_d_X);
                    Log.i("GestureDetector", "CompareGuidedVector" + "UXD:  " + d_X);
                    Log.i("GestureDetector", "CompareGuidedVector" + "YD:  " + ORG_d_Y);
                    Log.i("GestureDetector", "CompareGuidedVector" + "UYD:" + d_Y);*/

                    try {
                        if (ORG_d_X == Direction.NOMATTER || ORG_d_Y == Direction.NOMATTER) {
                            if (ORG_d_X == Direction.NOMATTER && ORG_d_Y != Direction.NOMATTER) {
                                if (d_Y != ORG_d_Y) {
                                    if (!approximateCheck(mUserGV,ORG_d_X, ORG_d_Y, i)){
                                        successPercentage-=progressStep;
                                    }
                                }
                            } else if (ORG_d_X != Direction.NOMATTER && ORG_d_Y == Direction.NOMATTER) {
                                if (d_X != ORG_d_X) {
                                    if (!approximateCheck(mUserGV, ORG_d_X, ORG_d_Y, i)){
                                        successPercentage-=progressStep;
                                    }
                                }
                            }
                        } else {
                            if ((d_X != ORG_d_X || d_Y != ORG_d_Y)) {
                                if (!approximateCheck(mUserGV, ORG_d_X, ORG_d_Y, i)) {
                                    successPercentage-=progressStep;
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("GestureDetector", "CompareGuidedVector" + e.toString());
                    }
                }
                if(successPercentage > THRESHOLD) isDetected = true;
            } else {
                Log.i("GestureDetector", "check" + "shortage in touched points data");
            }
        } catch (Exception e) {
            Log.e("GestureDetector", "check:: e: " + e.getMessage());
        }
        return isDetected;
    }

    private boolean approximateCheck(ArrayList<Direction> mUserGV, Direction XDirection, Direction YDirection, int index) {
        boolean isDetected = false;
        if (index + 3 <= mUserGV.size()) {
            Direction nextDx, nextDy, cuurentDx, cuurentDy;

            nextDx = mUserGV.get(index + 2);
            nextDy = mUserGV.get(index + 3);

            cuurentDx = mUserGV.get(index);
            cuurentDy = mUserGV.get(index + 1);

            if (nextDx == Direction.SAME || cuurentDx == Direction.SAME) {
                if (nextDx == Direction.SAME) {
                    if ((XDirection == cuurentDx || XDirection == Direction.NOMATTER) && (YDirection == cuurentDy || YDirection == nextDy || YDirection == Direction.NOMATTER))
                        isDetected = true;
                } else {
                    if ((XDirection == nextDx || XDirection == Direction.NOMATTER) && (YDirection == cuurentDy || YDirection == nextDy || YDirection == Direction.NOMATTER))
                        isDetected = true;
                }
            } else if (nextDy == Direction.SAME || cuurentDy == Direction.SAME) {
                if (nextDy == Direction.SAME) {
                    if ((YDirection == cuurentDy || YDirection == Direction.NOMATTER) && (XDirection == cuurentDx || XDirection == nextDx || XDirection == Direction.NOMATTER))
                        isDetected = true;
                } else {
                    if ((YDirection == nextDy || YDirection == Direction.NOMATTER) && (XDirection == cuurentDx || XDirection == nextDx || XDirection == Direction.NOMATTER))
                        isDetected = true;
                }
            }
        }
        return isDetected;
    }
    public void setThreshold(double t){
        THRESHOLD=t;
    }
}