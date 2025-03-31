package com.example.approval.service;

import com.example.approval.dto.ContentDto;
import com.example.approval.model.Content;
import com.example.approval.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    /**
     * 创建稿件，默认保存为草稿
     */
    public Content createContent(ContentDto contentDto, Long userId) {
        Content content = new Content();
        content.setTitle(contentDto.getTitle());
        content.setContent(contentDto.getContent());
        content.setStatus(Content.Status.DRAFT);
        content.setCreatedBy(userId);
        content.setCreatedAt(LocalDateTime.now());
        content.setUpdatedAt(LocalDateTime.now());
        return contentRepository.save(content);
    }

    /**
     * 更新稿件，仅允许草稿状态稿件修改，并验证用户权限
     */
    public Content updateContent(Long id, ContentDto contentDto, Long userId) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("稿件不存在"));
        // 验证创建者权限
        if (!content.getCreatedBy().equals(userId)) {
            throw new RuntimeException("无权限修改此稿件");
        }
        if (content.getStatus() != Content.Status.DRAFT) {
            throw new RuntimeException("只有草稿状态的稿件才允许修改");
        }
        content.setTitle(contentDto.getTitle());
        content.setContent(contentDto.getContent());
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
    public List<Content> getContentsByStatus(String status) {
        return contentRepository.findByStatus(Content.Status.valueOf(status));
    }

    /**
     * 按类型查询稿件
     */
    public List<Content> getContentsByType(String type) {
        return contentRepository.findByType(Content.Type.valueOf(type));
    }

    /**
     * 按分类查询稿件
     */
    public List<Content> getContentsByCategory(String category) {
        return contentRepository.findByCategory(Content.Category.valueOf(category));
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
