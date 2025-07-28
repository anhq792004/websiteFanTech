package com.example.datn.repository;

import com.example.datn.entity.KhachHang;
import com.example.datn.entity.PhieuGiamGia;
import com.example.datn.entity.PhieuGiamGiaKhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhieuGiamGiaKhachHangRepo extends JpaRepository<PhieuGiamGiaKhachHang,Long> {

    // Tìm tất cả khách hàng có phiếu giảm giá cụ thể
    List<PhieuGiamGiaKhachHang> findByPhieuGiamGiaId(Long phieuGiamGiaId);

    // Kiểm tra khách hàng đã có phiếu giảm giá này chưa
    boolean existsByPhieuGiamGiaIdAndKhachHangId(Long phieuGiamGiaId, Long khachHangId);

    // Đếm số lượng đã sử dụng
    @Query("SELECT COUNT(p) FROM PhieuGiamGiaKhachHang p WHERE p.phieuGiamGia.id = :phieuGiamGiaId AND p.daSuDung = true")
    Long countUsedByPhieuGiamGiaId(@Param("phieuGiamGiaId") Long phieuGiamGiaId);

    /**
     * Tìm phiếu giảm giá của khách hàng theo trạng thái và tình trạng sử dụng
     */
    List<PhieuGiamGiaKhachHang> findByKhachHangAndTrangThaiAndDaSuDung(
            KhachHang khachHang, Boolean trangThai, Boolean daSuDung);

    // Tìm một phiếu giảm giá cụ thể cho khách hàng
    Optional<PhieuGiamGiaKhachHang> findByPhieuGiamGiaAndKhachHang(PhieuGiamGia phieuGiamGia, KhachHang khachHang);

    List<PhieuGiamGiaKhachHang> findByKhachHang(KhachHang khachHang);

    // Tìm phiếu giảm giá theo khách hàng và trạng thái hoạt động
    List<PhieuGiamGiaKhachHang> findByKhachHangIdAndTrangThaiTrue(Long khachHangId);

}
