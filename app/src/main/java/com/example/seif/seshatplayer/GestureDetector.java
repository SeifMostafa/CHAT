package com.example.seif.seshatplayer;

import android.util.Log;

import com.example.seif.seshatplayer.model.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by seif on 10/9/17.
 */

public class GestureDetector {

    private Direction[][] gesture;
    private Map<Integer,Integer> charDirectionsLength;
    private boolean completedFlag = false;

    public GestureDetector(Direction[][] Gesture) {
        //this.gesture = clearedRedundancyList(Gesture);
        this.gesture = Gesture;
        charDirectionsLength = new HashMap<>();
        if (this.gesture.length < 2) Log.e("GestureDetector", "Hasn't gesture to detect!");
        int mDirectionsLength = 0;
        for(int i=0; i<gesture.length;i++){
            mDirectionsLength +=gesture[i].length;
            charDirectionsLength.put(i,mDirectionsLength);
        }
    }

    private void raiseCompletedFlag(){
        completedFlag = true;
    }
    public boolean getCompletedFlag(){
        return completedFlag;
    }

    private boolean compareCharDirections(List<Direction>toTest,Direction[]org){
        boolean isDetected = true;
        double mCharSuccessPercentage = 100;
        double mCharProgressStep = 100.0 / ((double) org.length / 2.0);

        for (int j = 0; j < org.length-1; ) {

            Direction d_X = toTest.get(j);
            Direction d_Y = toTest.get(j + 1);
            Direction ORG_d_X = org[j];
            Direction ORG_d_Y = org[j + 1];

            Log.i("GestureDetector", "CompareGuidedVector" + "XD:  " + ORG_d_X);
            Log.i("GestureDetector", "CompareGuidedVector" + "UXD:  " + d_X);
            Log.i("GestureDetector", "CompareGuidedVector" + "YD:  " + ORG_d_Y);
            Log.i("GestureDetector", "CompareGuidedVector" + "UYD:" + d_Y);

            try {
                if (ORG_d_X == Direction.NOMATTER || ORG_d_Y == Direction.NOMATTER) {
                    if (ORG_d_X == Direction.NOMATTER && ORG_d_Y != Direction.NOMATTER) {
                        if (d_Y != ORG_d_Y) {

                                mCharSuccessPercentage -= (mCharProgressStep);
                                isDetected = false;
                        }
                    } else if (ORG_d_X != Direction.NOMATTER && ORG_d_Y == Direction.NOMATTER) {
                        if (d_X != ORG_d_X) {

                                mCharSuccessPercentage -= (mCharProgressStep);
                                isDetected = false;
                        }
                    }
                } else {
                    if ((d_X != ORG_d_X || d_Y != ORG_d_Y)) {

                            mCharSuccessPercentage -= (mCharProgressStep);
                            isDetected = false;
                    }
                }
                j += 2;
            } catch (Exception e) {
                Log.e("GestureDetector", "CompareGuidedVector" + e.toString());
            }
        }
        if (isDetected || mCharSuccessPercentage > 70) {
            return true;
        }else return false;
    }
    public boolean check(ArrayList<Direction> mUserGV) {
        int mLocalCharsSuccessConuter = 0;
        List<Direction> mSubsetFrommUserGV_list = null;
        Direction[] currentCharDirections = null;
        do{
            // make subset from mUserGV ==> char directions.
            int directions_from_mUserGVIndex=0,directions_to_mUserGVIndex;
            directions_to_mUserGVIndex= charDirectionsLength.get(mLocalCharsSuccessConuter);
            if(mLocalCharsSuccessConuter > 0) directions_from_mUserGVIndex =  charDirectionsLength.get(mLocalCharsSuccessConuter-1);

            mSubsetFrommUserGV_list =  mUserGV.subList(directions_from_mUserGVIndex,directions_to_mUserGVIndex);
            currentCharDirections= gesture[mLocalCharsSuccessConuter];
            if(mSubsetFrommUserGV_list.size()>=currentCharDirections.length){
                boolean isDetected = compareCharDirections(mSubsetFrommUserGV_list,currentCharDirections);
                if(isDetected){

                    mLocalCharsSuccessConuter++;
                }else break;
            }else break;
        }while(mLocalCharsSuccessConuter<gesture.length);

        if(mLocalCharsSuccessConuter == gesture.length){
            raiseCompletedFlag();
            return true;
        }else{
            if(mSubsetFrommUserGV_list.size()>=currentCharDirections.length){
                return false;
            }else return true;
        }
    }

    /*private boolean approximateCheck(Direction XDirection, Direction YDirection, int index) {
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
    }*/



}