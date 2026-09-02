package com.sakura.techlab.modules.encryption;

import javax.crypto.SecretKey;

public final class ApiEncryptionPayload {

    private ApiEncryptionPayload() {
    }

    public record Request(
            String keyId,
            String encryptedKey,
            String iv,
            String ciphertext,
            Long timestamp,
            String requestId
    ) {
    }

    public record Response(String iv, String ciphertext) {
    }

    public record Config(boolean enabled, String keyId, String algorithm, String publicKey) {
    }

    public record DecryptedBody(byte[] content, SecretKey key, String requestId) {
    }
}
