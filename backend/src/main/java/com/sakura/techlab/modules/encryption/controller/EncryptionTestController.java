package com.sakura.techlab.modules.encryption.controller;


import com.sakura.techlab.common.Result;
import com.sakura.techlab.modules.encryption.ApiEncrypted;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Tag(name = "测试")
@RestController
@RequestMapping("/test")
public class EncryptionTestController {

    @GetMapping
    public Result<String> test() {
        return Result.success("test");
    }

    @Operation(summary = "测试加密请求和响应")
    @ApiEncrypted
    @PostMapping("/encryption")
    public Result<EncryptionTestResponse> testEncryption(
            @Valid @RequestBody EncryptionTestRequest request
    ) {
        return Result.success(new EncryptionTestResponse(
                "你好，" + request.name(),
                request.message(),
                LocalDateTime.now()
        ));
    }

    public record EncryptionTestRequest(
            @NotBlank(message = "名称不能为空") String name,
            @NotBlank(message = "消息不能为空") String message
    ) {
    }

    public record EncryptionTestResponse(
            String greeting,
            String receivedMessage,
            LocalDateTime serverTime
    ) {
    }
}
