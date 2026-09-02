package com.sakura.techlab.modules.dictionary.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典详情")
public class DictionaryDetailVo extends DictionaryVo {

    @Schema(description = "字典项列表")
    private List<DictionaryItemVo> items;
}
