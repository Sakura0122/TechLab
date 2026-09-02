package com.sakura.techlab.modules.dictionary.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "字典信息")
public class DictionaryVo {

    @Schema(description = "字典ID")
    private Long id;

    @Schema(description = "字典名称")
    private String name;

    @Schema(description = "字典编号")
    private String code;

    @Schema(description = "字典类型：1-系统字典，2-业务字典")
    private Integer type;

    @Schema(description = "字典描述")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
