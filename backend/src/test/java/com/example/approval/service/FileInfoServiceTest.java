package com.example.approval.service;

import com.example.approval.model.FileInfo;
import com.example.approval.repository.FileInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class FileInfoServiceTest {

    @Mock
    private FileInfoRepository fileInfoRepository;

    @InjectMocks
    private FileInfoService fileInfoService;

    private FileInfo testFileInfo;
    private MultipartFile testFile;
    private Long contentId = 1L;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 创建测试文件信息
        testFileInfo = new FileInfo();
        testFileInfo.setId(1L);
        testFileInfo.setFileName("test_file.docx");
        testFileInfo.setOriginalName("测试文件.docx");
        testFileInfo.setFileType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        testFileInfo.setFileSize(25600L);
        testFileInfo.setContentId(contentId);
        testFileInfo.setUploadBy(userId);

        // 创建测试文件
        testFile = new MockMultipartFile(
                "file",
                "测试文件.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "测试内容".getBytes()
        );
    }

    @Test
    void uploadFile_shouldSaveAndReturnFileInfo() throws Exception {
        when(fileInfoRepository.save(any(FileInfo.class))).thenReturn(testFileInfo);

        FileInfo savedFileInfo = fileInfoService.uploadFile(testFile, contentId, userId);

        assertNotNull(savedFileInfo);
        assertEquals(testFileInfo.getOriginalName(), savedFileInfo.getOriginalName());
        verify(fileInfoRepository, times(1)).save(any(FileInfo.class));
    }

    @Test
    void getFilesByContentId_shouldReturnFiles() {
        when(fileInfoRepository.findByContentId(contentId)).thenReturn(Arrays.asList(testFileInfo));

        List<FileInfo> files = fileInfoService.getFilesByContentId(contentId);

        assertNotNull(files);
        assertEquals(1, files.size());
        assertEquals(testFileInfo.getOriginalName(), files.get(0).getOriginalName());
    }

    @Test
    void getFileById_shouldReturnFile() {
        when(fileInfoRepository.findById(1L)).thenReturn(Optional.of(testFileInfo));

        FileInfo file = fileInfoService.getFileById(1L);

        assertNotNull(file);
        assertEquals(testFileInfo.getOriginalName(), file.getOriginalName());
    }

    @Test
    void deleteFile_shouldDeleteFile() {
        when(fileInfoRepository.findById(1L)).thenReturn(Optional.of(testFileInfo));
        doNothing().when(fileInfoRepository).delete(any(FileInfo.class));

        fileInfoService.deleteFile(1L);

        verify(fileInfoRepository, times(1)).delete(any(FileInfo.class));
    }
} 