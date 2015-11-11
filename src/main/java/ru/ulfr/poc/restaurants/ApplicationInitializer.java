package ru.ulfr.poc.restaurants;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

@SuppressWarnings("unused")
public class ApplicationInitializer implements WebApplicationInitializer {


    public void onStartup(ServletContext servletContext) throws ServletException {

        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(SecurityConfig.class);

        AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
        dispatcherContext.register(MvcResourcesConfig.class);
        dispatcherContext.setServletContext(servletContext);

        ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcher", new DispatcherServlet());
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
        registration.setMultipartConfig(new MultipartConfigElement("/tmp", 1024 * 1024 * 5, 1024 * 1024 * 5 * 5, 1024 * 1024));

        Config.getInstance();
    }
}
