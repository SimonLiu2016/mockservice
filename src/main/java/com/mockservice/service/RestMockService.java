package com.mockservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockservice.mockconfig.Route;
import com.mockservice.mockconfig.RouteType;
import com.mockservice.request.RequestFacade;
import com.mockservice.request.RestRequestFacade;
import com.mockservice.resource.MockResource;
import com.mockservice.resource.RestMockResource;
import com.mockservice.template.TemplateEngine;
import com.mockservice.web.webapp.ErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ConcurrentLruCache;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service("rest")
public class RestMockService implements MockService {

    private static final Logger log = LoggerFactory.getLogger(RestMockService.class);

    private final HttpServletRequest request;
    private final TemplateEngine templateEngine;
    private final ConfigService configService;
    private final ConcurrentLruCache<Route, MockResource> resourceCache;

    public RestMockService(HttpServletRequest request,
                           TemplateEngine templateEngine,
                           ConfigService configService,
                           @Value("${application.cache.rest-resource}") int cacheSizeLimit) {
        this.request = request;
        this.templateEngine = templateEngine;
        this.configService = configService;
        resourceCache = new ConcurrentLruCache<>(cacheSizeLimit, this::loadResource);
    }

    private MockResource loadResource(Route route) {
        return configService.getEnabledRoute(route)
                .map(r -> new RestMockResource(templateEngine, r.getResponse()))
                .orElse(null);
    }

    @Override
    public void cacheRemove(Route route) {
        resourceCache.remove(route);
    }

    @Override
    public ResponseEntity<String> mock(Map<String, String> variables) {
        RequestFacade requestFacade = new RestRequestFacade(request);
        Route route = getRoute(requestFacade);
        log.info("Route requested: {}", route);
        MockResource resource = resourceCache.get(route);
        Map<String, String> requestVariables = requestFacade.getVariables(variables);
        return ResponseEntity
                .status(resource.getCode())
                .headers(resource.getHeaders())
                .body(resource.getBody(requestVariables));
    }

    private Route getRoute(RequestFacade requestFacade) {
        return new Route()
                .setType(RouteType.REST)
                .setMethod(requestFacade.getRequestMethod())
                .setPath(requestFacade.getEndpoint())
                .setSuffix(requestFacade.getSuffix());
    }

    @Override
    public String mockError(Throwable t) {
        try {
            return new ObjectMapper().writeValueAsString(new ErrorInfo(t));
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
