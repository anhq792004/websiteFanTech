package com.example.datn.service.Implements;

import com.example.datn.dto.request.LoaiHoaDonRequest;
import com.example.datn.entity.HoaDon.HoaDon;
import com.example.datn.entity.HoaDon.HoaDonChiTiet;
import com.example.datn.entity.HoaDon.LichSuHoaDon;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.SanPham.SanPhamChiTiet;
import com.example.datn.repository.HoaDonRepo.HoaDonChiTietRepo;
import com.example.datn.repository.HoaDonRepo.HoaDonRepo;
import com.example.datn.repository.HoaDonRepo.LichSuHoaDonRepo;
import com.example.datn.repository.SanPhamRepo.SanPhamChiTietRepo;
import com.example.datn.service.BanHang.BanHangService;
import com.example.datn.service.HoaDonService.HoaDonService;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    @Override
    public void taoHoaDonCho(HoaDon hoaDon) {
        List<HoaDon> listHoaDon = hoaDonService.findAll();
        int count = (int) listHoaDon.stream()
                .filter(sl -> sl.getTrangThai() == hoaDonService.getTrangThaiHoaDon().getHoaDonCho())
                .count();
        if (count >= 10) {
            // Thông báo khi số lượng hóa đơn chờ vượt quá 10
            throw new IllegalStateException("Số lượng hóa đơn chờ tối qua là 10");
        }
        hoaDon.setMa(hoaDonService.generateOrderCode());
        hoaDon.setTrangThai(hoaDonService.getTrangThaiHoaDon().getHoaDonCho());
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setLoaiHoaDon(true);

        hoaDonRepo.saveAndFlush(hoaDon);

        //tao lich su hoa don
        LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
        lichSuHoaDon.setHoaDon(hoaDon);
        lichSuHoaDon.setNgayTao(hoaDon.getNgayTao());
        lichSuHoaDon.setTrangThai(hoaDon.getTrangThai());

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

        for (HoaDonChiTiet hdct : listHDCT) {
            SanPhamChiTiet sp = hdct.getSanPhamChiTiet();
            // Chỉ kiểm tra số lượng, không cần cập nhật số lượng trong kho nữa
            // vì đã cập nhật khi thêm vào giỏ hàng
            if (hdct.getSoLuong() < 0) {
                throw new RuntimeException("Số lượng sản phẩm không hợp lệ: " + sp.getSanPham().getTen());
            }
        }
        
        hoaDon.setNgaySua(LocalDateTime.now());
        hoaDon.setTrangThai(hoaDonService.getTrangThaiHoaDon().getHoanThanh());
        hoaDon.setLoaiHoaDon(true);

        LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
        lichSuHoaDon.setHoaDon(hoaDon);
        lichSuHoaDon.setTrangThai(hoaDonService.getTrangThaiHoaDon().getHoanThanh());
        lichSuHoaDon.setNgayTao(LocalDateTime.now());
        lichSuHoaDon.setNguoiTao("admin");
        lichSuHoaDon.setMoTa("Thanh toán thành công");
        lichSuHoaDonRepo.save(lichSuHoaDon);

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
        HoaDon hoaDon = hoaDonRepo.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        hoaDon.setLoaiHoaDon(request.getLoaiHoaDon());
        hoaDon.setTrangThai(hoaDonService.getTrangThaiHoaDon().getChoXacNhan());
        hoaDonRepo.save(hoaDon);

        LichSuHoaDon lichSuHoaDon = lichSuHoaDonRepo.findByHoaDonId(hoaDon.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch sử hóa đơn"));
        lichSuHoaDon.setTrangThai(hoaDonService.getTrangThaiHoaDon().getChoXacNhan());
        lichSuHoaDon.setNgayTao(LocalDateTime.now());
        lichSuHoaDon.setMoTa("Admin đã xác nhận đơn hàng");
        lichSuHoaDonRepo.save(lichSuHoaDon);

    }

}