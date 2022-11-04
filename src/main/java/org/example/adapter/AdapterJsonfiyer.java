package org.example.adapter;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import org.example.adapter.AdapterClass;
import org.example.adapter.ApiListenerClass;
import org.example.adapter.ReceiverClass;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class AdapterJsonfiyer {
    Map.Entry<String, PathItem> path;
    OpenAPI openAPI;
    String fileName;

    public AdapterJsonfiyer(OpenAPI openAPI, Map.Entry<String, PathItem> path) {
        this.path = path;
        this.openAPI = openAPI;
        this.fileName = path.getKey().substring(1).replace("/", "-") + "_Configuration.json";
    }

    // Convert string to JSON
    public JSONObject getAdapterJsonObj() {
        // First make the new JSONObject
        JSONObject adapterJson = new JSONObject();
        // Add the name
        adapterJson.put("name", new AdapterClass(this.openAPI, this.path).getAdapterName());
        // Add the description
        adapterJson.put("description", new AdapterClass(this.openAPI, this.path).getAdapterDescription());
        // Add the type
        adapterJson.put("type", "adapter");
        // Add the receiver
        JSONObject receiverJson = new JSONObject();
        receiverJson.put("name", new ReceiverClass(this.path).getReceiverName());
        adapterJson.put("receiver", receiverJson);
        // Add the apiListener
        JSONObject apiListenerJson = new JSONObject();
        apiListenerJson.put("name", new ApiListenerClass(this.path).getApiListenerName());
        apiListenerJson.put("method", new ApiListenerClass(this.path).getMethod());
        apiListenerJson.put("uriPattern", new ApiListenerClass(this.path).getUriPattern());
        apiListenerJson.put("produces", new ApiListenerClass(this.path).getProduces());
        adapterJson.put("apiListener", apiListenerJson);
        // Array of key value pairs
        JSONObject[] keyValuePairs = new JSONObject[ParamSingleton.getInstance().params.length];

        // Return the JSONObject
        return adapterJson;
    }
}
