package com.example.datn.service.Implements;

import com.example.datn.dto.response.TopSanPhamBanChayResponse;
import com.example.datn.dto.response.ThongKeTongQuanResponse;
import com.example.datn.dto.response.DoanhThuNgayResponse;
import com.example.datn.dto.response.DoanhThuThangResponse;
import com.example.datn.entity.SanPham.SanPham;
import com.example.datn.repository.SanPhamRepo.SanPhamRepo;
import com.example.datn.repository.HoaDonRepo.HoaDonChiTietRepo;
import com.example.datn.repository.ThongKeRepo;
import com.example.datn.service.ThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThongKeServiceImpl implements ThongKeService {
    private final ThongKeRepo thongKeRepo;

    @Override
    public List<TopSanPhamBanChayResponse> topSanPhamBanChayTheoNgay(LocalDate date) {
        return thongKeRepo.topSanPhamBanChayTheoNgay(date);
    }

    @Override
    public List<TopSanPhamBanChayResponse> topSanPhamBanChayTheoThang(YearMonth month) {
        return thongKeRepo.topSanPhamBanChayTheoThang(month.getYear(), month.getMonthValue());
    }

    @Override
    public List<TopSanPhamBanChayResponse> topSanPhamBanChayTrongKhoang(LocalDate from, LocalDate to) {
        return thongKeRepo.topSanPhamBanChayTrongKhoang(from, to);
    }

    @Override
    public ThongKeTongQuanResponse thongKeTongQuanTrongKhoang(LocalDate from, LocalDate to) {
        return thongKeRepo.thongKeTongQuanTrongKhoang(from, to);
    }

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
} 