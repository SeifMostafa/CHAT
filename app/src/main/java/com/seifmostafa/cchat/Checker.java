package com.seifmostafa.cchat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static com.seifmostafa.cchat.MainActivity.TextasMat;
import static com.seifmostafa.cchat.MainActivity.editor;
import static com.seifmostafa.cchat.MainActivity.textViewPercentage;
import static com.seifmostafa.cchat.MainActivity.updateWord;
import static com.seifmostafa.cchat.MainActivity.wordloop;
import static com.seifmostafa.cchat.Utils.IspointSexy;
import static com.seifmostafa.cchat.Utils.MAX_TOLERANCE;
import static com.seifmostafa.cchat.Utils.Mat22D;
import static com.seifmostafa.cchat.Utils.RGBtoBINARY;
import static com.seifmostafa.cchat.Utils.SaveOnSharedPref;
import static com.seifmostafa.cchat.Utils.TOLERANCE;
import static com.seifmostafa.cchat.Utils.TwoD2Mat;
import static com.seifmostafa.cchat.Utils.getTotalNonZero;
import static java.lang.Math.abs;

/**
 * Created by azizax on 11/07/17.
 */

public class Checker {
    Context context;
    private Paint mPaint;
    public static int progress;

    public Checker(Context context) {
        this.context = context;
    }

    static class ThinningService {
        /**
         * @param givenImage
         * @param changeGivenImage decides whether the givenArray should be modified or a clone should be used
         * @return a 2D array of binary image after thinning using zhang-suen thinning algo.
         */
        public static int[][] doZhangSuenThinning(final int[][] givenImage, boolean changeGivenImage) {
            int[][] binaryImage;
            if (changeGivenImage) {
                binaryImage = givenImage;
            } else {
                binaryImage = givenImage.clone();
            }
            int a, b;
            List<Point> pointsToChange = new LinkedList();
            boolean hasChange;
            do {
                hasChange = false;
                for (int y = 1; y + 1 < binaryImage.length; y++) {
                    for (int x = 1; x + 1 < binaryImage[y].length; x++) {
                        a = getA(binaryImage, y, x);
                        b = getB(binaryImage, y, x);
                        if (binaryImage[y][x] == 1 && 2 <= b && b <= 6 && a == 1
                                && (binaryImage[y - 1][x] * binaryImage[y][x + 1] * binaryImage[y + 1][x] == 0)
                                && (binaryImage[y][x + 1] * binaryImage[y + 1][x] * binaryImage[y][x - 1] == 0)) {
                            pointsToChange.add(new Point(x, y));
//binaryImage[y][x] = 0;
                            hasChange = true;
                        }
                    }
                }
                for (Point point : pointsToChange) {
                    binaryImage[point.getY()][point.getX()] = 0;
                }
                pointsToChange.clear();
                for (int y = 1; y + 1 < binaryImage.length; y++) {
                    for (int x = 1; x + 1 < binaryImage[y].length; x++) {
                        a = getA(binaryImage, y, x);
                        b = getB(binaryImage, y, x);
                        if (binaryImage[y][x] == 1 && 2 <= b && b <= 6 && a == 1
                                && (binaryImage[y - 1][x] * binaryImage[y][x + 1] * binaryImage[y][x - 1] == 0)
                                && (binaryImage[y - 1][x] * binaryImage[y + 1][x] * binaryImage[y][x - 1] == 0)) {
                            pointsToChange.add(new Point(x, y));
                            hasChange = true;
                        }
                    }
                }
                for (Point point : pointsToChange) {
                    binaryImage[point.getY()][point.getX()] = 0;
                }
                pointsToChange.clear();
            } while (hasChange);
            return binaryImage;
        }

        private static int getA(int[][] binaryImage, int y, int x) {
            int count = 0;
//p2 p3
            if (y - 1 >= 0 && x + 1 < binaryImage[y].length && binaryImage[y - 1][x] == 0 && binaryImage[y - 1][x + 1] == 1) {
                count++;
            }
//p3 p4
            if (y - 1 >= 0 && x + 1 < binaryImage[y].length && binaryImage[y - 1][x + 1] == 0 && binaryImage[y][x + 1] == 1) {
                count++;
            }
//p4 p5
            if (y + 1 < binaryImage.length && x + 1 < binaryImage[y].length && binaryImage[y][x + 1] == 0 && binaryImage[y + 1][x + 1] == 1) {
                count++;
            }
//p5 p6
            if (y + 1 < binaryImage.length && x + 1 < binaryImage[y].length && binaryImage[y + 1][x + 1] == 0 && binaryImage[y + 1][x] == 1) {
                count++;
            }
//p6 p7
            if (y + 1 < binaryImage.length && x - 1 >= 0 && binaryImage[y + 1][x] == 0 && binaryImage[y + 1][x - 1] == 1) {
                count++;
            }
//p7 p8
            if (y + 1 < binaryImage.length && x - 1 >= 0 && binaryImage[y + 1][x - 1] == 0 && binaryImage[y][x - 1] == 1) {
                count++;
            }
//p8 p9
            if (y - 1 >= 0 && x - 1 >= 0 && binaryImage[y][x - 1] == 0 && binaryImage[y - 1][x - 1] == 1) {
                count++;
            }
//p9 p2
            if (y - 1 >= 0 && x - 1 >= 0 && binaryImage[y - 1][x - 1] == 0 && binaryImage[y - 1][x] == 1) {
                count++;
            }
            return count;
        }

        private static int getB(int[][] binaryImage, int y, int x) {
            return binaryImage[y - 1][x] + binaryImage[y - 1][x + 1] + binaryImage[y][x + 1]
                    + binaryImage[y + 1][x + 1] + binaryImage[y + 1][x] + binaryImage[y + 1][x - 1]
                    + binaryImage[y][x - 1] + binaryImage[y - 1][x - 1];
        }

        private static class Point extends org.opencv.core.Point {
            private int x;
            private int y;

            public Point(int x, int y) {
                this.x = x;
                this.y = y;
            }

            public int getX() {
                return x;
            }

            public void setX(int x) {
                this.x = x;
            }

            public int getY() {
                return y;
            }

            public void setY(int y) {
                this.y = y;
            }
        }
    }

    public class DrawingView extends View {

        private static final float TOUCH_TOLERANCE = 4;
        public int width;
        public int height;
        Context context;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        private Paint circlePaint;
        private Path circlePath;
        private float mX, mY;

        public DrawingView(Context c) {
            super(c);
            context = c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(28f);
            progress=0;
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
            // check x,y with sexy points
            // call mainactivity.updateprogress
          //  Log.i("TextasMatSZ",""+TextasMat.size());
            if(IspointSexy((int)x,(int)y,TextasMat)) {
                updateprogress(progress++);
            }
            return true;
        }

        public Path getmPath() {
            return mPath;
        }
    }

    public class CenterLine extends AsyncTask<Bitmap, Void, Mat> {
        @Override
        protected Mat doInBackground(Bitmap... params) {
            Log.i("CenterLine", "doInBackground");
            Mat src, dest;
            src = RGBtoBINARY(params[0]);
            int[][] Original = Mat22D(src);
            ThinningService.doZhangSuenThinning(Original, false);
            dest = TwoD2Mat(Original);
            return dest;
            //return TwoD2Mat(Thinning(RGBtoBINARY(params[0])));
        }

        @Override
        protected void onPostExecute(Mat mat) {
            Log.i("CenterLine", "onPostExecute");

            super.onPostExecute(mat);
        }


    }

    public class CenterLineForOriginal extends AsyncTask<Bitmap, Void, Mat> {
        @Override
        protected Mat doInBackground(Bitmap... params) {
            Log.i("CenterLineForOriginal", "doInBackground");
            Mat src, dest;
            src = RGBtoBINARY(params[0]);
            int[][] Original = Mat22D(src);
            ThinningService.doZhangSuenThinning(Original, false);
            dest = TwoD2Mat(Original);
            return dest;
            //return TwoD2Mat(Thinning(RGBtoBINARY(params[0])));
        }

        @Override
        protected void onPostExecute(Mat mat) {
            Log.i("CenterLineForOriginal", "onPostExecute");
            super.onPostExecute(mat);
        }


    }

    public DrawingView getDrawingView(){
        DrawingView dv = new DrawingView(context);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(28);
        return dv;
    }

    public double MatchWritten(Mat original, Mat written, Activity activity) {
        double percentage = 0;
        try {
            List<android.graphics.Point> Originalpoints = getTotalNonZero(original);
            List<android.graphics.Point> Writtenpoints = getTotalNonZero(written);

            List<android.graphics.Point> Original_copy = new ArrayList<>(Originalpoints);
            List<android.graphics.Point> Writtern_copy = new ArrayList<>(Writtenpoints);

            Log.i("MatchWritten", "Original: " + Originalpoints.size());
            Log.i("MatchWritten", "Written: " + Writtenpoints.size());

            Collections.sort(Original_copy, new Comparator<Point>() {
                @Override
                public int compare(android.graphics.Point o1, android.graphics.Point o2) {
                    return o1.x >= o2.x ? 1 : -1;
                }
            });
            Collections.sort(Writtern_copy, new Comparator<android.graphics.Point>() {
                @Override
                public int compare(android.graphics.Point o1, android.graphics.Point o2) {
                    return o1.x >= o2.x ? 1 : -1;
                }
            });

            int C = 0, BigMistakesCounter = 0;

            for (android.graphics.Point point : Originalpoints) {
                //   Log.i("MatchWritten","OriginalPoint: "+ point.x +" , "+ point.y);
                nextpoint:
                for (android.graphics.Point searchpoint : Writtenpoints) {
                    if (abs(searchpoint.x - point.x) <= TOLERANCE && abs(searchpoint.y - point.y) <= TOLERANCE) {
                        Original_copy.remove(point);
                        Writtern_copy.remove(searchpoint);
                        C++;
                        // enter multple times for single one !!! (cause of the nested loop) so let's
                        break nextpoint;

                    }
                    if (abs(searchpoint.x - point.x) >= MAX_TOLERANCE && abs(searchpoint.y - point.y) >= MAX_TOLERANCE) {
                        BigMistakesCounter++;
                        break nextpoint;

                    }
                }
            }
            // scalling
            // original , C , bigMistakesCounter
            double okay = (double) C / Writtenpoints.size(), notOkay = (double) BigMistakesCounter / Writtenpoints.size();

            Log.i("MatchWritten", "Mistakes:" + notOkay);
            Log.i("MatchWritten", "Okay:" + okay);

            percentage = okay * 100;

            if ((notOkay * 100) >= 25) {
                Log.i("MatchWritten", "hola big wrong");
                // test toast display arabic
                Toast.makeText(activity, "Try again", Toast.LENGTH_LONG).show();
                //   dv.reset();
            } else {
                // test toast display arabic
                // flag_new_level=false;
                Toast.makeText(activity, "Thanks", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.i("checkTypo", e.toString());
        }
        return percentage;
    }

    public void updateprogress(int progress){
        textViewPercentage.setText(String.valueOf(progress));
        textViewPercentage.setVisibility(View.VISIBLE);
        if(progress==100){
            if(wordloop==1)
            {
                // call fun update word
                updateWord();
                wordloop=0;
                SaveOnSharedPref(editor,"wordloop",""+wordloop);

            }else {
                wordloop++;
                SaveOnSharedPref(editor,"wordloop",""+wordloop);
            }
        }
        // mainactivity.nextword
        // call mainactivity.updateloop, mainactivity.updatelevel

    }
}
