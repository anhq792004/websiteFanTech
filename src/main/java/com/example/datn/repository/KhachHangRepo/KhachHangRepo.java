package com.example.datn.repository.KhachHangRepo;

import com.example.datn.entity.KhachHang;
import com.example.datn.entity.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface KhachHangRepo extends JpaRepository<KhachHang, Long> {
    @Query("SELECT kh FROM KhachHang kh " +
            "WHERE (LOWER(kh.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(kh.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
//            "OR LOWER(kh.taiKhoan.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(kh.soDienThoai) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:trangThai IS NULL OR kh.trangThai = :trangThai) ")
    Page<KhachHang> searchKhachHang(String keyword, Boolean trangThai, Pageable pageable);

    @Query("SELECT kh FROM KhachHang kh " +
            "WHERE (LOWER(kh.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(kh.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
//            "OR LOWER(kh.taiKhoan.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(kh.soDienThoai) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<KhachHang> searchKhachHangKhongCoTrangThai(String keyword, Pageable pageable);

    /**
     * Tìm khách hàng theo tài khoản
     */
    KhachHang findByTaiKhoan(TaiKhoan taiKhoan);

    /**
     * Kiểm tra khách hàng có tồn tại theo tài khoản không
     */
    boolean existsByTaiKhoan(TaiKhoan taiKhoan);
}
