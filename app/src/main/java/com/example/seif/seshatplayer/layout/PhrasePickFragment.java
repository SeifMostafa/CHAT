package com.example.seif.seshatplayer.layout;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.seif.seshatplayer.MainActivity;
import com.example.seif.seshatplayer.R;

public class PhrasePickFragment extends Fragment {
    private String word = null,phrase = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            word = getArguments().getString(MainActivity.WordKey);
            phrase = getArguments().getString(MainActivity.PhraseKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_phrase_pick, container, false);
        TextView textView_phrase = (TextView) view.findViewById(R.id.textView_phrase);
        TextView textView_picked = (TextView) view.findViewById(R.id.textView_picked);


        textView_phrase.setText(phrase);
        textView_picked.setText("");

        return view;
    }
    // textswitcher and pick from txtview
    // request word
    // return true , false

}
