package com.example.datn.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class TempOrderDTO {
    private String tenNguoiNhan;
    private String sdtNguoiNhan;
    private String tinh;
    private String huyen;
    private String xa;
    private String soNhaNgoDuong;
    private String ghiChu;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiamGia;
    private BigDecimal phiVanChuyen;
    private BigDecimal discountAmount;
    private Long voucherId;
    private List<gioHangDTO> cartItems;
}
