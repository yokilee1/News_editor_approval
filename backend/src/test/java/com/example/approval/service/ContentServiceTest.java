package com.example.approval.service;

import com.example.approval.dto.ContentDto;
import com.example.approval.model.Content;
import com.example.approval.repository.ContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ContentServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private ContentService contentService;

    private Content testContent;
    private ContentDto testContentDto;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testContent = new Content();
        testContent.setId(1L);
        testContent.setTitle("测试内容");
        testContent.setContent("这是测试内容的详细内容。");
        testContent.setType(Content.Type.valueOf("NEWS"));
        testContent.setCategory(Content.Category.valueOf("社会新闻"));
        testContent.setStatus(Content.Status.valueOf("DRAFT"));
        testContent.setCreatedBy(userId);
        testContent.setCreatedAt(LocalDateTime.now());

        testContentDto = new ContentDto();
        testContentDto.setTitle("测试内容");
        testContentDto.setContent("这是测试内容的详细内容。");
        testContentDto.setType("NEWS");
        testContentDto.setCategory("社会新闻");
    }

    @Test
    void createContent_shouldReturnCreatedContent() {
        when(contentRepository.save(any(Content.class))).thenReturn(testContent);

        Content createdContent = contentService.createContent(testContentDto, userId);

        assertNotNull(createdContent);
        assertEquals("测试内容", createdContent.getTitle());
        assertEquals("NEWS", createdContent.getType().toString());
        assertEquals("DRAFT", createdContent.getStatus().toString());
        assertEquals(userId, createdContent.getCreatedBy());
        verify(contentRepository, times(1)).save(any(Content.class));
    }

    @Test
    void getAllContents_shouldReturnAllContents() {
        when(contentRepository.findAll()).thenReturn(Arrays.asList(testContent));

        List<Content> contents = contentService.getAllContents();

        assertNotNull(contents);
        assertEquals(1, contents.size());
        assertEquals("测试内容", contents.get(0).getTitle());
    }

    @Test
    void getContentsByUser_shouldReturnUserContents() {
        when(contentRepository.findByCreatedBy(userId)).thenReturn(Arrays.asList(testContent));

        List<Content> contents = contentService.getContentsByUser(userId);

        assertNotNull(contents);
        assertEquals(1, contents.size());
        assertEquals(userId, contents.get(0).getCreatedBy());
    }

    @Test
    void getContentById_shouldReturnContent() {
        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));

        Content foundContent = contentService.getContentById(1L);

        assertNotNull(foundContent);
        assertEquals(1L, foundContent.getId());
        assertEquals("测试内容", foundContent.getTitle());
    }

    @Test
    void updateContent_shouldReturnUpdatedContent() {
        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));
        when(contentRepository.save(any(Content.class))).thenReturn(testContent);

        testContentDto.setTitle("更新的内容标题");
        Content updatedContent = contentService.updateContent(1L, testContentDto, userId);

        assertNotNull(updatedContent);
        assertEquals("更新的内容标题", updatedContent.getTitle());
        verify(contentRepository, times(1)).save(any(Content.class));
    }

    @Test
    void deleteContent_shouldDeleteContent() {
        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));
        doNothing().when(contentRepository).delete(any(Content.class));

        contentService.deleteContent(1L, userId);

        verify(contentRepository, times(1)).delete(any(Content.class));
    }

    @Test
    void getContentsByStatus_shouldReturnContentsByStatus() {
        when(contentRepository.findByStatus(Content.Status.valueOf("DRAFT"))).thenReturn(Arrays.asList(testContent));

        List<Content> contents = contentService.getContentsByStatus("DRAFT");

        assertNotNull(contents);
        assertEquals(1, contents.size());
        assertEquals("DRAFT", contents.get(0).getStatus().toString());
    }

    @Test
    void getContentsByType_shouldReturnContentsByType() {
        when(contentRepository.findByType(Content.Type.valueOf("NEWS"))).thenReturn(Arrays.asList(testContent));

        List<Content> contents = contentService.getContentsByType("NEWS");

        assertNotNull(contents);
        assertEquals(1, contents.size());
        assertEquals("NEWS", contents.get(0).getType().toString());
    }

    @Test
    void getContentsByCategory_shouldReturnContentsByCategory() {
        when(contentRepository.findByCategory(Content.Category.valueOf("社会新闻"))).thenReturn(Arrays.asList(testContent));

        List<Content> contents = contentService.getContentsByCategory("社会新闻");

        assertNotNull(contents);
        assertEquals(1, contents.size());
        assertEquals("社会新闻", contents.get(0).getCategory().toString());
    }
} 