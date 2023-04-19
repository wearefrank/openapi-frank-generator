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

package nl.wearefrank.openapifrankadapter.schemas;

import io.swagger.v3.oas.models.media.Schema;
import nl.wearefrank.openapifrankadapter.schemas.Types.SimpleType;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class HelperClass {

    /**
     * Method to get all the SimpleTypes attributes
     *
     * @param e - the schema element. object - the object to add the attributes to
     * @return the object with the attributes
     */
    public static SimpleType getSimpleTypeAttributes(Map.Entry<String, Schema> e, SimpleType object) {
        object.setMinInclusive(e.getValue().getMinimum());
        object.setMaxInclusive(e.getValue().getMaximum());
        object.setMinLength(e.getValue().getMinLength());
        object.setMaxLength(e.getValue().getMaxLength());
        object.setPattern(e.getValue().getPattern());
        object.setEnumeration(e.getValue().getEnum());
        return object;
    }

    // Check if the element is a simple type
    public static boolean checkIfSimpleType(Map.Entry<String, Schema> e, SimpleType object) {
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
     * @param schema   - the schema element.
     * @param object   - the object to add the attributes to.
     * @param required - the list of required elements.
     * @return the object with the attributes
     */
    public static Element getElementAttributes(Schema schema, Element object, List<String> required) {
        if (schema.getMinItems() != null) {
            object.setMinOccurs(schema.getMinItems());
        } else {
            object.setMinOccursCheck(required);
        }
        object.setMaxOccurs(schema.getMaxItems());
        return object;
    }


    public static boolean isContain(String source, String reference) {
        String[] parts = reference.split("/");
        String subItem = parts[parts.length - 1];

        String pattern = "\\b" + subItem + "\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        return m.find();
    }
}
