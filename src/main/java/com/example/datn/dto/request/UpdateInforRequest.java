package com.example.datn.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateInforRequest {
    Long idHD;

    String tenNguoiNhan;

    String sdtNguoiNhan;

    String xa;

    String huyen;

    String tinh;

    String soNhaNgoDuong;
}
