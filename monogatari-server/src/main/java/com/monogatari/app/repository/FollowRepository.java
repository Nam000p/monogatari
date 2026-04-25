package com.monogatari.app.repository;

import com.monogatari.app.entity.Follow;
import com.monogatari.app.entity.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    List<Follow> findByUserId(Long userId);
    
    List<Follow> findByStoryId(Long storyId);
    
    boolean existsByUserIdAndStoryId(Long userId, Long storyId);
    
    long countByStoryId(Long storyId);
}