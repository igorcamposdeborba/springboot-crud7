package br.com.igor.registration.config.utils;

public enum Constants {

    CACHE_CONTROL("Cache-Control");

    private String value;
    Constants(String constantValue) {
        this.value = constantValue;
    }

    public String getValue(){
        return value;
    }
}
