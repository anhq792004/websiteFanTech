package com.example.datn.controller.TaiKhoanController;

import com.example.datn.service.taiKhoanService.TaiKhoanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class QuenMatKhauController {
    @Autowired
    private TaiKhoanService taiKhoanService;

    // Hiển thị form nhập email để gửi mã xác thực
    @GetMapping("/forgot-password")
    public String hienThiFormQuenMatKhau() {
        return "admin/user/quenMK";
    }

    // Xử lý gửi mã xác thực qua email
    @PostMapping("/forgot-password/send-code")
    public String sendVerificationCode(@RequestParam("email") String email,
                                       Model model,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        try {
            taiKhoanService.sendForgotPasswordCode(email, session);
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

    // Hiển thị trang xác nhận mã
    @GetMapping("/forgot-password/verify")
    public String showVerificationPage(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "admin/user/xacnhanQuenMK";
    }

    // Xử lý xác minh mã
    @PostMapping("/forgot-password/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestParam("email") String email,
                                                          @RequestParam("code") String code,
                                                          HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("Đang xác thực mã cho email: " + email + " với mã: " + code); // Debug

        if (taiKhoanService.verifyForgotPasswordCode(email, code, session)) {
            System.out.println("Xác thực thành công cho email: " + email); // Debug
            session.setAttribute("verifiedEmail", email);
            response.put("success", true);
            response.put("message", "Xác thực thành công!");
            return ResponseEntity.ok(response);
        } else {
            System.out.println("Xác thực thất bại cho email: " + email); // Debug
            response.put("success", false);
            response.put("message", "Mã xác thực không đúng hoặc đã hết hạn. Vui lòng thử lại.");
            return ResponseEntity.ok(response);
        }
    }

    // Hiển thị form nhập mật khẩu mới
    @GetMapping("/forgot-password/new-password")
    public String showNewPasswordForm(Model model, HttpSession session) {
        String email = (String) session.getAttribute("verifiedEmail");
        if (email == null) {
            return "redirect:/forgot-password";
        }
        model.addAttribute("email", email);
        return "admin/user/taoMatKhauMoi";
    }

    // Xử lý lưu mật khẩu mới
    @PostMapping("/forgot-password/change-password")
    public String changePassword(@RequestParam("email") String email,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Model model,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("email", email);
            model.addAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
            return "admin/user/taoMatKhauMoi";
        }

        try {
            taiKhoanService.changePassword(email, newPassword);
            session.removeAttribute("verifiedEmail");
            redirectAttributes.addFlashAttribute("success",
                    "Mật khẩu đã được thay đổi thành công! Vui lòng đăng nhập với mật khẩu mới.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("email", email);
            model.addAttribute("error", e.getMessage());
            return "admin/user/taoMatKhauMoi";
        }
    }
}
