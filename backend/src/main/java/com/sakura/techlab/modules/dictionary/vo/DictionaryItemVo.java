package com.sakura.techlab.modules.dictionary.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "字典项信息")
public class DictionaryItemVo {

    @Schema(description = "字典项ID")
    private Long id;

    @Schema(description = "字典项名称")
    private String name;

    @Schema(description = "字典项编号")
    private String code;

    @Schema(description = "字典项描述")
    private String description;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "排序值")
    private Integer sort;
}
