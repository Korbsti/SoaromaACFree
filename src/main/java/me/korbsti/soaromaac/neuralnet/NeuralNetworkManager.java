package me.korbsti.soaromaac.neuralnet;

import me.korbsti.soaromaac.Main;

import java.io.*;

public class NeuralNetworkManager {

    // file path to downloads folder
    private static final String FILE_PATH = System.getProperty("user.home") + File.separator + "Downloads" + File.separator;

    Main plugin;
    // create a constructor with the Main instance as a parameter and set it to plugin
    public NeuralNetworkManager(Main plugin) {
        this.plugin = plugin;
    }



    public static void writeNetworkToFile(NeuralNetwork network, String fileName) {

        File file = new File(FILE_PATH + fileName);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            System.out.println("Writing to file: " + file.getAbsolutePath());
            // write weights and biases to the file
            writer.write("inputNodes=" + network.inputNodes + "\n");
            writer.write("hiddenLayers=" + network.hiddenLayersCount + "\n");
            writer.write("hiddenNodesCount=" + network.hiddenLayersNeuronCount + "\n" );
            writer.write("weightDecayRate=" + network.weightDecayFactor + "\n");
            writer.write("outputNodes=" + network.outputNodes+ "\n");

            // Loop through all weights and biases and write them to the file
            for (int i = 0; i < network.weights.length; i++) {
                for (int j = 0; j < network.weights[i].length; j++) {
                    for (int k = 0; k < network.weights[i][j].length; k++) {
                        writer.write("w+" + i + "+" + j + "+" + k + "+" + network.weights[i][j][k]+ "\n");
                    }
                }
            }

            for (int i = 0; i != network.inputNodes; i++) {
                writer.write("bI+"+i +"+"+ network.inputLayer.get(i).getBias() + "\n");
            }

            for (int i = 0; i != network.hiddenLayersCount; i++) {
                for (int j = 0; j != network.hiddenLayersNeuronCount; j++) {
                    writer.write("bH+" + i + "+"+j + "+"+ network.hiddenLayers.get(i).get(j).getBias() + "\n");
                }

            }

            for (int i = 0; i != network.outputNodes; i++) {
                writer.write("bO+" +i+"+"+ network.outputLayer.get(i).getBias() + "\n");
            }

            System.out.println("Finished writing to file");
            writer.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public NeuralNetwork loadNetwork(File file){
        int inputNodes = 0;
        int hiddenLayers = 0;
        int hiddenNodesCount = 0;
        int outputNodes = 0;
        double learningRate = 0.1;
        double weightDecayRate = 0.00001;
        double[][][] weights = null;
        NeuralNetwork network = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            // loop through the file
            String line;
            while((line = reader.readLine()) != null){
                if(line.contains("inputNodes=")){
                    inputNodes = Integer.parseInt(line.substring(11));
                } else if(line.contains("hiddenLayers=")){
                    hiddenLayers = Integer.parseInt(line.substring(13));
                } else if(line.contains("hiddenNodesCount=")) {
                    hiddenNodesCount = Integer.parseInt(line.substring(17));
                } else if(line.contains("learningRate=")){
                    learningRate = Double.parseDouble((line.substring(13)));
                }  else if(line.contains("weightDecayRate=")){
                    weightDecayRate = Double.parseDouble((line.substring(16)));
                }else if(line.contains("outputNodes=")){
                    outputNodes = Integer.parseInt(line.substring(12));
                    weights = new double[hiddenLayers+1][hiddenNodesCount*2][hiddenNodesCount*100*hiddenLayers];
                    network = new NeuralNetwork(plugin, inputNodes, hiddenLayers, hiddenNodesCount, outputNodes, learningRate, weightDecayRate);
                } else if(line.contains("w+")){
                    String[] split = line.split("\\+");
                    int i = Integer.parseInt(split[1]);
                    int j = Integer.parseInt(split[2]);
                    int k = Integer.parseInt(split[3]);
                    double weight = Double.parseDouble(split[4]);
                    assert weights != null;
                    weights[i][j][k] = weight;
                } else if(line.contains("bI+")){
                    String[] split = line.split("\\+");
                    int i = Integer.parseInt(split[1]);
                    double bias = Double.parseDouble(split[2]);
                    assert network != null;
                    network.inputLayer.get(i).setBias(bias);

                } else if(line.contains("bH+")){
                    String[] split = line.split("\\+");
                    int i = Integer.parseInt(split[1]);
                    int j = Integer.parseInt(split[2]);
                    double bias = Double.parseDouble(split[3]);
                    assert network != null;
                    network.hiddenLayers.get(i).get(j).setBias(bias);
                } else if(line.contains("bO+")){
                    String[] split = line.split("\\+");
                    int i = Integer.parseInt(split[1]);
                    double bias = Double.parseDouble(split[2]);
                    assert network != null;
                    network.outputLayer.get(i).setBias(bias);
                }


            }

            network.weights = weights;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return network;
    }





}
