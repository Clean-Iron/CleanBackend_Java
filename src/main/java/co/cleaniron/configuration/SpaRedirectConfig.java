package co.cleaniron.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class SpaRedirectConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Sirve todo lo que esté en /static/**
        registry
                .addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward de la raíz
        registry.addViewController("/").setViewName("forward:/index.html");
        // Forward de cualquier ruta “amigable” a index.html (para rutas tipo /foo/bar)
        registry.addViewController("/{path:[^\\.]*}").setViewName("forward:/index.html");
    }
}
