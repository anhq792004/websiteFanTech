package com.example.datn.service.taiKhoanService;

import com.example.datn.entity.KhachHang;
import com.example.datn.entity.NhanVien.NhanVien;
import com.example.datn.entity.TaiKhoan;
import com.example.datn.repository.KhachHangRepo.KhachHangRepo;
import com.example.datn.repository.NhanVienRepo;
import com.example.datn.repository.TaiKhoanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private TaiKhoanRepo taiKhoanRepository;

    @Autowired
    private NhanVienRepo nhanVienRepository;

    @Autowired
    private KhachHangRepo khachHangRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        TaiKhoan taiKhoan = taiKhoanRepository.findByEmail(email);

        if (taiKhoan == null) {
            throw new UsernameNotFoundException("Không tìm thấy tài khoản với email: " + email);
        }

        // Kiểm tra trạng thái của tài khoản
        if (taiKhoan.getTrangThai() == null || !taiKhoan.getTrangThai()) {
            throw new UsernameNotFoundException("Tài khoản đã bị vô hiệu hóa");
        }

        // Kiểm tra trạng thái của KhachHang
        KhachHang khachHang = khachHangRepository.findByTaiKhoan(taiKhoan);
        if (khachHang != null && (khachHang.getTrangThai() == null || !khachHang.getTrangThai())) {
            throw new UsernameNotFoundException("Tài khoản khách hàng đã bị vô hiệu hóa");
        }

        // Kiểm tra trạng thái của NhanVien
        Optional<NhanVien> nhanVienOpt = nhanVienRepository.findByTaiKhoan(taiKhoan);
        if (nhanVienOpt.isPresent() && (nhanVienOpt.get().getTrangThai() == null || !nhanVienOpt.get().getTrangThai())) {
            throw new UsernameNotFoundException("Tài khoản nhân viên đã bị vô hiệu hóa");
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Thêm vai trò dựa trên ChucVu
        if (taiKhoan.getChucVu() != null && taiKhoan.getChucVu().getViTri() != null) {
            String role = "ROLE_" + taiKhoan.getChucVu().getViTri().toUpperCase();
            authorities.add(new SimpleGrantedAuthority(role));
        } else {
            // Mặc định là ROLE_USER nếu không có chức vụ
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new User(taiKhoan.getEmail(), taiKhoan.getMatKhau(), authorities);
    }
}
