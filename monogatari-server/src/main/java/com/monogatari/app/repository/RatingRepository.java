package com.monogatari.app.repository;

import com.monogatari.app.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByStoryId(Long storyId);
    
    Optional<Rating> findByUserIdAndStoryId(Long userId, Long storyId);
    
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.story.id = :storyId")
    Double calculateAverageRatingByStoryId(@Param("storyId") Long storyId);
}