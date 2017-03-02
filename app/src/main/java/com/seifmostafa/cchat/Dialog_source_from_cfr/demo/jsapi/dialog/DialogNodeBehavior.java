/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  edu.cmu.sphinx.jsapi.JSGFGrammar
 *  edu.cmu.sphinx.result.Result
 *  javax.speech.recognition.GrammarException
 *  javax.speech.recognition.RuleGrammar
 *  javax.speech.recognition.RuleParse
 */
package com.seifmostafa.cchat.Dialog_source_from_cfr.demo.jsapi.dialog;

import edu.cmu.sphinx.jsapi.JSGFGrammar;
import edu.cmu.sphinx.result.Result;
import java.io.IOException;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;

class DialogNodeBehavior {
    private DialogManager.DialogNode node;

    DialogNodeBehavior() {
    }

    public void onInit(DialogManager.DialogNode node) {
        this.node = node;
    }

    public void onEntry() throws IOException {
        this.trace("Entering " + this.getName());
    }

    public void onReady() {
        this.trace("Ready " + this.getName());
    }

    public String onRecognize(Result result) throws GrammarException {
        String tagString = this.getTagString(result);
        this.trace("Recognize result: " + result.getBestFinalResultNoFiller());
        this.trace("Recognize tag   : " + tagString);
        return tagString;
    }

    public void onExit() {
        this.trace("Exiting " + this.getName());
    }

    public String getName() {
        return this.node.getName();
    }

    public String toString() {
        return "Node " + this.getName();
    }

    public JSGFGrammar getGrammar() {
        return this.node.getGrammar();
    }

    RuleParse getRuleParse(Result result) throws GrammarException {
        String resultText = result.getBestFinalResultNoFiller();
        RuleGrammar ruleGrammar = this.getGrammar().getRuleGrammar();
        RuleParse ruleParse = ruleGrammar.parse(resultText, null);
        return ruleParse;
    }

    String getTagString(Result result) throws GrammarException {
        String tagString = null;
        RuleParse ruleParse = this.getRuleParse(result);
        if (ruleParse != null) {
            String[] tags = ruleParse.getTags();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; tags != null && i < tags.length; ++i) {
                sb.append(tags[i]);
                if (i >= tags.length - 1) continue;
                sb.append(" ");
            }
            tagString = sb.toString().trim();
        }
        return tagString;
    }

    void trace(String msg) {
        this.node.trace(msg);
    }
}

