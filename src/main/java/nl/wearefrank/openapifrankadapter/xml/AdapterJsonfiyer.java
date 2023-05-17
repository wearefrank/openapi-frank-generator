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

package nl.wearefrank.openapifrankadapter.xml;

import io.swagger.v3.oas.models.PathItem;
import nl.wearefrank.openapifrankadapter.Option;
import nl.wearefrank.openapifrankadapter.xml.receiver.ApiListenerClass;
import nl.wearefrank.openapifrankadapter.xml.receiver.ReceiverClass;
import nl.wearefrank.openapifrankadapter.xml.receiver.ReceiverJSONObject;
import org.json.simple.JSONObject;

import java.util.Map;

public class AdapterJsonfiyer {
    AdapterClass adapter;
    AdapterRefs adapterRefs;
    AdapterExits adapterExits;
    Map.Entry<String, PathItem> path;

    public AdapterJsonfiyer(AdapterClass adapter, AdapterRefs adapterRefs, AdapterExits adapterExits, Map.Entry<String, PathItem> path) {
        this.adapter = adapter;
        this.adapterRefs = adapterRefs;
        this.adapterExits = adapterExits;
        this.path = path;
    }

    // Convert string to JSON
    public JSONObject getAdapterJsonObj(Option templateOption) {
        // First make the new JSONObject
        JSONObject adapterJson = new JSONObject();
        // Add the name
        adapterJson.put("name", this.adapter.getAdapterName());
        // Add the description
        adapterJson.put("description", this.adapter.getAdapterDescription());
        // Add the type
        adapterJson.put("type", "xml");

        switch (templateOption){
            case RECEIVER:
                adapterJson = ReceiverJSONObject.getReceiverJsonObj(adapterJson, this.path);
            case SENDER:
                //adapterJson = ReceiverJSONObject.getReceiverJsonObj(adapterJson, this.path);
        }

        // Add the adapterRefs
        JSONObject adapterRefsJson = new JSONObject();
        adapterRefsJson.put("schema",  this.adapter.getAdapterName() + ".xsd");
        adapterRefsJson.put("root", this.adapterRefs.root);
        adapterRefsJson.put("responseRoot", this.adapterRefs.responseRoot);
        adapterJson.put("adapterRefs", adapterRefsJson);
        // add the params as a JSONObject
        adapterJson.put("parameters", adapterRefs.parameters.toArray(new String[0]));
        // Add the adapterExits
        adapterJson.put("exits",adapterExits.exits);

        // Return the JSONObject
        return adapterJson;
    }
}
