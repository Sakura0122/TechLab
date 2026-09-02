package com.sakura.techlab.modules.dictionary.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 数据字典表
 * @TableName dictionary
 */
@TableName(value ="dictionary")
@Data
public class Dictionary {
    /**
     * 字典ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 字典名称
     */
    private String name;

    /**
     * 字典编号
     */
    private String code;

    /**
     * 字典类型：1-系统字典，2-业务字典
     */
    private Integer type;

    /**
     * 字典描述
     */
    private String description;

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
    @TableLogic
    private LocalDateTime deletedAt;
}
