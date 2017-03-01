package com.seifmostafa.cchat;

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
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.javacv.cpp.opencv_contrib;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Collator;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import static com.googlecode.javacv.cpp.opencv_contrib.createFisherFaceRecognizer;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static java.lang.Math.abs;
import static org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import static org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.core.Mat.zeros;
import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.Canny;
import static org.opencv.imgproc.Imgproc.INTER_CUBIC;
import static org.opencv.imgproc.Imgproc.RETR_TREE;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.drawContours;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.moments;
import static org.opencv.imgproc.Imgproc.resize;


public class MainActivity extends Activity {

    private static final String    TAG                 = "OCVSample::Activity";
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    public static final int        JAVA_DETECTOR       = 0;
    private static final int TM_SQDIFF = 0;
    private static final int TM_SQDIFF_NORMED = 1;
    private static final int TM_CCOEFF = 2;
    private static final int TM_CCOEFF_NORMED = 3;
    private static final int TM_CCORR = 4;
    private static final int TM_CCORR_NORMED = 5;


    private int learn_frames = 0;
    private Mat teplateR;
    private Mat teplateL;
    int method = 0;

    // matrix for zooming
    private Mat mZoomWindow;
    private Mat mZoomWindow2;

    private MenuItem mItemFace50;
    private MenuItem               mItemFace40;
    private MenuItem               mItemFace30;
    private MenuItem               mItemFace20;
    // private MenuItem               mItemType;

    private Mat                    mRgba;
    private Mat                    mGray;
    private File                   mCascadeFile;
    private File                   mCascadeFileEye;
    private CascadeClassifier mJavaDetector;
    private CascadeClassifier      mJavaDetectorEye;


    private int                    mDetectorType       = JAVA_DETECTOR;
    private String[]               mDetectorName;

    private float                  mRelativeFaceSize   = 0.2f;
    private int mAbsoluteFaceSize = 0;

    private CameraBridgeViewBase mOpenCvCameraView;
    Queue<Mat> MatsOfFaces ;
    int num_components = 5;
    double threshold = 10.0;
    MatVector images;
    int[] labels;


    double xCenter = -1;
    double yCenter = -1;

    Mat  Original;
    Mat imageMat;
    Mat matObject ;

    public int count = 1;
    public int TOLERANCE =8;
    public int MAX_TOLERANCE =200;

    public String current = null;
    private String uniqueId;
    private static final int MY_DATA_CHECK_CODE = 0;
    private static final int RESULT_SPEECH = 1;


    ArrayList<String > SpeechRec_results;
    FrameLayout frameLayout;
    DrawingView dv ;
    private Paint mPaint;
    public static String tempDir;
    File mypath;

    TextView textViewPercentage;
    CustTextView textView;
    AlertDialog dialog;
    static {
        OpenCVLoader.initDebug();
    }

    opencv_contrib.FaceRecognizer faceRecognizer;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {

                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    imageMat=new Mat();
                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        // load cascade file from application resources
                        InputStream ise = getResources().openRawResource(R.raw.haarcascade_lefteye_2splits);
                        File cascadeDirEye = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFileEye = new File(cascadeDirEye, "haarcascade_lefteye_2splits.xml");
                        FileOutputStream ose = new FileOutputStream(mCascadeFileEye);

                        while ((bytesRead = ise.read(buffer)) != -1) {
                            ose.write(buffer, 0, bytesRead);
                        }
                        ise.close();
                        ose.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        mJavaDetectorEye = new CascadeClassifier(mCascadeFileEye.getAbsolutePath());
                        if (mJavaDetectorEye.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier for eye");
                            mJavaDetectorEye = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFileEye.getAbsolutePath());

                        cascadeDir.delete();
                        cascadeDirEye.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MatsOfFaces = new ArrayDeque<Mat>();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        tempDir = Environment.getExternalStorageDirectory() + "/" + "GetSignature" + "/";
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("GetSignature", Context.MODE_PRIVATE);
        prepareDirectory();
        uniqueId = getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random();
        current = uniqueId + ".png";
        mypath= new File(directory,current);
        // Log.v("ABSPATH" , mypath.getAbsolutePath());

        textViewPercentage =(TextView)findViewById(R.id.textView_percentage);
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
        mPaint.setStrokeWidth(28);
        frameLayout.addView(dv);


//        try {
//            faceRecognizer  = createFisherFaceRecognizer(num_components*2, threshold);
//            images= new MatVector(num_components*2);
//            labels= new int[num_components*2];
//           // setupFaceRec();
//            Log.i("setupFaceRec","Foll+++  "+faceRecognizer.name());
//          //  capPhotoandRec();
//        } catch (Exception e) {
//            Log.i("setupFaceRec",e.toString());
//        }
//        //faceRec();
        startActivity(new Intent(MainActivity.this,FdActivity.class));
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
        Bitmap mBitmap = null;
        mBitmap =  Bitmap.createBitmap (dv.getWidth(), dv.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(mBitmap);
        dv.draw(canvas);

        try
        {
            Original = new CenterLineForOriginal().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,getBitmapFromView(textView)).get();
            Mat Drawed = new CenterLine().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mBitmap).get();
            double FinalPercentage =   MatchWritten(Original,Drawed,TOLERANCE);
            textViewPercentage.setText(""+Math.floor(FinalPercentage+'%'));
            textViewPercentage.setVisibility(View.VISIBLE);
            Log.i("FinalPercentage",""+FinalPercentage);
            //MatchWritten_(Original,Drawed);

        }
        catch(Exception e)
        {
            Log.v("checkfill", e.toString());
        }
    }
    private void invisiblepercentage(){
                try{
            Thread.sleep(5000);
            textViewPercentage.setVisibility(View.INVISIBLE);
        }catch (Exception e){
            Log.i("checkfill-try_to_sleep",e.toString());
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
//    public void faceRec(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        dialog  = builder.create();
//        LayoutInflater inflater = getLayoutInflater();
//        View dialogLayout = inflater.inflate(R.layout.face_detect_surface_view, null);
//
//        mOpenCvCameraView = (CameraBridgeViewBase) dialogLayout.findViewById(R.id.fd_activity_surface_view);
//        mOpenCvCameraView.setCvCameraViewListener(this);
//        mOpenCvCameraView.enableFpsMeter();
//        mOpenCvCameraView.setCameraIndex(1);
//        mOpenCvCameraView.enableView();
//
//        dialog.setView(dialogLayout);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//        dialog.show();
//    }
//    public void onRecreateClick(View view) throws InterruptedException {
//        dialog.dismiss();
//        Thread.sleep(5000);
////
////        IplImage tmp =cvLoadImage("/storage/emulated/0/CCHAT/live.pgm");
////        IplImage grayImg = IplImage.create(tmp.width(), tmp.height(), IPL_DEPTH_8U, 1);
////        cvCvtColor(tmp, grayImg, CV_BGR2GRAY);
////         //   Log.i("onRecreateClick",""+grayImg.height()+","+grayImg.width());
////        grayImg = grayImg.clone();
////        CvMat toBePredicted = new CvMat();
////            int notRec = faceRecognizer.predict(grayImg);
////            if(notRec!=-1){
////                Log.i("checkFaces:","NotRec");
////                ReadFaces();
////            }else stopReadingFaces();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_SPEECH && requestCode == RESULT_OK);
        {
            SpeechRec_results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// check type ///////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////



    public double MatchWritten(Mat original,Mat written,int tolerance) {
        double percentage =0;
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

            int C=0,BigMistakesCounter=0;

           for(android.graphics.Point point:Originalpoints){
            //   Log.i("MatchWritten","OriginalPoint: "+ point.x +" , "+ point.y);
               nextpoint:
               for(android.graphics.Point searchpoint:Writtenpoints){
                   if(abs(searchpoint.x-point.x)<= tolerance && abs(searchpoint.y-point.y)<= tolerance){
                       Original_copy.remove(point);
                       Writtern_copy.remove(searchpoint);
                    C++;
                       // enter multple times for single one !!! (cause of the nested loop) so let's
                       break nextpoint;

                   }
                   if(abs(searchpoint.x-point.x)>= MAX_TOLERANCE && abs(searchpoint.y-point.y)>= MAX_TOLERANCE){
                       BigMistakesCounter++;
                       break nextpoint;

                   }
               }
           }
           // scalling
           // original , C , bigMistakesCounter
            double okay = (double)C/Writtenpoints.size(),notOkay = (double)BigMistakesCounter/Writtenpoints.size();

            Log.i("MatchWritten","Mistakes:"+notOkay);
            Log.i("MatchWritten","Okay:"+okay);

           percentage=okay*100;

           if((notOkay*100)>=25){
               Log.i("MatchWritten","hola big wrong");
               // test toast display arabic
               Toast.makeText(MainActivity.this,"Try again",Toast.LENGTH_LONG).show();
               dv.reset();
           }else{
               // test toast display arabic
               Toast.makeText(MainActivity.this,"Thanks",Toast.LENGTH_LONG).show();
           }
       }catch (Exception e){
           Log.i("checkTypo",e.toString());
       }
        return percentage;
    }


    // we can write or write then delete here
    private void WriteBitmap(File mypath,Bitmap mBitmap)  {
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

    private   List<MatOfPoint>  FindContour(Mat src){
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

    private Mat getMatFromView(View view){
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
        public void reset(){
            mBitmap.recycle();
            mBitmap = Bitmap.createBitmap(this.mBitmap.getWidth(), this.mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
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
            Log.i("CenterLine","onPostExecute");

            super.onPostExecute(mat);
        }


    }
    public class CenterLineForOriginal extends AsyncTask<Bitmap,Void,Mat>{
        @Override
        protected Mat doInBackground(Bitmap... params) {
            Log.i("CenterLineForOriginal","doInBackground");
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
            Log.i("CenterLineForOriginal","onPostExecute");
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
    public void retry(View view){
        dv.reset();
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

    private String[] GenerateName_Characters(String name){
        String[] Name_Characters = name.split("");
        return Name_Characters;
    }

    private class TreeGenerator extends AsyncTask<String,Void,ArrayList<String> >{

       public int NumberOfWordsToBeTeached;

        public TreeGenerator(int numberOfWordsToBeTeached) {
            NumberOfWordsToBeTeached = numberOfWordsToBeTeached;
        }

        private ArrayList<String> GenerateWordsTree(String Name, int number_of_words){
            int levels=0;
            int added=0;

            ArrayList<String> tree = new ArrayList<>();
            ArrayList<String> AvailableWords = GenerateDummy();
            sortfolders(AvailableWords);
            tree.add(Name);
            added++;
            while (number_of_words>tree.size()){
                for(int i=0;i<added;i++){
                    added=0;
                    String []Word_characters =GenerateName_Characters(tree.get((i)+levels));
                    for(String CH:Word_characters){
                        for(String searchword:AvailableWords){
                            if(searchword.substring(0,1).equals(CH)){
                                tree.add(searchword);
                                added++;
                                AvailableWords.remove(searchword);
                                break;
                            }
                        }
                    }
                }
                levels++;
            }
            return tree;
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            return GenerateWordsTree(params[0],NumberOfWordsToBeTeached);
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
        }
    }









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
        Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
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

   private void sortfolders(List <String>arr_items){

       // give path of folders and sort the inside folders by their names
       Locale lithuanian = new Locale("ar");
       Collator lithuanianCollator = Collator.getInstance(lithuanian);
       Collections.sort(arr_items,lithuanianCollator);
   }

    private ArrayList<String> GenerateDummy(){
        ArrayList<String> dummy = new ArrayList<>();
        dummy.add("من");
        dummy.add("في");
        dummy.add("عن");
        dummy.add("على");
        dummy.add("اب");
        dummy.add("ابن");
        dummy.add("عم");
        dummy.add("خال");
        dummy.add("خالة");
        dummy.add("جد");
        dummy.add("ام");
        dummy.add("صديق");
        dummy.add("صبر");
        dummy.add("سور");
        dummy.add("صبور");
        dummy.add("فوق");
        dummy.add("أسبوع");
        dummy.add("سنة");
        dummy.add("اليوم");
        dummy.add("أمس");
        dummy.add("ثانية");
        dummy.add("ساعة");
        dummy.add("دقيقة");
        dummy.add("قام");
        dummy.add("ذهب");
        dummy.add("يضحك");
        dummy.add("جيد");
        dummy.add("قبيح");
        dummy.add("صعب");
        dummy.add("سهل");
        dummy.add("سييء");
        dummy.add("مرحباً");
        dummy.add("شكرا");
        dummy.add("لا");
        dummy.add("يناير");
        dummy.add("قهوة");
        dummy.add("خَرُوْف");
        dummy.add("مبرمج");
        dummy.add("خَرُوْف");
        dummy.add("دهب");
        dummy.add("نون");
        dummy.add("برد");
        dummy.add("قلب");
        dummy.add("براد");
        dummy.add("يرد");
        dummy.add("حب");
        dummy.add("حرف");
        dummy.add("حرق");
        dummy.add("لو");
        dummy.add("ولد");
        dummy.add("لبن");
        dummy.add("برق");
        dummy.add("جاموسة");
        dummy.add("سوس");
        dummy.add("من");
        dummy.add("في");
        dummy.add("عن");
        dummy.add("على");
        dummy.add("اب");
        dummy.add("ابن");
        dummy.add("عم");
        dummy.add("خال");
        dummy.add("خالة");
        dummy.add("جد");
        dummy.add("ام");
        dummy.add("صديق");
        dummy.add("صبر");
        dummy.add("سور");
        dummy.add("صبور");
        dummy.add("فوق");
        dummy.add("أسبوع");
        dummy.add("سنة");
        dummy.add("اليوم");
        dummy.add("أمس");
        dummy.add("ثانية");
        dummy.add("ساعة");
        dummy.add("دقيقة");
        dummy.add("قام");
        dummy.add("ذهب");
        dummy.add("يضحك");
        dummy.add("جيد");
        dummy.add("قبيح");
        dummy.add("صعب");
        dummy.add("سهل");
        dummy.add("سييء");
        dummy.add("مرحباً");
        dummy.add("شكرا");
        dummy.add("لا");
        dummy.add("يناير");
        dummy.add("قهوة");
        dummy.add("خَرُوْف");
        dummy.add("مبرمج");
        dummy.add("خَرُوْف");
        dummy.add("دهب");
        dummy.add("نون");
        dummy.add("برد");
        dummy.add("قلب");
        dummy.add("براد");
        dummy.add("يرد");
        dummy.add("حب");
        dummy.add("حرف");
        dummy.add("حرق");
        dummy.add("لو");
        dummy.add("ولد");
        dummy.add("لبن");
        dummy.add("برق");
        dummy.add("جاموسة");
        dummy.add("سوس");
        return dummy;
    }

    private void LOGSTRINGARRAY(ArrayList<String>list){
        for(String s:list){
            Log.i("LOGSTRINGARRAY",s);
        }
    }

//    public void setupFaceRec() throws IOException {
//        int c=0;
//        IplImage grayImg;
//        IplImage img;
//
//        for(int i=0;i<num_components;i++){
//            img = cvLoadImage("/storage/emulated/0/CCHAT/Ameen/"+(i+1)+".png");
//            grayImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
//            cvCvtColor(img, grayImg, CV_BGR2GRAY);
//            images.put(c,grayImg); labels[c]=(c++);
//        }
//        for(int i=0;i<num_components;i++){
//            img = cvLoadImage("/storage/emulated/0/CCHAT/Nemqi/"+(i+1)+".png");
//            grayImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
//            cvCvtColor(img, grayImg, CV_BGR2GRAY);
//            images.put(c,grayImg); labels[c]=(c++);
//        }
//        faceRecognizer.train(images,labels);
//    }



    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
//    private void setMinFaceSize(float faceSize) {
//        mRelativeFaceSize = faceSize;
//        mAbsoluteFaceSize = 0;
//    }
//
//    private void CreateAuxiliaryMats() {
//        if (mGray.empty())
//            return;
//
//        int rows = mGray.rows();
//        int cols = mGray.cols();
//
//        if (mZoomWindow == null) {
//            mZoomWindow = mRgba.submat(rows / 2 + rows / 10, rows, cols / 2
//                    + cols / 10, cols);
//            mZoomWindow2 = mRgba.submat(0, rows / 2 - rows / 10, cols / 2
//                    + cols / 10, cols);
//        }
//
//    }
//
//    private void match_eye(Rect area, Mat mTemplate, int type) {
//        Point matchLoc;
//        Mat mROI = mGray.submat(area);
//        int result_cols = mROI.cols() - mTemplate.cols() + 1;
//        int result_rows = mROI.rows() - mTemplate.rows() + 1;
//        // Check for bad template size
//        if (mTemplate.cols() == 0 || mTemplate.rows() == 0) {
//            return ;
//        }
//        Mat mResult = new Mat(result_cols, result_rows, CvType.CV_8U);
//
//        switch (type) {
//            case TM_SQDIFF:
//                Imgproc.matchTemplate(mROI, mTemplate, mResult, Imgproc.TM_SQDIFF);
//                break;
//            case TM_SQDIFF_NORMED:
//                Imgproc.matchTemplate(mROI, mTemplate, mResult,
//                        Imgproc.TM_SQDIFF_NORMED);
//                break;
//            case TM_CCOEFF:
//                Imgproc.matchTemplate(mROI, mTemplate, mResult, Imgproc.TM_CCOEFF);
//                break;
//            case TM_CCOEFF_NORMED:
//                Imgproc.matchTemplate(mROI, mTemplate, mResult,
//                        Imgproc.TM_CCOEFF_NORMED);
//                break;
//            case TM_CCORR:
//                Imgproc.matchTemplate(mROI, mTemplate, mResult, Imgproc.TM_CCORR);
//                break;
//            case TM_CCORR_NORMED:
//                Imgproc.matchTemplate(mROI, mTemplate, mResult,
//                        Imgproc.TM_CCORR_NORMED);
//                break;
//        }
//
//        Core.MinMaxLocResult mmres = Core.minMaxLoc(mResult);
//        // there is difference in matching methods - best match is max/min value
//        if (type == TM_SQDIFF || type == TM_SQDIFF_NORMED) {
//            matchLoc = mmres.minLoc;
//        } else {
//            matchLoc = mmres.maxLoc;
//        }
//
//        Point matchLoc_tx = new Point(matchLoc.x + area.x, matchLoc.y + area.y);
//        Point matchLoc_ty = new Point(matchLoc.x + mTemplate.cols() + area.x,
//                matchLoc.y + mTemplate.rows() + area.y);
//
//        Imgproc.rectangle(mRgba, matchLoc_tx, matchLoc_ty, new Scalar(255, 255, 0,
//                255));
//        Rect rec = new Rect(matchLoc_tx,matchLoc_ty);
//
//
//    }
//
//    private Mat get_template(CascadeClassifier clasificator, Rect area, int size) {
//        Mat template = new Mat();
//        Mat mROI = mGray.submat(area);
//        MatOfRect eyes = new MatOfRect();
//        Point iris = new Point();
//        Rect eye_template = new Rect();
//        clasificator.detectMultiScale(mROI, eyes, 1.15, 2,
//                Objdetect.CASCADE_FIND_BIGGEST_OBJECT
//                        | Objdetect.CASCADE_SCALE_IMAGE, new Size(30, 30),
//                new Size());
//
//        Rect[] eyesArray = eyes.toArray();
//        for (int i = 0; i < eyesArray.length;) {
//            Rect e = eyesArray[i];
//            e.x = area.x + e.x;
//            e.y = area.y + e.y;
//            Rect eye_only_rectangle = new Rect((int) e.tl().x,
//                    (int) (e.tl().y + e.height * 0.4), (int) e.width,
//                    (int) (e.height * 0.6));
//            mROI = mGray.submat(eye_only_rectangle);
//            Mat vyrez = mRgba.submat(eye_only_rectangle);
//
//
//            Core.MinMaxLocResult mmG = Core.minMaxLoc(mROI);
//
//            Imgproc.circle(vyrez, mmG.minLoc, 2, new Scalar(255, 255, 255, 255), 2);
//            iris.x = mmG.minLoc.x + eye_only_rectangle.x;
//            iris.y = mmG.minLoc.y + eye_only_rectangle.y;
//            eye_template = new Rect((int) iris.x - size / 2, (int) iris.y
//                    - size / 2, size, size);
//            Imgproc.rectangle(mRgba, eye_template.tl(), eye_template.br(),
//                    new Scalar(255, 0, 0, 255), 2);
//            template = (mGray.submat(eye_template)).clone();
//            return template;
//        }
//        return template;
//    }
//
//    public void onCameraViewStarted(int width, int height) {
//        mGray = new Mat();
//        mRgba = new Mat();
//    }
//
//    public void onCameraViewStopped() {
//        mGray.release();
//        mRgba.release();
////        mZoomWindow.release();
////        mZoomWindow2.release();
//    }
//
//     public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
//
//         mRgba = inputFrame.rgba();
//        mGray = inputFrame.gray();
//
//        if (mAbsoluteFaceSize == 0) {
//            int height = mGray.rows();
//            if (Math.round(height * mRelativeFaceSize) > 0) {
//                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
//            }
//
//        }
//
//        if (mZoomWindow == null || mZoomWindow2 == null)
//            CreateAuxiliaryMats();
//
//        MatOfRect faces = new MatOfRect();
//
//        if (mDetectorType == JAVA_DETECTOR) {
//            if (mJavaDetector != null)
//                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
//                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
//        }
//        else {
//            Log.e(TAG, "Detection method is not selected!");
//        }
//
//        Rect[] facesArray = faces.toArray();
//      //   addFaces(facesArray,mGray);
//        for (int i = 0; i < facesArray.length; i++)
//        {	Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(),
//                FACE_RECT_COLOR, 3);
//            xCenter = (facesArray[i].x + facesArray[i].width + facesArray[i].x) / 2;
//            yCenter = (facesArray[i].y + facesArray[i].y + facesArray[i].height) / 2;
//            Point center = new Point(xCenter, yCenter);
//
//            Imgproc.circle(mRgba, center, 10, new Scalar(255, 0, 0, 255), 3);
//
//            Imgproc.putText(mRgba, "[" + center.x + "," + center.y + "]",
//                    new Point(center.x + 20, center.y + 20),
//                    Core.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(255, 255, 255,
//                            255));
//
//            Rect r = facesArray[i];
//            // compute the eye area
//            Rect eyearea = new Rect(r.x + r.width / 8,
//                    (int) (r.y + (r.height / 4.5)), r.width - 2 * r.width / 8,
//                    (int) (r.height / 3.0));
//            // split it
//            Rect eyearea_right = new Rect(r.x + r.width / 16,
//                    (int) (r.y + (r.height / 4.5)),
//                    (r.width - 2 * r.width / 16) / 2, (int) (r.height / 3.0));
//            Rect eyearea_left = new Rect(r.x + r.width / 16
//                    + (r.width - 2 * r.width / 16) / 2,
//                    (int) (r.y + (r.height / 4.5)),
//                    (r.width - 2 * r.width / 16) / 2, (int) (r.height / 3.0));
//            // draw the area - mGray is working grayscale mat, if you want to
//            // see area in rgb preview, change mGray to mRgba
//            Imgproc.rectangle(mRgba, eyearea_left.tl(), eyearea_left.br(),
//                    new Scalar(255, 0, 0, 255), 2);
//            Imgproc.rectangle(mRgba, eyearea_right.tl(), eyearea_right.br(),
//                    new Scalar(255, 0, 0, 255), 2);
//
//            if (learn_frames < 5) {
//                teplateR = get_template(mJavaDetectorEye, eyearea_right, 24);
//                teplateL = get_template(mJavaDetectorEye, eyearea_left, 24);
//                learn_frames++;
//            } else {
//                // Learning finished, use the new templates for template
//                // matching
//                match_eye(eyearea_right, teplateR, method);
//                match_eye(eyearea_left, teplateL, method);
//
//            }
//
//
//            // cut eye areas and put them to zoom windows
////            Imgproc.resize(mRgba.submat(eyearea_left), mZoomWindow2,
////                    mZoomWindow2.size());
////            Imgproc.resize(mRgba.submat(eyearea_right), mZoomWindow,
////                    mZoomWindow.size());
//
//          // Log.i("capPhotoandRec",""+facesArray[i].width+","+facesArray[i].height);
//            IplImage img = cvLoadImage("/storage/emulated/0/CCHAT/Ameen/1.png");
//            Mat tobeResized = new Mat(mGray,facesArray[i]);
//            CvMat resizeimage = new CvMat();
//            Log.i("addFaces","imgSize"+ img.width()+""+img.height());
//            Size sz = new Size(img.width(),img.height());
//            IplImage iplImage = IplImage.create( img.width(),img.height(),IPL_DEPTH_8U,1 );
//
//            cvReshape( iplImage, resizeimage, 0,0);
//            //Log.i("onCameraFrame:Prediction",predict(resizeimage,img.width(),img.height()));
//            Log.i("onCameraFrame:Prediction",""+faceRecognizer.predict(resizeimage.asIplImage()));
//            //imwrite( "/storage/emulated/0/CCHAT/live.pgm",resizeimage);
//        }
//         return mGray;
//    }
//
//    public String predict(Mat m,int WIDTH,int HEIGHT) {
//
//        int n[] = new int[1];
//        double p[] = new double[1];
//        IplImage ipl = MatToIplImage(m,WIDTH, HEIGHT);
////		IplImage ipl = MatToIplImage(m,-1, -1);
//
//        faceRecognizer.predict(ipl, n, p);
//        if (n[0]!=-1){
//            Log.i("predict","Result: "+n[0]);
//           // mProb=(int)p[0];
//        } else{
//            Log.i("predict","Result: null");
//            //mProb=-1;
//
//        }
//        //	if ((n[0] != -1)&&(p[0]<95))
//        if (n[0] != -1)
//            return ""+labels[n[0]];
//        else
//            return "Unkown";
//    }
//    IplImage MatToIplImage(Mat m,int width,int heigth) {
//
//
//        Bitmap bmp=Bitmap.createBitmap(m.width(), m.height(), Bitmap.Config.ARGB_8888);
//
//
//        Utils.matToBitmap(m, bmp);
//        return BitmapToIplImage(bmp,width, heigth);
//
//    }
//    IplImage BitmapToIplImage(Bitmap bmp, int width, int height) {
//
//        if ((width != -1) || (height != -1)) {
//            Bitmap bmp2 = Bitmap.createScaledBitmap(bmp, width, height, false);
//            bmp = bmp2;
//        }
//
//        IplImage image = IplImage.create(bmp.getWidth(), bmp.getHeight(),
//                IPL_DEPTH_8U, 4);
//
//        bmp.copyPixelsToBuffer(image.getByteBuffer());
//
//        IplImage grayImg = IplImage.create(image.width(), image.height(),
//                IPL_DEPTH_8U, 1);
//
//        cvCvtColor(image, grayImg, opencv_imgproc.CV_BGR2GRAY);
//
//        return grayImg;
//    }
//
//
//    public void stopReadingFaces(){
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mOpenCvCameraView.setVisibility(View.INVISIBLE);
//            }
//        });
//    }
//    public void ReadFaces(){
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mOpenCvCameraView.setVisibility(View.VISIBLE);
//            }
//        });
//    }
}

