-- 私聊消息表（已有数据库执行本脚本）
USE campus_trade;

CREATE TABLE IF NOT EXISTS chat_message (
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
