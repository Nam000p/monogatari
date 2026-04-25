package com.monogatari.app.repository;

import com.monogatari.app.entity.Story;
import com.monogatari.app.enums.StoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    Page<Story> findByAgeLimitLessThanEqual(Integer ageLimit, Pageable pageable);

    Page<Story> findByTypeAndAgeLimitLessThanEqual(StoryType type, Integer ageLimit, Pageable pageable);

    Page<Story> findByAuthorIdAndAgeLimitLessThanEqual(Long authorId, Integer ageLimit, Pageable pageable);

    Page<Story> findByTitleContainingIgnoreCaseAndAgeLimitLessThanEqual(String title, Integer ageLimit, Pageable pageable);

    @Query(value = "SELECT s FROM Story s LEFT JOIN FETCH s.genre WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%')) AND s.ageLimit <= :ageLimit",
           countQuery = "SELECT count(s) FROM Story s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%')) AND s.ageLimit <= :ageLimit")
    Page<Story> findByTitleWithGenresAndAgeLimit(@Param("title") String title, @Param("ageLimit") Integer ageLimit, Pageable pageable);

    @Query(value = "SELECT DISTINCT s FROM Story s LEFT JOIN FETCH s.genre LEFT JOIN FETCH s.author WHERE s.ageLimit <= :ageLimit",
           countQuery = "SELECT count(s) FROM Story s WHERE s.ageLimit <= :ageLimit")
    Page<Story> findAllWithGenresAndAgeLimit(@Param("ageLimit") Integer ageLimit, Pageable pageable);

    @Query(value = "SELECT s FROM Story s JOIN FETCH s.genre g WHERE g.id = :genreId AND s.ageLimit <= :ageLimit",
           countQuery = "SELECT count(s) FROM Story s JOIN s.genre g WHERE g.id = :genreId AND s.ageLimit <= :ageLimit")
    Page<Story> findByGenreIdAndAgeLimit(@Param("genreId") Long genreId, @Param("ageLimit") Integer ageLimit, Pageable pageable);

    Page<Story> findAllByAgeLimitLessThanEqualOrderByUpdatedAtDesc(Integer ageLimit, Pageable pageable);

    Page<Story> findAllByAgeLimitLessThanEqualOrderByViewCountDesc(Integer ageLimit, Pageable pageable);

    Page<Story> findAllByAgeLimitLessThanEqualOrderByAverageRatingDesc(Integer ageLimit, Pageable pageable);
}