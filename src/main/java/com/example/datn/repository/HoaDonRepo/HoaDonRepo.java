package com.example.datn.repository.HoaDonRepo;

import com.example.datn.dto.response.LichSuThanhToanResponse;
import com.example.datn.entity.HoaDon.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface HoaDonRepo extends JpaRepository<HoaDon,Long> {
    @Query("SELECT hd from HoaDon hd order by hd.ngayTao desc ")
    Page<HoaDon> findHoaDonAndSortDay(Pageable pageable);

    @Query("SELECT hd FROM HoaDon hd " +
            "LEFT JOIN hd.khachHang kh " +
            "LEFT JOIN hd.nhanVien nv " +
            "WHERE (LOWER(COALESCE(kh.ten, '')) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(COALESCE(kh.ma, '')) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(COALESCE(kh.soDienThoai, '')) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(COALESCE(nv.ma, '')) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(COALESCE(hd.ma, '')) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:loaiHoaDon IS NULL OR hd.loaiHoaDon = :loaiHoaDon) " +
            "AND (:tuNgay IS NULL OR hd.ngayTao >= :tuNgay) " +  // So sánh trực tiếp với LocalDate
            "AND (:denNgay IS NULL OR hd.ngayTao <= :denNgay) " + // So sánh trực tiếp với LocalDate
            "AND (:trangThai IS NULL OR hd.trangThai = :trangThai)")
    Page<HoaDon> searchHoaDon(String query, Boolean loaiHoaDon, LocalDateTime tuNgay, LocalDateTime denNgay, Integer trangThai, Pageable pageable);


    @Query("SELECT hd FROM HoaDon hd " +
            "LEFT JOIN hd.khachHang kh " +
            "LEFT JOIN hd.nhanVien nv " +
            "WHERE (LOWER(COALESCE(kh.ten, '')) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(COALESCE(kh.ma, '')) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(COALESCE(kh.soDienThoai, '')) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(COALESCE(nv.ma, '')) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(COALESCE(hd.ma, '')) LIKE LOWER(CONCAT('%', :query, '%')))" +
            "AND (:tuNgay IS NULL OR hd.ngayTao >= :tuNgay) " + // So sánh trực tiếp với LocalDate
            "AND (:denNgay IS NULL OR hd.ngayTao <= :denNgay) " + // So sánh trực tiếp với LocalDate
            "AND (:loaiHoaDon IS NULL OR hd.loaiHoaDon = :loaiHoaDon)" +
            "order by hd.ngayTao desc, hd.trangThai asc ")
    Page<HoaDon> searchHoaDonKhongtrangThai(String query, Boolean loaiHoaDon, LocalDateTime tuNgay, LocalDateTime denNgay, Pageable pageable);


    @Query("SELECT h.khachHang.id FROM HoaDon h WHERE h.id = :hoaDonId")
    Long findKhachHangIdByHoaDonId(@Param("hoaDonId") Long hoaDonId);

    @Query("SELECT new com.example.datn.dto.response.LichSuThanhToanResponse(" +
            "hd.tongTienSauGiamGia, hd.ngayTao, COALESCE(hd.loaiHoaDon, TRUE), " +
            "hd.phuongThucThanhToan, " +
            "hd.nguoiTao, " +
            "hd.trangThai) " +
            "FROM HoaDon hd " +
            "where hd.id = :hoaDonId")
    LichSuThanhToanResponse findThanhToanHoaDonId(@Param("hoaDonId") long id);

    List<HoaDon> findByKhachHang_IdAndTrangThaiNot(Long idKH, Integer trangThai);


}
