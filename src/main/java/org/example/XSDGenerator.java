package org.example;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import nl.nn.adapterframework.xml.PrettyPrintFilter;
import nl.nn.adapterframework.xml.SaxDocumentBuilder;
import nl.nn.adapterframework.xml.XmlWriter;
import org.example.adapter.ParamSingleton;
import org.example.schemas.*;
import org.example.schemas.Types.ComplexType;
import org.example.schemas.Types.Reference;
import org.example.schemas.Types.SimpleType;
import org.example.schemas.Types.Typing;
import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XSDGenerator {

    OpenAPI openAPI;

    // Used to store the name of the root element
    String upperSchemaName;

    // Used to store outside parameters given from the xml
    List<Parameter> parameters;

    public void execute(String schemaLocation, OpenAPI openAPI, ArrayList<String> refs, List<Parameter> parameters) throws SAXException, FileNotFoundException {
        this.openAPI = openAPI;
        this.parameters = parameters;
        ParamSingleton.getInstance().resetParams();

        //// Set up the XML writer
        // TODO: Ask if xsd is an xml file {xsd.xml}
        FileOutputStream outputStream = new FileOutputStream(schemaLocation);
        XmlWriter writer = new XmlWriter(outputStream, true);
        writer.setIncludeXmlDeclaration(true);
        writer.setNewlineAfterXmlDeclaration(true);
        PrettyPrintFilter contentHandler = new PrettyPrintFilter(writer);

        //// Set up the XML builder
        SaxDocumentBuilder builder = new SaxDocumentBuilder("xs:schema", contentHandler);
        builder.addAttribute("xmlns:xs","http://www.w3.org/2001/XMLSchema");
        builder.addAttribute("xmlns:tns","http://www.example.org");
        builder.addAttribute("targetNamespace","http://www.example.org");
        builder.addAttribute("elementFormDefault","qualified");
        for (Map.Entry<String, Schema> entry : openAPI.getComponents().getSchemas().entrySet()) {
            //TODO: use this for check:   boolean isRef = refs.contains(entry.getKey());
            if (refs.contains(entry.getKey())) {
                this.upperSchemaName = entry.getKey();
                Typing result = createXSDEntry(entry.getKey(), entry.getValue());
                result.AddToBuilder(builder);
            }
        }
        builder.endElement();
        builder.close();
    }

    /**
     * Prints the XSD elements for a given schema
     *
     * @param key - The name of the schema
     * @param entry - the schema
     * @return nothing
     */
    public Typing createXSDEntry(String key,Schema entry) {
        try {
            // Create required variable
            List<String> required = Arrays.asList("null");

            // Check the required properties
            try {
                required = entry.getRequired();
            } catch (NullPointerException e) {
                // Do nothing
            }

            System.out.println(required);

            try {
                //// SIMPLETYPE ////
                if (entry.getType() != null && entry.getEnum() != null) {
                    // TODO: Check if this is correct; map.entry<String, Schema> was used
                    SimpleType simpleType = new SimpleType(key, entry.getType());
                    simpleType = HelperClass.getSimpleTypeAttributes((Map.Entry<String, Schema>) entry, simpleType);
                    return simpleType;
                }
                //// REFERENCE ////
                else if (entry.getItems().get$ref() != null  ) {
                    // TODO: REFERENCES SHOULD NOT GET INSERTED RIGHT???
                    for (Map.Entry<String, Schema> innerEntry : openAPI.getComponents().getSchemas().entrySet()){
                        if (HelperClass.isContain(innerEntry.getKey(), entry.getItems().get$ref())) {
                            Reference reference = new Reference(key);

                            ComplexType complexType = new ComplexType("");
                            complexType.addTyping(createXSDEntry(innerEntry.getKey(), innerEntry.getValue()));

                            reference.addTyping(complexType);
                            return reference;
                        }
                    }
                }

            } catch (NullPointerException e) {
                // Get the properties
                Map<String, Schema> props = entry.getProperties();

                // Check if it is the root element [thus needing reference status]
                if (key == this.upperSchemaName){
                    Reference reference = new Reference(key);
                    ComplexType complexType = new ComplexType("");
                    reference.addTyping(recursiveXSD(props, complexType, required));
                    return reference;
                }
                Reference reference = new Reference(key);
                ComplexType complexType = new ComplexType("");
                reference.addTyping(recursiveXSD(props, complexType, required));
                return reference;
            }
        } catch (NullPointerException e) {
            System.out.println("[ERROR {createXSDEntry}] - " + e.getMessage());
        }
        // So the IDE does not nag //
        return null;
    }

    public ComplexType recursiveXSD(Map<String, Schema> props, ComplexType complexType, List<String> required) {
        for (Map.Entry<String, Schema> e : props.entrySet()) {
            // Check if outer parameters are given
            // TODO: Currently just given to first complexType, could pose problem when not sorted correctly

            if (this.parameters != null) {
                for (Parameter parameter : this.parameters) {
                    Element element = new Element(parameter.getName());
                    element.setType(parameter.getSchema().getType());
                    complexType.addElement(element);

                    // Add parameter to ParamSingleton
                    ParamSingleton.getInstance().addParam(parameter.getName());
                }
                this.parameters = null;
            }
            String name = e.getKey();
            // Check if the property has the word Type in it
            try {
                if (getType(e).contains("Type") && !getType(e).equals("numberType")) {
                    // Check if it is OAS mishap
                    if(e.getValue().get$ref() != null){
                        //// REFERENCE ////
                        //complexType.addTyping(new Reference(name, e.getValue().get$ref()));
                        for (Map.Entry<String, Schema> entry : openAPI.getComponents().getSchemas().entrySet()){
                            if (HelperClass.isContain(entry.getKey(), e.getValue().get$ref())) {
                                Reference reference = new Reference(name);
                                reference.addTyping(createXSDEntry(entry.getKey(), entry.getValue()));
                                complexType.addTyping(reference);
                            }
                        }
                        // TODO: fix that isContain also implemented here!!!
                    }
                    else if (e.getValue().getItems() == null) {
                        //// OAS MISHAP ////
                        // TODO: This should be an error from the openapispecification
                        //complexType = RecursiveXSD(e.getValue().getProperties(), complexType, e.getValue().getRequired());
                        break;
                    }
                    // Check if it is a reference to another schema
                    else if (e.getValue().getItems().get$ref() != null) {
                        //// REFERENCE ////
                        //complexType.addTyping(new Reference(name, e.getValue().getItems().get$ref()));
                        for (Map.Entry<String, Schema> entry : openAPI.getComponents().getSchemas().entrySet()){
                            if (HelperClass.isContain(entry.getKey(), e.getValue().get$ref())) {
                                complexType.addTyping(createXSDEntry(entry.getKey(), entry.getValue()));
                            }
                        }
                        // TODO: fix that isContain also implemented here!!!
                    }

                    else if (e.getValue().getItems().getProperties() == null) {
                        //// RECURSION, ARRAY ITEMS ////
                        SimpleType simple = new SimpleType(name, e.getValue().getType());
                        simple = HelperClass.getSimpleTypeAttributes(e, simple);
                        complexType.addTyping(simple);

                        // Add simpletype to ParamSingleton
                        ParamSingleton.getInstance().addParam(name);
                    }
                    else {
                        //// RECURSION, NORMAL ////
                        complexType.addTyping(createXSDEntry(name, e.getValue().getItems()));
                    }
                }
                else if (e.getValue().getItems() != null && e.getValue().getItems().getProperties() == null)  {
                    //// RECURSION, ARRAY ITEMS ////
                    SimpleType simple = new SimpleType(name, e.getValue().getType());
                    simple = HelperClass.getSimpleTypeAttributes(e, simple);
                    complexType.addTyping(simple);

                    // Add simpletype to ParamSingleton
                    ParamSingleton.getInstance().addParam(name);
                }
                else if (HelperClass.checkIfSimpleType(e, new SimpleType(e.getKey(), e.getValue().getType()))) {
                    //// SIMPLETYPE ////
                    SimpleType simple = new SimpleType(name, e.getValue().getType());
                    simple = HelperClass.getSimpleTypeAttributes(e, simple);
                    complexType.addTyping(simple);

                    // Add simpletype to ParamSingleton
                    ParamSingleton.getInstance().addParam(name);
                }
                else {
                    //// ELEMENT ////
                    Element element = new Element(name);
                    element.setType(getType(e));
                    element = HelperClass.getElementAttributes(e.getValue(), element, required);
                    complexType.addElement(element);

                    // add element to ParamSingleton
                    ParamSingleton.getInstance().addParam(name);
                }
            }
            catch (NullPointerException ex) {
                System.out.println("[ERROR {RecursiveXSD}] - " + ex.getMessage());
            }
        }
        return complexType;
    }

    /**
     * Translate the type from OAP to Frank
     *
     * @param e - the schema element
     * @return the OAP type of the element
     */
    public String getType(Map.Entry<String, Schema> e) {
        String type = e.getValue().getType();

        if (type != null) {
            if (type.equals("string") || type.equals("integer") || type.equals("boolean")) {
                return type;
            }
            // Number becomes its own type, array refers to the linksType that is used in OAP
            if (type.equals("number")) {
                return type + "Type";
            }
        }
        return e.getKey() + "Type";
    }
}