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
    public ResponseEntity<List<TopSanPhamBanChayResponse>> getTopSanPhamBanChay(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String month
    ) {
        if (from != null && to != null) {
            return ResponseEntity.ok(thongKeService.topSanPhamBanChayTrongKhoang(from, to));
        } else if ("ngay".equalsIgnoreCase(type) && date != null) {
            return ResponseEntity.ok(thongKeService.topSanPhamBanChayTheoNgay(date));
        } else if ("thang".equalsIgnoreCase(type) && month != null) {
            YearMonth ym = YearMonth.parse(month);
            return ResponseEntity.ok(thongKeService.topSanPhamBanChayTheoThang(ym));
        } else {
            return ResponseEntity.badRequest().build();
        }
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
}

@Controller
@RequestMapping("/admin/thong_ke")
class ThongKeViewController {
    @GetMapping("/index")
    public String thongKeView(Model model) {
        return "admin/thong_ke/index";
    }
} 