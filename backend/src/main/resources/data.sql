-- 预置用户数据
INSERT INTO users (id, username, password, name, email, role, created_at)
VALUES 
(1, 'admin', '$2a$10$D9nFh7UncP5lA8MxlEYp4OdK5LJ6TndUljKj7NHuzwrsYFhrwrw6S', '管理员', 'admin@example.com', 'ADMIN', NOW()),
(2, 'editor', '$2a$10$1Qv0GRwYH7XEkQkVlwOjl.0K37LUMHo3T8nJ8GjeNxR.eH5ewTO0i', '编辑', 'editor@example.com', 'EDITOR', NOW()),
(3, 'approver1', '$2a$10$D9nFh7UncP5lA8MxlEYp4OdK5LJ6TndUljKj7NHuzwrsYFhrwrw6S', '一级审批人', 'approver1@example.com', 'APPROVER', NOW()),
(4, 'approver2', '$2a$10$D9nFh7UncP5lA8MxlEYp4OdK5LJ6TndUljKj7NHuzwrsYFhrwrw6S', '二级审批人', 'approver2@example.com', 'APPROVER', NOW()),
(5, 'publisher', '$2a$10$D9nFh7UncP5lA8MxlEYp4OdK5LJ6TndUljKj7NHuzwrsYFhrwrw6S', '发布员', 'publisher@example.com', 'PUBLISHER', NOW()),
(6, 'lyj', '$2a$10$RfyGeZuy20FSlvjidjpBOuXojgfCqf0pP4zcOfcEag8AeifztGjIy', '管理员', 'rejector@example.com', 'ADMIN', NOW());

-- 预置内容数据
INSERT INTO content (id, title, content, summary, type, category, status, created_by, created_at, submitted_at, approved_at, rejected_at)
VALUES 
-- 草稿状态
(1, '测试新闻稿件', '这是一个测试新闻稿件的内容。', '测试新闻稿件摘要', 'NEWS', '社会新闻', 'DRAFT', 2, NOW(), NULL, NULL, NULL),
(2, '政策解读文章', '这是一篇关于最新政策的解读文章。', '最新政策解读要点', 'ARTICLE', '政策解读', 'DRAFT', 2, NOW(), NULL, NULL, NULL),

-- 待审批状态
(3, '财经分析报告', '这是一份详细的财经分析报告内容。', '本月财经形势分析', 'REPORT', '财经分析', 'PENDING', 2, NOW(), NOW(), NULL, NULL),
(4, '城市规划新政出台', '近日，我市发布新一轮城市规划政策，涉及住房、交通等多个领域...', '解读最新城市规划政策要点', 'NEWS', '城市建设', 'PENDING', 2, NOW(), NOW(), NULL, NULL),

-- 已通过状态
(5, '科技创新成果展示', '我市多家高科技企业在国际展会上展示最新研发成果...', '本地企业科技创新成果汇总', 'ARTICLE', '科技创新', 'APPROVED', 2, NOW(), NOW(), NOW(), NULL),
(6, '教育改革新举措', '教育部门出台新政策，推进素质教育发展...', '教育改革政策解读', 'NEWS', '教育', 'APPROVED', 2, NOW(), NOW(), NOW(), NULL),

-- 已拒绝状态
(7, '某企业经营异常', '某知名企业出现经营异常情况，可能存在财务问题...', '企业经营风险提示', 'REPORT', '财经监管', 'REJECTED', 2, NOW(), NOW(), NULL, NOW()),
(8, '网络谣言调查', '针对近期网络流传的某谣言进行深入调查...', '澄清网络虚假信息', 'ARTICLE', '社会', 'REJECTED', 2, NOW(), NOW(), NULL, NOW());

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
