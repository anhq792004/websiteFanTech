package com.example.datn.repository.SanPhamRepo;

import com.example.datn.entity.SanPham.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SanPhamRepo extends JpaRepository<SanPham,Long> {
    Page<SanPham> findByTenContaining(String ten, Pageable pageable);

    // Lọc theo kiểu quạt
    Page<SanPham> findByKieuQuatId(Long kieuQuatId, Pageable pageable);

    // Lọc theo trạng thái
    Page<SanPham> findByTrangThai(Boolean trangThai, Pageable pageable);

    // Kết hợp tìm kiếm và lọc
    Page<SanPham> findByTenContainingAndKieuQuatId(String ten, Long kieuQuatId, Pageable pageable);
    Page<SanPham> findByTenContainingAndTrangThai(String ten, Boolean trangThai, Pageable pageable);
    Page<SanPham> findByKieuQuatIdAndTrangThai(Long kieuQuatId, Boolean trangThai, Pageable pageable);
    Page<SanPham> findByTenContainingAndKieuQuatIdAndTrangThai(String ten, Long kieuQuatId, Boolean trangThai, Pageable pageable);
    //khoi
    @Query("SELECT s FROM SanPham s WHERE s.trangThai = true")
    List<SanPham> findByTrangThaiTrue();
    
    @Query("SELECT s.ma FROM SanPham s WHERE s.ma LIKE 'SP%' ORDER BY s.ma DESC")
    List<String> findLatestProductCode();
}
