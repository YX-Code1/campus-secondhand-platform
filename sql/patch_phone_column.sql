-- 若注册报「系统繁忙」，多半是手机号加密后超过 20 字符，执行本脚本一次即可
USE campus_trade;
ALTER TABLE sys_user MODIFY COLUMN phone VARCHAR(64) COMMENT '手机号(AES加密后Base64存储)';
