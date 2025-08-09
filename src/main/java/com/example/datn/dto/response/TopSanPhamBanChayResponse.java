package com.example.datn.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TopSanPhamBanChayResponse {
    private Long sanPhamId;
    private String maSanPham;
    private String tenSanPham;
    private Long soLuongBan;
    private BigDecimal tongTien;
    
    // Constructor cho Hibernate query - nhận SUM() results
    public TopSanPhamBanChayResponse(Long sanPhamId, String maSanPham, String tenSanPham, Long soLuongBan, BigDecimal tongTien) {
        this.sanPhamId = sanPhamId;
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.soLuongBan = soLuongBan;
        this.tongTien = tongTien;
    }
} 