package org.example.adapter;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

public class ParamSingleton {
    public ArrayList<Pair<String, String>>[] params;

    private Integer size;

    private static ParamSingleton instance = null;

    private ParamSingleton() {
        this.params = new ArrayList[]{new ArrayList<Pair<String, String>>()};
        this.size = 1;
    }

    public static ParamSingleton getInstance() {
        if (instance == null) {
            instance = new ParamSingleton();
        }
        return instance;
    }

    public void addParameter(String name, String type) {
        // Create pair of name and type
        // Add this to the ArrayList, where size is the index of first dimension
        Pair<String, String> pair = Pair.of(name, type);
        if (this.params[size - 1] == null) {
            this.params[size - 1] = new ArrayList<Pair<String, String>>();
        }
        ArrayList<Pair<String, String>>[] newParams = new ArrayList[size + 1];
        System.out.println("newParams.length: " + newParams.length);
        System.out.println("params.length: " + params.length);
        for (int i = 0; i < this.params.length; i++) {
            newParams[i] = this.params[i];
        }
        newParams[size - 1].add(pair);
        this.params = newParams;
    }

    public void printParams() {
        // print params;
        for (int i = 0; i < this.params.length; i++) {
            System.out.println("params[" + i + "]: " + this.params[i]);
        }
    }

    // increase index
    public void increaseIndex() {
        this.size++;
    }

    // reset index
    public void resetIndex() {
        this.size = 0;
    }
}
