package com.example.datn.service.Implements;

import com.example.datn.dto.request.AddDiaChiRequest;
import com.example.datn.dto.request.UpdateDiaChiRequest;
import com.example.datn.entity.DiaChi;
import com.example.datn.entity.KhachHang;
import com.example.datn.repository.DiaChiRepo;
import com.example.datn.repository.KhachHangRepo.KhachHangRepo;
import com.example.datn.service.DiaChiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiaChiServiceImpl implements DiaChiService {

    private final DiaChiRepo diaChiRepo;
    private final KhachHangRepo khachHangRepo;

    @Override
    public List<DiaChi> getDiaChiByIdKhachHang(Long idKH) {
        return diaChiRepo.findByKhachHang_Id(idKH);
    }

    @Override
    public boolean xoaDiaChiTheoKhachHang(Long idDiaChi, Long idKhachHang) {
        Optional<DiaChi> diaChiOptional = diaChiRepo.findByIdAndKhachHang_Id(idDiaChi, idKhachHang);
        if (diaChiOptional.isPresent()) {
            KhachHang khachHang = khachHangRepo.findById(idKhachHang).orElse(null);
            
            // Xóa địa chỉ
            diaChiRepo.delete(diaChiOptional.get());
            
            // Nếu địa chỉ bị xóa là địa chỉ mặc định, chọn địa chỉ khác làm mặc định
            if (khachHang != null && khachHang.getDiaChiMacDinhId() != null && 
                khachHang.getDiaChiMacDinhId().equals(idDiaChi)) {
                
                List<DiaChi> remainingAddresses = diaChiRepo.findByKhachHang_Id(idKhachHang);
                if (!remainingAddresses.isEmpty()) {
                    // Đặt địa chỉ đầu tiên còn lại làm mặc định
                    khachHang.setDiaChiMacDinhId(remainingAddresses.get(0).getId());
                } else {
                    // Không còn địa chỉ nào, xóa mặc định
                    khachHang.setDiaChiMacDinhId(null);
                }
                khachHangRepo.save(khachHang);
            }
            
            return true;
        }
        return false;
    }

    @Override
    public void update(UpdateDiaChiRequest request) {
        DiaChi diaChi = diaChiRepo.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        diaChi.setTinh(request.getTinh());
        diaChi.setHuyen(request.getHuyen());
        diaChi.setXa(request.getXa());
        diaChi.setSoNhaNgoDuong(request.getSoNhaNgoDuong());

        diaChiRepo.save(diaChi);
    }

    @Override
    public void addDiaChi(AddDiaChiRequest request) {
        DiaChi dc = new DiaChi();
        dc.setTinh(request.getTinh());
        dc.setHuyen(request.getHuyen());
        dc.setXa(request.getXa());
        dc.setSoNhaNgoDuong(request.getSoNhaNgoDuong());

        KhachHang kh = khachHangRepo.findById(request.getIdKH()).orElseThrow();
        dc.setKhachHang(kh);

        diaChiRepo.save(dc);
        
        // Nếu khách hàng chưa có địa chỉ mặc định, đặt địa chỉ này làm mặc định
        if (kh.getDiaChiMacDinhId() == null) {
            kh.setDiaChiMacDinhId(dc.getId());
            khachHangRepo.save(kh);
        }
    }


}
