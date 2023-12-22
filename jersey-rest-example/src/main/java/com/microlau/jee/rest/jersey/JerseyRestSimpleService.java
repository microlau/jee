package com.microlau.jee.rest.jersey;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class JerseyRestSimpleService {

    static SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;


    /*
        http://localhost:8080/rest/hello/
        http://localhost:8080/rest/hello/Joao

        http://localhost:7001/rest/hello/Joao

        Schema:
        http://localhost:8080/rest/application.wadl
        http://localhost:7001/rest/application.wadl?detail=true

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


    @Path("login")
    @POST
    public String generateToken(@PathParam("name") String name, @PathParam("password") String password) {
        Instant now = Instant.now();

        // https://developer.okta.com/blog/2018/10/31/jwts-with-java


        String SECRET_KEY = "ZXdvaVlXeG5Jam9nSWtoVE1qVTJJaXdLSW5SNWNDSTZJQ0pLVjFRaUNuMEs=";

        String jws = Jwts.builder()
                .setIssuer("Stormpath")
                .setSubject("msilverman")
                .claim("name", "Micah Silverman")
                .claim("scope", "admins")
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .signWith(
                        SignatureAlgorithm.HS256, SECRET_KEY
                        //SignatureAlgorithm.NONE, "ewoiYWxnIjogIkhTMjU2IiwKInR5cCI6ICJKV1QiCn0K"
                )
                .compact();

        return jws;

    }


}
