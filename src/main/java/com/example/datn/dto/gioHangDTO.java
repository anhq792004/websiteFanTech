package com.example.datn.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class gioHangDTO {
    Long sanPhamChiTietId;
    String tenSanPham;
    String mauSac;
    String congSuat;
    String hang;
    BigDecimal gia;
    Integer soLuong;
    String hinhAnh;
    Integer soLuongTon; // Số lượng tồn kho



    // Tính tổng tiền cho item này
    public BigDecimal getTongTien() {
        if (gia != null && soLuong != null) {
            return gia.multiply(BigDecimal.valueOf(soLuong));
        }
        return BigDecimal.ZERO;
    }

    // Kiểm tra có thể tăng số lượng không
    public boolean canIncrease() {
        return soLuong < soLuongTon;
    }

    // Kiểm tra có thể giảm số lượng không
    public boolean canDecrease() {
        return soLuong > 1;
    }
}
