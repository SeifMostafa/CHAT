/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  edu.cmu.sphinx.linguist.acoustic.Context
 *  edu.cmu.sphinx.linguist.acoustic.HMM
 *  edu.cmu.sphinx.linguist.acoustic.HMMPosition
 *  edu.cmu.sphinx.linguist.acoustic.LeftRightContext
 *  edu.cmu.sphinx.linguist.acoustic.Unit
 *  edu.cmu.sphinx.linguist.acoustic.UnitManager
 *  edu.cmu.sphinx.linguist.acoustic.tiedstate.GaussianMixture
 *  edu.cmu.sphinx.linguist.acoustic.tiedstate.HMMManager
 *  edu.cmu.sphinx.linguist.acoustic.tiedstate.Loader
 *  edu.cmu.sphinx.linguist.acoustic.tiedstate.MixtureComponent
 *  edu.cmu.sphinx.linguist.acoustic.tiedstate.Pool
 *  edu.cmu.sphinx.linguist.acoustic.tiedstate.Senone
 *  edu.cmu.sphinx.linguist.acoustic.tiedstate.SenoneHMM
 *  edu.cmu.sphinx.linguist.acoustic.tiedstate.SenoneSequence
 *  edu.cmu.sphinx.util.ExtendedStreamTokenizer
 *  edu.cmu.sphinx.util.LogMath
 *  edu.cmu.sphinx.util.SphinxProperties
 *  edu.cmu.sphinx.util.StreamFactory
 *  edu.cmu.sphinx.util.Utilities
 *  edu.cmu.sphinx.util.props.Configurable
 *  edu.cmu.sphinx.util.props.PropertyException
 *  edu.cmu.sphinx.util.props.PropertySheet
 *  edu.cmu.sphinx.util.props.PropertyType
 *  edu.cmu.sphinx.util.props.Registry
 */
package com.seifmostafa.cchat.acoustic;

import edu.cmu.sphinx.linguist.acoustic.Context;
import edu.cmu.sphinx.linguist.acoustic.HMM;
import edu.cmu.sphinx.linguist.acoustic.HMMPosition;
import edu.cmu.sphinx.linguist.acoustic.LeftRightContext;
import edu.cmu.sphinx.linguist.acoustic.Unit;
import edu.cmu.sphinx.linguist.acoustic.UnitManager;
import edu.cmu.sphinx.linguist.acoustic.tiedstate.GaussianMixture;
import edu.cmu.sphinx.linguist.acoustic.tiedstate.HMMManager;
import edu.cmu.sphinx.linguist.acoustic.tiedstate.Loader;
import edu.cmu.sphinx.linguist.acoustic.tiedstate.MixtureComponent;
import edu.cmu.sphinx.linguist.acoustic.tiedstate.Pool;
import edu.cmu.sphinx.linguist.acoustic.tiedstate.Senone;
import edu.cmu.sphinx.linguist.acoustic.tiedstate.SenoneHMM;
import edu.cmu.sphinx.linguist.acoustic.tiedstate.SenoneSequence;
import edu.cmu.sphinx.util.ExtendedStreamTokenizer;
import edu.cmu.sphinx.util.LogMath;
import edu.cmu.sphinx.util.SphinxProperties;
import edu.cmu.sphinx.util.StreamFactory;
import edu.cmu.sphinx.util.Utilities;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import edu.cmu.sphinx.util.props.PropertyType;
import edu.cmu.sphinx.util.props.Registry;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;

public class ModelLoader
implements Loader {
    public static final String PROP_LOG_MATH = "logMath";
    public static final String PROP_UNIT_MANAGER = "unitManager";
    public static final String PROP_IS_BINARY = "isBinary";
    public static final boolean PROP_IS_BINARY_DEFAULT = true;
    public static final String PROP_MODEL = "modelDefinition";
    public static final String PROP_MODEL_DEFAULT = "model.mdef";
    public static final String PROP_DATA_LOCATION = "dataLocation";
    public static final String PROP_DATA_LOCATION_DEFAULT = "data";
    public static final String PROP_PROPERTIES_FILE = "propertiesFile";
    public static final String PROP_PROPERTIES_FILE_DEFAULT = "model.props";
    public static final String PROP_VECTOR_LENGTH = "vectorLength";
    public static final int PROP_VECTOR_LENGTH_DEFAULT = 39;
    public static final String PROP_SPARSE_FORM = "sparseForm";
    public static final boolean PROP_SPARSE_FORM_DEFAULT = true;
    public static final String PROP_USE_CD_UNITS = "useCDUnits";
    public static final boolean PROP_USE_CD_UNITS_DEFAULT = true;
    public static final String PROP_MC_FLOOR = "MixtureComponentScoreFloor";
    public static final float PROP_MC_FLOOR_DEFAULT = 0.0f;
    public static final String PROP_VARIANCE_FLOOR = "varianceFloor";
    public static final float PROP_VARIANCE_FLOOR_DEFAULT = 1.0E-4f;
    public static final String PROP_MW_FLOOR = "mixtureWeightFloor";
    public static final float PROP_MW_FLOOR_DEFAULT = 1.0E-7f;
    protected static final String NUM_SENONES = "num_senones";
    protected static final String NUM_GAUSSIANS_PER_STATE = "num_gaussians";
    protected static final String NUM_STREAMS = "num_streams";
    protected static final String FILLER = "filler";
    protected static final String SILENCE_CIPHONE = "SIL";
    protected static final int BYTE_ORDER_MAGIC = 287454020;
    public static final String MODEL_VERSION = "0.3";
    protected static final int CONTEXT_SIZE = 1;
    private Pool meansPool;
    private Pool variancePool;
    private Pool matrixPool;
    private Pool meanTransformationMatrixPool;
    private Pool meanTransformationVectorPool;
    private Pool varianceTransformationMatrixPool;
    private Pool varianceTransformationVectorPool;
    private Pool mixtureWeightsPool;
    private Pool senonePool;
    private Map contextIndependentUnits;
    private HMMManager hmmManager;
    private LogMath logMath;
    private UnitManager unitManager;
    private Properties properties;
    private boolean swap;
    protected static final String DENSITY_FILE_VERSION = "1.0";
    protected static final String MIXW_FILE_VERSION = "1.0";
    protected static final String TMAT_FILE_VERSION = "1.0";
    private String name;
    private Logger logger;
    private boolean binary;
    private boolean sparseForm;
    private int vectorLength;
    private String location;
    private String model;
    private String dataDir;
    private String propsFile;
    private float distFloor;
    private float mixtureWeightFloor;
    private float varianceFloor;
    private boolean useCDUnits;
    static final /* synthetic */ boolean $assertionsDisabled;

    public void register(String name, Registry registry) throws PropertyException {
        this.name = name;
        registry.register("logMath", PropertyType.COMPONENT);
        registry.register("unitManager", PropertyType.COMPONENT);
        registry.register("isBinary", PropertyType.BOOLEAN);
        registry.register("sparseForm", PropertyType.BOOLEAN);
        registry.register("vectorLength", PropertyType.INT);
        registry.register("modelDefinition", PropertyType.STRING);
        registry.register("dataLocation", PropertyType.STRING);
        registry.register("propertiesFile", PropertyType.STRING);
        registry.register("MixtureComponentScoreFloor", PropertyType.FLOAT);
        registry.register("mixtureWeightFloor", PropertyType.FLOAT);
        registry.register("varianceFloor", PropertyType.FLOAT);
        registry.register("useCDUnits", PropertyType.BOOLEAN);
    }

    public void newProperties(PropertySheet ps) throws PropertyException {
        this.logger = ps.getLogger();
        this.propsFile = ps.getString("propertiesFile", "model.props");
        Class class_ = LogMath.class;
        this.logMath = (LogMath)ps.getComponent("logMath", class_);
        Class class_2 = UnitManager.class;
        this.unitManager = (UnitManager)ps.getComponent("unitManager", class_2);
        this.binary = ps.getBoolean("isBinary", this.getIsBinaryDefault());
        this.sparseForm = ps.getBoolean("sparseForm", this.getSparseFormDefault());
        this.vectorLength = ps.getInt("vectorLength", this.getVectorLengthDefault());
        this.model = ps.getString("modelDefinition", this.getModelDefault());
        this.dataDir = ps.getString("dataLocation", this.getDataLocationDefault()) + "/";
        this.distFloor = ps.getFloat("MixtureComponentScoreFloor", 0.0f);
        this.mixtureWeightFloor = ps.getFloat("mixtureWeightFloor", 1.0E-7f);
        this.varianceFloor = ps.getFloat("varianceFloor", 1.0E-4f);
        this.useCDUnits = ps.getBoolean("useCDUnits", true);
    }

    private void loadProperties() {
        if (this.properties == null) {
            this.properties = new Properties();
            try {
                URL url = this.getClass().getResource(this.propsFile);
                this.properties.load(url.openStream());
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private boolean getIsBinaryDefault() {
        this.loadProperties();
        String binary = (String)this.properties.get("isBinary");
        if (binary != null) {
            return Boolean.valueOf(binary).equals(Boolean.TRUE);
        }
        return true;
    }

    private boolean getSparseFormDefault() {
        this.loadProperties();
        String sparse = (String)this.properties.get("sparseForm");
        if (sparse != null) {
            return Boolean.valueOf(this.binary).equals(Boolean.TRUE);
        }
        return true;
    }

    private int getVectorLengthDefault() {
        this.loadProperties();
        String length = (String)this.properties.get("vectorLength");
        if (length != null) {
            return Integer.parseInt(length);
        }
        return 39;
    }

    private String getModelDefault() {
        this.loadProperties();
        String mdef = (String)this.properties.get("modelDefinition");
        if (mdef != null) {
            return mdef;
        }
        return "model.mdef";
    }

    private String getDataLocationDefault() {
        this.loadProperties();
        String location = (String)this.properties.get("dataLocation");
        if (location != null) {
            return location;
        }
        return "data";
    }

    public String getName() {
        return this.name;
    }

    public void load() throws IOException {
        this.hmmManager = new HMMManager();
        this.contextIndependentUnits = new LinkedHashMap();
        this.meanTransformationMatrixPool = this.createDummyMatrixPool("meanTransformationMatrix");
        this.meanTransformationVectorPool = this.createDummyVectorPool("meanTransformationMatrix");
        this.varianceTransformationMatrixPool = this.createDummyMatrixPool("varianceTransformationMatrix");
        this.varianceTransformationVectorPool = this.createDummyVectorPool("varianceTransformationMatrix");
        this.loadModelFiles(this.model);
    }

    protected HMMManager getHmmManager() {
        return this.hmmManager;
    }

    protected Pool getMatrixPool() {
        return this.matrixPool;
    }

    protected Pool getMixtureWeightsPool() {
        return this.mixtureWeightsPool;
    }

    protected Properties getProperties() {
        if (this.properties == null) {
            this.loadProperties();
        }
        return this.properties;
    }

    protected String getLocation() {
        return this.location;
    }

    private void loadModelFiles(String modelName) throws FileNotFoundException, IOException, ZipException {
        this.logger.config("Loading Sphinx3 acoustic model: " + modelName);
        this.logger.config("    Path      : " + this.location);
        this.logger.config("    modellName: " + this.model);
        this.logger.config("    dataDir   : " + this.dataDir);
        if (this.binary) {
            this.meansPool = this.loadDensityFileBinary(this.dataDir + "means", -3.4028235E38f);
            this.variancePool = this.loadDensityFileBinary(this.dataDir + "variances", this.varianceFloor);
            this.mixtureWeightsPool = this.loadMixtureWeightsBinary(this.dataDir + "mixture_weights", this.mixtureWeightFloor);
            this.matrixPool = this.loadTransitionMatricesBinary(this.dataDir + "transition_matrices");
        } else {
            this.meansPool = this.loadDensityFileAscii(this.dataDir + "means.ascii", -3.4028235E38f);
            this.variancePool = this.loadDensityFileAscii(this.dataDir + "variances.ascii", this.varianceFloor);
            this.mixtureWeightsPool = this.loadMixtureWeightsAscii(this.dataDir + "mixture_weights.ascii", this.mixtureWeightFloor);
            this.matrixPool = this.loadTransitionMatricesAscii(this.dataDir + "transition_matrices.ascii");
        }
        this.senonePool = this.createSenonePool(this.distFloor, this.varianceFloor);
        InputStream modelStream = this.getClass().getResourceAsStream(this.model);
        if (modelStream == null) {
            throw new IOException("can't find model " + this.model);
        }
        this.loadHMMPool(this.useCDUnits, modelStream, this.location + File.separator + this.model);
    }

    public Map getContextIndependentUnits() {
        return this.contextIndependentUnits;
    }

    private Pool createSenonePool(float distFloor, float varianceFloor) {
        Pool pool = new Pool("senones");
        int numMixtureWeights = this.mixtureWeightsPool.size();
        int numMeans = this.meansPool.size();
        int numVariances = this.variancePool.size();
        int numGaussiansPerSenone = this.mixtureWeightsPool.getFeature("num_gaussians", 0);
        int numSenones = this.mixtureWeightsPool.getFeature("num_senones", 0);
        int whichGaussian = 0;
        this.logger.fine("NG " + numGaussiansPerSenone);
        this.logger.fine("NS " + numSenones);
        this.logger.fine("NMIX " + numMixtureWeights);
        this.logger.fine("NMNS " + numMeans);
        this.logger.fine("NMNS " + numVariances);
        if (!$assertionsDisabled && numGaussiansPerSenone <= 0) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && numMixtureWeights != numSenones) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && numVariances != numSenones * numGaussiansPerSenone) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && numMeans != numSenones * numGaussiansPerSenone) {
            throw new AssertionError();
        }
        for (int i = 0; i < numSenones; ++i) {
            MixtureComponent[] mixtureComponents = new MixtureComponent[numGaussiansPerSenone];
            for (int j = 0; j < numGaussiansPerSenone; ++j) {
                mixtureComponents[j] = new MixtureComponent(this.logMath, (float[])this.meansPool.get(whichGaussian), (float[][])this.meanTransformationMatrixPool.get(0), (float[])this.meanTransformationVectorPool.get(0), (float[])this.variancePool.get(whichGaussian), (float[][])this.varianceTransformationMatrixPool.get(0), (float[])this.varianceTransformationVectorPool.get(0), distFloor, varianceFloor);
                ++whichGaussian;
            }
            GaussianMixture senone = new GaussianMixture(this.logMath, (float[])this.mixtureWeightsPool.get(i), mixtureComponents, (long)i);
            pool.put(i, (Object)senone);
        }
        return pool;
    }

    private SphinxProperties loadAcousticPropertiesFile(URL url) throws FileNotFoundException, IOException {
        String context = "acoustic." + this.getName() + "." + url;
        SphinxProperties.initContext((String)context, (URL)url);
        return SphinxProperties.getSphinxProperties((String)context);
    }

    private Pool loadDensityFileAscii(String path, float floor) throws FileNotFoundException, IOException {
        InputStream inputStream = this.getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new FileNotFoundException("Error trying to read file " + this.location + path);
        }
        ExtendedStreamTokenizer est = new ExtendedStreamTokenizer(inputStream, 35, false);
        Pool pool = new Pool(path);
        this.logger.fine("Loading density file from: " + path);
        est.expectString("param");
        int numStates = est.getInt("numStates");
        int numStreams = est.getInt("numStreams");
        int numGaussiansPerState = est.getInt("numGaussiansPerState");
        pool.setFeature("num_senones", numStates);
        pool.setFeature("num_streams", numStreams);
        pool.setFeature("num_gaussians", numGaussiansPerState);
        for (int i = 0; i < numStates; ++i) {
            est.expectString("mgau");
            est.expectInt("mgau index", i);
            est.expectString("feat");
            est.expectInt("feat index", 0);
            for (int j = 0; j < numGaussiansPerState; ++j) {
                est.expectString("density");
                est.expectInt("densityValue", j);
                float[] density = new float[this.vectorLength];
                for (int k = 0; k < this.vectorLength; ++k) {
                    density[k] = est.getFloat("val");
                    if (density[k] >= floor) continue;
                    density[k] = floor;
                }
                int id = i * numGaussiansPerState + j;
                pool.put(id, (Object)density);
            }
        }
        est.close();
        return pool;
    }

    private Pool loadDensityFileBinary(String path, float floor) throws FileNotFoundException, IOException {
        Properties props = new Properties();
        int blockSize = 0;
        DataInputStream dis = this.readS3BinaryHeader(this.location, path, props);
        String version = props.getProperty("version");
        if (version == null || !version.equals("1.0")) {
            throw new IOException("Unsupported version in " + path);
        }
        String checksum = props.getProperty("chksum0");
        boolean doCheckSum = checksum != null && checksum.equals("yes");
        int numStates = this.readInt(dis);
        int numStreams = this.readInt(dis);
        int numGaussiansPerState = this.readInt(dis);
        int[] vectorLength = new int[numStreams];
        for (int i = 0; i < numStreams; ++i) {
            vectorLength[i] = this.readInt(dis);
        }
        int rawLength = this.readInt(dis);
        for (int i2 = 0; i2 < numStreams; ++i2) {
            blockSize += vectorLength[i2];
        }
        if (!$assertionsDisabled && rawLength != numGaussiansPerState * blockSize * numStates) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && numStreams != 1) {
            throw new AssertionError();
        }
        Pool pool = new Pool(path);
        pool.setFeature("num_senones", numStates);
        pool.setFeature("num_streams", numStreams);
        pool.setFeature("num_gaussians", numGaussiansPerState);
        boolean r = false;
        for (int i3 = 0; i3 < numStates; ++i3) {
            for (int j = 0; j < numStreams; ++j) {
                for (int k = 0; k < numGaussiansPerState; ++k) {
                    float[] density = this.readFloatArray(dis, vectorLength[j]);
                    this.floorData(density, floor);
                    pool.put(i3 * numGaussiansPerState + k, (Object)density);
                }
            }
        }
        int checkSum = this.readInt(dis);
        dis.close();
        return pool;
    }

    protected DataInputStream readS3BinaryHeader(String location, String path, Properties props) throws IOException {
        String name;
        InputStream inputStream = this.getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new IOException("Can't open " + path);
        }
        DataInputStream dis = new DataInputStream(new BufferedInputStream(inputStream));
        String id = this.readWord(dis);
        if (!id.equals("s3")) {
            throw new IOException("Not proper s3 binary file " + location + path);
        }
        while ((name = this.readWord(dis)) != null && !name.equals("endhdr")) {
            String value = this.readWord(dis);
            props.setProperty(name, value);
        }
        int byteOrderMagic = dis.readInt();
        if (byteOrderMagic == 287454020) {
            this.swap = false;
        } else if (this.byteSwap(byteOrderMagic) == 287454020) {
            this.swap = true;
        } else {
            throw new IOException("Corrupt S3 file " + location + path);
        }
        return dis;
    }

    String readWord(DataInputStream dis) throws IOException {
        char c;
        StringBuffer sb = new StringBuffer();
        while (Character.isWhitespace(c = this.readChar(dis))) {
        }
        do {
            sb.append(c);
        } while (!Character.isWhitespace(c = this.readChar(dis)));
        return sb.toString();
    }

    private char readChar(DataInputStream dis) throws IOException {
        return (char)dis.readByte();
    }

    private int byteSwap(int val) {
        return 255 & val >> 24 | 65280 & val >> 8 | 16711680 & val << 8 | -16777216 & val << 24;
    }

    protected int readInt(DataInputStream dis) throws IOException {
        if (this.swap) {
            return Utilities.readLittleEndianInt((DataInputStream)dis);
        }
        return dis.readInt();
    }

    protected float readFloat(DataInputStream dis) throws IOException {
        float val = this.swap ? Utilities.readLittleEndianFloat((DataInputStream)dis) : dis.readFloat();
        return val;
    }

    protected void nonZeroFloor(float[] data, float floor) {
        for (int i = 0; i < data.length; ++i) {
            if ((double)data[i] == 0.0 || data[i] >= floor) continue;
            data[i] = floor;
        }
    }

    private void floorData(float[] data, float floor) {
        for (int i = 0; i < data.length; ++i) {
            if (data[i] >= floor) continue;
            data[i] = floor;
        }
    }

    protected void normalize(float[] data) {
        int i;
        float sum = 0.0f;
        for (i = 0; i < data.length; ++i) {
            sum += data[i];
        }
        if (sum != 0.0f) {
            for (i = 0; i < data.length; ++i) {
                data[i] = data[i] / sum;
            }
        }
    }

    private void dumpData(String name, float[] data) {
        System.out.println(" ----- " + name + " -----------");
        for (int i = 0; i < data.length; ++i) {
            System.out.println(name + " " + i + ": " + data[i]);
        }
    }

    protected void convertToLogMath(float[] data) {
        for (int i = 0; i < data.length; ++i) {
            data[i] = this.logMath.linearToLog((double)data[i]);
        }
    }

    protected float[] readFloatArray(DataInputStream dis, int size) throws IOException {
        float[] data = new float[size];
        for (int i = 0; i < size; ++i) {
            data[i] = this.readFloat(dis);
        }
        return data;
    }

    protected Pool loadHMMPool(boolean useCDUnits, InputStream inputStream, String path) throws FileNotFoundException, IOException {
        ExtendedStreamTokenizer est = new ExtendedStreamTokenizer(inputStream, 35, false);
        Pool pool = new Pool(path);
        this.logger.fine("Loading HMM file from: " + path);
        est.expectString("0.3");
        int numBase = est.getInt("numBase");
        est.expectString("n_base");
        int numTri = est.getInt("numTri");
        est.expectString("n_tri");
        int numStateMap = est.getInt("numStateMap");
        est.expectString("n_state_map");
        int numTiedState = est.getInt("numTiedState");
        est.expectString("n_tied_state");
        int numContextIndependentTiedState = est.getInt("numContextIndependentTiedState");
        est.expectString("n_tied_ci_state");
        int numTiedTransitionMatrices = est.getInt("numTiedTransitionMatrices");
        est.expectString("n_tied_tmat");
        int numStatePerHMM = numStateMap / (numTri + numBase);
        if (!$assertionsDisabled && numTiedState != this.mixtureWeightsPool.getFeature("num_senones", 0)) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && numTiedTransitionMatrices != this.matrixPool.size()) {
            throw new AssertionError();
        }
        for (int i = 0; i < numBase; ++i) {
            String name = est.getString();
            String left = est.getString();
            String right = est.getString();
            String position = est.getString();
            String attribute = est.getString();
            int tmat = est.getInt("tmat");
            int[] stid = new int[numStatePerHMM - 1];
            for (int j = 0; j < numStatePerHMM - 1; ++j) {
                stid[j] = est.getInt("j");
                if (!($assertionsDisabled || stid[j] >= 0 && stid[j] < numContextIndependentTiedState)) {
                    throw new AssertionError();
                }
            }
            est.expectString("N");
            if (!$assertionsDisabled && !left.equals("-")) {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && !right.equals("-")) {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && !position.equals("-")) {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && tmat >= numTiedTransitionMatrices) {
                throw new AssertionError();
            }
            Unit unit = this.unitManager.getUnit(name, attribute.equals("filler"));
            this.contextIndependentUnits.put(unit.getName(), unit);
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("Loaded " + (Object)unit);
            }
            if (unit.isFiller() && unit.getName().equals("SIL")) {
                unit = UnitManager.SILENCE;
            }
            float[][] transitionMatrix = (float[][])this.matrixPool.get(tmat);
            SenoneSequence ss = this.getSenoneSequence(stid);
            SenoneHMM hmm = new SenoneHMM(unit, ss, transitionMatrix, HMMPosition.lookup((String)position));
            this.hmmManager.put((HMM)hmm);
        }
        String lastUnitName = "";
        Unit lastUnit = null;
        int[] lastStid = null;
        SenoneSequence lastSenoneSequence = null;
        for (int i2 = 0; i2 < numTri; ++i2) {
            String name = est.getString();
            String left = est.getString();
            String right = est.getString();
            String position = est.getString();
            String attribute = est.getString();
            int tmat = est.getInt("tmat");
            int[] stid = new int[numStatePerHMM - 1];
            for (int j = 0; j < numStatePerHMM - 1; ++j) {
                stid[j] = est.getInt("j");
                if (!($assertionsDisabled || stid[j] >= numContextIndependentTiedState && stid[j] < numTiedState)) {
                    throw new AssertionError();
                }
            }
            est.expectString("N");
            if (!$assertionsDisabled && left.equals("-")) {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && right.equals("-")) {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && position.equals("-")) {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && !attribute.equals("n/a")) {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && tmat >= numTiedTransitionMatrices) {
                throw new AssertionError();
            }
            if (!useCDUnits) continue;
            Unit unit = null;
            String unitName = name + " " + left + " " + right;
            if (unitName.equals(lastUnitName)) {
                unit = lastUnit;
            } else {
                Unit[] leftContext = new Unit[]{(Unit)this.contextIndependentUnits.get(left)};
                Unit[] rightContext = new Unit[]{(Unit)this.contextIndependentUnits.get(right)};
                LeftRightContext context = LeftRightContext.get((Unit[])leftContext, (Unit[])rightContext);
                unit = this.unitManager.getUnit(name, false, (Context)context);
            }
            lastUnitName = unitName;
            lastUnit = unit;
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("Loaded " + (Object)unit);
            }
            float[][] transitionMatrix = (float[][])this.matrixPool.get(tmat);
            SenoneSequence ss = lastSenoneSequence;
            if (ss == null || !this.sameSenoneSequence(stid, lastStid)) {
                ss = this.getSenoneSequence(stid);
            }
            lastSenoneSequence = ss;
            lastStid = stid;
            SenoneHMM hmm = new SenoneHMM(unit, ss, transitionMatrix, HMMPosition.lookup((String)position));
            this.hmmManager.put((HMM)hmm);
        }
        est.close();
        return pool;
    }

    protected boolean sameSenoneSequence(int[] ssid1, int[] ssid2) {
        if (ssid1.length == ssid2.length) {
            for (int i = 0; i < ssid1.length; ++i) {
                if (ssid1[i] == ssid2[i]) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    protected SenoneSequence getSenoneSequence(int[] stateid) {
        Senone[] senones = new Senone[stateid.length];
        for (int i = 0; i < stateid.length; ++i) {
            senones[i] = (Senone)this.senonePool.get(stateid[i]);
        }
        return new SenoneSequence(senones);
    }

    private Pool loadMixtureWeightsAscii(String path, float floor) throws FileNotFoundException, IOException {
        this.logger.fine("Loading mixture weights from: " + path);
        InputStream inputStream = StreamFactory.getInputStream((String)this.location, (String)path);
        Pool pool = new Pool(path);
        ExtendedStreamTokenizer est = new ExtendedStreamTokenizer(inputStream, 35, false);
        est.expectString("mixw");
        int numStates = est.getInt("numStates");
        int numStreams = est.getInt("numStreams");
        int numGaussiansPerState = est.getInt("numGaussiansPerState");
        pool.setFeature("num_senones", numStates);
        pool.setFeature("num_streams", numStreams);
        pool.setFeature("num_gaussians", numGaussiansPerState);
        for (int i = 0; i < numStates; ++i) {
            est.expectString("mixw");
            est.expectString("[" + i);
            est.expectString("0]");
            float total = est.getFloat("total");
            float[] logMixtureWeight = new float[numGaussiansPerState];
            for (int j = 0; j < numGaussiansPerState; ++j) {
                float val = est.getFloat("mixwVal");
                if (val < floor) {
                    val = floor;
                }
                logMixtureWeight[j] = val;
            }
            this.convertToLogMath(logMixtureWeight);
            pool.put(i, (Object)logMixtureWeight);
        }
        est.close();
        return pool;
    }

    private Pool loadMixtureWeightsBinary(String path, float floor) throws FileNotFoundException, IOException {
        this.logger.fine("Loading mixture weights from: " + path);
        Properties props = new Properties();
        DataInputStream dis = this.readS3BinaryHeader(this.location, path, props);
        String version = props.getProperty("version");
        if (version == null || !version.equals("1.0")) {
            throw new IOException("Unsupported version in " + path);
        }
        String checksum = props.getProperty("chksum0");
        boolean doCheckSum = checksum != null && checksum.equals("yes");
        Pool pool = new Pool(path);
        int numStates = this.readInt(dis);
        int numStreams = this.readInt(dis);
        int numGaussiansPerState = this.readInt(dis);
        int numValues = this.readInt(dis);
        if (!$assertionsDisabled && numValues != numStates * numStreams * numGaussiansPerState) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && numStreams != 1) {
            throw new AssertionError();
        }
        pool.setFeature("num_senones", numStates);
        pool.setFeature("num_streams", numStreams);
        pool.setFeature("num_gaussians", numGaussiansPerState);
        for (int i = 0; i < numStates; ++i) {
            float[] logMixtureWeight = this.readFloatArray(dis, numGaussiansPerState);
            this.normalize(logMixtureWeight);
            this.floorData(logMixtureWeight, floor);
            this.convertToLogMath(logMixtureWeight);
            pool.put(i, (Object)logMixtureWeight);
        }
        dis.close();
        return pool;
    }

    protected Pool loadTransitionMatricesAscii(String path) throws FileNotFoundException, IOException {
        InputStream inputStream = StreamFactory.getInputStream((String)this.location, (String)path);
        this.logger.fine("Loading transition matrices from: " + path);
        Pool pool = new Pool(path);
        ExtendedStreamTokenizer est = new ExtendedStreamTokenizer(inputStream, 35, false);
        est.expectString("tmat");
        int numMatrices = est.getInt("numMatrices");
        int numStates = est.getInt("numStates");
        this.logger.fine("with " + numMatrices + " and " + numStates + " states, in " + (this.sparseForm ? "sparse" : "dense") + " form");
        for (int i = 0; i < numMatrices; ++i) {
            est.expectString("tmat");
            est.expectString("[" + i + "]");
            float[][] tmat = new float[numStates][numStates];
            for (int j = 0; j < numStates; ++j) {
                for (int k = 0; k < numStates; ++k) {
                    if (j < numStates - 1) {
                        if (this.sparseForm) {
                            if (k == j || k == j + 1) {
                                tmat[j][k] = est.getFloat("tmat value");
                            }
                        } else {
                            tmat[j][k] = est.getFloat("tmat value");
                        }
                    }
                    tmat[j][k] = this.logMath.linearToLog((double)tmat[j][k]);
                    if (!this.logger.isLoggable(Level.FINE)) continue;
                    this.logger.fine("tmat j " + j + " k " + k + " tm " + tmat[j][k]);
                }
            }
            pool.put(i, (Object)tmat);
        }
        est.close();
        return pool;
    }

    protected Pool loadTransitionMatricesBinary(String path) throws FileNotFoundException, IOException {
        this.logger.fine("Loading transition matrices from: " + path);
        Properties props = new Properties();
        DataInputStream dis = this.readS3BinaryHeader(this.location, path, props);
        String version = props.getProperty("version");
        if (version == null || !version.equals("1.0")) {
            throw new IOException("Unsupported version in " + path);
        }
        String checksum = props.getProperty("chksum0");
        boolean doCheckSum = checksum != null && checksum.equals("yes");
        Pool pool = new Pool(path);
        int numMatrices = this.readInt(dis);
        int numRows = this.readInt(dis);
        int numStates = this.readInt(dis);
        int numValues = this.readInt(dis);
        if (!$assertionsDisabled && numValues != numStates * numRows * numMatrices) {
            throw new AssertionError();
        }
        for (int i = 0; i < numMatrices; ++i) {
            float[][] tmat = new float[numStates][];
            tmat[numStates - 1] = new float[numStates];
            this.convertToLogMath(tmat[numStates - 1]);
            for (int j = 0; j < numRows; ++j) {
                tmat[j] = this.readFloatArray(dis, numStates);
                this.nonZeroFloor(tmat[j], 0.0f);
                this.normalize(tmat[j]);
                this.convertToLogMath(tmat[j]);
            }
            pool.put(i, (Object)tmat);
        }
        dis.close();
        return pool;
    }

    private Pool createDummyMatrixPool(String name) {
        Pool pool = new Pool(name);
        float[][] matrix = new float[this.vectorLength][this.vectorLength];
        this.logger.fine("creating dummy matrix pool " + name);
        for (int i = 0; i < this.vectorLength; ++i) {
            for (int j = 0; j < this.vectorLength; ++j) {
                matrix[i][j] = i == j ? 1.0f : 0.0f;
            }
        }
        pool.put(0, (Object)matrix);
        return pool;
    }

    private Pool createDummyVectorPool(String name) {
        this.logger.fine("creating dummy vector pool " + name);
        Pool pool = new Pool(name);
        float[] vector = new float[this.vectorLength];
        for (int i = 0; i < this.vectorLength; ++i) {
            vector[i] = 0.0f;
        }
        pool.put(0, (Object)vector);
        return pool;
    }

    public Pool getMeansPool() {
        return this.meansPool;
    }

    public Pool getMeansTransformationMatrixPool() {
        return this.meanTransformationMatrixPool;
    }

    public Pool getMeansTransformationVectorPool() {
        return this.meanTransformationVectorPool;
    }

    public Pool getVariancePool() {
        return this.variancePool;
    }

    public Pool getVarianceTransformationMatrixPool() {
        return this.varianceTransformationMatrixPool;
    }

    public Pool getVarianceTransformationVectorPool() {
        return this.varianceTransformationVectorPool;
    }

    public Pool getMixtureWeightPool() {
        return this.mixtureWeightsPool;
    }

    public Pool getTransitionMatrixPool() {
        return this.matrixPool;
    }

    public Pool getSenonePool() {
        return this.senonePool;
    }

    public int getLeftContextSize() {
        return 1;
    }

    public int getRightContextSize() {
        return 1;
    }

    public HMMManager getHMMManager() {
        return this.hmmManager;
    }

    public void logInfo() {
        this.logger.info("ModelLoader");
        this.meansPool.logInfo(this.logger);
        this.variancePool.logInfo(this.logger);
        this.matrixPool.logInfo(this.logger);
        this.senonePool.logInfo(this.logger);
        this.meanTransformationMatrixPool.logInfo(this.logger);
        this.meanTransformationVectorPool.logInfo(this.logger);
        this.varianceTransformationMatrixPool.logInfo(this.logger);
        this.varianceTransformationVectorPool.logInfo(this.logger);
        this.mixtureWeightsPool.logInfo(this.logger);
        this.senonePool.logInfo(this.logger);
        this.logger.info("Context Independent Unit Entries: " + this.contextIndependentUnits.size());
        this.hmmManager.logInfo(this.logger);
    }

    static {
        Class class_ = ModelLoader.class;
        $assertionsDisabled = !class_.desiredAssertionStatus();
    }
}

