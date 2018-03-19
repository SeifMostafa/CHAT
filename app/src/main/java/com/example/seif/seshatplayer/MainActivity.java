package com.example.seif.seshatplayer;

import android.Manifest;
import android.annotation.SuppressLint;
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

import static com.example.seif.seshatplayer.layout.LessonFragment.LessonFragment_TAG;
import static java.lang.Thread.sleep;


public class MainActivity extends AppCompatActivity {


    public static final String WORDS_PREFS_NAME = "WordsPrefsFile", WordIndexKey = "i", WordKey = "w", PhraseKey = "p", LessonKey = "L", WordLoopKey = "wl";
    public static final String SFKEY = "0";
    private static final int PERMISSIONS_MULTIPLE_REQUEST = 122;
    public static String firstTimekey = "1stTime";

    SharedPreferences sharedPreferences_words = null;
    SharedPreferences.Editor sharedPreferences_words_editor = null;
    MediaPlayer mediaPlayer;

    private String WordsFilePath = "WORDS.txt", PhrasesFilePath = "PHRASES.txt", SF = "/SeShatSF/";
    private String FileWordsAchieved = "archive.txt";

    private Map<Integer, Word[]> lessons;
    private int word_index = 0;
    private int lesson_index = 1;
    private String firstPhrase = "أنا إسمي ";


    public static Direction[] getDirections(String filepath, int version) {
        Stack<Direction> directions = new Stack<>();
        File file = new File(filepath);

        try {
            int foundedversions = 0;
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                String line = null;
                line = scan.nextLine();
                switch (line.charAt(0)) {
                    case 'I':
                        foundedversions++;
                        break;
                    case 'E':
                        if (foundedversions > version) {
                            Direction[] result = new Direction[directions.size()];
                            return directions.toArray(result);
                        } else {
                            directions.clear();
                        }
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


    @SuppressLint({"CommitPrefEdits", "UseSparseArrays"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lessons = new HashMap<>();

        sharedPreferences_words = this.getSharedPreferences(WORDS_PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences_words_editor = sharedPreferences_words.edit();
        checkPermission_AndroidVersion();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    //Start app
    private void startApp() {
        if (Boolean.valueOf(sharedPreferences_words.getString(firstTimekey, "true"))) {
            lesson_index = 1;
            word_index = 0;
//setting file paths
            SF = Environment.getExternalStorageDirectory() + SF;

            WordsFilePath = SF + WordsFilePath;
            PhrasesFilePath = SF + PhrasesFilePath;
            FileWordsAchieved = SF + FileWordsAchieved;
            new File(FileWordsAchieved);
//setting lessons
            setLessons();
            Word phrase = new Word(firstPhrase + lessons.get(lesson_index)[0].getText());
            openLessonFragment(phrase);

//saving indexes on shared preferences
            SaveOnSharedPref(LessonKey, String.valueOf(lesson_index));
            SaveOnSharedPref(WordIndexKey, String.valueOf(word_index));
            SaveOnSharedPref(SFKEY, SF);
            SaveOnSharedPref(firstTimekey, String.valueOf(false));
            try {
                voiceoffer(null, getResources().getString(R.string.myname));
                sleep(1000);
                voiceoffer(null, lessons.get(lesson_index)[0].getText());
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //starting lesson
                        updateLesson(0);
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
            FileWordsAchieved = SF + FileWordsAchieved;
            setLessons();
            openLessonFragment(lesson_index);
        }
    }

    //create word
    private Word form_word(String txt, String phrase) {
        try {
            Word resultWord = new Word(txt, SF + txt + ".png", SF + txt, phrase);
            int version = 0;
            Direction[][] gvVersion = prepareWordGuidedVectors(txt, version);

            do {
                //  Log.i("MainActivity", "form_word: gvVersion.sz: " + gvVersion[0].length);
                resultWord.setFV(gvVersion);
                version++;
                gvVersion = prepareWordGuidedVectors(txt, version);

            } while (gvVersion[0].length > 0);
            Log.i("MainActivity", "form_word: version: " + version);
            return resultWord;
        } catch (Exception e) {
            Log.e("form_wordE:", e.toString());
            e.printStackTrace();
            return null;
        }
    }

    //creating Guided Vectors for words.
    private Direction[][] prepareWordGuidedVectors(String word, int version) throws FileNotFoundException {
        char charConnector = 'ـ';
        Direction[][] result_directions = new Direction[word.length()][];
        ArrayList<Character> differentchars = new ArrayList<>();
        Character[] charactersWithoutEndConnector = {'أ', 'إ', 'د', 'ذ', 'ر', 'ز', 'و', 'ؤ', 'ا', 'آ'};
        differentchars.addAll(Arrays.asList(charactersWithoutEndConnector));
        for (int i = 0; i < word.length(); i++) {
            Character character = word.charAt(i);

            if (i == 0) {
                if (differentchars.contains(character)) {
                    result_directions[i] = getDirections(SF + character, version);
                } else {
                    result_directions[i] = getDirections(SF + character + charConnector, version);
                }
            } else if (i == word.length() - 1) {
                if (differentchars.contains(word.charAt(i - 1))) {
                    result_directions[i] = getDirections(SF + character, version);
                } else {
                    result_directions[i] = getDirections(SF + charConnector + character, version);
                }
            } else {
                if (differentchars.contains(word.charAt(i - 1)) && differentchars.contains(character)) {
                    result_directions[i] = getDirections(SF + character, version);
                } else if (differentchars.contains(word.charAt(i - 1)) && !differentchars.contains(character)) {
                    result_directions[i] = getDirections(SF + character + charConnector, version);
                } else if (!differentchars.contains(word.charAt(i - 1)) && differentchars.contains(character)) {
                    result_directions[i] = getDirections(SF + charConnector + character, version);
                } else {
                    result_directions[i] = getDirections(SF + charConnector + character + charConnector, version);
                }
            }
        }
        return result_directions;
    }

    /*
    ToFlag: if 0 = current, if -1 = prev , if 1 = next; flag to obtain the ToFlag cretira
    else to navigate to ToFlag as lesson index
     */
    public void updateLesson(int ToFlag) {
        lesson_index = Integer.parseInt(sharedPreferences_words.getString(LessonKey, "1"));
        switch (ToFlag) {
            case 0:
                openLessonFragment(lesson_index);
                break;
            case -1:
                openLessonFragment(lesson_index - 1);
                break;
            case 1:
                openLessonFragment(lesson_index + 1);
                break;
        }
    }

    //play voice of any Audio file path
    public void voiceoffer(View view, String DataPath2Bplayed) {
        if (view != null) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            view.startAnimation(shake);
        }
        if (mediaPlayer != null) {

            if (mediaPlayer.isLooping() || mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
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

    // stopping the media player
    public void StopMediaPlayer() {
        Log.i("MainActivity", "StopMediaPlayer");
        if (mediaPlayer.isPlaying() || mediaPlayer.isLooping()) {
            mediaPlayer.stop();
        }
    }

    //displaying help Photo
    public void helpbypic(View view, String img2Bdisplayed) {
        if (view != null) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            view.startAnimation(shake);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.layout_sample_pic_help, null);
        ImageView imageView = dialogLayout.findViewById(R.id.picsample);
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

    //To save on SharedPreference
    public void SaveOnSharedPref(String key, String value) {
        sharedPreferences_words_editor.putString(key, value).apply();
        sharedPreferences_words_editor.commit();
    }

    //Check Android version
    private void checkPermission_AndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            // write your logic here if while testing under M.Devices
            // not granted!
        }
    }

    //Check Permission
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
                        v -> requestPermissions(
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO},
                                PERMISSIONS_MULTIPLE_REQUEST)).show();
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
                                v -> requestPermissions(
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET,
                                                Manifest.permission.RECORD_AUDIO},
                                        PERMISSIONS_MULTIPLE_REQUEST)).show();
                    }
                }
                break;
        }
    }

    //open Lesson Fragment by Lesson ID
    public void openLessonFragment(int i) {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LessonFragment lessonFragment = new LessonFragment();
        Bundle bundle = new Bundle();
        Word[] lesson = lessons.get(i);
        SaveOnSharedPref(LessonKey, String.valueOf(i));
        Log.i("MainActivity", "openLessonFragment: am here with i(lesson_index)= " + i);
        bundle.putParcelableArray(LessonKey, lesson);
        bundle.putInt(WordIndexKey, 0);
        lessonFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_replacement, lessonFragment, LessonFragment_TAG);
        fragmentTransaction.addToBackStack(LessonFragment_TAG);
        fragmentTransaction.commit();

        Log.i("MainActivity", "openLessonFragment:: lesson_index" + lesson_index);
    }

    //open Lesson Fragment for just one word -- using in animation fragment
    public void openLessonFragment(Word word) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LessonFragment lessonFragment = new LessonFragment();
        Bundle bundle = new Bundle();
        word = form_word(word.getText(), word.getPhrase());
        bundle.putParcelable(WordKey, word);
        bundle.putInt(WordIndexKey, 0);
        lessonFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_replacement, lessonFragment, LessonFragment_TAG);
        fragmentTransaction.addToBackStack(LessonFragment_TAG);
        fragmentTransaction.commit();
        Log.i("MainActivity", "openLessonFragment:: lesson_index" + lesson_index);
    }

    //Return back to the latest state of Lesson fragment
    public void backToLessonFragment() {
        Log.i("MainActivity", "backToLessonFragment :: am here!");

        FragmentManager fragmentManager = getFragmentManager();
        LessonFragment lessonFragment = (LessonFragment) fragmentManager.findFragmentByTag(LessonFragment_TAG);
        if (lessonFragment != null) {
            Log.i("MainActivity", "backToLessonFragment != null");

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_replacement, lessonFragment, LessonFragment_TAG);
            fragmentTransaction.addToBackStack(LessonFragment_TAG);
            fragmentTransaction.commit();
        } else {
            Log.i("MainActivity", "lessonFragment = null");
        }
    }

    //open phrase fragment to pick the word learned
    public void openPhraseFragment(String phrase, String word) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PhrasePickFragment phrasePickFragment = new PhrasePickFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PhraseKey, phrase);
        bundle.putString(WordKey, word);
        phrasePickFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_replacement, phrasePickFragment);
        //fragmentTransaction.addToBackStack(PhrasePickFragment_TAG);
        fragmentTransaction.commit();
    }

    //open animation fragment to analyse phrases' words and words' chars
    public void openAnimationFragment(String word) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AnimationFragment animationFragment = new AnimationFragment();
        Bundle bundle = new Bundle();
        bundle.putString(WordKey, word);
        animationFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_replacement, animationFragment);
        //fragmentTransaction.addToBackStack(LessonFragment_TAG);
        fragmentTransaction.commit();
    }

    //open Help Fragment
    public void OpenHelpFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HelpFragment helpFragment = new HelpFragment();
        Bundle bundle = new Bundle();
        word_index = Integer.parseInt(sharedPreferences_words.getString(WordIndexKey, "0"));
        bundle.putInt(WordIndexKey, word_index);
        bundle.putParcelableArray(LessonKey, lessons.get(1));
        helpFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_replacement, helpFragment);
        // fragmentTransaction.addToBackStack(LessonFragment_TAG);
        fragmentTransaction.commit();
    }

    //putting finished words into archive
    public void assignWordAsFinished(String Word) {
        if (!readArchiveWords().contains(Word)) {
            try {
                FileWriter writer = new FileWriter(FileWordsAchieved, true);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write(Word + "\n");
                bufferedWriter.close();
                Log.i("MainActivity", "assignWordAsFinished: " + Word);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //reading from archive file finished words
    public ArrayList<String> readArchiveWords() {
        ArrayList<String> words = new ArrayList<>();
        try {
            FileReader reader = new FileReader(FileWordsAchieved);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                Log.i("MainActivity", "readArchiveWords: " + line);
                words.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    //reading phrases and words from WORDS.txt and store it into lessons
    @SuppressLint("UseSparseArrays")
    private void setLessons() {
        // int lessonNum = lesson_index - 1;
        try {
            FileReader wordsReader = new FileReader(WordsFilePath);
            FileReader phraseReader = new FileReader(PhrasesFilePath);
            BufferedReader WordsBufferedReader = new BufferedReader(wordsReader);
            BufferedReader PhrasesBufferedReader = new BufferedReader(phraseReader);
            String StringlessonCapacity = WordsBufferedReader.readLine();
            if (StringlessonCapacity != null) {
                int k = 1;
                while (true) {
                    int lessonCapacity = Integer.parseInt(StringlessonCapacity);
                    Word[] lessonWords = new Word[lessonCapacity];
                    for (int i = 0; i < lessonCapacity; i++) {
                        String word_txt = WordsBufferedReader.readLine();
                        String phrase = PhrasesBufferedReader.readLine();
                        lessonWords[i] = form_word(word_txt, phrase);
                    }
                    lessons.put(k, lessonWords);
                    StringlessonCapacity = WordsBufferedReader.readLine();
                    k++;
                    if (StringlessonCapacity == null)
                        break;
                }
            }
            wordsReader.close();
            // phrasesReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*    @SuppressLint("UseSparseArrays")
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
    }*/

}