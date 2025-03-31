package com.example.approval.controller;

import com.example.approval.model.Permission;
import com.example.approval.security.RequirePermission;
import com.example.approval.service.FileStorageService;
import com.example.approval.service.FileInfoService;
import com.example.approval.service.LogService;
import com.example.approval.model.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件上传和下载API
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private FileInfoService fileInfoService;
    
    @Autowired
    private LogService logService;

    /**
     * 上传单个文件
     */
    @PostMapping("/upload")
    @RequirePermission(Permission.CONTENT_CREATE)
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        
        String fileName = fileStorageService.storeFile(file);
        
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/download/")
                .path(fileName)
                .toUriString();
        
        // 记录操作日志
        logService.recordLog(
            "UPLOAD", 
            "File", 
            fileName, 
            "上传文件：" + file.getOriginalFilename(), 
            userId, 
            null,
            request.getRemoteAddr()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("fileName", fileName);
        response.put("originalName", file.getOriginalFilename());
        response.put("fileDownloadUri", fileDownloadUri);
        response.put("fileType", file.getContentType());
        response.put("size", file.getSize());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 上传多个文件
     */
    @PostMapping("/upload-multiple")
    @RequirePermission(Permission.CONTENT_CREATE)
    public ResponseEntity<?> uploadMultipleFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        
        List<Map<String, Object>> responses = Arrays.stream(files)
                .map(file -> {
                    try {
                        String fileName = fileStorageService.storeFile(file);
                        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/api/files/download/")
                                .path(fileName)
                                .toUriString();
                        
                        // 记录操作日志
                        logService.recordLog(
                            "UPLOAD", 
                            "File", 
                            fileName, 
                            "批量上传文件：" + file.getOriginalFilename(), 
                            userId, 
                            null,
                            request.getRemoteAddr()
                        );
                        
                        Map<String, Object> fileData = new HashMap<>();
                        fileData.put("fileName", fileName);
                        fileData.put("originalName", file.getOriginalFilename());
                        fileData.put("fileDownloadUri", fileDownloadUri);
                        fileData.put("fileType", file.getContentType());
                        fileData.put("size", file.getSize());
                        
                        return fileData;
                    } catch (Exception e) {
                        Map<String, Object> errorData = new HashMap<>();
                        errorData.put("originalName", file.getOriginalFilename());
                        errorData.put("error", e.getMessage());
                        return errorData;
                    }
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 下载文件
     */
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileName, 
            HttpServletRequest request) {
        
        // 加载文件资源
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        
        // 尝试确定文件内容类型
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // 如果类型无法确定，则默认为二进制流
        }
        
        // 默认内容类型
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    /**
     * 删除文件
     */
    @DeleteMapping("/delete/{fileName:.+}")
    @RequirePermission(Permission.CONTENT_DELETE)
    public ResponseEntity<?> deleteFile(
            @PathVariable String fileName,
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        
        boolean result = fileStorageService.deleteFile(fileName);
        
        // 记录操作日志
        logService.recordLog(
            "DELETE", 
            "File", 
            fileName, 
            "删除文件：" + fileName, 
            userId, 
            null,
            request.getRemoteAddr()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("fileName", fileName);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/content/{contentId}")
    public List<FileInfo> getFilesByContentId(@PathVariable Long contentId) {
        return fileInfoService.getFilesByContentId(contentId);
    }
} 