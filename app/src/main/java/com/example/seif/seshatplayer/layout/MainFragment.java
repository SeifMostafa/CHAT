package com.example.seif.seshatplayer.layout;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class MainFragment extends Fragment {

    public static final int RESULT_SPEECH = 177,WAIT2SayInstructions = 1500;
    ImageButton helpiBtn, PreviBtn, NextiBtn, PlaySoundiBtn, DisplayImageiBtn;
    private Word word;
    private Paint mPaint;
    DrawView drawView_MainText = null;
    private boolean Pronounced = false;
    private int PronouncedCounter = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.word = getArguments().getParcelable(MainActivity.WordKey);
        }
        // drawView_MainText = new DrawView(getActivity());
        Log.i("WORD_INFO:", "" + this.word.getTriggerpoints().length);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        TextView custTextView = (TextView) view.findViewById(R.id.textView_maintext);
        custTextView.setText(word.getText());

        helpiBtn = (ImageButton) ((MainActivity)getActivity()).findViewById(R.id.imagebutton_moreInfo);
        helpiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                HelpFragment helpFragment = new HelpFragment();
                fragmentTransaction.replace(R.id.fragment_replacement,helpFragment);
            }
        });

        PreviBtn = (ImageButton) view.findViewById(R.id.imagebutton_skipword);
        PreviBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // request prev word
            }
        });
        NextiBtn = (ImageButton) view.findViewById(R.id.imagebutton_skipword);
        NextiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // request nxt word
            }
        });
        PlaySoundiBtn = (ImageButton) view.findViewById(R.id.imagebutton_soundhelp);
        PlaySoundiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((MainActivity) getActivity()).voiceoffer(PlaySoundiBtn, word.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("PlaySoundiBtn", e.toString());
                }
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
        drawView_MainText = (DrawView) view.findViewById(R.id.textView_maintext);
        drawView_MainText.setVisibility(View.VISIBLE);
        drawView_MainText.SetTriggerPoints(this.word.getTriggerpoints());

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!Pronounced) {
            final String s = this.word.getText();

            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        Log.i("XX","XX");
                        Thread.sleep(WAIT2SayInstructions);
                    } catch (InterruptedException e) {
                    }
                    ((MainActivity) getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ((MainActivity) getActivity()).voiceoffer(null, s);
                                Thread.sleep(3000);
                                ((MainActivity) getActivity()).voiceoffer(drawView_MainText, R.raw.speakinstruction);
                                Thread.sleep(3000);
                                voicerec(null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
            thread.start(); //start the thread
        }
    }
    private void voicerec(View view) {
        if (view != null) {
            Animation shake = AnimationUtils.loadAnimation(((MainActivity)getActivity()), R.anim.shake);
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
}
