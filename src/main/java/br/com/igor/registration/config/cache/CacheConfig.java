package br.com.igor.registration.config.cache;

import br.com.igor.registration.config.utils.Constants;
import br.com.igor.registration.config.utils.EnvironmentVariables;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.concurrent.TimeUnit;

// Interceptador global para colocar informações no header do cache
@Configuration
public class CacheConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CacheControlInterceptor());
    }

    private static class CacheControlInterceptor implements HandlerInterceptor {

        // Ciclo de vida: PostHandle permite que o interceptador execute depois do controller (isso permite que o controller tenha um override para mudar o tempo de max-age do cache)
        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){
            if(! response.containsHeader(Constants.CACHE_CONTROL.getValue())){
                CacheControl cacheControl = EnvironmentVariables.cacheControl60minutes();
                response.setHeader(Constants.CACHE_CONTROL.getValue(), cacheControl.getHeaderValue());
            }
        }
    }

}
