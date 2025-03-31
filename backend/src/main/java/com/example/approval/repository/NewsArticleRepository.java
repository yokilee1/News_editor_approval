package com.example.approval.repository;

import com.example.approval.model.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
    List<NewsArticle> findByAuthorId(Long authorId);
    List<NewsArticle> findByStatus(NewsArticle.Status status);
} 