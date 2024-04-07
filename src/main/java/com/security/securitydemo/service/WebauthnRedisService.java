package com.security.securitydemo.service;

public interface WebauthnRedisService {

    void saveAssertionOptions(String key, String options);
    String getAssertionOptions(String key);
    void deleteAssertionOptions(String key);
}
