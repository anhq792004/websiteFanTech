package com.example.datn.controller;


import com.example.datn.dto.request.AddDiaChiRequest;
import com.example.datn.dto.request.AddKhachHangRequest;
import com.example.datn.dto.request.UpdateDiaChiRequest;
import com.example.datn.dto.request.UpdateInforKhachHangRequest;
import com.example.datn.entity.DiaChi;
import com.example.datn.entity.KhachHang;
import com.example.datn.repository.DiaChiRepo;
import com.example.datn.repository.TaiKhoanRepo;
import com.example.datn.service.DiaChiService;
import com.example.datn.service.KhachHangService.KhachHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/khach-hang")
public class KhachHangController {
    private final KhachHangService khachHangService;
    private final DiaChiService diaChiService;
    private final DiaChiRepo diaChiRepo;
    private final TaiKhoanRepo taiKhoanRepo;
    @GetMapping("/index")
    public String getAllKhachHang(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(name = "trangThai", defaultValue = "") Boolean trangThai,
            Model model) {
        if (page < 0) {
            page = 0;
        }
        PageRequest pageable = PageRequest.of(page, size);
        Page<KhachHang> listKH = khachHangService.findAll(search, trangThai, pageable);
        model.addAttribute("page", listKH.getNumber());
        model.addAttribute("size", listKH.getSize());
        model.addAttribute("totalPages", listKH.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("trangThai", trangThai);

        model.addAttribute("listKH", listKH);
        return "admin/khach_hang/index";
    }

    @GetMapping("/view-them")
    public String showThemKhachHangForm(Model model) {

        model.addAttribute("khachHang", new KhachHang());
        return "admin/khach_hang/view-them";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam Long id, Model model) {
        KhachHang khachHang = khachHangService.findById(id);
        // ✅ Format ngày sinh nếu có
        String ngaySinhFormatted = "";
        if (khachHang.getNgaySinh() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            ngaySinhFormatted = sdf.format(khachHang.getNgaySinh());
        }
        List<DiaChi> listDiaChi = diaChiService.getDiaChiByIdKhachHang(id);
        model.addAttribute("ngaySinhFormatted", ngaySinhFormatted); // ✅ Truyền thêm biến này vào model
        model.addAttribute("khachHang", khachHang);
        model.addAttribute("listDiaChi", listDiaChi);

        return "admin/khach_hang/detail";
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestParam("ten") String ten,
                                 @RequestParam("email") String email,
                                 @RequestParam("soDienThoai") String soDienThoai,
                                 @RequestParam("ngaySinh") String ngaySinh,
                                 @RequestParam("gioiTinh") String gioiTinh,
                                 @RequestParam("tinhThanhPho") String tinhThanhPho,
                                 @RequestParam("quanHuyen") String quanHuyen,
                                 @RequestParam("xaPhuong") String xaPhuong,
                                 @RequestParam("soNhaNgoDuong") String soNhaNgoDuong,
                                 @RequestParam(value = "hinhAnh", required = false) MultipartFile hinhAnh) {
        try {
            // Tạo AddKhachHangRequest từ các tham số
            AddKhachHangRequest request = new AddKhachHangRequest();
            request.setTen(ten);
            request.setEmail(email);
            request.setSoDienThoai(soDienThoai);
            
            // Parse ngày sinh từ String sang Date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date ngaySinhDate = sdf.parse(ngaySinh);
                request.setNgaySinh(ngaySinhDate);
            } catch (ParseException e) {
                return ResponseEntity.badRequest().body("Định dạng ngày sinh không hợp lệ (dd/MM/yyyy)");
            }
            
            request.setGioiTinh(gioiTinh);
            request.setTinhThanhPho(tinhThanhPho);
            request.setQuanHuyen(quanHuyen);
            request.setXaPhuong(xaPhuong);
            request.setSoNhaNgoDuong(soNhaNgoDuong);
            
            khachHangService.addKH(request, hinhAnh);
            return ResponseEntity.ok().body("Thêm khách hàng thành công!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add-dia-chi")
    public ResponseEntity<?> addDiaChi(@RequestBody AddDiaChiRequest request) {
        try {
            diaChiService.addDiaChi(request);
            return ResponseEntity.ok().body("Thêm địa chỉ thành công!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-status")
    public ResponseEntity<?> thayDoiTrangThai(@RequestParam(value = "id", required = true) Long id) {
        return khachHangService.changeStatus(id);
    }

    @PostMapping("/xoa-dia-chi")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> xoaDiaChi(@RequestParam Long diaChiId, 
                                                         @RequestParam Long khachHangId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra xem địa chỉ này có phải địa chỉ mặc định không
            KhachHang khachHang = khachHangService.findById(khachHangId);
            if (khachHang != null && khachHang.getDiaChiMacDinhId() != null && 
                khachHang.getDiaChiMacDinhId().equals(diaChiId)) {
                response.put("success", false);
                response.put("message", "Không thể xóa địa chỉ mặc định. Vui lòng đặt địa chỉ khác làm mặc định trước khi xóa.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Xóa địa chỉ
            diaChiRepo.deleteById(diaChiId);
            
            response.put("success", true);
            response.put("message", "Xóa địa chỉ thành công");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/cap-nhat-dia-chi")
    public String capNhatDiaChi(
            UpdateDiaChiRequest request
    ) {
        diaChiService.update(request);
        return "redirect:/khach-hang/detail?id=" + request.getKhachHangId();
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> updateKhachHang(@RequestParam("idKH") Long idKH,
                                                   @RequestParam("ten") String ten,
                                                   @RequestParam("soDienThoai") String soDienThoai,
                                                   @RequestParam("ngaySinh") String ngaySinh,
                                                   @RequestParam("gioiTinh") String gioiTinh,
                                                   @RequestParam(value = "hinhAnh", required = false) MultipartFile hinhAnh) {
        try {
            // Tạo UpdateInforKhachHangRequest từ các tham số
            UpdateInforKhachHangRequest request = new UpdateInforKhachHangRequest();
            request.setIdKH(idKH);
            request.setTen(ten);
            request.setSoDienThoai(soDienThoai);
            
            // Parse ngày sinh từ String sang Date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date ngaySinhDate = sdf.parse(ngaySinh);
                request.setNgaySinh(ngaySinhDate);
            } catch (ParseException e) {
                return ResponseEntity.badRequest().body("Định dạng ngày sinh không hợp lệ (dd/MM/yyyy)");
            }
            
            request.setGioiTinh(gioiTinh);
            
            khachHangService.updateInforKhachHang(request, hinhAnh);
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cập nhật thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/check-email")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = taiKhoanRepo.existsByEmail(email);
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/set-default-address")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> setDefaultAddress(
            @RequestParam Long khachHangId, 
            @RequestParam Long diaChiId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra khách hàng tồn tại
            KhachHang khachHang = khachHangService.findById(khachHangId);
            if (khachHang == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy khách hàng");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Kiểm tra địa chỉ có thuộc về khách hàng này không
            DiaChi diaChi = diaChiRepo.findById(diaChiId).orElse(null);
            if (diaChi == null || !diaChi.getKhachHang().getId().equals(khachHangId)) {
                response.put("success", false);
                response.put("message", "Địa chỉ không hợp lệ");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Cập nhật địa chỉ mặc định
            khachHang.setDiaChiMacDinhId(diaChiId);
            khachHangService.save(khachHang);
            
            response.put("success", true);
            response.put("message", "Đặt địa chỉ mặc định thành công");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
