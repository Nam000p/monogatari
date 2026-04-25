package com.monogatari.app.repository;

import com.monogatari.app.entity.User;
import com.monogatari.app.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);

	@Query(value = "SELECT * FROM users ORDER BY created_at DESC", nativeQuery = true)
    Page<User> findAllIncludeDeleted(Pageable pageable);

	@Query(value = "SELECT * FROM users WHERE id = :id", nativeQuery = true)
    Optional<User> findByIdIncludeDeleted(@Param("id") Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByStripeCustomerId(String stripeCustomerId);

    void deleteAllByStatusAndCreatedAtBefore(UserStatus status, Instant dateTime);
}