package com.example.datn.controller;

import com.example.datn.service.taiKhoanService.TaiKhoanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class QuenMatKhauController {
    @Autowired
    private TaiKhoanService taiKhoanService;

    // Hiển thị form quên mật khẩu
    @GetMapping("/forgot-password")
    public String hienThiFormDoiMatKhau() {
        return "admin/user/quenMK";
    }

    // Xử lý gửi mã xác thực qua email
    @PostMapping("/forgot-password/send-code")
    public String sendVerificationCode(@RequestParam("email") String email,
                                       @RequestParam("newPassword") String newPassword,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       Model model,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
            return "admin/user/quenMK";
        }

        try {
            taiKhoanService.sendForgotPasswordCode(email, newPassword, session);
            model.addAttribute("email", email);
            model.addAttribute("sent", true);
            System.out.println("Gửi mã thành công cho email: " + email); // Debug
            return "admin/user/xacnhanQuenMK";
        } catch (RuntimeException e) {
            System.out.println("Lỗi khi gửi mã: " + e.getMessage()); // Debug
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/forgot-password";
        }
    }

    // Xử lý xác minh mã và đổi mật khẩu
    @PostMapping("/forgot-password/verify-code")
    public String verifyCodeAndChangePassword(@RequestParam("email") String email,
                                              @RequestParam("code") String code,
                                              Model model,
                                              HttpSession session,
                                              RedirectAttributes redirectAttributes) {

        System.out.println("Đang xác thực mã cho email: " + email + " với mã: " + code); // Debug

        if (taiKhoanService.verifyForgotPasswordCodeAndChangePassword(email, code, session)) {
            System.out.println("Xác thực thành công cho email: " + email); // Debug

            // Thêm thông báo thành công vào session để hiển thị ở trang login
            redirectAttributes.addFlashAttribute("success",
                    "Mật khẩu đã được thay đổi thành công! Vui lòng đăng nhập với mật khẩu mới.");

            return "redirect:/login";
        } else {
            System.out.println("Xác thực thất bại cho email: " + email); // Debug

            model.addAttribute("email", email);
            model.addAttribute("error", "Mã xác thực không đúng hoặc đã hết hạn. Vui lòng thử lại.");
            return "admin/user/xacnhanQuenMK";
        }
    }

    // Thêm method để hiển thị trang xác nhận với thông báo
    @GetMapping("/forgot-password/verify")
    public String showVerificationPage(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "admin/user/xacnhanQuenMK";
    }
}
