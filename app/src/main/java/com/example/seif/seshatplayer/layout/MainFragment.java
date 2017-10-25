package com.example.seif.seshatplayer.layout;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.seif.seshatplayer.DrawView;
import com.example.seif.seshatplayer.MainActivity;
import com.example.seif.seshatplayer.R;
import com.example.seif.seshatplayer.model.Word;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static java.lang.Thread.sleep;



public class MainFragment extends Fragment {

    public static final int RESULT_SPEECH = 177, WAIT2SayInstructions = 1500;
    ImageButton helpiBtn, PreviBtn, NextiBtn, PlaySoundiBtn, DisplayImageiBtn;
    DrawView drawView_MainText = null;
    private Word[] words;
    private Word word = null;
    private int CurrentWordsArrayIndex = 0;
    private boolean Pronounced = false;
    private int PronouncedCounter = 0;
    Thread Thread_WordTrip = null;
    private boolean justWord = false;

    private int DEFAULT_LOOP_COUNTER = 4;
    private int DEFAULT_TYPEFACE_LEVELS = 4;
    private int word_loop =1;
    private int localWordIndx =0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.words = (Word[]) getArguments().getParcelableArray(MainActivity.LessonKey);

            if (this.words != null) {
                this.word = words[0];
                Log.i("onCreate", "from MainFragment" + "words not null");

            } else {
                this.word = (Word) getArguments().getParcelable(MainActivity.WordKey);
                justWord = true;
            }
        }
        Log.i("onCreate", "from MainFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        drawView_MainText = (DrawView) view.findViewById(R.id.textView_maintext);
        drawView_MainText.setVisibility(View.VISIBLE);

        if (!word.getFV().equals(null)) {

            drawView_MainText.SetGuidedVector(word.getFV()[0]);
        }


        TextView custTextView = (TextView) view.findViewById(R.id.textView_maintext);
        custTextView.setText(word.getText());

        helpiBtn = (ImageButton) ((MainActivity) getActivity()).findViewById(R.id.imagebutton_moreInfo);
        helpiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("helpiBtn", "is clicked!");
                try {
                    if (Thread_WordTrip != null) {
                        if (Thread_WordTrip.isAlive()) {
                            Thread_WordTrip.interrupt();
                            Log.i("helpiBtn", "is clicked!" + "Thread_WordTrip.is alive");
                        }
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                ((MainActivity) getActivity()).OpenHelpFragment();
            }
        });

        PreviBtn = (ImageButton) view.findViewById(R.id.imagebutton_prevword);
        PreviBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // request prev word
                if (Thread_WordTrip != null) Thread_WordTrip.interrupt();

                word = words[--CurrentWordsArrayIndex];
                custTextView.setText(word.getText());
                drawView_MainText.SetGuidedVector(word.getFV()[0]);
                setPreviBtnVisibilty();
              //  CreateWordTripThread().start();

            }
        });


        NextiBtn = (ImageButton) view.findViewById(R.id.imagebutton_skipword);
        NextiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // request nxt word
                if (Thread_WordTrip != null) Thread_WordTrip.interrupt();

                word = words[++CurrentWordsArrayIndex];
                custTextView.setText(word.getText());
                drawView_MainText.SetGuidedVector(word.getFV()[0]);
                setNextiBtnVisibility();
                //CreateWordTripThread().start();
            }
        });


        PlaySoundiBtn = (ImageButton) view.findViewById(R.id.imagebutton_soundhelp);
        PlaySoundiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  try {
                    ((MainActivity) getActivity()).voiceoffer(PlaySoundiBtn, word.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("PlaySoundiBtn", e.toString());
                }*/
                if (Thread_WordTrip != null) Thread_WordTrip.interrupt();

                word = words[++CurrentWordsArrayIndex];
                custTextView.setText(word.getText());
               drawView_MainText.SetGuidedVector(word.getFV()[0]);
                setNextiBtnVisibility();
                //CreateWordTripThread().start();

            }
        });
        DisplayImageiBtn = (ImageButton) view.findViewById(R.id.imagebutton_photohelp);
        DisplayImageiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((MainActivity) getActivity()).helpbypic(DisplayImageiBtn, word.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("DisplayImageiBtn", e.toString());
                }
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();

        if (!Pronounced) {
            final String s = this.word.getText();
            //CreateWordTripThread().start(); //start the thread
        }

        if (justWord) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        sleep(2000);
                        //((MainActivity) getActivity()).voiceoffer(null, MainActivity.firstPhraseAudioPath);
                        sleep(1100);
                        if(word.getText().length()>1){
//                            ((MainActivity) getActivity()).voiceoffer(null, word.getText().split(" ")[2]);
                        }else{
                        //    ((MainActivity) getActivity()).voiceoffer(null, word.getText());
                        }
                        sleep(1900);
                        //((MainActivity) getActivity()).updatelesson(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("PlaySoundiBtn", e.toString());
                    }
                }
            }).start();
        }
    }

    private void setNextiBtnVisibility() {
        if (CurrentWordsArrayIndex == words.length - 1) {
            NextiBtn.setVisibility(View.INVISIBLE);
        } else {
            NextiBtn.setVisibility(View.VISIBLE);
        }
    }

    private void setPreviBtnVisibilty() {
        if (CurrentWordsArrayIndex == 0) {
            PreviBtn.setVisibility(View.INVISIBLE);
        } else {
            PreviBtn.setVisibility(View.VISIBLE);
        }
    }

    private Thread CreateWordTripThread() {
        Thread_WordTrip = new Thread() {
            @Override
            public void run() {
                try {
                    Log.i("XX", "XX");
                    sleep(WAIT2SayInstructions);
                } catch (InterruptedException e) {
                }
                ((MainActivity) getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ((MainActivity) getActivity()).voiceoffer(null, word.getText());
                            sleep(3000);
                            ((MainActivity) getActivity()).voiceoffer(drawView_MainText, "speakinstruction.wav");
                            sleep(3000);
                            voicerec(null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void interrupt() {
                super.interrupt();
                ((MainActivity) getActivity()).StopMediaPlayer();
                onDetach();
            }

        };
        return Thread_WordTrip;
    }

    private void voicerec(View view) {
        if (view != null) {
            Animation shake = AnimationUtils.loadAnimation(((MainActivity) getActivity()), R.anim.shake);
            view.startAnimation(shake);
        }
        Intent voicerecogize = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voicerecogize.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        voicerecogize.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voicerecogize.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-EG");
        voicerecogize.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false);
        startActivityForResult(voicerecogize, RESULT_SPEECH);

    }
    public Typeface updateWordLoop() {
        Typeface tf = null;

        if (word_loop < (DEFAULT_LOOP_COUNTER * DEFAULT_TYPEFACE_LEVELS)) {
            if (word_loop % DEFAULT_LOOP_COUNTER == 0) {
                // change font
                if (word_loop >= 0 && word_loop <= DEFAULT_LOOP_COUNTER) {
                    tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lvl1.ttf");
                } else if (word_loop > DEFAULT_LOOP_COUNTER && word_loop <= DEFAULT_LOOP_COUNTER * 2) {
                    tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lvl2.ttf");
                } else if (word_loop > DEFAULT_LOOP_COUNTER * 2 && word_loop <= DEFAULT_LOOP_COUNTER * 3) {
                    tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lvl3.ttf");
                }
            }
            word_loop++;
        } else {
            // change word
            word_loop = 1;
            localWordIndx++;
            //
        }
        return tf;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> SpeechRec_results = null;
        if (requestCode == RESULT_SPEECH && requestCode == RESULT_OK) ;
        {
            Log.i("onActivityResult", "XXX");
            if (data != null) {
                SpeechRec_results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (SpeechRec_results != null) {
                    if (SpeechRec_results.get(0).equals(this.word.getText())) {
                        Log.i("onActivityResult", SpeechRec_results.get(0));
                        Pronounced = true;
                        PronouncedCounter++;
                    }
                }
            }

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("MainFragment", "onStop");
        if (Thread_WordTrip != null) {
            Thread_WordTrip.interrupt();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("MainFragment", "onDetach");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
