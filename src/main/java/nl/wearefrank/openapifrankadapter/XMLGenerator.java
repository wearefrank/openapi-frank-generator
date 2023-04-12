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
import java.io.InputStream;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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
                AdapterClass adapter = new AdapterClass(openAPI, path, operation);

                //// Generate XSD ////
                // Generate XSD for the adapter, add it to GenFiles (name + xsd, content as byte[])
                AdapterRefs adapterRefs = new AdapterRefs(openAPI, operation);
                genFiles.add(new GenFiles(adapter.getAdapterName() + ".xsd", adapterRefs.xsd.toString().getBytes()));

                //// Template ////
                // Get the template file as an input stream
                InputStream inputStream = XMLGenerator.class.getResourceAsStream("/template.hbs");

                // Read the input stream into a String
                String templateString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                // Close the input stream
                inputStream.close();

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
