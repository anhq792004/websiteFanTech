package com.example.datn.controller.TaiKhoanController;

import com.example.datn.dto.DangKyDto;
import com.example.datn.repository.TaiKhoanRepo;
import com.example.datn.service.taiKhoanService.TaiKhoanService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DangKyController {
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private TaiKhoanRepo taiKhoanRepo;

    @GetMapping("/register")
    public String showDangKyForm(Model model) {
        model.addAttribute("dangKyDto", new DangKyDto());
        return "admin/user/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("dangKyDto") DangKyDto dangKyDto,
                           BindingResult result,
                           Model model,
                           HttpSession session) {
        if (result.hasErrors()) {
            return "admin/user/register";
        }

        if (!dangKyDto.getMatKhau().equals(dangKyDto.getXacNhanMatKhau())) {
            model.addAttribute("error", "Mật khẩu và xác nhận mật khẩu không khớp");
            return "admin/user/register";
        }

        try {
            String verificationCode = taiKhoanService.dangKyTaiKhoan(dangKyDto);
            session.setAttribute("verificationCode", verificationCode);
            session.setAttribute("dangKyDto", dangKyDto);
            return "redirect:/verify-account?email=" + dangKyDto.getEmail();
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/user/register";
        }
    }
}
