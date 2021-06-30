package com.mockservice.service;

import com.mockservice.domain.Route;

import java.io.IOException;
import java.util.List;

public interface OpenApiService {
    List<Route> routesFromYaml(String yaml) throws IOException;
}
