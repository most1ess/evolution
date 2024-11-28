package simulation.entities;

import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.TreeMap;

public class Brain {
    private static final int seedMax = 50000;
    private MultiLayerNetwork net;
    private final int[] neuronNum;
    private final Activation firstActivation;
    private final Activation secondActivation;

    public Brain(int[] neuronNum) {
        this.neuronNum = neuronNum;
        this.firstActivation = generateActivation();
        this.secondActivation = generateActivation();

        generateNet();
        net.init();
        net.setListeners(new ScoreIterationListener(1));
    }

    public Brain(int[] neuronNum, Activation firstActivation, Activation secondActivation, MultiLayerNetwork net) {
        this.neuronNum = neuronNum;
        this.firstActivation = firstActivation;
        this.secondActivation = secondActivation;

        this.net = net;
        net.init();
        net.setListeners(new ScoreIterationListener(1));
    }

    public Brain mutate(double mutationChance, double mutationRadius) {
        Activation childFirstActivation;
        Activation childSecondActivation;
        if(Math.random() < mutationChance) childFirstActivation = generateActivation();
        else childFirstActivation = firstActivation;
        if(Math.random() < mutationChance) childSecondActivation = generateActivation();
        else childSecondActivation = secondActivation;

        INDArray params = net.params();
        double[] paramsArray = params.toDoubleVector();
        for(int i = 0; i<paramsArray.length; i++) {
            if(Math.random() < mutationChance) {
                double change = mutationRadius/2 - Math.random() * mutationRadius;
                paramsArray[i] += change;
            }
        }
        INDArray childParams = Nd4j.create(paramsArray);
        MultiLayerNetwork childNet = net.clone();
        childNet.setParams(childParams);

        return new Brain(neuronNum, childFirstActivation, childSecondActivation, childNet);
    }

    public int predict(double[] vision) {
        INDArray input = Nd4j.create(vision, 1, neuronNum[0]);
        INDArray out = net.output(input, false);
        double[] outputs = out.data().asDouble();

        double max = outputs[0];
        int maxNum = 0;
        for(int i = 0; i<outputs.length; i++) {
            if(outputs[i] > max) {
                maxNum = i;
                max = outputs[i];
            }
        }

        return maxNum;
    }

    private Activation generateActivation() {
        TreeMap<Integer, Activation> activationTreeMap = new TreeMap<>();
        activationTreeMap.put(0, Activation.SOFTMAX);
        activationTreeMap.put(1, Activation.CUBE);
        activationTreeMap.put(2, Activation.ELU);
        activationTreeMap.put(3, Activation.GELU);
        activationTreeMap.put(4, Activation.HARDSIGMOID);
        activationTreeMap.put(5, Activation.HARDTANH);
        activationTreeMap.put(6, Activation.IDENTITY);
        activationTreeMap.put(7, Activation.LEAKYRELU);
        activationTreeMap.put(8, Activation.MISH);
        activationTreeMap.put(9, Activation.RATIONALTANH);
        activationTreeMap.put(10, Activation.RELU);
        activationTreeMap.put(11, Activation.RECTIFIEDTANH);
        activationTreeMap.put(12, Activation.RELU6);
        activationTreeMap.put(13, Activation.RRELU);
        activationTreeMap.put(14, Activation.SELU);
        activationTreeMap.put(15, Activation.SIGMOID);
        activationTreeMap.put(16, Activation.SOFTPLUS);
        activationTreeMap.put(17, Activation.SOFTSIGN);
        activationTreeMap.put(18, Activation.SWISH);
        activationTreeMap.put(19, Activation.TANH);
        activationTreeMap.put(20, Activation.THRESHOLDEDRELU);

        int num = (int) (Math.random() * 21);
        return activationTreeMap.get(num);
    }

    private void generateNet() {
        int seed = (int) (Math.random() * seedMax);

        net = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(neuronNum[0]).nOut(neuronNum[1])
                        .activation(firstActivation) //Change this to RELU and you will see the net learns very well very quickly
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(secondActivation)
                        .nIn(neuronNum[1]).nOut(neuronNum[2]).build())
                .build());
    }

    public Activation getFirstActivation() {
        return firstActivation;
    }

    public Activation getSecondActivation() {
        return secondActivation;
    }

    public MultiLayerNetwork getNet() {
        return net;
    }
}
