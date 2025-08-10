package com.example.datn.controller.HoaDonController;

import com.example.datn.dto.request.AddSPToHDCTRequest;
import com.example.datn.dto.request.TrangThaiHoaDonRequest;
import com.example.datn.dto.request.UpdateInforRequest;
import com.example.datn.dto.request.UpdateSoLuongRequest;
import com.example.datn.dto.response.LichSuThanhToanResponse;
import com.example.datn.entity.HoaDon.HoaDon;
import com.example.datn.entity.HoaDon.HoaDonChiTiet;
import com.example.datn.entity.HoaDon.LichSuHoaDon;
import com.example.datn.entity.SanPham.SanPhamChiTiet;
import com.example.datn.repository.HoaDonRepo.HoaDonChiTietRepo;
import com.example.datn.service.BanHang.BanHangService;
import com.example.datn.service.HoaDonService.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
@RequestMapping("/hoa-don")
public class HoaDonController {
    private final HoaDonService hoaDonService;
    private final BanHangService banHangService;


    @GetMapping("/index")
    public String getAllHoaDon(@RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "5") int size,
                               @RequestParam(name = "query", defaultValue = "") String query,
                               @RequestParam(name = "loaiHoaDon", defaultValue = "") Boolean loaiHoaDon,
                               @RequestParam(name = "trangThai", required = false) Integer trangThai,
                               @RequestParam(name = "startDate", required = false) String startDate,
                               @RequestParam(name = "endDate", required = false) String endDate,
                               Model model) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(LocalTime.MAX);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Hoặc ghi log lỗi để dễ debug
        }

        if (page < 0) page = 0;

        Page<HoaDon> list = hoaDonService.searchHoaDon(query.trim(), loaiHoaDon, start, end, trangThai, PageRequest.of(page, size));

        TrangThaiHoaDonRequest trangThaiHoaDon = hoaDonService.getTrangThaiHoaDon();
        model.addAttribute("trangThaiHoaDon", trangThaiHoaDon);
        model.addAttribute("list", list);
        model.addAttribute("query", query);
        model.addAttribute("loaiHoaDon", loaiHoaDon != null ? loaiHoaDon : "");
        model.addAttribute("trangThai", trangThai != null ? trangThai : "");
        model.addAttribute("startDate", startDate != null ? startDate : "");
        model.addAttribute("endDate", endDate != null ? endDate : "");
        model.addAttribute("page", page);
        model.addAttribute("totalPages", list.getTotalPages());

        return "admin/hoa_don/index";
    }


    @GetMapping("/detail")
    public String detail(@RequestParam Long id, Model model) {
        Optional<HoaDon> hoaDonOptional = hoaDonService.findHoaDonById(id);
        HoaDon hoaDon = hoaDonOptional.orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn"));
        model.addAttribute("hoaDon", hoaDon);

        List<LichSuHoaDon> lichSuHoaDonList = hoaDonService.lichSuHoaDonList(id);
        model.addAttribute("lichSuHoaDonList", lichSuHoaDonList);

        LichSuThanhToanResponse lichSuThanhToanResponse = hoaDonService.getLSTTByHoaDonId(id);
        model.addAttribute("listLSTT", lichSuThanhToanResponse);

        List<HoaDonChiTiet> listHDCT = hoaDonService.listHoaDonChiTiets(id);
        model.addAttribute("listHDCT", listHDCT);

        List<SanPhamChiTiet> findSPCTByIdSanPham = hoaDonService.findSPCTByIdSanPham();
        model.addAttribute("findSPCTByIdSanPham", findSPCTByIdSanPham);

        Integer tongSoLuong = hoaDonService.tongSoLuong(id);
        model.addAttribute("tongSoLuong", tongSoLuong);

        return "admin/hoa_don/detail";
    }

    @PostMapping("/xac-nhan")
    @ResponseBody
    public ResponseEntity<String> xacNhanHoaDon(@RequestParam("id") Long id,
                                                @RequestParam("ghiChu") String ghiChu) {
        try {
            hoaDonService.xacNhan(id, ghiChu);
            hoaDonService.truSoLuongSanPham(id);
            return ResponseEntity.ok("Đơn hàng đã được xác nhận !");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Số lượng sản phẩm không đủ");
        }
    }

    @PostMapping("/return-trangThai")
    @ResponseBody
    public ResponseEntity<String> returnTrangThai(@RequestParam("id") Long id) {
        hoaDonService.returnTrangThai(id);
        return ResponseEntity.ok("Đơn hàng đã được chuyển về trạng thái chờ !");
    }

    @PostMapping("/giao-hang")
    @ResponseBody
    public ResponseEntity<String> giaoHang(@RequestParam("id") Long id) {
        hoaDonService.giaoHang(id);
        return ResponseEntity.ok("Đơn hàng đã được bàn giao thành công !");
    }

    @PostMapping("/hoan-thanh")
    @ResponseBody
    public ResponseEntity<String> hoanThanh(@RequestParam("id") Long id) {
        hoaDonService.hoanThanh(id);
        return ResponseEntity.ok("Đơn hàng đã hoàn thành !");
    }

    @PostMapping("/huy")
    @ResponseBody
    public ResponseEntity<String> huy(@RequestParam("id") Long id,
                                      @RequestParam("ghiChu") String ghiChu) {
        hoaDonService.huy(id, ghiChu);
//        hoaDonService.hoanSoLuongSanPham(id);
        return ResponseEntity.ok("Hóa đơn đã được hủy !");
    }

    @PostMapping("/huy-hd-onl")
    @ResponseBody
    public ResponseEntity<String> huyHDOnl(@RequestParam("id") Long id,
                                      @RequestParam("ghiChu") String ghiChu) {
        hoaDonService.huy(id, ghiChu);
        return ResponseEntity.ok("Hóa đơn đã được hủy !");
    }

    @PostMapping("/addSP")
    public ResponseEntity<String> addSP(@RequestBody AddSPToHDCTRequest addSPToHDCTRequest) {
        try {
            hoaDonService.addSPToHDCT(addSPToHDCTRequest);
            banHangService.updateTongTienHoaDon(addSPToHDCTRequest.getIdHD());
            return ResponseEntity.ok("Thêm sản phẩm thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi: " + e.getMessage());
        }
    }

    @PostMapping("/xoa")
    @ResponseBody
    public ResponseEntity<String> deleteChiTiet(@RequestParam("idSP") Long idSP,
                                                @RequestParam("idHD") Long idHD) {
        try {
            hoaDonService.deleteSPInHD(idSP, idHD);
            banHangService.updateTongTienHoaDon(idHD);
            return ResponseEntity.ok("Xóa thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xóa sản phẩm");
        }
    }

    @PostMapping("/tangSoLuong")
    @ResponseBody
    public ResponseEntity<String> tangSoLuong(@RequestParam("idSP") Long idSP,
                                              @RequestParam("idHD") Long idHD) {
        try {
            hoaDonService.tangSoLuong(idHD, idSP);
            banHangService.updateTongTienHoaDon(idHD);
            return ResponseEntity.ok("Tăng số lượng thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/giamSoLuong")
    @ResponseBody
    public ResponseEntity<String> giamSoLuong(@RequestParam("idSP") Long idSP,
                                              @RequestParam("idHD") Long idHD) {
        try {
            hoaDonService.giamSoLuong(idHD, idSP);
            banHangService.updateTongTienHoaDon(idHD);
            return ResponseEntity.ok("Giảm số lượng thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/updateSoLuong")
    @ResponseBody
    public ResponseEntity<String> updateSoLuong(@RequestBody UpdateSoLuongRequest request) {
        try {
            hoaDonService.updateSoluong(request);
            banHangService.updateTongTienHoaDon(request.getIdHD());
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/updateInfor")
    @ResponseBody
    public ResponseEntity<?> updateInfor( @RequestBody UpdateInforRequest request) {
        hoaDonService.updateInfor(request);
        return ResponseEntity.ok("Cập nhật thông tin thành công");
    }

    @GetMapping("/print/{id}")
    public String printHoaDon(@PathVariable Long id, Model model) {
        Optional<HoaDon> hoaDonOptional = hoaDonService.findHoaDonById(id);
        HoaDon hoaDon = hoaDonOptional.orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn"));
        model.addAttribute("hoaDon", hoaDon);

        List<HoaDonChiTiet> listHDCT = hoaDonService.listHoaDonChiTiets(id);
        model.addAttribute("listHDCT", listHDCT);

        Integer tongSoLuong = hoaDonService.tongSoLuong(id);
        model.addAttribute("tongSoLuong", tongSoLuong);

        return "admin/hoa_don/print";
    }
}
