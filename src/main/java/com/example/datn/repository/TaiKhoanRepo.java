package com.example.datn.repository;

import com.example.datn.entity.KhachHang;
import com.example.datn.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaiKhoanRepo extends JpaRepository<TaiKhoan,Long> {
    TaiKhoan findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT k FROM KhachHang k WHERE k.taiKhoan.id = :taiKhoanId")
    KhachHang findKhachHangByTaiKhoanId(Long taiKhoanId);
}
