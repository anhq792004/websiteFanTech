package com.example.datn.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LowStockProductResponse {
    private Long sanPhamChiTietId;
    private Long sanPhamId;
    private String tenSanPham;
    private Integer soLuong;
    private Double gia;
    private String hinhAnh; // đường dẫn ảnh (nếu có)
    private String thuocTinh; // mô tả ngắn (vd: công suất/hãng)
}