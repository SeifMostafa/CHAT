package com.example.seif.seshatplayer;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.example.seif.seshatplayer.model.Word;

import java.io.IOException;

public class TypeWriter extends TextView {
    public Word word;
    public Context mContext;
    Context context;
    private CharSequence mText;
    private int mIndex;
    private int idx;
    private long mDelay = 600;
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
            setText(txt);
            if (mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
                ((MainActivity) context).voiceoffer(null, String.valueOf(mText.charAt(idx)) + ".wav");
                Log.i("TypeWriter::characterAdder Runnable " + idx, mText.charAt(idx) + "");
                idx++;
            } else {

                ((MainActivity) context).voiceoffer(null, mText.toString());

                Log.i("TypeWriter: ", "finished");
            }
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

    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }

    public void setWord(Word w) {
        this.word = w;
    }
}
