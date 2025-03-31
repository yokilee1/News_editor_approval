-- 预置用户数据
INSERT INTO users (id, username, password, name, email, role, created_at)
VALUES 
(1, 'admin', '$2a$10$D9nFh7UncP5lA8MxlEYp4OdK5LJ6TndUljKj7NHuzwrsYFhrwrw6S', '管理员', 'admin@example.com', 'ADMIN', NOW()),
(2, 'editor', '$2a$10$1Qv0GRwYH7XEkQkVlwOjl.0K37LUMHo3T8nJ8GjeNxR.eH5ewTO0i', '编辑', 'editor@example.com', 'EDITOR', NOW()),
(3, 'approver1', '$2a$10$D9nFh7UncP5lA8MxlEYp4OdK5LJ6TndUljKj7NHuzwrsYFhrwrw6S', '一级审批人', 'approver1@example.com', 'APPROVER', NOW()),
(4, 'approver2', '$2a$10$D9nFh7UncP5lA8MxlEYp4OdK5LJ6TndUljKj7NHuzwrsYFhrwrw6S', '二级审批人', 'approver2@example.com', 'APPROVER', NOW()),
(5, 'publisher', '$2a$10$D9nFh7UncP5lA8MxlEYp4OdK5LJ6TndUljKj7NHuzwrsYFhrwrw6S', '发布员', 'publisher@example.com', 'PUBLISHER', NOW());

-- 预置内容数据
INSERT INTO content (id, title, content, type, category, status, created_by, created_at)
VALUES 
(1, '测试新闻稿件', '这是一个测试新闻稿件的内容。', 'NEWS', '社会新闻', 'DRAFT', 2, NOW()),
(2, '政策解读文章', '这是一篇关于最新政策的解读文章。', 'ARTICLE', '政策解读', 'DRAFT', 2, NOW()),
(3, '财经分析报告', '这是一份详细的财经分析报告内容。', 'REPORT', '财经分析', 'PENDING', 2, NOW());

-- 预置审批流程
INSERT INTO approval_flow (id, title, name, flow_name, description, level, category, is_active, is_default, created_by, created_at)
VALUES 
(1, '新闻审批流程', '新闻审批', '新闻审批流程', '适用于所有新闻类稿件的审批流程', 2, 'NEWS', TRUE, TRUE, 1, NOW()),
(2, '文章审批流程', '文章审批', '文章审批流程', '适用于所有文章类稿件的审批流程', 1, 'ARTICLE', TRUE, FALSE, 1, NOW()),
(3, '报告审批流程', '报告审批', '报告审批流程', '适用于所有报告类稿件的审批流程', 3, 'REPORT', TRUE, FALSE, 1, NOW());

-- 预置审批节点
INSERT INTO approval_node (id, node_name, flow_id, next_node, approver, approver_role, order_num, is_countersign, description, created_at)
VALUES 
(1, '新闻初审', 1, 2, 3, 'APPROVER', 1, FALSE, '新闻稿件初步审核', NOW()),
(2, '新闻终审', 1, NULL, 4, 'APPROVER', 2, FALSE, '新闻稿件终审审核', NOW()),
(3, '文章审核', 2, NULL, 3, 'APPROVER', 1, FALSE, '文章内容审核', NOW()),
(4, '报告初审', 3, 5, 3, 'APPROVER', 1, FALSE, '报告初步审核', NOW()),
(5, '报告复审', 3, 6, 4, 'APPROVER', 2, FALSE, '报告复审', NOW()),
(6, '报告终审', 3, NULL, 1, 'ADMIN', 3, FALSE, '报告终审', NOW());

-- 预置审批记录
INSERT INTO approval_record (id, node_id, task_type, task_id, approver_id, status, comment, created_at)
VALUES 
(1, 1, 'Content', '3', 3, 'PENDING', NULL, NOW());

-- 预置文件信息
INSERT INTO file_info (id, file_name, original_name, file_type, file_size, content_id, upload_by, created_at)
VALUES 
(1, 'content_1_file_1.docx', '新闻附件.docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 25600, 1, 2, NOW()),
(2, 'content_3_file_1.pdf', '财经分析报告.pdf', 'application/pdf', 512000, 3, 2, NOW());

-- 预置工作计划
INSERT INTO workplan (id, title, content, type, status, created_by, created_at)
VALUES 
(1, '周一新闻采编计划', '1. 采访市政府新闻发布会\n2. 整理社区反馈意见\n3. 撰写两篇社会新闻', 'DAILY', 'DRAFT', 2, NOW()),
(2, '本周工作计划', '本周计划完成5篇新闻稿件和2篇深度报道', 'WEEKLY', 'APPROVED', 2, NOW());
