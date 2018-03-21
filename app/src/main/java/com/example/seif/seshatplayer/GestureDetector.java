package com.example.seif.seshatplayer;

import android.os.Environment;
import android.util.Log;

import com.example.seif.seshatplayer.model.Direction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by seif on 10/9/17.
 */

public class GestureDetector {

    int counter = 0;
    private double THRESHOLD = 76;
    private ArrayList<Direction> wholeWord;
    private HashMap<String, Integer> wrongDirectionsCounter = new HashMap<>();

    //constructor
    GestureDetector(Direction[][] Gesture) {
        //getting word directions
        wholeWord = new ArrayList<>();
        for (Direction[] aGesture : Gesture) {
            wholeWord.addAll(Arrays.asList(aGesture));
        }
        if (this.wholeWord.size() < 2)
            Log.i("GestureDetector", "Hasn't gesture to detect!");
        //setting customized Threshold for each word
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
                //when painted directions less than the actual one
                for (int i = 0; i < mUserGV.size() - 1; i += 2) {
                    Direction d_X = mUserGV.get(i);
                    Direction d_Y = mUserGV.get(i + 1);
                    Direction ORG_d_X = wholeWord.get(i);
                    Direction ORG_d_Y = wholeWord.get(i + 1);
                    try {
                        //start comparing
                        if (ORG_d_X == Direction.NOMATTER || ORG_d_Y == Direction.NOMATTER) {
                            //when point is a dot
                            if (ORG_d_X == Direction.NOMATTER && ORG_d_Y != Direction.NOMATTER) {
                                if (d_Y != ORG_d_Y) {
                                    //when painted not equal actual
                                    if (!approximateCheck(mUserGV, ORG_d_X, ORG_d_Y, i)) {
                                        successPercentage -= progressStep;
                                        checkWrongDirection(ORG_d_X, ORG_d_Y);
                                    }
                                }
                            } else if (ORG_d_X != Direction.NOMATTER) {
                                if (d_X != ORG_d_X) {
                                    //when painted not equal actual
                                    if (!approximateCheck(mUserGV, ORG_d_X, ORG_d_Y, i)) {
                                        successPercentage -= progressStep;
                                        checkWrongDirection(ORG_d_X, ORG_d_Y);
                                    }
                                }
                            }
                        } else {
                            //word at any direction
                            if ((d_X != ORG_d_X || d_Y != ORG_d_Y)) {
                                if (!approximateCheck(mUserGV, ORG_d_X, ORG_d_Y, i)) {
                                    //when painted not equal actual
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
        if (wrongDirectionsCounter.isEmpty()) {
            wrongDirectionsCounter = readFileToMap();
        }
        if (wrongDirectionsCounter.containsKey((ORG_d_X.toString()) + (ORG_d_Y.toString()))) {
            counter = wrongDirectionsCounter.get((ORG_d_X.toString()) + (ORG_d_Y.toString()));
            wrongDirectionsCounter.replace((ORG_d_X.toString()) + (ORG_d_Y.toString()), counter, ++counter);
        } else {
            counter = 0;
            wrongDirectionsCounter.put((ORG_d_X.toString()) + (ORG_d_Y.toString()), ++counter);
        }
        Log.i("GestureDetector", wrongDirectionsCounter.toString());
        writeMapToFile(wrongDirectionsCounter);
    }

    //approximate check
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

    //write your map of wrong direction to file
    private void writeMapToFile(HashMap<String, Integer> hashMap) throws IOException {
        String file = Environment.getExternalStorageDirectory() + "/SeShatSF/wrongDirections.txt";
        FileWriter fStream;
        BufferedWriter out;
        fStream = new FileWriter(file, false);
        out = new BufferedWriter(fStream);
        for (Map.Entry<String, Integer> pairs : hashMap.entrySet()) {
            out.write(pairs.getKey() + ":" + pairs.getValue() + "\n");
        }
        out.close();
    }
//read your map of wrong direction to make changes on it

    private HashMap<String, Integer> readFileToMap() throws IOException {
        String file = Environment.getExternalStorageDirectory() + "/SeShatSF/wrongDirections.txt";
        HashMap<String, Integer> map = new HashMap<>();
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(":", 2);
            if (parts.length >= 2) {
                String key = parts[0];
                String value = parts[1];
                map.put(key, Integer.parseInt(value));
            } else {
                System.out.println("ignoring line: " + line);
            }
        }
        reader.close();
        return map;
    }


    /*public void setThreshold(double t) {
        THRESHOLD = t;
    }*/
}