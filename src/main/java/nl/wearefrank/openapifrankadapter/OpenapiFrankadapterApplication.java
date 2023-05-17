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

package nl.wearefrank.openapifrankadapter;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import nl.wearefrank.openapifrankadapter.error.ErrorApiResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
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
import java.util.LinkedList;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class })
@RestController
public class OpenapiFrankadapterApplication {

    public static void main(String[] args) {

        SpringApplication.run(OpenapiFrankadapterApplication.class, args);

        // TODO: Clean the processing folder on startup

    }

    @PostMapping(value = "/convert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> postFile(@RequestParam("file") MultipartFile file) throws IOException, SAXException {
        // Check if it's a JSON file
        if (!file.getContentType().equals("application/json") && !file.getContentType().equals("application/yaml") ) {
            return ResponseEntity.status(415)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new InputStreamResource(new ByteArrayInputStream("{\"message\": \"Unsupported Media Type\"}".getBytes())));
        }

        //// INITIALIZATION ////
        // Generate random folder for which to process the API request
        String uuid = UUID.randomUUID().toString().replaceAll("[^a-zA-Z0-9]", "");

        // Convert the incoming JSON multipart file to String
        String json = new String(file.getBytes());

        // Read the openapi specification
        SwaggerParseResult result = new OpenAPIParser().readContents(json, null, null);
        OpenAPI openAPI = result.getOpenAPI();
        LinkedList<GenFiles> genFiles;
        // Try catch for error handling return
        try{
            genFiles = XMLGenerator.execute(openAPI);
        }
        catch (ErrorApiResponse error){
            return ResponseEntity.status(error.getStatus())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new InputStreamResource(new ByteArrayInputStream(error.getMessage().getBytes())));
        }
        byte[] response = convertToZip(genFiles, file);

        // Return the zip file as a resource
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + uuid + ".zip\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(new ByteArrayInputStream(response)));
    }

    //// Method to convert in-memory files into a singular zip file ////
    public static byte[] convertToZip(LinkedList<GenFiles> files, MultipartFile init_json) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        ZipEntry initial = new ZipEntry("inputed-api.json");
        zipOutputStream.putNextEntry(initial);
        zipOutputStream.write(init_json.getBytes());
        zipOutputStream.closeEntry();

        for (GenFiles file : files) {
            ZipEntry entry = new ZipEntry(file.getName());
            zipOutputStream.putNextEntry(entry);
            zipOutputStream.write(file.getContent());
            zipOutputStream.closeEntry();
        }

        zipOutputStream.close();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
}
