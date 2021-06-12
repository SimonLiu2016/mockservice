package com.mockservice.response;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

public interface MockResponse {
    MockResponse setVariables(@Nullable Map<String, String> variables);
    MockResponse setHost(String host);
    String getResponseBody();
    int getResponseCode();
    HttpHeaders getResponseHeaders();
    boolean hasRequest();
    RequestMethod getRequestMethod();
    String getRequestRelativeReference();
    HttpHeaders getRequestHeaders();
    String getRequestBody();
}
