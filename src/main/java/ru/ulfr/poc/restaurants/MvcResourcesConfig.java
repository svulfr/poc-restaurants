package ru.ulfr.poc.restaurants;

import freemarker.cache.NullCacheStorage;
import freemarker.template.TemplateException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@ComponentScan({"ru.ulfr.poc.restaurants"})
@EnableWebMvc
@EnableAsync
@EnableScheduling
public class MvcResourcesConfig extends WebMvcConfigurerAdapter {

    @Bean
    public FreeMarkerConfigurer setupViewConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer() {
            @Override
            protected void postProcessConfiguration(freemarker.template.Configuration config) throws IOException, TemplateException {
                super.postProcessConfiguration(config);
                config.setCacheStorage(new NullCacheStorage());
            }
        };
        configurer.setTemplateLoaderPath("/templates");
        configurer.setDefaultEncoding("UTF-8");
        return configurer;
    }

    @Bean(name = "viewResolver")
    public FreeMarkerViewResolver setupViewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setSuffix(".ftl");
        resolver.setViewClass(FreeMarkerView.class);
        resolver.setCache(false);
        resolver.setCacheLimit(0);
        return resolver;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        HttpMessageConverter<?> defaultConverter = null;
        for (HttpMessageConverter<?> c : converters) {
            if (c instanceof StringHttpMessageConverter) {
                defaultConverter = c;
            }
        }
        converters.remove(defaultConverter);
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("/static/")
                .setCachePeriod(0)
                .resourceChain(false)
                .addResolver(new PathResourceResolver());
    }

}
