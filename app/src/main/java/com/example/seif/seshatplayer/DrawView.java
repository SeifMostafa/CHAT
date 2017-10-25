package com.example.seif.seshatplayer;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.seif.seshatplayer.model.Direction;

import java.io.IOException;
import java.util.ArrayList;


public class DrawView extends TextView {
    private static final float POINT_WIDTH = 2;
    Context context;

    ArrayList<Direction> mUserGuidedVectors;
    int wordCharsChecked = 0;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private Paint mBitmapPaint;
    private Paint circlePaint;
    private Path circlePath;
    private float mX, mY, mFingerFat;
    private Point lastPoint;
    private float textviewSZ;
    private ArrayList<Point> mTouchedPoints;
    private GestureDetector mGestureDetector;


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
        textviewSZ = this.getTextSize();
        mUserGuidedVectors = new ArrayList<>();
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
        Log.i("CustTextView", "touch_start: FingerFat: " + ff);
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
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            mTouchedPoints.add(new Point((int) x, (int) y));
        }
    }

    private void touch_up() {

        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
        if (mTouchedPoints.size() >= 2) {
            for (int i = 0; i < mTouchedPoints.size() - 1; i++) {
                Point point1 = mTouchedPoints.get(i);
                Point point2 = mTouchedPoints.get(i + 1);
                Direction[] directions = ComparePointsToCheckFV(point1, point2);
                Direction XDirection = directions[0];
                Direction YDirection = directions[1];
                if (XDirection != null && YDirection != null) {
                    if (mUserGuidedVectors.size() > 0) {
                        if (mUserGuidedVectors.get(mUserGuidedVectors.size() - 2) != XDirection || mUserGuidedVectors.get(mUserGuidedVectors.size() - 1) != YDirection) {
                            mUserGuidedVectors.add(XDirection);
                            mUserGuidedVectors.add(YDirection);
                        }
                    } else {
                        mUserGuidedVectors.add(XDirection);
                        mUserGuidedVectors.add(YDirection);
                    }
                }
            }
        } else {
            // single point
            if (mUserGuidedVectors.size() != 0) {
                Direction[] directions = ComparePointsToCheckFV(lastPoint, mTouchedPoints.get(mTouchedPoints.size() - 1));
                mUserGuidedVectors.add(directions[0]);
                mUserGuidedVectors.add(directions[1]);
            }
        }

        boolean checkResult = mGestureDetector.check(mUserGuidedVectors);
        double percentage = mGestureDetector.getSuccessPercentage();

        Log.i("CustTextView: ","touch_up: check result= "+ checkResult);

        if(checkResult || percentage > 70){
            // next
             reset();
            Toast.makeText(context,"Congrats",Toast.LENGTH_LONG).show();
        }else{
            reset();
            Toast.makeText(context,"FAILED",Toast.LENGTH_LONG).show();
        }
        Toast.makeText(context,"Percentage " + mGestureDetector.getSuccessPercentage()  ,Toast.LENGTH_LONG).show();

        lastPoint = new Point(mTouchedPoints.get(mTouchedPoints.size() - 1));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float fingerfat = (event.getSize() * 100);

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
    public void SetGuidedVector(Direction[]directions){
        mGestureDetector = new GestureDetector(directions);
    }
}