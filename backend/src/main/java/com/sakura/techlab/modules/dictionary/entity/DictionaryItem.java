package com.sakura.techlab.modules.dictionary.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 数据字典项表
 * @TableName dictionary_item
 */
@TableName(value ="dictionary_item")
@Data
public class DictionaryItem {
    /**
     * 字典项ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 所属字典ID
     */
    private Long dictionaryId;

    /**
     * 字典项名称
     */
    private String name;

    /**
     * 字典项编号
     */
    private String code;

    /**
     * 字典项描述
     */
    private String description;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 排序值，数值越小越靠前
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 删除时间
     */
    private LocalDateTime deletedAt;
}
