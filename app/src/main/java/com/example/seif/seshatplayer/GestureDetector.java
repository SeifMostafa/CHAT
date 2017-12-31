package com.example.seif.seshatplayer;

import android.util.Log;

import com.example.seif.seshatplayer.model.Direction;

import java.util.ArrayList;

/**
 * Created by seif on 10/9/17.
 */

public class GestureDetector {

    private double THRESHOLD = 76;
    private ArrayList<Direction> wholeWord;


    GestureDetector(Direction[][] Gesture) {
        wholeWord = new ArrayList<>();
        for (int j = 0; j < Gesture.length; j++) {
            for (int l = 0; l < Gesture[j].length; l++) {
                wholeWord.add(Gesture[j][l]);
            }
        }
        if (this.wholeWord.size() < 2)
            Log.i("GestureDetector", "Hasn't gesture to detect!");
        THRESHOLD += Gesture.length * 10 / wholeWord.size();
    }

    boolean check(ArrayList<Direction> mUserGV) {
        Log.i("GestureDetector", "wholeWord.size() = " + this.wholeWord.size());
        Log.i("GestureDetector", "mUserGV = " + mUserGV.toString());
        Log.i("GestureDetector", "wholeWord = " + wholeWord.toString());

        double successPercentage = 100;
        double progressStep = (100.0 / (double) wholeWord.size());
        boolean isDetected = false;
        try {
            if (mUserGV.size() <= wholeWord.size()) {
                for (int i = 0; i < mUserGV.size() - 1; i += 2) {
                    Direction d_X = mUserGV.get(i);
                    Direction d_Y = mUserGV.get(i + 1);
                    Direction ORG_d_X = wholeWord.get(i);
                    Direction ORG_d_Y = wholeWord.get(i + 1);
                    try {
                        if (ORG_d_X == Direction.NOMATTER || ORG_d_Y == Direction.NOMATTER) {

                            if (ORG_d_X == Direction.NOMATTER && ORG_d_Y != Direction.NOMATTER) {
                                if (d_Y != ORG_d_Y) {
                                    if (!approximateCheck(mUserGV, ORG_d_X, ORG_d_Y, i)) {
                                        successPercentage -= progressStep;
                                    }
                                }
                            } else if (ORG_d_X != Direction.NOMATTER) {
                                if (d_X != ORG_d_X) {
                                    if (!approximateCheck(mUserGV, ORG_d_X, ORG_d_Y, i)) {
                                        successPercentage -= progressStep;
                                    }
                                }
                            }
                        } else {
                            if ((d_X != ORG_d_X || d_Y != ORG_d_Y)) {
                                if (!approximateCheck(mUserGV, ORG_d_X, ORG_d_Y, i)) {
                                    successPercentage -= progressStep;
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("GestureDetector", "CompareGuidedVector" + e.toString());
                    }

                }
                if (successPercentage > THRESHOLD) {
                    isDetected = true;
                }

                Log.i("GestureDetector", "successPercentage = " + successPercentage);
                Log.i("GestureDetector", "THRESHOLD = " + THRESHOLD);
                Log.i("GestureDetector", "isDetected = " + isDetected);

            } else {

                Log.i("GestureDetector", "check " + "shortage in touched points data");
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

    public void setThreshold(double t) {
        THRESHOLD = t;
    }
}