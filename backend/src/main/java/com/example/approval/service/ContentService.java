package com.example.approval.service;

import com.example.approval.dto.ContentDto;
import com.example.approval.model.Content;
import com.example.approval.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    /**
     * 创建稿件
     */
    public Content createContent(ContentDto contentDto, Long userId) {
        Content content = new Content();
        content.setTitle(contentDto.getTitle());
        content.setContent(contentDto.getContent());
        content.setSummary(contentDto.getSummary()); // 设置 summary
        content.setCategory(contentDto.getCategory()); // 设置 category (String)
        content.setTagList(contentDto.getTags()); // 设置 tags
        content.setType("ARTICLE"); // 设置默认 type

        // 根据 isDraft 设置 status
        if (Boolean.TRUE.equals(contentDto.getIsDraft())) {
            content.setStatus(Content.Status.DRAFT);
        } else {
            content.setStatus(Content.Status.PENDING); // 非草稿，直接设为待审批
            content.setSubmittedAt(LocalDateTime.now());
        }

        content.setCreatedBy(userId);
        content.setCreatedAt(LocalDateTime.now());
        content.setUpdatedAt(LocalDateTime.now());
        return contentRepository.save(content);
    }

    /**
     * 更新稿件
     */
    public Content updateContent(Long id, ContentDto contentDto, Long userId) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("稿件不存在"));
        
        // 验证创建者权限
        if (!content.getCreatedBy().equals(userId)) {
            throw new RuntimeException("无权限修改此稿件");
        }
        
        // 只有草稿或审批拒绝状态的稿件才允许修改（根据业务需求调整）
        if (content.getStatus() != Content.Status.DRAFT && content.getStatus() != Content.Status.REJECTED) {
            throw new RuntimeException("当前状态不允许修改稿件");
        }
        
        content.setTitle(contentDto.getTitle());
        content.setContent(contentDto.getContent());
        content.setSummary(contentDto.getSummary()); // 更新 summary
        content.setCategory(contentDto.getCategory()); // 更新 category
        content.setTagList(contentDto.getTags()); // 更新 tags
        if (content.getType() == null) { // 如果更新时 type 为空，也设置默认值
             content.setType("ARTICLE");
        }

        // 根据 isDraft 更新 status
        if (Boolean.TRUE.equals(contentDto.getIsDraft())) {
            // 如果是保存草稿，保持或改为 DRAFT
            if (content.getStatus() != Content.Status.DRAFT) {
                content.setStatus(Content.Status.DRAFT);
                // 清除提交/审批时间等信息（根据需要）
                content.setSubmittedAt(null);
                content.setApprovedAt(null);
                content.setRejectedAt(null);
            }
        } else {
            // 如果是提交审批
            content.setStatus(Content.Status.PENDING);
            content.setSubmittedAt(LocalDateTime.now());
            // 清除审批时间
            content.setApprovedAt(null);
            content.setRejectedAt(null);
        }

        content.setUpdatedAt(LocalDateTime.now());
        return contentRepository.save(content);
    }

    /**
     * 根据ID查询稿件
     */
    public Content getContentById(Long id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("稿件不存在"));
    }

    /**
     * 查询用户创建的稿件
     */
    public List<Content> getContentsByUser(Long userId) {
        return contentRepository.findByCreatedBy(userId);
    }

    /**
     * 查询状态为指定状态的稿件
     */
    public List<Content> getContentsByStatus(Content.Status status) {
        return contentRepository.findByStatus(status);
    }

    /**
     * 获取所有稿件
     */
    public List<Content> getAllContents() {
        return contentRepository.findAll();
    }

    /**
     * 删除稿件，需验证用户权限
     */
    public void deleteContent(long id, Long userId) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("稿件不存在"));
        // 验证创建者权限
        if (!content.getCreatedBy().equals(userId)) {
            throw new RuntimeException("无权限删除此稿件");
        }
        contentRepository.delete(content);
    }

    // 根据需求可增加查询稿件详情、列表等方法
}
