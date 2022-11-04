package org.example.adapter;


import org.apache.commons.lang3.tuple.Pair;

public class AdapterParams {
    // Paramamter composess of name and type tuple
    private Pair<String, String>[] param;

    public AdapterParams(){
        this.param = new Pair[0];
    }

    // Add name and type to param
    public void addParam(String name, String type) {
        // TODO: Does this work?
        if (type == "null") {
            type = "";
        }
        Pair<String, String> newParam = Pair.of(name, type);
        Pair<String, String>[] newParamArray = new Pair[this.param.length + 1];
        System.arraycopy(this.param, 0, newParamArray, 0, this.param.length);
        newParamArray[this.param.length] = newParam;
        this.param = newParamArray;
    }
}
