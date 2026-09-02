package com.sakura.techlab.modules.dictionary.dto;

import com.sakura.techlab.common.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典分页查询参数")
public class DictionaryPageRequest extends PageDto {

    @Schema(description = "字典类型：1-系统字典，2-业务字典")
    private Integer type;
}
