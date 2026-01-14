-- 创建数据库
CREATE DATABASE IF NOT EXISTS pet DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pet;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（MD5加密）',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '用户角色：0-普通用户，1-管理员',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '账号状态：0-正常，1-封禁',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `pet` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '宠物ID',
    `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
    `name` VARCHAR(50) NOT NULL COMMENT '宠物姓名',
    `breed` VARCHAR(50) DEFAULT NULL COMMENT '品种',
    `gender` TINYINT DEFAULT NULL COMMENT '性别：0-公，1-母',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `photo` VARCHAR(255) DEFAULT NULL COMMENT '照片URL',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_pet_user_id` (`user_id`),
    CONSTRAINT `fk_pet_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物档案表';

CREATE TABLE IF NOT EXISTS `health_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '健康记录ID',
    `pet_id` BIGINT NOT NULL COMMENT '宠物ID',
    `record_time` DATETIME NOT NULL COMMENT '记录时间',
    `title` VARCHAR(100) NOT NULL COMMENT '标题/疫苗名称',
    `content` TEXT DEFAULT NULL COMMENT '内容/备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_hr_pet_id` (`pet_id`),
    KEY `idx_hr_record_time` (`record_time`),
    CONSTRAINT `fk_hr_pet` FOREIGN KEY (`pet_id`) REFERENCES `pet` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物健康记录表';

CREATE TABLE IF NOT EXISTS `community_post` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '动态ID',
    `user_id` BIGINT NOT NULL COMMENT '发布用户ID',
    `content` TEXT NOT NULL COMMENT '动态内容',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_cp_user_id` (`user_id`),
    CONSTRAINT `fk_cp_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区动态表';

CREATE TABLE IF NOT EXISTS `community_post_image` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '动态图片ID',
    `post_id` BIGINT NOT NULL COMMENT '动态ID',
    `url` VARCHAR(255) NOT NULL COMMENT '图片URL',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_cpi_post_id` (`post_id`),
    CONSTRAINT `fk_cpi_post` FOREIGN KEY (`post_id`) REFERENCES `community_post` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区动态图片表';

CREATE TABLE IF NOT EXISTS `community_comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `post_id` BIGINT NOT NULL COMMENT '动态ID',
    `user_id` BIGINT NOT NULL COMMENT '评论用户ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_cc_post_id` (`post_id`),
    KEY `idx_cc_user_id` (`user_id`),
    CONSTRAINT `fk_cc_post` FOREIGN KEY (`post_id`) REFERENCES `community_post` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_cc_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区评论表';
