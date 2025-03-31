package com.example.approval.repository;

import com.example.approval.model.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
    List<FileInfo> findByContentId(Long contentId);
    List<FileInfo> findByUploadBy(Long userId);
    FileInfo findByFileName(String fileName);
} 