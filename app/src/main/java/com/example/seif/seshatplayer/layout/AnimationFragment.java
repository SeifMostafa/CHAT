package com.example.seif.seshatplayer.layout;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.seif.seshatplayer.MainActivity;
import com.example.seif.seshatplayer.R;
import com.example.seif.seshatplayer.TypeWriter;
import com.example.seif.seshatplayer.model.Word;


public class AnimationFragment extends Fragment {
    Word word;
    TypeWriter custTextView;
    public static String AnimationFragment_TAG = "LessonFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.word = getArguments().getParcelable(MainActivity.WordKey);
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
        custTextView.setCharacterDelay(400);
        custTextView.animateText(word.getText());

        custTextView.setWord(word);



        return view;
    }
}
