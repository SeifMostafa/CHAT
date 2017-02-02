package com.seifmostafa.cchat;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by azizax on 31/01/17.
 */
public class CustTextView extends TextView {

    public CustTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Helvetica_Neue.ttf");
        setTypeface(tf ,1);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}

