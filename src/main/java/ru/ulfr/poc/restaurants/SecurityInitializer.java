package ru.ulfr.poc.restaurants;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;

/**
 * Replacement for filter registration
 */
public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer implements WebApplicationInitializer {
    @Override
    protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
        super.beforeSpringSecurityFilterChain(servletContext);
    }
}
