/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  edu.cmu.sphinx.recognizer.Recognizer
 *  edu.cmu.sphinx.util.props.Configurable
 *  edu.cmu.sphinx.util.props.ConfigurationManager
 *  edu.cmu.sphinx.util.props.PropertyException
 */
package com.seifmostafa.cchat.Dialog_source_from_cfr.demo.jsapi.dialog;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;


public class Dialog {

    private URL Myurl;
    Context context;
    public Dialog(URL url,Context c) {
        this.Myurl = url;
        this.context=c;
    }

    public void append(URL url) {
        try {
            ConfigurationManager cm = new ConfigurationManager(url);
            DialogManager dialogManager = (DialogManager)cm.lookup("dialogManager");
            Recognizer weatherRecognizer = (Recognizer)cm.lookup("weatherRecognizer");
            dialogManager.addNode("\u0645\u064e\u0644\u064e\u0641\u0652", new MyBehavior());
            dialogManager.addNode("\u0633\u064A\u0641", new MyBehavior());
            dialogManager.addNode("\u0627\u0644\u0631\u0626\u064a\u0633\u0629", new MyBehavior());
            dialogManager.addNode("\u062a\u064e\u062d\u0652\u0631\u064a\u0631", new MyBehavior());
            dialogManager.addNode("\u0625\u0650\u063a\u0652\u0644\u0627\u0642\u0652", new MyBehavior());
            dialogManager.addNode("\u0645\u064f\u0633\u064e\u0627\u0639\u064e\u062f\u064e\u0629\u0652", new MyBehavior());
            dialogManager.addNode("\u062c\u064e\u062f\u0652\u0648\u064e\u0644\u0652", new MyBehavior());
            dialogManager.setInitialNode("\u0627\u0644\u0631\u0626\u064a\u0633\u0629");
            dialogManager.allocate();
            weatherRecognizer.allocate();
            dialogManager.go();
            System.out.println("\u062a\u0646\u0638\u064a\u0641  ...");
            dialogManager.deallocate();
        }
        catch (IOException e) {
            System.err.println("\u062d\u062f\u062b \u062e\u0644\u0644 \u062e\u0644\u0627\u0644 \u062a\u062d\u0645\u064a\u0644 \u0627\u0644\u062d\u0648\u0627\u0631: " + e);
        }
        catch (PropertyException e) {
            System.err.println("\u062d\u062f\u062b \u062e\u0644\u0644 \u062e\u0644\u0627\u0644 \u062a\u0648\u0636\u064a\u0628 \u0627\u0644\u062d\u0648\u0627\u0631: " + (Object)e);
        }
        catch (InstantiationException e) {
            System.err.println("\u062d\u062f\u062b \u062e\u0644\u0644 \u062e\u0644\u0627\u0644 \u062a\u0643\u0648\u064a\u0646 \u0627\u0644\u062d\u0648\u0627\u0631: " + e);
        }
    }

    public void main() {
        try {
            URL url = new URL("raw/dialog_config.xml");
            this.append(url);
        }
        catch (Exception e) {
            Log.e("Dialog:main","\u062d\u062f\u062b \u062e\u0644\u0644 \u062e\u0644\u0627\u0644 \u062a\u062d\u0645\u064a\u0644 \u0627\u0644\u062d\u0648\u0627\u0631: " + e);
        }
    }
}

