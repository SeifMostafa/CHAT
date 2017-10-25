package com.example.seif.seshatplayer.layout;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.seif.seshatplayer.MainActivity;
import com.example.seif.seshatplayer.R;
import com.example.seif.seshatplayer.Typewriter;
import com.example.seif.seshatplayer.model.Word;


public class AnimationFragment extends Fragment {
    Word word;
    Typewriter custTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //this.word = new Word(getArguments().getString(MainActivity.AnimationKey));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/lvl1.ttf");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_animation, container, false);
        custTextView = (Typewriter) view.findViewById(R.id.textView_maintext);
        custTextView.setTypeface(tf);
        custTextView.setVisibility(View.VISIBLE);
        custTextView.setCharacterDelay(400);
        custTextView.animateText(word.getText());


        return view;
    }
}
