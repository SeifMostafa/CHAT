package com.example.seif.seshatplayer;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

public class Typewriter extends TextView {
    Context context;
    private CharSequence mText;
    private int mIndex;
    private int idx;

    private long mDelay = 500; //Default 500ms delay
    private Handler mHandler = new Handler();
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            CharSequence txt = mText.subSequence(0, mIndex++);
            setText(txt);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
                ((MainActivity) context).voiceoffer(null, String.valueOf(mText.charAt(idx)));
                Log.i("xx" + idx, mText.charAt(idx) + "");
                idx++;
            }
        }
    };

    public Typewriter(Context context) throws IOException {
        super(context);
        this.context = context;
    }

    public Typewriter(Context context, AttributeSet attrs) throws IOException {
        super(context, attrs);
        this.context = context;
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

}
