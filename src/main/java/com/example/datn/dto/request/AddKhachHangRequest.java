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
public class AddKhachHangRequest {

    String ten;

    String email;

    String soDienThoai;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date ngaySinh;

    String gioiTinh;

    String tinhThanhPho;

    String quanHuyen;

    String xaPhuong;

    String soNhaNgoDuong;
}
