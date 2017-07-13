package com.seifmostafa.cchat.Recognizers;

import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;
import com.googlecode.javacv.cpp.opencv_imgproc;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

import static com.googlecode.javacv.cpp.opencv_contrib.createLBPHFaceRecognizer;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;

public class PersonRecognizer {

    public final static int MAXIMG = 10;
    public final static String TAG = "PersonRecognizer";
    static final int WIDTH = 147;
    static final int HEIGHT = 147;
    FaceRecognizer faceRecognizer;
    //String mPath;		// for training
    int count = 0;
    int[] labels;
    ;
    int num_components = 5;
    double threshold = 10.0;
    private int mProb = 999;


    PersonRecognizer() {
        faceRecognizer = createLBPHFaceRecognizer(2, 8, 8, 8, 200);
        // path=Environment.getExternalStorageDirectory()+"/facerecog/faces/";
        labels = new int[MAXIMG];
        //mPath=path;
    }

    public static int[] convertIntegers(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++) {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }

//	void add(Mat m, String description) {
//		Bitmap bmp= Bitmap.createBitmap(m.width(), m.height(), Bitmap.Config.ARGB_8888);
//
//		Utils.matToBitmap(m,bmp);
//		bmp= Bitmap.createScaledBitmap(bmp, WIDTH, HEIGHT, false);
//
//		FileOutputStream f;
//		try {
//			f = new FileOutputStream(mPath+description+"-"+count+".png",true);
//			count++;
//			bmp.compress(Bitmap.CompressFormat.PNG, 100, f);
//			f.close();
//
//		} catch (Exception e) {
//			Log.e("error",e.getCause()+" "+e.getMessage());
//			e.printStackTrace();
//
//		}
//	}

    void changeRecognizer(int nRec) {
        switch (nRec) {
            case 0:
                faceRecognizer = createLBPHFaceRecognizer(1, 8, 8, 8, 100);
                break;
            case 1:
                faceRecognizer = com.googlecode.javacv.cpp.opencv_contrib.createFisherFaceRecognizer();
                break;
            case 2:
                faceRecognizer = com.googlecode.javacv.cpp.opencv_contrib.createEigenFaceRecognizer(10, 123.0);
                break;
        }
        train();

    }

    public void train() {
        MatVector images = new MatVector(MAXIMG);
        int c = 0;
        IplImage grayImg;
        IplImage img;

        for (int i = 0; i < num_components; i++) {
            img = cvLoadImage("/storage/emulated/0/CCHAT/Ameen/" + (i + 1) + ".png");
            grayImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
            cvCvtColor(img, grayImg, CV_BGR2GRAY);
            images.put(c, grayImg);
            labels[c] = c++;
        }
        for (int i = 0; i < num_components; i++) {
            img = cvLoadImage("/storage/emulated/0/CCHAT/Nem/" + (i + 1) + ".png");
            grayImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
            cvCvtColor(img, grayImg, CV_BGR2GRAY);
            images.put(c, grayImg);
            labels[c] = c++;
        }
        // there is bool in 3rd , but don't know why !!
        faceRecognizer.train(images, labels);
    }

    public boolean canPredict() {
        if (labels.length > 1)
            return true;
        else
            return false;

    }

    public int predict(Mat m) {
        if (!canPredict()) {
            Log.i(TAG + "predict", ": Can't");
            return -1;
        }
        int n[] = new int[1];
        double p[] = new double[1];
        IplImage ipl = MatToIplImage(m, WIDTH, HEIGHT);
//		IplImage ipl = MatToIplImage(m,-1, -1);

        faceRecognizer.predict(ipl, n, p);

        if (n[0] != -1)
            mProb = (int) p[0];
        else
            mProb = -1;
        //	if ((n[0] != -1)&&(p[0]<95))
        if (n[0] != -1)
            return labels[n[0]];
        else
            return -1;
    }

    IplImage MatToIplImage(Mat m, int width, int heigth) {


        Bitmap bmp = Bitmap.createBitmap(m.width(), m.height(), Bitmap.Config.ARGB_8888);


        Utils.matToBitmap(m, bmp);
        return BitmapToIplImage(bmp, width, heigth);

    }

    IplImage BitmapToIplImage(Bitmap bmp, int width, int height) {

        if ((width != -1) || (height != -1)) {
            Bitmap bmp2 = Bitmap.createScaledBitmap(bmp, width, height, false);
            bmp = bmp2;
        }

        IplImage image = IplImage.create(bmp.getWidth(), bmp.getHeight(),
                IPL_DEPTH_8U, 4);

        bmp.copyPixelsToBuffer(image.getByteBuffer());

        IplImage grayImg = IplImage.create(image.width(), image.height(),
                IPL_DEPTH_8U, 1);

        cvCvtColor(image, grayImg, opencv_imgproc.CV_BGR2GRAY);

        return grayImg;
    }

    protected void SaveBmp(Bitmap bmp, String path) {
        FileOutputStream file;
        try {
            file = new FileOutputStream(path, true);

            bmp.compress(Bitmap.CompressFormat.JPEG, 100, file);
            file.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("", e.getMessage() + e.getCause());
            e.printStackTrace();
        }

    }

    public void load() {
        train();
    }

    public int getProb() {
        // TODO Auto-generated method stub
        return mProb;
    }

}
