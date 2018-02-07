package com.example.seif.seshatplayer;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.seif.seshatplayer.layout.LessonFragment;
import com.example.seif.seshatplayer.model.Direction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


public class WordView extends TextView {

    private static float POINT_WIDTH = 0;
    public int word_loop = 0;
    Context context;
    LessonFragment mLessonFragment;
    ArrayList<Direction> mUserGuidedVectors;
    UpdateWord updateWord;
    ImageButton successBtn, fontBtn;
    Typeface newTypeface = null;
    int indx = 0;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private Paint mBitmapPaint;
    private Paint circlePaint;
    private Path circlePath;
    private float mX, mY, mFingerFat;
    private Point lastPoint;
    private ArrayList<Point> mTouchedPoints;
    private GestureDetector mGestureDetector;
    private int charsPassed = 0;
    private Map<Integer, Direction[][]> gesture;
    private int gestureSize = 0;


    public WordView(Context context) throws IOException {
        super(context);
        this.context = context;
        init();

    }

    public WordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public WordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        init();
    }

    public WordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;

        init();
    }

    public void init() {
        //word_loop=context.getSharedPreferences(MainActivity.WORDS_PREFS_NAME,0).getInt(MainActivity.WordLoopKey,0);
        newTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/lvl1.ttf");
        this.setTypeface(newTypeface);
        Log.i("init", "AM HERE!");
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(8f);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(8);
        POINT_WIDTH = 1f;
        mUserGuidedVectors = new ArrayList<>();
        setTextColor(Color.BLACK);
        updateWord = new LessonFragment();
        successBtn = (ImageButton) ((MainActivity) context).findViewById(R.id.imagebutton_success);
        fontBtn = (ImageButton) ((MainActivity) context).findViewById(R.id.imagebutton_skipFont);
        fontBtn.setOnClickListener(view -> {
            indx++;
            fontCreator();
        });
    }

    private void fontCreator() {
        setTextColor(Color.BLACK);
        if (indx == 0) {
            newTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/lvl1.ttf");
            word_loop = 0;
        } else if (indx == 1) {
            newTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/lvl2.ttf");
            setTypeface(newTypeface);
            word_loop = 4;
        } else if (indx == 2) {
            newTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/lvl3.ttf");
            setTypeface(newTypeface);
            word_loop = 8;
        } else if (indx == 3) {
            setTextColor(Color.TRANSPARENT);
            word_loop = 12;
            this.indx = 0;

        }
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
        invalidate();
        gestureSize = 0;
        mUserGuidedVectors.clear();
        mTouchedPoints.clear();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(circlePath, circlePaint);

    }

    private void touch_start(float x, float y, float ff) {

        mPath.reset();
        mPath.moveTo(x, y);


        mX = x;
        mY = y;
        mFingerFat = ff;
        mPath.addCircle(mX, mY, POINT_WIDTH, Path.Direction.CW);
        mTouchedPoints = new ArrayList<>();
        mTouchedPoints.add(new Point((int) x, (int) y));

    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= mFingerFat || dy >= mFingerFat) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
            circlePath.reset();
            circlePath.addCircle(mX, mY, 20, Path.Direction.CW);
            mTouchedPoints.add(new Point((int) x, (int) y));
        }

    }


    private ArrayList<Direction> appendPoints(ArrayList<Point> touchedPoints, ArrayList<Direction> outputUserGV) {
        if (touchedPoints.size() >= 2) {
            for (int i = 0; i < touchedPoints.size() - 1; i++) {
                Point point1 = touchedPoints.get(i);
                Point point2 = touchedPoints.get(i + 1);
                Direction[] directions = ComparePointsToCheckFV(point1, point2);
                Direction XDirection = directions[0];
                Direction YDirection = directions[1];

                if (outputUserGV.size() > 0) {
                    if ((outputUserGV.get(outputUserGV.size() - 2) != XDirection || outputUserGV.get(outputUserGV.size() - 1) != YDirection) &&
                            (XDirection != Direction.SAME || YDirection != Direction.SAME)) {
                        outputUserGV.add(XDirection);
                        outputUserGV.add(YDirection);
                    }
                } else {
                    if ((XDirection != Direction.SAME || YDirection != Direction.SAME)) {
                        outputUserGV.add(XDirection);
                        outputUserGV.add(YDirection);
                    }
                   /* else if ((XDirection == Direction.SAME && YDirection == Direction.SAME)) {
                        outputUserGV.add(XDirection);
                        outputUserGV.add(YDirection);
                    }*/
                }
            }
            lastPoint = new Point(touchedPoints.get(touchedPoints.size() - 1));
        } else {
            // single point
            if (outputUserGV.size() != 0) {
                Direction[] directions = ComparePointsToCheckFV(lastPoint, touchedPoints.get(touchedPoints.size() - 1));
                outputUserGV.add(directions[0]);
                outputUserGV.add(directions[1]);
            }
        }

        return outputUserGV;
    }

    /*
        called every time user touch screen to draw something and up his/her finger
     */
    private void wholeCheck(ArrayList<Direction> outputUserGV) {

        if (gestureSize == 0) {
            for (int j = 0; j < gesture.get(0).length; j++) {
                for (int l = 0; l < gesture.get(0)[j].length; l++) {
                    gestureSize++;
                }
            }
        }
        try {

            boolean checkResult = mGestureDetector.check(outputUserGV);

            /* take action upon the checkResult: update word/update char */
            Log.i("WordView", " gestureSize/gesture.get(0).length =  " + gestureSize / gesture.get(0).length);
            Log.i("WordView", " gestureSize-outputUserGV.size() =  " + (gestureSize - outputUserGV.size()));

            if (!checkResult) {
                int trials = 1;  // already checked for 1st time
                while (trials < gesture.size()) {

                    GestureDetector GD_otherV = new GestureDetector(gesture.get(trials++));
                    //  if(charsPassed+2==gesture.get(0).length) GD_otherV.setThreshold(50);
                    if (GD_otherV.check(outputUserGV)) {
                        Log.i("WordView", "tryOtherVersions:: trials:Success in trial: " + trials);
                        checkResult = true;
                        break;
                    }
                }
            }
            if (checkResult) {
                gestureSize = 0;
                reset();
                ((MainActivity) context).voiceoffer(null, context.getString(R.string.congrats));
                Log.i("WordView", " completed ");

                updateWord.setmContext(context);
                updateWord.setLessonFragment(this.mLessonFragment);
                // ((MainActivity)context).SaveOnSharedPref(MainActivity.WordLoopKey, String.valueOf(word_loop));

                newTypeface = updateWord.updateWordLoop(this.getTypeface(), ++word_loop);

                if (newTypeface != null) {
                    this.setTypeface(newTypeface);
                    invalidate();
                } else {
                    if (word_loop != 0) {
                        this.setTextColor(Color.TRANSPARENT);
                        this.invalidate();
                        Log.i("WordView", " from touch_up: blank level");
                    }
                }
            } else {
                Log.i("WordView", "Try Again");
                ((MainActivity) context).voiceoffer(null, context.getString(R.string.tryAgain));
                reset();
                outputUserGV.clear();
            }
            mGestureDetector = new GestureDetector(gesture.get(0));
        } catch (Exception e) {
            Log.e("WordView: ", "error: " + e.toString());
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();

        mUserGuidedVectors = appendPoints(mTouchedPoints, mUserGuidedVectors);
        checkSize(mUserGuidedVectors);

    }

    private void checkSize(ArrayList<Direction> outputUserGV) {
        if (gestureSize == 0) {
            for (int j = 0; j < gesture.get(0).length; j++) {
                for (int l = 0; l < gesture.get(0)[j].length; l++) {
                    gestureSize++;
                }
            }
        }

        if (gestureSize - outputUserGV.size() <= gestureSize / gesture.get(0).length) {
            successBtn.setVisibility(VISIBLE);

            successBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    successBtn.setVisibility(INVISIBLE);
                    wholeCheck(mUserGuidedVectors);
                }
            });

        }

        mGestureDetector = new GestureDetector(gesture.get(0));
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float fingerfat = 20;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y, fingerfat);
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

    private Direction[] ComparePointsToCheckFV(Point point1, Point point2) {
        float dx = Math.abs(point1.x - point2.x);
        float dy = Math.abs(point1.y - point2.y);

        Direction XDirection = null, YDirection = null;

        if (point1.y > point2.y) YDirection = Direction.UP;
        else if (point1.y < point2.y) YDirection = Direction.DOWN;
        if (dy <= mFingerFat) YDirection = Direction.SAME;

        if (point1.x > point2.x) XDirection = Direction.LEFT;
        else if (point1.x < point2.x) XDirection = Direction.RIGHT;
        if (dx <= mFingerFat) XDirection = Direction.SAME;
        return new Direction[]{XDirection, YDirection};
    }

    public void setGuidedVector(Map<Integer, Direction[][]> gvVersions) {
        gestureSize = 0;

        mGestureDetector = new GestureDetector(gvVersions.get(0)); // first version .. first char
        gesture = gvVersions;

        for (int j = 0; j < gesture.get(0).length; j++) {
            for (int l = 0; l < gesture.get(0)[j].length; l++) {
                gestureSize++;
            }
        }

    }

    public void setmLessonFragment(LessonFragment mLessonFragment) {
        this.mLessonFragment = mLessonFragment;
    }
}