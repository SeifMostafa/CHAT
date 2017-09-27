package com.example.seif.seshatplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by Admin on 9/25/2017.
 */

public class Typewriter extends TextView {
    private CharSequence mText;
    private int mIndex;
    MediaPlayer mediaPlayer = null;
    String SF = "/SF/";
    private int idx;

    private long mDelay = 500; //Default 500ms delay


    public Typewriter(Context context) throws IOException {

        super(context);

    }

    public Typewriter(Context context, AttributeSet attrs) throws IOException {
        super(context, attrs);
    }

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
                voiceoffer(String.valueOf(mText.charAt(idx)));
                Log.i("xx" + idx, mText.charAt(idx) + "");
                idx++;
            }


        }
    };

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

    public void voiceoffer(String DataPath2Bplayed) {

        if (mediaPlayer != null) {
            if (mediaPlayer.isLooping() || mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(SF + DataPath2Bplayed);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.stop();

            }
        });
    }

}
