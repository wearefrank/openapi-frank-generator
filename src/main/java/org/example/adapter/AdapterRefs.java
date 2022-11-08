package org.example.adapter;

import com.hierynomus.smbj.share.Open;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.example.XSDGenerator;
import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdapterRefs {
    private ArrayList<String> refs;

    public AdapterRefs(OpenAPI openAPI, Map.Entry<String, PathItem> path) throws FileNotFoundException, SAXException {
        this.refs = new ArrayList<>();

        // TODO: if more api types added, add them here
        if(path.getValue().getGet() != null){
            fillRefs(path.getValue().getGet().getResponses());
        }
        if(path.getValue().getPost() != null){
            fillRefs(path.getValue().getPost().getResponses());
        }
        if(path.getValue().getPut() != null){
            fillRefs(path.getValue().getPut().getResponses());
        }
        if(path.getValue().getDelete() != null){
            fillRefs(path.getValue().getDelete().getResponses());
        }
        if(path.getValue().getPatch() != null){
            fillRefs(path.getValue().getPatch().getResponses());
        }

        XSDGenerator xsdGenerator = new XSDGenerator();
        xsdGenerator.execute(openAPI, uniqueRefs());
        System.out.println(uniqueRefs());
    }
    // fill array with references
    public void fillRefs(ApiResponses response){
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
            String ref = response.get(code).getContent().get("*/*").getSchema().get$ref();
            // add the reference to the array
            String[] parts = ref.split("/");
            String lastPart = parts[parts.length - 1];
            System.out.println(lastPart);

            this.refs.add(lastPart);
            i++;
        }
    }

    //// print the refs
    public void printRefs() {
        for (String ref : this.refs) {
            System.out.println(ref);
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
