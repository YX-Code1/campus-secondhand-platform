-- 校园二手物品交易管理系统 DB2026-RA-V1.0
CREATE DATABASE IF NOT EXISTS campus_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_trade;

DROP TABLE IF EXISTS chat_message;
DROP TABLE IF EXISTS trade_record;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    username     VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录账号',
    password     VARCHAR(255) NOT NULL COMMENT 'BCrypt加密密码',
    real_name    VARCHAR(50)  COMMENT '姓名',
    phone        VARCHAR(64)  COMMENT '手机号(AES加密后Base64存储)',
    email        VARCHAR(100) COMMENT '邮箱',
    role         VARCHAR(20)  NOT NULL DEFAULT 'STUDENT' COMMENT 'STUDENT/STAFF/ADMIN',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '1正常 0禁用',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role_status (role, status)
) ENGINE=InnoDB COMMENT='用户表';

CREATE TABLE item (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    title        VARCHAR(100) NOT NULL COMMENT '物品名称',
    category     VARCHAR(50)  NOT NULL COMMENT '类别',
    price        DECIMAL(10,2) NOT NULL COMMENT '价格',
    description  TEXT COMMENT '描述',
    image_url    VARCHAR(500) COMMENT '图片路径',
    seller_id    BIGINT       NOT NULL COMMENT '发布者ID',
    status       VARCHAR(20)  NOT NULL DEFAULT 'ON_SALE' COMMENT 'ON_SALE/SOLD/OFF_SHELF/AUDIT_REJECT',
    audit_status VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
    view_count   INT          NOT NULL DEFAULT 0,
    deleted      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_price (price),
    INDEX idx_seller (seller_id),
    INDEX idx_status_deleted (status, deleted, audit_status),
    INDEX idx_create_time (create_time),
    FULLTEXT INDEX ft_title_desc (title, description),
    CONSTRAINT fk_item_seller FOREIGN KEY (seller_id) REFERENCES sys_user(id)
) ENGINE=InnoDB COMMENT='物品表';

CREATE TABLE trade_record (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    trade_no     VARCHAR(32)  NOT NULL UNIQUE COMMENT '交易编号',
    item_id      BIGINT       NOT NULL,
    buyer_id     BIGINT       NOT NULL,
    seller_id    BIGINT       NOT NULL,
    amount       DECIMAL(10,2) NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/IN_PROGRESS/COMPLETED/CANCELLED',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_buyer (buyer_id),
    INDEX idx_seller (seller_id),
    INDEX idx_item (item_id),
    INDEX idx_status (status),
    CONSTRAINT fk_trade_item FOREIGN KEY (item_id) REFERENCES item(id),
    CONSTRAINT fk_trade_buyer FOREIGN KEY (buyer_id) REFERENCES sys_user(id),
    CONSTRAINT fk_trade_seller FOREIGN KEY (seller_id) REFERENCES sys_user(id)
) ENGINE=InnoDB COMMENT='交易记录表';

CREATE TABLE chat_message (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    from_user_id BIGINT       NOT NULL COMMENT '发送者',
    to_user_id   BIGINT       NOT NULL COMMENT '接收者',
    content      VARCHAR(500) NOT NULL COMMENT '消息内容',
    read_flag    TINYINT      NOT NULL DEFAULT 0 COMMENT '0未读 1已读',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_from_to (from_user_id, to_user_id),
    INDEX idx_to_read (to_user_id, read_flag),
    CONSTRAINT fk_chat_from FOREIGN KEY (from_user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_chat_to FOREIGN KEY (to_user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB COMMENT='私聊消息';

-- 默认管理员由应用启动时自动创建：admin / Admin@123
