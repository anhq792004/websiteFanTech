package com.example.datn.controller.TaiKhoanController;

import com.example.datn.entity.TaiKhoan;
import com.example.datn.repository.TaiKhoanRepo;
import com.example.datn.service.taiKhoanService.TaiKhoanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class LoginController {

    @Autowired
    private TaiKhoanRepo taiKhoanRepo;
    @Autowired
    private TaiKhoanService taiKhoanService;

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String registered,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (registered != null) {
            model.addAttribute("registrationSuccess", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
        }
        if (logout != null) {
            model.addAttribute("logoutSuccess", "Đăng xuất thành công!");
        }
        return "admin/user/login";
    }

    @GetMapping("/login-success")
    public String loginSuccess(HttpSession session, Principal principal) {
        if (principal != null) {
            try {
                TaiKhoan taiKhoan = taiKhoanRepo.findByEmail(principal.getName());
                if (taiKhoan != null) {
                    // ✅ Đồng bộ tên thuộc tính session
                    session.setAttribute("currentUser", taiKhoan);
                    session.setAttribute("isLoggedIn", true);

                    String viTri = taiKhoan.getChucVu() != null ? taiKhoan.getChucVu().getViTri() : "";

                    System.out.println("Login success: " + taiKhoan.getEmail() + ", Role: " + viTri);

                    if ("ADMIN".equalsIgnoreCase(viTri) || "EMPLOYE".equalsIgnoreCase(viTri)) {
                        return "redirect:/sale/index";
                    } else {
                        return "redirect:/fanTech/index";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/login?error";
    }
}
