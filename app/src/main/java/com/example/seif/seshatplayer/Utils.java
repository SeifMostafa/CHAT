package com.example.seif.seshatplayer;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.Log;
import com.example.seif.seshatplayer.model.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import static android.R.id.list;

/**
 * Created by seif on 9/13/17.
 */

public class Utils {

    public static String getCurrentTime() {

        final Calendar c = Calendar.getInstance();
        int currentTime = (c.get(Calendar.HOUR_OF_DAY) * 10000) +
                (c.get(Calendar.MINUTE) * 100) +
                (c.get(Calendar.SECOND));
        Log.w("TIME:", String.valueOf(currentTime));
        return (String.valueOf(currentTime));

    }
    public static Direction[] ComparePointsToCheckFV(double x1, double y1, double x2, double y2) {
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

    public static ArrayList<Direction> ComparePointsToCheckFV(ArrayList<Point> touchedpoints) {
        ArrayList<Direction> result = new ArrayList<>();
        for(int i=0;i<touchedpoints.size()-1;i++) {
            Point p1 = touchedpoints.get(i), p2 = touchedpoints.get(i + 1);
            int x1 = p1.x, x2 = p2.x, y1 = p1.y, y2 = p2.y;
            Direction direction[] =ComparePointsToCheckFV(x1,y1,x2,y2);
            result.add(direction[0]);
            result.add(direction[1]);
            touchedpoints.remove(i);
        }
        return result;
    }
    public static  boolean CompareGuidedVector(ArrayList<Direction> USERgv,ArrayList<Direction> list_Org_Directions) {

        int orgI=0;


        if (!list_Org_Directions.equals(null) && !USERgv.equals(null)) {
            for (int i = 0; i < USERgv.size()-1; i += 2) {
                Direction d_X = USERgv.get(i);
                Direction d_Y = USERgv.get(i + 1);
                if(d_X != null && d_Y != null && list_Org_Directions.get(orgI)!=null&& list_Org_Directions.get(orgI+1)!=null)
                {
                    if(d_X == list_Org_Directions.get(orgI) && d_Y == list_Org_Directions.get(orgI+1))
                    {

                    }else if(list_Org_Directions.get(orgI+2)!=null&& list_Org_Directions.get(orgI+3)!=null){
                        if(d_X == list_Org_Directions.get(orgI+2) && d_Y == list_Org_Directions.get(orgI+3))
                        {

                        }else{

                        }
                    }
                }
/*                if ( && Org_Directions[i+1] != null && Org_Directions[i+2] !=null && Org_Directions[i+3] !=null&& Org_Directions[i+4] !=null  ) {
                    if ((!d_x.equals(Org_Directions[i+1]) && (!d_Y.equals(Org_Directions[i+2]))) || (!d_x.equals(Org_Directions[i+3]) && (!d_Y.equals(Org_Directions[i+4])))) {
                        return false;
                    }else{
                        USERgv.remove(i);
                        USERgv.remove(i+1);
                    }
                }*/
            }
        }
        return true;
    }
   public static ArrayList<Direction> clearedRedundancyList(ArrayList<Direction>dirtyList){

       ArrayList<Direction> result = new ArrayList<>();
       dirtyList.remove(Direction.INIT);

       if(dirtyList.size()>=4){
           for(int i=0;i<dirtyList.size()-3;i++){
                if((dirtyList.get(i) == dirtyList.get(i+2))&&(dirtyList.get(i+1) == dirtyList.get(i+3))){
                    dirtyList.remove(i);
                    dirtyList.remove(i+1);
                }else{
                    result.add(dirtyList.get(i));
                    result.add(dirtyList.get(i+1));
                }
           }
       }else{
           return dirtyList;
       }

       return result;
   }
    public static Direction[] clearedRedundancyList(Direction[]GV){

        Direction result_directions [] = new Direction[GV.length];
        int result_index=0;

        Stack<Direction> stack_directions = new Stack<>();
        stack_directions.addAll(Arrays.asList(GV));

        while(stack_directions.contains(Direction.INIT))
            stack_directions.remove(Direction.INIT);


        for(int i=0;i<stack_directions.size()-3;i+=2){
            Direction DX = stack_directions.get(i);
            Direction DY = stack_directions.get(i+1);
            if(stack_directions.get(i+2).equals(DX) && stack_directions.get(i+3).equals(DY)){
                stack_directions.pop();
                stack_directions.pop();
            }else{
                result_directions[result_index++] = DX;
                result_directions[result_index++] = DY;
            }
        }
        return result_directions;
    }

}
