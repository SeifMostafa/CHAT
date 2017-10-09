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
import android.widget.Toast;

import com.example.seif.seshatplayer.model.Direction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class DrawView extends TextView {

    private static final float TOUCH_TOLERANCE = 4;
    Context context;
   // Point[] TriggerPoints;
    ArrayList<Direction> GuidedVector;
    int TOLERANCE_MIN = 10;
    int TOLERANCE_MAX = 100;
    boolean skipinit = false;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private Paint mBitmapPaint;
    private Paint circlePaint;
    private Path circlePath;
    private float mX, mY;
    private ArrayList<Point> touchedpoints;
    private ArrayList<Direction> UserGuidedVector = new ArrayList<>();


    public DrawView(Context context) throws IOException {
        super(context);
        this.context = context;

        init();
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        init();
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        init();
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;

        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/lvl1.ttf");
        this.setTypeface(tf);
        Log.i("init", "AM HERE!");


        mPath = new Path();
        touchedpoints = new ArrayList<>();
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
        final float prev_x = mX, prev_y = mY, cur_x = x, cur_y = y;
        Appending2UserGuidedVector(prev_x, prev_y, cur_x, cur_y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            final float prev_x = mX, prev_y = mY, cur_x = x, cur_y = y;
            Appending2UserGuidedVector(prev_x, prev_y, cur_x, cur_y);

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
        //  Log.i("DrawView.touch_up", "Direction::L" + UserGuidedVector.size());
        //   Log.i("DrawView.touch_up", "Direction::OL" + GuidedVector.size());
        if (UserGuidedVector.size() >= GuidedVector.size()) {
            Log.i("DrawView.touch_up", "YUP");
            boolean result = CompareGuidedVector(UserGuidedVector, GuidedVector);
            Log.i("onTouchEvent", "ACTION_UP::GuidedVectorCMPR_Res:" + String.valueOf(result));
            if (result) {
                // nxt
                Toast.makeText((MainActivity) context, "YUP", Toast.LENGTH_LONG).show();

                Typeface newTypeface = ((MainActivity) context).updateWordLoop();
                if (newTypeface == null) {
                    Toast.makeText((MainActivity) context, "SAME FONT LOOP++", Toast.LENGTH_LONG).show();
                    reset();

                } else {
                    this.setTypeface(newTypeface);
                    reset();
                    Toast.makeText((MainActivity) context, "NEW FONT", Toast.LENGTH_LONG).show();
                }

            } else {
                // reset
                Toast.makeText(context, "NOPE", Toast.LENGTH_LONG).show();
                ((MainActivity) context).updatelesson(0);
            }
            UserGuidedVector.clear();
        }    else {
            Log.i("DrawView.touch_up", "NOPE");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
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

    /*public void SetTriggerPoints(Point[] trpoints) {
        this.TriggerPoints = trpoints;
    }*/

    public void SetGuidedVector(Direction[] gv) {
        this.GuidedVector = new ArrayList();
        Collections.addAll(GuidedVector, gv);
        Log.i("SetGuidedVector", "   GdLength:" + GuidedVector.size());
    }


    private boolean CompareGuidedVector(ArrayList<Direction> USERgv, ArrayList<Direction> list_Org_Directions) {
        int tolerance_failure = 0;
        int orgI = 1;  // 0 index = INIT

    /*    for(Direction direction:USERgv){
            Log.i("USERgv:  ","Direction: "+ direction);
        }

        for(Direction direction:list_Org_Directions){
            Log.i("ORGgv:  ","Direction: "+ direction);
        }*/

        if (!list_Org_Directions.equals(null) && !USERgv.equals(null)) {
            for (int i = 0; i < USERgv.size() - 1; ) {
                Direction d_X = USERgv.get(i);
                Direction d_Y = USERgv.get(i + 1);
                Direction ORG_d_X = list_Org_Directions.get(i);
                Direction ORG_d_Y = list_Org_Directions.get(i + 1);
                try {
                    if (d_X != null && d_Y != null && ORG_d_X != null && ORG_d_Y != null) {

                        if ((d_X != ORG_d_X || d_Y != ORG_d_Y)) {

                            tolerance_failure++;


                          /*  if (i + 3 < list_Org_Directions.size()) {
                                if (d_X != list_Org_Directions.get(i + 2) || d_Y == list_Org_Directions.get(i + 3)) {
                                    tolerance_failure++;
                                } else tolerance_failure++;
                            }*/
                        }
                    }
                    i += 2;
                    Log.i("FAILED",""+tolerance_failure);
                    if (tolerance_failure <=5) return true;
                    else return false;
                } catch (Exception e) {
                    Log.e("DrawView", "CompareGuidedVector" + e.toString());
                }
            }
        }

        return false;
    }

    private void Appending2UserGuidedVector(final float prev_x, final float prev_y, final float cur_x, final float cur_y) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Direction[] tempDirections = Utils.ComparePointsToCheckFV(prev_x, prev_y, cur_x, cur_y);
                Direction temp_direction_x = tempDirections[tempDirections.length - 1];
                Direction temp_direction_y = tempDirections[tempDirections.length - 2];

                if (UserGuidedVector.size() >= 2) {
                    Direction direction_x = UserGuidedVector.get(UserGuidedVector.size() - 1);
                    Direction direction_y = UserGuidedVector.get(UserGuidedVector.size() - 2);
                    if ((direction_x != temp_direction_x || direction_y != temp_direction_y) && temp_direction_x != null && temp_direction_y != null) {
                        UserGuidedVector.addAll(Arrays.asList(tempDirections));
                    }
                } else if (skipinit) {
                    if (temp_direction_x != null && temp_direction_y != null) {
                        UserGuidedVector.addAll(Arrays.asList(tempDirections));
                    }
                } else {
                    skipinit = true;
                }
            }
        }).start();
    }
}