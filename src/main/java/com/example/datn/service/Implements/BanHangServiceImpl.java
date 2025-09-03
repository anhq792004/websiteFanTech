package com.example.datn.service.Implements;

import com.example.datn.dto.request.LoaiHoaDonRequest;
import com.example.datn.entity.HoaDon.HoaDon;
import com.example.datn.entity.HoaDon.HoaDonChiTiet;
import com.example.datn.entity.HoaDon.LichSuHoaDon;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.NhanVien.NhanVien;
import com.example.datn.entity.SanPham.SanPhamChiTiet;
import com.example.datn.entity.TaiKhoan;
import com.example.datn.entity.PhieuGiamGia;
import com.example.datn.repository.HoaDonRepo.HoaDonChiTietRepo;
import com.example.datn.repository.HoaDonRepo.HoaDonRepo;
import com.example.datn.repository.HoaDonRepo.LichSuHoaDonRepo;
import com.example.datn.repository.NhanVienRepo;
import com.example.datn.repository.SanPhamRepo.SanPhamChiTietRepo;
import com.example.datn.repository.TaiKhoanRepo;
import com.example.datn.repository.PhieuGiamGiaRepo;
import com.example.datn.service.BanHang.BanHangService;
import com.example.datn.service.HoaDonService.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BanHangServiceImpl implements BanHangService {

    private final HoaDonService hoaDonService;
    private final HoaDonRepo hoaDonRepo;
    private final LichSuHoaDonRepo lichSuHoaDonRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final TaiKhoanRepo taiKhoanRepo;
    private final NhanVienRepo nhanVienRepo;
    private final PhieuGiamGiaRepo phieuGiamGiaRepo;

    private NhanVien getCurrentNhanVien() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            String username = null;

            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }

            TaiKhoan taiKhoan = taiKhoanRepo.findByEmail(username);
            if (taiKhoan == null) {
                throw new RuntimeException("Không tìm thấy tài khoản với email: " + username);
            }

            NhanVien nhanVien = nhanVienRepo.findByTaiKhoan(taiKhoan)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên cho tài khoản này"));
            return nhanVien;
        }
        throw new RuntimeException("Không tìm thấy thông tin người dùng đang đăng nhập");
    }

    @Transactional
    @Override
    public void taoHoaDonCho(HoaDon hoaDon) {
        List<HoaDon> listHoaDon = hoaDonService.findAll();
        NhanVien currentNhanVien = getCurrentNhanVien();
        int count = (int) listHoaDon.stream()
                .filter(sl -> sl.getTrangThai() == hoaDonService.getTrangThaiHoaDon().getHoaDonCho())
                .count();
        if (count >= 10) {
            // Thông báo khi số lượng hóa đơn chờ vượt quá 10
            throw new IllegalStateException("Số lượng hóa đơn chờ tối qua là 10");
        }
        hoaDon.setNhanVien(currentNhanVien);
        hoaDon.setMa(hoaDonService.generateOrderCode());
        hoaDon.setTrangThai(hoaDonService.getTrangThaiHoaDon().getHoaDonCho());
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setLoaiHoaDon(true);
        hoaDon.setNguoiTao(currentNhanVien.getTen());

        hoaDonRepo.saveAndFlush(hoaDon);

        //tao lich su hoa don
        LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
        lichSuHoaDon.setNhanVien(currentNhanVien);
        lichSuHoaDon.setHoaDon(hoaDon);
        lichSuHoaDon.setNgayTao(hoaDon.getNgayTao());
        lichSuHoaDon.setTrangThai(hoaDon.getTrangThai());
        lichSuHoaDon.setNguoiTao(currentNhanVien.getTen());

        lichSuHoaDonRepo.save(lichSuHoaDon);
    }

    @Override
    public List<HoaDon> findHoaDon() {
        return hoaDonRepo.findAll().stream().
                filter(loc -> loc.getTrangThai() == hoaDonService.getTrangThaiHoaDon()
                        .getHoaDonCho()).toList();
    }

    @Override
    public KhachHang getKhachHangLe(Long id) {
        return null;
    }

    @Override
    @Transactional
    public void thanhToan(Long idHD) {
        HoaDon hoaDon = hoaDonRepo.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại: " + idHD));
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepo.findByHoaDon_Id(hoaDon.getId());
        NhanVien currentNhanVien = getCurrentNhanVien();
        if (!hoaDon.getTrangThai().equals(hoaDonService.getTrangThaiHoaDon().getHoaDonCho())) {
            throw new RuntimeException("Hóa đơn đã được xử lý bởi người khác!");
        }
        for (HoaDonChiTiet hdct : listHDCT) {
            SanPhamChiTiet sp = hdct.getSanPhamChiTiet();
            // Chỉ kiểm tra số lượng, không cần cập nhật số lượng trong kho nữa
            // vì đã cập nhật khi thêm vào giỏ hàng
            if (hdct.getSoLuong() < 0) {
                throw new RuntimeException("Số lượng sản phẩm không hợp lệ: " + sp.getSanPham().getTen());
            }
        }
        hoaDon.setNhanVien(currentNhanVien);
        hoaDon.setNgaySua(LocalDateTime.now());
        hoaDon.setTrangThai(hoaDonService.getTrangThaiHoaDon().getHoanThanh());
        hoaDon.setLoaiHoaDon(true);
        hoaDon.setNguoiTao(currentNhanVien.getTen());

        // Ghi lịch sử hóa đơn
        LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
        lichSuHoaDon.setNhanVien(currentNhanVien);
        lichSuHoaDon.setHoaDon(hoaDon);
        lichSuHoaDon.setTrangThai(hoaDonService.getTrangThaiHoaDon().getHoanThanh());
        lichSuHoaDon.setNgaySua(LocalDateTime.now());
        lichSuHoaDon.setMoTa("Thanh toán thành công");
        lichSuHoaDon.setNguoiTao(currentNhanVien.getTen());
        lichSuHoaDonRepo.save(lichSuHoaDon);

        // Trừ số lượng phiếu giảm giá nếu hóa đơn có áp dụng
        PhieuGiamGia voucher = hoaDon.getPhieuGiamGia();
        if (voucher != null) {
            // Reload bản ghi mới nhất để tránh stale entity
            PhieuGiamGia v = phieuGiamGiaRepo.findById(voucher.getId())
                    .orElse(null);
            if (v != null) {
                Integer soLuong = v.getSoLuong();
                Integer daSuDung = v.getSoLuongDaSuDung();
                if (soLuong == null) soLuong = 0;
                if (daSuDung == null) daSuDung = 0;
                if (soLuong > 0) {
                    v.setSoLuong(soLuong - 1);
                    v.setSoLuongDaSuDung(daSuDung + 1);
                    phieuGiamGiaRepo.save(v);
                }
            }
        }

        hoaDonRepo.save(hoaDon);
    }

    @Override
    public void updateTongTienHoaDon(Long idHD) {
        //Lấy ds hóa đơn chi tiết theo id hóa đơn
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepo.findByHoaDon_Id(idHD);
        BigDecimal tongTien = listHDCT.stream()
                .map(HoaDonChiTiet::getThanhTien) // Sử dụng thành tiền đã tính theo giá giảm
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Lưu tổng tiền vào hóa đơn
        HoaDon hoaDon = hoaDonRepo.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + idHD));
        hoaDon.setTongTien(tongTien);
        hoaDonRepo.save(hoaDon);
    }

    @Override
    public void updateLoaiHoaDon(LoaiHoaDonRequest request) {
        NhanVien currentNhanVien = getCurrentNhanVien();
        HoaDon hoaDon = hoaDonRepo.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        hoaDon.setLoaiHoaDon(request.getLoaiHoaDon());
        hoaDon.setTrangThai(hoaDonService.getTrangThaiHoaDon().getChoXacNhan());
        hoaDonRepo.save(hoaDon);

        LichSuHoaDon lichSuHoaDon = lichSuHoaDonRepo.findByHoaDonId(hoaDon.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch sử hóa đơn"));
        lichSuHoaDon.setNhanVien(currentNhanVien);
        lichSuHoaDon.setTrangThai(hoaDonService.getTrangThaiHoaDon().getChoXacNhan());
        lichSuHoaDon.setNgayTao(LocalDateTime.now());
        lichSuHoaDon.setMoTa("Admin đã xác nhận đơn hàng");
        lichSuHoaDon.setNguoiTao(currentNhanVien.getTen());
        lichSuHoaDonRepo.save(lichSuHoaDon);

    }

}