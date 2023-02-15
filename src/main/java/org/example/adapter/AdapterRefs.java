package org.example.adapter;

import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.example.XSDGenerator;
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

    public AdapterRefs(String adapterName, OpenAPI openAPI, Map.Entry<String, PathItem> path) throws FileNotFoundException, SAXException {
        this.refs = new ArrayList<>();

        schemaLocation = System.getProperty("user.dir") + "/Converter/Processing/" + adapterName + ".xsd";

        // TODO: if more api types added, add them here
        if (path.getValue().getGet() != null) {
            fillRefs(path.getValue().getGet().getResponses());

            // List of parameters
            List<Parameter> getParams = path.getValue().getGet().getParameters();
            for (Parameter parameter : getParams) {
                if (!parameters.contains(parameter.getName()))
                    parameters.add(parameter.getName());
            }
        }
        if (path.getValue().getPost() != null) {
            fillRefs(path.getValue().getPost().getResponses());
        }
        if (path.getValue().getPut() != null) {
            fillRefs(path.getValue().getPut().getResponses());
        }
        if (path.getValue().getDelete() != null) {
            fillRefs(path.getValue().getDelete().getResponses());
        }
        if (path.getValue().getPatch() != null) {
            fillRefs(path.getValue().getPatch().getResponses());
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

        i = 0;
        for (String code : statusCodes) {
            // get the reference of the response
            if (response.get(code).getContent() == null) {
                i++;
                continue;
            }

            String ref = "";
            try {
                ref = response.get(code).getContent().get("*/*").getSchema().get$ref();
            } catch (NullPointerException e) {
                ref = response.get(code).getContent().get("application/json").getSchema().get$ref();
            }

            if (ref == null) {
                ref = response.get(code).getContent().get("application/json").getSchema().getItems().get$ref();
            }
            // add the reference to the array

            String[] parts = ref.split("/");
            String lastPart = parts[parts.length - 1];

            this.refs.add(lastPart);
            i++;
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
