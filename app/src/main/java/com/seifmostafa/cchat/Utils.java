package com.seifmostafa.cchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.objdetect.CascadeClassifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Stack;

import static android.graphics.BitmapFactory.decodeFile;
import static android.graphics.BitmapFactory.decodeStream;
import static com.seifmostafa.cchat.MainActivity.currentText;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.core.Mat.zeros;
import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.Canny;
import static org.opencv.imgproc.Imgproc.RETR_TREE;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.drawContours;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.moments;

/**
 * Created by azizax on 11/07/17.
 */

public class Utils {

    public static final int TOLERANCE = 8;
    public static final  int MAX_TOLERANCE = 200;
    public static File mCascadeFile;
    public static File mCascadeFileEye;
    public static CascadeClassifier mJavaDetector;
    public static CascadeClassifier mJavaDetectorEye;
    public static String[] mDetectorName;
    public static final String wordspath="";
    public static final String voicefolder="";
    public static final String photosfolder="";
    public static final String tempDir="";

    public static Bitmap getBitmapFromView(View v)  {
        Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }

    public static void  WriteBitmap(File mypath, Bitmap mBitmap,Context context) {
        try {
            FileOutputStream mFileOutStream = new FileOutputStream(mypath);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
            mFileOutStream.flush();
            mFileOutStream.close();
            String url = MediaStore.Images.Media.insertImage(context.getContentResolver(), mBitmap, "title", null);
            Log.v("log_tag", "url: " + url);
            Log.v("log_tag", "url: " + currentText);
            //Log.v("log_tag","url:REAL " + mypath);
            // In case you want to delete the file
            boolean deleted = mypath.delete();
            Log.v("log_tag", "deleted: " + mypath.toString() + deleted);
            //If you want to convert the image to string use base64 converter
        } catch (Exception e) {
            Log.i("WriteBitmap", e.toString());
        }

    }

    public static List<MatOfPoint> FindContour(Mat src) {
        int thresh = 100;
        int max_thresh = 255;
        Scalar color = new Scalar(100.0, 100.0, 100.0);

        Mat mat = new Mat();
        Mat canny_output = new Mat();
        src.convertTo(mat, CV_LOAD_IMAGE_GRAYSCALE);
        List<MatOfPoint> contours = new ArrayList<>();
        double[] val = new double[4];
        Canny(mat, canny_output, thresh, max_thresh, 3);
        Mat hierarchy = new Mat();
        findContours(canny_output, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE);

        // Log.i("SSSS",""+contours.size());
//        Mat drawing = zeros( canny_output.size(), CV_8UC3 );
//        Mat grayRnd = new Mat( canny_output.size(), CvType.CV_8U);
//        Core.randu(grayRnd, 0, 255);
//        for( int i = 0; i< contours.size(); i++ ) {
//            drawContours( drawing, contours, i, color, 2, 8, hierarchy, 0, new Point(0,0));
//        }

        return contours;
    }

    public static Mat getMatFromView(View view) {
        Bitmap mBitmap = null;
        mBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(mBitmap);
        view.draw(canvas);
        try {
            Mat mat = new Mat(mBitmap.getHeight(), mBitmap.getWidth(), CV_8UC3);
            org.opencv.android.Utils.bitmapToMat(mBitmap, mat);
            return mat;
        } catch (Exception e) {
            Log.v("getMatFromView", e.toString());
            return null;
        }
    }
    public static Bitmap getBitmapFromMat(Mat mat){
        Bitmap bmp = null;
        Mat tmp = new Mat (mat.cols(), mat.rows(), CvType.CV_8U, new Scalar(4));
        try {
            Imgproc.cvtColor(mat, tmp, Imgproc.COLOR_GRAY2RGBA, 4);
            bmp = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
            org.opencv.android.Utils.matToBitmap(tmp, bmp);
        }
        catch (CvException e){Log.d("Exception",e.getMessage());}
        return bmp;
    }

    public static Mat DrawCenterPoint(Mat src) {
        int thresh = 100;
        int max_thresh = 255;
        Scalar color = new Scalar(255, 255, 255);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat canny_output = new Mat();
        Mat hierarchy = new Mat();

        Canny(src, canny_output, thresh, thresh * 2, 3);
        Mat drawing = zeros(canny_output.size(), CV_8UC3);
        findContours(canny_output, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE, new Point(0, 0));
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint matOfPoint = contours.get(i);
            Moments m = moments(matOfPoint);
            int x = (int) (m.get_m10() / m.get_m00());
            int y = (int) (m.get_m01() / m.get_m00());
            drawContours(drawing, contours, -1, color, 2);
            circle(drawing, new Point(x, y), 7, color);
            // putText(drawing,textView.getText().toString(),new Point(x-20 ,y-20),7,7,color);

        }
        return drawing;
    }

    public static Mat RGBtoBINARY(Bitmap mBitmap) {
        Mat mat = new Mat(mBitmap.getHeight(), mBitmap.getWidth(), CV_8UC1);
        org.opencv.android.Utils.bitmapToMat(mBitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2RGBA, 4);
        Bitmap bitmap = Bitmap.createBitmap(mBitmap);
        org.opencv.android.Utils.matToBitmap(mat, bitmap);
        Mat result = new Mat(mBitmap.getHeight(), mBitmap.getWidth(), CV_8UC3);
        org.opencv.android.Utils.bitmapToMat(bitmap, result);
        for (int x = 0; x < result.height(); ++x) {
            for (int y = 0; y < result.width(); ++y) {
                // get one pixel color
                int red = (int) result.get(x, y)[0];
                int green = (int) result.get(x, y)[1];
                int blue = (int) result.get(x, y)[2];

                if (red != 0 || green != 0 || blue != 0) {
                    result.put(x, y, new double[]{255.0, 255.0, 255.0, 0});
                } else {
                    result.put(x, y, new double[]{0, 0, 0, 0});
                    //Log.i("RGBtoBINARY", "R: "+red+" G: "+green+" B: "+blue);
                }
            }
        }
        return result;
    }

    public static int[][] Mat22D(Mat mat) {
        int[][] result = new int[mat.rows()][mat.cols()];
        for (int i = 0; i < mat.rows(); i++) {
            for (int j = 0; j < mat.cols(); j++) {
                if ((int) mat.get(i, j)[0] == 255 && (int) mat.get(i, j)[1] == 255 && (int) mat.get(i, j)[2] == 255) {
                    result[i][j] = 1;
                } else result[i][j] = 0;
            }
        }
        return result;
    }

    public static Mat TwoD2Mat(int[][] source) {
        Mat mat = new Mat(source.length, source[0].length, CV_8UC1);
        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < source[i].length; j++) {
                if (source[i][j] == 1) {
                    mat.put(i, j, new double[]{255, 255, 255});
                } else mat.put(i, j, new double[]{0, 0, 0});
            }
        }
        Log.i("TwoD2Mat", "Finished");
        return mat;
    }

    public static void LOGPOINTS(List<android.graphics.Point> points) {

        Log.i("LOGPOINTS", "" + points.size());

        for (android.graphics.Point point : points) {
            Log.i("LOGPOINTS", "X: " + point.x + " Y: " + point.y);
        }
    }

    public static List<android.graphics.Point> getTotalNonZero(Mat mat) {
        Log.i("getTotalNonZero", "X: " + mat.width() + " Y: " + mat.height());
        List<android.graphics.Point> points = new ArrayList<>();
        for (int x = 1; x < mat.width(); x++)
            for (int y = 1; y < mat.height(); y++) {
                int pixelvalue = (int) mat.get(y, x)[0];
                if (pixelvalue != 0) {
                    points.add(new android.graphics.Point(y, x));
                }
            }
        return points;
    }

    public static Bitmap toBinary(Bitmap bmpOriginal) {
        int width, height, threshold;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        threshold = 8;
        Bitmap bmpBinary = Bitmap.createBitmap(bmpOriginal);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get one pixel color
                int pixel = bmpOriginal.getPixel(x, y);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                if (red != 0 || green != 0 || blue != 0) {
                    bmpBinary.setPixel(x, y, Color.WHITE);
                } else {
                    bmpBinary.setPixel(x, y, Color.BLACK);

                }

            }
        }
        return bmpBinary;
    }

    public static void showDialogwithMat(Mat matObject,Activity activity) {
        Bitmap bitmap = Bitmap.createBitmap(matObject.width(), matObject.height(), Bitmap.Config.RGB_565);
        org.opencv.android.Utils.matToBitmap(matObject, bitmap);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setPositiveButton("شكرا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.layout_sample_pic_help, null);
        ImageView imageView = (ImageView) dialogLayout.findViewById(R.id.picsample);
        imageView.setImageBitmap(bitmap);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void SaveBitmap(Checker.DrawingView dv) {
        String filename = "typo";
        Bitmap mBitmap = null;
        mBitmap = Bitmap.createBitmap(dv.getWidth(), dv.getHeight(), Bitmap.Config.RGB_565);

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
            Bitmap bitmap = decodeFile(file.getName());

            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean prepareDirectory(Activity activity) {
        try {
            if (makedirs()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Could not initiate File System.. Is Sdcard mounted properly?", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static String getCurrentTime() {

        final Calendar c = Calendar.getInstance();
        int currentTime = (c.get(Calendar.HOUR_OF_DAY) * 10000) +
                (c.get(Calendar.MINUTE) * 100) +
                (c.get(Calendar.SECOND));
        Log.w("TIME:", String.valueOf(currentTime));
        return (String.valueOf(currentTime));

    }

    public static boolean makedirs() {
        File tempdir = new File(tempDir);
        if (!tempdir.exists())
            tempdir.mkdirs();

        if (tempdir.isDirectory()) {
            File[] files = tempdir.listFiles();
            for (File file : files) {
                if (!file.delete()) {
                    System.out.println("Failed to delete " + file);
                }
            }
        }
        return (tempdir.isDirectory());
    }

    public static void sortwords(List<String> arr_items) {

        // give path of folders and sort the inside folders by their names
        Locale lithuanian = new Locale("ar");
        Collator lithuanianCollator = Collator.getInstance(lithuanian);
        Collections.sort(arr_items, lithuanianCollator);
    }

    public static Stack<String> GenerateDummy() {
        Stack<String> dummy = new Stack<>();
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

    public static void LOGSTRINGARRAY(ArrayList<String> list) {
        for (String s : list) {
            Log.i("LOGSTRINGARRAY", s);
        }
    }

    public static  String getTodaysDate() {

        final Calendar c = Calendar.getInstance();
        int todaysDate = (c.get(Calendar.YEAR) * 10000) +
                ((c.get(Calendar.MONTH) + 1) * 100) +
                (c.get(Calendar.DAY_OF_MONTH));
        Log.w("DATE:", String.valueOf(todaysDate));
        return (String.valueOf(todaysDate));

    }

    public static void writeStringToFile(String data,Context context,String filepath) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filepath, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String readFileintoString(Context context,String filepath) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filepath);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static void writeStackTofile(Stack<String> result_words,String filepath) {
        try {
            PrintWriter writer = new PrintWriter(filepath, "UTF-8");
            for(String s:result_words){
                writer.println(s);
            }
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Stack<String>  readfileintoStack(String filepath){

        File file = new File(filepath);
        Stack<String> words = new Stack<>();
        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine())
            {
                String line =null;
                line = scan.nextLine();
                words.push(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
        return words;
    }

    public static void  SaveOnSharedPref(SharedPreferences.Editor editor,String key, String value) {
        editor.putString(key, value).apply();
        editor.commit();
    }

    public static Mat FormSexyMat(int stage, Mat org){
        Mat dest=org.clone();
        /*
        v.i variable:
            stage1/2/3,mPaint.strokesize28
         */
        for (int x = 1; x < org.width(); x++) {
            int CounterToBeSkipped=stage*7;

            for (int y = 1; y < org.height(); y++) {
                int pixelvalue = (int) org.get(y, x)[0];
                if (pixelvalue != 0) {
                    if (CounterToBeSkipped == 0) {
                        Log.i("CounterToBeSkipped","notZ");

                    } else {
                        dest.put(y, x, new double[]{0, 0, 0,0});
                        CounterToBeSkipped--;
                    }
                }
            }
        }
        return dest;
    }

    public static boolean IspointSexy(int x,int y,Mat mat){
        if(mat.get(y,x)!=null) return  (mat.get(y,x)[0] != 0);
        else return false;
    }
}
