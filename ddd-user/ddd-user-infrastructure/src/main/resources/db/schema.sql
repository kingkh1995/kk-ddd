DROP TABLE IF EXISTS `user`;

CREATE TABLE `user`
(
    `id`          BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `state`       VARCHAR(10)         NOT NULL COMMENT '用户状态',
    `name`        VARCHAR(20)         NOT NULL DEFAULT '' COMMENT '用户名',
    `phone`       CHAR(11)            NOT NULL DEFAULT '' COMMENT '手机号码',
    `email`       VARCHAR(100)        NOT NULL DEFAULT '' COMMENT '邮箱',
    `create_time` DATETIME(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `version`     INT(11) UNSIGNED    NOT NULL DEFAULT 0 COMMENT '版本号',
    `deleted`     TINYINT(1)          NOT NULL DEFAULT 0 COMMENT '逻辑删除标识符',
    PRIMARY KEY (`id`),
    INDEX `idx_name` (`name`),
    INDEX `idx_phone` (`phone`),
    INDEX `idx_email` (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户';

DROP TABLE IF EXISTS `user_base`;

CREATE TABLE `user_base`
(
    `id`           BIGINT(20) UNSIGNED NOT NULL COMMENT '主键ID',
    `gender`       TINYINT(1)          NULL COMMENT '性别（0-男性 1-女性）',
    `birthday`     DATE                NULL COMMENT '用户状态',
    `profile_path` VARCHAR(32)         NULL COMMENT '头像图片地址',
    `create_time`  DATETIME(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `version`      INT(11) UNSIGNED    NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户基本信息';

DROP TABLE IF EXISTS `user_security`;

CREATE TABLE `user_security`
(
    `id`                 BIGINT(20) UNSIGNED NOT NULL COMMENT '主键ID',
    `encrypted_password` VARCHAR(1000)       NOT NULL COMMENT '加密后密码',
    `hash_algorithm`     VARCHAR(20)         NOT NULL COMMENT '密码加密哈希算法',
    `hash_salt`          VARCHAR(100)        NOT NULL COMMENT '哈希算法盐值',
    `hash_iterations`    TINYINT(4) UNSIGNED NOT NULL COMMENT '哈希算法加盐次数',
    `create_time`        DATETIME(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        DATETIME(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `version`            INT(11) UNSIGNED    NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户安全信息';

DROP TABLE IF EXISTS `account`;

CREATE TABLE `account`
(
    `id`          BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     BIGINT(20) UNSIGNED NOT NULL COMMENT '用户ID',
    `type`        VARCHAR(10)         NOT NULL COMMENT '账号类型',
    `principal`   VARCHAR(100)        NOT NULL DEFAULT '' COMMENT '账号',
    `unbind_time` DATETIME(3)         NULL COMMENT '解绑时间',
    `create_time` DATETIME(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `version`     INT(11) UNSIGNED    NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_principal` (`principal`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户绑定账户';

DROP TABLE IF EXISTS `distributed_lock`;

CREATE TABLE `distributed_lock`
(
    `lock_name` VARCHAR(500) NOT NULL COMMENT '主键及锁名',
    PRIMARY KEY (`lock_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='分布式锁表';