package com.example.datn.repository.impl;

import com.example.datn.dto.response.DoanhThuNgayResponse;
import com.example.datn.dto.response.DoanhThuThangResponse;
import com.example.datn.dto.response.TopSanPhamBanChayResponse;
import com.example.datn.dto.response.ThongKeTongQuanResponse;
import com.example.datn.repository.ThongKeRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class ThongKeRepoImpl implements ThongKeRepo {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<TopSanPhamBanChayResponse> topSanPhamBanChayTheoNgay(LocalDate date) {
        String sql = """
            SELECT new com.example.datn.dto.response.TopSanPhamBanChayResponse(
                sp.id, sp.ma, sp.ten, SUM(hdct.soLuong), SUM(hdct.thanhTien)
            )
            FROM HoaDonChiTiet hdct
            JOIN hdct.sanPhamChiTiet spct
            JOIN spct.sanPham sp
            JOIN hdct.hoaDon hd
            WHERE hd.trangThai = 3 AND CAST(hd.ngayTao AS DATE) = :date
            GROUP BY sp.id, sp.ma, sp.ten
            ORDER BY SUM(hdct.soLuong) DESC
        """;
        Query query = entityManager.createQuery(sql, TopSanPhamBanChayResponse.class);
        query.setParameter("date", date);
        query.setMaxResults(5);
        return query.getResultList();
    }

    @Override
    public List<TopSanPhamBanChayResponse> topSanPhamBanChayTheoThang(int year, int month) {
        String sql = """
            SELECT new com.example.datn.dto.response.TopSanPhamBanChayResponse(
                sp.id, sp.ma, sp.ten, SUM(hdct.soLuong), SUM(hdct.thanhTien)
            )
            FROM HoaDonChiTiet hdct
            JOIN hdct.sanPhamChiTiet spct
            JOIN spct.sanPham sp
            JOIN hdct.hoaDon hd
            WHERE hd.trangThai = 3 AND YEAR(hd.ngayTao) = :year AND MONTH(hd.ngayTao) = :month
            GROUP BY sp.id, sp.ma, sp.ten
            ORDER BY SUM(hdct.soLuong) DESC
        """;
        Query query = entityManager.createQuery(sql, TopSanPhamBanChayResponse.class);
        query.setParameter("year", year);
        query.setParameter("month", month);
        query.setMaxResults(5);
        return query.getResultList();
    }

    @Override
    public List<TopSanPhamBanChayResponse> topSanPhamBanChayTrongKhoang(LocalDate from, LocalDate to) {
        String sql = """
            SELECT new com.example.datn.dto.response.TopSanPhamBanChayResponse(
                sp.id, sp.ma, sp.ten, SUM(hdct.soLuong), SUM(hdct.thanhTien)
            )
            FROM HoaDonChiTiet hdct
            JOIN hdct.sanPhamChiTiet spct
            JOIN spct.sanPham sp
            JOIN hdct.hoaDon hd
            WHERE hd.trangThai = 3 AND CAST(hd.ngayTao AS DATE) >= :from AND CAST(hd.ngayTao AS DATE) <= :to
            GROUP BY sp.id, sp.ma, sp.ten
            ORDER BY SUM(hdct.soLuong) DESC
        """;
        Query query = entityManager.createQuery(sql, TopSanPhamBanChayResponse.class);
        query.setParameter("from", from);
        query.setParameter("to", to);
        query.setMaxResults(5);
        return query.getResultList();
    }

    @Override
    public ThongKeTongQuanResponse thongKeTongQuanTrongKhoang(LocalDate from, LocalDate to) {
        String sql = """
            SELECT COUNT(DISTINCT hd.id), COALESCE(SUM(hd.tongTienSauGiamGia),0), COALESCE(SUM(hdct.soLuong),0), COUNT(DISTINCT hd.khachHang.id)
            FROM HoaDonChiTiet hdct
            JOIN hdct.hoaDon hd
            WHERE hd.trangThai = 3 AND CAST(hd.ngayTao AS DATE) >= :from AND CAST(hd.ngayTao AS DATE) <= :to
        """;
        Query query = entityManager.createQuery(sql);
        query.setParameter("from", from);
        query.setParameter("to", to);
        Object[] result = (Object[]) query.getSingleResult();
        return new ThongKeTongQuanResponse(
            ((Number) result[0]).longValue(),
            ((Number) result[1]).longValue(),
            ((Number) result[2]).longValue(),
            ((Number) result[3]).longValue()
        );
    }

    @Override
    public List<DoanhThuNgayResponse> doanhThuTungNgayTrongThang(int year, int month) {
        String sql = """
            SELECT DAY(hd.ngayTao), COALESCE(SUM(hd.tongTienSauGiamGia),0)
            FROM HoaDon hd
            WHERE hd.trangThai = 3 AND YEAR(hd.ngayTao) = :year AND MONTH(hd.ngayTao) = :month
            GROUP BY DAY(hd.ngayTao)
            ORDER BY DAY(hd.ngayTao)
        """;
        Query query = entityManager.createQuery(sql);
        query.setParameter("year", year);
        query.setParameter("month", month);
        List<Object[]> result = query.getResultList();
        return result.stream().map(r -> new DoanhThuNgayResponse((int) r[0], ((Number) r[1]).longValue())).toList();
    }

    @Override
    public List<DoanhThuThangResponse> doanhThuTungThangTrongNam(int year) {
        String sql = """
            SELECT MONTH(hd.ngayTao), COALESCE(SUM(hd.tongTienSauGiamGia),0)
            FROM HoaDon hd
            WHERE hd.trangThai = 3 AND YEAR(hd.ngayTao) = :year
            GROUP BY MONTH(hd.ngayTao)
            ORDER BY MONTH(hd.ngayTao)
        """;
        Query query = entityManager.createQuery(sql);
        query.setParameter("year", year);
        List<Object[]> result = query.getResultList();
        return result.stream().map(r -> new DoanhThuThangResponse((int) r[0], ((Number) r[1]).longValue())).toList();
    }

    @Override
    public List<DoanhThuNgayResponse> doanhThuTheoKhoangNgay(LocalDate from, LocalDate to) {
        String sql = """
            SELECT DAY(hd.ngayTao), COALESCE(SUM(hd.tongTienSauGiamGia),0)
            FROM HoaDon hd
            WHERE hd.trangThai = 3 AND CAST(hd.ngayTao AS DATE) >= :from AND CAST(hd.ngayTao AS DATE) <= :to
            GROUP BY DAY(hd.ngayTao)
            ORDER BY DAY(hd.ngayTao)
        """;
        Query query = entityManager.createQuery(sql);
        query.setParameter("from", from);
        query.setParameter("to", to);
        List<Object[]> result = query.getResultList();
        return result.stream().map(r -> new DoanhThuNgayResponse((int) r[0], ((Number) r[1]).longValue())).toList();
    }

    @Override
    public long tongDoanhThuToanHeThong() {
        String sql = "SELECT COALESCE(SUM(hd.tongTienSauGiamGia),0) FROM HoaDon hd WHERE hd.trangThai = 3";
        Query query = entityManager.createQuery(sql);
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public long doanhThuHomNay() {
        String sql = "SELECT COALESCE(SUM(hd.tongTienSauGiamGia),0) FROM HoaDon hd WHERE hd.trangThai = 3 AND CAST(hd.ngayTao AS DATE) = CURRENT_DATE";
        Query query = entityManager.createQuery(sql);
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public long doanhThuTuanNay() {
        String sql = "SELECT COALESCE(SUM(hd.tongTienSauGiamGia),0) FROM HoaDon hd WHERE hd.trangThai = 3 AND YEAR(hd.ngayTao) = YEAR(CURRENT_DATE) AND DATEPART(week, hd.ngayTao) = DATEPART(week, CURRENT_DATE)";
        Query query = entityManager.createQuery(sql);
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public long doanhThuThangNay() {
        String sql = "SELECT COALESCE(SUM(hd.tongTienSauGiamGia),0) FROM HoaDon hd WHERE hd.trangThai = 3 AND YEAR(hd.ngayTao) = YEAR(CURRENT_DATE) AND MONTH(hd.ngayTao) = MONTH(CURRENT_DATE)";
        Query query = entityManager.createQuery(sql);
        return ((Number) query.getSingleResult()).longValue();
    }
} 