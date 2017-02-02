package com.seifmostafa.cchat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static org.opencv.core.Core.CMP_NE;
import static org.opencv.core.Core.compare;
import static org.opencv.core.Core.countNonZero;
import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.core.Mat.zeros;
import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2BGR555;
import static org.opencv.imgproc.Imgproc.Canny;
import static org.opencv.imgproc.Imgproc.RETR_TREE;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.drawContours;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.moments;
public class MainActivity extends Activity {

    Mat imageMat;
    Mat matObject ;
    public int count = 1;
    public String current = null;
    private String uniqueId;
    private static final int MY_DATA_CHECK_CODE = 0;
    private static final int RESULT_SPEECH = 1;

    CustTextView textView;
    ArrayList<String > SpeechRec_results;
    FrameLayout frameLayout;
    DrawingView dv ;
    private Paint mPaint;
    public static String tempDir;
    File mypath;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    imageMat=new Mat();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        tempDir = Environment.getExternalStorageDirectory() + "/" + "GetSignature" + "/";
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("GetSignature", Context.MODE_PRIVATE);
        prepareDirectory();
        uniqueId = getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random();
        current = uniqueId + ".png";
        mypath= new File(directory,current);
        // Log.v("ABSPATH" , mypath.getAbsolutePath());


       textView = (CustTextView) findViewById(R.id.textView_text);
//        textView.setText("لُلو");
       // res=textView.getText().toString();
        frameLayout = (FrameLayout)findViewById(R.id.overtext);

        dv = new DrawingView(this);
        //setContentView(dv);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        frameLayout.addView(dv);
      //  Log.i("Touched","X: "+textView.getTop()+" Y: "+textView.getLeft());

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    public void checkfill(View view) {

        // prepare -- Calculate  black OriginalPoints
//        getTotalNonZero(getBitmapFromView(textView),OriginalPoints);
//        Log.i("blackpoints_Original",""+ OriginalPoints.size());


        Bitmap mBitmap = null;
        mBitmap =  Bitmap.createBitmap (dv.getWidth(), dv.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(mBitmap);
        dv.draw(canvas);

        try
        {
           Mat Original = new CenterLine().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,getBitmapFromView(textView)).get();
            Mat Drawed = new CenterLine().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mBitmap).get();
         int FinalPercentage =   MatchWritten(Original,Drawed,100);
            Log.i("FinalPercentage",""+FinalPercentage);
        }
        catch(Exception e)
        {
            Log.v("checkfill", e.toString());
        }
    }

    public void voicerec(View view) {
        Intent voicerecogize = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voicerecogize.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        voicerecogize.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voicerecogize.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-EG");
        voicerecogize.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,false);
        startActivityForResult(voicerecogize, RESULT_SPEECH);
    }

    public void voiceoffer(View view) {
        final MediaPlayer mp = new MediaPlayer();
        if(mp.isPlaying())
        {
            mp.stop();
        }

        try {
            mp.reset();
            AssetFileDescriptor afd;
            afd = getAssets().openFd("swan_lake.mp3");
            mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mp.prepare();
            mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void helpbypic(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("شكرا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.layout_sample_pic_help, null);
        ImageView imageView = (ImageView) dialogLayout.findViewById(R.id.picsample);
        Bitmap icon = BitmapFactory.decodeResource(MainActivity.this.getResources(),
                R.drawable.small);
        imageView.setImageBitmap(icon);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_SPEECH && requestCode == RESULT_OK);
        {
            SpeechRec_results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////check type ///////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////



    public int MatchWritten(Mat original,Mat written,int tolerance) {
        int percentage =0;
       try {
            List<android.graphics.Point> Originalpoints = getTotalNonZero(original);
            List<android.graphics.Point> Writtenpoints = getTotalNonZero(written);

           List<android.graphics.Point> Original_copy =new ArrayList<>(Originalpoints);
           List<android.graphics.Point> Writtern_copy = new ArrayList<>(Writtenpoints);

           Log.i("MatchWritten","Original: "+Originalpoints.size());
           Log.i("MatchWritten","Written: "+ Writtenpoints.size());

           Collections.sort(Original_copy, new Comparator<android.graphics.Point>() {
               @Override
               public int compare(android.graphics.Point o1, android.graphics.Point o2) {
                   return o1.x >= o2.x ? 1 : -1 ;
               }
           });
           Collections.sort(Writtern_copy, new Comparator<android.graphics.Point>() {
               @Override
               public int compare(android.graphics.Point o1, android.graphics.Point o2) {
                   return o1.x >= o2.x ? 1 : -1 ;
               }
           });

            int C=0;
           for(android.graphics.Point point:Originalpoints){
            //   Log.i("MatchWritten","OriginalPoint: "+ point.x +" , "+ point.y);
               for(android.graphics.Point searchpoint:Writtenpoints){
                    if((searchpoint.x==point.x && searchpoint.y == point.y )||
                            (searchpoint.x+tolerance==point.x && searchpoint.y+tolerance == point.y )||
                                (searchpoint.x+tolerance==point.x && searchpoint.y == point.y )||
                                    (searchpoint.x==point.x && searchpoint.y+tolerance == point.y )||
                                        (searchpoint.x==point.x+tolerance && searchpoint.y == point.y+tolerance )||
                                            (searchpoint.x==point.x+tolerance && searchpoint.y == point.y )||
                                                (searchpoint.x==point.x && searchpoint.y == point.y +tolerance)){
                        Original_copy.remove(point);
                        Writtern_copy.remove(searchpoint);

                    C++;
                    }
                 //  Log.i("MatchWritten","WrittenPoint: "+ searchpoint.x +" , "+ searchpoint.y);

               }
           }
           Log.i("MatchWritten","Original(after removing) : "+(Originalpoints.size()-Original_copy.size()));
           Log.i("MatchWritten","Original(after removing) : "+(Originalpoints.size()-C));
       }catch (Exception e){
           Log.i("checkTypo",e.toString());
       }
        return percentage;
    }
    // we can write or write then delete here
    public void WriteBitmap(File mypath,Bitmap mBitmap)  {
        try{
            FileOutputStream mFileOutStream = new FileOutputStream(mypath);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
            mFileOutStream.flush();
            mFileOutStream.close();
            String url = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "title", null);
            Log.v("log_tag","url: " + url);
            Log.v("log_tag","url: " + current);
            //Log.v("log_tag","url:REAL " + mypath);
            // In case you want to delete the file
            boolean deleted = mypath.delete();
            Log.v("log_tag","deleted: " + mypath.toString() + deleted);
            //If you want to convert the image to string use base64 converter
        }catch (Exception e){
            Log.i("WriteBitmap",e.toString());
        }

    }

    public  List<MatOfPoint>  FindContour(Mat src){
        int thresh = 100;
        int max_thresh = 255;
        Scalar color = new Scalar(100.0, 100.0, 100.0 );

        Mat mat = new Mat();
        Mat canny_output = new Mat();
        src.convertTo(mat, CV_LOAD_IMAGE_GRAYSCALE);
        List<MatOfPoint> contours = new ArrayList<>();
        double[] val = new double[4];
        Canny( mat , canny_output , thresh ,max_thresh, 3);
        Mat hierarchy = new Mat();
        findContours( canny_output, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE);

       // Log.i("SSSS",""+contours.size());
//        Mat drawing = zeros( canny_output.size(), CV_8UC3 );
//        Mat grayRnd = new Mat( canny_output.size(), CvType.CV_8U);
//        Core.randu(grayRnd, 0, 255);
//        for( int i = 0; i< contours.size(); i++ ) {
//            drawContours( drawing, contours, i, color, 2, 8, hierarchy, 0, new Point(0,0));
//        }

        return contours;
    }
    public MatOfPoint FindContour_(Mat src){
             List<MatOfPoint> chainCode;
            Mat grayImg = new Mat(src.height(), src.width(), CvType.CV_8UC1);

            //turn into binary image and invert
            Imgproc.cvtColor(src, grayImg, Imgproc.COLOR_RGB2GRAY);
            Imgproc.threshold(grayImg, grayImg, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
            Mat invertcolormatrix= new Mat(grayImg.rows(),grayImg.cols(), grayImg.type(), new Scalar(255,255,255));
            Core.subtract(invertcolormatrix, grayImg, grayImg);

            //get chain code
            chainCode = new Vector<MatOfPoint>();
            Imgproc.findContours(grayImg, chainCode, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

            //loop through indices of individual contours to find largest contour
            double largest_area = 0;
            int largest_contour_index = 0;
            for(int i = 0; i< chainCode.size(); i++){
                //find the area of the contour
                double a = Imgproc.contourArea(chainCode.get(i),false);
                //find largest contour and it's index
                if(a > largest_area){
                    largest_area= a;
                    largest_contour_index = i;
                }
            }
        Log.i("MATRIX",""+chainCode.get(largest_contour_index).size());
//        for(int j=0;j<chainCode.get(largest_contour_index).rows();j++){
//            for(int i=0;i<chainCode.get(largest_contour_index).cols();i++){
//                Log.i("MATRIX","+++"+j+","+i+"+++++++++"+chainCode.get(largest_contour_index).get(j,i));
//            }
//        }

            return chainCode.get(largest_contour_index);
        }
    public Mat getMatFromView(View view){
        Bitmap mBitmap = null;
        mBitmap =  Bitmap.createBitmap (view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(mBitmap);
        view.draw(canvas);
        try
        {
            Mat mat = new Mat(mBitmap.getHeight(), mBitmap.getWidth(), CV_8UC3);
            Utils.bitmapToMat(mBitmap,mat);
            return mat;
        }
        catch(Exception e)
        {
            Log.v("getMatFromView", e.toString());
            return null;
        }
    }
    public Mat DrawCenterPoint(Mat src){


        int thresh = 100;
        int max_thresh = 255;
        Scalar color = new Scalar(255, 255, 255 );
        List<MatOfPoint> contours = new ArrayList<>();
        Mat canny_output = new Mat();
        Mat hierarchy = new Mat();

        Canny( src, canny_output, thresh, thresh*2, 3 );
        Mat drawing = zeros( canny_output.size(), CV_8UC3 );
        findContours(canny_output,contours,hierarchy,RETR_TREE,CHAIN_APPROX_SIMPLE,new Point(0,0));
        for(int i = 0; i< contours.size(); i++){
            MatOfPoint matOfPoint = contours.get(i);
            Moments m =moments(matOfPoint);
            int x  = (int) (m.get_m10() / m.get_m00());
            int y = (int) (m.get_m01() / m.get_m00());
            drawContours( drawing, contours, -1, color, 2);
            circle(drawing,new Point(x ,y),7,color);
            // putText(drawing,textView.getText().toString(),new Point(x-20 ,y-20),7,7,color);

        }
        return drawing;
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

        public int width;
        public  int height;
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path mPath;
        private Paint   mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context=c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(16f);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath( mPath,  mPaint);
            canvas.drawPath( circlePath,  circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

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
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
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
            mCanvas.drawPath(mPath,  mPaint);
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
            return true;
        }

        public Path getmPath() {
            return mPath;
        }
    }

    public class CenterLine extends AsyncTask<Bitmap,Void,Mat>{
        @Override
        protected Mat doInBackground(Bitmap... params) {
            Log.i("CenterLine","doInBackground");
            Mat src,dest;
            src = RGBtoBINARY(params[0]);
            int [][] Original = Mat22D(src);
            ThinningService.doZhangSuenThinning(Original,false);
             dest = TwoD2Mat(Original);
            return dest;
            //return TwoD2Mat(Thinning(RGBtoBINARY(params[0])));
        }

        @Override
        protected void onPostExecute(Mat mat) {
            super.onPostExecute(mat);
        }


    }

    private Mat RGBtoBINARY(Bitmap mBitmap) {
        Mat mat = new Mat(mBitmap.getHeight(), mBitmap.getWidth(), CV_8UC1);
        Utils.bitmapToMat(mBitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2RGBA, 4);
        Bitmap bitmap = Bitmap.createBitmap(mBitmap);
        Utils.matToBitmap(mat, bitmap);
        Mat result = new Mat(mBitmap.getHeight(), mBitmap.getWidth(), CV_8UC3);
        Utils.bitmapToMat(bitmap, result);
        for (int x = 0; x < result.height(); ++x) {
            for (int y = 0; y < result.width(); ++y) {
                // get one pixel color
                int red = (int) result.get(x, y)[0];
                int green = (int) result.get(x, y)[1];
                int blue = (int) result.get(x, y)[2];

                if (red != 0 || green != 0 || blue != 0) {
                    result.put(x, y,new double[]{255.0,255.0,255.0,0});
                } else{
                    result.put(x, y, new double[]{0,0,0,0});
                    //Log.i("RGBtoBINARY", "R: "+red+" G: "+green+" B: "+blue);
                }
            }
        }
        return result;
    }

    private int [][] Mat22D(Mat mat){
        int [][] result = new int[mat.rows()][mat.cols()];
        for(int i=0;i<mat.rows();i++){
            for(int j=0;j<mat.cols();j++){
                if((int) mat.get(i,j)[0]==255 &&(int) mat.get(i,j)[1]==255 &&(int) mat.get(i,j)[2]==255){
                    result[i][j] = 1;
                }else  result[i][j] = 0;
            }
        }
        return result;
    }

    private Mat TwoD2Mat(int [][] source){
        Mat mat = new Mat(source.length,source[0].length,CV_8UC1);
        for(int i=0;i<source.length;i++){
            for(int j=0;j<source[i].length;j++){
                if(source[i][j]==1){
                    mat.put(i,j,new double[]{255,255,255});
                }else mat.put(i,j,new double[]{0,0,0});
            }
        }
        Log.i("TwoD2Mat","Finished");
        return mat;
    }

    public void LOGPOINTS(List<android.graphics.Point>points){

        Log.i("LOGPOINTS",""+points.size());

        for(android.graphics.Point point : points){
            Log.i("LOGPOINTS","X: "+point.x+" Y: "+point.y);
        }
    }

    public List<android.graphics.Point> getTotalNonZero(Mat mat){
        Log.i("getTotalNonZero","X: "+mat.width()+" Y: "+mat.height());
        List<android.graphics.Point>points = new ArrayList<>();
        for(int x = 1; x < mat.height(); x++)
            for(int y = 1; y < mat.width(); y++) {
                int pixelvalue = (int) mat.get(x,y)[0];
                if(pixelvalue != 0) {
                    points.add(new android.graphics.Point(x,y));
                }
            }
        return points;
    }

    public Bitmap toBinary(Bitmap bmpOriginal) {
        int width, height, threshold;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        threshold = 8;
        Bitmap bmpBinary = Bitmap.createBitmap(bmpOriginal);

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get one pixel color
                int pixel = bmpOriginal.getPixel(x, y);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                if(red!=0||green!=0||blue!=0){
                    bmpBinary.setPixel(x, y, Color.WHITE);
                } else{
                    bmpBinary.setPixel(x, y, Color.BLACK);

                }

            }
        }
        return bmpBinary;
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////voice offer ///////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////





    /////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////voice rec ///////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    public void checkIfSpeechRec_resultsContainsTheWord(String Word,ArrayList<String> SpeechRec_results){
        if(SpeechRec_results.size()>0){
            for(String i:SpeechRec_results){
                if(i==Word){
                    Toast.makeText(this,"Well Done!",Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("SpeechResults",i);
            }

        }
        Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show();

    }


    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// AI ///////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// Photo Help ///////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    public void showDialogwithMat(Mat matObject){
        Bitmap bitmap =Bitmap.createBitmap (matObject.width(), matObject.height(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(matObject,bitmap);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("شكرا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.layout_sample_pic_help, null);
        ImageView imageView = (ImageView) dialogLayout.findViewById(R.id.picsample);
        imageView.setImageBitmap(bitmap);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.show();
    }


    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// Utility ///////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    public static Bitmap getBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }


    public void SaveBitmap(){
        String filename ="typo";
        Bitmap mBitmap =null;
        mBitmap =  Bitmap.createBitmap (dv.getWidth(), dv.getHeight(), Bitmap.Config.RGB_565);;
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        File file = new File(filename + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, filename + ".png");
            Log.e("file exist", "" + file + ",Bitmap= " + filename);
        }
        try {
            // make a new bitmap from your file
            Bitmap bitmap = BitmapFactory.decodeFile(file.getName());

            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean prepareDirectory() {
        try
        {
            if (makedirs())
            {
                return true;
            } else {
                return false;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Could not initiate File System.. Is Sdcard mounted properly?", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private String getTodaysDate() {

        final Calendar c = Calendar.getInstance();
        int todaysDate =     (c.get(Calendar.YEAR) * 10000) +
                ((c.get(Calendar.MONTH) + 1) * 100) +
                (c.get(Calendar.DAY_OF_MONTH));
        Log.w("DATE:",String.valueOf(todaysDate));
        return(String.valueOf(todaysDate));

    }

    private String getCurrentTime() {

        final Calendar c = Calendar.getInstance();
        int currentTime =     (c.get(Calendar.HOUR_OF_DAY) * 10000) +
                (c.get(Calendar.MINUTE) * 100) +
                (c.get(Calendar.SECOND));
        Log.w("TIME:",String.valueOf(currentTime));
        return(String.valueOf(currentTime));

    }

    private boolean makedirs() {
        File tempdir = new File(tempDir);
        if (!tempdir.exists())
            tempdir.mkdirs();

        if (tempdir.isDirectory())
        {
            File[] files = tempdir.listFiles();
            for (File file : files)
            {
                if (!file.delete())
                {
                    System.out.println("Failed to delete " + file);
                }
            }
        }
        return (tempdir.isDirectory());
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }



}

