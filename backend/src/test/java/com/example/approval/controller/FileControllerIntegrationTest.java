package com.example.approval.controller;

import com.example.approval.config.TestConfig;
import com.example.approval.model.FileInfo;
import com.example.approval.service.FileInfoService;
import com.example.approval.service.LogService;
import com.example.approval.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class FileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileInfoService fileInfoService;

    @MockBean
    private LogService logService;

    @MockBean
    private FileStorageService fileStorageService;

    private FileInfo testFileInfo;
    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() {
        testFileInfo = new FileInfo();
        testFileInfo.setId(1L);
        testFileInfo.setFileName("test_file.docx");
        testFileInfo.setOriginalName("测试文件.docx");
        testFileInfo.setFileType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        testFileInfo.setFileSize(25600L);
        testFileInfo.setContentId(1L);
        testFileInfo.setUploadBy(1L);

        testFile = new MockMultipartFile(
                "file",
                "测试文件.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "测试内容".getBytes()
        );
    }

    @Test
    void uploadFile_shouldReturnFileInfo() throws Exception {
        when(fileInfoService.uploadFile(any(), anyLong(), anyLong())).thenReturn(testFileInfo);
        when(logService.recordLog(anyString(), anyString(), anyString(), anyString(), anyLong(), any(), anyString())).thenReturn(null);

        mockMvc.perform(multipart("/api/files/upload")
                .file(testFile)
                .param("contentId", "1")
                .header("X-User-ID", "1")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalName").value("测试文件.docx"))
                .andExpect(jsonPath("$.fileName").exists())
                .andExpect(jsonPath("$.fileDownloadUri").exists())
                .andExpect(jsonPath("$.fileType").exists());
    }

    @Test
    void getFilesByContentId_shouldReturnFiles() throws Exception {
        List<FileInfo> files = Arrays.asList(testFileInfo);
        when(fileInfoService.getFilesByContentId(anyLong())).thenReturn(files);
        
        mockMvc.perform(get("/api/files/content/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].originalName").value("测试文件.docx"));
    }

    @Test
    void deleteFile_shouldReturnSuccess() throws Exception {
        when(fileStorageService.deleteFile(anyString())).thenReturn(true);
        when(logService.recordLog(anyString(), anyString(), anyString(), anyString(), anyLong(), any(), anyString())).thenReturn(null);

        mockMvc.perform(delete("/api/files/delete/{fileName}", "test_file.docx")
                .header("X-User-ID", "1")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
} 