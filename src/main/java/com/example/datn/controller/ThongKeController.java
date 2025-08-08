package com.example.datn.controller;

import com.example.datn.dto.response.TopSanPhamBanChayResponse;
import com.example.datn.dto.response.ThongKeTongQuanResponse;
import com.example.datn.dto.response.DoanhThuNgayResponse;
import com.example.datn.dto.response.DoanhThuThangResponse;
import com.example.datn.service.ThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/thong-ke")
@CrossOrigin(origins = "*")
public class ThongKeController {
    private final ThongKeService thongKeService;

    @GetMapping("/tong-quan")
    public ResponseEntity<ThongKeTongQuanResponse> getTongQuan(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(thongKeService.thongKeTongQuanTrongKhoang(from, to));
    }

    @GetMapping("/top-ban-chay")
    public ResponseEntity<?> topBanChay(
            @RequestParam(defaultValue = "day") String scope,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "5") int limit) {
        List<TopSanPhamBanChayResponse> result;
        LocalDate today = LocalDate.now();
        switch (scope) {
            case "month" -> result = thongKeService.topSanPhamBanChayThang(today.getYear(), today.getMonthValue());
            case "week" -> {
                LocalDate start = today.minusDays(today.getDayOfWeek().getValue() - 1);
                LocalDate end = start.plusDays(6);
                result = thongKeService.topSanPhamBanChayKhoang(start, end);
            }
            case "custom" -> {
                LocalDate start = LocalDate.parse(from);
                LocalDate end = LocalDate.parse(to);
                result = thongKeService.topSanPhamBanChayKhoang(start, end);
            }
            default -> result = thongKeService.topSanPhamBanChayNgay(today);
        }
        if (limit > 0 && result.size() > limit) {
            result = result.subList(0, limit);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sap-het-hang")
    public ResponseEntity<?> sapHetHang(@RequestParam(defaultValue = "10") int threshold,
                                        @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(thongKeService.danhSachSapHetHang(threshold, limit));
    }

    @GetMapping("/doanh-thu-ngay-trong-thang")
    public ResponseEntity<List<DoanhThuNgayResponse>> doanhThuNgayTrongThang(@RequestParam int year, @RequestParam int month) {
        return ResponseEntity.ok(thongKeService.doanhThuTungNgayTrongThang(year, month));
    }
    @GetMapping("/doanh-thu-thang-trong-nam")
    public ResponseEntity<List<DoanhThuThangResponse>> doanhThuThangTrongNam(@RequestParam int year) {
        return ResponseEntity.ok(thongKeService.doanhThuTungThangTrongNam(year));
    }
    @GetMapping("/doanh-thu-theo-khoang")
    public ResponseEntity<List<DoanhThuNgayResponse>> doanhThuTheoKhoang(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(thongKeService.doanhThuTheoKhoangNgay(from, to));
    }
    @GetMapping("/doanh-thu-tong-hop")
    public ResponseEntity<?> doanhThuTongHop() {
        return ResponseEntity.ok(new Object() {
            public final long tongDoanhThu = thongKeService.tongDoanhThuToanHeThong();
            public final long doanhThuHomNay = thongKeService.doanhThuHomNay();
            public final long doanhThuTuanNay = thongKeService.doanhThuTuanNay();
            public final long doanhThuThangNay = thongKeService.doanhThuThangNay();
        });
    }
    
    @GetMapping("/trang-thai-don-hang")
    public ResponseEntity<?> trangThaiDonHang() {
        return ResponseEntity.ok(new Object() {
            public final long dangGiaoHang = thongKeService.countDonHangDangGiaoHang();
            public final long daHuy = thongKeService.countDonHangDaHuy();
            public final long hoanThanh = thongKeService.countDonHangHoanThanh();
        });
    }
    
    @GetMapping("/thong-ke-khac")
    public ResponseEntity<?> thongKeKhac() {
        return ResponseEntity.ok(new Object() {
            public final long phieuGiamGia = thongKeService.countPhieuGiamGiaDangHoatDong();
            public final long sanPhamBanChay = thongKeService.countSanPhamBanChay();
            public final long sanPhamHetHang = thongKeService.countSanPhamHetHang();
            public final long tongDonHang = thongKeService.countTongDonHang();
        });
    }
}

@Controller
@RequestMapping("/admin/thong_ke")
class ThongKeViewController {
    @GetMapping("/index")
    public String thongKeView(Model model) {
        return "admin/thong_ke/index";
    }
} 