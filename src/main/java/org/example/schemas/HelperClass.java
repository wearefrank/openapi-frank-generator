package org.example.schemas;

import io.swagger.v3.oas.models.media.Schema;
import org.example.schemas.Types.SimpleType;

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
     * @param schema - the schema element.
     * @param object - the object to add the attributes to.
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

        String pattern = "\\b"+subItem+"\\b";
        Pattern p=Pattern.compile(pattern);
        Matcher m=p.matcher(source);
        return m.find();
    }
}
