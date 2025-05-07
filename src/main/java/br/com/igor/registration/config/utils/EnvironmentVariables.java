package br.com.igor.registration.config.utils;

import org.springframework.http.CacheControl;

import java.util.concurrent.TimeUnit;

public class EnvironmentVariables {

    public static CacheControl cacheControl60minutes(){
        return CacheControl.maxAge(1, TimeUnit.HOURS);
    }
}
