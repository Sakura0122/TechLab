package com.sakura.techlab.controller;

import com.sakura.techlab.common.Result;
import com.sakura.techlab.encryption.ApiEncryptionPayload;
import com.sakura.techlab.encryption.ApiEncryptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "接口加密")
@RestController
@RequestMapping("/api-encryption")
public class ApiEncryptionController {

    private final ApiEncryptionService encryptionService;

    public ApiEncryptionController(ApiEncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    @Operation(summary = "获取接口加密配置和临时公钥")
    @GetMapping("/public-key")
    public Result<ApiEncryptionPayload.Config> getPublicKey() {
        return Result.success(encryptionService.getConfig());
    }
}
