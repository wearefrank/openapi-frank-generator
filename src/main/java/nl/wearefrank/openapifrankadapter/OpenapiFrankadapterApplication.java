package nl.wearefrank.openapifrankadapter;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import org.xml.sax.SAXException;

import java.io.*;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

// Disable security
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@RestController
public class OpenapiFrankadapterApplication {

    public static void main(String[] args) {

        SpringApplication.run(OpenapiFrankadapterApplication.class, args);

        // TODO: Clean the processing folder on startup

    }

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> postFile(@RequestParam("file") MultipartFile file) throws IOException, URISyntaxException, SAXException {

        //// INITIALIZATION ////

        // Generate random folder for which to process the API request
        String uuid = UUID.randomUUID().toString() + LocalDateTime.now();
        uuid = uuid.replaceAll("[^a-zA-Z0-9]", "");
        String folderPath = System.getProperty("user.dir") + "/src/main/resources/processing/" + uuid;
        File folder = new File(folderPath);
        folder.mkdir();

        // Save the file to the resources folder
        String fileName = file.getOriginalFilename(); // TODO: Check if this name shouldnt just be openapi.json
        Path filePath = Paths.get("src/main/resources/processing/" + uuid, fileName);
        file.transferTo(filePath);

        // TODO : Check if this part can be simplified with file.getContent()

        String source = System.getProperty("user.dir") + "/src/main/resources/processing/" + uuid + "/" + fileName;
        System.out.println(source);

        // Read the openapi specification
        SwaggerParseResult result = new OpenAPIParser().readLocation(source, null, null);

        OpenAPI openAPI = result.getOpenAPI();
        XMLGenerator.execute(openAPI, folderPath);

        // Create a zip file with the saved file
        String zipFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".zip";
        Path zipFilePath = Paths.get("src/main/resources/processing/" + uuid, zipFileName);
        createZipFile(Paths.get("src/main/resources/processing/" + uuid), zipFilePath.toFile());

        // Return the zip file as a resource
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"");

        FileInputStream resource = new FileInputStream(zipFilePath.toFile());
        FilterInputStream filterInputStream = new FilterInputStream(resource) {
            @Override
            public void close() throws IOException {
                super.close();
                // Delete all files in the folder
                File[] files = folder.listFiles();
                for (File file : files) {
                    file.delete();
                }
                // Delete the folder
                folder.delete();
            }
        };

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(filterInputStream));
    }
    
    private void createZipFile(Path folderPath, File zipFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        File[] files = folderPath.toFile().listFiles();

        for (File file : files) {
            if (file.getName().equals(zipFile.getName())) {
                continue;
            }

            FileInputStream fis = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        zipOut.close();
        fos.close();
    }
}
