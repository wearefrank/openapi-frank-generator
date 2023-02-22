package nl.wearefrank.openapifrankadapter;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import nl.wearefrank.openapifrankadapter.adapter.AdapterClass;
import nl.wearefrank.openapifrankadapter.adapter.AdapterJsonfiyer;
import nl.wearefrank.openapifrankadapter.adapter.AdapterRefs;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class XMLGenerator {
    static public void execute(OpenAPI openAPI, String folderPath) throws IOException, SAXException, URISyntaxException {
        Paths paths = openAPI.getPaths();

        // For loop going through all the paths and instantiating a new AdapterClass
        for (Map.Entry<String, PathItem> path : paths.entrySet()) {
            // Create a new AdapterClass
            AdapterClass adapter = new AdapterClass(openAPI, path);

            //// Generate XSD ////
            // Generate XSD for the adapter
            AdapterRefs adapterRefs = new AdapterRefs(adapter.getAdapterName(), folderPath, openAPI, path);

            //// Template ////
            // Get the template file

            File templateFile = new File(XMLGenerator.class.getResource("/template.hbs").toURI());
            String templateString = new String(java.nio.file.Files.readAllBytes(templateFile.toPath()));

            // Create a new Handlebars object
            Handlebars handlebars = new Handlebars();
            Template template = handlebars.compileInline(templateString);

            // Create JSON and apply the template
            AdapterJsonfiyer adapterJsonfiyer = new AdapterJsonfiyer(openAPI, path, adapterRefs);
            String adapterTemplate = template.apply(adapterJsonfiyer.getAdapterJsonObj());

            // Export the template to xml file
            File xmlFile = new File(folderPath + "/" + adapter.getAdapterName() + ".xml");
            // Write string to file
            java.nio.file.Files.write(xmlFile.toPath(), adapterTemplate.getBytes());


        }
    }
}
