package com.example.seif.seshatplayer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.example.seif.seshatplayer.layout.LessonFragment;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import static java.lang.Thread.sleep;


public class MainActivity extends AppCompatActivity {


    public static final String WORDS_PREFS_NAME = "WordsPrefsFile", WordIndexKey = "i", WordKey = "w", PhraseKey = "p", LessonKey = "L";
    public static final String SFKEY = "0";
    private static final int PERMISSIONS_MULTIPLE_REQUEST = 122;
    public static String firstTimekey = "1stTime";

    SharedPreferences sharedPreferences_words = null;
    SharedPreferences.Editor sharedPreferences_words_editor = null;
    MediaPlayer mediaPlayer = null;

    private String WordsFilePath = "WORDS.txt", PhrasesFilePath = "PHRASES.txt", SF = "/SeShatSF/";
    private String FileWordsAchieved = "Archive.txt";

    private Map<Integer, Word[]> lessons;
    private int word_index = 0;
    private int lesson_index = 1;

    private String firstPhrase = "أنا إسمي ";

    public static Direction[] getDirections(String filepath) {
        Stack<Direction> directions = new Stack<>();
        File file = new File(filepath);
        try {
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                String line = null;
                line = scan.nextLine();
                switch (line.charAt(0)) {
                  /*  case 'I':
                        directions.push(Direction.INIT);
                        break;
                    case 'E':
                        directions.push(Direction.END);
                        break;*/
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

    /*
      * read file into string and the end = \n and return this string
      */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences_words = this.getSharedPreferences(WORDS_PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences_words_editor = sharedPreferences_words.edit();
        checkPermission_AndroidVersion();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void startApp() {
        if (Boolean.valueOf(sharedPreferences_words.getString(firstTimekey, "true"))) {
            lesson_index = 1;
            word_index = 0;

            SF = Environment.getExternalStorageDirectory() + SF;

            WordsFilePath = SF + WordsFilePath;
            PhrasesFilePath = SF + PhrasesFilePath;

            new File(Environment.getExternalStorageDirectory() + FileWordsAchieved);

            setLessons();
            Word phrase = new Word(firstPhrase + lessons.get(lesson_index)[0].getText());
            OpenLessonFragment(phrase);

            SaveOnSharedPref(LessonKey, String.valueOf(lesson_index));
            SaveOnSharedPref(WordIndexKey, String.valueOf(word_index));
            SaveOnSharedPref(SFKEY, SF);
            SaveOnSharedPref(firstTimekey, String.valueOf(false));
            try {
                voiceoffer(null, getResources().getString(R.string.myname));
                sleep(1000);
                voiceoffer(null, lessons.get(lesson_index)[0].getText());
                Handler handler = new Handler();
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        // do something
                        updatelesson(0,true);
                    }
                }, 1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {

            lesson_index = Integer.parseInt(sharedPreferences_words.getString(LessonKey, "1"));

            word_index = Integer.parseInt(sharedPreferences_words.getString(WordIndexKey, "0"));
            SF = sharedPreferences_words.getString(SFKEY, SF);
            WordsFilePath = SF + WordsFilePath;
            PhrasesFilePath = SF + PhrasesFilePath;

            setLessons();
            OpenLessonFragment(lesson_index);
        }
    }


    private Word form_word(String txt, String phrase) {
        try {
            Word resultWord = new Word(txt, SF + txt + ".png", SF + txt, phrase);
            resultWord.setFV(prepareWordGuidedVectors(txt));
            return resultWord;
        } catch (Exception e) {
            Log.e("form_wordE:", e.toString());
            e.printStackTrace();
            return null;
        }
    }

    private Direction[][] prepareWordGuidedVectors(String word) {
        char charConnector = 'ـ';
        Direction[][] result_directions = new Direction[word.length()][];
        ArrayList<Character> differentchars = new ArrayList<>();
        Character[] charactersWithoutEndConnector = {'أ', 'إ', 'د', 'ذ', 'ر', 'ز', 'و', 'ؤ', 'ا', 'آ'};
        differentchars.addAll(Arrays.asList(charactersWithoutEndConnector));
        for (int i = 0; i < word.length(); i++) {
            Character character = word.charAt(i);

            if (i == 0) {
                if (differentchars.contains(character)) {
                    result_directions[i] = getDirections(SF + character);
                } else {
                    result_directions[i] = getDirections(SF + character + charConnector);
                }
            } else if (i == word.length() - 1) {
                if (differentchars.contains(word.charAt(i - 1))) {
                    result_directions[i] = getDirections(SF + character);
                } else {
                    result_directions[i] = getDirections(SF + charConnector + character);
                }
            } else {
                if (differentchars.contains(word.charAt(i - 1)) && differentchars.contains(character)) {
                    result_directions[i] = getDirections(SF + character);
                } else if (differentchars.contains(word.charAt(i - 1)) && !differentchars.contains(character)) {
                    result_directions[i] = getDirections(SF + character + charConnector);
                } else {
                    result_directions[i] = getDirections(SF + charConnector + character + charConnector);
                }
            }
        }
        return result_directions;
    }

    /*
    ToFlag: if 0 = current, if -1 = prev , if 1 = next; flag to obtain the ToFlag cretira
    else to navigate to ToFlag as lesson index
     */
    public void updatelesson(int ToFlag, boolean flag) {
        if (flag) {
            switch (ToFlag) {
                case 0:
                    OpenLessonFragment(lesson_index);
                    break;
                case -1:
                    OpenLessonFragment(lesson_index - 1);
                    break;
                case 1:
                    OpenLessonFragment(lesson_index + 1);
                    break;
            }
        } else {
            lesson_index = ToFlag;
            setLessons();
            OpenLessonFragment(ToFlag);
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
            Log.e("MainActivity", "voiceoffer::e: " + e.toString());
            Log.e("MainActivity", "voiceoffer::DataPath2Bplayed: " + SF + DataPath2Bplayed);
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
       /* builder.setPositiveButton("شكرا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });*/
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
        dialog.setCancelable(true);
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


    private void OpenLessonFragment(int i) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LessonFragment lessonFragment = new LessonFragment();
        Bundle bundle = new Bundle();
        Word[] lesson = lessons.get(i);

        Log.i("MainActivity", "OpenLessonFragment: am here with i(lesson_index)= " + i);
        Log.i("MainActivity", "OpenLessonFragment: & lesson.sz= " + lesson.length);

        bundle.putParcelableArray(LessonKey, lesson);
        lessonFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_replacement, lessonFragment);
        fragmentTransaction.commit();
        Log.i("MainActivity", "OpenLessonFragment:: lesson_index" + lesson_index);

    }

    public void OpenLessonFragment(Word word) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LessonFragment lessonFragment = new LessonFragment();
        Bundle bundle = new Bundle();

        if (lesson_index == 1) {
             word = form_word(word.getText(), null);
            bundle.putBoolean(firstTimekey, true);
            bundle.putParcelableArray(LessonKey, lessons.get(1));
        } else {
            bundle.putBoolean(firstTimekey, false);
            bundle.putParcelableArray(LessonKey, lessons.get(lesson_index));
        }

        bundle.putParcelable(WordKey, word);
        lessonFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_replacement, lessonFragment);
        fragmentTransaction.commit();
        Log.i("MainActivity", "OpenLessonFragment:: lesson_index" + lesson_index);
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
        bundle.putString(WordKey, word);
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
            FileWriter writer = new FileWriter(FileWordsAchieved, true);
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
            FileReader reader = new FileReader(FileWordsAchieved);
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

    private void setLessons() {
        lessons = new HashMap<>();
        int lessonNum = lesson_index - 1;
        try {
            FileReader wordsReader = new FileReader(WordsFilePath);
            FileReader phraseReader = new FileReader(PhrasesFilePath);
            BufferedReader WordsBufferedReader = new BufferedReader(wordsReader);
            BufferedReader PhrasesBufferedReader = new BufferedReader(phraseReader);
            String StringlessonCapacity = WordsBufferedReader.readLine();
            if (StringlessonCapacity != null) {

                for (int j = 1; j <= lesson_index - 2; j++) {
                    // skip till lesson_index-1
                    if (StringlessonCapacity != null) {
                        int lessonCapacity = Integer.parseInt(StringlessonCapacity);
                        System.out.println("StringlessonCapacity: skipped" + StringlessonCapacity);

                        for (int i = 0; i < lessonCapacity; i++) {
                            String word_txt = WordsBufferedReader.readLine();
                            String phrase = PhrasesBufferedReader.readLine();
                        }
                        StringlessonCapacity = WordsBufferedReader.readLine();
                    }
                }
                for (int k = lessonNum; k <= lesson_index + 1; k++) {
                    if (k != 0 && StringlessonCapacity != null) {

                        int lessonCapacity = Integer.parseInt(StringlessonCapacity);
                        //  System.out.println("StringlessonCapacity: " + StringlessonCapacity);

                        Word[] lessonWords = new Word[lessonCapacity];
                        for (int i = 0; i < lessonCapacity; i++) {
                            String word_txt = WordsBufferedReader.readLine();
                            String phrase = PhrasesBufferedReader.readLine();
                            lessonWords[i] = form_word(word_txt, phrase);
                           // Log.i("setLessons: ","lessonWords[i]: " + i + " "+lessonWords[i]);

                        }
                        lessons.put(k, lessonWords);
                        StringlessonCapacity = WordsBufferedReader.readLine();
                    }
                }
            }

            wordsReader.close();
            // phrasesReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}