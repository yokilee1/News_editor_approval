-- 删除外键约束（如果存在）
SET FOREIGN_KEY_CHECKS = 0;

-- 删除表（如果存在）
DROP TABLE IF EXISTS file_info;
DROP TABLE IF EXISTS approval_record;
DROP TABLE IF EXISTS approval_node;
DROP TABLE IF EXISTS approval_flow;
DROP TABLE IF EXISTS content;
DROP TABLE IF EXISTS workplan;
DROP TABLE IF EXISTS users;

-- 用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL
);

-- 内容表
CREATE TABLE content (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    category VARCHAR(50),
    summary TEXT NULL,
    tags VARCHAR(255) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,
    submitted_at DATETIME NULL,
    approved_at DATETIME NULL,
    rejected_at DATETIME NULL,
    published_at DATETIME NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- 审批流程表
CREATE TABLE approval_flow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    flow_name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    level INT NOT NULL DEFAULT 1,
    category VARCHAR(50) NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_by BIGINT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 审批节点表
CREATE TABLE approval_node (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    node_name VARCHAR(100) NOT NULL,
    flow_id BIGINT NOT NULL,
    next_node BIGINT NULL,
    approver BIGINT NOT NULL,
    approver_role VARCHAR(50) NULL,
    order_num INT NOT NULL,
    is_countersign BOOLEAN NOT NULL DEFAULT FALSE,
    description TEXT NULL,
    allow_reject BOOLEAN NOT NULL DEFAULT TRUE,
    reject_strategy VARCHAR(20) NOT NULL DEFAULT 'TO_PREVIOUS',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,
    FOREIGN KEY (flow_id) REFERENCES approval_flow(id) ON DELETE CASCADE,
    FOREIGN KEY (approver) REFERENCES users(id) ON DELETE CASCADE
);

-- 审批记录表
CREATE TABLE approval_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    node_id BIGINT NULL,
    task_type VARCHAR(255) NOT NULL,
    task_id VARCHAR(50) NOT NULL,
    approver_id BIGINT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    comment TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,
    FOREIGN KEY (node_id) REFERENCES approval_node(id) ON DELETE SET NULL,
    FOREIGN KEY (approver_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 文件信息表
CREATE TABLE file_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100) NULL,
    file_size BIGINT NULL,
    content_id BIGINT NULL,
    upload_by BIGINT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (content_id) REFERENCES content(id) ON DELETE CASCADE,
    FOREIGN KEY (upload_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 工作计划表
CREATE TABLE workplan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,
    submitted_at DATETIME NULL,
    approved_at DATETIME NULL,
    rejected_at DATETIME NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

SET FOREIGN_KEY_CHECKS = 1;
