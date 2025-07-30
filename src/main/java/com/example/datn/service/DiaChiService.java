package com.example.datn.service;

import com.example.datn.dto.request.AddDiaChiRequest;
import com.example.datn.dto.request.UpdateDiaChiRequest;
import com.example.datn.entity.DiaChi;

import java.util.List;

public interface DiaChiService {
    List<DiaChi> getDiaChiByIdKhachHang(Long idKH);

    boolean xoaDiaChiTheoKhachHang(Long idDiaChi, Long idKhachHang);

    void update(UpdateDiaChiRequest request);

    void addDiaChi(AddDiaChiRequest request);

}
