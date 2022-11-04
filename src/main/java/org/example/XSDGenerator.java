package org.example;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class XSDGenerator {
    public void execute(OpenAPI openAPI) throws SAXException, FileNotFoundException {
        //// Set up the XML writer
        // TODO: Ask if xsd is an xml file {xsd.xml}
        FileOutputStream outputStream = new FileOutputStream(System.getProperty("user.dir") + "/../Converter/Processing/" + "xsd.txt");
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
            Typing result = createXSDEntry(entry.getKey(), entry.getValue());
            result.AddToBuilder(builder);
            ParamSingleton.getInstance().increaseIndex();
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

            try {
                //// SIMPLETYPE ////
                if (entry.getType() != null && entry.getEnum() != null) {
                    // TODO: Check if this is correct; map.entry<String, Schema> was used
                    SimpleType simpleType = new SimpleType(key, entry.getType());
                    simpleType = getSimpleTypeAttributes((Map.Entry<String, Schema>) entry, simpleType);

                    // Add new parameter to the singleton
                    ParamSingleton.getInstance().addParameter(key, entry.getType());

                    return simpleType;
                }
                //// REFERENCE ////
                else if (entry.getItems().get$ref() != null  ) {
                    // TODO: REFERENCES SHOULD NOT GET INSERTED RIGHT???
                    ParamSingleton.getInstance().addParameter(key, entry.getItems().get$ref());
                    return new Reference(key, entry.getItems().get$ref());
                }

            } catch (NullPointerException e) {
                // Get the properties
                Map<String, Schema> props = entry.getProperties();
                ComplexType complexType = new ComplexType(key);
                return RecursiveXSD(props, complexType, required);

            }
        } catch (NullPointerException e) {
            System.out.println("[ERROR {createXSDEntry}] - " + e.getMessage());
        }
        // So the IDE does not nag //
        return null;
    }

    public ComplexType RecursiveXSD(Map<String, Schema> props, ComplexType complexType, List<String> required) {
        for (Map.Entry<String, Schema> e : props.entrySet()) {
            String name = e.getKey();
            // Check if the property has the word Type in it
            try {
                if (getType(e).contains("Type") && !getType(e).equals("numberType")) {
                    // Check if it is OAS mishap
                    if(e.getValue().get$ref() != null){
                        //// REFERENCE ////
                        complexType.addTyping(new Reference(name, e.getValue().get$ref()));

                        // TODO: REFERENCES SHOULD NOT GET INSERTED RIGHT???
                        ParamSingleton.getInstance().addParameter(name, e.getValue().get$ref());
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
                        complexType.addTyping(new Reference(name, e.getValue().getItems().get$ref()));

                        // TODO: REFERENCES SHOULD NOT GET INSERTED RIGHT???
                        ParamSingleton.getInstance().addParameter(name, e.getValue().getItems().get$ref());
                    }

                    else if (e.getValue().getItems().getProperties() == null) {
                        //// RECURSION, ARRAY ITEMS ////
                        SimpleType simple = new SimpleType(name, e.getValue().getType());
                        simple = getSimpleTypeAttributes(e, simple);
                        complexType.addTyping(simple);

                        // Add new parameter to the singleton
                        ParamSingleton.getInstance().addParameter(name, e.getValue().getType());
                    }
                    else {
                        //// RECURSION, NORMAL ////
                        complexType.addTyping(createXSDEntry(name, e.getValue().getItems()));
                    }
                }
                else if (e.getValue().getItems() != null && e.getValue().getItems().getProperties() == null)  {
                    //// RECURSION, ARRAY ITEMS ////
                    SimpleType simple = new SimpleType(name, e.getValue().getType());
                    simple = getSimpleTypeAttributes(e, simple);
                    complexType.addTyping(simple);

                    // Add the parameter to the singleton
                    ParamSingleton.getInstance().addParameter(name, e.getValue().getType());
                }
                else if (checkIfSimpleType(e, new SimpleType(e.getKey(), e.getValue().getType()))) {
                    //// SIMPLETYPE ////
                    SimpleType simple = new SimpleType(name, e.getValue().getType());
                    simple = getSimpleTypeAttributes(e, simple);
                    complexType.addTyping(simple);

                    // Add new parameter to the singleton
                    ParamSingleton.getInstance().addParameter(name, e.getValue().getType());
                }
                else {
                    //// ELEMENT ////
                    Element element = new Element(name);
                    element.setType(getType(e));
                    element = getElementAttributes(e, element, required);
                    complexType.addElement(element);

                    // Add new parameter to the singleton
                    ParamSingleton.getInstance().addParameter(name, getType(e));
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
                return "xs:" + type;
            }
            // Number becomes its own type, array refers to the linksType that is used in OAP
            if (type.equals("number")) {
                return type + "Type";
            }
        }

        return e.getKey() + "Type";
    }

    /**
     * Method to get all the SimpleTypes attributes
     *
     * @param e - the schema element. object - the object to add the attributes to
     * @return the object with the attributes
     */
    public SimpleType getSimpleTypeAttributes(Map.Entry<String, Schema> e, SimpleType object) {
        object.setMinInclusive(e.getValue().getMinimum());
        object.setMaxInclusive(e.getValue().getMaximum());
        object.setMinLength(e.getValue().getMinLength());
        object.setMaxLength(e.getValue().getMaxLength());
        object.setPattern(e.getValue().getPattern());
        object.setEnumeration(e.getValue().getEnum());
        return object;
    }
    // Check if the element is a simple type
    public boolean checkIfSimpleType(Map.Entry<String, Schema> e, SimpleType object) {
        if (e.getValue().getMinimum() != null || e.getValue().getMaximum() != null || e.getValue().getMinLength() != null || e.getValue().getMaxLength() != null || e.getValue().getPattern() != null || e.getValue().getEnum() != null) {
            object.setMinInclusive(e.getValue().getMinimum());
            object.setMaxInclusive(e.getValue().getMaximum());
            object.setMinLength(e.getValue().getMinLength());
            object.setMaxLength(e.getValue().getMaxLength());
            object.setPattern(e.getValue().getPattern());
            object.setEnumeration(e.getValue().getEnum());
            return true;
        }
        return false;
    }

    /**
     * Method to get all the Element attributes
     *
     * @param e - the schema element.
     * @param object - the object to add the attributes to.
     * @param required - the list of required elements.
     * @return the object with the attributes
     */
    public Element getElementAttributes(Map.Entry<String, Schema> e, Element object, List<String> required) {
        if (e.getValue().getMinItems() != null) {
            object.setMinOccurs(e.getValue().getMinItems());
        } else {
            object.setMinOccursCheck(required);
        }
        object.setMaxOccurs(e.getValue().getMaxItems());
        return object;
    }
}