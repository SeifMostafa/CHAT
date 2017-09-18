package com.example.seif.seshatplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seif.seshatplayer.layout.MainFragment;
import com.example.seif.seshatplayer.model.Word;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class DrawView extends TextView {
    Context context;
    private int color = Color.parseColor("#0000FF");
    private float width = 4f;
    Point[]TriggerPoints;
    int TOLERANCE_MIN = 10;
    int TOLERANCE_MAX = 100;
    private List<Holder> holderList = new ArrayList<Holder>();

    private class Holder {
        Path path;
        Paint paint;

        Holder(int color, float width) {
            path = new Path();
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(width);
            paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
        }
    }

    public DrawView(Context context) throws IOException {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) throws IOException {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) throws IOException {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

        holderList.add(new Holder(color, width));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawPoint(TriggerPoints[0].x,TriggerPoints[0].y,new Holder(color,width).paint);
        for (Holder holder : holderList) {
            canvas.drawPath(holder.path, holder.paint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                holderList.add(new Holder(color, width));
                holderList.get(holderList.size() - 1).path.moveTo(eventX, eventY);
                break;

            case MotionEvent.ACTION_MOVE:
                //holderList.get(holderList.size() - 1).path.lineTo(eventX, eventY);
                Point initWord=TriggerPoints[0];
                if(Math.abs(eventX-initWord.x)<=TOLERANCE_MAX && Math.abs(eventY-initWord.y)<=TOLERANCE_MAX){
                holderList.get(holderList.size() - 1).path.lineTo(eventX, eventY);
                }
            case MotionEvent.ACTION_UP:
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }
    @Override
    public ViewPropertyAnimator animate() {

        final int screenWidth, currentMsg;
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
        final Animation.AnimationListener finalMyAnimationListener = myAnimationListener;
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
                DrawView.this.measure(0, 0);
                // Get textView width
                int textWidth = DrawView.this.getMeasuredWidth();
                // Create the animation
                animation = new TranslateAnimation(-textWidth, screenWidth, 0, 0);

                animation.setDuration(5000);
                animation.setRepeatMode(Animation.RESTART);
                animation.setRepeatCount(Animation.INFINITE);
                animation.setAnimationListener(finalMyAnimationListener);
                DrawView.this.setAnimation(animation);
            }
        };
        animation.setAnimationListener(myAnimationListener);

        this.setAnimation(animation);
        return super.animate();
    }
    public void SetTriggerPoints(Point[]trpoints){
        this.TriggerPoints = trpoints;
    }
}
