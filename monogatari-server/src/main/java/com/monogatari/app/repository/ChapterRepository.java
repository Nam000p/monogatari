package com.monogatari.app.repository;

import com.monogatari.app.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long>{
	List<Chapter> findByStoryIdOrderByChapterNumberAsc(Long storyId);
}