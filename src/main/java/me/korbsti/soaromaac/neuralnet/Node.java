package me.korbsti.soaromaac.neuralnet;

public class Node {

    public double value;

    public double bias;
    public double error;

    public Node(double value, double bias) {
        this.value = value;
        this.bias = bias;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    public double derivative(){
        return value * (1 - value);
    }

}
