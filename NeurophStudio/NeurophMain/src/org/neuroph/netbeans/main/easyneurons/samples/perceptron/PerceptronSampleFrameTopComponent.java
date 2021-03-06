package org.neuroph.netbeans.main.easyneurons.samples.perceptron;

import org.neuroph.netbeans.classificationsample.ObservableTrainingSet;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import org.netbeans.api.settings.ConvertAsProperties;
import org.neuroph.core.Layer;
import org.neuroph.core.Neuron;
import org.neuroph.core.Weight;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.netbeans.visual.TrainingController;
import org.neuroph.netbeans.visual.NeuralNetAndDataSet;
import org.neuroph.netbeans.project.NeurophProjectFilesFactory;
import org.neuroph.nnet.Perceptron;
import org.neuroph.nnet.comp.neuron.ThresholdNeuron;
import org.neuroph.nnet.learning.BinaryDeltaRule;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//org.neuroph.netbeans.main.easyneurons.samples.perceptron//PerceptronSampleFrame//EN",
        autostore = false)
public final class PerceptronSampleFrameTopComponent extends TopComponent implements LearningEventListener {

    private static PerceptronSampleFrameTopComponent instance;
    /**
     * path to the icon used by the component and its open action
     */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "PerceptronSampleFrameTopComponent";

    public PerceptronSampleFrameTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(PerceptronSampleFrameTopComponent.class, "CTL_PerceptronSampleFrameTopComponent"));
        setToolTipText(NbBundle.getMessage(PerceptronSampleFrameTopComponent.class, "HINT_PerceptronSampleFrameTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMinimumSize(new java.awt.Dimension(750, 400));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 750, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files
     * only, i.e. deserialization routines; otherwise you could get a
     * non-deserialized instance. To obtain the singleton instance, use
     * {@link #findInstance}.
     */
    public static synchronized PerceptronSampleFrameTopComponent getDefault() {
        if (instance == null) {
            instance = new PerceptronSampleFrameTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the PerceptronSampleFrameTopComponent instance. Never call
     * {@link #getDefault} directly!
     */
    public static synchronized PerceptronSampleFrameTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(PerceptronSampleFrameTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof PerceptronSampleFrameTopComponent) {
            return (PerceptronSampleFrameTopComponent) win;
        }
        Logger.getLogger(PerceptronSampleFrameTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    Object readProperties(java.util.Properties p) {
        if (instance == null) {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    protected TruthTablePanel ttPanel;
    protected PatternSpacePanel psPanel;
    protected TrainingPanel tPanel;
    protected ImagePanel iPanel;
    public static final double[][] AND_INPUTS = {{0, 0, 0}, {1, 0, 0}, {0, 1, 0}, {1, 1, 1}};
    Image imageBuffer;
    Graphics2D drawingBuffer;
    Perceptron neuralNetwork;
    NeuralNetAndDataSet neuralNetAndDataSet;
    TrainingController trainingController;

    DataSet trainingSet = new DataSet(2, 1);
    ObservableTrainingSet perceptronSampleTrainingSet = new ObservableTrainingSet();
    private ConcurrentLinkedQueue<Vector> displayDataBuffer;
    private Thread drawingThread = null;
    private boolean isDrawing = false;

    public void initSample() {
        // on bi trebalo da vuce perceptron kreiran u projektu
        this.neuralNetwork = new Perceptron(2, 1);
        neuralNetwork.setLearningRule(new BinaryDeltaRule());
        neuralNetwork.setLabel("PerceptronSampleNetwork");
        neuralNetwork.getLearningRule().addListener(this);

        this.neuralNetAndDataSet = new NeuralNetAndDataSet(neuralNetwork, trainingSet);
        this.displayDataBuffer = new ConcurrentLinkedQueue<>();

        linkAllToOne();
    }

    public void linkAllToOne() {
        ttPanel = new TruthTablePanel(this, 200, 200);
        psPanel = new PatternSpacePanel(this, 200, 200);
        tPanel = new TrainingPanel(this);
        iPanel = new ImagePanel();

        this.setSize(750, 440);

        ttPanel.setSize(200, 200);
        psPanel.setSize(200, 200);
        tPanel.setSize(350, 240);
        iPanel.setSize(350, 350);

        add(ttPanel);
        add(psPanel);
        add(tPanel);
        add(iPanel);

        iPanel.setLocation(0, 40);
        tPanel.setLocation(350, 200);
        psPanel.setLocation(550, 0);
        ttPanel.setLocation(350, 0);

    }

    int q = 0;

//    public void update(Observable o, Object arg) {
//
//        Layer l = neuralNetwork.getLayerAt(1);
//        Neuron[] v = l.getNeurons();
//        Weight[] w = v[0].getWeights();
//
//        double ll = ((ThresholdNeuron) neuralNetwork.getLayerAt(1).getNeuronAt(0)).getThresh();
//        double w1 = w[0].getValue();
//        double w2 = w[1].getValue();
//
//        double sum = -ll + w1 + w2;
//        q++;
//
//        psPanel.updateLine(ll, w1, w2);
//        tPanel.updateJProgresBar(q);
//
//        neuralNetwork.getLayerAt(1).getNeuronAt(0).getWeights()[0].getValue();
//
//        iPanel.setNeuronFields(0, sum, ll, w[0].getValue(), w[1].getValue());
//    }
    // Variables declaration - do not modify
    // End of variables declaration
    public void buttonHasChanged(int index, double value) {
        outputs[index][0] = value;
        psPanel.updatePoints();
    }

    //TrainingPanel button pressed tran
    public void train(double learningRate, double maxError, int maxIterations) {
        if (!trainingSet.isEmpty()) {
            trainingSet.clear();
        }
        trainingSet.setLabel("PerceptronSampleTrainingSet");

        trainingSet.add(new DataSetRow(new double[]{0, 0}, outputs[0]));
        trainingSet.add(new DataSetRow(new double[]{1, 0}, outputs[1]));
        trainingSet.add(new DataSetRow(new double[]{0, 1}, outputs[2]));
        trainingSet.add(new DataSetRow(new double[]{1, 1}, outputs[3]));

        neuralNetAndDataSet.setDataSet(trainingSet);

        trainingController = new TrainingController(neuralNetAndDataSet);

        trainingController.setStepDRParams(learningRate, maxError, maxIterations);

        NeurophProjectFilesFactory.getDefault().createNeuralNetworkFile(neuralNetwork);
        NeurophProjectFilesFactory.getDefault().createTrainingSetFile(trainingSet);

        this.requestActive();
        trainingController.train();

        q = tPanel.setJProgresBar();
        z = 0;
    }

    // why this doesent work?
    public void step() {
        ((SupervisedLearning) neuralNetwork.getLearningRule()).doLearningEpoch(trainingSet);
        redraw();
    }

    public void stopTraining() {
        neuralNetAndDataSet.stopTraining();
    }

    public void randomize() {
        neuralNetAndDataSet.randomize();
        redraw();
        tPanel.setJProgresBar();
    }
    int z = 0;

    public void test() {
        if (z > 3) {
            z = 0;
        }
        switch (z) {
            case 0:
                neuralNetAndDataSet.setInput("0 0");
                iPanel.setInputFields(new double[]{0, 0});
                break;
            case 1:
                neuralNetAndDataSet.setInput("1 0");
                iPanel.setInputFields(new double[]{1, 0});
                break;
            case 2:
                neuralNetAndDataSet.setInput("0 1");
                iPanel.setInputFields(new double[]{0, 1});
                break;
            case 3:
                neuralNetAndDataSet.setInput("1 1");
                iPanel.setInputFields(new double[]{1, 1});
                break;
        }

        double sum = neuralNetwork.getOutput()[0];

        iPanel.setOutputField(sum);

        z++;
    }
    public double[][] outputs = {{0}, {0}, {0}, {0}};

    @Override
    public void handleLearningEvent(LearningEvent le) {
        redraw();
    }

    private void redraw() {
        Layer layer = neuralNetwork.getLayerAt(1);
        List<Neuron> neurons = layer.getNeurons();
        Weight[] w = neurons.get(0).getWeights();

        double ll = ((ThresholdNeuron) neuralNetwork.getLayerAt(1).getNeuronAt(0)).getThresh();
        double w1 = w[0].getValue();
        double w2 = w[1].getValue();

        double sum = -ll + w1 + w2;
        q++;

        psPanel.updateLine(ll, w1, w2);
        tPanel.updateJProgresBar(q);

        neuralNetwork.getLayerAt(1).getNeuronAt(0).getWeights()[0].getValue();

        iPanel.setNeuronFields(0, sum, ll, w[0].getValue(), w[1].getValue());
    }
}
