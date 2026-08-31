package com.sakura.techlab.controller;


import com.sakura.techlab.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "测试")
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public Result<String> test() {
        return Result.success("test");
    }
}
