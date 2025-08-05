package com.example.datn.service.Implements.NhanVienServiceImpl;

import com.example.datn.dto.request.AddNhanVienRequest;
import com.example.datn.dto.request.UpdateNhanVienRequest;
import com.example.datn.entity.ChucVu;
import com.example.datn.entity.DiaChi;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.NhanVien.NhanVien;
import com.example.datn.entity.TaiKhoan;
import com.example.datn.repository.ChucVuRepo;
import com.example.datn.repository.DiaChiRepo;
import com.example.datn.repository.NhanVienRepo;
import com.example.datn.repository.TaiKhoanRepo;
import com.example.datn.service.NhanVienService.NhanVienService;
import com.example.datn.service.taiKhoanService.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NhanVienServicelmpl implements NhanVienService {
    private final NhanVienRepo nhanVienRepo;
    private final ChucVuRepo chucVuRepo;
    private final TaiKhoanRepo taiKhoanRepo;
    private final DiaChiRepo diaChiRepo;

    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<NhanVien> findAll(String keyword, Boolean trangThai, Pageable pageable) {
        if (trangThai == null) {
            return nhanVienRepo.searchNhanVienKhongCoTrangThai(keyword, pageable);
        }
        return nhanVienRepo.searchNhanVien(keyword, trangThai, pageable);
    }

    @Override
    public NhanVien findNhanVienById(Long id) {
        return nhanVienRepo.findById(id).orElse(null);    }

    @Override
    public void updateNhanVien(UpdateNhanVienRequest request) {
        Optional<NhanVien> nhanVienOptional = nhanVienRepo.findById(request.getId());

        if (nhanVienOptional.isPresent() ){
            NhanVien nhanVien = nhanVienOptional.get();

            nhanVien.setTen(request.getTen());
            nhanVien.setSoDienThoai(request.getSoDienThoai());
            nhanVien.setNgaySinh(request.getNgaySinh());
            nhanVien.setGioiTinh(request.getGioiTinh());
            nhanVien.setCanCuocCongDan(request.getCanCuocCongDan());
            DiaChi dc = nhanVien.getDiaChi();
            if (dc == null) {
                dc = new DiaChi();
            }

            dc.setTinh(request.getTinhThanhPho());
            dc.setHuyen(request.getQuanHuyen());
            dc.setXa(request.getXaPhuong());
            dc.setSoNhaNgoDuong(request.getSoNhaNgoDuong());

            diaChiRepo.save(dc);
            nhanVien.setDiaChi(dc);

            nhanVienRepo.save(nhanVien);
        }
    }


    @Override
    public boolean thayDoiTrangThaiNhanVien(Long id) {
        Optional<NhanVien> nhanVienOptional=nhanVienRepo.findById(id);
        if (nhanVienOptional.isPresent()){
            NhanVien nhanVien= nhanVienOptional.get();
            nhanVien.setTrangThai(nhanVien.getTrangThai()==Boolean.FALSE?Boolean.TRUE:Boolean.FALSE);
            nhanVienRepo.save(nhanVien);
            return true;
        }
        return false;
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
    public void addNhanVien(AddNhanVienRequest request) {
        // Kiểm tra email đã tồn tại
        if (taiKhoanRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng. Vui lòng chọn email khác!");
        }

        TaiKhoan taiKhoan = new TaiKhoan();
        ChucVu chucVu = chucVuRepo.findByViTri(request.getChucVu())
                .orElseThrow(() -> new RuntimeException("Chức vụ không tồn tại: " + request.getChucVu()));
        taiKhoan.setMa("TK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        taiKhoan.setChucVu(chucVu);
        taiKhoan.setEmail(request.getEmail());
        taiKhoan.setNgayTao(new Date());
        taiKhoan.setTrangThai(true);

        // Tạo và mã hóa mật khẩu ngẫu nhiên
        String randomPassword = generateRandomPassword(8);
        taiKhoan.setMatKhau(passwordEncoder.encode(randomPassword)); // Mã hóa mật khẩu
        taiKhoanRepo.save(taiKhoan);

        DiaChi diaChi = new DiaChi();
        diaChi.setHuyen(request.getQuanHuyen());
        diaChi.setTinh(request.getTinhThanhPho());
        diaChi.setXa(request.getXaPhuong());
        diaChi.setSoNhaNgoDuong(request.getSoNhaNgoDuong());
        diaChiRepo.save(diaChi);

        NhanVien nhanVien = new NhanVien();
        nhanVien.setMa(generateCode());
        nhanVien.setTaiKhoan(taiKhoan);
        nhanVien.setChucVu(chucVu);
        nhanVien.setCanCuocCongDan(request.getCanCuocCongDan());
        nhanVien.setTen(request.getTen());
        nhanVien.setSoDienThoai(request.getSoDienThoai());
        nhanVien.setNgaySinh(request.getNgaySinh());
        nhanVien.setGioiTinh(request.getGioiTinh());
        nhanVien.setNgayTao(LocalDateTime.now());
        nhanVien.setTrangThai(true);
        nhanVien.setDiaChi(diaChi);
        nhanVienRepo.save(nhanVien);

        // Gửi email chứa mật khẩu
        try {
            String subject = "Tài khoản nhân viên đã được tạo";
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
    }
    @Override
    public String generateCode() {
        Long count = nhanVienRepo.count(); // Số lượng hóa đơn trong DB
        // Tạo mã hóa đơn với tiền tố "HD" và số thứ tự
        return String.format("NV%03d", count + 1); // VD: HD001, HD002
    }

    @Override
    public ResponseEntity<?> changeStatus(Long id) {
        NhanVien nhanVien = nhanVienRepo.findById(id).orElse(null);
        if (nhanVien == null) {
            return ResponseEntity.badRequest().body("Không tìm nhân viên.");
        }
        nhanVien.setTrangThai(!nhanVien.getTrangThai());
        nhanVienRepo.save(nhanVien);
        return ResponseEntity.ok("Cập nhật trạng thái thành công.");
    }
}
