package com.monogatari.app.repository;

import com.monogatari.app.entity.Transaction;
import com.monogatari.app.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Transaction> findByStripeInvoiceId(String stripeInvoiceId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.status = :status")
    BigDecimal calculateTotalRevenue(@Param("status") TransactionStatus status);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.status = :status AND t.createdAt >= :startDate")
    BigDecimal calculateRevenueSince(@Param("status") TransactionStatus status, @Param("startDate") Instant startDate);

    @Query("SELECT t FROM Transaction t JOIN FETCH t.user ORDER BY t.createdAt DESC")
    List<Transaction> findTop10WithUserByOrderByCreatedAtDesc();
}