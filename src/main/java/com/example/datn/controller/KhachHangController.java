package com.example.datn.controller;


import com.example.datn.dto.request.AddDiaChiRequest;
import com.example.datn.dto.request.AddKhachHangRequest;
import com.example.datn.dto.request.UpdateDiaChiRequest;
import com.example.datn.dto.request.UpdateInforKhachHangRequest;
import com.example.datn.entity.DiaChi;
import com.example.datn.entity.KhachHang;
import com.example.datn.repository.DiaChiRepo;
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

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/khach-hang")
public class KhachHangController {
    private final KhachHangService khachHangService;
    private final DiaChiService diaChiService;
    private final DiaChiRepo diaChiRepo;

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
        model.addAttribute("khachHang", khachHang);

        List<DiaChi> listDiaChi = diaChiService.getDiaChiByIdKhachHang(id);
        model.addAttribute("listDiaChi", listDiaChi);

        return "admin/khach_hang/detail";
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody AddKhachHangRequest request) {
        try {
            khachHangService.addKH(request);
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

    @GetMapping("/xoa/{id}")
    public String xoa(@RequestParam("khachHangId") Integer khachHangId,
                      @PathVariable("id") Long id) {
        diaChiRepo.deleteById(id);
        return "redirect:/khach-hang/detail?id=" + khachHangId;
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
    public ResponseEntity<String> updateKhachHang(@RequestBody UpdateInforKhachHangRequest request) {
        try {
            khachHangService.updateInforKhachHang(request);
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cập nhật thất bại");
        }
    }





}
