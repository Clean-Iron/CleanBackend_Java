package co.cleaniron.configuration;

import java.io.IOException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class SpaRedirectConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/**")                   // cualquier ruta
                .addResourceLocations("classpath:/static/")  // viene de aquí
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location)
                            throws IOException {
                        // 1) Si existe un archivo/directorio exacto, lo servimos
                        Resource requested = location.createRelative(resourcePath);
                        if (requested.exists() && requested.isReadable()) {
                            // si es carpeta, probamos index.html dentro
                            if (requested.getURL().toString().endsWith("/")) {
                                Resource indexInFolder = location
                                        .createRelative(resourcePath + "/index.html");
                                if (indexInFolder.exists() && indexInFolder.isReadable()) {
                                    return indexInFolder;
                                }
                            }
                            return requested;
                        }
                        // 2) Si no existe, intentamos carpeta + index.html
                        Resource indexInFolder = location
                                .createRelative(resourcePath + "/index.html");
                        if (indexInFolder.exists() && indexInFolder.isReadable()) {
                            return indexInFolder;
                        }
                        // 3) Fallback al index.html raíz del SPA
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}
