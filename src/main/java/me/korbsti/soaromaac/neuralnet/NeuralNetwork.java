package me.korbsti.soaromaac.neuralnet;

import me.korbsti.soaromaac.Main;
import me.korbsti.soaromaac.neuralnet.Node;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {

    public int generation = 0;
    public int success = 0;
    public int fail = 0;
    public double learningRate;
    public int inputNodes;
    public int hiddenLayersCount;
    public int outputNodes;

    public int weightLayers;

    public int hiddenLayersNeuronCount;

    // first index is layer, second index is node It's connected to, third index is the weight of the specified node
    public double[][][] weights;

    // first index is layer, second index is node

    public List<Node> inputLayer = new ArrayList<>();
    public List<List<Node>> hiddenLayers = new ArrayList<>();
    public List<Node> outputLayer = new ArrayList<>();
    private int arraySizes;
    public double weightDecayFactor = 0;

    public int stopLearningAt = 0;
    public double learningSuccessThreshold = 0.0;


    public boolean stopNetworkFromLearning = false;
    Main plugin;
    public NeuralNetwork(Main plugin, int inputNodes, int hiddenLayers, int hiddenLayersNeuronCount, int outputNodes, double learningRate, double weightDecayFactor) {
        this.inputNodes = inputNodes;
        this.hiddenLayersCount = hiddenLayers;
        this.outputNodes = outputNodes;
        this.hiddenLayersNeuronCount = hiddenLayersNeuronCount;
        this.weightLayers = hiddenLayers + 1;
        this.learningRate = learningRate;
        this.arraySizes = hiddenLayersNeuronCount * weightLayers * inputNodes;
        this.stopLearningAt = stopLearningAt;
        this.learningSuccessThreshold = learningSuccessThreshold;
        this.plugin = plugin;
        this.weightDecayFactor = weightDecayFactor;

        this.weights = new double[weightLayers][hiddenLayersNeuronCount][arraySizes];

        // initialize weights with a random value between 0 and 1

        for (int i = 0; i != weightLayers; i++) {
            for (int j = 0; j != hiddenLayersNeuronCount; j++) {
                for (int k = 0; k != arraySizes; k++) {
                    weights[i][j][k] = Math.random();
                }
            }
        }


        for (int i = 0; i != inputNodes; i++) {
            inputLayer.add(new Node(0, 0));
        }

        for (int i = 0; i != hiddenLayers; i++) {
            this.hiddenLayers.add(new ArrayList<>());
            for (int j = 0; j != hiddenLayersNeuronCount; j++) {
                this.hiddenLayers.get(i).add(new Node(Math.random(), Math.random()));
            }
        }

        for (int i = 0; i != outputNodes; i++) {
            outputLayer.add(new Node(0, Math.random()));
        }


    }


    public double[] feedForward(double[] input) {
        for (int i = 0; i != inputNodes; i++) {
            inputLayer.get(i).setValue(input[i]);
        }

        for (int i = 0; i != hiddenLayersNeuronCount; i++) {
            double netOutput = 0;


            for (int j = 0; j != inputNodes; j++) {
                netOutput += inputLayer.get(j).getValue() * weights[0][i][j];
            }
            hiddenLayers.get(0).get(i).setValue(activationFunction((netOutput + hiddenLayers.get(0).get(i).getBias())));
        }

        // calculate hidden layers values
        for (int i = 1; i != hiddenLayersCount; i++) {
            for (int j = 0; j != hiddenLayersNeuronCount; j++) {
                double netOutput = 0;

                for (int k = 0; k != hiddenLayersNeuronCount; k++) {
                    netOutput += hiddenLayers.get(i - 1).get(k).getValue() * weights[i][j][k];

                }

                hiddenLayers.get(i).get(j).setValue(activationFunction((netOutput + hiddenLayers.get(i).get(j).getBias())));
            }
        }


        double[] returning = new double[outputNodes];
        // calculate output layer
        for (int i = 0; i != outputNodes; i++) {

            double total = 0;

            for (int j = 0; j != hiddenLayersNeuronCount; j++) {
                total += hiddenLayers.get(hiddenLayersCount - 1).get(j).getValue() * weights[weightLayers - 1][i][j];
            }
            double value = activationFunction(total + outputLayer.get(i).getBias());
            outputLayer.get(i).setValue(value);
            returning[i] = value;
        }


        return returning;

    }


    private void backpropagation(double[] expected) {
        for (int i = 0; i != outputNodes; i++) {
            outputLayer.get(i).error = expected[i] - outputLayer.get(i).getValue();
        }

        for (int i = hiddenLayersCount - 1; i >= 0; i--) {
            for (int j = 0; j != hiddenLayersNeuronCount; j++) {
                if (i == hiddenLayersCount - 1) {
                    double error = 0;
                    for (int k = 0; k != outputNodes; k++) {
                        error += outputLayer.get(k).error * weights[i + 1][k][j];
                    }
                    hiddenLayers.get(i).get(j).error = error;
                } else {
                    double error = 0;
                    for (int k = 0; k != hiddenLayersNeuronCount; k++) {
                        error += hiddenLayers.get(i + 1).get(k).error * weights[i + 1][k][j];
                    }
                    hiddenLayers.get(i).get(j).error = error;
                }
            }
        }

        for (int i = 0; i != hiddenLayersCount; i++) {
            for (int j = 0; j != hiddenLayersNeuronCount; j++) {
                double error = hiddenLayers.get(i).get(j).error;
                if (i == 0) {
                    for (int k = 0; k != inputNodes; k++) {
                        weights[i][j][k] += learningRate * error * inputLayer.get(k).getValue() - weightDecayFactor * weights[i][j][k];
                    }
                    hiddenLayers.get(i).get(j).bias += learningRate * error - weightDecayFactor * hiddenLayers.get(i).get(j).bias;
                } else {
                    for (int k = 0; k != hiddenLayersNeuronCount; k++) {
                        weights[i][j][k] += learningRate * error * hiddenLayers.get(i - 1).get(k).getValue() - weightDecayFactor * weights[i][j][k];
                    }
                    hiddenLayers.get(i).get(j).bias += learningRate * error - weightDecayFactor * hiddenLayers.get(i).get(j).bias;
                }
            }
        }

        for (int i = 0; i != outputNodes; i++) {
            double error = outputLayer.get(i).error;
            for (int j = 0; j != hiddenLayersNeuronCount; j++) {
                weights[hiddenLayersCount][i][j] += learningRate * error * hiddenLayers.get(hiddenLayersCount - 1).get(j).getValue() - weightDecayFactor * weights[hiddenLayersCount][i][j];
            }
            outputLayer.get(i).bias += learningRate * error - weightDecayFactor * outputLayer.get(i).bias;
        }
    }









    // visualize the neural network
    public void visualize() {
        System.out.println("Input Layer");
        for (int i = 0; i != inputNodes; i++) {
            System.out.println("Node " + i + " = " + inputLayer.get(i).getValue());
        }

        System.out.println("Hidden Layers");
        for (int i = 0; i != hiddenLayersCount; i++) {
            System.out.println("Hidden Layer " + i);
            for (int j = 0; j != hiddenLayersNeuronCount; j++) {

                System.out.println("Node " + j + " = " + hiddenLayers.get(i).get(j).getValue());
            }
        }

        System.out.println("Output Layer");
        for (int i = 0; i != outputNodes; i++) {
            System.out.println("Node " + i + " = " + outputLayer.get(i).getValue());
        }


        // visualize the weights
        System.out.println("Weights");
        for (int i = 0; i != weightLayers; i++) {
            System.out.println("Layer " + i);
            for (int j = 0; j != hiddenLayersNeuronCount; j++) {
                System.out.println("Node " + j);
                for (int k = 0; k != arraySizes; k++) {
                    System.out.println("Weight " + k + " = " + weights[i][j][k]);

                }
            }
        }
        // visualize the biases
        System.out.println("Biases");
        for (int i = 0; i != hiddenLayersCount; i++) {
            System.out.println("Hidden Layer " + i);
            for (int j = 0; j != hiddenLayersNeuronCount; j++) {
                System.out.println("Node " + j + " = " + hiddenLayers.get(i).get(j).getBias());
            }
        }

    }

    // return true if a number is close enough to another number
    public boolean closeEnough(double a, double b) {
        return Math.abs(a - b) < learningSuccessThreshold;
    }

    public void train(double[] input, double[] expectedOutput) {
        if (stopNetworkFromLearning) {
            return;
        }
        double[] d = feedForward(input);
        backpropagation(expectedOutput);
        generation++;

        //visualize();

        // print output of neural network
        /*
            System.out.println("Generation " + generation);
            System.out.println("Input: " + input[0] + " " + input[1]);
            System.out.println("Expected Output: " + expectedOutput[0] + " " + expectedOutput[1]);
            System.out.println("Output: " + d[0] + " " + d[1]);
            System.out.println("Error: " + costFunction(expectedOutput[0], d[0]) + " " + costFunction(expectedOutput[1], d[1]));
            System.out.println("Close Enough: " + closeEnough(expectedOutput[0], d[0]) + " " + closeEnough(expectedOutput[1], d[1]));
            System.out.println();
        */
        if(closeEnough(expectedOutput[0], d[0])) {
            success++;
        }
        System.out.println("Error: " + (costFunction(expectedOutput[0], d[0])));

        if(success > stopLearningAt) {
            stopNetworkFromLearning = true;
            System.out.println("Network has learned");
            System.out.println("Generation " + generation);
            System.out.println("Expected Output: " + expectedOutput[0] + " " + expectedOutput[1]);
            System.out.println("Output: " + d[0] + " " + d[1]);
            System.out.println("Error: " + costFunction(expectedOutput[0], d[0]) + " " + costFunction(expectedOutput[1], d[1]));
            System.out.println("Close Enough: " + closeEnough(expectedOutput[0], d[0]) + " " + closeEnough(expectedOutput[1], d[1]));
            System.out.println();
            plugin.neuralNetworkManager.writeNetworkToFile(this, "network.txt");

        }




    }

    public double costFunction(double expected, double actual) {
        return 0.5 * Math.pow(expected - actual, 2);
    }

    public double sigmoidFunction(double x) {

        return 1 / (1 + Math.exp(-x));
    }

    public double reluFunction(double x) {
        return Math.max(0, x);
    }

    public double activationFunction(double x) {
        return sigmoidFunction(x);
    }


    public double outputChangeRespectToTotalNet(double target, double outputOfNeuron) {

        return -(target - outputOfNeuron) * outputOfNeuron * (1 - outputOfNeuron);
    }


    public double activationFunctionDerivative(double x) {
        return x * (1 - x);
    }

    public double totalErrorWithRespectToOutput(double expected, double actual) {
        return -(expected - actual);
    }

    // create a sigmoid derivative function
    public double sigmoidDerivative(double x) {
        return x * (1 - x);
    }

    /*
    public double changeWeight(double weight, double outputChangeRespectToTotalNet) {

        return weight - learningRate * outputChangeRespectToTotalNet;
    }

        public double derivativeFunction(double output) {
        return output * (1 - output);
    }


    public double totalErrorWithRespectToWeight(double totalError, double output, double input) {
        return totalError * output * (1 - output) * input;
    }


    public double calculateValuesOfHiddenLayer(double weight, double outputOfNeuron, double outputOfHiddenNeuron) {

        return weight * outputOfNeuron * (1 - outputOfNeuron) * outputOfHiddenNeuron;
    }

    public double totalErrorWithRespectToLog(double output) {
        return output * (1 - output);
    */

}


