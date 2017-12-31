package com.example.seif.seshatplayer;

import android.content.Context;
import android.graphics.Typeface;

import com.example.seif.seshatplayer.layout.LessonFragment;

/**
 * Created by seif on 11/2/17.
 */

public interface UpdateWord {
    Typeface updateWordLoop(Typeface typeface, int word_loop);

    void setmContext(Context context);

    void setLessonFragment(LessonFragment lessonFragment);
}
