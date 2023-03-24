/*
   Copyright 2023 WeAreFrank!
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package nl.wearefrank.openapifrankadapter.adapter;

import io.swagger.v3.oas.models.PathItem;
import org.json.simple.JSONObject;

import java.util.Map;

public class AdapterJsonfiyer {
    AdapterClass adapter;
    AdapterRefs adapterRefs;
    Map.Entry<String, PathItem> path;

    public AdapterJsonfiyer(AdapterClass adapter, AdapterRefs adapterRefs, Map.Entry<String, PathItem> path) {
        this.adapter = adapter;
        this.adapterRefs = adapterRefs;
        this.path = path;
    }

    // Convert string to JSON
    public JSONObject getAdapterJsonObj() {
        // First make the new JSONObject
        JSONObject adapterJson = new JSONObject();
        // Add the name
        adapterJson.put("name", this.adapter.getAdapterName());
        // Add the description
        adapterJson.put("description", this.adapter.getAdapterDescription());
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
        // Add the adapterRefs
        JSONObject adapterRefsJson = new JSONObject();
        adapterRefsJson.put("schema",  this.adapter.getAdapterName() + ".xsd");
        adapterRefsJson.put("root", this.adapterRefs.root);
        adapterRefsJson.put("responseRoot", this.adapterRefs.responseRoot);
        adapterJson.put("adapterRefs", adapterRefsJson);

        // instantiate params as a String array
        String[] params = adapterRefs.parameters.toArray(new String[0]);
        // add the params as a JSONObject
        adapterJson.put("parameters", params);

        // Return the JSONObject
        return adapterJson;
    }
}
