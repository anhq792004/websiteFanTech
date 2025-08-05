package com.example.datn.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class UpdateNhanVienRequest {
    private Long id;

    private String ten;

    private String email;

    private String soDienThoai;

    private String chucVu;

    private String canCuocCongDan;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date ngaySinh;

    private String gioiTinh;

    private String tinhThanhPho;

    private String quanHuyen;

    private String xaPhuong;

    private String soNhaNgoDuong;
}
