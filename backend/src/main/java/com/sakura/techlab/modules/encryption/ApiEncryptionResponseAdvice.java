package com.sakura.techlab.modules.encryption;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.crypto.SecretKey;

import static com.sakura.techlab.modules.encryption.ApiEncryptionRequestAdvice.ENCRYPTION_KEY_ATTRIBUTE;
import static com.sakura.techlab.modules.encryption.ApiEncryptionRequestAdvice.REQUEST_ID_ATTRIBUTE;

@ControllerAdvice
public class ApiEncryptionResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ApiEncryptionProperties properties;
    private final ApiEncryptionService encryptionService;

    public ApiEncryptionResponseAdvice(
            ApiEncryptionProperties properties,
            ApiEncryptionService encryptionService
    ) {
        this.properties = properties;
        this.encryptionService = encryptionService;
    }

    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return properties.isEnabled();
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        Object key = servletRequest.getAttribute(ENCRYPTION_KEY_ATTRIBUTE);
        Object requestId = servletRequest.getAttribute(REQUEST_ID_ATTRIBUTE);
        return key instanceof SecretKey secretKey && requestId instanceof String id
                ? encryptionService.encrypt(body, secretKey, id)
                : body;
    }
}
