package com.example.datn.controller;

import com.example.datn.dto.request.AddNhanVienRequest;
import com.example.datn.dto.request.UpdateNhanVienRequest;
import com.example.datn.entity.ChucVu;
import com.example.datn.entity.DiaChi;
import com.example.datn.entity.NhanVien.NhanVien;
import com.example.datn.repository.DiaChiRepo;
import com.example.datn.repository.TaiKhoanRepo;
import com.example.datn.service.ChucVuService.ChucVuService;
import com.example.datn.service.NhanVienService.NhanVienService;
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
@RequestMapping("/admin/nhan-vien")
public class NhanVienController {
    private final NhanVienService nhanVienService;
    private final ChucVuService chucVuService;
    private final DiaChiRepo diaChiRepo;
    private final TaiKhoanRepo taiKhoanRepo;

    @ModelAttribute("listDiaChi")
    List<DiaChi> getListDiaChi() {
        return diaChiRepo.findAll();
    }

    @GetMapping("/index")
    public String getAllNhanVien(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(name = "trangThai", defaultValue = "") Boolean trangThai,
            Model model) {
        if (page < 0) {
            page = 0;
        }
        PageRequest pageable = PageRequest.of(page, size);
        Page<NhanVien> listNV = nhanVienService.findAll(search, trangThai, pageable);
        model.addAttribute("page", listNV.getNumber());
        model.addAttribute("size", listNV.getSize());
        model.addAttribute("totalPages", listNV.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("trangThai", trangThai);

        model.addAttribute("listNV", listNV);
        return "admin/nhan_vien/index";
    }

    @GetMapping("/view-them")
    public String showThemNhanVienForm(Model model) {
        model.addAttribute("nhanVien", new NhanVien());
        model.addAttribute("listChucVu", chucVuService.findAllChucVu());
        return "admin/nhan_vien/view-them";
    }

    @PostMapping("/them")
    public ResponseEntity<?> add(@RequestParam("ten") String ten,
                                 @RequestParam("canCuocCongDan") String canCuocCongDan,
                                 @RequestParam("email") String email,
                                 @RequestParam("soDienThoai") String soDienThoai,
                                 @RequestParam("ngaySinh") String ngaySinh,
                                 @RequestParam("gioiTinh") String gioiTinh,
                                 @RequestParam("tinhThanhPho") String tinhThanhPho,
                                 @RequestParam("quanHuyen") String quanHuyen,
                                 @RequestParam("xaPhuong") String xaPhuong,
                                 @RequestParam("soNhaNgoDuong") String soNhaNgoDuong,
                                 @RequestParam("chucVu") String chucVu,
                                 @RequestParam(value = "hinhAnh", required = false) MultipartFile hinhAnh) {
        try {
            // Tạo AddNhanVienRequest từ các tham số
            AddNhanVienRequest nhanVien = new AddNhanVienRequest();
            nhanVien.setTen(ten);
            nhanVien.setCanCuocCongDan(canCuocCongDan);
            nhanVien.setEmail(email);
            nhanVien.setSoDienThoai(soDienThoai);
            
            // Parse ngày sinh từ String sang Date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date ngaySinhDate = sdf.parse(ngaySinh);
                nhanVien.setNgaySinh(ngaySinhDate);
            } catch (ParseException e) {
                return ResponseEntity.badRequest().body("Định dạng ngày sinh không hợp lệ (dd/MM/yyyy)");
            }
            
            nhanVien.setGioiTinh(gioiTinh);
            nhanVien.setTinhThanhPho(tinhThanhPho);
            nhanVien.setQuanHuyen(quanHuyen);
            nhanVien.setXaPhuong(xaPhuong);
            nhanVien.setSoNhaNgoDuong(soNhaNgoDuong);
            nhanVien.setChucVu(chucVu);
            
            nhanVienService.addNhanVien(nhanVien, hinhAnh);
            return ResponseEntity.ok().body("Thêm thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> updateNhanVien(@RequestParam("id") Long id,
                                                 @RequestParam("ten") String ten,
                                                 @RequestParam("soDienThoai") String soDienThoai,
                                                 @RequestParam("canCuocCongDan") String canCuocCongDan,
                                                 @RequestParam("ngaySinh") String ngaySinh,
                                                 @RequestParam("gioiTinh") String gioiTinh,
                                                 @RequestParam("tinhThanhPho") String tinhThanhPho,
                                                 @RequestParam("quanHuyen") String quanHuyen,
                                                 @RequestParam("xaPhuong") String xaPhuong,
                                                 @RequestParam("soNhaNgoDuong") String soNhaNgoDuong,
                                                 @RequestParam("chucVu") String chucVu,
                                                 @RequestParam(value = "hinhAnh", required = false) MultipartFile hinhAnh) {
        try {
            // Tạo UpdateNhanVienRequest từ các tham số
            UpdateNhanVienRequest request = new UpdateNhanVienRequest();
            request.setId(id);
            request.setTen(ten);
            request.setSoDienThoai(soDienThoai);
            request.setCanCuocCongDan(canCuocCongDan);
            
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
            request.setChucVu(chucVu);
            
            nhanVienService.updateNhanVien(request, hinhAnh);
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cập nhật thất bại: " + e.getMessage());
        }
    }

    @GetMapping("/detail")
    public String detail(@RequestParam Long id, Model model) {
        NhanVien nhanVien = nhanVienService.findNhanVienById(id);
        String ngaySinhFormatted = "";
        if (nhanVien.getNgaySinh() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            ngaySinhFormatted = sdf.format(nhanVien.getNgaySinh());
        }
        model.addAttribute("ngaySinhFormatted", ngaySinhFormatted);
        model.addAttribute("nhanVien", nhanVien);
        return "admin/nhan_vien/detail";
    }

    @GetMapping("/api/chuc-vu")
    @ResponseBody
    public ResponseEntity<List<ChucVu>> getChucVu() {
        List<ChucVu> chucVuList = chucVuService.findAllChucVu();
        return ResponseEntity.ok(chucVuList);
    }

    @PostMapping("/change-status")
    public ResponseEntity<?> thayDoiTrangThai(@RequestParam(value = "id", required = true) Long id) {
        return nhanVienService.changeStatus(id);
    }

    // Thêm method này vào Controller để kiểm tra email realtime (optional)
    @PostMapping("/check-email")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = taiKhoanRepo.existsByEmail(email);
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/changePassword")
    public String changePassword(){

        return "admin/nhan_vien/change-password";
    }

}