package com.seifmostafa.cchat.Model;

import java.util.Stack;

import static com.seifmostafa.cchat.Utils.photosfolder;
import static com.seifmostafa.cchat.Utils.readfileintoStack;
import static com.seifmostafa.cchat.Utils.voicefolder;
import static com.seifmostafa.cchat.Utils.wordspath;

/**
 * Created by azizax on 11/07/17.
 */

public class Loader {
    private Stack<Word> words = new Stack<>();

    private Stack<String> loadtxt(){
      return readfileintoStack(wordspath);
    }

    /*
        photos are .png
        voice offer .wav
        AI(Editor) or server will set .txt contains the words, folder contains photos, folder contains sounds.

     */

    public Stack<Word> getallwords(){
        Stack<String> words_txt =   loadtxt();
        for(String s:words_txt){
            Word word = new Word(s,photosfolder+"/"+s+".png",voicefolder+"/"+s+".wav");
            words.push(word);
        }
        return words;
    }
}
