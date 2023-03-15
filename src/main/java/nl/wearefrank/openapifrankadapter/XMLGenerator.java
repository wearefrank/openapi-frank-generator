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
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Map;

public class XMLGenerator {
    static public LinkedList<GenFiles> execute(OpenAPI openAPI) throws IOException, SAXException, URISyntaxException {
        Paths paths = openAPI.getPaths();

        LinkedList<GenFiles> genFiles = new LinkedList<>();

        // For loop going through all the paths and instantiating a new AdapterClass
        for (Map.Entry<String, PathItem> path : paths.entrySet()) {
            // For loop going one level deeper in the path
            for (Map.Entry<PathItem.HttpMethod, io.swagger.v3.oas.models.Operation> operation : path.getValue().readOperationsMap().entrySet()) {

                // Create a new AdapterClass
                System.out.println("Generating adapter for " + path.getKey() + " " + operation.getKey());
                AdapterClass adapter = new AdapterClass(openAPI, path, operation);

                //// Generate XSD ////
                // Generate XSD for the adapter, add it to GenFiles (name + xsd, content as byte[])
                AdapterRefs adapterRefs = new AdapterRefs(openAPI, operation);
                genFiles.add(new GenFiles(adapter.getAdapterName() + ".xsd", adapterRefs.xsd.toString().getBytes()));

                //// Template ////
                // Get the template file
                File templateFile = new File(XMLGenerator.class.getResource("/template.hbs").toURI());
                String templateString = new String(java.nio.file.Files.readAllBytes(templateFile.toPath()));

                // Create a new Handlebars object
                Handlebars handlebars = new Handlebars();
                Template template = handlebars.compileInline(templateString);

                // Create JSON and apply the template
                AdapterJsonfiyer adapterJsonfiyer = new AdapterJsonfiyer(adapter, adapterRefs, path);
                String adapterTemplate = template.apply(adapterJsonfiyer.getAdapterJsonObj());

                // Export the template to xml file
                genFiles.add(new GenFiles(adapter.getAdapterName() + ".xml", adapterTemplate.getBytes()));
            }
        }

        // Return all the Files generated
        return genFiles;
    }
}
