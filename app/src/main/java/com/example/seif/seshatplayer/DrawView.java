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
    private ArrayList<Direction> UserGuidedVector  = new ArrayList<>();


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
                ArrayList<Direction> tempDirections = ComparePointsToCheckFV(touchedpoints);
                if(UserGuidedVector.size()>=2 && tempDirections.size() == 2){
                    if((UserGuidedVector.get(UserGuidedVector.size()-1) != tempDirections.get(tempDirections.size()-1)) ||
                            (UserGuidedVector.get(UserGuidedVector.size()-2) != tempDirections.get(tempDirections.size()-2))){
                        UserGuidedVector.addAll(tempDirections);
                    }
                }else{
                    UserGuidedVector.addAll(tempDirections);
                }
                touchedpoints = new ArrayList<>();
                break;

            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();


                if (UserGuidedVector.size() >= 2) {

                    Log.i("onTouchEvent", "ACTION_UP::UserGuidedVector::Directions before: " + UserGuidedVector.size());
                     clearedRedundancyList(UserGuidedVector);
                    Log.i("onTouchEvent", "ACTION_UP::UserGuidedVector::Directions after: " + UserGuidedVector.size());

                  /*  for(Direction d:UserGuidedVector){
                        Log.i("Direction: ",""+d);
                    }*/

                    if (UserGuidedVector.size() >= GuidedVector.size()) {

                        boolean result = CompareGuidedVector(UserGuidedVector, GuidedVector);
                        Log.i("onTouchEvent","ACTION_UP::GuidedVectorCMPR_Res:" + String.valueOf(result));
                        if(result){
                            // nxt
                        }else{
                            // reset
                        }
                    }else{
                        Log.i("onTouchEvent","ACTION_UP::"+GuidedVector.size());
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
        this.GuidedVector = new ArrayList();
        Collections.addAll(GuidedVector, gv);
  //     clearedRedundancyList(GuidedVector);
        Log.i("SetGuidedVector", "   GdLength:" + GuidedVector.size());
    }


    public ArrayList<Direction> ComparePointsToCheckFV(ArrayList<Point> touchedpoints) {
        ArrayList<Direction> result = new ArrayList<>();
        for (int i = 0; i < touchedpoints.size() - 1; i++) {
            Point p1 = touchedpoints.get(i), p2 = touchedpoints.get(i + 1);
            int x1 = p1.x, x2 = p2.x, y1 = p1.y, y2 = p2.y;
            Direction direction[] = Utils.ComparePointsToCheckFV(x1, y1, x2, y2);
            result.add(direction[0]);
            result.add(direction[1]);
        }
        return result;
    }

    private boolean CompareGuidedVector(ArrayList<Direction> USERgv, ArrayList<Direction> list_Org_Directions) {

/*        int orgI = 1;  // 0 index = INIT
        for(Direction direction:USERgv){
            Log.i("USERgv:  ","Direction: "+ direction);
        }
        for(Direction direction:list_Org_Directions){
            Log.i("ORGgv:  ","Direction: "+ direction);
        }*/
        if (!list_Org_Directions.equals(null) && !USERgv.equals(null)) {
            for (int i = 0; i < USERgv.size() - 1; i += 2) {
                Direction d_X = USERgv.get(i);
                Direction d_Y = USERgv.get(i + 1);
                Direction ORG_d_X = list_Org_Directions.get(i);
                Direction ORG_d_Y = list_Org_Directions.get(i + 1);

                if (d_X != null && d_Y != null && ORG_d_X != null && ORG_d_Y != null) {

                    if (d_X != ORG_d_X && d_Y != ORG_d_Y) {
                        return false;
                    } /*else if (list_Org_Directions.get(orgI + 2) != null && list_Org_Directions.get(orgI + 3) != null) {
                        if (d_X == list_Org_Directions.get(orgI + 2) && d_Y == list_Org_Directions.get(orgI + 3)) {
                            orgI++;
                        }
                    }*//*else if(){
                     */   // null's
                    //}
                }
            }
        }
        return false;
    }
}