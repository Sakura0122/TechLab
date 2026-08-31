package com.sakura.techlab.encryption;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import static com.sakura.techlab.encryption.ApiEncryptionPayload.DecryptedBody;
import static com.sakura.techlab.encryption.ApiEncryptionPayload.Request;

@ControllerAdvice
public class ApiEncryptionRequestAdvice extends RequestBodyAdviceAdapter {

    public static final String ENCRYPTION_KEY_ATTRIBUTE =
            ApiEncryptionRequestAdvice.class.getName() + ".key";
    public static final String REQUEST_ID_ATTRIBUTE =
            ApiEncryptionRequestAdvice.class.getName() + ".requestId";

    private final ApiEncryptionProperties properties;
    private final ApiEncryptionService encryptionService;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    public ApiEncryptionRequestAdvice(
            ApiEncryptionProperties properties,
            ApiEncryptionService encryptionService,
            ObjectMapper objectMapper,
            HttpServletRequest request
    ) {
        this.properties = properties;
        this.encryptionService = encryptionService;
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public boolean supports(
            MethodParameter methodParameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return properties.isEnabled()
                && (methodParameter.hasMethodAnnotation(ApiEncrypted.class)
                || AnnotatedElementUtils.hasAnnotation(
                        methodParameter.getContainingClass(),
                        ApiEncrypted.class
                ));
    }

    @Override
    public HttpInputMessage beforeBodyRead(
            HttpInputMessage inputMessage,
            MethodParameter parameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) throws IOException {
        DecryptedBody decryptedBody = encryptionService.decrypt(
                objectMapper.readValue(inputMessage.getBody(), Request.class),
                request.getMethod(),
                request.getRequestURI()
        );
        request.setAttribute(ENCRYPTION_KEY_ATTRIBUTE, decryptedBody.key());
        request.setAttribute(REQUEST_ID_ATTRIBUTE, decryptedBody.requestId());

        return new HttpInputMessage() {
            @Override
            public InputStream getBody() {
                return new ByteArrayInputStream(decryptedBody.content());
            }

            @Override
            public HttpHeaders getHeaders() {
                return inputMessage.getHeaders();
            }
        };
    }
}
