package com.example.approval.service;

import com.example.approval.dto.ContentDto;
import com.example.approval.model.Content;
import com.example.approval.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

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
     * 更新稿件，仅允许草稿状态稿件修改
     */
    public Content updateContent(Long contentId, ContentDto contentDto) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("稿件不存在"));
        if (!content.getStatus().equals(Content.Status.DRAFT)) {
            throw new RuntimeException("非草稿状态稿件不能修改");
        }
        content.setTitle(contentDto.getTitle());
        content.setContent(contentDto.getContent());
        content.setUpdatedAt(LocalDateTime.now());
        return contentRepository.save(content);
    }

    // 根据需求可增加查询稿件详情、列表等方法
}
