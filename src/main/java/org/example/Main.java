package org.example;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws IOException, SAXException, URISyntaxException {

        //// INITIALIZATION ////
        // Get source of current directory
        String source = System.getProperty("user.dir") + "/Converter/Intake/openapi.json";

        // Add cli option (if given source is another file location)
        if (args.length > 0) {
            source = args[0];
        }
        // ! Change this value if debugging ! //
        boolean debug = false;

        // Read the openapi specification off of a file or url
        SwaggerParseResult result = new OpenAPIParser().readLocation(source, null, null);

        OpenAPI openAPI = result.getOpenAPI();
        XMLGenerator.execute(openAPI);
    }
}