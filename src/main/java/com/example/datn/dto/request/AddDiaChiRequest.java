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
public class AddDiaChiRequest {
    Long idKH;
    String tinh;
    String huyen;
    String xa;
    String soNhaNgoDuong;
}
