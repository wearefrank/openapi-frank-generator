package nl.wearefrank.openapifrankadapter;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

// Disable security
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@RestController
public class OpenapiFrankadapterApplication {

    public static void main(String[] args) throws IOException, URISyntaxException, SAXException {

        SpringApplication.run(OpenapiFrankadapterApplication.class, args);


        //// INITIALIZATION ////

        // Generate random folder for which to process the API request
        String uuid = UUID.randomUUID().toString() + LocalDateTime.now();
        uuid = uuid.replaceAll("[^a-zA-Z0-9]", "");
        String folderPath = System.getProperty("user.dir") + "/src/main/resources/processing/" + uuid;
        File folder = new File(folderPath);
        folder.mkdir();

        // TODO : This should be gotten from the API incoming request
        String source = System.getProperty("user.dir") + "/src/test/java/nl/wearefrank/openapifrankadapter/TestingOASFile/openapi.json";

        // Read the openapi specification off of a file or url
        SwaggerParseResult result = new OpenAPIParser().readLocation(source, null, null);

        OpenAPI openAPI = result.getOpenAPI();
        XMLGenerator.execute(openAPI, folderPath);

        //// Zip all the files inside the folder
        // create a ZipOutputStream to write the zip file
        String zipFilePath = folderPath + ".zip";
        FileOutputStream fos = new FileOutputStream(zipFilePath);
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        // get a list of all the files in the directory
        File[] files = folder.listFiles();

        // compress each file into the zip file
        for (File file : files) {
            FileInputStream fis = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }

            fis.close();
        }
        // close the ZipOutputStream
        zipOut.close();
        fos.close();
    }

    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "myName", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }
}
