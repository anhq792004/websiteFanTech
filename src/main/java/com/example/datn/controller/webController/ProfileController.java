package com.example.datn.controller.webController;

import com.example.datn.entity.HoaDon.HoaDon;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.TaiKhoan;
import com.example.datn.service.HoaDonService.HoaDonService;
import com.example.datn.service.KhachHangService.KhachHangService;
import com.example.datn.service.taiKhoanService.TaiKhoanService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final KhachHangService khachHangService;

    private final TaiKhoanService taiKhoanService;

    private final HoaDonService hoaDonService;

    /**
     * Hiển thị trang thông tin cá nhân
     */
    @GetMapping
    public String showProfile(Model model, HttpSession session) {
        // Lấy thông tin tài khoản từ session
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");

        if (currentUser == null) {
            return "redirect:/login";
        }

        // Tìm khách hàng theo tài khoản
        KhachHang khachHang = khachHangService.findByTaiKhoan(currentUser);

        if (khachHang == null) {
            // Nếu chưa có thông tin khách hàng, tạo mới
            khachHang = new KhachHang();
            khachHang.setTaiKhoan(currentUser);
            khachHang.setMa(generateCustomerCode());
            khachHang.setNgayTao(new Date());
            khachHang.setTrangThai(true);
            khachHang = khachHangService.save(khachHang);
        }

        model.addAttribute("khachHang", khachHang);
        return "user/infor/profile"; // Tên template
    }

    /**
     * Hiển thị trang chỉnh sửa thông tin
     */
    @GetMapping("/edit")
    public String showEditProfile(Model model, HttpSession session) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");

        if (currentUser == null) {
            return "redirect:/login";
        }

        KhachHang khachHang = khachHangService.findByTaiKhoan(currentUser);

        if (khachHang == null) {
            // Tạo mới nếu chưa có
            khachHang = new KhachHang();
            khachHang.setTaiKhoan(currentUser);
            khachHang.setMa(generateCustomerCode());
            khachHang.setNgayTao(new Date());
            khachHang.setTrangThai(true);
        }

        model.addAttribute("khachHang", khachHang);
        return "user/infor/updateTTKH";
    }

    /**
     * Xử lý cập nhật thông tin
     */
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute KhachHang khachHang,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");

            if (currentUser == null) {
                return "redirect:/login";
            }

            // Lấy thông tin khách hàng hiện tại từ database
            KhachHang existingKhachHang = khachHangService.findByTaiKhoan(currentUser);

            if (existingKhachHang == null) {
                // Tạo mới nếu chưa có
                existingKhachHang = new KhachHang();
                existingKhachHang.setTaiKhoan(currentUser);
                existingKhachHang.setMa(generateCustomerCode());
                existingKhachHang.setNgayTao(new Date());
                existingKhachHang.setTrangThai(true);
            }

            // Cập nhật thông tin
            existingKhachHang.setTen(khachHang.getTen());
            existingKhachHang.setSoDienThoai(khachHang.getSoDienThoai());
            existingKhachHang.setGioiTinh(khachHang.getGioiTinh());
            existingKhachHang.setNgaySinh(khachHang.getNgaySinh());

            // Lưu vào database
            khachHangService.save(existingKhachHang);

            redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin thành công!");
            return "redirect:/profile";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật thông tin!");
            return "redirect:/profile/edit";
        }
    }

    /**
     * Tạo mã khách hàng tự động
     */
    private String generateCustomerCode() {
        // Lấy số lượng khách hàng hiện tại và tạo mã mới
        long count = khachHangService.count();
        return "KH" + String.format("%04d", count + 1);
    }

    @GetMapping("/ordered")
    public String trackOrder(Model model,
                             HttpSession session) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        KhachHang khachHang = khachHangService.findByTaiKhoan(currentUser);
        List<HoaDon> hoaDons = hoaDonService.getHoaDonByIdKH(khachHang.getId());
        model.addAttribute("hoaDons", hoaDons);

        return "user/infor/ordered";
    }

    @GetMapping("/order-infor/detail")
    public String detail(Model model,
                             HttpSession session) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        KhachHang khachHang = khachHangService.findByTaiKhoan(currentUser);
        List<HoaDon> hoaDons = hoaDonService.getHoaDonByIdKH(khachHang.getId());
        model.addAttribute("hoaDons", hoaDons);

        return "user/infor/ordered";
    }
    @GetMapping("/address")
    public String address() {

        return "user/infor/address";
    }

    @GetMapping("/coupon")
    public String coupon() {

        return "user/infor/coupon";
    }

    @GetMapping("/change-password")
    public String changePassword() {

        return "user/infor/changePassword";
    }
}
