package com.example.datn.service.taiKhoanService;

import com.example.datn.dto.DangKyDto;
import com.example.datn.entity.ChucVu;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.TaiKhoan;
import com.example.datn.repository.ChucVuRepo;
import com.example.datn.repository.KhachHangRepo.KhachHangRepo;
import com.example.datn.repository.TaiKhoanRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
public class TaiKhoanService {
    @Autowired
    private TaiKhoanRepo taiKhoanRepository;

    @Autowired
    private ChucVuRepo chucVuRepository;

    @Autowired
    private KhachHangRepo khachHangRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public boolean emailDaTonTai(String email) {
        return taiKhoanRepository.findByEmail(email) != null;
    }

    public String dangKyTaiKhoan(DangKyDto dangKyDto) {
        // Kiểm tra email đã tồn tại
        if (emailDaTonTai(dangKyDto.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        // Tạo mã xác nhận ngẫu nhiên
        String verificationCode = generateVerificationCode();

        // Chuẩn bị dữ liệu tài khoản nhưng chưa lưu
        String noidungEmail =
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 5px;'>" +
                        "<h2 style='color: #333; text-align: center;'>Xác nhận đăng ký tài khoản VinFan</h2>" +
                        "<p>Kính gửi Quý khách,</p>" +
                        "<p>Cảm ơn bạn đã đăng ký tài khoản tại FanTech. Vui lòng sử dụng mã xác nhận dưới đây để hoàn tất đăng ký:</p>" +
                        "<h3 style='color: #007bff; text-align: center;'>" + verificationCode + "</h3>" +
                        "<p>Nếu bạn không thực hiện đăng ký này, vui lòng bỏ qua email này.</p>" +
                        "<p>Trân trọng,<br/>Đội ngũ VinFan</p>" +
                        "</div>";

        try {
            emailService.sendEmail(dangKyDto.getEmail(), "Xác nhận đăng ký tài khoản FanTech", noidungEmail);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể gửi email xác nhận. Vui lòng thử lại.");
        }

        return verificationCode;
    }

    public boolean confirmRegistration(String email, String code, HttpSession session) {
        String storedCode = (String) session.getAttribute("verificationCode");
        DangKyDto dangKyDto = (DangKyDto) session.getAttribute("dangKyDto");

        if (dangKyDto == null || storedCode == null || !storedCode.equals(code)) {
            return false;
        }

        // Chuẩn bị và lưu tài khoản
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setMa("TK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        taiKhoan.setEmail(dangKyDto.getEmail());
        taiKhoan.setMatKhau(passwordEncoder.encode(dangKyDto.getMatKhau()));
        taiKhoan.setNgayTao(new Date());
        taiKhoan.setTrangThai(true); // Kích hoạt ngay sau xác nhận
        taiKhoan.setVerificationCode(null);

        ChucVu chucVuUser = chucVuRepository.findByViTri("User")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chức vụ mặc định 'User'"));
        taiKhoan.setChucVu(chucVuUser);

        // Lưu tài khoản
        TaiKhoan savedTaiKhoan = taiKhoanRepository.save(taiKhoan);

        // Lưu thông tin khách hàng
        KhachHang khachHang = new KhachHang();
        khachHang.setMa("KH" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        khachHang.setNgayTao(new Date());
        khachHang.setTrangThai(true);
        khachHang.setTaiKhoan(savedTaiKhoan);
        khachHangRepository.save(khachHang);

        // Xóa session sau khi lưu thành công
        session.removeAttribute("verificationCode");
        session.removeAttribute("dangKyDto");

        return true;
    }

    public boolean verifyAccount(String email, String code) {
        TaiKhoan taiKhoan = taiKhoanRepository.findByEmail(email);
        if (taiKhoan == null || taiKhoan.getVerificationCode() == null) {
            return false;
        }

        if (taiKhoan.getVerificationCode().equals(code)) {
            taiKhoan.setTrangThai(true); // Kích hoạt tài khoản
            taiKhoan.setVerificationCode(null); // Xóa mã xác nhận
            taiKhoanRepository.save(taiKhoan);
            return true;
        }
        return false;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Tạo mã 6 chữ số
        return String.valueOf(code);
    }

    public boolean doiMatKhauVaThongBao(String email, String matKhauCu, String matKhauMoi) {
        TaiKhoan taiKhoan = taiKhoanRepository.findByEmail(email);
        if (taiKhoan == null) {
            return false;
        }

        if (!passwordEncoder.matches(matKhauCu, taiKhoan.getMatKhau())) {
            return false;
        }

        taiKhoan.setMatKhau(passwordEncoder.encode(matKhauMoi));
        taiKhoanRepository.save(taiKhoan);

        String noidungEmail =
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 5px;'>" +
                        "<h2 style='color: #333; text-align: center;'>Thông báo đổi mật khẩu</h2>" +
                        "<p>Kính gửi Quý khách,</p>" +
                        "<p>Mật khẩu tài khoản VinFan của bạn đã được thay đổi thành công.</p>" +
                        "<p>Nếu bạn không thực hiện thay đổi này, vui lòng liên hệ với chúng tôi ngay lập tức.</p>" +
                        "<p>Trân trọng,<br/>Đội ngũ VinFan</p>" +
                        "</div>";

        try {
            emailService.sendEmail(email, "Thông báo đổi mật khẩu VinFan", noidungEmail);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}
