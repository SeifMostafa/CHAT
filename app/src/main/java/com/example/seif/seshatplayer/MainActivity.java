package com.example.seif.seshatplayer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.seif.seshatplayer.layout.AnimationFragment;
import com.example.seif.seshatplayer.layout.HelpFragment;
import com.example.seif.seshatplayer.layout.MainFragment;
import com.example.seif.seshatplayer.layout.PhrasePickFragment;
import com.example.seif.seshatplayer.model.Direction;
import com.example.seif.seshatplayer.model.Word;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;


public class MainActivity extends AppCompatActivity {


    public static final String AnimationKey = "AK", WORDS_PREFS_NAME = "WordsPrefsFile", WordLoopKey = "WL", WordIndexKey = "WI", WordKey = "w", PhraseKey = "p", WordsArrayKey = "WA";
    private static final int PERMISSIONS_MULTIPLE_REQUEST = 122;
    SharedPreferences sharedPreferences_words = null;
    SharedPreferences.Editor sharedPreferences_words_editor = null;
    MediaPlayer mediaPlayer = null;
    private String WordsFilePath = "/SF/WORDS.txt", PhrasesFilePath = "/SF/PHRASES.txt", AppenddedToOutputFVfile = "_fv.txt", AppenddedToOutputTriggerPointsfile = "_trpoints.txt", AppendedToImageFile = ".png", AppendedToSpeechFile = ".wav", SF = "/SF/";
    private ArrayList<String> words, phrases;
    private int word_loop = 1, word_index = 0;
    private String filename = "Archive.txt";
    private int DEFAULT_LESSON_LENGTH = 5;
    private int DEFAULT_LOOP_COUNTER = 4;
    private int DEFAULT_TYPEFACE_LEVELS = 4;

    private String firstPhrase = "أنا إسمي ";
    public static String firstPhraseAudioPath = "myname";
    private String firstTimekey = "1stTime";

    public static String TAG = "MainActivity";
    int height,width;
/*
  * read file into string and the end = \n and return this string
  */
    private Stack<String> readFileintoStack(String filepath) {

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

    public static Direction[] getDirections(String filepath) {
        Stack<Direction> directions = new Stack<>();
        File file = new File(filepath);
        try {
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                String line = null;
                line = scan.nextLine();
                switch (line.charAt(0)) {
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
                    case 'S':
                        directions.push(Direction.SAME);
                        break;
                    case 'N':
                        directions.push(Direction.NOMATTER);
                        break;
                }
            }
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
        Direction[] result = new Direction[directions.size()];
        return directions.toArray(result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences_words = this.getSharedPreferences(WORDS_PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences_words_editor = sharedPreferences_words.edit();
        checkPermission_AndroidVersion();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //OpenPhraseFragment("سيف مصطفى","سيف");

        Direction [] directions = new Direction[]{Direction.SAME,Direction.DOWN,Direction.LEFT,Direction.SAME};
        Direction [] org_directions = new Direction[]{Direction.RIGHT,Direction.NOMATTER};
        GestureDetector gestureDetector = new GestureDetector(org_directions);

        ArrayList<Direction>directions1 = new ArrayList<>();
        directions1.addAll(Arrays.asList(directions));

        Log.i("LittleCheck: ",gestureDetector.check(directions1)+" ");
    }

    private void startApp() {
        // read file into words
        try {
            WordsFilePath = Environment.getExternalStorageDirectory() + WordsFilePath;
            PhrasesFilePath = Environment.getExternalStorageDirectory() + PhrasesFilePath;
            new File(Environment.getExternalStorageDirectory(), filename);
            SF = Environment.getExternalStorageDirectory() + SF;
        } catch (Exception e) {
            Log.e("StorageE:", e.toString());
            e.printStackTrace();
        }
        try {
            words = new ArrayList<>(readFileintoStack(WordsFilePath));
            phrases = new ArrayList<>(readFileintoStack(PhrasesFilePath));
            Log.i("words:", "" + words.size());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("words", "error");
        }
        if (sharedPreferences_words.getAll().isEmpty()) {
            word_loop = 0;
            word_index = 0;
            SaveOnSharedPref(WordLoopKey, String.valueOf(word_loop));
            SaveOnSharedPref(WordIndexKey, String.valueOf(word_index));
        } else {
            word_loop = Integer.parseInt(sharedPreferences_words.getString(WordLoopKey, "0"));
            word_index = Integer.parseInt(sharedPreferences_words.getString(WordIndexKey, "0"));
        }

        if (Boolean.valueOf(sharedPreferences_words.getString(firstTimekey, "true"))) {
            Word phrase = new Word(firstPhrase + words.get(0));
            OpenMainFragment(phrase);
            SaveOnSharedPref(firstTimekey, String.valueOf(false));
        } else {
            Word word = new Word("س");
            Direction [][] word_directions = new Direction[1][];
            word_directions[0] = getDirections(SF + "س" + 2 + AppenddedToOutputFVfile);
            word.setFV(word_directions);
            OpenMainFragment(word);
        }
    }

    public String getNextWord() {
        return words.get(++word_index);
    }

    public String getPrevWord() {
        return words.get(--word_index);
    }

    public String getCurrentWord() {
        return words.get(word_index);
    }

    public Typeface updateWordLoop() {
        Typeface tf = null;

        if (word_loop < (DEFAULT_LOOP_COUNTER * DEFAULT_TYPEFACE_LEVELS)) {
            if (word_loop % DEFAULT_LOOP_COUNTER == 0) {
                // change font
                if (word_loop >= 0 && word_loop <= DEFAULT_LOOP_COUNTER) {
                    tf = Typeface.createFromAsset(getAssets(), "fonts/lvl1.ttf");
                } else if (word_loop > DEFAULT_LOOP_COUNTER && word_loop <= DEFAULT_LOOP_COUNTER * 2) {
                    tf = Typeface.createFromAsset(getAssets(), "fonts/lvl2.ttf");
                } else if (word_loop > DEFAULT_LOOP_COUNTER * 2 && word_loop <= DEFAULT_LOOP_COUNTER * 3) {
                    tf = Typeface.createFromAsset(getAssets(), "fonts/lvl3.ttf");
                }
            }
            word_loop++;
        } else {
            // change word
            word_loop = 0;
            updatelesson(1);
        }
        return tf;
    }


    private Word form_word(int index) {
        try {
            Word resultWord = new Word(words.get(index), SF + words.get(index) + AppendedToImageFile, SF + words.get(index) + AppendedToSpeechFile, phrases.get(index));
                resultWord.setFV(prepareWordGuidedVectors(words.get(index)));
            return resultWord;
         } catch (Exception e) {
            Log.e("form_wordE:", e.toString());
            e.printStackTrace();
            return null;
        }
    }

    private Direction[][] prepareWordGuidedVectors(String word) {
           Direction[][] result_directions = new Direction[word.length()][];
           ArrayList<Character> differentchars = new ArrayList<>();
           Character[] characters = {'أ', 'إ', 'د', 'ذ', 'ر', 'ز', 'و', 'ؤ','ا'};
           differentchars.addAll(Arrays.asList(characters));
           for (int i = 0; i < word.length(); i++) {
               if (i == 0) {
                   result_directions[i] = getDirections(SF + word.charAt(i) + 1 + AppenddedToOutputFVfile);
               } else if (i == word.length() - 1) {
                   if (differentchars.contains(word.charAt(i-1))) {
                       result_directions[i] = getDirections(SF + word.charAt(i) + 2 + AppenddedToOutputFVfile);

                   }else{
                       result_directions[i] = getDirections(SF + word.charAt(i) + 0 + AppenddedToOutputFVfile);
                   }
               } else {
                   if (differentchars.contains(word.charAt(i-1))) {
                       result_directions[i] = getDirections(SF + word.charAt(i) + 2 + AppenddedToOutputFVfile);

                   }else{
                       result_directions[i] = getDirections(SF + word.charAt(i) + 3 + AppenddedToOutputFVfile);
                   }
               }
           }
           return result_directions;
       }
    /*
    ToFlag: if 0 = current, if -1 = prev;
     */
    public void updatelesson(int ToFlag) {
        switch (ToFlag) {
            case 0:
                OpenMainFragment(word_index);
                break;
            case -1:
                if (word_index == 0) {
                    OpenMainFragment(word_index);
                } else {
                    OpenMainFragment(--word_index);
                }
                break;
            case 1:
                OpenMainFragment(++word_index);
                break;
        }
    }

    public void updatelesson(String word) {
        if (word != null) {
            OpenMainFragment(words.indexOf(word));
        } else {
            finish();
            Log.e("updatelessonE:", "word == null");
        }
    }

    public void voiceoffer(View view, String DataPath2Bplayed) {
        if (view != null) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            view.startAnimation(shake);
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isLooping() || mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(SF + DataPath2Bplayed);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.stop());
    }

    public void voiceoffer(View view, int res_id) {
        if (view != null) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            view.startAnimation(shake);
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }

        try {
            mediaPlayer = MediaPlayer.create(this, res_id);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.stop();

            }
        });
    }

    public void StopMediaPlayer() {
        Log.i("MainActivity", "StopMediaPlayer");
        if (mediaPlayer.isPlaying() || mediaPlayer.isLooping()) {
            mediaPlayer.stop();
        }
    }

    public void helpbypic(View view, String img2Bdisplayed) {
        if (view != null) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            view.startAnimation(shake);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("شكرا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.layout_sample_pic_help, null);
        ImageView imageView = (ImageView) dialogLayout.findViewById(R.id.picsample);
        File imgFile = new File(SF + img2Bdisplayed);
        if (imgFile.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void SaveOnSharedPref(String key, String value) {
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
            startApp();
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
                        startApp();
                    } else {
                        Log.i("onRequestPermResult", "" + RecordAudioPermission + "," + InternetPermission + "," + write_storagePermission + "," + read_storagePermission);
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

    /*
    dummy fn till getting data separated in lessons
     */
    private Word[] fillWordsArray(int arraySize, int index) {

        Word[] wordsArray = new Word[arraySize];
        for (int i = 0; i < arraySize; i++) {
            wordsArray[i] = form_word(index);
            index++;
        }
        return wordsArray;
    }

    private void OpenMainFragment(int i) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        Word[] wordsArray = fillWordsArray(DEFAULT_LESSON_LENGTH, i);
        bundle.putParcelableArray(WordsArrayKey, wordsArray);
        mainFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_replacement, mainFragment);
        fragmentTransaction.commit();
    }

    private void OpenMainFragment(Word word) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(WordKey, word);
        mainFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_replacement, mainFragment);
        fragmentTransaction.commit();
    }

    public void OpenPhraseFragment(String phrase, String word) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PhrasePickFragment phrasePickFragment = new PhrasePickFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PhraseKey, phrase);
        bundle.putString(WordKey, word);
        phrasePickFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_replacement, phrasePickFragment);
        fragmentTransaction.commit();
    }

    public void OpenAnimationFragment(String word) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AnimationFragment animationFragment = new AnimationFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AnimationKey, word);
        animationFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_replacement, animationFragment);
        fragmentTransaction.commit();
    }

    public void OpenHelpFragment() {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HelpFragment helpFragment = new HelpFragment();
        fragmentTransaction.replace(R.id.fragment_replacement, helpFragment);
        fragmentTransaction.commit();
    }

    public void AssignWordAsFinished(String Word) {
        try {
            FileWriter writer = new FileWriter(filename, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(Word + "\n");
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> ReadArchiveWords() {
        ArrayList<String> words = new ArrayList<>();
        try {
            FileReader reader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                words.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    public ArrayList<String> getWords() {
        return new ArrayList<>(this.words);
    }

}