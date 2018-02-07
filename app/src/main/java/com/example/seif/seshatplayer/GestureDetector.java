package com.example.seif.seshatplayer;

import android.util.Log;

import com.example.seif.seshatplayer.model.Direction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by seif on 10/9/17.
 */

public class GestureDetector {

    int counter = 0;
    private double THRESHOLD = 76;
    private ArrayList<Direction> wholeWord;
    private HashMap<String, Integer> wrongDirectionscounter = new HashMap<>();

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
                                        checkWrongDirection(ORG_d_X, ORG_d_Y);

                                    }
                                }
                            } else if (ORG_d_X != Direction.NOMATTER) {
                                if (d_X != ORG_d_X) {
                                    if (!approximateCheck(mUserGV, ORG_d_X, ORG_d_Y, i)) {
                                        successPercentage -= progressStep;
                                        checkWrongDirection(ORG_d_X, ORG_d_Y);

                                    }
                                }
                            }
                        } else {
                            if ((d_X != ORG_d_X || d_Y != ORG_d_Y)) {
                                if (!approximateCheck(mUserGV, ORG_d_X, ORG_d_Y, i)) {
                                    successPercentage -= progressStep;
                                    checkWrongDirection(ORG_d_X, ORG_d_Y);
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

    private void checkWrongDirection(Direction ORG_d_X, Direction ORG_d_Y) throws IOException {
        if (wrongDirectionscounter.containsKey((ORG_d_X.toString()) + (ORG_d_Y.toString()))) {
            counter = wrongDirectionscounter.get((ORG_d_X.toString()) + (ORG_d_Y.toString()));
            wrongDirectionscounter.replace((ORG_d_X.toString()) + (ORG_d_Y.toString()), counter++);
        } else {
            wrongDirectionscounter.put((ORG_d_X.toString()) + (ORG_d_Y.toString()), counter++);
        }
        Log.i("GestureDetector", wrongDirectionscounter.toString());
        writeMapToFile(wrongDirectionscounter);
    }

    private boolean approximateCheck(ArrayList<Direction> mUserGV, Direction XDirection, Direction YDirection, int index) {
        boolean isDetected = false;
        if (index + 3 <= mUserGV.size()) {
            Direction nextDx, nextDy, currentDx, currentDy;

            nextDx = mUserGV.get(index + 2);
            nextDy = mUserGV.get(index + 3);

            currentDx = mUserGV.get(index);
            currentDy = mUserGV.get(index + 1);

            if (nextDx == Direction.SAME || currentDx == Direction.SAME) {
                if (nextDx == Direction.SAME) {
                    if ((XDirection == currentDx || XDirection == Direction.NOMATTER) && (YDirection == currentDy || YDirection == nextDy || YDirection == Direction.NOMATTER))
                        isDetected = true;
                } else {
                    if ((XDirection == nextDx || XDirection == Direction.NOMATTER) && (YDirection == currentDy || YDirection == nextDy || YDirection == Direction.NOMATTER))
                        isDetected = true;
                }
            } else if (nextDy == Direction.SAME || currentDy == Direction.SAME) {
                if (nextDy == Direction.SAME) {
                    if ((YDirection == currentDy || YDirection == Direction.NOMATTER) && (XDirection == currentDx || XDirection == nextDx || XDirection == Direction.NOMATTER))
                        isDetected = true;
                } else {
                    if ((YDirection == nextDy || YDirection == Direction.NOMATTER) && (XDirection == currentDx || XDirection == nextDx || XDirection == Direction.NOMATTER))
                        isDetected = true;
                }
            }
        }
        return isDetected;
    }

    private void writeMapToFile(HashMap<String, Integer> hashMap) throws IOException {
        FileWriter fStream;
        BufferedWriter out;
        fStream = new FileWriter("values.txt");
        out = new BufferedWriter(fStream);
        for (Map.Entry<String, Integer> pairs : hashMap.entrySet()) {
            out.write(pairs.getKey() + " " + pairs.getValue() + "\n");
        }

        out.close();
    }

    public void setThreshold(double t) {
        THRESHOLD = t;
    }
}