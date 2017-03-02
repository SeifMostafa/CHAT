/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  edu.cmu.sphinx.frontend.util.Microphone
 *  edu.cmu.sphinx.jsapi.JSGFGrammar
 *  edu.cmu.sphinx.recognizer.Recognizer
 *  edu.cmu.sphinx.result.Result
 *  edu.cmu.sphinx.util.props.Configurable
 *  edu.cmu.sphinx.util.props.PropertyException
 *  edu.cmu.sphinx.util.props.PropertySheet
 *  edu.cmu.sphinx.util.props.PropertyType
 *  edu.cmu.sphinx.util.props.Registry
 *  javax.speech.recognition.GrammarException
 */
package com.seifmostafa.cchat.Dialog_source_from_cfr.demo.jsapi.dialog;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.jsapi.JSGFGrammar;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.Configurable;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import edu.cmu.sphinx.util.props.PropertyType;
import edu.cmu.sphinx.util.props.Registry;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import javax.speech.recognition.GrammarException;

public class DialogManager
implements Configurable {
    public static final String PROP_JSGF_GRAMMAR = "jsgfGrammar";
    public static final String PROP_MICROPHONE = "microphone";
    public static final String PROP_RECOGNIZER = "recognizer";
    private JSGFGrammar grammar;
    private Logger logger;
    private Recognizer recognizer;
    private Microphone microphone;
    private DialogNode initialNode;
    private Map nodeMap = new HashMap();
    private String name;

    public void register(String name, Registry registry) throws PropertyException {
        this.name = name;
        registry.register("jsgfGrammar", PropertyType.COMPONENT);
        registry.register("microphone", PropertyType.COMPONENT);
        registry.register("recognizer", PropertyType.COMPONENT);
    }

    public void newProperties(PropertySheet ps) throws PropertyException {
        this.logger = ps.getLogger();
        Class class_ = JSGFGrammar.class;
        this.grammar = (JSGFGrammar)ps.getComponent("jsgfGrammar", class_);
        Class class_2 = Microphone.class;
        this.microphone = (Microphone)ps.getComponent("microphone", class_2);
        Class class_3 = Recognizer.class;
        this.recognizer = (Recognizer)ps.getComponent("recognizer", class_3);
    }

    public void addNode(String name, DialogNodeBehavior behavior) {
        DialogNode node = new DialogNode(name, behavior);
        this.putNode(node);
    }

    public void setInitialNode(String name) {
        if (this.getNode(name) == null) {
            throw new IllegalArgumentException("Unknown node " + name);
        }
        this.initialNode = this.getNode(name);
    }

    public void allocate() throws IOException {
        this.recognizer.allocate();
        Iterator i = this.nodeMap.values().iterator();
        while (i.hasNext()) {
            DialogNode node = (DialogNode)i.next();
            node.init();
        }
    }

    public void deallocate() {
        this.recognizer.deallocate();
    }

    public void go() {
        DialogNode lastNode = null;
        DialogNode curNode = this.initialNode;
        try {
            if (this.microphone.startRecording()) {
                do {
                    String nextStateName;
                    if (curNode != lastNode) {
                        if (lastNode != null) {
                            lastNode.exit();
                        }
                        curNode.enter();
                        lastNode = curNode;
                    }
                    if ((nextStateName = curNode.recognize()) == null || nextStateName.length() == 0) continue;
                    DialogNode node = (DialogNode)this.nodeMap.get(nextStateName);
                    if (node == null) {
                        this.warn("Can't transition to unknown state " + nextStateName);
                        continue;
                    }
                    curNode = node;
                } while (true);
            }
            this.error("Can't start the microphone");
        }
        catch (GrammarException ge) {
            this.error("grammar problem in state " + curNode.getName() + " " + (Object)ge);
        }
        catch (IOException ioe) {
            this.error("problem loading grammar in state " + curNode.getName() + " " + ioe);
        }
    }

    public String getName() {
        return this.name;
    }

    private DialogNode getNode(String name) {
        return (DialogNode)this.nodeMap.get(name);
    }

    private void putNode(DialogNode node) {
        this.nodeMap.put(node.getName(), node);
    }

    private void warn(String s) {
        System.out.println("Warning: " + s);
    }

    private void error(String s) {
        System.out.println("Error: " + s);
    }

    private void trace(String s) {
        this.logger.info(s);
    }

    public Recognizer getRecognizer() {
        return this.recognizer;
    }

    public void setRecognizer(Recognizer recognizer) {
        this.recognizer = recognizer;
    }

    class DialogNode {
        private DialogNodeBehavior behavior;
        private String name;

        DialogNode(String name, DialogNodeBehavior behavior) {
            this.behavior = behavior;
            this.name = name;
        }

        void init() {
            this.behavior.onInit(this);
        }

        void enter() throws IOException {
            this.trace("Entering " + this.name);
            this.behavior.onEntry();
            this.behavior.onReady();
        }

        String recognize() throws GrammarException {
            this.trace("Recognize " + this.name);
            Result result = DialogManager.this.recognizer.recognize();
            return this.behavior.onRecognize(result);
        }

        void exit() {
            this.trace("Exiting " + this.name);
            this.behavior.onExit();
        }

        public String getName() {
            return this.name;
        }

        public JSGFGrammar getGrammar() {
            return DialogManager.this.grammar;
        }

        public void trace(String msg) {
            DialogManager.this.trace(msg);
        }

        public DialogManager getDialogManager() {
            return DialogManager.this;
        }
    }

}

