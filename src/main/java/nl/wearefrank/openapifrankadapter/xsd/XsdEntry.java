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
import nl.wearefrank.openapifrankadapter.schemas.HelperClass;
import nl.wearefrank.openapifrankadapter.schemas.Types.ComplexType;
import nl.wearefrank.openapifrankadapter.schemas.Types.Reference;
import nl.wearefrank.openapifrankadapter.schemas.Types.SimpleType;
import nl.wearefrank.openapifrankadapter.schemas.Types.Typing;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class XsdEntry {

    static public Typing createXSDEntry(String key, Schema entry, OpenAPI openAPI) throws ErrorApiResponse {
        try {
            // Create required variable
            List<String> required = List.of("null");

            // Check the required properties, of none exists on path, gets covered by the catch
            try {
                required = entry.getRequired();
            } catch (Exception e) { /*Do nothing*/ }

            try {
                //// SIMPLETYPE ////
                if (entry.getType() != null && entry.getEnum() != null) { return createSimpleType(key, entry); }

                //// REFERENCE ////
                else if (entry.getItems().get$ref() != null) { return createReference(key, entry, openAPI); }

            } catch (NullPointerException e) {
                //// COMPLEXTYPE ////
                Map<String, Schema> props = entry.getProperties();
                return createComplexType(key, props, required, openAPI);
            }

        } catch (NullPointerException error) {
            System.out.println(LocalDateTime.now() + " [ERROR {createXSDEntry}] - " + error.getMessage());
            String message = "Error in reading the OpenApiSpecification, check if the OpenApiSpecification does not contain errors or invalid entries..." + " [ERROR {createXSDEntry}] - " + error.getMessage();
            throw new ErrorApiResponse(500, message);
        }

        // So the compiler does not nag, this path should not be reached. Error //
        System.out.println(LocalDateTime.now() + " [ERROR {createXSDEntry}] - This path should not be reached");
        throw new ErrorApiResponse(500, "This path in the code should not be reached, please contact the developer");
    }

    static SimpleType createSimpleType(String key, Schema entry) throws ErrorApiResponse {
        try {
            SimpleType simpleType = new SimpleType(key, entry.getType());
            simpleType = HelperClass.getSimpleTypeAttributes(Map.entry(key, entry), simpleType);
            return simpleType;
        }
        catch (Exception error) {
            System.out.println(LocalDateTime.now() + " [ERROR {createSimpleType}] - " + error.getMessage());
            String message = "Error in getting entry type, check if the OpenApiSpecification does not contain errors or invalid entries..." + " [ERROR {createSimpleType}] - " + error.getMessage();
            throw new ErrorApiResponse(500, message);
        }
    }

    static Reference createReference(String key, Schema entry, OpenAPI openAPI) throws ErrorApiResponse{
        try {
            for (Map.Entry<String, Schema> innerEntry : openAPI.getComponents().getSchemas().entrySet()) {
                if (HelperClass.isContain(innerEntry.getKey(), entry.getItems().get$ref())) {
                    Reference reference = new Reference(key);
                    ComplexType complexType = new ComplexType("");
                    complexType.addTyping(createXSDEntry(innerEntry.getKey(), innerEntry.getValue(), openAPI));
                    reference.addTyping(complexType);
                    return reference;
                }
            }
            return null;
        }
        catch (NullPointerException error) {
            System.out.println(LocalDateTime.now() + " [ERROR {createReference}] - " + error.getMessage());
            String message = "Error in getting Component(s) and/or schema(s), check if the OpenApiSpecification does not contain errors or invalid entries..." + " [ERROR {createReference}] - " + error.getMessage();
            throw new ErrorApiResponse(500, message);
        }
    }

    static Reference createComplexType(String key, Map<String, Schema> props, List<String> required, OpenAPI openAPI) throws ErrorApiResponse {
        try {
            Reference reference = new Reference(key);
            ComplexType complexType = new ComplexType("");
            reference.addTyping(RecursiveXSD.createRecursiveXSD(props, complexType, required, openAPI));
            return reference;
        }
        catch (NullPointerException error) {
            System.out.println(LocalDateTime.now() + " [ERROR {createComplexType}] - " + error.getMessage());
            String message = "Fault in program, send error:" + " [ERROR {createComplexType}] - " + error.getMessage();
            throw new ErrorApiResponse(500, message);
        }
    }
}
