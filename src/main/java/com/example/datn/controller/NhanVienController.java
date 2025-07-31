package com.example.datn.controller;

import com.example.datn.dto.request.AddNhanVienRequest;
import com.example.datn.dto.request.UpdateInforKhachHangRequest;
import com.example.datn.dto.request.UpdateNhanVienRequest;
import com.example.datn.entity.ChucVu;
import com.example.datn.entity.DiaChi;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.NhanVien.NhanVien;
import com.example.datn.repository.DiaChiRepo;
import com.example.datn.repository.NhanVienRepo;
import com.example.datn.service.ChucVuService.ChucVuService;
import com.example.datn.service.DiaChiService;
import com.example.datn.service.NhanVienService.NhanVienService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?>add(@RequestBody AddNhanVienRequest nhanVien){
        try {
            nhanVienService.addNhanVien(nhanVien);
            return ResponseEntity.ok().body("Thêm thanh công");
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> updateNhanVien(@RequestBody UpdateNhanVienRequest request) {
        try {
            nhanVienService.updateNhanVien(request);
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cập nhật thất bại");
        }
    }

    @GetMapping("/detail")
    public String detail(@RequestParam Long id, Model model){
        NhanVien nhanVien=nhanVienService.findNhanVienById(id);
        model.addAttribute("nhanVien",nhanVien);
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

}