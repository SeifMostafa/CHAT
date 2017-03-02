/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  edu.cmu.sphinx.jsapi.JSGFGrammar
 *  edu.cmu.sphinx.result.Result
 *  edu.cmu.sphinx.util.Timer
 *  javax.speech.recognition.GrammarException
 */
package com.seifmostafa.cchat.Dialog_source_from_cfr.demo.jsapi.dialog;


import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.Timer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.speech.recognition.GrammarException;

public class MyBehavior
extends NewGrammarDialogNodeBehavior {
    Dialog dia;
    private Collection sampleUtterances;

    public MyBehavior() {
    }

    public void onReady() {
        super.onReady();
        this.help();
    }

    protected void help() {
        this.dumpSampleUtterances();
    }

    public String onRecognize(Result result) throws GrammarException {
        String tag = super.onRecognize(result);
        if (tag != null) {
          //  Dialog.textDisplay.append("\n " + result.getBestFinalResultNoFiller() + "\n");
            if (tag.equals("exit")) {
              //  Dialog.textDisplay.append("\u0645\u0639 \u0627\u0644\u0633\u0644\u0627\u0645\u0629\n");
                System.exit(0);
            }
            if (tag.equals("\u0645\u064f\u0633\u064e\u0627\u0639\u064e\u062f\u064e\u0629\u0652")) {
                this.help();
            } else if (tag.equals("stats")) {
                Timer.dumpAll();
            } else {
                if (tag.startsWith("goto_")) {
                    return tag.replaceFirst("goto_", "");
                }
                if (tag.startsWith("browse")) {
                    this.execute(tag);
                }
            }
        } else {
           // Dialog.textDisplay.append("\n \u0627\u0633\u0641! \u0644\u0645 \u0627\u0633\u0645\u0639\u0643 \u062c\u064a\u062f\u0627\u064e.\n");
        }
        return null;
    }

    private void execute(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        }
        catch (IOException e) {
            // empty catch block
        }
    }

    private Collection collectSampleUtterances() {
        HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < 100; ++i) {
            String s = this.getGrammar().getRandomSentence();
            if (set.contains(s)) continue;
            set.add(s);
        }
        ArrayList sampleList = new ArrayList(set);
        Collections.sort(sampleList);
        return sampleList;
    }

    private void dumpSampleUtterances() {
        if (this.sampleUtterances == null) {
            this.sampleUtterances = this.collectSampleUtterances();
        }
    }

    protected void grammarChanged() {
        this.sampleUtterances = null;
    }
}

