package com.example.datn.service;

import com.example.datn.dto.response.DoanhThuNgayResponse;
import com.example.datn.dto.response.DoanhThuThangResponse;
import com.example.datn.dto.response.LowStockProductResponse;
import com.example.datn.dto.response.TopSanPhamBanChayResponse;
import com.example.datn.dto.response.ThongKeTongQuanResponse;

import java.time.LocalDate;
import java.util.List;

public interface ThongKeService {
    // Top sản phẩm bán chạy
    List<TopSanPhamBanChayResponse> topSanPhamBanChayNgay(LocalDate date);
    List<TopSanPhamBanChayResponse> topSanPhamBanChayThang(int year, int month);
    List<TopSanPhamBanChayResponse> topSanPhamBanChayKhoang(LocalDate from, LocalDate to);

    // Thống kê tổng quan theo khoảng
    ThongKeTongQuanResponse thongKeTongQuanTrongKhoang(LocalDate from, LocalDate to);

    // Doanh thu
    List<DoanhThuNgayResponse> doanhThuTungNgayTrongThang(int year, int month);
    List<DoanhThuThangResponse> doanhThuTungThangTrongNam(int year);
    List<DoanhThuNgayResponse> doanhThuTheoKhoangNgay(LocalDate from, LocalDate to);
    long tongDoanhThuToanHeThong();
    long doanhThuHomNay();
    long doanhThuTuanNay();
    long doanhThuThangNay();

    // Đếm đơn hàng theo trạng thái
    long countDonHangDangGiaoHang();
    long countDonHangDaHuy();
    long countDonHangHoanThanh();

    // Thống kê khác
    long countPhieuGiamGiaDangHoatDong();
    long countSanPhamBanChay();
    long countSanPhamHetHang();
    long countTongDonHang();

    // Danh sách sắp hết hàng
    List<LowStockProductResponse> danhSachSapHetHang(int threshold, int limit);
} 