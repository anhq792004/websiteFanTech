package com.example.datn.service;

import com.example.datn.dto.response.DoanhThuNgayResponse;
import com.example.datn.dto.response.DoanhThuThangResponse;
import com.example.datn.dto.response.TopSanPhamBanChayResponse;
import com.example.datn.dto.response.ThongKeTongQuanResponse;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface ThongKeService {
    List<TopSanPhamBanChayResponse> topSanPhamBanChayTheoNgay(LocalDate date);
    List<TopSanPhamBanChayResponse> topSanPhamBanChayTheoThang(YearMonth month);
    List<TopSanPhamBanChayResponse> topSanPhamBanChayTrongKhoang(LocalDate from, LocalDate to);
    ThongKeTongQuanResponse thongKeTongQuanTrongKhoang(LocalDate from, LocalDate to);
    List<DoanhThuNgayResponse> doanhThuTungNgayTrongThang(int year, int month);
    List<DoanhThuThangResponse> doanhThuTungThangTrongNam(int year);
    List<DoanhThuNgayResponse> doanhThuTheoKhoangNgay(LocalDate from, LocalDate to);
    long tongDoanhThuToanHeThong();
    long doanhThuHomNay();
    long doanhThuTuanNay();
    long doanhThuThangNay();
} 