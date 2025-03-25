-- 删除外键约束（如果存在）
SET @fk_exists := (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'news_articles' AND CONSTRAINT_NAME = 'FKnjd1vm54yix5sgiqkl19x7wxe' AND CONSTRAINT_TYPE = 'FOREIGN KEY');
SET @sql := IF(@fk_exists > 0, 'ALTER TABLE news_articles DROP FOREIGN KEY FKnjd1vm54yix5sgiqkl19x7wxe', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_exists := (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'documents' AND CONSTRAINT_NAME = 'FKd3y1vm54yix5sgiqkl19x7wxe' AND CONSTRAINT_TYPE = 'FOREIGN KEY');
SET @sql := IF(@fk_exists > 0, 'ALTER TABLE documents DROP FOREIGN KEY FKd3y1vm54yix5sgiqkl19x7wxe', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_exists := (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'approval_node' AND CONSTRAINT_NAME = 'FK12345678901234567890123456789012' AND CONSTRAINT_TYPE = 'FOREIGN KEY');
SET @sql := IF(@fk_exists > 0, 'ALTER TABLE approval_node DROP FOREIGN KEY FK12345678901234567890123456789012', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_exists := (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'approval_record' AND CONSTRAINT_NAME = 'FK98765432109876543210987654321098' AND CONSTRAINT_TYPE = 'FOREIGN KEY');
SET @sql := IF(@fk_exists > 0, 'ALTER TABLE approval_record DROP FOREIGN KEY FK98765432109876543210987654321098', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 删除表（如果存在）
DROP TABLE IF EXISTS workplan;
DROP TABLE IF EXISTS approval_record;
DROP TABLE IF EXISTS approval_node;
DROP TABLE IF EXISTS approval_flows;
DROP TABLE IF EXISTS documents;
DROP TABLE IF EXISTS news_articles;
DROP TABLE IF EXISTS users;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL
);

-- 稿件表
CREATE TABLE documents (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           title VARCHAR(255) NOT NULL,
                           content TEXT NOT NULL,
                           status ENUM('DRAFT', 'PENDING', 'APPROVED', 'REJECTED', 'PUBLISHED', 'ARCHIVED') NOT NULL,
                           created_by BIGINT NOT NULL,
                           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at DATETIME NULL,
                           submitted_at DATETIME NULL,
                           approved_at DATETIME NULL,
                           rejected_at DATETIME NULL,
                           FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- 审批流程表
CREATE TABLE IF NOT EXISTS approval_flow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL
);

-- 审批节点表（用于存储审批流程的各个节点）
CREATE TABLE IF NOT EXISTS approval_node (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flow_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    order_num INT NOT NULL,
    approval_type VARCHAR(20) NOT NULL DEFAULT 'any',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,
    FOREIGN KEY (flow_id) REFERENCES approval_flow(id) ON DELETE CASCADE
);

-- 审批记录表
CREATE TABLE IF NOT EXISTS approval_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    node_id BIGINT NOT NULL,
    task_type VARCHAR(255) NOT NULL,
    task_id BIGINT NOT NULL,
    approver_id BIGINT NULL,
    comment TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,
    FOREIGN KEY (node_id) REFERENCES approval_node(id) ON DELETE CASCADE,
    FOREIGN KEY (approver_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 工作计划表（如果涉及工作计划的审批）
CREATE TABLE workplan (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          title VARCHAR(255) NOT NULL,
                          content TEXT NOT NULL,
                          type VARCHAR(50) NOT NULL, -- 文档类型，如 "日报"、"周报"、"月报"
                          status ENUM('DRAFT', 'PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'DRAFT',
                          document_id BIGINT NULL, -- 关联的稿件ID（可选）
                          created_by BIGINT NOT NULL,
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME NULL,
                          submitted_at DATETIME NULL,
                          approved_at DATETIME NULL,
                          rejected_at DATETIME NULL,
                          FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE SET NULL,
                          FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- 新闻文章表（假设存在）
CREATE TABLE news_articles (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               title VARCHAR(255) NOT NULL,
                               content TEXT NOT NULL,
                               author_id BIGINT NOT NULL,
                               created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at DATETIME NULL,
                               FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);
