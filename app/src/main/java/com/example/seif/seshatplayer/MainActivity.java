package com.example.seif.seshatplayer;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.seif.seshatplayer.layout.MainFragment;
import com.example.seif.seshatplayer.model.Direction;
import com.example.seif.seshatplayer.model.Word;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;


public class MainActivity extends Activity {

    private static final int PERMISSIONS_MULTIPLE_REQUEST = 122;
    public static final String WORDS_PREFS_NAME = "WordsPrefsFile", WordLoopKey  = "WL" , WordIndexKey  = "WI",WordKey ="w";
    private final String WordsFilePath="WORDS.txt",PhrasesFilePath = "PHRASES.txt",AppenddedToOutputFVfile = "_fv.txt", AppenddedToOutputTriggerPointsfile = "_trpoints.txt"
            ,AppendedToImageFile =".png",AppendedToSpeechFile = ".wav";

    private static final int RESULT_SPEECH = 1;
    private Stack<String> words,phrases;
    private int word_loop=0,word_index=0;
    SharedPreferences sharedPreferences_words;
    SharedPreferences.Editor sharedPreferences_words_editor  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission_AndroidVersion();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sharedPreferences_words = this.getSharedPreferences(WORDS_PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences_words_editor = sharedPreferences_words.edit();

        // read file into words
        words =   readFileintoStrack(WordsFilePath);
        phrases = readFileintoStrack(PhrasesFilePath);

        if(sharedPreferences_words.getAll().isEmpty()){
            word_loop  =0;
            word_index = 0;
            SaveOnSharedPref(WordLoopKey,String.valueOf(word_loop));
            SaveOnSharedPref(WordIndexKey,String.valueOf(word_index));
        }else{
            word_loop  = Integer.parseInt(sharedPreferences_words.getString(WordLoopKey,"0"));
            word_index = Integer.parseInt(sharedPreferences_words.getString(WordIndexKey,"0"));
        }


        OpenMainFragment(word_index);

      /*  HelpFragment helpFragment = new HelpFragment();
        fragmentTransaction.replace(R.id.fragment_replacement,helpFragment);*/
        /*PhrasePickFragment phrasePickFragment = new PhrasePickFragment();
        fragmentTransaction.replace(R.id.fragment_replacement,phrasePickFragment);
        fragmentTransaction.commit();*/
    }

    public String getNextWord(){
        return words.get(++word_index);
    }
    public String getPrevWord(){
        return words.get(--word_index);
    }
    public String getCurrentWord(){
        return words.get(word_index);
    }
    private Word form_word(int index){
        return new Word(words.get(index),words.get(index)+AppendedToImageFile,words.get(index)+AppendedToSpeechFile,phrases.get(index)
                ,getPoints(words.get(index)+AppenddedToOutputTriggerPointsfile),getDirections(words.get(index)+AppenddedToOutputFVfile));
    }
    /*
    ToFlag: if 0 = current, if -1 = prev;
     */
    public void updatelesson(int ToFlag){
        switch(ToFlag){
            case 0:
                OpenMainFragment(word_index);
                break;
            case -1:
                OpenMainFragment(--word_index);
                break;
        }
    }
    public void voiceoffer(View view, String DataPath2Bplayed) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        view.startAnimation(shake);

        final MediaPlayer mp = new MediaPlayer();
        if (mp.isPlaying()) {
            mp.stop();
        }
        try {
            mp.setDataSource(DataPath2Bplayed);
            mp.prepare();
            mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void  SaveOnSharedPref(String key, String value) {
        sharedPreferences_words_editor.putString(key, value).apply();
        sharedPreferences_words_editor.commit();
    }

    private void checkPermission_AndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();

        } else {
            // write your logic here if while testing under M.Devices
            // not granted!
        }

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.INTERNET) + ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.INTERNET) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.RECORD_AUDIO)) {

                Snackbar.make(this.findViewById(android.R.id.content),
                        "Please Grant Permissions to upload profile photo",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO},
                                        PERMISSIONS_MULTIPLE_REQUEST);
                            }
                        }).show();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO},
                        PERMISSIONS_MULTIPLE_REQUEST);
            }
        } else {
            // write your logic code if permission already granted
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean RecordAudioPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean InternetPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean write_storagePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean read_storagePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (RecordAudioPermission && InternetPermission && write_storagePermission && read_storagePermission) {
                        // write your logic here
                    } else {
                        Snackbar.make(this.findViewById(android.R.id.content),
                                "Please Grant Permissions to be able to work",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        requestPermissions(
                                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET,
                                                        Manifest.permission.RECORD_AUDIO},
                                                PERMISSIONS_MULTIPLE_REQUEST);
                                    }
                                }).show();
                    }
                }
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_SPEECH && requestCode == RESULT_OK) ;
        {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(results.size()>0){

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private Direction[] getDirections(String filepath){
        Stack<Direction> directions = new Stack<>() ;
        File file = new File(filepath);
        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine())
            {
                String line =null;
                line = scan.nextLine();
                switch(line.charAt(0)){
                    case 'I':
                        directions.push(Direction.INIT);
                        break;
                    case 'E':
                        directions.push(Direction.END);
                        break;
                    case 'L':
                        directions.push(Direction.LEFT);
                        break;
                    case 'R':
                        directions.push(Direction.RIGHT);
                        break;
                    case 'U':
                        directions.push(Direction.UP);
                        break;
                    case 'D':
                        directions.push(Direction.DOWN);
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
        Direction[] result = new Direction[directions.size()];
        return directions.toArray(result);
    }
    private Point[] getPoints(String filepath){
        Stack<Point> points = new Stack<>() ;
        File file = new File(filepath);
        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine())
            {
                String line =null;
                line = scan.nextLine();
                String[]x_y = line.split(",");
                points.push(new Point(Integer.parseInt(x_y[0]),Integer.parseInt(x_y[1])));
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
        Point[] result = new Point[points.size()];
        return points.toArray(result);
    }

    private void OpenMainFragment(int i){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(WordKey, form_word(i));
        mainFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_replacement,mainFragment);
    }
    /*
  * read file into string and the end = \n and return this string
  */
    public static Stack<String> readFileintoStrack(String filepath) {

        Stack<String> result = new Stack<>();
        try {
            FileReader reader = new FileReader(filepath);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                result.push(line);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}