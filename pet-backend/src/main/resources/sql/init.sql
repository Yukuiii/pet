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
    `role` VARCHAR(10) NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '账号状态：0-正常，1-封禁',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

INSERT IGNORE INTO `user` (`username`, `email`, `password`, `nickname`, `role`, `status`, `create_time`, `update_time`)
VALUES ('admin', 'admin@pet.local', MD5('123456'), '管理员', 'admin', 0, NOW(), NOW());

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

CREATE TABLE IF NOT EXISTS `knowledge_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_kc_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='养护知识分类表';

CREATE TABLE IF NOT EXISTS `knowledge_article` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文章ID',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `title` VARCHAR(120) NOT NULL COMMENT '标题',
    `summary` VARCHAR(255) DEFAULT NULL COMMENT '摘要',
    `cover` VARCHAR(255) DEFAULT NULL COMMENT '封面URL',
    `content` MEDIUMTEXT NOT NULL COMMENT '正文',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_ka_category_id` (`category_id`),
    KEY `idx_ka_create_time` (`create_time`),
    FULLTEXT KEY `ft_ka_title_content` (`title`, `content`),
    CONSTRAINT `fk_ka_category` FOREIGN KEY (`category_id`) REFERENCES `knowledge_category` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='养护知识文章表';

CREATE TABLE IF NOT EXISTS `announcement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `title` VARCHAR(120) NOT NULL COMMENT '公告标题',
    `content` TEXT NOT NULL COMMENT '公告内容',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-有效，1-无效',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_announcement_status` (`status`),
    KEY `idx_announcement_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

CREATE TABLE IF NOT EXISTS `site_config` (
    `id` BIGINT NOT NULL COMMENT '配置ID（固定为1）',
    `site_name` VARCHAR(60) NOT NULL COMMENT '站点名称',
    `logo` VARCHAR(255) DEFAULT NULL COMMENT 'Logo URL',
    `contact_email` VARCHAR(120) DEFAULT NULL COMMENT '联系邮箱',
    `contact_phone` VARCHAR(50) DEFAULT NULL COMMENT '联系电话',
    `icp` VARCHAR(60) DEFAULT NULL COMMENT '备案号',
    `footer_text` VARCHAR(255) DEFAULT NULL COMMENT '页脚文案',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网站基础配置表';

INSERT IGNORE INTO `knowledge_category` (`id`, `name`, `sort`) VALUES
  (1, '喂养', 10),
  (2, '疾病', 20),
  (3, '行为', 30),
  (4, '日常护理', 40);

INSERT IGNORE INTO `knowledge_article` (`id`, `category_id`, `title`, `summary`, `cover`, `content`) VALUES
  (1, 1, '幼犬喂养入门：从频次到食量', '介绍幼犬阶段的喂养频次、主食选择与常见误区。', NULL,
   '1) 喂养频次：幼犬建议少量多餐。\n2) 主食选择：优先选择适龄犬粮。\n3) 饮水：随时提供清洁饮水。\n4) 禁忌：避免高盐高油、巧克力、葡萄等。'),
  (2, 2, '常见皮肤问题：瘙痒与掉毛怎么办', '从环境、寄生虫与饮食三个角度排查皮肤问题。', NULL,
   '1) 先排查跳蚤/螨虫等寄生虫。\n2) 观察是否更换洗护/环境潮湿。\n3) 饮食过敏可考虑单一蛋白试喂。\n4) 严重或持续建议及时就医。'),
  (3, 3, '拆家与乱叫：行为纠正的三步法', '通过消耗精力、建立规则和正向强化改善行为。', NULL,
   '1) 运动与嗅闻游戏：先消耗精力。\n2) 规则一致：全家口令一致。\n3) 正向强化：奖励正确行为，避免体罚。'),
  (4, 4, '疫苗与驱虫：时间表怎么安排', '给出常见疫苗与体内外驱虫的建议时间点。', NULL,
   '1) 疫苗：按兽医建议完成基础免疫。\n2) 体内外驱虫：根据体重、年龄与生活环境制定周期。\n3) 记录：建议建立健康记录，方便追踪。');

INSERT IGNORE INTO `announcement` (`id`, `title`, `content`, `status`) VALUES
  (1, '社区公告：文明养宠倡议', '请文明交流，禁止发布违法违规、辱骂攻击、涉黄涉赌等不当内容。管理员将对违规内容进行处理。', 0);

INSERT IGNORE INTO `site_config` (`id`, `site_name`, `logo`, `contact_email`, `contact_phone`, `icp`, `footer_text`)
VALUES (1, '宠物管理系统', NULL, 'admin@pet.local', NULL, NULL, 'Copyright © Pet');
