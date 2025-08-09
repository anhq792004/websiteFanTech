package com.example.datn.service.Implements;

import com.example.datn.dto.response.TopSanPhamBanChayResponse;
import com.example.datn.dto.response.ThongKeTongQuanResponse;
import com.example.datn.dto.response.DoanhThuNgayResponse;
import com.example.datn.dto.response.DoanhThuThangResponse;
import com.example.datn.dto.response.LowStockProductResponse;
import com.example.datn.repository.ThongKeRepo;
import com.example.datn.service.ThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ThongKeServiceImpl implements ThongKeService {
    private final ThongKeRepo thongKeRepo;

    // Top bán chạy
    @Override
    public List<TopSanPhamBanChayResponse> topSanPhamBanChayNgay(LocalDate date) {
        return thongKeRepo.topSanPhamBanChayTheoNgay(date);
    }

    @Override
    public List<TopSanPhamBanChayResponse> topSanPhamBanChayThang(int year, int month) {
        return thongKeRepo.topSanPhamBanChayTheoThang(year, month);
    }

    @Override
    public List<TopSanPhamBanChayResponse> topSanPhamBanChayKhoang(LocalDate from, LocalDate to) {
        return thongKeRepo.topSanPhamBanChayTrongKhoang(from, to);
    }

    // Tổng quan
    @Override
    public ThongKeTongQuanResponse thongKeTongQuanTrongKhoang(LocalDate from, LocalDate to) {
        return thongKeRepo.thongKeTongQuanTrongKhoang(from, to);
    }

    // Doanh thu
    @Override
    public List<DoanhThuNgayResponse> doanhThuTungNgayTrongThang(int year, int month) {
        return thongKeRepo.doanhThuTungNgayTrongThang(year, month);
    }

    @Override
    public List<DoanhThuThangResponse> doanhThuTungThangTrongNam(int year) {
        return thongKeRepo.doanhThuTungThangTrongNam(year);
    }

    @Override
    public List<DoanhThuNgayResponse> doanhThuTheoKhoangNgay(LocalDate from, LocalDate to) {
        return thongKeRepo.doanhThuTheoKhoangNgay(from, to);
    }

    @Override
    public long tongDoanhThuToanHeThong() {
        return thongKeRepo.tongDoanhThuToanHeThong();
    }

    @Override
    public long doanhThuHomNay() {
        return thongKeRepo.doanhThuHomNay();
    }

    @Override
    public long doanhThuTuanNay() {
        return thongKeRepo.doanhThuTuanNay();
    }

    @Override
    public long doanhThuThangNay() {
        return thongKeRepo.doanhThuThangNay();
    }

    // Đếm đơn hàng
    @Override
    public long countDonHangDangGiaoHang() {
        return thongKeRepo.countDonHangDangGiaoHang();
    }

    @Override
    public long countDonHangDaHuy() {
        return thongKeRepo.countDonHangDaHuy();
    }

    @Override
    public long countDonHangHoanThanh() {
        return thongKeRepo.countDonHangHoanThanh();
    }

    // Thống kê khác
    @Override
    public long countPhieuGiamGiaDangHoatDong() {
        return thongKeRepo.countPhieuGiamGiaDangHoatDong();
    }

    @Override
    public long countSanPhamBanChay() {
        return thongKeRepo.countSanPhamBanChay();
    }

    @Override
    public long countSanPhamHetHang() {
        return thongKeRepo.countSanPhamHetHang();
    }

    @Override
    public long countTongDonHang() {
        return thongKeRepo.countTongDonHang();
    }

    @Override
    public List<LowStockProductResponse> danhSachSapHetHang(int threshold, int limit) {
        return thongKeRepo.sanPhamSapHetHang(threshold, limit);
    }
} 