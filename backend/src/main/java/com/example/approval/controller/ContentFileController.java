package com.example.approval.controller;

import com.example.approval.model.FileInfo;
import com.example.approval.model.Permission;
import com.example.approval.security.RequirePermission;
import com.example.approval.service.FileInfoService;
import com.example.approval.service.FileStorageService;
import com.example.approval.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内容附件管理接口
 */
@RestController
@RequestMapping("/api/content")
public class ContentFileController {
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private FileInfoService fileInfoService;
    
    @Autowired
    private LogService logService;
    
    /**
     * 为内容上传附件
     */
    @PostMapping("/{contentId}/attachments")
    @RequirePermission(Permission.CONTENT_EDIT)
    public ResponseEntity<?> uploadContentAttachment(
            @PathVariable Long contentId,
            @RequestParam("file") MultipartFile file,
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        
        String fileName = fileStorageService.storeFile(file);
        
        FileInfo fileInfo = fileInfoService.saveFileInfo(file, fileName, userId, contentId);
        
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/download/")
                .path(fileName)
                .toUriString();
        
        // 记录操作日志
        logService.recordLog(
            "UPLOAD", 
            "ContentAttachment", 
            contentId + ":" + fileName, 
            "上传内容附件：" + file.getOriginalFilename(), 
            userId, 
            null,
            request.getRemoteAddr()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", fileInfo.getId());
        response.put("fileName", fileName);
        response.put("originalName", file.getOriginalFilename());
        response.put("fileDownloadUri", fileDownloadUri);
        response.put("fileType", file.getContentType());
        response.put("size", file.getSize());
        response.put("contentId", contentId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取内容的所有附件
     */
    @GetMapping("/{contentId}/attachments")
    @RequirePermission(Permission.CONTENT_VIEW)
    public ResponseEntity<?> getContentAttachments(@PathVariable Long contentId) {
        List<FileInfo> files = fileInfoService.getFilesByContent(contentId);
        return ResponseEntity.ok(files);
    }
    
    /**
     * 删除内容附件
     */
    @DeleteMapping("/{contentId}/attachments/{fileName:.+}")
    @RequirePermission(Permission.CONTENT_EDIT)
    public ResponseEntity<?> deleteContentAttachment(
            @PathVariable Long contentId,
            @PathVariable String fileName,
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        
        boolean result = fileInfoService.deleteFileWithInfo(fileName);
        
        // 记录操作日志
        logService.recordLog(
            "DELETE", 
            "ContentAttachment", 
            contentId + ":" + fileName, 
            "删除内容附件：" + fileName, 
            userId, 
            null,
            request.getRemoteAddr()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("fileName", fileName);
        response.put("contentId", contentId);
        
        return ResponseEntity.ok(response);
    }
} 