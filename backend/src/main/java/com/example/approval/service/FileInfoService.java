package com.example.approval.service;

import com.example.approval.model.FileInfo;
import com.example.approval.repository.FileInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FileInfoService {
    
    @Autowired
    private FileInfoRepository fileInfoRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    /**
     * 保存文件信息
     */
    public FileInfo saveFileInfo(MultipartFile file, String fileName, Long userId, Long contentId) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(fileName);
        fileInfo.setOriginalName(file.getOriginalFilename());
        fileInfo.setFileType(file.getContentType());
        fileInfo.setFileSize(file.getSize());
        fileInfo.setUploadBy(userId);
        fileInfo.setContentId(contentId);
        
        return fileInfoRepository.save(fileInfo);
    }
    
    /**
     * 获取内容关联的所有文件
     */
    public List<FileInfo> getFilesByContent(Long contentId) {
        return fileInfoRepository.findByContentId(contentId);
    }
    
    /**
     * 获取用户上传的所有文件
     */
    public List<FileInfo> getFilesByUser(Long userId) {
        return fileInfoRepository.findByUploadBy(userId);
    }
    
    /**
     * 删除文件及其信息
     */
    public boolean deleteFileWithInfo(String fileName) {
        FileInfo fileInfo = fileInfoRepository.findByFileName(fileName);
        if (fileInfo != null) {
            fileInfoRepository.delete(fileInfo);
        }
        return fileStorageService.deleteFile(fileName);
    }
    
    /**
     * 上传文件并保存文件信息
     */
    public FileInfo uploadFile(MultipartFile file, Long contentId, Long userId) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
        
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(fileName);
        fileInfo.setOriginalName(originalFilename);
        fileInfo.setFileType(file.getContentType());
        fileInfo.setFileSize(file.getSize());
        fileInfo.setContentId(contentId);
        fileInfo.setUploadBy(userId);
        fileInfo.setCreatedAt(LocalDateTime.now());
        
        return fileInfoRepository.save(fileInfo);
    }
    
    /**
     * 根据内容ID获取所有相关文件
     */
    public List<FileInfo> getFilesByContentId(Long contentId) {
        return fileInfoRepository.findByContentId(contentId);
    }
    
    /**
     * 根据ID获取文件信息
     */
    public FileInfo getFileById(Long id) {
        return fileInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
    }
    
    /**
     * 删除文件
     */
    public void deleteFile(Long id) {
        FileInfo fileInfo = getFileById(id);
        fileInfoRepository.delete(fileInfo);
    }
} 