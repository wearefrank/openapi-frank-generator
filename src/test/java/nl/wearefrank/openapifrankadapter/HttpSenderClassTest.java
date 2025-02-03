package nl.wearefrank.openapifrankadapter;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import nl.wearefrank.openapifrankadapter.xml.sender.HttpSenderClass;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpSenderClassTest {

    @Test
    void testMapContentType() {
        assertEquals("JSON", HttpSenderClass.mapContentType("application/json"));
        assertEquals("XML", HttpSenderClass.mapContentType("application/xml"));
        assertEquals("ANY", HttpSenderClass.mapContentType("*/*"));
        assertEquals("UNKNOWN", HttpSenderClass.mapContentType("invalid/type"));
    }

    @Test
    void testGetOperations() {
        PathItem item = new PathItem()
                .get(new Operation())
                .post(new Operation());

        Map<String, Operation> operations = HttpSenderClass.getOperations(item);
        assertEquals(2, operations.size());
        assertTrue(operations.containsKey("GET"));
        assertTrue(operations.containsKey("POST"));
    }
}