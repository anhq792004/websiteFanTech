package com.example.datn.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateInforKhachHangRequest {
    Long idKH;

    String ten;

    String gioiTinh;

    String soDienThoai;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date ngaySinh;

}
