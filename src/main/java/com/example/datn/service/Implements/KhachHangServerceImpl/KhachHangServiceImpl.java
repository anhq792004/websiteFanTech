package com.example.datn.service.Implements.KhachHangServerceImpl;

import com.example.datn.dto.request.AddKhachHangRequest;
import com.example.datn.dto.request.UpdateInforKhachHangRequest;
import com.example.datn.entity.ChucVu;
import com.example.datn.entity.DiaChi;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.TaiKhoan;
import com.example.datn.repository.ChucVuRepo;
import com.example.datn.repository.DiaChiRepo;
import com.example.datn.repository.KhachHangRepo.KhachHangRepo;
import com.example.datn.repository.TaiKhoanRepo;
import com.example.datn.service.KhachHangService.KhachHangService;
import com.example.datn.service.taiKhoanService.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KhachHangServiceImpl implements KhachHangService {

    private final KhachHangRepo khachHangRepo;

    private final DiaChiRepo diaChiRepo;

    private final ChucVuRepo chucVuRepo;

    private final TaiKhoanRepo taiKhoanRepo;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;


    @Override
    public Page<KhachHang> findAll(String keyword, Boolean trang_thai, Pageable pageable) {
        if (trang_thai == null) {
            return khachHangRepo.searchKhachHangKhongCoTrangThai(keyword, pageable);
        }
        return khachHangRepo.searchKhachHang(keyword, trang_thai, pageable);
    }

    @Override
    public KhachHang findById(Long id) {
        return khachHangRepo.findById(id).orElse(null);
    }

    // Phương thức tạo mật khẩu ngẫu nhiên
    private String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }

    @Override
    public KhachHang addKH(AddKhachHangRequest request) {
        // Tạo tài khoản
        TaiKhoan taiKhoan = new TaiKhoan();
        ChucVu chucVu = chucVuRepo.findByViTri("User")
                .orElseGet(() -> {
                    ChucVu newChucVu = new ChucVu();
                    newChucVu.setViTri("User");
                    return chucVuRepo.save(newChucVu);
                });
        taiKhoan.setMa("TK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        taiKhoan.setChucVu(chucVu);
        taiKhoan.setEmail(request.getEmail());
        taiKhoan.setNgayTao(new Date());
        taiKhoan.setTrangThai(true);

        // Tạo và mã hóa mật khẩu ngẫu nhiên
        String randomPassword = generateRandomPassword(8);
        taiKhoan.setMatKhau(passwordEncoder.encode(randomPassword)); // Sử dụng PasswordEncoder
        taiKhoanRepo.save(taiKhoan);

        // Tạo khách hàng
        KhachHang khachHang = new KhachHang();
        khachHang.setMa(generateCode());
        khachHang.setTaiKhoan(taiKhoan);
        khachHang.setTen(request.getTen());
        khachHang.setGioiTinh(request.getGioiTinh());
        khachHang.setNgayTao(new Date());
        khachHang.setSoDienThoai(request.getSoDienThoai());
        khachHang.setNgaySinh(request.getNgaySinh());
        khachHang.setTrangThai(true);
        khachHangRepo.save(khachHang);

        // Tạo địa chỉ
        DiaChi diaChi = new DiaChi();
        diaChi.setKhachHang(khachHang);
        diaChi.setHuyen(request.getQuanHuyen());
        diaChi.setTinh(request.getTinhThanhPho());
        diaChi.setXa(request.getXaPhuong());
        diaChi.setSoNhaNgoDuong(request.getSoNhaNgoDuong());
        diaChiRepo.save(diaChi);

        // Gửi email chứa mật khẩu
        try {
            String subject = "Tài khoản của bạn đã được tạo";
            String content = "Chào " + request.getTen() + ",<br><br>" +
                    "Tài khoản của bạn đã được tạo thành công. Dưới đây là thông tin đăng nhập:<br>" +
                    "Email: " + request.getEmail() + "<br>" +
                    "Mật khẩu: " + randomPassword + "<br><br>" +
                    "Vui lòng đổi mật khẩu sau khi đăng nhập lần đầu.<br>" +
                    "Trân trọng,<br>Đội ngũ hỗ trợ";
            emailService.sendEmail(request.getEmail(), subject, content);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return khachHang;
    }

    @Override
    public void updateInforKhachHang(UpdateInforKhachHangRequest request) {
        Optional<KhachHang> khachHangOptional = khachHangRepo.findById(request.getIdKH());

        if (khachHangOptional.isPresent() ){
            KhachHang khachHang = khachHangOptional.get();
            khachHang.setTen(request.getTen());
            khachHang.setSoDienThoai(request.getSoDienThoai());
            khachHang.setNgaySinh(request.getNgaySinh());
            khachHang.setGioiTinh(request.getGioiTinh());

            khachHangRepo.save(khachHang);
        }
    }

    @Override
    public String generateCode() {
        Long count = khachHangRepo.count(); // Số lượng hóa đơn trong DB
        // Tạo mã hóa đơn với tiền tố "HD" và số thứ tự
        return String.format("KH%03d", count + 1); // VD: HD001, HD002
    }

    @Override
    public KhachHang findByTaiKhoan(TaiKhoan taiKhoan) {
        return khachHangRepo.findByTaiKhoan(taiKhoan);
    }

    @Override
    public KhachHang save(KhachHang khachHang) {
        return khachHangRepo.save(khachHang);
    }

    @Override
    public long count() {
        return khachHangRepo.count();
    }

    @Override
    public ResponseEntity<?> changeStatus(Long id) {
        KhachHang khachHang = khachHangRepo.findById(id).orElse(null);
        if (khachHang == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy khách hàng.");
        }
        khachHang.setTrangThai(!khachHang.getTrangThai());
        khachHangRepo.save(khachHang);
        return ResponseEntity.ok("Cập nhật trạng thái thành công.");
    }

}
