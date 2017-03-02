/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  edu.cmu.sphinx.linguist.acoustic.AcousticModel
 *  edu.cmu.sphinx.linguist.acoustic.Context
 *  edu.cmu.sphinx.linguist.acoustic.HMM
 *  edu.cmu.sphinx.linguist.acoustic.HMMPosition
 *  edu.cmu.sphinx.linguist.acoustic.LeftRightContext
 *  edu.cmu.sphinx.linguist.acoustic.Unit
 *  edu.cmu.sphinx.linguist.acoustic.UnitManager
 *  edu.cmu.sphinx.linguist.acoustic.tiedstate.CompositeSenone
 *  edu.cmu.sphinx.linguist.acoustic.tiedstate.HMMManager
 *  edu.cmu.sphinx.linguist.acoustic.tiedstate.Loader
 *  edu.cmu.sphinx.linguist.acoustic.tiedstate.Senone
 *  edu.cmu.sphinx.linguist.acoustic.tiedstate.SenoneHMM
 *  edu.cmu.sphinx.linguist.acoustic.tiedstate.SenoneSequence
 *  edu.cmu.sphinx.util.Timer
 *  edu.cmu.sphinx.util.props.Configurable
 *  edu.cmu.sphinx.util.props.PropertyException
 *  edu.cmu.sphinx.util.props.PropertySheet
 *  edu.cmu.sphinx.util.props.PropertyType
 *  edu.cmu.sphinx.util.props.Registry
 */
package com.seifmostafa.cchat.acoustic;

import edu.cmu.sphinx.linguist.acoustic.AcousticModel;
import edu.cmu.sphinx.linguist.acoustic.Context;
import edu.cmu.sphinx.linguist.acoustic.HMM;
import edu.cmu.sphinx.linguist.acoustic.HMMPosition;
import edu.cmu.sphinx.linguist.acoustic.LeftRightContext;
import edu.cmu.sphinx.linguist.acoustic.Unit;
import edu.cmu.sphinx.linguist.acoustic.UnitManager;
import edu.cmu.sphinx.linguist.acoustic.tiedstate.CompositeSenone;
import edu.cmu.sphinx.linguist.acoustic.tiedstate.HMMManager;
import edu.cmu.sphinx.linguist.acoustic.tiedstate.Loader;
import edu.cmu.sphinx.linguist.acoustic.tiedstate.Senone;
import edu.cmu.sphinx.linguist.acoustic.tiedstate.SenoneHMM;
import edu.cmu.sphinx.linguist.acoustic.tiedstate.SenoneSequence;
import edu.cmu.sphinx.util.Timer;
import edu.cmu.sphinx.util.props.Configurable;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import edu.cmu.sphinx.util.props.PropertyType;
import edu.cmu.sphinx.util.props.Registry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Model
implements AcousticModel,
Configurable {
    public static final String PROP_LOADER = "loader";
    public static final String PROP_UNIT_MANAGER = "unitManager";
    public static final String PROP_USE_COMPOSITES = "useComposites";
    public static final boolean PROP_USE_COMPOSITES_DEFAULT = true;
    protected static final String TIMER_LOAD = "AM_Load";
    protected String name;
    private Logger logger;
    protected Loader loader;
    protected UnitManager unitManager;
    private boolean useComposites = false;
    private Properties properties;
    protected transient Timer loadTimer;
    private transient Map compositeSenoneSequenceCache = new HashMap();
    private boolean allocated = false;
    static final /* synthetic */ boolean $assertionsDisabled;

    public void register(String name, Registry registry) throws PropertyException {
        this.name = name;
        registry.register("loader", PropertyType.COMPONENT);
        registry.register("unitManager", PropertyType.COMPONENT);
        registry.register("useComposites", PropertyType.BOOLEAN);
    }

    public void newProperties(PropertySheet ps) throws PropertyException {
        Class class_ = Loader.class;
        this.loader = (Loader)ps.getComponent("loader", class_);
        Class class_2 = UnitManager.class;
        this.unitManager = (UnitManager)ps.getComponent("unitManager", class_2);
        this.useComposites = ps.getBoolean("useComposites", true);
        this.logger = ps.getLogger();
    }

    public void allocate() throws IOException {
        if (!this.allocated) {
            this.loadTimer = Timer.getTimer((String)"AM_Load");
            this.loadTimer.start();
            this.loader.load();
            this.loadTimer.stop();
            this.logInfo();
            this.allocated = true;
        }
    }

    public void deallocate() {
    }

    public String getName() {
        return this.name;
    }

    private HMM getCompositeHMM(Unit unit, HMMPosition position) {
        Unit ciUnit = this.unitManager.getUnit(unit.getName(), unit.isFiller(), Context.EMPTY_CONTEXT);
        SenoneSequence compositeSequence = this.getCompositeSenoneSequence(unit, position);
        SenoneHMM contextIndependentHMM = (SenoneHMM)this.lookupNearestHMM(ciUnit, HMMPosition.UNDEFINED, true);
        float[][] tmat = contextIndependentHMM.getTransitionMatrix();
        return new SenoneHMM(unit, compositeSequence, tmat, position);
    }

    public HMM lookupNearestHMM(Unit unit, HMMPosition position, boolean exactMatch) {
        if (exactMatch) {
            return this.lookupHMM(unit, position);
        }
        HMMManager mgr = this.loader.getHMMManager();
        HMM hmm = mgr.get(position, unit);
        if (hmm != null) {
            return hmm;
        }
        if (this.useComposites && hmm == null && this.isComposite(unit) && (hmm = this.getCompositeHMM(unit, position)) != null) {
            mgr.put(hmm);
        }
        if (hmm == null) {
            hmm = this.getHMMAtAnyPosition(unit);
        }
        if (hmm == null) {
            hmm = this.getHMMInSilenceContext(unit, position);
        }
        if (hmm == null) {
            Unit ciUnit = this.lookupUnit(unit.getName());
            if (!$assertionsDisabled && !unit.isContextDependent()) {
                throw new AssertionError();
            }
            if (ciUnit == null) {
                this.logger.severe("Can't find HMM for " + unit.getName());
            }
            if (!$assertionsDisabled && ciUnit == null) {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && ciUnit.isContextDependent()) {
                throw new AssertionError();
            }
            hmm = mgr.get(HMMPosition.UNDEFINED, ciUnit);
        }
        if (!$assertionsDisabled && hmm == null) {
            throw new AssertionError();
        }
        return hmm;
    }

    private boolean isComposite(Unit unit) {
        if (unit.isFiller()) {
            return false;
        }
        Context context = unit.getContext();
        if (context instanceof LeftRightContext) {
            LeftRightContext lrContext = (LeftRightContext)context;
            if (lrContext.getRightContext() == null) {
                return true;
            }
            if (lrContext.getLeftContext() == null) {
                return true;
            }
        }
        return false;
    }

    private Unit lookupUnit(String name) {
        return (Unit)this.loader.getContextIndependentUnits().get(name);
    }

    public Iterator getHMMIterator() {
        return this.loader.getHMMManager().getIterator();
    }

    public Iterator getContextIndependentUnitIterator() {
        return this.loader.getContextIndependentUnits().values().iterator();
    }

    public SenoneSequence getCompositeSenoneSequence(Unit unit, HMMPosition position) {
        Context context = unit.getContext();
        SenoneSequence compositeSenoneSequence = null;
        compositeSenoneSequence = (SenoneSequence)this.compositeSenoneSequenceCache.get(unit.toString());
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("getCompositeSenoneSequence: " + unit.toString() + (compositeSenoneSequence != null ? "Cached" : ""));
        }
        if (compositeSenoneSequence != null) {
            return compositeSenoneSequence;
        }
        ArrayList<SenoneSequence> senoneSequenceList = new ArrayList<SenoneSequence>();
        Iterator i = this.getHMMIterator();
        while (i.hasNext()) {
            Unit hmmUnit;
            SenoneHMM hmm = (SenoneHMM)i.next();
            if (hmm.getPosition() != position || !(hmmUnit = hmm.getUnit()).isPartialMatch(unit.getName(), context)) continue;
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("collected: " + hmm.getUnit().toString());
            }
            senoneSequenceList.add(hmm.getSenoneSequence());
        }
        if (senoneSequenceList.size() == 0) {
            Unit ciUnit = this.unitManager.getUnit(unit.getName(), unit.isFiller());
            SenoneHMM baseHMM = this.lookupHMM(ciUnit, HMMPosition.UNDEFINED);
            senoneSequenceList.add(baseHMM.getSenoneSequence());
        }
        int longestSequence = 0;
        for (int i2 = 0; i2 < senoneSequenceList.size(); ++i2) {
            SenoneSequence ss = (SenoneSequence)senoneSequenceList.get(i2);
            if (ss.getSenones().length <= longestSequence) continue;
            longestSequence = ss.getSenones().length;
        }
        ArrayList<CompositeSenone> compositeSenones = new ArrayList<CompositeSenone>();
        float logWeight = 0.0f;
        for (int i3 = 0; i3 < longestSequence; ++i3) {
            HashSet<Senone> compositeSenoneSet = new HashSet<Senone>();
            for (int j = 0; j < senoneSequenceList.size(); ++j) {
                SenoneSequence senoneSequence = (SenoneSequence)senoneSequenceList.get(j);
                if (i3 >= senoneSequence.getSenones().length) continue;
                Senone senone = senoneSequence.getSenones()[i3];
                compositeSenoneSet.add(senone);
            }
            compositeSenones.add(CompositeSenone.create(compositeSenoneSet, (float)logWeight));
        }
        compositeSenoneSequence = SenoneSequence.create(compositeSenones);
        this.compositeSenoneSequenceCache.put(unit.toString(), compositeSenoneSequence);
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine(unit.toString() + " consists of " + compositeSenones.size() + " composite senones");
            if (this.logger.isLoggable(Level.FINEST)) {
                compositeSenoneSequence.dump("am");
            }
        }
        return compositeSenoneSequence;
    }

    public int getLeftContextSize() {
        return this.loader.getLeftContextSize();
    }

    public int getRightContextSize() {
        return this.loader.getRightContextSize();
    }

    private SenoneHMM lookupHMM(Unit unit, HMMPosition position) {
        return (SenoneHMM)this.loader.getHMMManager().get(position, unit);
    }

    private String makeTag(Unit base, Context context) {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        sb.append(base.getName());
        sb.append("-");
        sb.append(context.toString());
        sb.append(")");
        return sb.toString();
    }

    protected void logInfo() {
        if (this.loader != null) {
            this.loader.logInfo();
        }
        this.logger.info("CompositeSenoneSequences: " + this.compositeSenoneSequenceCache.size());
    }

    private SenoneHMM getHMMAtAnyPosition(Unit unit) {
        SenoneHMM hmm = null;
        HMMManager mgr = this.loader.getHMMManager();
        Iterator i = HMMPosition.iterator();
        while (hmm == null && i.hasNext()) {
            HMMPosition pos = (HMMPosition)i.next();
            hmm = (SenoneHMM)mgr.get(pos, unit);
        }
        return hmm;
    }

    private SenoneHMM getHMMInSilenceContext(Unit unit, HMMPosition position) {
        SenoneHMM hmm = null;
        HMMManager mgr = this.loader.getHMMManager();
        Context context = unit.getContext();
        if (context instanceof LeftRightContext) {
            LeftRightContext lrContext = (LeftRightContext)context;
            Unit[] lc = lrContext.getLeftContext();
            Unit[] rc = lrContext.getRightContext();
            Unit[] nlc = this.hasNonSilenceFiller(lc) ? this.replaceNonSilenceFillerWithSilence(lc) : lc;
            Unit[] nrc = this.hasNonSilenceFiller(rc) ? this.replaceNonSilenceFillerWithSilence(rc) : rc;
            if (nlc != lc || nrc != rc) {
                LeftRightContext newContext = LeftRightContext.get((Unit[])nlc, (Unit[])nrc);
                Unit newUnit = this.unitManager.getUnit(unit.getName(), unit.isFiller(), (Context)newContext);
                hmm = (SenoneHMM)mgr.get(position, newUnit);
                if (hmm == null) {
                    hmm = this.getHMMAtAnyPosition(newUnit);
                }
            }
        }
        return hmm;
    }

    private void checkNull(String msg, Unit[] c) {
        for (int i = 0; i < c.length; ++i) {
            if (c[i] != null) continue;
            System.out.println("null at index " + i + " of " + msg);
        }
    }

    private boolean hasNonSilenceFiller(Unit[] units) {
        if (units == null) {
            return false;
        }
        for (int i = 0; i < units.length; ++i) {
            if (!units[i].isFiller() || units[i].equals((Object)UnitManager.SILENCE)) continue;
            return true;
        }
        return false;
    }

    private Unit[] replaceNonSilenceFillerWithSilence(Unit[] context) {
        Unit[] replacementContext = new Unit[context.length];
        for (int i = 0; i < context.length; ++i) {
            replacementContext[i] = context[i].isFiller() && !context[i].equals((Object)UnitManager.SILENCE) ? UnitManager.SILENCE : context[i];
        }
        return replacementContext;
    }

    public Properties getProperties() {
        if (this.properties == null) {
            this.properties = new Properties();
            try {
                this.properties.load(this.getClass().getResource("model.props").openStream());
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return this.properties;
    }

    static {
        Class class_ = Model.class;
        $assertionsDisabled = !class_.desiredAssertionStatus();
    }
}

