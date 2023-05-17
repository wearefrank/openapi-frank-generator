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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdapterExits {
    List<Response> exits;

    public AdapterExits() {
        this.exits = new ArrayList<>();
    }
    public List<Response> GetAdapterExits(Map.Entry<PathItem.HttpMethod, io.swagger.v3.oas.models.Operation> operation) {
        for (String responseCode : operation.getValue().getResponses().keySet()) {

            boolean skip = false;
            //// Check if the REF is in RESPONSE layer
            if (operation.getValue().getResponses().get(responseCode).get$ref() != null) {
                exits.add(new Response(responseCode, "false"));
                skip = true;
            }

            //// Check if the REF is in SCHEMA layer
            try {
                for (String schema : operation.getValue().getResponses().get(responseCode).getContent().keySet()) {
                    if (operation.getValue().getResponses().get(responseCode).getContent().get(schema).getSchema().get$ref() != null && !skip) {
                        exits.add(new Response(responseCode, "false"));
                        skip = true;
                    }
                }
            }
            catch (NullPointerException error) {
                // Do nothinh
            }

            //// Check if the REF is in ITEMS layer
            try {
                for (String schema : operation.getValue().getResponses().get(responseCode).getContent().keySet()) {
                    if (operation.getValue().getResponses().get(responseCode).getContent().get(schema).getSchema().getItems().get$ref() != null && !skip) {
                        exits.add(new Response(responseCode, "false"));
                        skip = true;
                    }
                }
            }
            catch (NullPointerException error) {
                // Do nothing
            }
            //// No REF found
            if (!skip) {exits.add(new Response(responseCode, "true"));}
        }
        return exits;
    }
}
