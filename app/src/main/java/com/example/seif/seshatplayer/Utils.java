package com.example.seif.seshatplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;
import java.util.Stack;

/**
 * Created by seif on 9/13/17.
 */

public class Utils {
    public static final String SyllabusFolderPath="/SF/";
    // media player
    public static String getCurrentTime() {

        final Calendar c = Calendar.getInstance();
        int currentTime = (c.get(Calendar.HOUR_OF_DAY) * 10000) +
                (c.get(Calendar.MINUTE) * 100) +
                (c.get(Calendar.SECOND));
        Log.w("TIME:", String.valueOf(currentTime));
        return (String.valueOf(currentTime));

    }
    public static Stack<String>  readfileintoStack(String filepath){

        File file = new File(filepath);
        Stack<String> words = new Stack<>();
        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine())
            {
                String line =null;
                line = scan.nextLine();
                words.push(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
        return words;
    }



}
