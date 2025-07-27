package com.example.datn.repository;

import com.example.datn.dto.response.TopSanPhamBanChayResponse;
import com.example.datn.dto.response.ThongKeTongQuanResponse;
import com.example.datn.dto.response.DoanhThuNgayResponse;
import com.example.datn.dto.response.DoanhThuThangResponse;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ThongKeRepo {
    // Top sản phẩm bán chạy
    List<TopSanPhamBanChayResponse> topSanPhamBanChayTheoNgay(LocalDate date);
    List<TopSanPhamBanChayResponse> topSanPhamBanChayTheoThang(int year, int month);
    List<TopSanPhamBanChayResponse> topSanPhamBanChayTrongKhoang(LocalDate from, LocalDate to);

    // Thống kê tổng quan
    ThongKeTongQuanResponse thongKeTongQuanTrongKhoang(LocalDate from, LocalDate to);

    // Doanh thu từng ngày trong tháng
    List<DoanhThuNgayResponse> doanhThuTungNgayTrongThang(int year, int month);
    // Doanh thu từng tháng trong năm
    List<DoanhThuThangResponse> doanhThuTungThangTrongNam(int year);
    // Doanh thu theo khoảng ngày
    List<DoanhThuNgayResponse> doanhThuTheoKhoangNgay(LocalDate from, LocalDate to);
    // Tổng doanh thu, doanh thu hôm nay, tuần này, tháng này
    long tongDoanhThuToanHeThong();
    long doanhThuHomNay();
    long doanhThuTuanNay();
    long doanhThuThangNay();
} 