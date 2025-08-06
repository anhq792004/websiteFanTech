package com.example.datn.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LichSuThanhToanResponse {
    BigDecimal tongTienSauGiamGia;
    LocalDateTime ngayTao;
    boolean loaiHoaDon;
    String hinhThucThanhToan;
    String nguoiTao;
    Integer trangThai;

    public LichSuThanhToanResponse(BigDecimal tongTienSauGiamGia, LocalDateTime ngayTao, boolean loaiHoaDon, String hinhThucThanhToan, String nguoiTao, Integer trangThai) {
        this.tongTienSauGiamGia = tongTienSauGiamGia;
        this.ngayTao = ngayTao;
        this.loaiHoaDon = loaiHoaDon;
        this.hinhThucThanhToan = hinhThucThanhToan;
        this.nguoiTao = nguoiTao;
        this.trangThai = trangThai;
    }

    // Constructor chấp nhận Boolean (có thể null) và xử lý an toàn
    public LichSuThanhToanResponse(BigDecimal tongTienSauGiamGia, LocalDateTime ngayTao, Boolean loaiHoaDon, String hinhThucThanhToan, String nguoiTao, Integer trangThai) {
        this.tongTienSauGiamGia = tongTienSauGiamGia;
        this.ngayTao = ngayTao;
        this.loaiHoaDon = loaiHoaDon != null ? loaiHoaDon : true; // Nếu null thì mặc định là true
        this.hinhThucThanhToan = hinhThucThanhToan;
        this.nguoiTao = nguoiTao;
        this.trangThai = trangThai;
    }
}
