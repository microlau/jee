package com.microlau.jee.rest.jersey;

import org.glassfish.jersey.server.ResourceConfig;


public class CustomApplication extends ResourceConfig
{
    public CustomApplication()
    {
        packages("com.microlau.jee.rest.jersey");
        //register(LoggingFilter.class);
        //register(GsonMessageBodyHandler.class);

        //Register Auth Filter here
        //register(AuthenticationFilter.class);
        //register(TokenAuthenticationFilter.class);
    }
}