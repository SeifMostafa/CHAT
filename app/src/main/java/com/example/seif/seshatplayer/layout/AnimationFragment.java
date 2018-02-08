package com.example.seif.seshatplayer.layout;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.seif.seshatplayer.MainActivity;
import com.example.seif.seshatplayer.R;
import com.example.seif.seshatplayer.TypeWriter;


public class AnimationFragment extends Fragment {
    public static String AnimationFragment_TAG = "LessonFragment";
    String word;
    TypeWriter custTextView;
    ImageButton helpiBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.word = getArguments().getString(MainActivity.WordKey);
            if (word != null) Log.i("AnimationFragment", "Word != null");
            else Log.i("AnimationFragment", "Word = null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/lvl1.ttf");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_animation, container, false);
        custTextView = (TypeWriter) view.findViewById(R.id.textView_maintext);
        custTextView.setTypeface(tf);
        custTextView.setVisibility(View.VISIBLE);
        custTextView.setCharacterDelay();
        custTextView.animateText(word);

        custTextView.setWord(word);
        helpiBtn = (ImageButton) getActivity().findViewById(R.id.imagebutton_moreInfo);
        helpiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("helpiBtn", "is clicked!");
                try {
                    custTextView.clearAnimation();
                    /*if () {

                            Log.i("helpiBtn", "is clicked!" + "Thread_WordJourney.is alive");
                    }*/
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                ((MainActivity) getActivity()).OpenHelpFragment();
                LessonFragment.isAnimated = false;
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        int time2waitbeforeOpenningPickPhraseFragment = (
                word.length() + 2) * 1000;

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                if (isVisible())
                    ((MainActivity) getActivity()).backToLessonFragment();
            }
        };
        handler.postDelayed(r, time2waitbeforeOpenningPickPhraseFragment);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
