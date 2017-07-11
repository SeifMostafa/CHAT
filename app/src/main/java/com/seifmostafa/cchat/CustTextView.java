package com.seifmostafa.cchat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

/**
 * Created by azizax on 31/01/17.
 */
public class CustTextView extends TextView {
    Context context;
    public CustTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=context;
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
        setTypeface(tf, 1);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public ViewPropertyAnimator animate() {

        int screenWidth, currentMsg;
         Animation.AnimationListener myAnimationListener = null;

        // Get the screen width
        Point size = new Point();
        ((Activity) context).getWindowManager().getDefaultDisplay().getSize(size);
        screenWidth = (int) size.x;

        // Measure the size of textView
        this.measure(0, 0);
        // Get textView width
        int textWidth = this.getMeasuredWidth();
        // Create the animation
        Animation animation = new TranslateAnimation(-textWidth, screenWidth, 0, 0);
        animation.setDuration(5000);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(Animation.INFINITE);

        // Create the animation listener
        Animation.AnimationListener finalMyAnimationListener = myAnimationListener;
        myAnimationListener = new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // If out of messages loop from start

                // Measure the size of textView // this is important
                CustTextView.this.measure(0, 0);
                // Get textView width
                int textWidth = CustTextView.this.getMeasuredWidth();
                // Create the animation
                animation = new TranslateAnimation(-textWidth, screenWidth, 0, 0);

                animation.setDuration(5000);
                animation.setRepeatMode(Animation.RESTART);
                animation.setRepeatCount(Animation.INFINITE);
                animation.setAnimationListener(finalMyAnimationListener);
                CustTextView.this.setAnimation(animation);
            }
        };
        animation.setAnimationListener(myAnimationListener);

        CustTextView.this.setAnimation(animation);
        return super.animate();
    }


}

