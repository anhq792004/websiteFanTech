package com.example.datn.controller.webController;

import com.example.datn.dto.request.AddDiaChiRequest;
import com.example.datn.dto.request.UpdateDiaChiRequest;
import com.example.datn.dto.request.UpdateInforKhachHangRequest;
import com.example.datn.dto.request.UpdateInforRequest;
import com.example.datn.entity.DiaChi;
import com.example.datn.entity.HoaDon.HoaDon;
import com.example.datn.entity.HoaDon.HoaDonChiTiet;
import com.example.datn.entity.HoaDon.LichSuHoaDon;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.SanPham.SanPhamChiTiet;
import com.example.datn.entity.TaiKhoan;
import com.example.datn.repository.DiaChiRepo;
import com.example.datn.service.DiaChiService;
import com.example.datn.service.HoaDonService.HoaDonService;
import com.example.datn.service.KhachHangService.KhachHangService;
import com.example.datn.service.PhieuGiamGiaSercvice;
import com.example.datn.service.taiKhoanService.TaiKhoanService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final KhachHangService khachHangService;

    private final TaiKhoanService taiKhoanService;

    private final HoaDonService hoaDonService;

    private final DiaChiRepo diaChiRepo;

    private final DiaChiService diaChiService;

    private final PhieuGiamGiaSercvice phieuGiamGiaService;

    /**
     * Hiển thị trang thông tin cá nhân
     */
    @GetMapping
    public String showProfile(Model model, HttpSession session) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");

        if (currentUser == null) {
            return "redirect:/login";
        }

        KhachHang khachHang = khachHangService.findByTaiKhoan(currentUser);

        // ✅ Format ngày sinh nếu có
        String ngaySinhFormatted = "";
        if (khachHang.getNgaySinh() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            ngaySinhFormatted = sdf.format(khachHang.getNgaySinh());
        }

        model.addAttribute("khachHang", khachHang);
        model.addAttribute("ngaySinhFormatted", ngaySinhFormatted); // ✅ Truyền thêm biến này vào model
        return "user/infor/profile";
    }


    /**
     * Hiển thị trang chỉnh sửa thông tin
     */
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> update(@RequestParam("idKH") Long idKH,
                                          @RequestParam("ten") String ten,
                                          @RequestParam("soDienThoai") String soDienThoai,
                                          @RequestParam("ngaySinh") String ngaySinh,
                                          @RequestParam("gioiTinh") String gioiTinh,
                                          @RequestParam(value = "hinhAnh", required = false) MultipartFile hinhAnh) {
        try {
            // Tạo UpdateInforKhachHangRequest từ các tham số
            UpdateInforKhachHangRequest request = new UpdateInforKhachHangRequest();
            request.setIdKH(idKH);
            request.setTen(ten);
            request.setSoDienThoai(soDienThoai);
            
            // Parse ngày sinh từ String sang Date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date ngaySinhDate = sdf.parse(ngaySinh);
                request.setNgaySinh(ngaySinhDate);
            } catch (ParseException e) {
                return ResponseEntity.badRequest().body("Định dạng ngày sinh không hợp lệ (dd/MM/yyyy)");
            }
            
            request.setGioiTinh(gioiTinh);
            
            khachHangService.updateInforKhachHang(request, hinhAnh);
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cập nhật thất bại: " + e.getMessage());
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
        model.addAttribute("khachHang", khachHang);

        return "user/infor/ordered";
    }

    @GetMapping("/ordered-detail")
    public String detail(@RequestParam Long id,
                         Model model,
                         HttpSession session) {
        Optional<HoaDon> hoaDonOptional = hoaDonService.findHoaDonById(id);
        HoaDon hoaDon = hoaDonOptional.orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn"));
        model.addAttribute("hoaDon", hoaDon);
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        KhachHang khachHang = khachHangService.findByTaiKhoan(currentUser);
        //
        List<HoaDonChiTiet> listHDCT = hoaDonService.listHoaDonChiTiets(id);
        model.addAttribute("listHDCT", listHDCT);
        //
        List<HoaDon> hoaDons = hoaDonService.getHoaDonByIdKH(khachHang.getId());
        model.addAttribute("hoaDons", hoaDons);
        //timeline lịch sử hóa đơn
        List<LichSuHoaDon> lichSuHoaDonList = hoaDonService.lichSuHoaDonList(id);
        model.addAttribute("lichSuHoaDonList", lichSuHoaDonList);

        //tong so luong san pham
        Integer tongSoLuong = hoaDonService.tongSoLuong(id);
        model.addAttribute("tongSoLuong", tongSoLuong);

        model.addAttribute("khachHang", khachHang);
        return "user/infor/orderedDetail";
    }

    @PostMapping("/updateInfor")
    @ResponseBody
    public ResponseEntity<?> updateInfor( @RequestBody UpdateInforRequest request) {
        hoaDonService.updateInfor(request);
        return ResponseEntity.ok("Cập nhật thông tin thành công");
    }

    @PostMapping("/huy")
    @ResponseBody
    public ResponseEntity<String> huy(@RequestParam("id") Long id,
                                      @RequestParam("ghiChu") String ghiChu) {
        hoaDonService.huyOnl(id, ghiChu);
//        hoaDonService.hoanSoLuongSanPham(id);
        return ResponseEntity.ok("Hóa đơn đã được hủy !");
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

    /**
     * Đặt địa chỉ mặc định cho khách hàng hiện tại (client)
     */
    @PostMapping("/set-default-address")
    @ResponseBody
    public ResponseEntity<?> setDefaultAddressClient(@RequestParam Long diaChiId, HttpSession session) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa đăng nhập");
        }
        KhachHang khachHang = khachHangService.findByTaiKhoan(currentUser);
        DiaChi diaChi = diaChiRepo.findById(diaChiId).orElse(null);
        if (diaChi == null || !diaChi.getKhachHang().getId().equals(khachHang.getId())) {
            return ResponseEntity.badRequest().body("Địa chỉ không hợp lệ");
        }
        khachHang.setDiaChiMacDinhId(diaChiId);
        khachHangService.save(khachHang);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Cập nhật địa chỉ mặc định thành công");
        return ResponseEntity.ok(res);
    }

    //phieu giam gia
    @GetMapping("/coupon")
    public String coupon(Model model, HttpSession session) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        KhachHang khachHang = khachHangService.findByTaiKhoan(currentUser);
        List<Map<String, Object>> availableVouchers = phieuGiamGiaService.getAllVouchers(khachHang);
        model.addAttribute("khachHang", khachHang);
        model.addAttribute("availableVouchers", availableVouchers);

        return "user/infor/coupon";
    }

    @GetMapping("/change-password")
    public String changePassword(Model model, HttpSession session) {
        TaiKhoan currentUser = (TaiKhoan) session.getAttribute("currentUser");
        KhachHang khachHang = khachHangService.findByTaiKhoan(currentUser);
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("khachHang", khachHang);
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
