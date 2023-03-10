package nl.wearefrank.openapifrankadapter.adapter;

import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import nl.wearefrank.openapifrankadapter.XSDGenerator;
import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdapterRefs {
    private final ArrayList<String> refs;

    String schemaLocation;
    String root;
    String responseRoot;
    List<String> parameters = new ArrayList<>();

    public AdapterRefs(String adapterName, String folderPath, OpenAPI openAPI, Map.Entry<PathItem.HttpMethod, io.swagger.v3.oas.models.Operation> operation) throws FileNotFoundException, SAXException {
        this.refs = new ArrayList<>();

        schemaLocation = folderPath + "/" + adapterName + ".xsd";

        // TODO: if more api types added, add them here
        fillRefs(operation.getValue().getResponses());

        // List of parameters
        if (operation.getValue().getParameters() != null) {
            List<Parameter> getParams = operation.getValue().getParameters();
            for (Parameter parameter : getParams) {
                if (!parameters.contains(parameter.getName()))
                    parameters.add(parameter.getName());
            }
        }

        XSDGenerator xsdGenerator = new XSDGenerator();
        xsdGenerator.execute(schemaLocation, openAPI, uniqueRefs());

        root = uniqueRefs().get(0);

        for (int i = 1; i < uniqueRefs().size(); i++) {
            // if the last value, add a closing tag
            if (i == uniqueRefs().size() - 1) {
                responseRoot += uniqueRefs().get(i);
            } else {
                responseRoot += uniqueRefs().get(i) + ",";
            }
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
                //System.out.println(entry.getKey());
                String ref = entry.getValue().getSchema().get$ref();

                if (ref == null) {
                    if (entry.getValue().getSchema().getType().equals("array")) {
                        ref = entry.getValue().getSchema().getItems().get$ref();
                    }
                    else {
                        System.out.println("No ref found " + entry);
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
