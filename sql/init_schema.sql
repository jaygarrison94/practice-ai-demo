-- ============================================================
-- 私人管家 APP - 数据库初始化脚本
-- Database: private_butler
-- ============================================================

CREATE DATABASE IF NOT EXISTS private_butler DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE private_butler;

-- 1. 用户账号表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    password VARCHAR(256) NOT NULL COMMENT '密码(MD5)',
    remember_password TINYINT(1) DEFAULT 0 COMMENT '记住密码: 0-否 1-是',
    login_fail_count TINYINT DEFAULT 0 COMMENT '连续登录失败次数',
    lock_time DATETIME NULL COMMENT '账号锁定时间(null=未锁定)',
    last_login_time DATETIME NULL COMMENT '最后登录时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-正常 0-禁用',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    UNIQUE KEY uk_phone (phone),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账号表';

-- 2. 用户资料表
CREATE TABLE IF NOT EXISTS sys_user_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    nickname VARCHAR(20) NOT NULL COMMENT '昵称(1-10字符)',
    avatar VARCHAR(512) NULL COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别: 0-未设置 1-男 2-女',
    birthday DATE NULL COMMENT '生日',
    remind_sound_enabled TINYINT(1) DEFAULT 1 COMMENT '提醒铃声: 1-开 0-关',
    remind_vibration_enabled TINYINT(1) DEFAULT 1 COMMENT '提醒振动: 1-开 0-关',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    UNIQUE KEY uk_user_id (user_id),
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户资料表';

-- 3. 日程表
CREATE TABLE IF NOT EXISTS sch_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(100) NOT NULL COMMENT '日程标题',
    schedule_date DATE NOT NULL COMMENT '日程日期',
    schedule_time TIME NOT NULL COMMENT '日程时间',
    remind_before VARCHAR(20) DEFAULT '10m' COMMENT '提前提醒(5m/10m/30m/1h/1d)',
    note VARCHAR(500) NULL COMMENT '备注',
    category VARCHAR(20) DEFAULT 'OTHER' COMMENT '分类: WORK/LIFE/IMPORTANT/OTHER',
    repeat_type VARCHAR(10) DEFAULT 'NONE' COMMENT '重复类型: NONE/DAILY/WEEKLY/MONTHLY',
    repeat_week_days VARCHAR(20) NULL COMMENT '每周重复日(1,3,5)',
    repeat_month_date TINYINT NULL COMMENT '每月重复日期(1-31)',
    repeat_end_date DATE NULL COMMENT '重复结束日期(null=无限期)',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-待提醒 2-已完成 0-已删除',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    KEY idx_user_date (user_id, schedule_date),
    KEY idx_user_category (user_id, category),
    KEY idx_user_status (user_id, status),
    CONSTRAINT fk_schedule_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日程表';

-- 4. 记账记录表
CREATE TABLE IF NOT EXISTS bk_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type TINYINT NOT NULL COMMENT '类型: 1-支出 2-收入',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额(≤999999.99)',
    category_id BIGINT NULL COMMENT '分类ID(null=其他)',
    category_name VARCHAR(20) NOT NULL COMMENT '分类名称(冗余)',
    note VARCHAR(200) NULL COMMENT '备注',
    record_date DATE NOT NULL COMMENT '记账日期',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-正常 0-已删除',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    KEY idx_user_date (user_id, record_date),
    KEY idx_user_type_date (user_id, type, record_date),
    KEY idx_user_category (user_id, category_id),
    CONSTRAINT fk_record_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记账记录表';

-- 5. 自定义分类表
CREATE TABLE IF NOT EXISTS bk_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL COMMENT '用户ID(null=系统预设)',
    name VARCHAR(20) NOT NULL COMMENT '分类名称(1-8字符)',
    type TINYINT NOT NULL COMMENT '类型: 1-支出 2-收入',
    color VARCHAR(10) DEFAULT '#999999' COMMENT '分类颜色(HEX)',
    sort_order TINYINT DEFAULT 0 COMMENT '排序号',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-正常 0-已删除',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    KEY idx_user_type (user_id, type),
    CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义分类表';

-- 6. 短信验证码表
CREATE TABLE IF NOT EXISTS sys_sms_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    code VARCHAR(10) NOT NULL COMMENT '验证码',
    type VARCHAR(20) NOT NULL COMMENT '类型: REGISTER/RESET_PWD',
    expires_at DATETIME NOT NULL COMMENT '过期时间(5分钟)',
    used TINYINT(1) DEFAULT 0 COMMENT '是否已使用: 0-否 1-是',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    KEY idx_phone_type (phone, type, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信验证码表';

-- ============================================================
-- 初始数据: 系统预设分类
-- ============================================================
INSERT INTO bk_category (user_id, name, type, color, sort_order, status, created_at, updated_at) VALUES
(NULL, '餐饮', 1, '#FF6B6B', 1, 1, NOW(), NOW()),
(NULL, '交通', 1, '#4ECDC4', 2, 1, NOW(), NOW()),
(NULL, '购物', 1, '#45B7D1', 3, 1, NOW(), NOW()),
(NULL, '房租', 1, '#96CEB4', 4, 1, NOW(), NOW()),
(NULL, '其他', 1, '#999999', 5, 1, NOW(), NOW()),
(NULL, '工资', 2, '#2ECC71', 6, 1, NOW(), NOW()),
(NULL, '兼职', 2, '#F39C12', 7, 1, NOW(), NOW()),
(NULL, '理财', 2, '#9B59B6', 8, 1, NOW(), NOW()),
(NULL, '其他', 2, '#999999', 9, 1, NOW(), NOW());
