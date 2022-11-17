package org.example.adapter;

import java.util.ArrayList;

public class ParamSingleton {
    // Singleton instance

    private static ParamSingleton instance = null;
    private ArrayList<String> params;
    private ParamSingleton() {
        this.params = new ArrayList<>();
    }

    public static ParamSingleton getInstance() {
        if (instance == null) {
            instance = new ParamSingleton();
        }
        return instance;
    }

    // add param to list
    public void addParam(String param){
        this.params.add(param);
    }

    // return list as an array of params
    public String[] getParams(){
        String[] paramsArray = new String[this.params.size()];
        int i = 0;
        for (String param : this.params) {
            paramsArray[i] = param;
            i++;
        }
        return paramsArray;
    }

    // reset list
    public void resetParams(){
        this.params = new ArrayList<>(); }
}
