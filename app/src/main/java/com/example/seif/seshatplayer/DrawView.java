package com.example.seif.seshatplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.example.seif.seshatplayer.model.Direction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.seif.seshatplayer.Utils.CompareGuidedVector;
import static com.example.seif.seshatplayer.Utils.ComparePointsToCheckFV;
import static com.example.seif.seshatplayer.Utils.clearedRedundancyList;


public class DrawView extends TextView {
    private static final float TOUCH_TOLERANCE = 4;
    Context context;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private Paint mBitmapPaint;
    private Paint circlePaint;
    private Path circlePath;
    private float mX, mY;
    Point[] TriggerPoints;
    ArrayList<Direction> GuidedVector;
    int TOLERANCE_MIN = 10;
    int TOLERANCE_MAX = 100;
    boolean INITisOK = false;
    private ArrayList<Point> touchedpoints;
    private ArrayList<Direction> UserGuidedVector;

    public DrawView(Context context) throws IOException {
        super(context);
        this.context = context;

        init();
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/lvl1.ttf");
        this.setTypeface(tf);
        Log.i("init", "AM HERE!");
        mPath = new Path();
        touchedpoints = new ArrayList<>();
        UserGuidedVector = new ArrayList<>();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(16f);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(14);





    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    public void reset() {
        mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(this.mBitmap.getWidth(), this.mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(circlePath, circlePaint);
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                touchedpoints.add(new Point((int) x, (int) y));
                break;

            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                touchedpoints.add(new Point((int) x, (int) y));
                Log.i("ComparePointsToCheckFV","touchedpoints.length: "+touchedpoints.size());
                UserGuidedVector.addAll(ComparePointsToCheckFV(touchedpoints));
                Log.i("ComparePointsToCheckFV","touchedpoints.length: "+touchedpoints.size());
                break;

            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();



                if (UserGuidedVector.size() >= 2 ) {
                    //UserGuidedVector = clearedRedundancyList(UserGuidedVector);
                    GuidedVector = clearedRedundancyList(GuidedVector);


                    Log.i("Direction: ",""+UserGuidedVector.size());
                    UserGuidedVector = clearedRedundancyList(UserGuidedVector);
                    Log.i("Direction: ",""+UserGuidedVector.size());

                    for(Direction d:UserGuidedVector){
                        Log.i("Direction: ",""+d);
                    }

                    Log.i("GuidedVector:", "Length: " + UserGuidedVector.size());

                    if(UserGuidedVector.size()>=GuidedVector.size()){

                        boolean result = CompareGuidedVector(UserGuidedVector,GuidedVector);
                        Log.i("GuidedVector:", "Length: " + UserGuidedVector.size());
                        Log.i("GuidedVectorCMPR_Res:", "Result: " + String.valueOf(result));
                    }
                }
                break;
        }
        return true;
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

        DrawView.this.setAnimation(animation);
        return super.animate();
    }

    public void SetTriggerPoints(Point[] trpoints) {
        this.TriggerPoints = trpoints;
    }

    public void SetGuidedVector(Direction[] gv) {
        this.GuidedVector= new ArrayList();
        Collections.addAll(GuidedVector, gv);
        Log.i("GdLength:", GuidedVector.size() + "");
    }


}