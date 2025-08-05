package com.example.datn.repository.SanPhamRepo;

import com.example.datn.entity.SanPham.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SanPhamChiTietRepo extends JpaRepository<SanPhamChiTiet,Long> {
    //khoi
    @Query("SELECT spct FROM SanPhamChiTiet spct WHERE spct.trangThai = true")
    List<SanPhamChiTiet> findByTrangThaiTrue();
    
    @Query("SELECT MIN(spct.gia) FROM SanPhamChiTiet spct WHERE spct.trangThai = true")
    Double findMinPrice();
    
    @Query("SELECT MAX(spct.gia) FROM SanPhamChiTiet spct WHERE spct.trangThai = true")
    Double findMaxPrice();
}
