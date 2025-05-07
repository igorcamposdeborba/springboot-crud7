package br.com.igor.registration.config.cache;

import br.com.igor.registration.config.utils.EnvironmentVariables;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Cache: cria hash etag para verificar se o conteúdo do arquivo estático mudou OU retorna 304 Not modified
@Configuration
public class ETagStaticFiles implements WebMvcConfigurer {

    @Bean
    public Filter shallowETagHeaderFilter(){
        return new ShallowEtagHeaderFilter(); // criar e-tag
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        Integer periodValue = Integer.parseInt(EnvironmentVariables.cacheControl60minutes().getHeaderValue().split("=")[1]);

        registry.addResourceHandler("static/**") // cache etag apenas para recursos estáticos (jpg, xlsx, etc)
                .addResourceLocations("/classpath:/static/")
                .setCachePeriod(periodValue);
    }
}
