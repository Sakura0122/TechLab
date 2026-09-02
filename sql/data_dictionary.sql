-- 创建 TechLab 数据库
CREATE DATABASE IF NOT EXISTS `tech_lab`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `tech_lab`;

-- 数据字典表
CREATE TABLE IF NOT EXISTS `dictionary`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '字典ID',
    `name`            VARCHAR(100) NOT NULL COMMENT '字典名称',
    `code`            VARCHAR(100) NOT NULL COMMENT '字典编号',
    `type`            TINYINT      NOT NULL COMMENT '字典类型：1-系统字典，2-业务字典',
    `description`     VARCHAR(500) NULL COMMENT '字典描述',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`      DATETIME     NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_data_dictionary_code` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '数据字典表';

-- 数据字典项表
CREATE TABLE IF NOT EXISTS `dictionary_item`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '字典项ID',
    `dictionary_id`   BIGINT       NOT NULL COMMENT '所属字典ID',
    `name`            VARCHAR(100) NOT NULL COMMENT '字典项名称',
    `code`            VARCHAR(100) NOT NULL COMMENT '字典项编号',
    `description`     VARCHAR(500) NULL COMMENT '字典项描述',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `sort`            INT          NOT NULL DEFAULT 0 COMMENT '排序值，数值越小越靠前',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`      DATETIME     NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_data_dictionary_item_dictionary_code` (`dictionary_id`, `code`),
    KEY `idx_data_dictionary_item_dictionary_sort` (`dictionary_id`, `sort`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '数据字典项表';
