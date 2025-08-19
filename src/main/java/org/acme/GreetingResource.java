package org.acme;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Slf4j
@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response hello() {
        String response = "Hello from Quarkus REST";

        log.info("aloooo");
        return Response.ok(response).build();
    }
}
