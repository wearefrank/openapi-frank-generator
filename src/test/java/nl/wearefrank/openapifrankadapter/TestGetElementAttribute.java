package nl.wearefrank.openapifrankadapter;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import nl.wearefrank.openapifrankadapter.schemas.Element;
import nl.wearefrank.openapifrankadapter.schemas.HelperClass;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = OpenapiFrankadapterApplication.class
)
class TestGetElementAttribute {

    String source = System.getProperty("user.dir") + "/src/test/java/TestingOASFile/openapi.json";
    //                 InputStream inputStream = XMLGenerator.class.getResourceAsStream("/receiverTemplate.hbs"); zelfde als dit nodig
    SwaggerParseResult result = new OpenAPIParser().readLocation(source, null, null);

    OpenAPI openAPI = result.getOpenAPI();

    @Test
    void test1() {
        // Mock the schema
        Schema schema = mock(Schema.class);
        when(schema.getMinItems()).thenReturn(4);
        when(schema.getMaxItems()).thenReturn(8);

        Element element = new Element("testElement");

        element = HelperClass.getElementAttributes(schema, element, null);

        assertTrue(element.getMinOccurs() == 4 && element.getMaxOccurs() == 8);
    }

    @Test
    void test2() {
        // Mock the schema
        Schema schema = mock(Schema.class);
        when(schema.getMinItems()).thenReturn(null);
        when(schema.getMaxItems()).thenReturn(8);

        Element element = new Element("testElement");

        element = HelperClass.getElementAttributes(schema, element, null);

        assertTrue(element.getMinOccurs() == 0 && element.getMaxOccurs() == 8);
    }

    @Test
    void test3() {
        // Mock the schema
        Schema schema = mock(Schema.class);
        when(schema.getMinItems()).thenReturn(null);
        when(schema.getMaxItems()).thenReturn(5);

        Element element = new Element("testElement");

        // list of elements
        List<String> required = List.of("testElement");

        element = HelperClass.getElementAttributes(schema, element, required);

        assertTrue(element.getMinOccurs() == 1 && element.getMaxOccurs() == 5);
    }
}
