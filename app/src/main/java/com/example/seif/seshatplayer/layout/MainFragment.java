package com.example.seif.seshatplayer.layout;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.seif.seshatplayer.R;


public class MainFragment extends Fragment {


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
        TextView tv = (TextView) view.findViewById(R.id.textView_maintext);
        tv.setText("");
        String text = "ورد";
        for(int i=0;i<text.length();i++){
            Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.scale);
            a.reset();
            tv.setText(tv.getText().toString()+text.charAt(i));
            tv.clearAnimation();
            tv.startAnimation(a);
        }









        return view;
    }
}
