package com.example.approval.controller;

import com.example.approval.model.FileInfo;
import com.example.approval.service.FileInfoService;
import com.example.approval.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileInfoService fileInfoService;

    @MockBean
    private LogService logService;

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
        doNothing().when(logService).recordLog(anyString(), anyString(), anyString(), anyString(), anyLong(), any(), anyString());

        mockMvc.perform(multipart("/api/files/upload")
                .file(testFile)
                .param("contentId", "1")
                .header("X-User-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.file.originalName").value("测试文件.docx"));
    }

    @Test
    void getFilesByContentId_shouldReturnFiles() throws Exception {
        when(fileInfoService.getFilesByContentId(anyLong())).thenReturn(Arrays.asList(testFileInfo));

        mockMvc.perform(get("/api/files/content/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].originalName").value("测试文件.docx"));
    }

    @Test
    void deleteFile_shouldReturnSuccess() throws Exception {
        when(fileInfoService.getFileById(anyLong())).thenReturn(testFileInfo);
        doNothing().when(fileInfoService).deleteFile(anyLong());
        doNothing().when(logService).recordLog(anyString(), anyString(), anyString(), anyString(), anyLong(), any(), anyString());

        mockMvc.perform(delete("/api/files/1")
                .header("X-User-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
} 