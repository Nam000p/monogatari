package com.monogatari.app.repository;

import com.monogatari.app.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByChapterIdOrderByCreatedAtDesc(Long chapterId);
}