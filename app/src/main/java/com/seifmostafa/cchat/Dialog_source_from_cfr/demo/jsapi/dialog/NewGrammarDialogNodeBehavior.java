/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  edu.cmu.sphinx.jsapi.JSGFGrammar
 */
package com.seifmostafa.cchat.Dialog_source_from_cfr.demo.jsapi.dialog;

import java.io.IOException;

class NewGrammarDialogNodeBehavior
extends DialogNodeBehavior {
    public void onEntry() throws IOException {
        super.onEntry();
        this.getGrammar().loadJSGF(this.getGrammarName());
    }

    public String getGrammarName() {
        return this.getName();
    }
}

