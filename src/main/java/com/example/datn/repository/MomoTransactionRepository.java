package com.example.datn.repository;

import com.example.datn.entity.MomoTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MomoTransactionRepository extends JpaRepository<MomoTransaction, Long> {
    Optional<MomoTransaction> findByOrderId(String orderId);
    Optional<MomoTransaction> findByHoaDonId(Long hoaDonId);


} 