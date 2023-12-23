package com.microlau.jee.rest.jersey.filter;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

/**
 * This filter verify the access permissions for a user
 * based on username and passowrd provided in request
 * */
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String REALM = "realm";

    @Context
    private ResourceInfo resourceInfo;

    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";
    private static final String AUTHENTICATION_BEARER = "Bearer";
    private static final String AUTHENTICATION_BASIC = "Basic";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Method method = resourceInfo.getResourceMethod();
        //Access allowed for all
        if (!method.isAnnotationPresent(PermitAll.class)) {
            //Access denied for all
            if (method.isAnnotationPresent(DenyAll.class)) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                        .entity("Access blocked for all users !!").build());
                return;
            }

            //Get request headers
            final MultivaluedMap<String, String> headers = requestContext.getHeaders();

            //Fetch authorization header
            final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

            //If no authorization information present; block access
            if (authorization == null || authorization.isEmpty()) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("You cannot access this resource").build());
                return;
            }

            String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);


            // Verify if is Basic or Bearer
            if (authorizationHeader.startsWith(AUTHENTICATION_BASIC)) {
                //Get encoded username and password
                final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");

                //Decode username and password
                String usernameAndPassword = new String(Base64.getDecoder().decode(encodedUserPassword.getBytes(StandardCharsets.UTF_8)));
                ;

                //Split username and password tokens
                final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
                final String username = tokenizer.nextToken();
                final String password = tokenizer.nextToken();

                //Verifying Username and password
                System.out.println(username);
                System.out.println(password);


                //Verify user access
                if (method.isAnnotationPresent(RolesAllowed.class)) {
                    RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                    Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));

                    //Is user valid?
                    if (!isUserAllowed(username, password, rolesSet)) {
                        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                                .entity("You cannot access this resource").build());
                        return;
                    }
                }
            } else if (authorizationHeader.startsWith(AUTHENTICATION_BEARER)) {
                // Validate the Authorization header
                if (!isTokenBasedAuthentication(authorizationHeader)) {
                    abortWithUnauthorized(requestContext);
                    return;
                }

                // Extract the token from the Authorization header
                String token = authorizationHeader
                        .substring(AUTHENTICATION_BEARER.length()).trim();

                try {
                    // Validate the token
                    validateToken(token);

                } catch (Exception e) {
                    abortWithUnauthorized(requestContext);
                }
            }
        }

    }

    private boolean isUserAllowed(final String username, final String password, final Set<String> rolesSet) {
        boolean isAllowed = false;

        //Step 1. Fetch password from database and match with password in argument
        //If both match then get the defined role for user from database and continue; else return isAllowed [false]
        //Access the database and do this part yourself
        //String userRole = userMgr.getUserRole(username);

        if (username.equals("username") && password.equals("password")) {
            String userRole = "ADMIN";

            //Step 2. Verify user role
            if (rolesSet.contains(userRole)) {
                isAllowed = true;
            }
        }
        return isAllowed;
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {

        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_BEARER.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {

        // Abort the filter chain with a 401 status code response
        // The WWW-Authenticate header is sent along with the response
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE,
                                AUTHENTICATION_BEARER + " realm=\"" + REALM + "\"")
                        .build());
    }

    private void validateToken(final String token) throws Exception {
        // Check if the token was issued by the server and if it's not expired
        // Throw an Exception if the token is invalid
        Claims claims = decodeJWT(token);

        System.out.println(claims);

    }

    public static Claims decodeJWT(String jwt) {
        //This line will throw an exception if it is not a signed JWS (as expected)
        String SECRET_KEY = "ZXdvaVlXeG5Jam9nSWtoVE1qVTJJaXdLSW5SNWNDSTZJQ0pLVjFRaUNuMEs=";
        Claims claims = Jwts.parser()
                .setSigningKey(Base64.getDecoder().decode(SECRET_KEY))
                .parseClaimsJws(jwt).getBody();
        return claims;
    }

}

