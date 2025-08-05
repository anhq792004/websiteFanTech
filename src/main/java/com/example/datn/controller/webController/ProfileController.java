package com.example.datn.controller.webController;

import com.example.datn.dto.request.AddDiaChiRequest;
import com.example.datn.dto.request.UpdateDiaChiRequest;
import com.example.datn.entity.DiaChi;
import com.example.datn.entity.HoaDon.HoaDon;
import com.example.datn.entity.HoaDon.HoaDonChiTiet;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.TaiKhoan;
import com.example.datn.repository.DiaChiRepo;
import com.example.datn.service.DiaChiService;
import com.example.datn.service.HoaDonService.HoaDonService;
import com.example.datn.service.KhachHangService.KhachHangService;
import com.example.datn.service.taiKhoanService.TaiKhoanService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    private final DiaChiRepo diaChiRepo;

    private final DiaChiService diaChiService;

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
    // dia chi
    @GetMapping("/address")
    public String address(Model model,
                          HttpSession session) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        KhachHang khachHang = khachHangService.findByTaiKhoan(currentUser);
        List<DiaChi> listDiaChi = diaChiService.getDiaChiByIdKhachHang(khachHang.getId());
        model.addAttribute("khachHang", khachHang);
        model.addAttribute("listDiaChi", listDiaChi);
        return "user/infor/address";
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

    @GetMapping("/xoa/{id}")
    public String xoa(@PathVariable("id") Long id, HttpSession session) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        KhachHang khachHang = khachHangService.findByTaiKhoan(currentUser);

        // Kiểm tra xem địa chỉ này có thuộc khách hàng hiện tại không (nếu cần)
        DiaChi diaChi = diaChiRepo.findById(id).orElse(null);
        if (diaChi != null && diaChi.getKhachHang().getId().equals(khachHang.getId())) {
            diaChiRepo.deleteById(id);
        }

        return "redirect:/profile/address";
    }


    @PostMapping("/cap-nhat-dia-chi")
    public String capNhatDiaChi(
            UpdateDiaChiRequest request
    ) {
        diaChiService.update(request);
        return "redirect:/profile/address";
    }

    //phieu giam gia
    @GetMapping("/coupon")
    public String coupon() {

        return "user/infor/coupon";
    }

    @GetMapping("/change-password")
    public String changePassword(Model model, HttpSession session) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("showVerificationForm", false);
        model.addAttribute("showPasswordForm", false);
        return "user/infor/changePassword";
    }

    @PostMapping("/change-password/request")
    public String requestVerificationCode(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            taiKhoanService.sendForgotPasswordCode(currentUser.getEmail(), session);
            redirectAttributes.addFlashAttribute("message", "Mã xác thực đã được gửi đến email của bạn!");
            model.addAttribute("showVerificationForm", true);
            model.addAttribute("showPasswordForm", false);
            return "user/infor/changePassword";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể gửi mã xác thực. Vui lòng thử lại!");
            return "redirect:/profile/change-password";
        }
    }

    @PostMapping("/change-password/verify")
    public String verifyCode(@RequestParam("code") String code, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        boolean isVerified = taiKhoanService.verifyForgotPasswordCode(currentUser.getEmail(), code, session);
        if (isVerified) {
            model.addAttribute("showVerificationForm", false);
            model.addAttribute("showPasswordForm", true);
            return "user/infor/changePassword";
        } else {
            redirectAttributes.addFlashAttribute("error", "Mã xác thực không đúng!");
            model.addAttribute("showVerificationForm", true);
            model.addAttribute("showPasswordForm", false);
            return "user/infor/changePassword";
        }
    }

    @PostMapping("/change-password/update")
    public String updatePassword(@RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "redirect:/profile/change-password";
        }

        try {
            taiKhoanService.changePassword(currentUser.getEmail(), newPassword);
            redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công!");
            session.removeAttribute("verifiedEmail");
            return "redirect:/profile/change-password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi đổi mật khẩu!");
            return "redirect:/profile/change-password";
        }
    }

    @GetMapping("/print-invoice/{id}")
    public String printInvoice(@PathVariable Long id, Model model, HttpSession session) {
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

        return "user/orderInfor/print";
    }
}
