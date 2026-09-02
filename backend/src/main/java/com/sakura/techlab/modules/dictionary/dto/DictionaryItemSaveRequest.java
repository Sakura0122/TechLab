package com.sakura.techlab.modules.dictionary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "字典项保存参数")
public class DictionaryItemSaveRequest {

    @Schema(description = "字典项名称")
    @NotBlank(message = "字典项名称不能为空")
    @Size(max = 100, message = "字典项名称不能超过100个字符")
    private String name;

    @Schema(description = "字典项编号")
    @NotBlank(message = "字典项编号不能为空")
    @Size(max = 100, message = "字典项编号不能超过100个字符")
    private String code;

    @Schema(description = "字典项描述")
    @Size(max = 500, message = "字典项描述不能超过500个字符")
    private String description;

    @Schema(description = "状态：0-禁用，1-启用")
    @NotNull(message = "字典项状态不能为空")
    @Min(value = 0, message = "字典项状态只能为0或1")
    @Max(value = 1, message = "字典项状态只能为0或1")
    private Integer status;

    @Schema(description = "排序值，数值越小越靠前")
    @NotNull(message = "字典项排序值不能为空")
    @Min(value = 0, message = "字典项排序值不能小于0")
    private Integer sort;
}
