package com.example.seif.seshatplayer.layout;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.seif.seshatplayer.MainActivity;
import com.example.seif.seshatplayer.R;

import java.text.BreakIterator;
import java.util.Locale;

public class PhrasePickFragment extends Fragment {
    private String word = null,phrase = null;
    TextView textView_phrase,textView_picked;
    ImageButton helpiBtn;
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
        View view =  inflater.inflate(R.layout.fragment_phrase_pick, container, false);
        textView_phrase = (TextView) view.findViewById(R.id.textView_phrase);
        textView_picked = (TextView) view.findViewById(R.id.textView_picked);
        textView_picked.setText("");
        textView_phrase.setMovementMethod(LinkMovementMethod.getInstance());
        textView_phrase.setText(phrase, TextView.BufferType.SPANNABLE);
        Spannable spans = (Spannable) textView_phrase.getText();
        Locale loc = new Locale("ar");
        BreakIterator iterator = BreakIterator.getWordInstance(loc);
        iterator.setText(phrase);
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
                .next()) {
            String possibleWord = phrase.substring(start, end);
            if (Character.isLetterOrDigit(possibleWord.charAt(0))) {
                ClickableSpan clickSpan = getClickableSpan(possibleWord);
                spans.setSpan(clickSpan, start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        textView_picked.setVisibility(View.VISIBLE);
        textView_phrase.setVisibility(View.VISIBLE);



        helpiBtn = (ImageButton) ((MainActivity)getActivity()).findViewById(R.id.imagebutton_moreInfo);
        helpiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).voiceoffer(helpiBtn,((MainActivity)getActivity()).getString(R.string.pickwordinstr));
            }
        });

        return view;
    }


    private ClickableSpan getClickableSpan(final String word) {
        return new ClickableSpan() {
            final String mWord;
            {
                mWord = word;
            }
            @Override
            public void onClick(View widget) {
                Log.d("tapped on:", mWord);
                textView_picked.setText(mWord);
                if(mWord.equals(word)){
                    // congrats .. return
                }
            }
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
    }

}
