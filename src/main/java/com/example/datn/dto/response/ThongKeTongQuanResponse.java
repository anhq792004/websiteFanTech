package com.example.datn.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeTongQuanResponse {
    private Long tongDonHang;
    private Long tongDoanhThu;
    private Long tongSanPhamBan;
    private Long tongKhachHang;
} 