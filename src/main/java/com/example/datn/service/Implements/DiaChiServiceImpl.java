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
            diaChiRepo.delete(diaChiOptional.get());
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
    }


}
