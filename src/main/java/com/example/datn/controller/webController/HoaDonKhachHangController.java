package com.example.datn.controller.webController;

import com.example.datn.entity.HoaDon.HoaDon;
import com.example.datn.entity.HoaDon.HoaDonChiTiet;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.TaiKhoan;
import com.example.datn.service.HoaDonService.HoaDonService;
import com.example.datn.service.KhachHangService.KhachHangService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/vin-fan")
public class HoaDonKhachHangController {

    private final HoaDonService hoaDonService;
    private final KhachHangService khachHangService;

    @GetMapping("/hoa-don-kh/{id}")
    public String detailHoaDon(@PathVariable Long id, Model model, HttpSession session) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Lấy thông tin khách hàng
        KhachHang khachHang = khachHangService.findByTaiKhoan(currentUser);
        if (khachHang == null) {
            return "redirect:/login";
        }

        // Lấy hóa đơn theo ID và kiểm tra xem có thuộc về khách hàng này không
        HoaDon hoaDon = hoaDonService.findHoaDonById(id).orElse(null);
        if (hoaDon == null || !hoaDon.getKhachHang().getId().equals(khachHang.getId())) {
            return "redirect:/profile/ordered";
        }

        // Lấy chi tiết hóa đơn
        List<HoaDonChiTiet> listHDCT = hoaDonService.listHoaDonChiTiets(id);
        
        // Tính tổng số lượng
        Integer tongSoLuong = hoaDonService.tongSoLuong(id);

        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("listHDCT", listHDCT);
        model.addAttribute("tongSoLuong", tongSoLuong);

        return "user/orderInfor/detail";
    }
} 