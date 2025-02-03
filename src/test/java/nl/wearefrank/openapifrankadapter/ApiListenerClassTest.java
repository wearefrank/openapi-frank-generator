package nl.wearefrank.openapifrankadapter;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import nl.wearefrank.openapifrankadapter.xml.receiver.ApiListenerClass;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApiListenerClassTest {

    @Test
    void testMapContentType() {
        assertEquals("JSON", ApiListenerClass.mapContentType("application/json"));
        assertEquals("XML", ApiListenerClass.mapContentType("application/xml"));
        assertEquals("ANY", ApiListenerClass.mapContentType("*/*"));
        assertEquals("UNKNOWN", ApiListenerClass.mapContentType("invalid/type"));
    }

    @Test
    void testGetOperations() {
        PathItem item = new PathItem()
                .get(new Operation())
                .post(new Operation());

        Map<String, Operation> operations = ApiListenerClass.getOperations(item);
        assertEquals(2, operations.size());
        assertTrue(operations.containsKey("GET"));
        assertTrue(operations.containsKey("POST"));
    }

    @Test
    void testApiListenerClassConstructor() {
        PathItem item = new PathItem()
                .get(new Operation().description("Test GET operation"))
                .post(new Operation().description("Test POST operation"));

        Map.Entry<String, PathItem> path = Map.entry("/test", item);
        ApiListenerClass apiListener = new ApiListenerClass(path);

        assertEquals("ApiListener-test", apiListener.getApiListenerName());
        assertEquals("GET", apiListener.getMethod());
        assertEquals("/test", apiListener.getUriPattern());
        assertEquals("UNKNOWN", apiListener.getProduces());
    }
}