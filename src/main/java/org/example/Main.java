package org.example;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.xml.sax.SAXException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, SAXException {

        //// INITIALIZATION ////
        // get source of current directory
        String source = System.getProperty("user.dir") + "/Converter/Intake/openapi.json";

        // Add cli option
        if (args.length > 0) {
            source = args[0];
        }
        // ! Change this value if debugging ! //
        boolean debug = false;

        // Read the openapi specification off of a file or url
        SwaggerParseResult result = new OpenAPIParser().readLocation(source, null, null);
        OpenAPI openAPI = result.getOpenAPI();

        XSDGenerator xsdGenerator = new XSDGenerator();
        xsdGenerator.execute(openAPI);

        XMLGenerator.execute(openAPI);

    }
}