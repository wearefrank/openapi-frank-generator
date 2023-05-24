package nl.wearefrank.openapifrankadapter.xml.sender;

import io.swagger.v3.oas.models.PathItem;
import nl.wearefrank.openapifrankadapter.xml.sender.HttpSenderClass;
import nl.wearefrank.openapifrankadapter.xml.sender.SenderClass;
import org.json.simple.JSONObject;

import java.util.Map;

public class SenderJSONObject {
    public static JSONObject getSenderJsonObj(JSONObject adapterJson, Map.Entry<String, PathItem> path) {
        // Add the sender
        JSONObject senderJson = new JSONObject();
        senderJson.put("name", new SenderClass(path).getSenderName());
        adapterJson.put("sender", senderJson);
        // Add the httpSender
        JSONObject httpSenderJson = new JSONObject();
        httpSenderJson.put("name", new HttpSenderClass(path).getApiListenerName());
        httpSenderJson.put("method", new HttpSenderClass(path).getMethod());
        httpSenderJson.put("url", new HttpSenderClass(path).getUrl());
        adapterJson.put("httpSender", httpSenderJson);
        // Return the JSONObject
        return adapterJson;
    }
}
