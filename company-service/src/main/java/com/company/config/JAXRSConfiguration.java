package com.company.config;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author flora
 */
@ApplicationScoped
@ApplicationPath("api")
public class JAXRSConfiguration extends Application {
    
}
