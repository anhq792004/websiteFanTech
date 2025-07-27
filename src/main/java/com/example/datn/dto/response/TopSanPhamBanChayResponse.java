package com.example.datn.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopSanPhamBanChayResponse {
    private Long sanPhamId;
    private String maSanPham;
    private String tenSanPham;
    private Long soLuongBan;
    private Long tongTien;
} 