package com.example.seif.seshatplayer.layout;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.seif.seshatplayer.MainActivity;
import com.example.seif.seshatplayer.R;


public class HelpFragment extends Fragment {

    ImageButton PrevlessoniBtn,CurrentlessoniBtn,AchievedlessoniBtn,PrevlessoniBtn_help,CurrentlessoniBtn_help,AchievedlessoniBtn_help;
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
        View view = inflater.inflate(R.layout.fragment_help, container, false);
       // PreviBtn = (ImageButton)view.findViewById()
        PrevlessoniBtn = (ImageButton)view.findViewById(R.id.imagebutton_prevlesson);
        PrevlessoniBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).updatelesson(-1);
            }
        });
        CurrentlessoniBtn = (ImageButton)view.findViewById(R.id.imagebutton_currentlesson);
        CurrentlessoniBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).updatelesson(0);
            }
        });
        AchievedlessoniBtn = (ImageButton)view.findViewById(R.id.imagebutton_achievedlessons);
        AchievedlessoniBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        PrevlessoniBtn_help = (ImageButton)view.findViewById(R.id.imagebutton_prevlesson_help);
        PrevlessoniBtn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        CurrentlessoniBtn_help = (ImageButton)view.findViewById(R.id.imagebutton_currentlesson_help);
        CurrentlessoniBtn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        AchievedlessoniBtn_help = (ImageButton)view.findViewById(R.id.imagebutton_achievedlessons_help);
        AchievedlessoniBtn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });









    return view;
    }

    private void ShowAchievementsListview(){

    }
    // listview and clickable to back to specified word
    // from begin to current lesson


}
