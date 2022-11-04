package org.example.adapter;

import io.swagger.v3.oas.models.PathItem;

import java.util.Map;

public class ReceiverClass {
    private String receiverName;
    public ReceiverClass(Map.Entry<String, PathItem> path) {
        this.receiverName = path.getKey().substring(1);  // receiverName: remove the first slash
    }
    public String getReceiverName() {
        return "Receiver-" + this.receiverName;
    }
}
