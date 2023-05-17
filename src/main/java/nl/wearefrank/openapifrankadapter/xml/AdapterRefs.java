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

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import nl.wearefrank.openapifrankadapter.XSDGenerator;
import nl.wearefrank.openapifrankadapter.error.ErrorApiResponse;
import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdapterRefs {
    private final ArrayList<String> refs;
    public Writer xsd;
    public String root;
    public String responseRoot;
    List<String> parameters = new ArrayList<>();

    public AdapterRefs(OpenAPI openAPI, Map.Entry<PathItem.HttpMethod, io.swagger.v3.oas.models.Operation> operation) throws SAXException, FileNotFoundException, ErrorApiResponse {
        try{
            // List of references
            this.refs = new ArrayList<>();
            fillRefs(operation.getValue().getResponses());

            // List of parameters
            if (operation.getValue().getParameters() != null) {
                List<Parameter> getParams = operation.getValue().getParameters();
                for (Parameter parameter : getParams) {
                    if (!parameters.contains(parameter.getName()))
                        parameters.add(parameter.getName());
                }
            }

            ArrayList<String> filteredReferences = uniqueRefs();

            try{
                root = filteredReferences.get(0);
                responseRoot = "";

                for (int i = 1; i < filteredReferences.size(); i++) {
                    // if the last value, add a closing tag
                    if (i == filteredReferences.size() - 1) {
                        responseRoot += filteredReferences.get(i);
                    } else {
                        responseRoot += filteredReferences.get(i) + ", ";
                    }
                }
            }
            catch (IndexOutOfBoundsException error) {
                // This means that the filteredReferences is empty, so the root and responseRoot should be empty
                root = null;
            }

            // Generate XSD IF there is a root
            if (root != null) {
                XSDGenerator xsdGenerator = new XSDGenerator();
                this.xsd = xsdGenerator.execute(openAPI, filteredReferences);
            }
        }
        catch (RuntimeException error) {
            System.out.println(LocalDateTime.now() + " [ERROR {AdapterRefs}] - " + error.getMessage());
            String message = "Error in getting the (unique/root) references, as they are null or incorrect. Check if the OpenApiSpecification does not contain errors or invalid entries..." + " [ERROR {AdapterRefs}] - " + error.getMessage();
            throw new ErrorApiResponse(500, message);
        }
    }

    // fill array with references
    public void fillRefs(ApiResponses response) {
        // make array of every possible api status code
        String[] statusCodes = new String[response.size()];
        int i = 0;
        for (Map.Entry<String, ApiResponse> entry : response.entrySet()) {
            statusCodes[i] = entry.getKey();
            i++;
        }

        for (String code : statusCodes) {
            // get the reference of the response
            if (response.get(code).getContent() == null) {
                continue;
            }

            for (Map.Entry<String, MediaType> entry : response.get(code).getContent().entrySet()) {
                String ref = entry.getValue().getSchema().get$ref();
                if (ref == null) {
                    if (entry.getValue().getSchema().getType().equals("array")) {
                        ref = entry.getValue().getSchema().getItems().get$ref();
                    }
                    else {
                        continue;
                    }
                }

                String[] parts = ref.split("/");
                String lastPart = parts[parts.length - 1];

                this.refs.add(lastPart);
            }
        }
    }

    //// Make an array with unique refs
    public ArrayList<String> uniqueRefs() {
        ArrayList<String> uniqueRefs = new ArrayList<>();
        for (String ref : this.refs) {
            if (!uniqueRefs.contains(ref)) {
                uniqueRefs.add(ref);
            }
        }
        return uniqueRefs;
    }
}
