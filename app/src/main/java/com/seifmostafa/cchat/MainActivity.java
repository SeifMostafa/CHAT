package com.seifmostafa.cchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.seifmostafa.cchat.Model.Word;
import com.seifmostafa.cchat.Recognizers.FaceDetectionActivity;
import com.seifmostafa.cchat.Recognizers.VoiceRecognitionActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Stack;

import static com.seifmostafa.cchat.Utils.FormSexyMat;
import static com.seifmostafa.cchat.Utils.RGBtoBINARY;
import static com.seifmostafa.cchat.Utils.SaveOnSharedPref;
import static com.seifmostafa.cchat.Utils.getBitmapFromMat;
import static com.seifmostafa.cchat.Utils.getBitmapFromView;
import static com.seifmostafa.cchat.Utils.mCascadeFile;
import static com.seifmostafa.cchat.Utils.mCascadeFileEye;
import static com.seifmostafa.cchat.Utils.mDetectorName;
import static com.seifmostafa.cchat.Utils.mJavaDetector;
import static com.seifmostafa.cchat.Utils.mJavaDetectorEye;

public class MainActivity extends Activity {

    public static final int JAVA_DETECTOR = 0;
    private static final String TAG = "CCHAT::MainActivity";
    private static final int RESULT_SPEECH = 1;
    public static String currentText;
    public static int wordloop,level,wordindex;

    private Mat imageMat;
    private ArrayList<String> SpeechRec_results;
    private FrameLayout frameLayout;

    public SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    public static TextView textViewPercentage;
    public static  CustTextView textView;
    public static Mat TextasMat;
    public static Stack<Word> Words;

    public static Checker checker;
    ImageView imageView;
    static {
        OpenCVLoader.initDebug();
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {

                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    imageMat = new Mat();
                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        // load cascade file from application resources
                        InputStream ise = getResources().openRawResource(R.raw.haarcascade_lefteye_2splits);
                        File cascadeDirEye = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFileEye = new File(cascadeDirEye, "haarcascade_lefteye_2splits.xml");
                        FileOutputStream ose = new FileOutputStream(mCascadeFileEye);

                        while ((bytesRead = ise.read(buffer)) != -1) {
                            ose.write(buffer, 0, bytesRead);
                        }
                        ise.close();
                        ose.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        mJavaDetectorEye = new CascadeClassifier(mCascadeFileEye.getAbsolutePath());
                        if (mJavaDetectorEye.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier for eye");
                            mJavaDetectorEye = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFileEye.getAbsolutePath());

                        cascadeDir.delete();
                        cascadeDirEye.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public MainActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";

        Log.i(TAG, "Instantiated new " + this.getClass());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        sharedPreferences = getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        checker = new Checker(MainActivity.this);

        checksharedpref();
        ShowBoardOverText();

    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initviews();


    }
    private void ShowBoardOverText(){
        frameLayout = (FrameLayout) findViewById(R.id.overtext);
        frameLayout.addView(checker.getDrawingView());
    }


    public static void updateWord(){
        if(Words.size()>0) textView.setText(Words.pop().getWord());
        else Log.i("Level"," is completed!");
    }

    private void initviews(){
        textViewPercentage = (TextView) this.findViewById(R.id.textView_percentage);
        textView = (CustTextView) findViewById(R.id.textView_text);
        textView.post( new Runnable() {
            @Override
            public void run() {
                TextasMat = RGBtoBINARY(getBitmapFromView(textView));
            }
        });

        //textView.setText(currentText);
     //   imageView = (ImageView)findViewById(R.id.imageview_textpoints);
        // TextasMat sexy ,,
//        imageView.setImageBitmap(getBitmapFromMat(FormSexyMat(wordloop+1,TextasMat)));
                //textView.setText(currentText);
                imageView = (ImageView)findViewById(R.id.imageview_textpoints);
                // TextasMat sexy ,,
                imageView.setImageBitmap(getBitmapFromMat(FormSexyMat(wordloop+1,TextasMat)));
                textView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
            }

    private void checksharedpref(){

        if(sharedPreferences.getString("firsttime","").equals("")){
            try {

                // call loader
                // save words stack in sharedpref
                SaveOnSharedPref(this.editor,"firsttime","no");
            } catch (Exception e){
                Log.i("Set firsttime",e.toString());
            }
        }else {
            // load stack words from sharedpref
            Log.i("Level:",sharedPreferences.getString("level",""));
            this.level=Integer.parseInt(sharedPreferences.getString("level",""));
        }

        if(sharedPreferences.getString("level","").equals("")){
            try {
                SaveOnSharedPref(this.editor,"level","0");
            } catch (Exception e){
                Log.i("Set level",e.toString());
            }
        }else {
            Log.i("Level:",sharedPreferences.getString("level",""));
            this.level=Integer.parseInt(sharedPreferences.getString("level",""));
        }

        if(sharedPreferences.getString("wordindex","").equals("")){
            try {
                SaveOnSharedPref(this.editor,"wordindex","0");
            } catch (Exception e){
                Log.i("Set wordindex",e.toString());
            }
        }else {
            Log.i("wordindex: ",sharedPreferences.getString("wordindex",""));
            this.wordindex= Integer.parseInt(sharedPreferences.getString("wordindex",""));
        }

        if(sharedPreferences.getString("wordloop","").equals("")){
            try {
                SaveOnSharedPref(this.editor,"wordloop","0");
            } catch (Exception e){
                Log.i("Set wordloop",e.toString());
            }
        }else{
            Log.i("wordloop: ",sharedPreferences.getString("wordloop",""));
            this.wordloop= Integer.parseInt(sharedPreferences.getString("wordloop",""));
        }
    }

    public void checkfill(View view) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        view.startAnimation(shake);
       /* Bitmap mBitmap = null;
        mBitmap = Bitmap.createBitmap(dv.getWidth(), dv.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(mBitmap);
        dv.draw(canvas);

        try {
            Original = new CenterLineForOriginal().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getBitmapFromView(textView)).get();
            Mat Drawed = new CenterLine().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mBitmap).get();
            double FinalPercentage = MatchWritten(Original, Drawed, TOLERANCE);
            textViewPercentage.setText("" + Math.floor(FinalPercentage + '%'));
            textViewPercentage.setVisibility(View.VISIBLE);
            Log.i("FinalPercentage", "" + FinalPercentage);
        } catch (Exception e) {
            Log.v("checkfill", e.toString());
        }
*/
    }

    public void voicerec(View view) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        view.startAnimation(shake);
//        Intent voicerecogize = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        voicerecogize.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
//        voicerecogize.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        voicerecogize.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-EG");
//        voicerecogize.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,false);
//        startActivityForResult(voicerecogize, RESULT_SPEECH);

        VoiceRecOffline();
    }

    public void voiceoffer(View view) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        view.startAnimation(shake);

        final MediaPlayer mp = new MediaPlayer();
        if (mp.isPlaying()) {
            mp.stop();
        }
        try {
            mp.setDataSource("/storage/emulated/0/CCHAT/helpVoice/ورد.wav");
            mp.prepare();
            mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void helpbypic(View view) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        view.startAnimation(shake);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("شكرا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.layout_sample_pic_help, null);
        ImageView imageView = (ImageView) dialogLayout.findViewById(R.id.picsample);
        File imgFile = new  File("/storage/emulated/0/CCHAT/helpPhoto/ورد.jpg");
        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_SPEECH && requestCode == RESULT_OK) ;
        {
            SpeechRec_results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void VoiceRecOffline() {
        startActivity(new Intent(MainActivity.this, VoiceRecognitionActivity.class));
    }

    public void checkIfSpeechRec_resultsContainsTheWord(String word, ArrayList<String> SpeechRec_results) {
        if (SpeechRec_results.size() > 0) {
            for (String i : SpeechRec_results) {
                if (i == word) {
                    Toast.makeText(this, "Well Done!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("SpeechResults", i);
            }

        }
        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();

    }

    public void faceRec() {
         startActivity(new Intent(MainActivity.this,FaceDetectionActivity.class));
    }

    public void retry(View view) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        view.startAnimation(shake);

//        textView.animate();
//        String s = "سيف";
//        textView.setText(s);
       // dv.reset();
    }


}