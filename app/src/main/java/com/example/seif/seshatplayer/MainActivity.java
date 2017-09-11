package com.example.seif.seshatplayer;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.example.seif.seshatplayer.layout.HelpFragment;
import com.example.seif.seshatplayer.layout.MainFragment;
import com.example.seif.seshatplayer.layout.PhrasePickFragment;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
/*        MainFragment mainFragment = new MainFragment();
        fragmentTransaction.replace(R.id.fragment_replacement,mainFragment);*/
      /*  HelpFragment helpFragment = new HelpFragment();
        fragmentTransaction.replace(R.id.fragment_replacement,helpFragment);*/
        PhrasePickFragment phrasePickFragment = new PhrasePickFragment();
        fragmentTransaction.replace(R.id.fragment_replacement,phrasePickFragment);
        fragmentTransaction.commit();

    }
}
