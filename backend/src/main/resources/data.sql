-- 预置管理员账号
INSERT INTO users (username, password, name, email, role, created_at)
VALUES ('admin', '$2a$10$D9nFh7UncP5lA8MxlEYp4OdK5LJ6TndUljKj7NHuzwrsYFhrwrw6S', '管理员', 'admin@example.com', 'ADMIN', NOW());

-- 预置测试用户
INSERT INTO users (username, password, name, email, role, created_at)
VALUES ('editor', '$2a$10$1Qv0GRwYH7XEkQkVlwOjl.0K37LUMHo3T8nJ8GjeNxR.eH5ewTO0i', '编辑', 'editor@example.com', 'EDITOR', NOW());

-- 预置测试稿件
INSERT INTO documents (title, content, status, created_by, created_at)
VALUES ('测试稿件', '这是一个测试稿件的内容。', 'DRAFT', 2, NOW());

-- 预置审批流程
INSERT INTO approval_flow (name, description, status, created_at)
VALUES ('两级审批', '默认审批流程', 'active', NOW());

-- 添加其他初始用户数据
INSERT INTO users (username, password, name, email, role, created_at)
VALUES ('approver', '$2a$10$D9nFh7UncP5lA8MxlEYp4OdK5LJ6TndUljKj7NHuzwrsYFhrwrw6S', '审批员', 'approver@example.com', 'APPROVER', NOW());
