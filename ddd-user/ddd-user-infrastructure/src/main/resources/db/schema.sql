DROP TABLE IF EXISTS account;

CREATE TABLE account
(
    id          BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    create_time DATETIME(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME(3)         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    user_id     BIGINT(20) UNSIGNED NOT NULL COMMENT '用户ID',
    state       VARCHAR(100)        NOT NULL COMMENT '账户状态',
    version     BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户账户（收付款账户、虚拟账户等）';

DROP TABLE IF EXISTS user;

CREATE TABLE user
(
    id       BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name     VARCHAR(30)         NOT NULL COMMENT '姓名',
    username VARCHAR(100)        NOT NULL COMMENT '用户名',
    password VARCHAR(32)         NOT NULL DEFAULT '' COMMENT '密码',
    gender   CHAR(6)             NULL COMMENT '性别',
    age      TINYINT(3)          NULL COMMENT '年龄',
    email    VARCHAR(50)         NULL     DEFAULT NULL COMMENT '邮箱',
    version  INT(11) UNSIGNED    NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
    deleted  TINYINT(1)          NOT NULL DEFAULT 0 COMMENT '逻辑删除标识符',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户';

DROP TABLE IF EXISTS distributed_lock;

CREATE TABLE distributed_lock
(
    lock_name VARCHAR(500) NOT NULL COMMENT '主键及锁名',
    PRIMARY KEY (lock_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='分布式锁表';