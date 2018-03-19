package com.example.seif.seshatplayer.layout;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.seif.seshatplayer.MainActivity;
import com.example.seif.seshatplayer.R;
import com.example.seif.seshatplayer.UpdateWord;
import com.example.seif.seshatplayer.WordView;
import com.example.seif.seshatplayer.model.Word;

public class LessonFragment extends Fragment implements UpdateWord {

    public static final int RESULT_SPEECH = 177, WAIT2SayInstructions = 1000;
    public static int DEFAULT_LOOP_COUNTER = 4;
    public static int DEFAULT_TYPEFACE_LEVELS = 4;
    public static String LessonFragment_TAG = "LessonFragment";
    public static boolean phraseIsAnimated = false;
    public static boolean wordIsAnimated = false;
    public static boolean isPicked = false;
    ImageButton helpiBtn, PreviBtn, NextiBtn, PlaySoundiBtn, DisplayImageiBtn;
    WordView wordView_MainText = null;
    Thread Thread_WordJourney = null;
    LessonFragment instance;
    private Boolean isPronunced = false;
    private Boolean isWritten = false;
    private Word[] words;
    private Word word = null;
    private int CurrentWordsArrayIndex = 0;
    private Boolean firstTime = false;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //getting lesson words when sending lesson key through openLessonFragment(int i)
            words = (Word[]) getArguments().getParcelableArray(MainActivity.LessonKey);//will be null when calling animation

            //getting text when sending word text through openLessonFragment(String word)
            word = getArguments().getParcelable(MainActivity.WordKey);//will be not null when calling animation
            //getting boolean to check if first time or not
            firstTime = getArguments().getBoolean(MainActivity.firstTimekey);
            //getting word index (till now always 0 for each lesson )
            CurrentWordsArrayIndex = getArguments().getInt(MainActivity.WordIndexKey);

            if (word == null && words != null) {
                //filling when calling by openLessonFragment(int i)
                word = words[CurrentWordsArrayIndex];
                Log.i("onCreate", "from LessonFragment" + "word == null");
            }
        }

        instance = this;
        mContext = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        wordView_MainText = view.findViewById(R.id.textView_maintext);
        wordView_MainText.setText(word.getText());
        wordView_MainText.setmLessonFragment(this);

        if (word.getFV() != null) {

            //setting word guided vector in the wordView to help you check writing
            wordView_MainText.setGuidedVector(word.getFV());
            Log.d("LessonFragment", "FV = " + word.getFV());
        } else if (!firstTime) {
            //setting word from lessons' words
            word = words[CurrentWordsArrayIndex];
            //setting word guided vector in the wordView to help you check writing
            wordView_MainText.setGuidedVector(word.getFV());
            //putting text into wordView
            wordView_MainText.setText(word.getText());
            Log.d("LessonFragment", "FV = " + word.getFV());

        }

        helpiBtn = getActivity().findViewById(R.id.imagebutton_moreInfo);
        helpiBtn.setOnClickListener(view15 -> {
            Log.i("helpiBtn", "is clicked!");
            try {
                if (Thread_WordJourney != null) {
                    if (Thread_WordJourney.isAlive()) {
                        Thread_WordJourney.interrupt();
                        Log.i("helpiBtn", "is clicked!" + "Thread_WordJourney.is alive");
                    }
                }

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            ((MainActivity) getActivity()).OpenHelpFragment();

            wordIsAnimated = false;
            phraseIsAnimated = false;
        });

        PreviBtn = view.findViewById(R.id.imagebutton_prevword);
        PreviBtn.setOnClickListener(view13 -> {
            // request prev word
            prevWordCall();
            setPreviBtnVisibilty();
            setNextiBtnVisibility();

        });


        NextiBtn = view.findViewById(R.id.imagebutton_skipword);
        NextiBtn.setOnClickListener(view14 -> {
            wordIsAnimated = false;
            phraseIsAnimated = false;
            // request nxt word
            nextWordCall();
            setPreviBtnVisibilty();
            setNextiBtnVisibility();

        });


        PlaySoundiBtn = view.findViewById(R.id.imagebutton_soundhelp);
        PlaySoundiBtn.setOnClickListener(view12 -> {
            try {
                //playing Audio file of word
                ((MainActivity) getActivity()).voiceoffer(PlaySoundiBtn, word.getText());
                Log.i("PlaySoundiBtn", word.getText());

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PlaySoundiBtn", e.toString());
            }
        });

        DisplayImageiBtn = view.findViewById(R.id.imagebutton_photohelp);
        DisplayImageiBtn.setOnClickListener(view1 -> {
            try {
                //displaying image file of word
                ((MainActivity) getActivity()).helpbypic(DisplayImageiBtn, word.getText());
                Log.i("DisplayImageiBtn", word.getText());

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("DisplayImageiBtn", e.toString());
            }
        });

        setNextiBtnVisibility();
        setPreviBtnVisibilty();


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!firstTime && !wordIsAnimated) {
            //word not animated yet
            if (!phraseIsAnimated && words != null) {
                //word's phrase not animated
                ((MainActivity) getActivity()).openAnimationFragment(word.getPhrase());
                phraseIsAnimated = true;
            } else {
                //word phrase animated but word not animated
                ((MainActivity) getActivity()).openAnimationFragment(word.getText());
                wordIsAnimated = true;
            }
        } else if (!firstTime && instance.isWritten &&/* instance.isPronunced &&*/ !isPicked) {
            //word has been written and not picked from phrase
            //saying picking instructions and start phrase fragment to pick word
            ((MainActivity) mContext).voiceoffer(instance.wordView_MainText, mContext.getString(R.string.pickwordinstr));
            ((MainActivity) getActivity()).openPhraseFragment(word.getPhrase(), word.getText());
        } else if (!firstTime && isPicked  /*&& instance.isPronunced*/) {
            //word has been picked
            if (instance.CurrentWordsArrayIndex + 1 == instance.words.length) {
                //if it's the last word
                //update lesson, reset all word and phrase booleans, get the next lesson word and setting it's guided vector
                Log.i("LessonFragment: ", "UpdateLesson: ");
                CurrentWordsArrayIndex = 0;
                instance.word = instance.words[CurrentWordsArrayIndex];
                isPicked = false;
                instance.isWritten = false;
                instance.isPronunced = false;
                wordIsAnimated = false;
                phraseIsAnimated = false;

                instance.wordView_MainText.setGuidedVector(instance.word.getFV());
                instance.wordView_MainText.setText(
                        instance.word.getText());

                instance.wordView_MainText.invalidate();
                setNextiBtnVisibility();
                setPreviBtnVisibilty();

                // ((MainActivity) instance.mContext).updatelesson(1, true);
                ((MainActivity) instance.mContext).updateLesson(1);
            } else {
                //not the last word in lesson so get next word
                phraseIsAnimated = false;
                wordIsAnimated = false;
                instance.nextWordCall();
                instance.setPreviBtnVisibilty();
                instance.setNextiBtnVisibility();
            }
        }
    }

    //next button visibility checking
    private void setNextiBtnVisibility() {
        if (words == null) {
            //when calling openAnimationFragment(String word) and words == null
            NextiBtn.setVisibility(View.INVISIBLE);

        } else {
            //during the lesson adventure
            if (CurrentWordsArrayIndex == words.length - 1) {
                //if last word
                NextiBtn.setVisibility(View.INVISIBLE);
            } else {
                //not last word
                NextiBtn.setVisibility(View.VISIBLE);
            }
        }
    }
//previous button visibility checking

    private void setPreviBtnVisibilty() {
        if (CurrentWordsArrayIndex == 0) {
            //if first word
            PreviBtn.setVisibility(View.INVISIBLE);
        } else {
            //not first word
            PreviBtn.setVisibility(View.VISIBLE);
        }
    }

    //instructions after finishing word trip
    private Thread Thread_WordJourney_voice_speech() {

        Thread_WordJourney = new Thread() {
            @Override
            public void run() {
                try {
                    Log.i("XX", "XX");
                    sleep(WAIT2SayInstructions);
                } catch (InterruptedException ignored) {
                }
                ((MainActivity) mContext).runOnUiThread(() -> {
                    try {
                        ((MainActivity) mContext).voiceoffer(null, instance.word.getText());
                        sleep(1500);

                        if (words == null) {
                            //when calling openAnimationFragment(String word) and words == null
                            //using archive words
                            ((MainActivity) getActivity()).OpenHelpFragment();
                        } else {
                            //playing pick instruction when finish writing
                            ((MainActivity) mContext).voiceoffer(instance.wordView_MainText, mContext.getString(R.string.pickwordinstr));
                            sleep(2500);
                            //  instance.voicerec(null);
                            //start picking word
                            ((MainActivity) getActivity()).openPhraseFragment(word.getPhrase(), word.getText());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
        return Thread_WordJourney;
    }

/*    private void voicerec(View view) {
        if (view != null) {
            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            view.startAnimation(shake);
        }
        Intent voicerecogize = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voicerecogize.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        voicerecogize.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voicerecogize.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-EG");
        voicerecogize.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false);
        startActivityForResult(voicerecogize, RESULT_SPEECH);
    }*/

    //an override method from UpdateWord interface to make WordView interact with LessonFragment to update word's fonts levels
    @Override
    public Typeface updateWordLoop(Typeface typeface, int word_loop) {
        Typeface tf;
        //check word loop counter
        if (word_loop < (DEFAULT_LOOP_COUNTER * DEFAULT_TYPEFACE_LEVELS) - 2) {
            if (word_loop % DEFAULT_LOOP_COUNTER == 0) {
                // change font
                if (word_loop > 0 && word_loop == DEFAULT_LOOP_COUNTER) {
                    //level 2 (less dots level)
                    tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/lvl2.ttf");
                } else if (word_loop > DEFAULT_LOOP_COUNTER && word_loop == DEFAULT_LOOP_COUNTER * 2) {
                    //level 3 (less dots and arrows level)
                    tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/lvl3.ttf");
                } else {
                    //level 4 (Blank level)
                    return null;
                }
            } else {
                tf = typeface;
            }
        } else {
            //finish writing
            // change word
            isWritten = true;
            //store word in archive
            ((MainActivity) mContext).assignWordAsFinished(instance.word.getText());
            instance.Thread_WordJourney_voice_speech().start();
            Log.i("LessonFragment: ", "UpdateWordLoop: changeword");
            //return to font level 1 (hollow with arrows)
            tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lvl1.ttf");
        }
        return tf;
    }

    @Override
    public void setmContext(Context context) {
        mContext = context;
    }

    @Override
    public void setLessonFragment(LessonFragment fragment) {
        instance = fragment;
    }

    /*
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
                        isPronunced = true;
                    }
                }
            } else {
                Log.i("onActivityResult", "Data == null");
            }
        }
    }*/

    @Override
    public void onStop() {
        super.onStop();
        Log.i("LessonFragment", "onStop");
        if (Thread_WordJourney != null) {
            Thread_WordJourney.interrupt();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("LessonFragment", "onDetach");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //request next word
    private void nextWordCall() {
        instance = this;
        if (instance.Thread_WordJourney != null) instance.Thread_WordJourney.interrupt();

        //get next word
        instance.word = instance.words[++instance.CurrentWordsArrayIndex];

        //save word index
        ((MainActivity) getActivity()).SaveOnSharedPref(MainActivity.WordIndexKey, String.valueOf(instance.CurrentWordsArrayIndex));

        if (!phraseIsAnimated) {

            //animate the new phrase if not animated
            ((MainActivity) getActivity()).openAnimationFragment(instance.word.getPhrase());
            phraseIsAnimated = true;
        } else {

            //animate the new word if not animated
            ((MainActivity) getActivity()).openAnimationFragment(instance.word.getText());

            wordIsAnimated = true;
        }
        //reset word operations
        isPicked = false;
        instance.isWritten = false;
        instance.isPronunced = false;
        //set word guided vector
        instance.wordView_MainText.setGuidedVector(instance.word.getFV());
        instance.wordView_MainText.setText(
                instance.word.getText());

        instance.wordView_MainText.invalidate();

    }

    //request previous word
    private void prevWordCall() {
        if (instance.Thread_WordJourney != null) instance.Thread_WordJourney.interrupt();

        //getting previous word
        instance.word = instance.words[--instance.CurrentWordsArrayIndex];

        //store word index
        ((MainActivity) getActivity()).SaveOnSharedPref(MainActivity.WordIndexKey, String.valueOf(instance.CurrentWordsArrayIndex));
        if (!phraseIsAnimated) {

            //animate the new phrase if not animated
            ((MainActivity) getActivity()).openAnimationFragment(instance.word.getPhrase());
            phraseIsAnimated = true;
        } else {

            //animate the new word if not animated
            ((MainActivity) getActivity()).openAnimationFragment(instance.word.getText());
            wordIsAnimated = true;
        }

        //reset word operation
        isPicked = false;
        instance.isPronunced = false;

        //set word guided vector
        instance.wordView_MainText.setGuidedVector(instance.word.getFV());
        instance.wordView_MainText.setText(
                instance.word.getText());

        instance.wordView_MainText.invalidate();
    }
}