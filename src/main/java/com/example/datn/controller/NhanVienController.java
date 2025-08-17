package com.example.datn.controller;

import com.example.datn.dto.request.AddNhanVienRequest;
import com.example.datn.dto.request.UpdateNhanVienRequest;
import com.example.datn.entity.ChucVu;
import com.example.datn.entity.DiaChi;
import com.example.datn.entity.NhanVien.NhanVien;
import com.example.datn.entity.TaiKhoan;
import com.example.datn.repository.DiaChiRepo;
import com.example.datn.repository.TaiKhoanRepo;
import com.example.datn.service.ChucVuService.ChucVuService;
import com.example.datn.service.NhanVienService.NhanVienService;
import com.example.datn.service.taiKhoanService.TaiKhoanService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private final TaiKhoanRepo taiKhoanRepo;
    private final TaiKhoanService taiKhoanService;

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
    public ResponseEntity<?> add(@RequestParam("ten") String ten,
                                 @RequestParam("canCuocCongDan") String canCuocCongDan,
                                 @RequestParam("email") String email,
                                 @RequestParam("soDienThoai") String soDienThoai,
                                 @RequestParam("ngaySinh") String ngaySinh,
                                 @RequestParam("gioiTinh") String gioiTinh,
                                 @RequestParam("tinhThanhPho") String tinhThanhPho,
                                 @RequestParam("quanHuyen") String quanHuyen,
                                 @RequestParam("xaPhuong") String xaPhuong,
                                 @RequestParam("soNhaNgoDuong") String soNhaNgoDuong,
                                 @RequestParam("chucVu") String chucVu,
                                 @RequestParam(value = "hinhAnh", required = false) MultipartFile hinhAnh) {
        try {
            AddNhanVienRequest nhanVien = new AddNhanVienRequest();
            nhanVien.setTen(ten);
            nhanVien.setCanCuocCongDan(canCuocCongDan);
            nhanVien.setEmail(email);
            nhanVien.setSoDienThoai(soDienThoai);

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date ngaySinhDate = sdf.parse(ngaySinh);
                nhanVien.setNgaySinh(ngaySinhDate);
            } catch (ParseException e) {
                return ResponseEntity.badRequest().body("Định dạng ngày sinh không hợp lệ (dd/MM/yyyy)");
            }

            nhanVien.setGioiTinh(gioiTinh);
            nhanVien.setTinhThanhPho(tinhThanhPho);
            nhanVien.setQuanHuyen(quanHuyen);
            nhanVien.setXaPhuong(xaPhuong);
            nhanVien.setSoNhaNgoDuong(soNhaNgoDuong);
            nhanVien.setChucVu(chucVu);

            nhanVienService.addNhanVien(nhanVien, hinhAnh);
            return ResponseEntity.ok().body("Thêm thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> updateNhanVien(@RequestParam("id") Long id,
                                                 @RequestParam("ten") String ten,
                                                 @RequestParam("soDienThoai") String soDienThoai,
                                                 @RequestParam("canCuocCongDan") String canCuocCongDan,
                                                 @RequestParam("ngaySinh") String ngaySinh,
                                                 @RequestParam("gioiTinh") String gioiTinh,
                                                 @RequestParam("tinhThanhPho") String tinhThanhPho,
                                                 @RequestParam("quanHuyen") String quanHuyen,
                                                 @RequestParam("xaPhuong") String xaPhuong,
                                                 @RequestParam("soNhaNgoDuong") String soNhaNgoDuong,
                                                 @RequestParam("chucVu") String chucVu,
                                                 @RequestParam(value = "hinhAnh", required = false) MultipartFile hinhAnh) {
        try {
            UpdateNhanVienRequest request = new UpdateNhanVienRequest();
            request.setId(id);
            request.setTen(ten);
            request.setSoDienThoai(soDienThoai);
            request.setCanCuocCongDan(canCuocCongDan);

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date ngaySinhDate = sdf.parse(ngaySinh);
                request.setNgaySinh(ngaySinhDate);
            } catch (ParseException e) {
                return ResponseEntity.badRequest().body("Định dạng ngày sinh không hợp lệ (dd/MM/yyyy)");
            }

            request.setGioiTinh(gioiTinh);
            request.setTinhThanhPho(tinhThanhPho);
            request.setQuanHuyen(quanHuyen);
            request.setXaPhuong(xaPhuong);
            request.setSoNhaNgoDuong(soNhaNgoDuong);
            request.setChucVu(chucVu);

            nhanVienService.updateNhanVien(request, hinhAnh);
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cập nhật thất bại: " + e.getMessage());
        }
    }

    @GetMapping("/detail")
    public String detail(@RequestParam Long id, Model model) {
        NhanVien nhanVien = nhanVienService.findNhanVienById(id);
        String ngaySinhFormatted = "";
        if (nhanVien.getNgaySinh() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            ngaySinhFormatted = sdf.format(nhanVien.getNgaySinh());
        }
        model.addAttribute("ngaySinhFormatted", ngaySinhFormatted);
        model.addAttribute("nhanVien", nhanVien);
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

    @PostMapping("/check-email")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = taiKhoanRepo.existsByEmail(email);
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/changePassword")
    public String changePassword(Model model, HttpSession session) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("userEmail", currentUser.getEmail()); // Thêm email vào model
        model.addAttribute("showVerificationForm", false);
        model.addAttribute("showPasswordForm", false);
        return "admin/nhan_vien/change-password"; // Đảm bảo đường dẫn đúng
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
            model.addAttribute("userEmail", currentUser.getEmail());
            model.addAttribute("showVerificationForm", true);
            model.addAttribute("showPasswordForm", false);
            return "admin/nhan_vien/change-password"; // Sửa đường dẫn
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể gửi mã xác thực. Vui lòng thử lại!");
            return "redirect:/admin/nhan-vien/changePassword";
        }
    }

    @PostMapping("/change-password/verify")
    public String verifyCode(@RequestParam("code") String code, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        boolean isVerified = taiKhoanService.verifyForgotPasswordCode(currentUser.getEmail(), code, session);
        model.addAttribute("userEmail", currentUser.getEmail());

        if (isVerified) {
            model.addAttribute("message", "Mã xác thực hợp lệ!");
            model.addAttribute("showVerificationForm", false);
            model.addAttribute("showPasswordForm", true);
            return "admin/nhan_vien/change-password";
        } else {
            model.addAttribute("errorMessage", "Mã xác thực không đúng!"); // Change to a different attribute name
            model.addAttribute("showVerificationForm", true);
            model.addAttribute("showPasswordForm", false);
            return "admin/nhan_vien/change-password";
        }
    }

    @PostMapping("/change-password/update")
    public String updatePassword(@RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 HttpSession session,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Validate mật khẩu mới không trống
        if (newPassword == null || newPassword.trim().isEmpty()) {
            model.addAttribute("error", "Mật khẩu mới không được để trống!");
            model.addAttribute("userEmail", currentUser.getEmail());
            model.addAttribute("showVerificationForm", false);
            model.addAttribute("showPasswordForm", true);
            return "admin/nhan_vien/change-password";
        }

        // Validate tối thiểu 3 ký tự
        if (newPassword.length() < 3) {
            model.addAttribute("error", "Mật khẩu mới phải có ít nhất 3 ký tự!");
            model.addAttribute("userEmail", currentUser.getEmail());
            model.addAttribute("showVerificationForm", false);
            model.addAttribute("showPasswordForm", true);
            return "admin/nhan_vien/change-password";
        }

        // Validate không có ký tự đặc biệt (chỉ cho phép chữ cái, số)
        if (!newPassword.matches("^[a-zA-Z0-9]+$")) {
            model.addAttribute("error", "Mật khẩu mới không được chứa ký tự đặc biệt! Chỉ cho phép chữ cái và số.");
            model.addAttribute("userEmail", currentUser.getEmail());
            model.addAttribute("showVerificationForm", false);
            model.addAttribute("showPasswordForm", true);
            return "admin/nhan_vien/change-password";
        }

        // Validate xác nhận mật khẩu
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            model.addAttribute("error", "Xác nhận mật khẩu không được để trống!");
            model.addAttribute("userEmail", currentUser.getEmail());
            model.addAttribute("showVerificationForm", false);
            model.addAttribute("showPasswordForm", true);
            return "admin/nhan_vien/change-password";
        }

        // Validate mật khẩu xác nhận khớp
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            model.addAttribute("userEmail", currentUser.getEmail());
            model.addAttribute("showVerificationForm", false);
            model.addAttribute("showPasswordForm", true);
            return "admin/nhan_vien/change-password";
        }

        try {
            taiKhoanService.changePassword(currentUser.getEmail(), newPassword);
            redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công!");
            session.removeAttribute("verifiedEmail");
            return "redirect:/admin/nhan-vien/changePassword";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi đổi mật khẩu: " + e.getMessage());
            model.addAttribute("userEmail", currentUser.getEmail());
            model.addAttribute("showVerificationForm", false);
            model.addAttribute("showPasswordForm", true);
            return "admin/nhan_vien/change-password";
        }
    }

}