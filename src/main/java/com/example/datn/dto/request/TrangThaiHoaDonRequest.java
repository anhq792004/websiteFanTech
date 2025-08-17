package com.example.datn.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrangThaiHoaDonRequest {
    Integer hoaDonCho;
    Integer choXacNhan;
    Integer daXacNhan;
    Integer dangGiaoHang;
    Integer hoanThanh;
    Integer huy;
    Integer hoanHang;
}
