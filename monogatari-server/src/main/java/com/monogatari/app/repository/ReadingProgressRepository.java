package com.monogatari.app.repository;

import com.monogatari.app.entity.ReadingProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Long> {
    Optional<ReadingProgress> findByUserIdAndStoryId(Long userId, Long storyId);
    
    List<ReadingProgress> findAllByUserIdOrderByLastReadAtDesc(Long userId);
}