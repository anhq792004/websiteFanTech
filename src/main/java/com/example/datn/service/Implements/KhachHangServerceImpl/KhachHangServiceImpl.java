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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KhachHangServiceImpl implements KhachHangService {

    private final KhachHangRepo khachHangRepo;

    private final DiaChiRepo diaChiRepo;

    private final ChucVuRepo chucVuRepo;

    private final TaiKhoanRepo taiKhoanRepo;


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

    @Override
    public KhachHang addKH(AddKhachHangRequest request) {
        TaiKhoan taiKhoan = new TaiKhoan();
        ChucVu chucVu = chucVuRepo.findByViTri("User") // Tìm ChucVu có viTri = "User"
                .orElseGet(() -> {
                    ChucVu newChucVu = new ChucVu();
                    newChucVu.setViTri("User");
                    return chucVuRepo.save(newChucVu); // Tạo mới nếu không tìm thấy
                });
        taiKhoan.setChucVu(chucVu); // Gán ChucVu vào TaiKhoan
        taiKhoan.setEmail(request.getEmail());
        taiKhoan.setNgayTao(new Date());
        taiKhoanRepo.save(taiKhoan); // Lưu TaiKhoan

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

        DiaChi diaChi = new DiaChi();
        diaChi.setKhachHang(khachHang);
        diaChi.setHuyen(request.getQuanHuyen());
        diaChi.setTinh(request.getTinhThanhPho());
        diaChi.setXa(request.getXaPhuong());
        diaChi.setSoNhaNgoDuong(request.getSoNhaNgoDuong());
        diaChiRepo.save(diaChi);
        return null;
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
