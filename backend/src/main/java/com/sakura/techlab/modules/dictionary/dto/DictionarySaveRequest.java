package com.sakura.techlab.modules.dictionary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "字典保存参数")
public class DictionarySaveRequest {

    @Schema(description = "字典名称")
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典名称不能超过100个字符")
    private String name;

    @Schema(description = "字典编号")
    @NotBlank(message = "字典编号不能为空")
    @Size(max = 100, message = "字典编号不能超过100个字符")
    private String code;

    @Schema(description = "字典类型：1-系统字典，2-业务字典")
    @NotNull(message = "字典类型不能为空")
    private Integer type;

    @Schema(description = "字典描述")
    @Size(max = 500, message = "字典描述不能超过500个字符")
    private String description;

    @Schema(description = "字典项列表")
    @Valid
    @NotNull(message = "字典项列表不能为空")
    private List<DictionaryItemSaveRequest> items = new ArrayList<>();
}
