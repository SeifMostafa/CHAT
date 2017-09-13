package com.example.seif.seshatplayer;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.seif.seshatplayer.layout.HelpFragment;
import com.example.seif.seshatplayer.layout.MainFragment;
import com.example.seif.seshatplayer.layout.PhrasePickFragment;

import java.util.Stack;


public class MainActivity extends Activity {
    private Stack<String> words;
    private int word_loop=0;
    private int word_index=0;
    SharedPreferences sharedPreferences_words;
    SharedPreferences.Editor sharedPreferences_words_editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // read file into words
        // write data into sharedpref

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainFragment mainFragment = new MainFragment();
        fragmentTransaction.replace(R.id.fragment_replacement,mainFragment);

      /*  HelpFragment helpFragment = new HelpFragment();
        fragmentTransaction.replace(R.id.fragment_replacement,helpFragment);*/
        /*PhrasePickFragment phrasePickFragment = new PhrasePickFragment();
        fragmentTransaction.replace(R.id.fragment_replacement,phrasePickFragment);
        fragmentTransaction.commit();*/


    }

}
