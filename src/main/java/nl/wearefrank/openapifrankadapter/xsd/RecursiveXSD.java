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

package nl.wearefrank.openapifrankadapter.xsd;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import nl.wearefrank.openapifrankadapter.error.ErrorApiResponse;
import nl.wearefrank.openapifrankadapter.schemas.Element;
import nl.wearefrank.openapifrankadapter.schemas.HelperClass;
import nl.wearefrank.openapifrankadapter.schemas.Types.ComplexType;
import nl.wearefrank.openapifrankadapter.schemas.Types.Reference;
import nl.wearefrank.openapifrankadapter.schemas.Types.SimpleType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RecursiveXSD {

    static public ComplexType createRecursiveXSD(Map<String, Schema> props, ComplexType complexType, List<String> required, OpenAPI openAPI) throws ErrorApiResponse {
        for (Map.Entry<String, Schema> e : props.entrySet()) {
            String name = e.getKey();
            if (isComplexType(e)) {
                createComplexType(e, name, complexType, openAPI);
            } else if (HelperClass.checkIfSimpleType(e, new SimpleType(e.getKey(), e.getValue().getType()))) {
                createSimpleType(e, name, complexType);
            } else {
                createElement(e, name, complexType, required);
            }
        }
        return complexType;
    }

    private static boolean isComplexType(Map.Entry<String, Schema> e) throws ErrorApiResponse {
        try{
            String type = getType(e);
            return type.contains("Type") && !type.equals("numberType");
        }
        catch (NullPointerException error) {
            System.out.println(LocalDateTime.now() + " [ERROR {isComplexType}] - " + error.getMessage());
            String message = "Error in getting Type, check if the OpenApiSpecification does not contain errors or invalid entries..." + " [ERROR {isComplexType}] - " + error.getMessage();
            throw new ErrorApiResponse(500, message);
        }
    }

    private static void createComplexType(Map.Entry<String, Schema> e, String name, ComplexType complexType, OpenAPI openAPI) throws ErrorApiResponse {
        //// REFERENCE ////
        if (e.getValue().get$ref() != null) {
            createReference(name, e.getValue().get$ref(), complexType, openAPI);
        }
        //// CHECK in order for program not to crash when there are NO ITEMS [needed for the following methods below]
        // method above still needs to be executed regardless of items or not. Thus check in place here.
        else if (e.getValue().getItems() != null) {

            if (e.getValue().getItems().get$ref() != null) {
                //// REFERENCE ////
                createReference(name, e.getValue().getItems().get$ref(), complexType, openAPI); }

            else if (e.getValue().getItems().getProperties() == null) {
                //// RECURSION, ARRAY ITEMS ////
                createSimpleType(e, name, complexType);}

        } else {
            //// RECURSION, NORMAL ////
            complexType.addTyping(XsdEntry.createXSDEntry(name, e.getValue().getItems(), openAPI));
        }
    }

    private static void createSimpleType(Map.Entry<String, Schema> e, String name, ComplexType complexType) throws ErrorApiResponse {
        try {
            SimpleType simple = new SimpleType(name, e.getValue().getType());
            simple = HelperClass.getSimpleTypeAttributes(e, simple);
            complexType.addTyping(simple);
        }
        catch (NullPointerException error) {
            System.out.println(LocalDateTime.now() + " [ERROR {createSimpleType}] - " + error.getMessage());
            String message = "Error in getting Value and/or Type, check if the OpenApiSpecification does not contain errors or invalid entries..." + " [ERROR {createSimpleType}] - " + error.getMessage();
            throw new ErrorApiResponse(500, message);
        }
    }


    private static void createElement(Map.Entry<String, Schema> e, String name, ComplexType complexType, List<String> required) throws ErrorApiResponse {
        try {
            Element element = new Element(name);
            element.setType(getType(e));
            element = HelperClass.getElementAttributes(e.getValue(), element, required);
            complexType.addElement(element);
        }
        catch (NullPointerException error) {
            System.out.println(LocalDateTime.now() + " [ERROR {createElement}] - " + error.getMessage());
            String message = "Error in getting Value and/or Type, check if the OpenApiSpecification does not contain errors or invalid entries..." + " [ERROR {createElement}] - " + error.getMessage();
            throw new ErrorApiResponse(500, message);
        }
    }

    /**
     * Create a reference, add it to the complexType in the current rotation.
     *
     * @param name         the name of the reference
     * @param ref          the reference
     * @param complexType  the complexType in the current rotation
     * @param openAPI      the openAPI object
     */
    private static void createReference(String name, String ref, ComplexType complexType, OpenAPI openAPI) throws ErrorApiResponse {
        try {
            for (Map.Entry<String, Schema> entry : openAPI.getComponents().getSchemas().entrySet()) {
                if (HelperClass.isContain(entry.getKey(), ref)) {
                    Reference reference = new Reference(name);
                    reference.addTyping(XsdEntry.createXSDEntry(entry.getKey(), entry.getValue(), openAPI));
                    complexType.addTyping(reference);
                }
            }
        }
        catch (NullPointerException error) {
            System.out.println(LocalDateTime.now() + " [ERROR {createReference}] - " + error.getMessage());
            String message = "Error in getting Schema(s) in a component, check if the OpenApiSpecification does not contain errors or invalid entries..." + " [ERROR {createReference}] - " + error.getMessage();
            throw new ErrorApiResponse(500, message);
        }
    }

    /**
     * Translate the type from OAP to Frank
     *
     * @param e - the schema element
     * @return the OAP type of the element
     */
    public static String getType(Map.Entry<String, Schema> e) throws ErrorApiResponse {
        try {
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
        catch (NullPointerException error) {
            System.out.println(LocalDateTime.now() + " [ERROR {getType}] - " + error.getMessage());
            String message = "Error in getting Value and/or Type, check if the OpenApiSpecification does not contain errors or invalid entries..." + " [ERROR {getType}] - " + error.getMessage();
            throw new ErrorApiResponse(500, message);
        }
    }
}
