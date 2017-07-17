package com.seifmostafa.cchat.Recognizers;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.seifmostafa.cchat.MainActivity;
import com.seifmostafa.cchat.R;

import java.io.File;

import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;


public class VoiceRecognitionActivity extends Activity implements RecognitionListener {
    public static final String SEARCH_KEYWORD = "x";

    //Handler a = new Handler();
    static {
        System.loadLibrary("pocketsphinx_jni");
    }

    SpeechRecognizer recognizer;
    TextView recognizer_state, recognized_word,textView_required;
        Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_voice_recognition);
        setupRecognizer();
        recognizer_state = (TextView) findViewById(R.id.rec_state);
        textView_required = (TextView)findViewById(R.id.textView_req) ;
        recognized_word = (TextView) findViewById(R.id.rec_result);
        start = (Button) findViewById(R.id.button);
        textView_required.setText(MainActivity.currentText);
        start.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        recognizer_state.setText("...");
                        Log.i("startbtn", "start Recording");
                        recognizer.startListening(SEARCH_KEYWORD);
                        break;
                    case MotionEvent.ACTION_UP:
                        recognizer_state.setText(" ");
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
//                if(comando.contains(MainActivity.currentText))
//                {
//                Log.i("Result", comando);
//                    Toast.makeText(VoiceRecognitionActivity.this,"جيد جدا",Toast.LENGTH_LONG).show();
//                    startActivity(new Intent(VoiceRecognitionActivity.this,MainActivity.class));
//                    finish();
//                }
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

                if(comando.contains(MainActivity.currentText))
                {
                    Log.i("Result", comando);
                    Log.i("ResultCurrent",MainActivity.currentText);
                    Toast.makeText(VoiceRecognitionActivity.this,"جيد جدا",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(VoiceRecognitionActivity.this,MainActivity.class));
                    recognizer.cancel();
                    finish();
                }
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
