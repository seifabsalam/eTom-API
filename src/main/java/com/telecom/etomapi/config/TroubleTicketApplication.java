package com.telecom.etomapi.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

public class TroubleTicketApplication extends ResourceConfig {
    public TroubleTicketApplication() {
        // Register packages for Resources and ExceptionMappers
        packages("com.telecom.etomapi.controller", "com.telecom.etomapi.exception");
        
        // Register Binder for DI
        register(new TroubleTicketBinder());
        
        // Register Jackson for JSON support
        register(JacksonFeature.class);
        
        // Optional: Add logging/tracing if needed
        // register(org.glassfish.jersey.logging.LoggingFeature.class);
    }
}
