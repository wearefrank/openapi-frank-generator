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

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import nl.nn.adapterframework.xml.PrettyPrintFilter;
import nl.nn.adapterframework.xml.SaxDocumentBuilder;
import nl.nn.adapterframework.xml.XmlWriter;
import nl.wearefrank.openapifrankadapter.schemas.Types.Typing;
import nl.wearefrank.openapifrankadapter.xsd.XsdEntry;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

public class XSDGenerator {

    public Writer execute(OpenAPI openAPI, ArrayList<String> refs) throws SAXException {

        StringWriter stringWriter = new StringWriter();
        Writer writerOutput = stringWriter;

        XmlWriter writer = new XmlWriter(writerOutput, true);
        writer.setIncludeXmlDeclaration(true);
        writer.setNewlineAfterXmlDeclaration(true);
        PrettyPrintFilter contentHandler = new PrettyPrintFilter(writer);

        //// Set up the XML builder
        SaxDocumentBuilder builder = new SaxDocumentBuilder("xs:schema", contentHandler);
        builder.addAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
        builder.addAttribute("xmlns:tns", "http://www.example.org");
        builder.addAttribute("targetNamespace", "http://www.example.org");
        builder.addAttribute("elementFormDefault", "qualified");
        for (Map.Entry<String, Schema> entry : openAPI.getComponents().getSchemas().entrySet()) {
            //TODO: use this for check:   boolean isRef = refs.contains(entry.getKey());
            if (refs.contains(entry.getKey())) {
                Typing result = XsdEntry.createXSDEntry(entry.getKey(), entry.getValue(), openAPI);
                result.AddToBuilder(builder);
            }
        }
        builder.endElement();
        builder.close();

        return writerOutput;
    }
}