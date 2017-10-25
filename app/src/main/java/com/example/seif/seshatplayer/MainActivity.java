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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;


public class MainActivity extends AppCompatActivity {


    public static final String AnimationKey = "AK", WORDS_PREFS_NAME = "WordsPrefsFile", WordIndexKey = "i", WordKey = "w", PhraseKey = "p", LessonKey = "L";
    public static final String SFKEY = "SF";
    private static final int PERMISSIONS_MULTIPLE_REQUEST = 122;

    SharedPreferences sharedPreferences_words = null;
    SharedPreferences.Editor sharedPreferences_words_editor = null;
    MediaPlayer mediaPlayer = null;

    private String WordsFilePath = "WORDS.txt", PhrasesFilePath = "PHRASES.txt", SF = "/SeShatSF/";
    private String FileWordsAchieved = "Archive.txt";

    private Map<Integer, Word[]>lessons;
    private int  word_index = 0;
    private int  lesson_index = 0;

    private String firstPhrase = "أنا إسمي ";
    private String firstTimekey = "1stTime";

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
            lesson_index = 0;
            setLessons(lesson_index);
            word_index = 0;

            SF = Environment.getExternalStorageDirectory() + SF;
            File file = new File(SF);
            String[] directories = file.list(new FilenameFilter() {
                @Override
                public boolean accept(File current, String name) {
                    return new File(current, name).isDirectory();
                }
            });

            SF = SF + directories[0] + "/";
            WordsFilePath = SF + WordsFilePath;
            PhrasesFilePath = SF + PhrasesFilePath;
            new File(Environment.getExternalStorageDirectory(), FileWordsAchieved);
            Word phrase = new Word(firstPhrase + directories[0]);
            OpenMainFragment(phrase);

            SaveOnSharedPref(LessonKey, String.valueOf(lesson_index));
            SaveOnSharedPref(WordIndexKey, String.valueOf(word_index));
            SaveOnSharedPref(SFKEY, SF);
            SaveOnSharedPref(firstTimekey, String.valueOf(false));

        } else {

            lesson_index = Integer.parseInt(sharedPreferences_words.getString(LessonKey, "0"));
            setLessons(lesson_index);

            word_index = Integer.parseInt(sharedPreferences_words.getString(WordIndexKey, "0"));
            SF = sharedPreferences_words.getString(SFKEY,SF);
            WordsFilePath = SF + WordsFilePath;
            PhrasesFilePath = SF + PhrasesFilePath;

           /* Word word = new Word("س");
            Direction[][] word_directions = new Direction[1][];
            word_directions[0] = getDirections(SF + "س" + 2);
            word.setFV(word_directions);*/
            //OpenMainFragment(word);
            OpenMainFragment(lesson_index);
        }
    }





    private Word form_word(String txt, String phrase) {
        try {
            Word resultWord = new Word(txt, SF + txt + ".png", SF + txt + ".mp3", phrase);
            resultWord.setFV(prepareWordGuidedVectors(txt));
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
        Character[] characters = {'أ', 'إ', 'د', 'ذ', 'ر', 'ز', 'و', 'ؤ', 'ا'};
        differentchars.addAll(Arrays.asList(characters));
        for (int i = 0; i < word.length(); i++) {
            if (i == 0) {
                result_directions[i] = getDirections(SF + word.charAt(i) + 1 );
            } else if (i == word.length() - 1) {
                if (differentchars.contains(word.charAt(i - 1))) {
                    result_directions[i] = getDirections(SF + word.charAt(i) + 2 );

                } else {
                    result_directions[i] = getDirections(SF + word.charAt(i) + 0 );
                }
            } else {
                if (differentchars.contains(word.charAt(i - 1))) {
                    result_directions[i] = getDirections(SF + word.charAt(i) + 2 );

                } else {
                    result_directions[i] = getDirections(SF + word.charAt(i) + 3 );
                }
            }
        }
        return result_directions;
    }

    /*
    ToFlag: if 0 = current, if -1 = prev;
     */
    public void updatelesson(int ToFlag,boolean flag) {
        if(flag) {
            switch (ToFlag) {
                case 0:
                    OpenMainFragment(lesson_index);
                    break;
                case -1:
                    OpenMainFragment(--lesson_index);
                    break;
                case 1:
                    OpenMainFragment(++lesson_index);
                    break;
            }
        }else{
            OpenMainFragment(ToFlag);
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


    private void OpenMainFragment(int i) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        Word[] lesson = lessons.get(lesson_index);
        bundle.putParcelableArray(LessonKey, lesson);
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

    private   void setLessons(int lesson_index) {
        lessons = new HashMap<>();
        int lessonIndex = 1;

        try {
            FileReader wordsReader = new FileReader(WordsFilePath);
            FileReader phrasesReader = new FileReader(PhrasesFilePath);

            BufferedReader PhrasesBufferedReader = new BufferedReader(phrasesReader);
            BufferedReader WordsBufferedReader = new BufferedReader(wordsReader);
            String StringlessonCapacity = WordsBufferedReader.readLine();
            while (StringlessonCapacity != null) {
                int lessonNum = lessonIndex++;
                int lessonCapacity = Integer.parseInt(StringlessonCapacity);
                Word[] lessonWords = new Word[lessonCapacity];

                for (int i = 0; i < lessonCapacity; i++) {
                    String word_txt = WordsBufferedReader.readLine();
                    String phrase = PhrasesBufferedReader.readLine();
                    lessonWords[i] =   form_word(word_txt,phrase);
                }
                lessons.put(lessonNum, lessonWords);
                StringlessonCapacity = WordsBufferedReader.readLine();
            }
                wordsReader.close();
                phrasesReader.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }