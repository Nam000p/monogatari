package com.monogatari.app.repository;

import com.monogatari.app.entity.Subscription;
import com.monogatari.app.enums.PlanType;
import com.monogatari.app.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserId(Long userId);

    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);

    boolean existsByUserIdAndStatus(Long userId, SubscriptionStatus status);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.planType = :planType AND s.status = :status")
    long countByPlanTypeAndStatus(@Param("planType") PlanType planType, @Param("status") SubscriptionStatus status);

    Optional<Subscription> findFirstByUserIdOrderByCurrentPeriodEndDesc(Long userId);
}