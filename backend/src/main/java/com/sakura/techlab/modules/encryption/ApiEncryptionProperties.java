package com.sakura.techlab.modules.encryption;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "tech-lab.api-encryption")
public class ApiEncryptionProperties {

    private boolean enabled = true;
    private int rsaKeySize = 3072;
    private Duration timestampTolerance = Duration.ofMinutes(5);
}
