package com.example.seif.seshatplayer.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.seif.seshatplayer.MainActivity;
import com.example.seif.seshatplayer.R;
import com.example.seif.seshatplayer.model.Word;

import java.io.File;


public class MainFragment extends Fragment {

    ImageButton helpiBtn, PreviBtn, NextiBtn, PlaySoundiBtn, DisplayImageiBtn;
    private Word word;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            word = getArguments().getParcelable(MainActivity.WordKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);


        helpiBtn = (ImageButton) view.findViewById(R.id.imagebutton_moreInfo);
        helpiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                HelpFragment helpFragment = new HelpFragment();
                fragmentTransaction.replace(R.id.fragment_replacement, helpFragment);
            }
        });
        PreviBtn = (ImageButton) view.findViewById(R.id.imagebutton_skipword);
        PreviBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // request prev word
            }
        });
        NextiBtn = (ImageButton) view.findViewById(R.id.imagebutton_skipword);
        NextiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // request nxt word
            }
        });
        PlaySoundiBtn = (ImageButton) view.findViewById(R.id.imagebutton_soundhelp);
        PlaySoundiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).voiceoffer(PlaySoundiBtn, "");
            }
        });
        DisplayImageiBtn = (ImageButton) view.findViewById(R.id.imagebutton_photohelp);
        DisplayImageiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // alertDialog
            }
        });
        return view;
    }

    private void helpbypic(View view, String img2Bdisplayed) {
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        view.startAnimation(shake);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("شكرا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.layout_sample_pic_help, null);
        ImageView imageView = (ImageView) dialogLayout.findViewById(R.id.picsample);
        File imgFile = new File(img2Bdisplayed);
        if (imgFile.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * Created by azizax on 31/01/17.
     */
    public class CustTextView extends android.support.v7.widget.AppCompatTextView {
        Context context;

        public CustTextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            this.context = context;
            init();
        }

        public CustTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public CustTextView(Context context) {
            super(context);
            init();
        }

        public void init() {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Helvetica_Neue.ttf");
            setTypeface(tf, 1);

        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
        }

        @Override
        public ViewPropertyAnimator animate() {

            final int screenWidth, currentMsg;
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
            final Animation.AnimationListener finalMyAnimationListener = myAnimationListener;
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
                    CustTextView.this.measure(0, 0);
                    // Get textView width
                    int textWidth = CustTextView.this.getMeasuredWidth();
                    // Create the animation
                    animation = new TranslateAnimation(-textWidth, screenWidth, 0, 0);

                    animation.setDuration(5000);
                    animation.setRepeatMode(Animation.RESTART);
                    animation.setRepeatCount(Animation.INFINITE);
                    animation.setAnimationListener(finalMyAnimationListener);
                    CustTextView.this.setAnimation(animation);
                }
            };
            animation.setAnimationListener(myAnimationListener);

            CustTextView.this.setAnimation(animation);
            return super.animate();
        }
    }

}
