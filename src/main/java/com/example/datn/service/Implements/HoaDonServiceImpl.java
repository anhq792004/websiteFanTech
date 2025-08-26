package com.example.datn.service.Implements;

import com.example.datn.dto.request.*;
import com.example.datn.dto.response.LichSuHoaDonResponse;
import com.example.datn.dto.response.LichSuThanhToanResponse;
import com.example.datn.entity.HoaDon.HoaDon;
import com.example.datn.entity.HoaDon.HoaDonChiTiet;
import com.example.datn.entity.HoaDon.LichSuHoaDon;
import com.example.datn.entity.HoaDon.PhuongThucThanhToan;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.NhanVien.NhanVien;
import com.example.datn.entity.PhieuGiamGia;
import com.example.datn.entity.SanPham.SanPhamChiTiet;
import com.example.datn.entity.TaiKhoan;
import com.example.datn.repository.HoaDonRepo.HoaDonChiTietRepo;
import com.example.datn.repository.HoaDonRepo.HoaDonRepo;
import com.example.datn.repository.HoaDonRepo.LichSuHoaDonRepo;
import com.example.datn.repository.KhachHangRepo.KhachHangRepo;
import com.example.datn.repository.NhanVienRepo;
import com.example.datn.repository.SanPhamRepo.SanPhamChiTietRepo;
import com.example.datn.repository.TaiKhoanRepo;
import com.example.datn.service.HoaDonService.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl implements HoaDonService {
    private final HoaDonRepo hoaDonRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;
    private final LichSuHoaDonRepo lichSuHoaDonRepo;
    private final KhachHangRepo khachHangRepo;
    private final NhanVienRepo nhanVienRepo;
    private final TaiKhoanRepo taiKhoanRepo;

    // Lấy thông tin nhân viên đang đăng nhập
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
    @Override
    public Optional<HoaDon> findHoaDonByIdWithDiscount(Long id) {
        return hoaDonRepo.findById(id)
                .map(hoaDon -> {
                    // Eager loading để đảm bảo dữ liệu được load đầy đủ
                    if (hoaDon.getPhieuGiamGia() != null) {
                        hoaDon.getPhieuGiamGia().getId(); // Force load
                    }
                    if (hoaDon.getKhachHang() != null) {
                        hoaDon.getKhachHang().getId(); // Force load
                    }
                    return hoaDon;
                });
    }

    @Override
    public List<HoaDon> findAll() {
        return hoaDonRepo.findAll();
    }

    //hoaDon
    @Override
    public Page<HoaDon> findAllHoaDonAndSortDay(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return hoaDonRepo.findHoaDonAndSortDay(pageable);
    }

    @Override
    public Page<HoaDon> searchHoaDon(String query, Boolean loaiHoaDon, LocalDateTime tuNgay, LocalDateTime denNgay, Integer trangThai, Pageable pageable) {
        if (trangThai == null) {
            return hoaDonRepo.searchHoaDonKhongtrangThai(query, loaiHoaDon, tuNgay, denNgay, pageable);
        }
        return hoaDonRepo.searchHoaDon(query, loaiHoaDon, tuNgay, denNgay, trangThai, pageable);
    }

    @Override
    public Optional<HoaDon> findHoaDonById(Long id) {
        return hoaDonRepo.findById(id);
    }

    @Override
    public String generateOrderCode() {
        // Lấy số lượng hóa đơn hiện tại
        Long count = hoaDonRepo.count(); // Số lượng hóa đơn trong DB
        // Tạo mã hóa đơn với tiền tố "HD" và số thứ tự
        return String.format("HD%03d", count + 1); // VD: HD001, HD002
    }

    @Override
    public List<HoaDon> getHoaDonByIdKH(Long idKH) {
        return hoaDonRepo.findByKhachHang_IdAndTrangThaiNot(idKH, 0);
    }

    @Override
    public void truSoLuongSanPham(Long idHD) {
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepo.findByHoaDon_Id(idHD);
        for (HoaDonChiTiet hdct : listHDCT) {
            SanPhamChiTiet spct = hdct.getSanPhamChiTiet();
            int soLuongTon = spct.getSoLuong(); // Số lượng hiện tại trong kho
            int soLuongBan = hdct.getSoLuong();    // Số lượng trong hóa đơn chi tiết

            if (soLuongTon >= soLuongBan) {
                spct.setSoLuong(soLuongTon - soLuongBan); // Trừ số lượng
                sanPhamChiTietRepo.save(spct);
            } else {
                throw new RuntimeException("Số lượng tồn kho không đủ cho sản phẩm: " + spct.getSanPham().getTen());
            }
        }
    }

    @Override
    public void hoanSoLuongSanPham(Long idHD) {
//        HoaDon hoaDon = hoaDonRepo.findById(idHD).orElse(null);
//        if (hoaDon == null) {
//            return; // Hóa đơn không tồn tại, không làm gì cả
//        }
//        List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepo.findByHoaDon_Id(idHD);
//        if (hoaDon.getTrangThai() != getTrangThaiHoaDon().getChoXacNhan()) {
//            for (HoaDonChiTiet hdct : listHDCT) {
//                SanPhamChiTiet spct = hdct.getSanPhamChiTiet();
//                int soLuongTon = spct.getSoLuong();   // Số lượng hiện tại trong kho
//                int soLuongHoan = hdct.getSoLuong();  // Số lượng cần hoàn lại
//
//                spct.setSoLuong(soLuongTon + soLuongHoan); // Cộng lại số lượng
//                sanPhamChiTietRepo.save(spct);
//            }
//        }

    }


    @Override
    public List<HoaDonChiTiet> listHoaDonChiTiets(Long id) {
        return hoaDonChiTietRepo.findByHoaDon_Id(id);
    }

    @Override
    public LichSuThanhToanResponse getLSTTByHoaDonId(Long idHoaDon) {
        LichSuThanhToanResponse response = hoaDonRepo.findThanhToanHoaDonId(idHoaDon);
        if (response != null && response.getHinhThucThanhToan() != null) {
            try {
                PhuongThucThanhToan phuongThuc = PhuongThucThanhToan.valueOf(response.getHinhThucThanhToan());
                response.setHinhThucThanhToan(phuongThuc.getDisplayName());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    @Override
    public List<SanPhamChiTiet> findSPCTByIdSanPham() {
        return hoaDonChiTietRepo.findSanPhamChiTietByIdSanPham();
    }

    @Override
    public void deleteSPInHD(Long idSPCT, Long idHD) {
        // Tìm hóa đơn chi tiết chứa sản phẩm cần xóa thông qua query
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepo.findAll();
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(idHD);
        HoaDon hoaDon = hoaDonOptional.get();
        HoaDonChiTiet hdctToDelete = null;

        for (HoaDonChiTiet hdct : listHDCT) {
            if (hdct.getSanPhamChiTiet() != null && hdct.getSanPhamChiTiet().getId().equals(idSPCT)) {
                hdctToDelete = hdct;
                break;
            }
        }

        if (hdctToDelete != null) {
            // Lấy thông tin sản phẩm chi tiết
            SanPhamChiTiet sanPhamChiTiet = hdctToDelete.getSanPhamChiTiet();

            // Lấy số lượng sản phẩm trong giỏ
            int soLuongTrongGio = hdctToDelete.getSoLuong();

            // Hoàn trả số lượng vào kho
            if (hoaDon.getTrangThai() != getTrangThaiHoaDon().getChoXacNhan()) {
                sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() + soLuongTrongGio);
                sanPhamChiTietRepo.save(sanPhamChiTiet);
            }
            // Xóa sản phẩm khỏi giỏ hàng
            hoaDonChiTietRepo.deleteSanPhamChiTiet_Id(idSPCT);
            updateTongTienHoaDon(idHD);
        } else {
            throw new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng");
        }
    }


    @Override
    public void xacNhan(Long id, String ghiChu) {
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(id);
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepo.findByHoaDon_Id(id);

        if (hoaDonOptional.isPresent()) {
            HoaDon hoaDon = hoaDonOptional.get();

            if (!hoaDon.getTrangThai().equals(getTrangThaiHoaDon().getChoXacNhan())) {
                throw new RuntimeException("Hóa đơn đã được xử lý bởi người khác!");
            }
            for (HoaDonChiTiet hdct : listHDCT) {
                SanPhamChiTiet spct = hdct.getSanPhamChiTiet();
                int soLuongTon = spct.getSoLuong();
                int soLuongBan = hdct.getSoLuong();

                if (soLuongTon < soLuongBan) {
                    throw new RuntimeException("Số lượng tồn kho không đủ cho sản phẩm: " + spct.getSanPham().getTen());
                }
            }
            NhanVien currentNhanVien = getCurrentNhanVien();

            hoaDon.setTrangThai(getTrangThaiHoaDon().getDaXacNhan());
            hoaDon.setGhiChu(ghiChu);
            hoaDon.setNhanVien(currentNhanVien);
            hoaDon.setNguoiTao(currentNhanVien.getTen());
            hoaDon.setNgaySua(LocalDateTime.now());
            hoaDonRepo.save(hoaDon);

            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setNhanVien(currentNhanVien);
            lichSuHoaDon.setNguoiTao(currentNhanVien.getTen());
            lichSuHoaDon.setTrangThai(getTrangThaiHoaDon().getDaXacNhan());
            lichSuHoaDon.setNgaySua(LocalDateTime.now());
            lichSuHoaDon.setMoTa(ghiChu);
            lichSuHoaDonRepo.save(lichSuHoaDon);
        } else {
            throw new RuntimeException("Không tìm thấy hóa đơn với ID: " + id);
        }
    }

    @Override
    public void returnTrangThai(Long id) {
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(id);
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepo.findByHoaDon_Id(id);

        for (HoaDonChiTiet hdct : listHDCT) {
            SanPhamChiTiet spct = hdct.getSanPhamChiTiet();
            int soLuongTon = spct.getSoLuong();   // Số lượng hiện tại trong kho
            int soLuongHoan = hdct.getSoLuong();  // Số lượng cần hoàn lại

            spct.setSoLuong(soLuongTon + soLuongHoan); // Cộng lại số lượng
            sanPhamChiTietRepo.save(spct);
        }

        if (hoaDonOptional.isPresent()) {
            HoaDon hoaDon = hoaDonOptional.get();

            if (!hoaDon.getTrangThai().equals(getTrangThaiHoaDon().getChoXacNhan())) {
                throw new RuntimeException("Hóa đơn đã được xử lý bởi người khác!");
            }

            NhanVien currentNhanVien = getCurrentNhanVien();

            // Cập nhật trạng thái của HoaDon
            hoaDon.setTrangThai(getTrangThaiHoaDon().getChoXacNhan());
            hoaDon.setGhiChu("");
            hoaDon.setNgaySua(LocalDateTime.now());
            hoaDon.setNguoiTao(currentNhanVien.getTen());
            hoaDonRepo.save(hoaDon);

            // Tạo lịch sử cập nhật
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setTrangThai(getTrangThaiHoaDon().getChoXacNhan());
            lichSuHoaDon.setNgayTao(LocalDateTime.now());
            lichSuHoaDon.setNguoiTao(currentNhanVien.getTen());
            lichSuHoaDon.setMoTa("");
            lichSuHoaDonRepo.save(lichSuHoaDon);
        }
    }

    @Override
    public void giaoHang(Long id) {
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(id);
        if (hoaDonOptional.isPresent()) {
            HoaDon hoaDon = hoaDonOptional.get();
            if (!hoaDon.getTrangThai().equals(getTrangThaiHoaDon().getDaXacNhan())) {
                throw new RuntimeException("Hóa đơn đã được xử lý bởi người khác!");
            }
            NhanVien currentNhanVien = getCurrentNhanVien();
            // Cập nhật trạng thái của HoaDon giao hàng
            hoaDon.setTrangThai(getTrangThaiHoaDon().getDangGiaoHang());
            hoaDon.setNgaySua(LocalDateTime.now());
            hoaDon.setNguoiTao(currentNhanVien.getTen());
            hoaDonRepo.save(hoaDon);
            // Tạo một bản ghi lịch sử cho HoaDon đã được giao hàng
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setTrangThai(getTrangThaiHoaDon().getDangGiaoHang());
            lichSuHoaDon.setNgaySua(LocalDateTime.now());
            lichSuHoaDon.setNguoiTao(currentNhanVien.getTen());

            lichSuHoaDon.setMoTa("Đơn hàng đã được gửi lúc " + LocalDate.now());
            lichSuHoaDonRepo.save(lichSuHoaDon);
        }
    }

    @Override
    public void hoanThanh(Long id) {
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(id);
        if (hoaDonOptional.isPresent()) {
            HoaDon hoaDon = hoaDonOptional.get();
            if (!hoaDon.getTrangThai().equals(getTrangThaiHoaDon().getDangGiaoHang())) {
                throw new RuntimeException("Hóa đơn đã được xử lý bởi người khác!");
            }
            NhanVien currentNhanVien = getCurrentNhanVien();
            // Cập nhật trạng thái của HoaDon hoàn thành
            hoaDon.setTrangThai(getTrangThaiHoaDon().getHoanThanh());
            hoaDon.setNgaySua(LocalDateTime.now());
            hoaDon.setNguoiTao(currentNhanVien.getTen());
            hoaDonRepo.save(hoaDon);
            // Tạo một bản ghi lịch sử cho HoaDon hoàn thành
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setTrangThai(getTrangThaiHoaDon().getHoanThanh());
            lichSuHoaDon.setNgaySua(LocalDateTime.now());
            lichSuHoaDon.setNguoiTao(currentNhanVien.getTen());
            lichSuHoaDon.setMoTa("Đơn hàng đã được giao thành công lúc " + LocalDate.now());
            lichSuHoaDonRepo.save(lichSuHoaDon);
        }
    }

    @Override
    public void huy(Long id, String ghiChu) {
        Optional<HoaDon> optionalHoaDon = hoaDonRepo.findById(id);
        if (optionalHoaDon.isPresent()) {
            HoaDon hoaDon = optionalHoaDon.get();
            if (!hoaDon.getTrangThai().equals(getTrangThaiHoaDon().getChoXacNhan())
                    && !hoaDon.getTrangThai().equals(getTrangThaiHoaDon().getDaXacNhan())
                    && !hoaDon.getTrangThai().equals(getTrangThaiHoaDon().getHoaDonCho())) {
                throw new RuntimeException("Hóa đơn đã được xử lý bởi người khác!");
            }
            NhanVien currentNhanVien = getCurrentNhanVien();
            List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepo.findByHoaDon_Id(id);

            if (hoaDon.getTrangThai() != getTrangThaiHoaDon().getChoXacNhan()) {
                for (HoaDonChiTiet hdct : listHDCT) {
                    SanPhamChiTiet spct = hdct.getSanPhamChiTiet();
                    int soLuongTon = spct.getSoLuong();   // Số lượng hiện tại trong kho
                    int soLuongHoan = hdct.getSoLuong();  // Số lượng cần hoàn lại

                    spct.setSoLuong(soLuongTon + soLuongHoan); // Cộng lại số lượng
                    sanPhamChiTietRepo.save(spct);
                }
            }

            // Cập nhật trạng thái hóa đơn sang HỦY
            hoaDon.setGhiChu(ghiChu);
            hoaDon.setTrangThai(getTrangThaiHoaDon().getHuy());
            hoaDon.setNgaySua(LocalDateTime.now());
            hoaDon.setNguoiTao(currentNhanVien.getTen());
            hoaDonRepo.save(hoaDon);

            // Tạo một bản ghi lịch sử cho HoaDon hủy
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setTrangThai(getTrangThaiHoaDon().getHuy());
            lichSuHoaDon.setNgaySua(LocalDateTime.now());
            lichSuHoaDon.setMoTa(ghiChu);
            lichSuHoaDon.setNguoiTao(currentNhanVien.getTen());
            lichSuHoaDonRepo.save(lichSuHoaDon);
        } else {
            throw new RuntimeException("Không tìm thấy hóa đơn với ID: " + id);
        }
    }

    @Override
    public void hoanHang(Long id, String ghiChu) {
        Optional<HoaDon> optionalHoaDon = hoaDonRepo.findById(id);
        if (optionalHoaDon.isPresent()) {
            HoaDon hoaDon = optionalHoaDon.get();

            if (!hoaDon.getTrangThai().equals(getTrangThaiHoaDon().getDangGiaoHang())) {
                throw new RuntimeException("Hóa đơn đã được xử lý bởi người khác!");
            }
            NhanVien currentNhanVien = getCurrentNhanVien();
            List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepo.findByHoaDon_Id(id);

            for (HoaDonChiTiet hdct : listHDCT) {
                SanPhamChiTiet spct = hdct.getSanPhamChiTiet();
                int soLuongTon = spct.getSoLuong();   // Số lượng hiện tại trong kho
                int soLuongHoan = hdct.getSoLuong();  // Số lượng cần hoàn lại

                spct.setSoLuong(soLuongTon + soLuongHoan); // Cộng lại số lượng
                sanPhamChiTietRepo.save(spct);
            }

            // Cập nhật trạng thái hóa đơn sang HỦY
            hoaDon.setGhiChu(ghiChu);
            hoaDon.setTrangThai(getTrangThaiHoaDon().getHoanHang());
            hoaDon.setNgaySua(LocalDateTime.now());
            hoaDon.setNguoiTao(currentNhanVien.getTen());
            hoaDonRepo.save(hoaDon);

            // Tạo một bản ghi lịch sử cho HoaDon hủy
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setTrangThai(getTrangThaiHoaDon().getHoanHang());
            lichSuHoaDon.setNgaySua(LocalDateTime.now());
            lichSuHoaDon.setMoTa(ghiChu);
            lichSuHoaDon.setNguoiTao(currentNhanVien.getTen());
            lichSuHoaDonRepo.save(lichSuHoaDon);
        } else {
            throw new RuntimeException("Không tìm thấy hóa đơn với ID: " + id);
        }
    }

    @Override
    public void huyOnl(Long id, String ghiChu) {
        Optional<HoaDon> optionalHoaDon = hoaDonRepo.findById(id);
        if (optionalHoaDon.isPresent()) {
            HoaDon hoaDon = optionalHoaDon.get();
            if (!hoaDon.getTrangThai().equals(getTrangThaiHoaDon().getChoXacNhan())
                    && !hoaDon.getTrangThai().equals(getTrangThaiHoaDon().getDaXacNhan())
                    && !hoaDon.getTrangThai().equals(getTrangThaiHoaDon().getHoaDonCho())) {
                throw new RuntimeException("Hóa đơn đã được xử lý bởi người khác!");
            }
            List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepo.findByHoaDon_Id(id);

            if (hoaDon.getTrangThai() != getTrangThaiHoaDon().getChoXacNhan()) {
                for (HoaDonChiTiet hdct : listHDCT) {
                    SanPhamChiTiet spct = hdct.getSanPhamChiTiet();
                    int soLuongTon = spct.getSoLuong();   // Số lượng hiện tại trong kho
                    int soLuongHoan = hdct.getSoLuong();  // Số lượng cần hoàn lại

                    spct.setSoLuong(soLuongTon + soLuongHoan); // Cộng lại số lượng
                    sanPhamChiTietRepo.save(spct);
                }
            }
            // Cập nhật trạng thái hóa đơn sang HỦY
            hoaDon.setGhiChu(ghiChu);
            hoaDon.setTrangThai(getTrangThaiHoaDon().getHuy());
            hoaDon.setNgaySua(LocalDateTime.now());
            hoaDonRepo.save(hoaDon);

            // Lấy thông tin khách hàng đang đăng nhập
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String nguoiTao = "Khách lẻ";
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                String username;
                if (principal instanceof UserDetails) {
                    username = ((UserDetails) principal).getUsername();
                } else {
                    username = principal.toString();
                }

                TaiKhoan taiKhoan = taiKhoanRepo.findByEmail(username);
                if (taiKhoan != null) {
                    KhachHang khachHang = khachHangRepo.findByTaiKhoan(taiKhoan);
                    if (khachHang != null) {
                        nguoiTao = khachHang.getTen() != null ? khachHang.getTen() : "Khách lẻ";
                    }
                }
            }

            // Tạo một bản ghi lịch sử cho HoaDon hủy
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setTrangThai(getTrangThaiHoaDon().getHuy());
            lichSuHoaDon.setNgaySua(LocalDateTime.now());
            lichSuHoaDon.setMoTa(ghiChu);
            lichSuHoaDon.setNguoiTao(nguoiTao); // Sử dụng tên khách hàng
            lichSuHoaDonRepo.save(lichSuHoaDon);
        } else {
            throw new RuntimeException("Không tìm thấy hóa đơn với ID: " + id);
        }
    }

    private void updateTongTienHoaDon(Long hoaDonId) {
        Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(hoaDonId);
        if (hoaDonOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy hóa đơn với ID: " + hoaDonId);
        }

        HoaDon hoaDon = hoaDonOpt.get();
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepo.findByHoaDon_Id(hoaDonId);

        // Tính tổng tiền từ tất cả các sản phẩm
        BigDecimal tongTien = listHDCT.stream()
                .map(HoaDonChiTiet::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        hoaDon.setTongTien(tongTien);

        // Tính tổng tiền sau giảm giá
        BigDecimal tongTienSauGiamGia = tinhTongTienSauGiamGia(hoaDon, tongTien);
        hoaDon.setTongTienSauGiamGia(tongTienSauGiamGia);

        hoaDonRepo.save(hoaDon);
    }

    //Tính tổng tiền sau khi áp dụng phiếu giảm giá
    private BigDecimal tinhTongTienSauGiamGia(HoaDon hoaDon, BigDecimal tongTien) {
        if (hoaDon.getPhieuGiamGia() == null) {
            return tongTien;
        }

        PhieuGiamGia phieuGiamGia = hoaDon.getPhieuGiamGia();
        BigDecimal giaTriGiam = BigDecimal.ZERO;

        // Kiểm tra điều kiện áp dụng phiếu giảm giá
        if (phieuGiamGia.getGiaTriDonHangToiThieu() != null &&
                tongTien.compareTo(phieuGiamGia.getGiaTriDonHangToiThieu()) < 0) {
            return tongTien; // Không đủ điều kiện áp dụng
        }

        if (phieuGiamGia.getLoaiGiamGia()) {
            // Giảm theo phần trăm
            giaTriGiam = tongTien.multiply(phieuGiamGia.getGiaTriGiam())
                    .divide(BigDecimal.valueOf(100));

            // Kiểm tra giá trị giảm tối đa
            if (phieuGiamGia.getGiaTriGiamToiDa() != null &&
                    giaTriGiam.compareTo(phieuGiamGia.getGiaTriGiamToiDa()) > 0) {
                giaTriGiam = phieuGiamGia.getGiaTriGiamToiDa();
            }
        } else {
            // Giảm theo số tiền cố định
            giaTriGiam = phieuGiamGia.getGiaTriGiam();
        }

        BigDecimal tongTienSauGiam = tongTien.subtract(giaTriGiam);
        return tongTienSauGiam.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : tongTienSauGiam;
    }

    @Override
    public void addSPToHDCT(AddSPToHDCTRequest addSPToHDCTRequest) {
        Optional<SanPhamChiTiet> sanPhamChiTietOptional = sanPhamChiTietRepo.findById(addSPToHDCTRequest.getIdSP());
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(addSPToHDCTRequest.getIdHD());

        if (sanPhamChiTietOptional.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + addSPToHDCTRequest.getIdSP());
        }

        if (hoaDonOptional.isEmpty()) {
            throw new RuntimeException("Không tìm thấy hóa đơn với ID: " + addSPToHDCTRequest.getIdHD());
        }

        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietOptional.get();
        HoaDon hoaDon = hoaDonOptional.get();

        // Kiểm tra số lượng trong kho
        if (sanPhamChiTiet.getSoLuong() <= 0) {
            throw new RuntimeException("Sản phẩm " + sanPhamChiTiet.getSanPham().getTen() + " đã hết hàng!");
        }

        // Check sp tồn tại trong hóa đơn
        Optional<HoaDonChiTiet> existingHDCT = hoaDonChiTietRepo.
                findByHoaDonIdAndSanPhamChiTietId(hoaDon.getId(), sanPhamChiTiet.getId());

        if (existingHDCT.isPresent()) {
            // Nếu đã có thì tăng số lượng lên 1 và cập nhật thành tiền
            HoaDonChiTiet hoaDonChiTiet = existingHDCT.get();
            int soLuongMoi = hoaDonChiTiet.getSoLuong() + 1;

            // Kiểm tra số lượng trước khi thêm
            if (sanPhamChiTiet.getSoLuong() <= 0) {
                throw new RuntimeException("Số lượng tồn kho không đủ (" + sanPhamChiTiet.getSoLuong() +
                        ") cho sản phẩm " + sanPhamChiTiet.getSanPham().getTen());
            }

            hoaDonChiTiet.setSoLuong(soLuongMoi);
            BigDecimal thanhTienMoi = hoaDonChiTiet.getGia().multiply(BigDecimal.valueOf(soLuongMoi));
            hoaDonChiTiet.setThanhTien(thanhTienMoi);

            // ✅ TRỪ KHO NGAY khi thêm sản phẩm
            // Giảm số lượng sản phẩm trong kho
            if (hoaDon.getTrangThai() != getTrangThaiHoaDon().getChoXacNhan()) {
                sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() - 1);
                sanPhamChiTietRepo.save(sanPhamChiTiet);
            }
            hoaDonChiTietRepo.save(hoaDonChiTiet);
        } else {
            // Kiểm tra số lượng trước khi thêm mới
            if (addSPToHDCTRequest.getSoLuong() > sanPhamChiTiet.getSoLuong()) {
                throw new RuntimeException("Số lượng yêu cầu (" + addSPToHDCTRequest.getSoLuong() +
                        ") vượt quá số lượng tồn kho (" + sanPhamChiTiet.getSoLuong() + ") cho sản phẩm " +
                        sanPhamChiTiet.getSanPham().getTen());
            }

            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setHoaDon(hoaDonOptional.get());
            hoaDonChiTiet.setSanPhamChiTiet(sanPhamChiTietOptional.get());
            hoaDonChiTiet.setGia(addSPToHDCTRequest.getGia());
            hoaDonChiTiet.setSoLuong(addSPToHDCTRequest.getSoLuong());
            BigDecimal thanhTien = addSPToHDCTRequest.getGia().multiply(BigDecimal.valueOf(addSPToHDCTRequest.getSoLuong()));
            hoaDonChiTiet.setThanhTien(thanhTien);

            // ✅ TRỪ KHO NGAY khi thêm sản phẩm mới
            // Giảm số lượng sản phẩm trong kho
            if (hoaDon.getTrangThai() != getTrangThaiHoaDon().getChoXacNhan()) {
                sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() - addSPToHDCTRequest.getSoLuong());
                sanPhamChiTietRepo.save(sanPhamChiTiet);
            }
            hoaDonChiTietRepo.save(hoaDonChiTiet);
        }

        // **QUAN TRỌNG: Cập nhật tổng tiền hóa đơn sau khi thêm sản phẩm**
        updateTongTienHoaDon(addSPToHDCTRequest.getIdHD());
    }

    @Override
    public void addKHToHDCT(AddKHToHDCTRequest addKHToHDCTRequest) {
        Optional<KhachHang> khachHangOptional = khachHangRepo.findById(addKHToHDCTRequest.getIdSP());
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(addKHToHDCTRequest.getIdHD());

        KhachHang khachHang = khachHangOptional.get();
        HoaDon hoaDon = hoaDonOptional.get();

        hoaDon.setTenNguoiNhan(khachHang.getTen());
        hoaDon.setKhachHang(khachHang);
        hoaDon.setSdtNguoiNhan(khachHang.getSoDienThoai());
        hoaDonRepo.save(hoaDon);
    }

    @Override
    public void removeDiscount(Long idHD) {
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(idHD);
        if (hoaDonOptional.isPresent()) {
            HoaDon hoaDon = hoaDonOptional.get();
            // Remove the associated voucher
            hoaDon.setPhieuGiamGia(null);
            // Recalculate total amount
            updateTongTienHoaDon(idHD);
            hoaDonRepo.save(hoaDon);
        } else {
            throw new RuntimeException("Không tìm thấy hóa đơn với ID: " + idHD);
        }
    }

    @Override
    public void updateSoluong(UpdateSoLuongRequest request) {
        Optional<SanPhamChiTiet> spOpt = sanPhamChiTietRepo.findById(request.getIdSP());
        Optional<HoaDon> hdOpt = hoaDonRepo.findById(request.getIdHD());

        if (spOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + request.getIdSP());
        }

        if (hdOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy hóa đơn với ID: " + request.getIdHD());
        }

        HoaDon hoaDon = hdOpt.get();
        SanPhamChiTiet sanPhamChiTiet = spOpt.get();

        // Tìm xem sản phẩm này đã có trong hóa đơn chưa
        Optional<HoaDonChiTiet> hdctOpt = hoaDonChiTietRepo.findByHoaDonAndSanPhamChiTiet(hoaDon, sanPhamChiTiet);

        // Kiểm tra số lượng hợp lệ
        if (request.getSoLuong() <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0!");
        }

        int soLuongHienTai = hdctOpt.map(HoaDonChiTiet::getSoLuong).orElse(0);
        int soLuongKhaDung = sanPhamChiTiet.getSoLuong() + soLuongHienTai;

        if (request.getSoLuong() > soLuongKhaDung) {
            throw new RuntimeException("Số lượng yêu cầu (" + request.getSoLuong() +
                    ") vượt quá số lượng có thể bán (" + soLuongKhaDung + ") cho sản phẩm " +
                    sanPhamChiTiet.getSanPham().getTen());
        }

        HoaDonChiTiet hoaDonChiTiet;
        if (hdctOpt.isPresent()) {
            hoaDonChiTiet = hdctOpt.get();
            int chenhLech = request.getSoLuong() - soLuongHienTai;
            hoaDonChiTiet.setSoLuong(request.getSoLuong());
            if (hoaDon.getTrangThai() != getTrangThaiHoaDon().getChoXacNhan()) {
                sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() - chenhLech);
                sanPhamChiTietRepo.save(sanPhamChiTiet);
            }
        } else {
            hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setHoaDon(hoaDon);
            hoaDonChiTiet.setSanPhamChiTiet(sanPhamChiTiet);
            hoaDonChiTiet.setSoLuong(request.getSoLuong());
            hoaDonChiTiet.setGia(sanPhamChiTiet.getGia()); // Sử dụng giá từ sản phẩm
            if (hoaDon.getTrangThai() != getTrangThaiHoaDon().getChoXacNhan()) {
                sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() - request.getSoLuong());
                sanPhamChiTietRepo.save(sanPhamChiTiet);
            }
        }

        BigDecimal gia = sanPhamChiTiet.getGia();
        BigDecimal thanhTien = gia.multiply(BigDecimal.valueOf(request.getSoLuong()));
        hoaDonChiTiet.setThanhTien(thanhTien);

        hoaDonChiTietRepo.save(hoaDonChiTiet);
        updateTongTienHoaDon(request.getIdHD());
    }


    @Override
    public void tangSoLuong(Long idHD, Long idSPCT) {
        HoaDonChiTiet hdct = hoaDonChiTietRepo.findByHoaDon_IdAndSanPhamChiTiet_Id(idHD, idSPCT);
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(idHD);
        HoaDon hoaDon = hoaDonOptional.get();

        if (hdct != null) {
            // Lấy thông tin sản phẩm chi tiết
            SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepo.findById(idSPCT)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm chi tiết với ID đã cho."));

            // Kiểm tra số lượng tồn kho trước khi tăng
            if (sanPhamChiTiet.getSoLuong() <= 0) {
                throw new RuntimeException("Số lượng tồn kho không đủ (" + sanPhamChiTiet.getSoLuong() +
                        ") cho sản phẩm " + sanPhamChiTiet.getSanPham().getTen());
            }

            // Tăng số lượng lên 1
            hdct.setSoLuong(hdct.getSoLuong() + 1);

            // Cập nhật lại thành tiền
            BigDecimal gia = hdct.getGia();
            hdct.setThanhTien(gia.multiply(BigDecimal.valueOf(hdct.getSoLuong())));

            // ✅ TRỪ KHO NGAY khi tăng số lượng
            // Giảm số lượng sản phẩm trong kho
            if (hoaDon.getTrangThai() != getTrangThaiHoaDon().getChoXacNhan()) {
                sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() - 1);
                sanPhamChiTietRepo.save(sanPhamChiTiet);
            }
            // Lưu lại bản ghi HoaDonChiTiet đã cập nhật
            hoaDonChiTietRepo.save(hdct);

            // **Cập nhật tổng tiền hóa đơn**
            updateTongTienHoaDon(idHD);
        } else {
            throw new RuntimeException("Không tìm thấy hóa đơn chi tiết với ID sản phẩm chi tiết.");
        }
    }

    @Override
    public void giamSoLuong(Long idHD, Long idSPCT) {
        HoaDonChiTiet hdct = hoaDonChiTietRepo.findByHoaDon_IdAndSanPhamChiTiet_Id(idHD, idSPCT);
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(idHD);
        HoaDon hoaDon = hoaDonOptional.get();

        if (hdct != null) {
            // Kiểm tra nếu số lượng hiện tại bằng 1, không cho giảm nữa
            if (hdct.getSoLuong() <= 1) {
                throw new RuntimeException("Số lượng không thể nhỏ hơn 1");
            }

            // Lấy thông tin sản phẩm chi tiết
            SanPhamChiTiet sanPhamChiTiet = hdct.getSanPhamChiTiet();

            // Giảm số lượng đi 1
            hdct.setSoLuong(hdct.getSoLuong() - 1);

            // Cập nhật lại thành tiền
            BigDecimal gia = hdct.getGia();
            hdct.setThanhTien(gia.multiply(BigDecimal.valueOf(hdct.getSoLuong())));

            // ✅ CỘNG KHO NGAY khi giảm số lượng (hoàn lại)
            // Giảm số lượng sản phẩm trong kho
            if (hoaDon.getTrangThai() != getTrangThaiHoaDon().getChoXacNhan()) {
                sanPhamChiTiet.setSoLuong(sanPhamChiTiet.getSoLuong() + 1);
                sanPhamChiTietRepo.save(sanPhamChiTiet);
            }

            // Lưu lại bản ghi HoaDonChiTiet đã cập nhật
            hoaDonChiTietRepo.save(hdct);

            // **Cập nhật tổng tiền hóa đơn**
            updateTongTienHoaDon(idHD);
        } else {
            throw new RuntimeException("Không tìm thấy hóa đơn chi tiết với ID sản phẩm chi tiết.");
        }
    }

    @Override
    public Integer tongSoLuong(Long idHD) {
        Integer tongSoLuong = hoaDonChiTietRepo.sumSoLuong(idHD);
        return tongSoLuong != null ? tongSoLuong : 0;
    }

    @Override
    public void updateInfor(UpdateInforRequest request) {
        Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(request.getIdHD());
        if (hoaDonOptional.isPresent()) {
            HoaDon hoaDon = hoaDonOptional.get();
            hoaDon.setTenNguoiNhan(request.getTenNguoiNhan());
            hoaDon.setSdtNguoiNhan(request.getSdtNguoiNhan());
            hoaDon.setTinh(request.getTinh());
            hoaDon.setHuyen(request.getHuyen());
            hoaDon.setXa(request.getXa());
            hoaDon.setSoNhaNgoDuong(request.getSoNhaNgoDuong());
            hoaDonRepo.save(hoaDon);
        }
    }


    // lichSuHoaDon
    @Override
    public List<LichSuHoaDon> lichSuHoaDonList(Long id) {
        return lichSuHoaDonRepo.findLichSuHoaDonByIdHoaDon(id);
    }

    @Override
    public List<LichSuHoaDonResponse> lichSuHoaDonResponseList(Long id) {
        return null;
    }

    @Override
    public TrangThaiHoaDonRequest getTrangThaiHoaDon() {
        TrangThaiHoaDonRequest request = new TrangThaiHoaDonRequest(0, 1, 2,
                3, 4, 5, 6);
        return request;
    }

    @Override
    public void saveHoaDon(HoaDon hoaDon) {
        hoaDonRepo.save(hoaDon);
    }
}
