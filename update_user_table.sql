-- 为sys_user表添加phone字段
ALTER TABLE sys_user ADD COLUMN phone VARCHAR(20) COMMENT '手机号' AFTER nickname;

-- 如果需要添加索引
-- ALTER TABLE sys_user ADD INDEX idx_phone (phone);
