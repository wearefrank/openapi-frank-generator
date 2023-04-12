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
import nl.wearefrank.openapifrankadapter.schemas.HelperClass;
import nl.wearefrank.openapifrankadapter.schemas.Types.ComplexType;
import nl.wearefrank.openapifrankadapter.schemas.Types.Reference;
import nl.wearefrank.openapifrankadapter.schemas.Types.SimpleType;
import nl.wearefrank.openapifrankadapter.schemas.Types.Typing;

import java.util.List;
import java.util.Map;

public class XsdEntry {

    static public Typing createXSDEntry(String key, Schema entry, OpenAPI openAPI) {
        try {
            // Create required variable
            List<String> required = List.of("null");

            // Check the required properties, of none exists on path, gets covered by the catch
            try {
                required = entry.getRequired();
            } catch (NullPointerException e) { /*Do nothing*/ }

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
            System.out.println("[ERROR {createXSDEntry}] - " + error.getMessage());
            return null;
        }

        // So the compiler does not nag, this path should not be reached. Error //
        System.out.println("[ERROR {createXSDEntry}] - This path should not be reached");
        return null;
    }

    static SimpleType createSimpleType(String key, Schema entry) {
        try {
            SimpleType simpleType = new SimpleType(key, entry.getType());
            simpleType = HelperClass.getSimpleTypeAttributes(Map.entry(key, entry), simpleType);
            return simpleType;
        }
        catch (NullPointerException error) {
            System.out.println("[ERROR {createSimpleType}] - " + error.getMessage());
            return null;
        }
    }

    static Reference createReference(String key, Schema entry, OpenAPI openAPI) {
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
            System.out.println("[ERROR {createReference}] - " + error.getMessage());
            return null;
        }
    }

    static Reference createComplexType(String key, Map<String, Schema> props, List<String> required, OpenAPI openAPI) {
        try {
            Reference reference = new Reference(key);
            ComplexType complexType = new ComplexType("");
            reference.addTyping(RecursiveXSD.createRecursiveXSD(props, complexType, required, openAPI));
            return reference;
        }
        catch (NullPointerException error) {
            System.out.println("[ERROR {createComplexType}] - " + error.getMessage());
            return null;
        }
    }
}
