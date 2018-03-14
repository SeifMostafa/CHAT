package com.example.seif.seshatplayer;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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

    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(400);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CharSequence txt = mText.subSequence(0, mIndex++);
            if (mIndex <= mText.length()) {
                ((MainActivity) context).voiceoffer(null, String.valueOf(mText.charAt(idx)) + ".wav");

                mHandler.postDelayed(characterAdder, mDelay);
                Log.i("TypeWriter::characterAdder Runnable " + idx, mText.charAt(idx) + "");
                idx++;
            } else {
                ((MainActivity) context).voiceoffer(null, mText.toString());

                Log.i("TypeWriter: ", "finished");
            }
            setText(txt);

        }

    };

    private Runnable wordAdder = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(400);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (idx < words.size()) {
                phrase += words.get(idx) + " ";
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
                    animatePhrase();
                }
            }
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

    public void animateText(CharSequence text) {
        mText = text;
        mIndex = 0;
        idx = 0;
        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }

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

    public void setWord(String w) {
        this.word = w;
        words = getRawWords(word);

    }

    public ArrayList<String> getRawWords(String s) {
        String[] s2 = s.split(" ");
        ArrayList<String> words = new ArrayList<>();
        words.addAll(Arrays.asList(s2));
        return words;
    }
}
