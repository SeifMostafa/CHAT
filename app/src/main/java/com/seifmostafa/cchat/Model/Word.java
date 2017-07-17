package com.seifmostafa.cchat.Model;

import java.io.File;

/**
 * Created by azizax on 11/07/17.
 */

public class Word {
    private String word,photopath,soundpath;

    public Word(String word, String photo, String sound) {
        this.word = word;
        this.photopath = photo;
        this.soundpath = sound;
    }

    public String getWord() {
        return word;
    }

    public String getPhotopath() {
        return photopath;
    }

    public String getSoundpath() {
        return soundpath;
    }
}
