package com.sakura.techlab.modules.encryption;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakura.techlab.common.ResultCodeEnum;
import com.sakura.techlab.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.sakura.techlab.modules.encryption.ApiEncryptionPayload.Config;
import static com.sakura.techlab.modules.encryption.ApiEncryptionPayload.DecryptedBody;
import static com.sakura.techlab.modules.encryption.ApiEncryptionPayload.Request;
import static com.sakura.techlab.modules.encryption.ApiEncryptionPayload.Response;

@Service
public class ApiEncryptionService {

    private static final String ALGORITHM = "AES-256-GCM+RSA-OAEP-256";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;
    private static final int AES_KEY_LENGTH = 32;
    private static final Base64.Encoder BASE64_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_DECODER = Base64.getUrlDecoder();

    private final ApiEncryptionProperties properties;
    private final ObjectMapper objectMapper;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, Long> acceptedRequests = new ConcurrentHashMap<>();
    private final KeyPair keyPair;
    private final String keyId;

    public ApiEncryptionService(ApiEncryptionProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(properties.getRsaKeySize(), secureRandom);
            keyPair = generator.generateKeyPair();
            keyId = BASE64_ENCODER.encodeToString(
                    MessageDigest.getInstance("SHA-256").digest(keyPair.getPublic().getEncoded())
            ).substring(0, 16);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("初始化接口加密密钥失败", e);
        }
    }

    public Config getConfig() {
        return new Config(
                properties.isEnabled(),
                keyId,
                ALGORITHM,
                BASE64_ENCODER.encodeToString(keyPair.getPublic().getEncoded())
        );
    }

    public DecryptedBody decrypt(Request payload, String method, String path) {
        validate(payload);
        try {
            byte[] rawKey = decryptKey(payload.encryptedKey());
            byte[] iv = BASE64_DECODER.decode(payload.iv());
            if (rawKey.length != AES_KEY_LENGTH || iv.length != GCM_IV_LENGTH) {
                throw invalidRequest();
            }

            SecretKey key = new SecretKeySpec(rawKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            cipher.updateAAD(requestAad(method, path, payload));
            byte[] content = cipher.doFinal(BASE64_DECODER.decode(payload.ciphertext()));

            long expiredBefore = System.currentTimeMillis() - properties.getTimestampTolerance().toMillis();
            acceptedRequests.entrySet().removeIf(entry -> entry.getValue() < expiredBefore);
            if (acceptedRequests.putIfAbsent(payload.requestId(), payload.timestamp()) != null) {
                throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "请求已被处理");
            }
            return new DecryptedBody(content, key, payload.requestId());
        } catch (BusinessException e) {
            throw e;
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw invalidRequest();
        }
    }

    public Response encrypt(Object body, SecretKey key, String requestId) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            cipher.updateAAD(responseAad(requestId));
            return new Response(
                    BASE64_ENCODER.encodeToString(iv),
                    BASE64_ENCODER.encodeToString(cipher.doFinal(objectMapper.writeValueAsBytes(body)))
            );
        } catch (GeneralSecurityException | JsonProcessingException e) {
            throw new IllegalStateException("加密接口响应失败", e);
        }
    }

    private void validate(Request payload) {
        long now = System.currentTimeMillis();
        long tolerance = properties.getTimestampTolerance().toMillis();
        if (payload == null
                || !keyId.equals(payload.keyId())
                || !StringUtils.hasText(payload.encryptedKey())
                || !StringUtils.hasText(payload.iv())
                || !StringUtils.hasText(payload.ciphertext())
                || !StringUtils.hasText(payload.requestId())
                || payload.timestamp() == null
                || payload.timestamp() < now - tolerance
                || payload.timestamp() > now + tolerance) {
            throw invalidRequest();
        }
    }

    private byte[] decryptKey(String encryptedKey) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        cipher.init(
                Cipher.DECRYPT_MODE,
                keyPair.getPrivate(),
                new javax.crypto.spec.OAEPParameterSpec(
                        "SHA-256",
                        "MGF1",
                        MGF1ParameterSpec.SHA256,
                        javax.crypto.spec.PSource.PSpecified.DEFAULT
                )
        );
        return cipher.doFinal(BASE64_DECODER.decode(encryptedKey));
    }

    private byte[] requestAad(String method, String path, Request payload) {
        return String.join(
                "\n",
                method,
                path,
                payload.keyId(),
                payload.timestamp().toString(),
                payload.requestId()
        ).getBytes(StandardCharsets.UTF_8);
    }

    private byte[] responseAad(String requestId) {
        return ("response\n" + requestId).getBytes(StandardCharsets.UTF_8);
    }

    private BusinessException invalidRequest() {
        return new BusinessException(ResultCodeEnum.PARAMS_ERROR, "加密请求无效");
    }
}
