package com.example.seif.seshatplayer;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
/*this TextView is using for animation*/

public class TypeWriter extends TextView {
    public String word;
    public String phrase = "";

    public Context mContext;
    Context context;
    int counter = 0;
    private CharSequence mText;
    private int mIndex;
    private int idx;
    private long mDelay;
    private ArrayList<String> words;

    private Handler mHandler = new Handler();
    //this runnable to animate chars of a word
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {

            try {
                Thread.sleep(400);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //to get each char displayed
            CharSequence txt = mText.subSequence(0, mIndex++);
            if (mIndex <= mText.length()) {
                //play each char audio file
                ((MainActivity) context).voiceoffer(null, String.valueOf(mText.charAt(idx)) + ".wav");
                mHandler.postDelayed(characterAdder, mDelay);
                Log.i("TypeWriter::characterAdder Runnable " + idx, mText.charAt(idx) + "");
                idx++;
            } else {
                //play whole word audio file
                ((MainActivity) context).voiceoffer(null, mText.toString());

                Log.i("TypeWriter: ", "finished");
            }
            //displaying in the TypeWriter TextView
            setText(txt);

        }

    };
    //this runnable to animate words of a phrase
    private Runnable wordAdder = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(400);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (idx < words.size()) {
                //to get each word displayed
                phrase += words.get(idx) + " ";
                //play sound of each word
                ((MainActivity) context).voiceoffer(null, words.get(idx));
                mHandler.postDelayed(wordAdder, mDelay);
                idx++;
            } else {

                try {
                    Thread.sleep(4000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (counter < 2) {
                    phrase = "";
                    //do it second time to animate again
                    animatePhrase();
                }
            }
            //displaying in the TypeWriter TextView
            setText(phrase);
        }

    };

    public TypeWriter(Context context) throws IOException {
        super(context);
        this.context = context;

    }

    public TypeWriter(Context context, AttributeSet attrs) throws IOException {
        super(context, attrs);
        this.context = context;

    }

    void setContext(Context context) {
        this.mContext = context;
    }

    //responsible to make word animation by running characterAdder
    public void animateText(CharSequence text) {
        mText = text;
        mIndex = 0;
        idx = 0;
        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }

    //responsible to make phrase animation by running wordAdder
    public void animatePhrase() {
        counter++;
        idx = 0;
        setText("");
        mHandler.removeCallbacks(wordAdder);
        mHandler.postDelayed(wordAdder, mDelay);
    }

    public void setCharacterDelay(long delay) {
        mDelay = delay;
    }

    //set word to use it in the phrase animation
    public void setWord(String w) {
        this.word = w;
        words = getRawWords(word);
    }

    //split phrase into words to animation
    public ArrayList<String> getRawWords(String s) {
        String[] s2 = s.split(" ");
        ArrayList<String> words = new ArrayList<>();
        words.addAll(Arrays.asList(s2));
        return words;
    }
}
