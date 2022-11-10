package org.example;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.example.adapter.AdapterClass;
import org.example.adapter.AdapterJsonfiyer;
import org.example.adapter.AdapterRefs;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class XMLGenerator {
    static public void execute(OpenAPI openAPI) throws IOException, SAXException {
        Paths paths = openAPI.getPaths();

        // For loop going through all the paths and instantiating a new AdapterClass
        for (Map.Entry<String, PathItem> path : paths.entrySet()) {
            // Create a new AdapterClass
            AdapterClass adapter = new AdapterClass(openAPI, path);

            //// Template ////
            // Get the template file
            File templateFile = new File(System.getProperty("user.dir") + "/src/main/java/org/example/template.hbs");
            String templateString = new String(java.nio.file.Files.readAllBytes(templateFile.toPath()));

            // Create a new Handlebars object
            Handlebars handlebars = new Handlebars();
            Template template = handlebars.compileInline(templateString);

            // Create JSON and apply the template
            AdapterJsonfiyer adapterJsonfiyer = new AdapterJsonfiyer(openAPI, path);
            String adapterTemplate = template.apply(adapterJsonfiyer.getAdapterJsonObj());

            // Print the template
            System.out.println(adapterTemplate);

            // Export the template to xml file
            File xmlFile = new File(System.getProperty("user.dir") + "/Converter/Processing/" + adapter.getAdapterName() + ".xml");
            // Write string to file
            java.nio.file.Files.write(xmlFile.toPath(), adapterTemplate.getBytes());

            AdapterRefs adapterRefs = new AdapterRefs(adapter.getAdapterName(), openAPI, path);
        }
    }
}
