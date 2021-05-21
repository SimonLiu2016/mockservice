package com.mockservice.web.internal;

import com.mockservice.mockconfig.RouteType;
import com.mockservice.service.ConfigService;
import com.mockservice.service.MockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

@RestController
public class ConfigBasedSoapController {

    private static final Logger log = LoggerFactory.getLogger(ConfigBasedSoapController.class);

    private final MockService mockService;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final ConfigService configService;

    public ConfigBasedSoapController(@Qualifier("soap") MockService mockService,
                                     RequestMappingHandlerMapping requestMappingHandlerMapping,
                                     ConfigService configService) {
        this.mockService = mockService;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.configService = configService;

        try {
            register();
        } catch (Exception e) {
            log.error("Failed to register configured routes.", e);
        }
    }

    private void register() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("mock");

        configService
                .getRoutesDistinctByPathAndMethod(RouteType.SOAP)
                .forEach(route -> {
                    RequestMappingInfo mappingInfo = RequestMappingInfo
                            .paths(route.getPath())
                            .methods(route.getMethod())
                            .build();
                    requestMappingHandlerMapping.registerMapping(mappingInfo, this, method);
                });
    }

    public ResponseEntity<String> mock() {
        return mockService.mock(null);
    }

    @ExceptionHandler
    protected ResponseEntity<String> handleException(Throwable t) {
        log.error("", t);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mockService.mockError(t));
    }
}
