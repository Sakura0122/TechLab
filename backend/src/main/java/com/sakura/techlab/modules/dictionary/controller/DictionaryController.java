package com.sakura.techlab.modules.dictionary.controller;

import com.sakura.techlab.common.PageVo;
import com.sakura.techlab.common.Result;
import com.sakura.techlab.modules.dictionary.dto.DictionaryPageRequest;
import com.sakura.techlab.modules.dictionary.dto.DictionarySaveRequest;
import com.sakura.techlab.modules.dictionary.service.DictionaryService;
import com.sakura.techlab.modules.dictionary.vo.DictionaryDetailVo;
import com.sakura.techlab.modules.dictionary.vo.DictionaryVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "数据字典")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/dictionary")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    @Operation(summary = "分页查询字典")
    @GetMapping
    public Result<PageVo<DictionaryVo>> page(@Valid DictionaryPageRequest request) {
        return Result.success(dictionaryService.pageDictionaries(request));
    }

    @Operation(summary = "查询字典详情")
    @GetMapping("/{id}")
    public Result<DictionaryDetailVo> detail(
            @NotNull(message = "字典ID不能为空") @PathVariable Long id
    ) {
        return Result.success(dictionaryService.getDictionary(id));
    }

    @Operation(summary = "新增字典及字典项")
    @PostMapping
    public Result<DictionaryDetailVo> create(@Valid @RequestBody DictionarySaveRequest request) {
        return Result.success(dictionaryService.createDictionary(request));
    }

    @Operation(summary = "编辑字典及字典项")
    @PutMapping("/{id}")
    public Result<DictionaryDetailVo> update(
            @NotNull(message = "字典ID不能为空") @PathVariable Long id,
            @Valid @RequestBody DictionarySaveRequest request
    ) {
        return Result.success(dictionaryService.updateDictionary(id, request));
    }

    @Operation(summary = "删除字典及字典项")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @NotNull(message = "字典ID不能为空") @PathVariable Long id
    ) {
        dictionaryService.deleteDictionary(id);
        return Result.success();
    }
}
