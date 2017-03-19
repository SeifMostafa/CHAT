package com.seifmostafa.cchat;/*
 * PocketSphinx Continue Recognition Demo
 * Developed by Luis G III
 * e-mail: loiis.x14@gmail.com
 * visit: http://hellospoonpr@gmail.com and get your own HelloSpoon robot!
 * */

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;


public class VrActivity extends Activity implements RecognitionListener {
    public static final String SEARCH_KEYWORD = "x";

    //Handler a = new Handler();
    static {
        System.loadLibrary("pocketsphinx_jni");
    }

    SpeechRecognizer recognizer;
    TextView recognizer_state, recognized_word;
    Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_voice_recognition);
        setupRecognizer();
        recognizer_state = (TextView) findViewById(R.id.textView2);
        recognized_word = (TextView) findViewById(R.id.textView3);
        start = (Button) findViewById(R.id.button);
        start.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        recognizer_state.setText("Recognition Started!");
                        Log.i("startbtn", "start Recording");
                        recognizer.startListening(SEARCH_KEYWORD);
                        break;
                    case MotionEvent.ACTION_UP:
                        recognizer_state.setText("Recognition Stopped!");
                        Log.i("startbtn", "stop Recording");
                        recognizer.stop();

                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recognizer != null) recognizer.removeListener(this);
    }


    @Override
    public void onBeginningOfSpeech() {
        // TODO Auto-generated method stub
        Log.i("onBeginningOfSpeech", "Hello");
    }

    @Override
    public void onEndOfSpeech() {
        // TODO Auto-generated method stub
        Log.i("onEndOfSpeech", "Hello");

    }

    private void setupRecognizer() {
        File modelsDir = new File("/storage/emulated/0/CCHAT/MODEL");

        recognizer = defaultSetup()
                .setAcousticModel(new File(modelsDir, "model"))
                .setDictionary(new File(modelsDir, "dict/an4.dic"))
                .setRawLogDir(new File(modelsDir, "logdir"))
                .setKeywordThreshold(1e-40f)
                .getRecognizer();
        recognizer.addListener(this);


        File digitsGrammar = new File(modelsDir, "grammar/grammar.gram");
        recognizer.addKeywordSearch(SEARCH_KEYWORD, digitsGrammar);

    }

    @Override
    public void onPartialResult(Hypothesis arg0) {
        String comando = null;
        if (arg0 != null) {
            if (arg0.getHypstr() != null) {
                comando = arg0.getHypstr();
                Log.i("Result", comando);
                recognized_word.setText(comando);

            }
        }
    }

    @Override
    public void onResult(Hypothesis hup) {
        String comando = null;
        if (hup != null) {
            if (hup.getHypstr() != null) {
                comando = hup.getHypstr();
                Log.i("Result", comando);
                recognized_word.setText(comando);
            }
        }

    }

//	public void Timer(){
//		 new Thread(new Runnable() {
//		        @Override
//		        public void run() {
//		                try {
//		                    Thread.sleep(500);
//		                    a.post(new Runnable() {
//		                        @Override
//		                        public void run() {
//
//		                        }
//		                    });
//		                } catch (Exception e) {
//		                }
//		            }
//
//		    }).start();
//	}


}
