package nl.wearefrank.openapifrankadapter;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XmlGeneratorTest {

    @ParameterizedTest
    @MethodSource("provideOptions")
    void testXmlGenerator(Option templateOption, String expectedContent) {
        InputStream inputStream = TestGetElementAttribute.class.getResourceAsStream("/TestingOASFile/openapi.json");
        String source;
        try {
            assert inputStream != null;
            source = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the input stream", e);
        }

        SwaggerParseResult result = new OpenAPIParser().readContents(source, null, null);
        OpenAPI openAPI = result.getOpenAPI();

        try {
            List<GeneratedFile> files = XMLGenerator.execute(openAPI, templateOption);

            files.stream()
                    .filter(file -> file.getName().endsWith(".xml"))
                    .forEach(file -> {
                        String fileContents = new String(file.getContent());
                        Assertions.assertTrue(fileContents.contains(expectedContent));
                    });
        } catch (Exception e) {
            Assertions.fail("Exception occurred during test: " + e.getMessage());
        }
    }

    private static Stream<Arguments> provideOptions() {
        return Stream.of(
                Arguments.of(Option.RECEIVER, "<Receiver"),
                Arguments.of(Option.SENDER, "<Sender")
        );
    }

//    @Test
//    void testExecute() throws SAXException, IOException {
//        OpenAPI openAPI = new OpenAPI();
//        Paths paths = new Paths();
//        PathItem pathItem = new PathItem().get(new Operation());
//        paths.addPathItem("/test", pathItem);
//        openAPI.setPaths(paths);
//        Info info = new Info();
//        openAPI.setInfo(info);
//
//        List<GeneratedFile> files = XMLGenerator.execute(openAPI, Option.RECEIVER);
//        assertFalse(files.isEmpty());
//        assertTrue(files.stream().anyMatch(file -> file.getName().endsWith(".xml")));
//    }

    @Test
    void testPrettyPrintByDom4j() {
        String xmlString = "<root><child>value</child></root>";
        String prettyXml = XMLGenerator.prettyPrintByDom4j(xmlString, 4, false);
        assertTrue(prettyXml.contains("\n    <child>value</child>\n"));
    }
}