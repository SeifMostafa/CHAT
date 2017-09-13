package com.example.seif.seshatplayer.layout;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.seif.seshatplayer.R;


public class MainFragment extends Fragment {

    ImageButton helpiBtn,PreviBtn,NextiBtn,PlaySoundiBtn,DisplayImageiBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_main, container, false);
       /* TextView tv = (TextView) view.findViewById(R.id.textView_maintext);
        tv.setText("");
        String text = "ورد";
        for(int i=0;i<text.length();i++){
            Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.scale);
            a.reset();
            tv.setText(tv.getText().toString()+text.charAt(i));
            tv.clearAnimation();
            tv.startAnimation(a);
        }*/

        helpiBtn = (ImageButton) view.findViewById(R.id.imagebutton_moreInfo);
        helpiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                HelpFragment helpFragment = new HelpFragment();
                fragmentTransaction.replace(R.id.fragment_replacement,helpFragment);
            }
        });
        PreviBtn = (ImageButton)view.findViewById(R.id.imagebutton_skipword);
        PreviBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // request prev word
            }
        });
        NextiBtn = (ImageButton)view.findViewById(R.id.imagebutton_skipword);
        NextiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // request nxt word
            }
        });
        PlaySoundiBtn = (ImageButton)view.findViewById(R.id.imagebutton_soundhelp);
        PlaySoundiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mediaplayer
            }
        });
        DisplayImageiBtn = (ImageButton)view.findViewById(R.id.imagebutton_photohelp);
        DisplayImageiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // alertDialog
            }
        });


























        return view;
    }
    /**
     * Created by azizax on 31/01/17.
     */
    public class CustTextView extends TextView {
        Context context;
        public CustTextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            this.context=context;
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
