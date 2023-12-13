package com.microlau.jee.rest.jersey;

import java.util.Date;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class JerseyRestSimpleService {
    /*
        http://localhost:8080/rest/hello/
        http://localhost:8080/rest/hello/Joao

        Schema: http://localhost:8080/rest/application.wadl

    */

    @Path("hello")
    @GET
    public String hello() {
        return "Hello, today is " + new Date();
    }

    @Path("hello/{name}")
    @GET
    public String hello(@PathParam("name") String name) {
        return "Hello there " + name;
    }

}
