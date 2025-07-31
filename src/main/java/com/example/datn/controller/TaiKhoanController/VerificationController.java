package com.example.datn.controller.TaiKhoanController;

import com.example.datn.service.taiKhoanService.TaiKhoanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
public class VerificationController {
    @Autowired
    private TaiKhoanService taiKhoanService;

    @GetMapping("/verify-account")
    public String showVerificationPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "admin/user/xacnhan";
    }

    @PostMapping("/verify-account")
    public String verifyAccount(@RequestParam String email, @RequestParam String code, Model model, HttpSession session) {
        boolean isVerified = taiKhoanService.confirmRegistration(email, code, session);
        if (isVerified) {
            return "redirect:/verify-account?verified=true&email=" + email; // Thêm verified=true để kích hoạt thông báo
        } else {
            model.addAttribute("error", "Mã xác nhận không đúng");
            model.addAttribute("email", email);
            return "admin/user/xacnhan";
        }
    }
}
