package nl.wearefrank.openapifrankadapter.xml.receiver;

import io.swagger.v3.oas.models.PathItem;
import org.json.simple.JSONObject;

import java.util.Map;

public class ReceiverJSONObject {

    public static JSONObject getReceiverJsonObj(JSONObject adapterJson, Map.Entry<String, PathItem> path) {
        // Add the receiver
        JSONObject receiverJson = new JSONObject();
        receiverJson.put("name", new ReceiverClass(path).getReceiverName());
        adapterJson.put("receiver", receiverJson);
        // Add the apiListener
        JSONObject apiListenerJson = new JSONObject();
        apiListenerJson.put("name", new ApiListenerClass(path).getApiListenerName());
        apiListenerJson.put("method", new ApiListenerClass(path).getMethod());
        apiListenerJson.put("uriPattern", new ApiListenerClass(path).getUriPattern());
        apiListenerJson.put("produces", new ApiListenerClass(path).getProduces());
        adapterJson.put("apiListener", apiListenerJson);
        // Return the JSONObject
        return adapterJson;
    }
}
