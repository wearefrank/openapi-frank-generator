package nl.wearefrank.openapifrankadapter.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Controller;

@Controller
@Path("/JsonPut")
public class JsonPut {
    // Should handle an incoming JSON file or object. File or object should be read and parsed to an OpenAPI object.

    @PUT
    @Consumes("application/json")
    public void putJson(String json) {
        System.out.println(json);

        Response.status(200).entity("JsonPut").build();
    }
}